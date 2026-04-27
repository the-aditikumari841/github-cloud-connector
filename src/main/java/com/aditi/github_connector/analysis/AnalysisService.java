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
        String[] lines = result.split("\\r?\\n");

        StringBuilder issues = new StringBuilder();
        int count = 0;

        Pattern pattern = Pattern.compile("(.+?):(\\d+):(\\d+):\\s*(.*)");

//        StringBuilder current = new StringBuilder();

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

//        for (String line : lines) {
//            if (line.matches("\\[(WARN|ERROR)]\\s.*")) {
//
//                if (current.length() > 0) {
//                    count++;
//                    processIssue(current.toString(), issues, pattern);
//
//                    if(count >= 20) {
//                        issues.append("\n...and more issues (truncated)");
//                        break;
//                    }
//                    current.setLength(0);
//                }
//                current.append(line);
//            } else {
//                if (current.length() > 0) {
//                    current.append(" ").append(line.trim());
//                }
//            }
//        }

//        if(current.length() > 0 && count < 20) {
//            count++;
//            processIssue(current.toString(), issues, pattern);
//        }

        String summary = count == 0
                ? "Checkstyle Passed"
                : "Checkstyle Issues Found";

        return "### Code Review Results\n\n"
                + summary + "\n\n"
                + "**Total issues:** " + count + "\n\n"
                + (count > 0 ? "**Issues:**\n" + issues : "")
                + "\n---\n_Reviewed automatically by CI Bot_";
    }

    private void processIssue(String line, StringBuilder issues, Pattern pattern){
        String cleaned = line.replaceFirst("\\[(WARN|ERROR)]\\s*", "");

        Matcher matcher = pattern.matcher(cleaned);

        if(matcher.find()){
            String filePath = matcher.group(1);
            String lineNo = matcher.group(2);
            String colNo = matcher.group(3);
            String message = matcher.group(4);

            int lastSlash = Math.max(
                    filePath.lastIndexOf("/"),
                    filePath.lastIndexOf("\\")
            );

            String fileName = (lastSlash != -1)
                    ? filePath.substring(lastSlash + 1)
                    : filePath;

            cleaned = fileName + ":" + lineNo + ":" + colNo + ": " + message;
        }
        issues.append("• ").append(cleaned).append("\n");
    }
}
