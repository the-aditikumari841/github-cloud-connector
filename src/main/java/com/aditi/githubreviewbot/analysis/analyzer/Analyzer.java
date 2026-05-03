package com.aditi.githubreviewbot.analysis.analyzer;

import com.aditi.githubreviewbot.analysis.model.Issue;

import java.util.List;

public interface Analyzer {
    boolean supports(String fileName);

    List<Issue> analyze(String repoPath);
}
