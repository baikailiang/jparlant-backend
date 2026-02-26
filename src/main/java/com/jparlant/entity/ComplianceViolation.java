package com.jparlant.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ComplianceViolation {
    private Long id;
    private Long agentId;
    private Long ruleId;
    private String ruleName;
    private String complianceLevel;
    private String content;
    private String sessionId;
    private String userId;
    private String phase;
    private String violationDataJson;
    private LocalDateTime createdAt;
}