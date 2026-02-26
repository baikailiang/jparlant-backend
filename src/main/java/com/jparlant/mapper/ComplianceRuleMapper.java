package com.jparlant.mapper;

import com.jparlant.entity.ComplianceRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ComplianceRuleMapper {
    
    List<ComplianceRule> selectAll();
    
    ComplianceRule selectById(@Param("id") Long id);
    
    List<ComplianceRule> selectByAgentId(@Param("agentId") Long agentId);
    
    int insert(ComplianceRule complianceRule);
    
    int updateById(ComplianceRule complianceRule);
    
    int deleteById(@Param("id") Long id);
}