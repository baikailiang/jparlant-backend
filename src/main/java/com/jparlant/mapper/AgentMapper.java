package com.jparlant.mapper;

import com.jparlant.entity.Agent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface AgentMapper {

    List<Agent> selectAll();

    Agent selectById(@Param("id") Long id);

    int insert(Agent agent);

    int updateById(Agent agent);

    int deleteById(@Param("id") Long id);

    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
