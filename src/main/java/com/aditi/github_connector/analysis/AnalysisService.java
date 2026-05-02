package com.aditi.github_connector.analysis;

import com.aditi.github_connector.analysis.analyzer.Analyzer;
import com.aditi.github_connector.analysis.analyzer.AnalyzerFactory;
import com.aditi.github_connector.analysis.model.Issue;
import com.aditi.github_connector.ci.CIExecutor;
import com.aditi.github_connector.client.github.GithubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final CIExecutor ciExecutor;
    private final GithubClient githubClient;
    private final AnalyzerFactory analyzerFactory;

    public void analyze(String owner, String repo, String cloneUrl,
                        int prNumber, String branch, String sha) {

        String repoPath = null;

        try {
            System.out.println("Step 1: cloning repo... ");
            repoPath = ciExecutor.cloneRepo(cloneUrl, branch);

            System.out.println("Step 2: checkout commit... ");
            ciExecutor.checkoutCommit(repoPath, sha);

            System.out.println("Step 3: fetching changed files... ");
            List<String> changedFiles = githubClient.getChangedFiles(owner, repo, prNumber);

            System.out.println("Step 4: selecting analyzers... ");
            List<Analyzer> analyzers = analyzerFactory.getAnalyzers(changedFiles);

            if (analyzers.isEmpty()) {
                System.out.println("No analyzers found for changed files.");
                return;
            }

            List<Issue> issues = new ArrayList<>();

            System.out.println("Step 5: running analyzers... ");
            for (Analyzer analyzer : analyzers) {
                issues.addAll(analyzer.analyze(repoPath));
            }

            System.out.println("Step 6: filtering issues... ");
            List<Issue> filtered = filterIssues(issues, changedFiles);

            System.out.println("Step 7: posting comment... ");
            String comment = formatComment(filtered);

            githubClient.commentOnPr(owner, repo, prNumber, comment);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;

        } finally {
            if (repoPath != null) {
                System.out.println("Step 8: cleaning up repo... ");
                ciExecutor.deleteDirectory(repoPath);
            }
        }
    }

    private List<Issue> filterIssues(List<Issue> issues, List<String> changedFiles) {
        return issues.stream()
                .filter(issue -> changedFiles.stream().
                        anyMatch(file -> issue.getFile().replace("\\", "/").contains(file))).toList();
    }

    private String formatComment(List<Issue> issues) {

        if (issues.isEmpty()) {
            return "### Code Review Results \n\nNo issues found \n\n_Reviewed automatically by CI Bot_";
        }

        StringBuilder commentBuilder = new StringBuilder();

        Map<String, List<Issue>> issuesByTool = issues.stream()
                .collect(Collectors.groupingBy(Issue::getTool));

        List<String> toolOrder = List.of("SPOTBUGS", "RUFF", "CHECKSTYLE");

        Map<String, Integer> severityPriority = Map.of(
                "HIGH", 3,
                "MEDIUM", 2,
                "LOW", 1
        );

        for (String tool : toolOrder) {
            List<Issue> toolIssues = issuesByTool.get(tool);
            if (toolIssues == null || toolIssues.isEmpty()) {
                continue;
            }

            toolIssues.sort((a, b) ->
                    severityPriority.getOrDefault(b.getSeverity(), 0)
                            - severityPriority.getOrDefault(a.getSeverity(), 0)
            );

            for (int i = 0; i < Math.min(20, toolIssues.size()); i++) {
                Issue issue = toolIssues.get(i);

                String fileName = new File(issue.getFile()).getName();

                commentBuilder.append("• ")
                        .append(fileName).append(":")
                        .append(issue.getLine())
                        .append("\n  Tool: ").append(issue.getTool())
                        .append(" | Severity: ").append(issue.getSeverity())
                        .append(" | Rule: ").append(issue.getRuleId())
                        .append("\n  -> ").append(issue.getMessage())
                        .append("\n\n");
            }
        }
        return "### Code Review Results\n\n"
                + "**Issues in changed files:** " + issues.size() + "\n\n"
                + commentBuilder
                + "\n---\n_Reviewed automatically by CI Bot_";
    }
}
