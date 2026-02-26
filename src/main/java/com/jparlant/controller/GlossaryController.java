package com.jparlant.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jparlant.cache.CacheNotifyService;
import com.jparlant.common.Result;
import com.jparlant.dto.CacheRefreshMessageDTO;
import com.jparlant.dto.GlossaryDTO;
import com.jparlant.entity.Glossary;
import com.jparlant.exception.BusinessException;
import com.jparlant.service.GlossaryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 术语库管理控制器
 */
@Slf4j
@RestController
public class GlossaryController {

    @Autowired
    private GlossaryService glossaryService;
    @Autowired
    private CacheNotifyService cacheNotifyService;



    /**
     * 获取指定Agent的所有术语
     */
    @GetMapping("/agents/{agentId}/glossary")
    public Result<List<Glossary>> listByAgent(@PathVariable Long agentId) {
        log.info("获取Agent的术语列表, agentId: {}", agentId);
        List<Glossary> glossary = glossaryService.listByAgentId(agentId);
        return Result.success(glossary);
    }

    /**
     * 根据ID获取术语详情
     */
    @GetMapping("/glossary/{id}")
    public Result<Glossary> getById(@PathVariable Long id) {
        log.info("获取术语详情, id: {}", id);
        Glossary glossary = glossaryService.getById(id);
        if (glossary == null) {
            throw new BusinessException(404, "术语不存在");
        }
        return Result.success(glossary);
    }

    /**
     * 创建术语
     */
    @PostMapping("/glossary")
    public Result<Glossary> create(@RequestBody GlossaryDTO dto) {
        log.info("创建术语: {}", dto.getName());
        Glossary glossary = new Glossary();
        BeanUtils.copyProperties(dto, glossary);

        // 手动将 List/Map 转为 JSON 字符串存入实体类
        try {
            ObjectMapper mapper = new ObjectMapper();
            glossary.setSynonyms(mapper.writeValueAsString(dto.getSynonyms()));
            glossary.setRelatedNames(mapper.writeValueAsString(dto.getRelatedNames()));
            glossary.setExamples(mapper.writeValueAsString(dto.getExamples()));
        } catch (JsonProcessingException e) {
            log.error("JSON转换失败", e);
        }
        Glossary created = glossaryService.create(glossary);
        cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.GLOSSARY, created.getAgentId());
        return Result.success(created);
    }

    /**
     * 更新术语
     */
    @PutMapping("/glossary/{id}")
    public Result<Glossary> update(@PathVariable Long id, @RequestBody Glossary glossary) {
        log.info("更新术语, id: {}", id);
        Glossary updated = glossaryService.update(id, glossary);
        // 术语更新后，必须通知相关 Agent 刷新缓存
        if (updated != null) {
            cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.GLOSSARY, updated.getAgentId());
        }
        return Result.success(updated);
    }

    /**
     * 删除术语
     */
    @DeleteMapping("/glossary/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除术语, id: {}", id);
        // 1. 获取详情以便拿到 agentId
        Glossary glossary = glossaryService.getById(id);
        if (glossary == null) {
            return Result.success();
        }
        Long agentId = glossary.getAgentId();

        // 2. 执行删除
        boolean success = glossaryService.delete(id);
        if (!success) {
            throw new BusinessException("删除失败");
        }

        // 3. 删除成功后，通知刷新该 Agent 的术语缓存
        cacheNotifyService.notifyRefresh(CacheRefreshMessageDTO.RefreshType.GLOSSARY, agentId);
        return Result.success();
    }
}
