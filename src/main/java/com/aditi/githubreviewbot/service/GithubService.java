package com.aditi.githubreviewbot.service;

import com.aditi.githubreviewbot.client.github.GithubClient;
import com.aditi.githubreviewbot.dto.request.CreateIssueRequest;
import com.aditi.githubreviewbot.dto.response.IssueResponse;
import com.aditi.githubreviewbot.dto.response.RepoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GithubService {
    private final GithubClient githubClient;

    public List<RepoResponse> fetchRepositories(String username) {
        return githubClient.getUserRepos(username);
    }

    public IssueResponse createIssue(CreateIssueRequest request) {
        return githubClient.createIssue(
                request.getOwner(),
                request.getRepo(),
                request
        );
    }

    public List<RepoResponse> fetchMyRepositories() {
        return githubClient.getMyRepos();
    }
}
