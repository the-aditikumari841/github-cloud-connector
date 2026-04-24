package com.aditi.github_connector.webhook;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {
    private final WebhookService webhookService;

    @PostMapping("/github")
    public ResponseEntity<Void> handle(
            @RequestBody Map<String, Object> payload,
            @RequestHeader("X-GitHub-Event") String event
    ) {
        webhookService.process(event, payload);
        return ResponseEntity.ok().build();
    }
}
