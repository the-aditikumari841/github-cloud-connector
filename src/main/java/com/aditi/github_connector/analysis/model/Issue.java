package com.aditi.github_connector.analysis.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Issue {
    private String file;
    private int line;
    private int column;
    private String message;
}
