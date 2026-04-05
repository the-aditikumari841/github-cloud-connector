package com.aditi.github_connector.controller;

import com.aditi.github_connector.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/github")
public class GithubController {

    private final GithubService githubService;

    @GetMapping("/repos/{username}")
    public ResponseEntity<String> getRepos(@PathVariable String username) {
        return ResponseEntity.ok(githubService.fetchRepositories(username));
    }
}
