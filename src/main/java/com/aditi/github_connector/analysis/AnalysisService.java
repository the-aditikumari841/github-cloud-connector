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
        String summary = result.contains("BUILD SUCCESS")
                ? "Checkstyle Passed"
                : "Checkstyle Issues Found";

        return "### Code Review Results ###\n"
                + summary + "\n\n"
                + "```\n"
                + result.substring(0, Math.min(result.length(), 2000))
                + "\n```";
    }
}
