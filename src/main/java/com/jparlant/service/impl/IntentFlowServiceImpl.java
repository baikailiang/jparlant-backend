package com.jparlant.service.impl;

import com.jparlant.dto.BatchSaveIntentFlowDTO;
import com.jparlant.entity.IntentStepTransition;
import com.jparlant.mapper.IntentStepTransitionMapper;
import com.jparlant.service.IntentFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IntentFlowServiceImpl implements IntentFlowService {



    @Autowired
    private IntentStepTransitionMapper transitionMapper;

    @Transactional
    @Override
    public void batchSaveFlow(BatchSaveIntentFlowDTO request) {

        List<IntentStepTransition> transitions = request.getTransitions();

        if (transitions == null || transitions.isEmpty()) {
            return;
        }

        Long intentId = request.getIntentId();

        // 删除该 intent 下旧的连线
        transitionMapper.deleteByIntentId(intentId);

        // 插入新的连线
        if (!transitions.isEmpty()) {
            transitionMapper.batchInsert(transitions);
        }
    }

}

