package com.aditi.github_connector.analysis.analyzer.impl;

import com.aditi.github_connector.analysis.analyzer.Analyzer;
import com.aditi.github_connector.analysis.model.Issue;
import com.aditi.github_connector.analysis.parser.impl.RuffParser;
import com.aditi.github_connector.ci.CIExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PythonAnalyzer implements Analyzer {

    private final CIExecutor ciExecutor;
    private final RuffParser ruffParser;

    @Override
    public boolean supports(String file) {
        return file.endsWith(".py");
    }

    @Override
    public List<Issue> analyze(String repoPath) {
        String output = ciExecutor.runRuff(repoPath);
        return ruffParser.parse(output);

    }
}
