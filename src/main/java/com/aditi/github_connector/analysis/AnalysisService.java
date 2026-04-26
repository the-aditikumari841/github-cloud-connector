package com.aditi.github_connector.analysis;

import com.aditi.github_connector.ci.CIExecutor;
import com.aditi.github_connector.client.github.GithubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final CIExecutor ciExecutor;
    private final GithubClient githubClient;

    public void analyze(String owner, String repo, String cloneUrl, int prNumber){
        System.out.println("Step 1: cloning repo... ");
        String repoPath = ciExecutor.cloneRepo(cloneUrl);

        System.out.println("Step 2: Running checkstyle... ");
        String result = ciExecutor.runCheckStyle(repoPath);

        System.out.println("Step 3: Posting comment ");
        String comment = formatComment(result);

        githubClient.commentOnPr(owner, repo, prNumber, comment);

    }

    private String formatComment(String result) {
        String[] lines = result.split("\n");

        StringBuilder issues = new StringBuilder();
        int count = 0;
        for (String line : lines) {
            if (line.contains("[WARN]")) {
                count++;

                String cleaned = line.
                        replaceAll(".*repo-\\d+\\\\", "")
                        .replaceFirst("\\[WARN\\]\\s*", "");

                int firstColonIndex = cleaned.indexOf(':');
                int secondColonIndex = cleaned.indexOf(':', firstColonIndex + 1);

                int lastSlash = Math.max(cleaned.lastIndexOf("\\"), cleaned.lastIndexOf("/"));
                if(lastSlash != -1 && firstColonIndex != -1 && secondColonIndex != -1 && lastSlash < firstColonIndex) {
                        cleaned = cleaned.substring(lastSlash + 1);
                }

                issues.append("• ").append(cleaned).append("\n");

                if(count >= 20) {
                    issues.append("\n...and more issues (truncated)");
                    break;
                }
            }
        }

        String summary = count == 0
                ? "Checkstyle Passed"
                : "Checkstyle Issues Found";

        return "### Code Review Results\n\n"
                + summary + "\n\n"
                + "**Total issues:** " + count + "\n\n"
                + (count > 0 ? "**Issues:**\n" + issues : "")
                + "\n---\n_Reviewed automatically by CI Bot_";
    }
}
