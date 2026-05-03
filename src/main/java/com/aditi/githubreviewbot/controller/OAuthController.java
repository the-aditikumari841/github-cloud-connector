package com.aditi.githubreviewbot.controller;

import com.aditi.githubreviewbot.service.OAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/github")
@RequiredArgsConstructor
public class OAuthController {

    private final OAuthService oAuthService;

    @GetMapping("/login")
    public void redirectToGithub(HttpServletResponse response) throws Exception {
        String url = oAuthService.getAuthorizationUrl();
        response.sendRedirect(url);
    }

    @GetMapping("/callback")
    public String handleCallback(@RequestParam String code) {
        return oAuthService.exchangeCodeForToken(code);
    }
}
