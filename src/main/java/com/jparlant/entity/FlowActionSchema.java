package com.jparlant.entity;

import lombok.Data;

@Data
public class FlowActionSchema {
    private Long id;
    private Long methodId;
    private Long parentId;
    private String schemaType;
    private String name;
    private String type;
    private String description;
    private Integer isComplex;
    private Integer sortOrder;
}
