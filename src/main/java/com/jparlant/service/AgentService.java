package com.jparlant.service;

import com.jparlant.entity.Agent;
import java.util.List;

public interface AgentService {

    List<Agent> list();

    Agent getById(Long id);

    Agent create(Agent agent);

    Agent update(Long id, Agent agent);

    boolean delete(Long id);

    boolean updateStatus(Long id, Integer status);
}
