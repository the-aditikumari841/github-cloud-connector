package com.aditi.github_connector.analysis.analyzer;

import com.aditi.github_connector.analysis.model.Issue;

import java.util.List;

public interface Analyzer {
    boolean supports(String fileName);

    List<Issue> analyze(String repoPath);
}
