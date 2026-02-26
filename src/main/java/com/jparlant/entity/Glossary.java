package com.jparlant.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Glossary {
    private Long id;
    private String name;
    private String definition;
    private String category;
    private String synonyms;
    private String relatedNames;
    private String examples;
    private Long agentId;
    private Integer priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}