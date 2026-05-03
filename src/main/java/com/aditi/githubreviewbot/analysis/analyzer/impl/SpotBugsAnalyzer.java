package com.aditi.githubreviewbot.analysis.analyzer.impl;

import com.aditi.githubreviewbot.analysis.analyzer.Analyzer;
import com.aditi.githubreviewbot.analysis.model.Issue;
import com.aditi.githubreviewbot.analysis.parser.impl.SpotBugsXmlParser;
import com.aditi.githubreviewbot.ci.CIExecutor;
import com.aditi.githubreviewbot.config.AnalysisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SpotBugsAnalyzer implements Analyzer {

    private final CIExecutor ciExecutor;
    private final SpotBugsXmlParser spotBugsXmlParser;
    private final AnalysisProperties analysisProperties;

    @Override
    public boolean supports(String file) {
        return file.endsWith(".java");
    }

    @Override
    public List<Issue> analyze(String repoPath) {
        ciExecutor.runSpotBugs(repoPath);
        List<Issue> issues = spotBugsXmlParser.parse(repoPath);

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
