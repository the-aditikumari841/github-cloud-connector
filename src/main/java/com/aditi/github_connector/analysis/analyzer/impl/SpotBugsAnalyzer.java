package com.aditi.github_connector.analysis.analyzer.impl;

import com.aditi.github_connector.analysis.analyzer.Analyzer;
import com.aditi.github_connector.analysis.model.Issue;
import com.aditi.github_connector.analysis.parser.impl.SpotBugsParser;
import com.aditi.github_connector.ci.CIExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SpotBugsAnalyzer implements Analyzer {

    private final CIExecutor ciExecutor;
    private final SpotBugsParser spotBugsParser;

    @Override
    public boolean supports(String file) {
        return file.endsWith(".java");
    }

    @Override
    public List<Issue> analyze(String repoPath) {
        ciExecutor.runSpotBugs(repoPath);
        return spotBugsParser.parse(repoPath);
    }
}
