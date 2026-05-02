package com.aditi.github_connector.analysis.parser.impl;

import com.aditi.github_connector.analysis.model.Issue;
import com.aditi.github_connector.analysis.parser.ToolParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RuffJsonParser implements ToolParser {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Issue> parse(String output) {
        List<Issue> issues = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(output);

            if(root.isArray()) {
                for (JsonNode node : root) {
                    Issue issue = new Issue();

                    issue.setFile(node.path("filename").asText());

                    JsonNode location = node.path("location");

                    issue.setLine(location.path("row").asInt(-1));
                    issue.setColumn(location.path("column").asInt(-1));


                    issue.setMessage(node.path("message").asText());

                    issues.add(issue);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Ruff JSON", e);
        }
        return issues;
    }
}
