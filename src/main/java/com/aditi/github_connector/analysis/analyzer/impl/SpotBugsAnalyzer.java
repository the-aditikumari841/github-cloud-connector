package com.aditi.github_connector.analysis.analyzer.impl;

import com.aditi.github_connector.analysis.analyzer.Analyzer;
import com.aditi.github_connector.analysis.model.Issue;
import com.aditi.github_connector.analysis.parser.impl.SpotBugsParser;
import com.aditi.github_connector.ci.CIExecutor;
import com.aditi.github_connector.config.AnalysisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SpotBugsAnalyzer implements Analyzer {

    private final CIExecutor ciExecutor;
    private final SpotBugsParser spotBugsParser;
    private final AnalysisProperties analysisProperties;

    @Override
    public boolean supports(String file) {
        return file.endsWith(".java");
    }

    @Override
    public List<Issue> analyze(String repoPath) {
        ciExecutor.runSpotBugs(repoPath);
        List<Issue> issues = spotBugsParser.parse(repoPath);

        issues.forEach(issue -> {
            String ruleId = issue.getRuleId();

            String severity = analysisProperties.getSeverity()
                    .getOrDefault("spotbugs", Map.of())
                    .getOrDefault(ruleId, "MEDIUM");

            issue.setTool("SPOTBUGS");
            issue.setRuleId(ruleId);
            issue.setSeverity(severity);
        });
        return issues;
    }
}
