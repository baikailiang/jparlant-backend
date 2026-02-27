CREATE DATABASE IF NOT EXISTS jparlant CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE jparlant;




-- 规则引擎表
CREATE TABLE `compliance_rules` (
                                    `id` bigint(20) AUTO_INCREMENT PRIMARY KEY,
                                    `agent_id` bigint(20) NOT NULL COMMENT '所属AgentID',
    -- 核心识别字段
                                    `name` VARCHAR(100) NOT NULL COMMENT '规则名称',
                                    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
                                    `scope` VARCHAR(20) NOT NULL DEFAULT 'ALL' COMMENT '作用域: INPUT(检查用户输入), RESPONSE(检查AI回复), ALL(全量检查)',
    -- 判定逻辑字段
                                    `keywords` JSON DEFAULT NULL COMMENT '关键词列表 (JSON Array) - 第一层漏斗',
                                    `parameters` JSON DEFAULT NULL COMMENT '规则参数 (JSON Object: {"regex_patterns": ["..."]}) - 第二层漏斗',
                                    `condition_expr` TEXT DEFAULT NULL COMMENT 'SpEL触发条件表达式 - 第三层漏斗',
    -- 响应处理字段
                                    `blocked_response` TEXT DEFAULT NULL COMMENT '拦截后返回给用户的标准话术或提示',
    -- 管理与监控字段
                                    `categories` JSON DEFAULT NULL COMMENT '自定义分类标签 (JSON Array)',
                                    `priority` INT NOT NULL DEFAULT 1 COMMENT '优先级',
                                    `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '状态: 0禁用, 1启用',
    -- 审计字段
                                    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                    `guideline_prompt` TEXT DEFAULT NULL COMMENT '规则prompt',
    -- 索引优化
    -- 1. 最核心的查询索引：按Agent、状态、作用域和优先级过滤
                                    INDEX `idx_agent_scope_enabled_priority` (`agent_id`, `scope`, `enabled`, `priority`),
    -- 2. 方便后台管理查询
                                    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合规检查规则引擎配置表';


-- 术语定义表
CREATE TABLE `glossary` (
                            `id` bigint(64) AUTO_INCREMENT PRIMARY KEY,
                            `name` VARCHAR(100) NOT NULL COMMENT '术语名称',
                            `definition` TEXT NOT NULL COMMENT '定义描述',
                            `category` VARCHAR(50) NOT NULL COMMENT '分类',
                            `synonyms` JSON DEFAULT NULL COMMENT '同义词列表 (JSON Array)',
                            `related_names` JSON DEFAULT NULL COMMENT '相关术语 (JSON Array)',
                            `examples` JSON DEFAULT NULL COMMENT '示例 (JSON Object)',
                            `agent_id` bigint(64) NOT NULL COMMENT '所属Agent',
                            `priority` INT NOT NULL DEFAULT 1 COMMENT '优先级',
                            `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            INDEX `idx_agent_id` (`agent_id`) -- 必须加索引，用于缓存加载
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务词汇术语表';



-- Agent定义表
CREATE TABLE agents (
                        id bigint(64) AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        instructions TEXT,
                        description TEXT,
                        keywords VARCHAR(500),
                        status TINYINT(1) NOT NULL DEFAULT 1,
                        created_at TIMESTAMP NOT NULL,
                        updated_at TIMESTAMP NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='agent定义表';



-- Agent 意图定义表
CREATE TABLE `agent_intent` (
                                `id` bigint(64) AUTO_INCREMENT PRIMARY KEY,
                                `agent_id` bigint(64) NOT NULL COMMENT '所属Agent ID',
                                `name` VARCHAR(100) NOT NULL COMMENT '意图名称',
                                `description` TEXT COMMENT '意图的详细描述，交给LLM识别用',
                                `flow_type` VARCHAR(20) DEFAULT 'LINEAR' COMMENT '流程类型: LINEAR, CONDITIONAL, LOOP, INTERACTIVE',
                                `metadata_json` TEXT COMMENT '流程元数据 Map<String, Object> 的 JSON 格式',
                                `enabled` TINYINT(1) DEFAULT 1 COMMENT '是否启用',
                                `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent意图及流程主表';


-- 意图对应的业务步骤表
CREATE TABLE `intent_step` (
                               `id` bigint(64) AUTO_INCREMENT PRIMARY KEY,
                               `intent_id` bigint(64) NOT NULL COMMENT '所属意图ID',
                               `name` VARCHAR(100) NOT NULL COMMENT '步骤名称',
                               `description` TEXT COMMENT '步骤描述，用于指导LLM判断进度',
                               `belongToPhase` VARCHAR(20) DEFAULT 'READY' COMMENT '步骤所属阶段: READY, PROLOGUE, UNDERSTANDING, PROCESSING, REVIEW, PENDING, HANDOVER, SUSPENDED, CLOSING, ARCHIVED, TERMINATED',
                               `priority` INT DEFAULT 1 COMMENT '主干顺序（用于默认直线流程）',
                               `step_type` VARCHAR(20) DEFAULT 'INPUT' COMMENT '步骤类型: INPUT, ACTION, COMPLETED',
                               `prompt` TEXT COMMENT '引导用户的提示语',
                               `expected_inputs_json` TEXT COMMENT '期望的用户输入类型 Map<String, String> 的 JSON 格式',
                               `validation_json` TEXT COMMENT '输入验证规则 Map<String, Object> 的 JSON 格式',
                               `dependencies` TEXT COMMENT '前置依赖步骤的id集合 List<Long>',
                               `can_skip` TINYINT(1) DEFAULT 0 COMMENT '是否允许跳过, 0不允许，1允许',
                               `skip_to_prompt` TEXT COMMENT '跳跃时的引导提示语',
                               `ocr_action` TEXT COMMENT '图片识别执行器',
                               `core_actions_json` TEXT COMMENT '核心逻辑 List<ActionCall> JSON'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='意图业务步骤明细表';


CREATE TABLE `intent_step_transition` (
                                          `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          `intent_id` BIGINT NOT NULL COMMENT '所属意图',
                                          `from_step_id` BIGINT NOT NULL COMMENT '起始步骤',
                                          `to_step_id` BIGINT NOT NULL COMMENT '目标步骤',
                                          `branch_code` VARCHAR(50) DEFAULT 'DEFAULT' COMMENT '分支标识，如 DEFAULT / A / B / NEED_VERIFY / SKIP',
                                          `condition_json` TEXT COMMENT '触发条件（基于上下文、用户输入、状态）',
                                          `priority` INT DEFAULT 1 COMMENT '同一个 from_step 下的分支顺序',
                                          `is_default` TINYINT(1) DEFAULT 0 COMMENT '是否默认分支,0否，1是',
                                          KEY `idx_from_step` (`from_step_id`),
                                          KEY `idx_intent_from` (`intent_id`, `from_step_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='步骤流转 / 分支关系表';



CREATE TABLE `flow_variable` (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `intent_id` bigint NOT NULL COMMENT '所属意图 ID',
                                 `parent_id` bigint DEFAULT NULL COMMENT '父级ID（用于对象属性或数组元素定义）',
                                 `name` varchar(64) DEFAULT NULL COMMENT '变量名称（如果是数组元素，名称可为空）',
                                 `description` varchar(255) DEFAULT NULL COMMENT '变量描述',
                                 `type` varchar(32) NOT NULL COMMENT '类型: STRING, NUMBER, BOOLEAN, OBJECT, ARRAY',
                                 `is_required` tinyint(1) DEFAULT '0' COMMENT '是否必填',
                                 `default_value` text DEFAULT NULL COMMENT '默认值',
                                 `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
                                 `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`),
                                 UNIQUE KEY `uk_intent_parent_name` (`intent_id`, `parent_id`, `name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='流程变量结构定义表';



-- 合规违规记录表
CREATE TABLE `compliance_violations` (
                                         `id` bigint(64) AUTO_INCREMENT PRIMARY KEY,
                                         `agent_id` bigint(64) NOT NULL COMMENT '所属智能体ID',
                                         `rule_id` bigint(64) NOT NULL COMMENT '触发的规则ID',
                                         `rule_name` VARCHAR(255) COMMENT '规则快照名称',
                                         `compliance_level` VARCHAR(20) COMMENT '违规等级快照',
                                         `content` TEXT COMMENT '导致违规的原始文本内容',
                                         `session_id` VARCHAR(100) COMMENT '会话ID',
                                         `user_id` VARCHAR(100) COMMENT '用户唯一标识',
                                         `phase` VARCHAR(20) DEFAULT 'INPUT' COMMENT '拦截阶段: INPUT, PROCESSING, RESPONSE',
                                         `violation_data_json` TEXT COMMENT '违规详情元数据 Map 的 JSON 格式',
                                         `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '违规发生时间',
                                         INDEX `idx_session_id` (`session_id`),
                                         INDEX `idx_user_id` (`user_id`),
                                         INDEX `idx_agent_id` (`agent_id`),
                                         INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合规违规拦截流水表';



-- 工具执行日志表
CREATE TABLE `tool_execution_logs` (
                                       `id` bigint(64) AUTO_INCREMENT PRIMARY KEY,
                                       `agent_id` bigint(64) NOT NULL COMMENT '所属智能体ID',
                                       `tool_name` VARCHAR(255) NOT NULL COMMENT '调用的工具/Action名称',
                                       `session_id` VARCHAR(100) COMMENT '会话ID',
                                       `user_id` VARCHAR(100) COMMENT '用户唯一标识',
                                       `parameters_json` TEXT COMMENT '调用的输入参数 JSON',
                                       `execution_result_json` TEXT COMMENT '工具返回的原始结果 JSON',
                                       `success` TINYINT(1) DEFAULT 0 COMMENT '是否执行成功',
                                       `execution_time_ms` BIGINT COMMENT '执行耗时(毫秒)',
                                       `error_message` TEXT COMMENT '失败时的错误堆栈/描述',
                                       `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录时间',
                                       INDEX `idx_tool_name` (`tool_name`),
                                       INDEX `idx_session_id` (`session_id`),
                                       INDEX `idx_agent_id` (`agent_id`),
                                       INDEX `idx_success` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='外部工具/Action调用执行日志';


-- 执行器 Bean 信息表
CREATE TABLE `flow_action_bean` (
                                    `id` bigint NOT NULL AUTO_INCREMENT,
                                    `bean_name` varchar(100) NOT NULL COMMENT 'Spring Bean名称',
                                    `display_name` varchar(100) DEFAULT NULL COMMENT '显示名称/业务名称',
                                    `description` varchar(255) DEFAULT NULL COMMENT '描述',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uk_bean_name` (`bean_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程执行器Bean定义';

-- 执行器方法表
CREATE TABLE `flow_action_method` (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `bean_id` bigint NOT NULL COMMENT '所属Bean ID',
                                      `method_name` varchar(100) NOT NULL COMMENT '方法名称',
                                      `display_name` varchar(100) DEFAULT NULL COMMENT '显示名称',
                                      `description` varchar(255) DEFAULT NULL COMMENT '描述',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_bean_id` (`bean_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程执行器方法定义';

-- 属性/参数 Schema 表 (递归结构)
CREATE TABLE `flow_action_schema` (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `method_id` bigint NOT NULL COMMENT '所属方法ID',
                                      `parent_id` bigint DEFAULT NULL COMMENT '父节点ID (用于嵌套对象)',
                                      `schema_type` varchar(20) NOT NULL COMMENT '类型: PARAMETER(入参), RETURN(返回值)',
                                      `name` varchar(100) NOT NULL COMMENT '字段名/参数名',
                                      `type` varchar(50) DEFAULT NULL COMMENT '数据类型',
                                      `description` varchar(255) DEFAULT NULL COMMENT '描述',
                                      `is_complex` tinyint(1) DEFAULT '0' COMMENT '是否是复杂对象',
                                      `sort_order` int DEFAULT '0' COMMENT '排序',
                                      PRIMARY KEY (`id`),
                                      KEY `idx_method_id` (`method_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='流程执行器参数与返回结构定义';