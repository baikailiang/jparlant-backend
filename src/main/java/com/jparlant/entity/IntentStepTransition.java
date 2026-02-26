package com.jparlant.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class IntentStepTransition implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 所属意图ID
     */
    private Long intentId;

    /**
     * 起始步骤ID
     */
    private Long fromStepId;

    /**
     * 目标步骤ID
     */
    private Long toStepId;

    /**
     * 分支标识，如 DEFAULT / A / B / NEED_VERIFY / SKIP
     */
    private String branchCode;

    /**
     * 触发条件 JSON（基于上下文、用户输入、状态）
     */
    private String conditionJson;

    /**
     * 同一个 from_step 下的分支顺序
     */
    private Integer priority;

    /**
     * 是否默认分支：0 否，1 是
     */
    private Integer isDefault;
}
