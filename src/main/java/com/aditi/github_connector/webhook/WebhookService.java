package com.aditi.github_connector.webhook;

import com.aditi.github_connector.analysis.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebhookService {
    private final AnalysisService analysisService;

    public void process(String event, Map<String, Object> payload) {

        if (!"pull_request".equals(event)) return;

        if (payload.get("repository") == null) return;
        if (payload.get("pull_request") == null) return;

        Map<String, Object> repo = (Map<String, Object>) payload.get("repository");
        Map<String, Object> pr = (Map<String, Object>) payload.get("pull_request");
        Map<String, Object> head = (Map<String, Object>) pr.get("head");

        if (head == null) return;

        String cloneUrl = (String) repo.get("clone_url");
        String fullName = (String) repo.get("full_name");

        String branch = (String) head.get("ref");
        String sha = (String) head.get("sha");

        if (branch == null || sha == null) return;

        Number prNum = (Number) payload.get("number");
        int prNumber = prNum.intValue();

        String[] parts = fullName.split("/");
        String owner = parts[0];
        String repoName = parts[1];

        analysisService.analyze(owner, repoName, cloneUrl, prNumber, branch, sha);
    }
}
