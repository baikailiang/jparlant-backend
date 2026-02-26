package com.jparlant.mapper;

import com.jparlant.entity.IntentStepTransition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IntentStepTransitionMapper {

    int batchInsert(@Param("list") List<IntentStepTransition> transitions);

    int deleteByIntentId(@Param("intentId") Long intentId);
}
