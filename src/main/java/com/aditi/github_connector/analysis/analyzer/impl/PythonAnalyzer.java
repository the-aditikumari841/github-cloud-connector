package com.aditi.github_connector.analysis.analyzer.impl;

import com.aditi.github_connector.analysis.analyzer.Analyzer;
import com.aditi.github_connector.analysis.model.Issue;
import com.aditi.github_connector.analysis.parser.impl.GenericTextParser;
import com.aditi.github_connector.analysis.parser.impl.RuffJsonParser;
import com.aditi.github_connector.ci.CIExecutor;
import com.aditi.github_connector.config.AnalysisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PythonAnalyzer implements Analyzer {

    private final CIExecutor ciExecutor;
    private final RuffJsonParser ruffJsonParser;
    private final AnalysisProperties analysisProperties;

    @Override
    public boolean supports(String file) {
        return file.endsWith(".py");
    }

    @Override
    public List<Issue> analyze(String repoPath) {
        String output = ciExecutor.runRuff(repoPath);
        List<Issue> issues = ruffJsonParser.parse(output);

        issues.forEach(issue -> {
            String ruleId = extractRuleId(issue.getMessage());

            String severity = analysisProperties.getSeverity()
                    .getOrDefault("ruff", Map.of())
                    .getOrDefault(ruleId, "LOW");

            issue.setTool("RUFF");
            issue.setRuleId(ruleId);
            issue.setSeverity(severity);
        });
        return issues;
    }

    private String extractRuleId(String message) {
        if(message == null || message.isEmpty())
            return "UNKNOWN";

        String cleanedMessage = message.trim();
        int firstSpaceIndex = cleanedMessage.indexOf(' ');

        return firstSpaceIndex == -1
                ? cleanedMessage
                : cleanedMessage.substring(0, firstSpaceIndex);
    }
}
