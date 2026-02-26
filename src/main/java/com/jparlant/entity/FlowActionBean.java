package com.jparlant.entity;


import lombok.Data;

import java.util.List;

@Data
public class FlowActionBean {
    private Long id;
    private String beanName;
    private String displayName;
    private List<FlowActionMethod> methods; // 用于一对多映射
}
