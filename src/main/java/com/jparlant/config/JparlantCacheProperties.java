package com.jparlant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jparlant.cache")
public class JparlantCacheProperties {
    /**
     * Redis 缓存刷新通道名称
     */
    private String channel = "jparlant-cache-refresh-topic";
}
