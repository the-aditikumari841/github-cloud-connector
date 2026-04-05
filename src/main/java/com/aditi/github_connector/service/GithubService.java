package com.aditi.github_connector.service;

import com.aditi.github_connector.client.GithubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GithubService {
    private final GithubClient githubClient;

    public String fetchRepositories(String username) {
        return githubClient.getUserRepos(username);
    }
}
