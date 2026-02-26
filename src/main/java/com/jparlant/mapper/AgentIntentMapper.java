package com.jparlant.mapper;

import com.jparlant.entity.AgentIntent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AgentIntentMapper {

    List<AgentIntent> selectAll();

    AgentIntent selectById(@Param("id") Long id);

    List<AgentIntent> selectByAgentId(@Param("agentId") Long agentId);

    int insert(AgentIntent agentIntent);

    int updateById(AgentIntent agentIntent);

    int deleteById(@Param("id") Long id);
}
