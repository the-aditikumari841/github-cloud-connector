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

        if(!"pull_request".equals(event)) return;

        if(payload.get("repository") == null) return;
        Map<String, Object> repo = (Map<String, Object>) payload.get("repository");

        String cloneUrl = (String) repo.get("clone_url");
        String fullName = (String) repo.get("full_name");

        Number prNum = (Number) payload.get("number");
        int prNumber = prNum.intValue();

        String[] parts = fullName.split("/");
        String owner = parts[0];
        String repoName = parts[1];

        analysisService.analyze(owner, repoName, cloneUrl, prNumber);
    }
}
