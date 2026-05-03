package com.aditi.githubreviewbot;

import com.aditi.githubreviewbot.config.AnalysisProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AnalysisProperties.class)
public class GithubConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubConnectorApplication.class, args);
    }

}
