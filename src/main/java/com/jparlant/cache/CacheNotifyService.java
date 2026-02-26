package com.jparlant.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jparlant.config.JparlantCacheProperties;
import com.jparlant.dto.CacheRefreshMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CacheNotifyService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JparlantCacheProperties jparlantCacheProperties;

    public void notifyRefresh(CacheRefreshMessageDTO.RefreshType type, Long agentId) {
        try {
            CacheRefreshMessageDTO message = new CacheRefreshMessageDTO(type, agentId, String.valueOf(System.currentTimeMillis()));

            // 手动转为 JSON 字符串
            String jsonMessage = objectMapper.writeValueAsString(message);

            log.info("发布缓存刷新通知 (String模式): {}", jsonMessage);
            redisTemplate.convertAndSend(jparlantCacheProperties.getChannel(), jsonMessage);
        } catch (JsonProcessingException e) {
            log.error("JSON 转换失败", e);
        }
    }
}
