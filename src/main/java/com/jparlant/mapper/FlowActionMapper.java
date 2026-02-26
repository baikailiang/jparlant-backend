package com.jparlant.mapper;

import com.jparlant.entity.FlowActionBean;
import com.jparlant.entity.FlowActionSchema;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FlowActionMapper {
    // 查询所有 Bean 及其关联的 Method
    List<FlowActionBean> selectAllBeansAndMethods();

    // 查询所有 Schema 节点
    List<FlowActionSchema> selectAllSchemas();
}
