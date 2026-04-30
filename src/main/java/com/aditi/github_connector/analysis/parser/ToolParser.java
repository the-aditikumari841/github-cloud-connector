package com.aditi.github_connector.analysis.parser;

import com.aditi.github_connector.analysis.model.Issue;

import java.util.List;

public interface ToolParser {
    List<Issue> parse(String output);
}
