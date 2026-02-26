package com.jparlant.mapper;

import com.jparlant.entity.Glossary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface GlossaryMapper {

    List<Glossary> selectAll();

    Glossary selectById(@Param("id") Long id);

    List<Glossary> selectByAgentId(@Param("agentId") Long agentId);

    int insert(Glossary glossary);

    int updateById(Glossary glossary);

    int deleteById(@Param("id") Long id);
}
