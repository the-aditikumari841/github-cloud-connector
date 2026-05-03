package com.aditi.githubreviewbot.client.github;

import com.aditi.githubreviewbot.dto.request.CreateIssueRequest;
import com.aditi.githubreviewbot.dto.response.IssueResponse;
import com.aditi.githubreviewbot.dto.response.RepoResponse;
import com.aditi.githubreviewbot.util.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class GithubClient {
    private final WebClient webClient;
    private final TokenProvider tokenProvider;

    public List<RepoResponse> getUserRepos(String username) {
        return webClient.get()
                .uri("/users/{username}/repos", username)
                .header("Authorization", "Bearer " + tokenProvider.getToken())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .map(errorBody -> new RuntimeException(response.statusCode().value() + ":" + errorBody))
                )
                .bodyToFlux(RepoResponse.class)
                .collectList()
                .block();
    }

    public IssueResponse createIssue(String owner, String repo, CreateIssueRequest request) {
        return webClient.post()
                .uri("/repos/{owner}/{repo}/issues", owner, repo)
                .header("Authorization", "Bearer " + tokenProvider.getToken())
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .map(errorBody -> new RuntimeException(response.statusCode().value() + ":" + errorBody))
                )
                .bodyToMono(IssueResponse.class)
                .block();
    }

    public List<RepoResponse> getMyRepos() {
        return webClient.get()
                .uri("/user/repos")
                .header("Authorization", "Bearer " + tokenProvider.getToken())
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .map(errorBody -> new RuntimeException(response.statusCode().value() + ":" + errorBody))
                )
                .bodyToFlux(RepoResponse.class)
                .collectList()
                .block();
    }

    public void commentOnPr(String owner, String repo, int prNumber, String comment) {
        webClient.post()
                .uri("/repos/{owner}/{repo}/issues/{prNumber}/comments",
                        owner, repo, prNumber)
                .header("Authorization", "Bearer " + tokenProvider.getToken())
                .bodyValue(Map.of("body", comment))
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .map(errorBody -> new RuntimeException(
                                        "GitHub API error: " + response.statusCode().value() + ":" + errorBody
                                ))
                )
                .bodyToMono(Void.class)
                .block();
    }

    public List<String> getChangedFiles(String owner, String repo, int prNumber) {
        return webClient.get().uri("/repos/{owner}/{repo}/pulls/{prNumber}/files",
                        owner, repo, prNumber)
                .header("Authorization", "Bearer " + tokenProvider.getToken())
                .retrieve()
                .onStatus(status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .map(errorBody -> new RuntimeException(
                                        "GitHub API error: " + response.statusCode().value() + ":" + errorBody
                                )))
                .bodyToFlux(Map.class)
                .map(file -> (String) file.get("filename"))
                .collectList()
                .block();
    }
}
