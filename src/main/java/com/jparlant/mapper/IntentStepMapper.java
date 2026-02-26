package com.jparlant.mapper;

import com.jparlant.entity.IntentStep;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface IntentStepMapper {

    List<IntentStep> selectAll();

    IntentStep selectById(@Param("id") Long id);

    List<IntentStep> selectByIntentId(@Param("intentId") Long intentId);

    int insert(IntentStep intentStep);

    int updateById(IntentStep intentStep);

    int deleteById(@Param("id") Long id);

    void deleteByIntentId(@Param("intentId") Long intentId);
}
