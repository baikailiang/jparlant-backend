package com.jparlant.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ComplianceRule {
    private Long id;
    private Long agentId;
    private String name;
    private String description;
    private String scope;
    private String keywords;
    private String parameters;
    private String conditionExpr;
    private String blockedResponse;
    private String categories;
    private Integer priority;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String guidelinePrompt;
}