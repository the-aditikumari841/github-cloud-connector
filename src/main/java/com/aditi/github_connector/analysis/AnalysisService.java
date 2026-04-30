package com.aditi.github_connector.analysis;

import com.aditi.github_connector.ci.CIExecutor;
import com.aditi.github_connector.client.github.GithubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final CIExecutor ciExecutor;
    private final GithubClient githubClient;

    public void analyze(String owner, String repo, String cloneUrl,
                        int prNumber, String branch, String sha) {

        String repoPath = null;

        try {
            System.out.println("Step 1: cloning repo... ");
            repoPath = ciExecutor.cloneRepo(cloneUrl, branch);

            System.out.println("Step 2: checkout commit... ");
            ciExecutor.checkoutCommit(repoPath, sha);

            System.out.println("Step 3: Running checkstyle... ");
            String checkstyleOutput = ciExecutor.runCheckStyle(repoPath);

            System.out.println("Step 4: Posting comment ");
            String comment = formatComment(checkstyleOutput);

            githubClient.commentOnPr(owner, repo, prNumber, comment);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;

        } finally {
            if(repoPath != null){
                System.out.println("Step 5: Cleaning up repo... ");
                ciExecutor.deleteDirectory(repoPath);
            }
        }
    }

    private String formatComment(String result) {
        String[] lines = result.split("\\r?\\n");

        StringBuilder issues = new StringBuilder();
        int count = 0;

        Pattern pattern = Pattern.compile("(.+?):(\\d+):(\\d+):\\s*(.*)");

        StringBuilder buffer = new StringBuilder();

        for (String line : lines) {
            if (buffer.length() > 0) {
                buffer.append(" ").append(line.trim());
            } else {
                buffer.append(line.trim());
            }

            String candidate = buffer.toString();
            Matcher matcher = pattern.matcher(candidate);
            if (matcher.find()) {
                count++;
                String filePath = matcher.group(1);
                String lineNo = matcher.group(2);
                String colNo = matcher.group(3);
                String message = matcher.group(4);

                int lastSlash = Math.max(
                        filePath.lastIndexOf('/'),
                        filePath.lastIndexOf('\\')
                );

                String fileName = (lastSlash != -1)
                        ? filePath.substring(lastSlash + 1)
                        : filePath;

                issues.append("• ")
                        .append(fileName)
                        .append(":")
                        .append(lineNo)
                        .append(":")
                        .append(colNo)
                        .append(":")
                        .append(message)
                        .append("\n");

                if (count >= 20) {
                    issues.append("\n...and more issues (truncated)");
                    break;
                }

                buffer.setLength(0);

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
