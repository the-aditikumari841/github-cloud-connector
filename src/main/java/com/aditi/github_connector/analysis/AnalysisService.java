package com.aditi.github_connector.analysis;

import com.aditi.github_connector.ci.CIExecutor;
import com.aditi.github_connector.client.github.GithubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnalysisService {
    private final CIExecutor ciExecutor;
    private final GithubClient githubClient;

    public void analyze(String owner, String repo, String cloneUrl, int prNumber){
        String repoPath = ciExecutor.cloneRepo(cloneUrl);
        String result = ciExecutor.runCheckStyle(repoPath);
        String comment = formatComment(result);

        githubClient.commentOnPr(owner, repo, prNumber, comment);

    }

    private String formatComment(String result) {
        return "### Code Review Result\n\n" + result;
    }
}
