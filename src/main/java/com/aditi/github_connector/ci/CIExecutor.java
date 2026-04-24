package com.aditi.github_connector.ci;

import org.springframework.stereotype.Component;

@Component
public class CIExecutor {
    public String cloneRepo(String repoUrl) {
        String dir = "repo-" + System.currentTimeMillis();

        try {
            ProcessBuilder builder = new ProcessBuilder(
                    "git", "clone", repoUrl, dir
            );
            builder.inheritIO();
            builder.start().waitFor();
        } catch (Exception e) {
            throw new RuntimeException("Clone failed" + e.getMessage(), e);
        }
        return dir;
    }

    public String runCheckStyle(String repoPath) {
        return "Checkstyle passed (dummy result)";
    }
}
