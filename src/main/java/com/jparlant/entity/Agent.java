package com.jparlant.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Agent {
    private Long id;
    private String name;
    private String instructions;
    private String description;
    private String keywords;
    private Boolean status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}