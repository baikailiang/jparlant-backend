package com.jparlant.controller;

import com.jparlant.cache.CacheNotifyService;
import com.jparlant.common.Result;
import com.jparlant.dto.CacheRefreshMessageDTO;
import com.jparlant.entity.Agent;
import com.jparlant.exception.BusinessException;
import com.jparlant.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Agent管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/agents")
public class AgentController {

    @Autowired
    private AgentService agentService;
    @Autowired
    private CacheNotifyService cacheNotifyService;


    /**
     * 获取所有Agent列表
     */
    @GetMapping
    public Result<List<Agent>> list() {
        log.info("获取Agent列表");
        List<Agent> agents = agentService.list();
        return Result.success(agents);
    }

    /**
     * 根据ID获取Agent详情
     */
    @GetMapping("/{id}")
    public Result<Agent> getById(@PathVariable Long id) {
        log.info("获取Agent详情, id: {}", id);
        Agent agent = agentService.getById(id);
        if (agent == null) {
            throw new BusinessException(404, "Agent不存在");
        }
        return Result.success(agent);
    }

    /**
     * 创建Agent
     */
    @PostMapping
    public Result<Agent> create(@RequestBody Agent agent) {
        log.info("创建Agent: {}", agent.getName());
        Agent created = agentService.create(agent);
        cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.ALL, created.getId());
        return Result.success(created);
    }

    /**
     * 更新Agent
     */
    @PutMapping("/{id}")
    public Result<Agent> update(@PathVariable Long id, @RequestBody Agent agent) {
        log.info("更新Agent, id: {}", id);
        Agent updated = agentService.update(id, agent);
        // 变更 Agent 定义
        cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.SINGLE_AGENT, id);
        return Result.success(updated);
    }

    /**
     * 删除Agent
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除Agent, id: {}", id);
        boolean success = agentService.delete(id);
        if (!success) {
            throw new BusinessException("删除失败");
        }
        cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.ALL, id);
        return Result.success();
    }


    @PostMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        // 校验参数合法性
        if (status != 0 && status != 1) {
            throw new BusinessException("非法状态码");
        }

        log.info("修改Agent状态, id: {}, 目标状态: {}", id, status == 1 ? "启用" : "禁用");

        // 调用 service 修改状态
        boolean success = agentService.updateStatus(id, status);

        if (!success) {
            throw new BusinessException("操作失败");
        }
        // 状态变更影响 Agent 加载
        cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.SINGLE_AGENT, id);
        return Result.success();
    }

}
