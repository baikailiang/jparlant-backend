package com.jparlant.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ToolExecutionLog {
    private Long id;
    private Long agentId;
    private String toolName;
    private String sessionId;
    private String userId;
    private String parametersJson;
    private String executionResultJson;
    private Boolean success;
    private Long executionTimeMs;
    private String errorMessage;
    private LocalDateTime createdAt;
}