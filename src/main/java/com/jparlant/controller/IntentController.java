package com.jparlant.controller;

import com.jparlant.cache.CacheNotifyService;
import com.jparlant.common.Result;
import com.jparlant.dto.CacheRefreshMessageDTO;
import com.jparlant.entity.AgentIntent;
import com.jparlant.exception.BusinessException;
import com.jparlant.service.IntentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 意图管理控制器
 */
@Slf4j
@RestController
public class IntentController {

    @Autowired
    private IntentService intentService;
    @Autowired
    private CacheNotifyService cacheNotifyService;


    /**
     * 获取指定Agent的所有意图
     */
    @GetMapping("/agents/{agentId}/intents")
    public Result<List<AgentIntent>> listByAgent(@PathVariable Long agentId) {
        log.info("获取Agent的意图列表, agentId: {}", agentId);
        List<AgentIntent> intents = intentService.listByAgentId(agentId);
        return Result.success(intents);
    }

    /**
     * 根据ID获取意图详情
     */
    @GetMapping("/intents/{id}")
    public Result<AgentIntent> getById(@PathVariable Long id) {
        log.info("获取意图详情, id: {}", id);
        AgentIntent intent = intentService.getById(id);
        if (intent == null) {
            throw new BusinessException(404, "意图不存在");
        }
        return Result.success(intent);
    }

    /**
     * 创建意图
     */
    @PostMapping("/intents")
    public Result<AgentIntent> create(@RequestBody AgentIntent agentIntent) {
        log.info("创建意图: {}", agentIntent.getName());
        AgentIntent created = intentService.create(agentIntent);
        // 创建成功后，刷新该 Agent 的流程/意图缓存
        if (created != null) {
            cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.AGENT_FLOW, created.getAgentId());
        }
        return Result.success(created);
    }

    /**
     * 更新意图
     */
    @PutMapping("/intents/{id}")
    public Result<AgentIntent> update(@PathVariable Long id, @RequestBody AgentIntent agentIntent) {
        log.info("更新意图, id: {}", id);
        AgentIntent updated = intentService.update(id, agentIntent);
        cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.AGENT_FLOW, updated.getAgentId());
        return Result.success(updated);
    }

    /**
     * 删除意图
     */
    @DeleteMapping("/intents/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除意图, id: {}", id);
        // 1. 删除前查询，获取所属 AgentId
        AgentIntent intent = intentService.getById(id);
        if (intent == null) {
            return Result.success();
        }
        Long agentId = intent.getAgentId();

        // 2. 执行删除
        boolean success = intentService.delete(id);
        if (!success) {
            throw new BusinessException("删除失败");
        }

        // 3. 删除成功后，刷新缓存
        cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.AGENT_FLOW, agentId);
        return Result.success();
    }
}
