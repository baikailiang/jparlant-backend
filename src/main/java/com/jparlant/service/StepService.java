package com.jparlant.service;

import com.jparlant.entity.IntentStep;
import java.util.List;

public interface StepService {

    List<IntentStep> list();

    IntentStep getById(Long id);

    List<IntentStep> listByIntentId(Long intentId);

    IntentStep create(IntentStep intentStep);

    IntentStep update(Long id, IntentStep intentStep);

    boolean delete(Long id);
}
