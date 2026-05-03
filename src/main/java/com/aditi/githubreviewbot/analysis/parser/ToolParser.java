package com.aditi.githubreviewbot.analysis.parser;

import com.aditi.githubreviewbot.analysis.model.Issue;

import java.util.List;

public interface ToolParser {
    List<Issue> parse(String output);
}
