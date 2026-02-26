package com.jparlant.controller;

import com.jparlant.cache.CacheNotifyService;
import com.jparlant.common.Result;
import com.jparlant.dto.CacheRefreshMessageDTO;
import com.jparlant.entity.ComplianceRule;
import com.jparlant.exception.BusinessException;
import com.jparlant.service.ComplianceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 合规规则管理控制器
 */
@Slf4j
@RestController
public class ComplianceController {

    @Autowired
    private ComplianceService complianceService;
    @Autowired
    private CacheNotifyService cacheNotifyService;


    /**
     * 获取指定Agent的所有合规规则
     */
    @GetMapping("/agents/{agentId}/compliance-rules")
    public Result<List<ComplianceRule>> listByAgent(@PathVariable Long agentId) {
        log.info("获取Agent的合规规则列表, agentId: {}", agentId);
        List<ComplianceRule> rules = complianceService.listByAgentId(agentId);
        return Result.success(rules);
    }

    /**
     * 根据ID获取合规规则详情
     */
    @GetMapping("/compliance-rules/{id}")
    public Result<ComplianceRule> getById(@PathVariable Long id) {
        log.info("获取合规规则详情, id: {}", id);
        ComplianceRule rule = complianceService.getById(id);
        if (rule == null) {
            throw new BusinessException(404, "合规规则不存在");
        }
        return Result.success(rule);
    }

    /**
     * 创建合规规则
     */
    @PostMapping("/compliance-rules")
    public Result<ComplianceRule> create(@RequestBody ComplianceRule complianceRule) {
        log.info("创建合规规则: {}", complianceRule.getName());
        ComplianceRule created = complianceService.create(complianceRule);
        cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.COMPLIANCE, created.getAgentId());
        return Result.success(created);
    }

    /**
     * 更新合规规则
     */
    @PutMapping("/compliance-rules/{id}")
    public Result<ComplianceRule> update(@PathVariable Long id, @RequestBody ComplianceRule complianceRule) {
        log.info("更新合规规则, id: {}", id);
        ComplianceRule updated = complianceService.update(id, complianceRule);
        cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.COMPLIANCE, updated.getAgentId());
        return Result.success(updated);
    }

    /**
     * 删除合规规则
     */
    @DeleteMapping("/compliance-rules/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除合规规则, id: {}", id);
        // 1. 删除前先获取详情，为了拿到 agentId
        ComplianceRule rule = complianceService.getById(id);
        if (rule == null) {
            return Result.success(); // 数据本身不存在，直接返回成功
        }

        // 2. 执行删除
        boolean success = complianceService.delete(id);
        if (!success) {
            throw new BusinessException("删除失败");
        }

        // 3. 删除成功后，通知刷新该 Agent 的规则缓存
        cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.COMPLIANCE, rule.getAgentId());
        return Result.success();
    }
}
