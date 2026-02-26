package com.jparlant.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class GlossaryDTO {
    private String name;
    private String definition;
    private String category;
    private List<String> synonyms;       // 匹配前端数组
    private List<String> relatedNames;   // 匹配前端数组
    private Map<String, String> examples; // 匹配前端对象
    private Long agentId;
    private Integer priority;
}
