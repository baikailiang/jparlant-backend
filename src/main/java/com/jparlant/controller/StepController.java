package com.jparlant.controller;

import com.jparlant.cache.CacheNotifyService;
import com.jparlant.common.Result;
import com.jparlant.dto.BatchSaveIntentFlowDTO;
import com.jparlant.dto.BeanWithMethodsDTO;
import com.jparlant.dto.CacheRefreshMessageDTO;
import com.jparlant.entity.IntentStep;
import com.jparlant.exception.BusinessException;
import com.jparlant.service.FlowMetadataService;
import com.jparlant.service.IntentFlowService;
import com.jparlant.service.IntentService;
import com.jparlant.service.StepService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 步骤管理控制器
 */
@Slf4j
@RestController
public class StepController {

    @Autowired
    private StepService stepService;
    @Autowired
    private IntentFlowService intentFlowService;
    @Autowired
    private FlowMetadataService flowMetadataService;
    @Autowired
    private IntentService intentService;
    @Autowired
    private CacheNotifyService cacheNotifyService;



    /**
     * 获取指定意图的所有步骤
     */
    @GetMapping("/intents/{intentId}/steps")
    public Result<List<IntentStep>> listByIntent(@PathVariable Long intentId) {
        log.info("获取意图的步骤列表, intentId: {}", intentId);
        List<IntentStep> steps = stepService.listByIntentId(intentId);
        return Result.success(steps);
    }

    /**
     * 根据ID获取步骤详情
     */
    @GetMapping("/steps/{id}")
    public Result<IntentStep> getById(@PathVariable Long id) {
        log.info("获取步骤详情, id: {}", id);
        IntentStep step = stepService.getById(id);
        if (step == null) {
            throw new BusinessException(404, "步骤不存在");
        }
        return Result.success(step);
    }

    /**
     * 创建步骤
     */
    @PostMapping("/steps")
    public Result<IntentStep> create(@RequestBody IntentStep intentStep) {
        log.info("创建步骤: {}", intentStep);
        IntentStep created = stepService.create(intentStep);
        refreshAgentFlowByIntentId(created.getIntentId());
        return Result.success(created);
    }

    /**
     * 更新步骤
     */
    @PutMapping("/steps/{id}")
    public Result<IntentStep> update(@PathVariable Long id, @RequestBody IntentStep intentStep) {
        log.info("更新步骤, id: {}", id);
        IntentStep updated = stepService.update(id, intentStep);
        refreshAgentFlowByIntentId(updated.getIntentId());
        return Result.success(updated);
    }

    /**
     * 删除步骤
     */
    @DeleteMapping("/steps/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除步骤, id: {}", id);
        // 1. 先查出来，拿到 intentId
        IntentStep step = stepService.getById(id);
        if (step == null) {
            return Result.success();
        }
        Long intentId = step.getIntentId();

        // 2. 执行删除
        boolean success = stepService.delete(id);
        if (!success) {
            throw new BusinessException("删除失败");
        }

        // 3. 刷新缓存
        refreshAgentFlowByIntentId(intentId);
        return Result.success();
    }


    @PostMapping("/steps/batch/line")
    public Result<Void> batchSaveStepsAndTransitions(@RequestBody BatchSaveIntentFlowDTO request) {
        log.info("批量保存流程配置: {}", request);
        intentFlowService.batchSaveFlow(request);
        Long agentId = intentService.getAgentIdByIntentId(request.getIntentId());
        cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.AGENT_FLOW, agentId);
        return Result.success();
    }


    /**
     * 获取所有可用的业务执行器及方法 (从数据库获取)
     */
    @GetMapping("/steps/action")
    public Result<List<BeanWithMethodsDTO>> listAvailableActions() {
        log.info("获取所有可用业务动作元数据");
        List<BeanWithMethodsDTO> actions = flowMetadataService.getAllActionsFromDb();
        return Result.success(actions);
    }


    /**
     * 通过 IntentId 刷新 Agent 级别的流程缓存
     */
    private void refreshAgentFlowByIntentId(Long intentId) {
        try {
            Long agentId = intentService.getAgentIdByIntentId(intentId);
            if (agentId != null) {
                cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.AGENT_FLOW, agentId);
            }
        } catch (Exception e) {
            log.error("刷新 AgentFlow 缓存失败, intentId: {}", intentId, e);
        }
    }

}
