package com.jparlant.service.impl;

import com.jparlant.entity.AgentIntent;
import com.jparlant.mapper.AgentIntentMapper;
import com.jparlant.mapper.IntentStepMapper;
import com.jparlant.service.IntentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IntentServiceImpl implements IntentService {

    @Autowired
    private AgentIntentMapper agentIntentMapper;
    @Autowired
    private IntentStepMapper intentStepMapper;


    @Override
    public List<AgentIntent> list() {
        return agentIntentMapper.selectAll();
    }

    @Override
    public AgentIntent getById(Long id) {
        return agentIntentMapper.selectById(id);
    }

    @Override
    public List<AgentIntent> listByAgentId(Long agentId) {
        return agentIntentMapper.selectByAgentId(agentId);
    }

    @Override
    public AgentIntent create(AgentIntent agentIntent) {
        if(null == agentIntent.getFlowType()){
            agentIntent.setFlowType("LINEAR");
        }
        agentIntentMapper.insert(agentIntent);
        return agentIntent;
    }

    @Override
    @Transactional // 建议加上事务，保证更新和查询的一致性
    public AgentIntent update(Long id, AgentIntent agentIntent) {
        // 确保 ID 正确
        agentIntent.setId(id);

        // 此时调用的是动态 SQL，只有非 Null 的字段会被拼接到 SET 语句中
        int rows = agentIntentMapper.updateById(agentIntent);

        if (rows > 0) {
            // 重新查询最新数据返回给前端（包含那些没有被更新的旧字段）
            return agentIntentMapper.selectById(id);
        }
        return null;
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        // 删除意图下的相关步骤
        intentStepMapper.deleteByIntentId(id);
        return agentIntentMapper.deleteById(id) > 0;
    }


    @Override
    public Long getAgentIdByIntentId(Long intentId) {
        if (intentId == null) {
            return null;
        }
        AgentIntent intent = agentIntentMapper.selectById(intentId);
        return intent != null ? intent.getAgentId() : null;
    }
}
