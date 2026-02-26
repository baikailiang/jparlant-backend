package com.jparlant.dto;

import com.jparlant.entity.IntentStepTransition;
import lombok.Data;

import java.util.List;

@Data
public class BatchSaveIntentFlowDTO {

    private Long intentId;

    /**
     * 步骤流转关系（连线）
     */
    private List<IntentStepTransition> transitions;

}
