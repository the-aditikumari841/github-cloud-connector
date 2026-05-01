package com.aditi.github_connector.service;

import com.aditi.github_connector.client.auth.OAuthClient;
import com.aditi.github_connector.util.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class OAuthService {

    @Value("${oauth.github.client-id}")
    private String clientId;

    @Value("${oauth.github.client-secret}")
    private String clientSecret;

    @Value("${oauth.github.redirect-uri}")
    private String redirectUri;

    private final OAuthClient oAuthClient;
    private final TokenProvider tokenProvider;

    public String getAuthorizationUrl() {
        return "https://github.com/login/oauth/authorize" +
                "?client_id=" + clientId +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) +
                "&scope=repo";
    }

    public String exchangeCodeForToken(String code) {
        String token = oAuthClient.getAccessToken(clientId, clientSecret, code);

        tokenProvider.setOAuthToken(token);

        return "OAuth Success! Token stored.";
    }
}
