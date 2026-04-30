package com.aditi.github_connector.analysis.analyzer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnalyzerFactory {
    private final List<Analyzer> analyzers;

    public List<Analyzer> getAnalyzers(List<String> files) {
        return analyzers.stream()
                .filter(analyzer -> files.stream().anyMatch(file -> analyzer.supports(file)))
                .toList();
    }
}
