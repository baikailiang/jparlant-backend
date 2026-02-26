package com.jparlant.service;

import com.jparlant.entity.ComplianceRule;
import java.util.List;

public interface ComplianceService {

    List<ComplianceRule> list();

    ComplianceRule getById(Long id);

    List<ComplianceRule> listByAgentId(Long agentId);

    ComplianceRule create(ComplianceRule complianceRule);

    ComplianceRule update(Long id, ComplianceRule complianceRule);

    boolean delete(Long id);
}
