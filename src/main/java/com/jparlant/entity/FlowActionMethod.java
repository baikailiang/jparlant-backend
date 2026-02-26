package com.jparlant.entity;

import lombok.Data;

@Data
public class FlowActionMethod {
    private Long id;
    private Long beanId;
    private String methodName;
    private String displayName;
}
