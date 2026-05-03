package com.aditi.githubreviewbot.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RepoResponse {

    private String name;

    private String full_name;

    @JsonProperty("html_url")
    private String html_url;
}
