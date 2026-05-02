package com.aditi.github_connector.analysis.parser.impl;

import com.aditi.github_connector.analysis.model.Issue;
import com.aditi.github_connector.analysis.parser.ToolParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EslintJsonParser implements ToolParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Issue> parse(String output) {
        List<Issue> issues = new ArrayList<>();

        try {

            if(output == null || !output.trim().startsWith("[")) {
                System.out.println("ESLint output is not JSON. Skipping parsing...");
                return issues;
            }
            JsonNode root = objectMapper.readTree(output);

            if (root.isArray()) {
                for (JsonNode fileNode : root) {
                    String filePath = fileNode.path("filePath").asText();

                    for (JsonNode message : fileNode.path("messages")) {
                        Issue issue = new Issue();

                        issue.setFile(filePath);
                        issue.setLine(message.path("line").asInt(-1));
                        issue.setColumn(message.path("column").asInt(-1));
                        issue.setMessage(message.path("message").asText());
                        issues.add(issue);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Failed to parse Eslint JSON. Skipping parsing...");
            e.printStackTrace();
            return issues;
        }
        return issues;
    }
}
