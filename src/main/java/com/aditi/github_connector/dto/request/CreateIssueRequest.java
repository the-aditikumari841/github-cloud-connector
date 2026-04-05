package com.aditi.github_connector.dto.request;

import lombok.Data;

@Data
public class CreateIssueRequest {
    private String owner;
    private String repo;
    private String title;
    private String body;
}
