package com.aditi.githubreviewbot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "analysis")
@Data
public class AnalysisProperties {
    private Map<String, Map<String, String>> severity;
}
