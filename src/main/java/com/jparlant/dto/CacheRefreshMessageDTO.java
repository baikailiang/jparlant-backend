package com.jparlant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheRefreshMessageDTO implements Serializable {

    // 消息类型
    public enum RefreshType {
        SINGLE_AGENT,    // 刷新单个智能体全量缓存
        AGENT_FLOW,      // 仅流程
        COMPLIANCE,      // 仅合规
        GLOSSARY,        // 仅术语表
        ALL              // 全局清空
    }

    private RefreshType type;
    private Long agentId;    // 当类型非 ALL 时有效
    private String timestamp; // 防止消息重复或统计用
}
