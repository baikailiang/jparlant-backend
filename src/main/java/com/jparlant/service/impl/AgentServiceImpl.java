package com.jparlant.service.impl;

import com.jparlant.entity.Agent;
import com.jparlant.mapper.AgentMapper;
import com.jparlant.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentServiceImpl implements AgentService {

    @Autowired
    private AgentMapper agentMapper;

    @Override
    public List<Agent> list() {
        return agentMapper.selectAll();
    }

    @Override
    public Agent getById(Long id) {
        return agentMapper.selectById(id);
    }

    @Override
    public Agent create(Agent agent) {
        if (agent.getKeywords() != null && !agent.getKeywords().isEmpty()) {
            // 使用 replace 将所有中文逗号“，”替换为英文逗号“,”
            String sanitizedKeywords = agent.getKeywords().replace("，", ",");
            agent.setKeywords(sanitizedKeywords);
        }

        agentMapper.insert(agent);
        return agent;
    }

    @Override
    public Agent update(Long id, Agent agent) {
        agent.setId(id);
        agentMapper.updateById(agent);
        return agentMapper.selectById(id);
    }

    @Override
    public boolean delete(Long id) {
        return agentMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        return agentMapper.updateStatus(id, status) > 0;
    }
}
