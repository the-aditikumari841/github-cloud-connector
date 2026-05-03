package com.aditi.githubreviewbot.analysis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Issue {
    private String file;
    private int line;
    private int column;
    private String message;
    private String tool;
    private String severity;
    private String ruleId;
}
