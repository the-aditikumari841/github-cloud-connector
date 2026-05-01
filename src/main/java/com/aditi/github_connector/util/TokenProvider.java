package com.aditi.github_connector.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenProvider {

    @Value("${github.token}")
    private String patToken;

    private String oauthToken;

    public String getToken() {
        if (oauthToken != null) {
            System.out.println("Using OAuth Token");
            return oauthToken;
        }
        System.out.println("Using PAT Token");
        return patToken;
    }

    public void setOAuthToken(String token) {
        this.oauthToken = token;
    }
}
