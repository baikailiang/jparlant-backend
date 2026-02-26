package com.jparlant.entity;

import lombok.Data;
import java.util.List;
import java.util.Date;

@Data
public class FlowVariable {
    private Long id;
    private Long intentId;
    private Long parentId;      // 父级ID
    private String name;        // 变量名
    private String description; // 描述
    private String type;        // STRING, NUMBER, BOOLEAN, OBJECT, ARRAY
    private Integer isRequired; // 是否必填 (0:否, 1:是)
    private String defaultValue;// 默认值
    private Date createdAt;
    private Date updatedAt;

    // 逻辑字段：用于树形展示
    private List<FlowVariable> children;
}
