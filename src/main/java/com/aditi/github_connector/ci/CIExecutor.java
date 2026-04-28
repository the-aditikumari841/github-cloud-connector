package com.aditi.github_connector.ci;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Component
public class CIExecutor {
    public String cloneRepo(String repoUrl) {
        String dir = System.getProperty("java.io.tmpdir")
                + File.separator
                + "repo-" + System.currentTimeMillis();

        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "git", "clone", repoUrl, dir
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
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

    public String runCheckStyle(String repoPath) {
        try {

            File repoDir = new File(repoPath);

            File mvnwFile = new File(repoDir, "mvnw.cmd");

            ProcessBuilder builder;

            if (mvnwFile.exists()) {
                builder = new ProcessBuilder("cmd","/c","mvnw.cmd", "checkstyle:check");
                System.out.println("Using Maven wrapper (mvnw.cmd)");
            } else {
                builder = new ProcessBuilder("cmd","/c","mvn.cmd", "checkstyle:check");
                System.out.println("Using System Maven (mvn.cmd)");
            }

            builder.directory(repoDir);
            builder.redirectErrorStream(true);

            System.out.println("Running command: " + builder.command());
            System.out.println("Working directory: " + repoDir.getAbsolutePath());

            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            StringBuilder output = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            System.out.println("====CHECKSTYLE OUTPUT====");
            System.out.println(output.toString());
            System.out.println("Checkstyle finished with exit code " + exitCode);

            if(exitCode != 0) {
                output.append("\n(Checkstyle found issues)");
            }

            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Checkstyle execution failed: " + e.getMessage(), e);
        }
    }
}
