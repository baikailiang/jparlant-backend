package com.jparlant.service.impl;

import com.jparlant.entity.ComplianceRule;
import com.jparlant.mapper.ComplianceRuleMapper;
import com.jparlant.service.ComplianceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplianceServiceImpl implements ComplianceService {

    @Autowired
    private ComplianceRuleMapper complianceRuleMapper;

    @Override
    public List<ComplianceRule> list() {
        return complianceRuleMapper.selectAll();
    }

    @Override
    public ComplianceRule getById(Long id) {
        return complianceRuleMapper.selectById(id);
    }

    @Override
    public List<ComplianceRule> listByAgentId(Long agentId) {
        return complianceRuleMapper.selectByAgentId(agentId);
    }

    @Override
    public ComplianceRule create(ComplianceRule complianceRule) {
        complianceRuleMapper.insert(complianceRule);
        return complianceRule;
    }

    @Override
    public ComplianceRule update(Long id, ComplianceRule complianceRule) {
        complianceRule.setId(id);
        complianceRuleMapper.updateById(complianceRule);
        return complianceRuleMapper.selectById(id);
    }

    @Override
    public boolean delete(Long id) {
        return complianceRuleMapper.deleteById(id) > 0;
    }
}
