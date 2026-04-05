package com.aditi.github_connector.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IssueResponse {

    @JsonProperty("html_url")
    private String html_url;

    private String title;

    private String state;
}
