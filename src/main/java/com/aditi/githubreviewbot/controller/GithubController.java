package com.aditi.githubreviewbot.controller;

import com.aditi.githubreviewbot.dto.request.CreateIssueRequest;
import com.aditi.githubreviewbot.dto.response.IssueResponse;
import com.aditi.githubreviewbot.dto.response.RepoResponse;
import com.aditi.githubreviewbot.service.GithubService;
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

    @GetMapping("/my-repos")
    public ResponseEntity<List<RepoResponse>> getMyRepos() {
        return ResponseEntity.ok(githubService.fetchMyRepositories());
    }

    @PostMapping("/issues")
    public ResponseEntity<IssueResponse> createIssue(@RequestBody CreateIssueRequest request) {
        return ResponseEntity.ok(githubService.createIssue(request));
    }
}
