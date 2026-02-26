package com.jparlant.service;

import com.jparlant.entity.FlowVariable;

import java.util.List;

public interface FlowVariableService {
    FlowVariable create(FlowVariable variable);
    FlowVariable update(Long id, FlowVariable variable);
    void delete(Long id);
    List<FlowVariable> listByIntentId(Long intentId);
}
