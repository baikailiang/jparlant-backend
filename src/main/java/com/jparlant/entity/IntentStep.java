package com.jparlant.entity;

import lombok.Data;

@Data
public class IntentStep {
    private Long id;
    private Long intentId;
    private String name;
    private String description;
    private String belongToPhase;
    private Integer priority;
    private String stepType;
    private String prompt;
    private String expectedInputsJson;
    private String validationJson;
    private String dependencies;
    private Boolean canSkip;
    private String skipToPrompt;
    private String ocrAction;
    private String coreActionsJson;
    private Integer isDirectReturn;
}