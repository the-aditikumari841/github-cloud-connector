package com.aditi.github_connector.ci;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Component
public class CIExecutor {
    public String cloneRepo(String repoUrl) {
        String dir = "repo-" + System.currentTimeMillis();

        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "git", "clone", repoUrl, dir
            );
            builder.inheritIO();

            Process process = builder.start();
            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new RuntimeException("clone failed with exit code " + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Clone failed" + e.getMessage(), e);
        }
        return dir;
    }

    public String runCheckStyle(String repoPath) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "mvn", "checkstyle:check"
            );
            builder.directory(new File(repoPath));
            builder.redirectErrorStream(true);

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
            if(exitCode != 0) {
                output.append("\n (Checkstyle found issues)");
            }
            return output.toString();
        } catch (Exception e) {
            throw new RuntimeException("Checkstyle execution failed: " + e.getMessage(), e);
        }
    }
}
