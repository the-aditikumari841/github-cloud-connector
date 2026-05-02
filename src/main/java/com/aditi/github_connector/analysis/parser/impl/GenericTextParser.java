package com.aditi.github_connector.analysis.parser.impl;

import com.aditi.github_connector.analysis.model.Issue;
import com.aditi.github_connector.analysis.parser.ToolParser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GenericTextParser implements ToolParser {

    public static final Pattern PATTERN = Pattern.compile("(.+?):(\\d+):(\\d+):\\s*(.*)");

    @Override
    public List<Issue> parse(String output) {
        List<Issue> issues = new ArrayList<>();

        for (String line : output.split("\\r?\\n")) {
            Matcher matcher = PATTERN.matcher(line);
            if (matcher.find()) {
                Issue issue = new Issue();
                issue.setFile(matcher.group(1));
                issue.setLine(Integer.parseInt(matcher.group(2)));
                issue.setColumn(Integer.parseInt(matcher.group(3)));
                issue.setMessage(matcher.group(4));
                issues.add(issue);
            }
        }
        return issues;
    }
}
