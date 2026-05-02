package com.aditi.github_connector.analysis.analyzer.impl;

import com.aditi.github_connector.analysis.analyzer.Analyzer;
import com.aditi.github_connector.analysis.model.Issue;
import com.aditi.github_connector.analysis.parser.impl.EslintJsonParser;
import com.aditi.github_connector.ci.CIExecutor;
import com.aditi.github_connector.config.AnalysisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EslintAnalyzer implements Analyzer {

    private final CIExecutor ciExecutor;
    private final EslintJsonParser eslintJsonParser;
    private final AnalysisProperties analysisProperties;

    @Override
    public boolean supports(String file) {
        return file.endsWith(".js") || file.endsWith(".ts");
    }

    @Override
    public List<Issue> analyze(String repoPath) {
        String output = ciExecutor.runEslint(repoPath);
        List<Issue> issues = eslintJsonParser.parse(output);

        issues.forEach(issue -> {
            String ruleId = extractRuleId(issue.getMessage());

            String severity = analysisProperties.getSeverity()
                    .getOrDefault("eslint", Map.of())
                    .getOrDefault(ruleId, "LOW");

            issue.setTool("ESLINT");
            issue.setRuleId(ruleId);
            issue.setSeverity(severity);
        });
        return issues;
    }

    private String extractRuleId(String message) {
        if(message == null ||  message.isEmpty())
            return "UNKNOWN";

        String cleanedMessage = message.trim();
        int firstSpaceIndex = cleanedMessage.indexOf(' ');

        return firstSpaceIndex == -1
                ? cleanedMessage
                : cleanedMessage.substring(0, firstSpaceIndex);
    }
}
