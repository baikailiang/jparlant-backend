package com.jparlant.service.impl;

import com.jparlant.entity.IntentStep;
import com.jparlant.mapper.IntentStepMapper;
import com.jparlant.service.StepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StepServiceImpl implements StepService {

    @Autowired
    private IntentStepMapper intentStepMapper;

    @Override
    public List<IntentStep> list() {
        return intentStepMapper.selectAll();
    }

    @Override
    public IntentStep getById(Long id) {
        return intentStepMapper.selectById(id);
    }

    @Override
    public List<IntentStep> listByIntentId(Long intentId) {
        return intentStepMapper.selectByIntentId(intentId);
    }

    @Override
    public IntentStep create(IntentStep intentStep) {
        intentStepMapper.insert(intentStep);
        return intentStep;
    }

    @Override
    public IntentStep update(Long id, IntentStep intentStep) {
        intentStep.setId(id);
        intentStepMapper.updateById(intentStep);
        return intentStepMapper.selectById(id);
    }

    @Override
    public boolean delete(Long id) {
        return intentStepMapper.deleteById(id) > 0;
    }

}
