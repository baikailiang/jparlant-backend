package com.jparlant.mapper;

import com.jparlant.entity.FlowVariable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FlowVariableMapper {
    int insert(FlowVariable variable);
    int updateById(FlowVariable variable);
    int deleteById(Long id);
    // 级联删除：根据父ID删除所有子项
    int deleteByParentId(Long parentId);

    FlowVariable selectById(Long id);
    List<FlowVariable> selectByIntentId(Long intentId);
    // 查询某个节点下的子节点
    List<FlowVariable> selectByParentId(Long parentId);
}