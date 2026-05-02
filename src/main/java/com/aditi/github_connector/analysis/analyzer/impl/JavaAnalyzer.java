package com.aditi.github_connector.analysis.analyzer.impl;

import com.aditi.github_connector.analysis.analyzer.Analyzer;
import com.aditi.github_connector.analysis.model.Issue;
import com.aditi.github_connector.analysis.parser.impl.DefaultTextParser;
import com.aditi.github_connector.ci.CIExecutor;
import com.aditi.github_connector.config.AnalysisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JavaAnalyzer implements Analyzer {

    private final CIExecutor ciExecutor;
    private final DefaultTextParser defaultTextParser;
    private final AnalysisProperties analysisProperties;

    @Override
    public boolean supports(String file) {
        return file.endsWith(".java");
    }

    @Override
    public List<Issue> analyze(String repoPath) {
        String output = ciExecutor.runCheckStyle(repoPath);
        List<Issue> issues = defaultTextParser.parse(output);

        issues.forEach(issue -> {
            String ruleId = extractRuleId(issue.getMessage());

            String severity = analysisProperties.getSeverity()
                    .getOrDefault("checkstyle", Map.of())
                    .getOrDefault(ruleId, "LOW");

            issue.setTool("CHECKSTYLE");
            issue.setRuleId(ruleId);
            issue.setSeverity(severity);
        });
        return issues;
    }

    private String extractRuleId(String message) {
        if(message.contains("[") && message.contains("]")) {
            return message.substring(
                    message.lastIndexOf("[") + 1,
                    message.lastIndexOf("]")
            );
        }
        return "UNKNOWN";
    }
}
