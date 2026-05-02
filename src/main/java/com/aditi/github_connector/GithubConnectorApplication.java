package com.aditi.github_connector;

import com.aditi.github_connector.config.AnalysisProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AnalysisProperties.class)
public class GithubConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubConnectorApplication.class, args);
    }

}
