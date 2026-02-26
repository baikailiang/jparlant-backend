package com.jparlant.service;

import com.jparlant.entity.AgentIntent;
import java.util.List;

public interface IntentService {

    List<AgentIntent> list();

    AgentIntent getById(Long id);

    List<AgentIntent> listByAgentId(Long agentId);

    AgentIntent create(AgentIntent agentIntent);

    AgentIntent update(Long id, AgentIntent agentIntent);

    boolean delete(Long id);

    Long getAgentIdByIntentId(Long intentId);
}
