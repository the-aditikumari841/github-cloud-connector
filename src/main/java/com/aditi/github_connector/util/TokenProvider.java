package com.aditi.github_connector.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider {

    @Value("${github.token}")
    private String patToken;

    public String getPatToken() {
        return patToken;
    }
}
