package com.aditi.github_connector.ci;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class CIExecutor {
    public String cloneRepo(String repoUrl, String branch) {
        String dir = System.getProperty("java.io.tmpdir")
                + File.separator
                + "repo-" + System.currentTimeMillis();

        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "git",
                    "clone",
                    "--branch", branch,
                    "--single-branch",
                    repoUrl,
                    dir
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("clone failed with exit code " + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Clone failed: " + e.getMessage(), e);
        }
        return dir;
    }

    public void checkoutCommit(String repoPath, String sha) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "git", "checkout", sha
            );

            builder.directory(new File(repoPath));
            builder.redirectErrorStream(true);

            Process process = builder.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                while (reader.readLine() != null) {
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("checkout failed with exit code " + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("checkout failed: " + e.getMessage(), e);
        }
    }

    public void deleteDirectory(String repoPath) {
        try {
            Path directory = Paths.get(repoPath);

            if (!Files.exists(directory))
                return;

            AtomicBoolean hasError = new AtomicBoolean(false);

            System.out.println("Starting cleanup: " + repoPath);

            try (var paths = Files.walk(directory)) {
                paths.sorted(Comparator.reverseOrder())
                        .forEach(p -> deleteWithRetry(p, hasError));
            }
            if (hasError.get()) {
                System.out.println("Cleanup completed with some failures: " + repoPath);
            } else {
                System.out.println("Cleanup completed successfully: " + repoPath);
            }

        } catch (Exception e) {
            System.out.println("Delete directory failed: " + e.getMessage());
        }
    }

    private void deleteWithRetry(Path repoPath, AtomicBoolean hasError) {
        int attempts = 3;

        while (attempts > 0) {
            try {
                Files.delete(repoPath);
                return;
            } catch (IOException e) {
                attempts--;

                if (attempts == 0) {
                    hasError.set(true);
                    System.out.println("Failed to delete: " + repoPath + " | " + e.getMessage());
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
    }

    public String runCheckStyle(String repoPath) {
        try {

            File repoDir = new File(repoPath);

            File mvnwFile = new File(repoDir, "mvnw.cmd");

            ProcessBuilder builder;

            if (mvnwFile.exists()) {
                builder = new ProcessBuilder("cmd", "/c", "mvnw.cmd", "checkstyle:check");
                System.out.println("Using Maven wrapper (mvnw.cmd)");
            } else {
                builder = new ProcessBuilder("cmd", "/c", "mvn.cmd", "checkstyle:check");
                System.out.println("Using System Maven (mvn.cmd)");
            }

            builder.directory(repoDir);
            builder.redirectErrorStream(true);

            System.out.println("Running command: " + builder.command());
            System.out.println("Working directory: " + repoDir.getAbsolutePath());

            Process process = builder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            System.out.println("====CHECKSTYLE OUTPUT====");
            System.out.println(output.toString());
            System.out.println("Checkstyle finished with exit code " + exitCode);

            if (exitCode != 0) {
                output.append("\n(Checkstyle found issues)");
            }

            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Checkstyle execution failed: " + e.getMessage(), e);
        }
    }

    public String runSpotBugs(String repoPath) {
        try {
            File repoDir = new File(repoPath);

            ProcessBuilder builder = new ProcessBuilder(
                    "cmd", "/c",
                    "mvn", "spotbugs:spotbugs"
            );

            builder.directory(repoDir);
            builder.redirectErrorStream(true);

            System.out.println("Running spotbugs...");
            System.out.println("Working directory: " + repoDir.getAbsolutePath());

            Process process = builder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            System.out.println("Spotbugs finished with exit code " + exitCode);

            if (exitCode != 0) {
                output.append("\nSpotbugs found issues\n");
            }

            return output.toString();

        }  catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Spotbugs execution failed: " + e.getMessage(), e);
        }
    }

    public String runRuff(String repoPath) {
        try {
            File repoDir = new File(repoPath);

            ProcessBuilder builder = new ProcessBuilder(
                    "cmd", "/c",
                    "ruff",
                    "check",
                    ".",
                    "--output-format",
                    "text"
            );

            builder.directory(repoDir);
            builder.redirectErrorStream(true);

            System.out.println("Running ruff...");
            System.out.println("Working directory: " + repoDir.getAbsolutePath());

            Process process = builder.start();

            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            int exitCode = process.waitFor();
            System.out.println("====RUFF OUTPUT====");
            System.out.println(output.toString());
            System.out.println("Ruff finished with exit code " + exitCode);

            if (exitCode != 0) {
                output.append("\n(Ruff found issues)");
            }

            return output.toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ruff execution failed: " + e.getMessage(), e);
        }
    }

}
