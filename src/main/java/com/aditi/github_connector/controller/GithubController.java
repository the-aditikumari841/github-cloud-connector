package com.aditi.github_connector.controller;

import com.aditi.github_connector.dto.request.CreateIssueRequest;
import com.aditi.github_connector.dto.response.IssueResponse;
import com.aditi.github_connector.dto.response.RepoResponse;
import com.aditi.github_connector.service.GithubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/github")
public class GithubController {

    private final GithubService githubService;

    @GetMapping("/repos/{username}")
    public ResponseEntity<List<RepoResponse>> getRepos(@PathVariable String username) {
        return ResponseEntity.ok(githubService.fetchRepositories(username));
    }

    @PostMapping("/issues")
    public ResponseEntity<IssueResponse> createIssue(@RequestBody CreateIssueRequest request) {
        return ResponseEntity.ok(githubService.createIssue(request));
    }
}
