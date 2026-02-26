package com.jparlant.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AgentIntent {
    private Long id;
    private Long agentId;
    private String name;
    private String description;
    private String flowType;
    private String metadataJson;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}