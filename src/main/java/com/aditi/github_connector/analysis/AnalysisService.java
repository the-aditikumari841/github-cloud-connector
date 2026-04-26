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

        for (String line : lines) {
            if (line.contains("[WARN]")) {
                count++;

                String cleaned = line.replaceFirst("\\[WARN\\]\\s*", "");

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

//                if(cleaned.contains("repo-")) {
//                    cleaned = cleaned.replaceAll(".*repo-\\d+\\\\", "");
//                }

//                int firstColonIndex = cleaned.indexOf(':');

//                int secondColonIndex = cleaned.indexOf(':', firstColonIndex + 1);

//                int lastSlash = Math.max(cleaned.lastIndexOf("\\"), cleaned.lastIndexOf("/"));
//
//                if(lastSlash != -1 && firstColonIndex != -1
//                        && lastSlash < firstColonIndex
//                        && cleaned.contains(".")) {
//
//                        cleaned = cleaned.substring(lastSlash + 1);
//                }

//                if(firstColonIndex == -1){
//                    issues.append("• ").append(cleaned).append("\n");
//                    continue;
//                }
//
//                String beforeColon = cleaned.substring(0, firstColonIndex);
//                int lastSlash = Math.max(beforeColon.lastIndexOf('/'), beforeColon.lastIndexOf('\\'));
//
//                if(lastSlash != -1 ) {
//                    String fileName = beforeColon.substring(lastSlash + 1);
//                    cleaned = fileName + cleaned.substring(firstColonIndex);
//                }

//                if(firstColonIndex != -1){
//                    String beforeColon =  cleaned.substring(0, firstColonIndex);
//                    int lastSlash = Math.max(beforeColon.lastIndexOf("\\"), beforeColon.lastIndexOf("/"));
//                    if(lastSlash != -1){
//                        String fileName = beforeColon.substring(lastSlash + 1);
//                        cleaned = fileName + cleaned.substring(firstColonIndex);
//                    }
//                }

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
