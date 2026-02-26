package com.jparlant.service;

import com.jparlant.dto.BatchSaveIntentFlowDTO;

public interface IntentFlowService {

    /**
     * 批量保存步骤 & 流转关系
     */
    void batchSaveFlow(BatchSaveIntentFlowDTO request);

}
