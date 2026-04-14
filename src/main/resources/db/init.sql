CREATE TABLE `intent_step` (
   `id` bigint(64) NOT NULL AUTO_INCREMENT,
   `intent_id` bigint(64) NOT NULL COMMENT '所属意图ID',
   `name` varchar(100) NOT NULL COMMENT '步骤名称',
   `description` text COMMENT '步骤描述，用于指导LLM判断进度',
   `belong_to_phase` varchar(20) DEFAULT 'READY' COMMENT '步骤所属阶段: READY, PROLOGUE, UNDERSTANDING, PROCESSING, REVIEW, PENDING, HANDOVER, SUSPENDED, CLOSING, ARCHIVED, TERMINATED',
   `priority` int(11) DEFAULT '1' COMMENT '主干顺序（用于默认直线流程）',
   `step_type` varchar(20) DEFAULT 'INPUT' COMMENT '步骤类型: INPUT, CONFIRM, ACTION, FLOW_CONTROL',
   `prompt` text COMMENT '引导用户的提示语',
   `expected_inputs_json` text COMMENT '期望的用户输入类型 Map<String, String> 的 JSON 格式',
   `validation_json` text COMMENT '输入验证规则 Map<String, Object> 的 JSON 格式',
   `dependencies` text COMMENT '前置依赖步骤的id集合 List<Long>',
   `can_skip` tinyint(1) DEFAULT '0' COMMENT '是否允许跳过，0不允许，1允许',
   `skip_to_prompt` text COMMENT '跳跃时的引导提示语',
   `core_actions_json` text COMMENT '核心业务处理器',
   `ocr_action` text COMMENT '图片识别执行器',
   `is_direct_return` int(11) DEFAULT '0' COMMENT '是否是需要直接返回提示，不需要经过大模型回答,0否，1是',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=87 DEFAULT CHARSET=utf8mb4 COMMENT='意图业务步骤明细表';


CREATE TABLE `intent_step_transition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `intent_id` bigint(20) NOT NULL COMMENT '所属意图',
  `from_step_id` bigint(20) NOT NULL COMMENT '起始步骤',
  `to_step_id` bigint(20) NOT NULL COMMENT '目标步骤',
  `branch_code` varchar(50) DEFAULT 'DEFAULT' COMMENT '分支标识，如 DEFAULT / A / B / NEED_VERIFY / SKIP',
  `condition_json` text COMMENT '触发条件（基于上下文、用户输入、状态）',
  `priority` int(11) DEFAULT '1' COMMENT '同一个 from_step 下的分支顺序',
  `is_default` tinyint(1) DEFAULT '0' COMMENT '是否默认分支，0否，1是',
  PRIMARY KEY (`id`),
  KEY `idx_from_step` (`from_step_id`),
  KEY `idx_intent_from` (`intent_id`,`from_step_id`)
) ENGINE=InnoDB AUTO_INCREMENT=179 DEFAULT CHARSET=utf8mb4 COMMENT='步骤流转 / 分支关系表';


CREATE TABLE `glossary` (
    `id` bigint(64) NOT NULL AUTO_INCREMENT,
    `name` varchar(100) NOT NULL COMMENT '术语名称',
    `definition` text NOT NULL COMMENT '定义描述',
    `category` varchar(50) NOT NULL COMMENT '分类',
    `synonyms` json DEFAULT NULL COMMENT '同义词列表 (JSON Array)',
    `related_names` json DEFAULT NULL COMMENT '相关术语 (JSON Array)',
    `examples` json DEFAULT NULL COMMENT '示例 (JSON Object)',
    `agent_id` bigint(64) NOT NULL COMMENT '所属Agent',
    `priority` int(11) NOT NULL DEFAULT '1' COMMENT '优先级',
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_agent_id` (`agent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COMMENT='业务词汇术语表';

CREATE TABLE `flow_variable` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
     `intent_id` bigint(20) NOT NULL COMMENT '所属意图 ID',
     `parent_id` bigint(20) DEFAULT NULL COMMENT '父级ID（用于对象属性或数组元素定义）',
     `name` varchar(64) DEFAULT NULL COMMENT '变量名称（如果是数组元素，名称可为空）',
     `description` varchar(255) DEFAULT NULL COMMENT '变量描述',
     `type` varchar(32) NOT NULL COMMENT '类型: STRING, NUMBER, BOOLEAN, OBJECT, ARRAY',
     `is_required` tinyint(1) DEFAULT '0' COMMENT '是否必填',
     `default_value` text COMMENT '默认值',
     `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
     `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     PRIMARY KEY (`id`),
     KEY `idx_intent_parent` (`intent_id`,`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=80 DEFAULT CHARSET=utf8mb4 COMMENT='流程变量结构定义表';


CREATE TABLE `compliance_rules` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `agent_id` bigint(20) NOT NULL COMMENT '所属AgentID',
    `name` varchar(100) NOT NULL COMMENT '规则名称',
    `description` varchar(255) DEFAULT NULL COMMENT '描述',
    `scope` varchar(20) NOT NULL DEFAULT 'ALL' COMMENT '作用域: INPUT(检查用户输入), RESPONSE(检查AI回复), ALL(全量检查)',
    `keywords` json DEFAULT NULL COMMENT '关键词列表 (JSON Array) - 第一层漏斗',
    `parameters` json DEFAULT NULL COMMENT '规则参数 (JSON Object: {"regex_patterns": ["..."]}) - 第二层漏斗',
    `condition_expr` text COMMENT 'SpEL触发条件表达式 - 第三层漏斗',
    `blocked_response` text COMMENT '拦截后返回给用户的标准话术或提示',
    `categories` json DEFAULT NULL COMMENT '自定义分类标签 (JSON Array)',
    `priority` int(11) NOT NULL DEFAULT '1' COMMENT '优先级',
    `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态: 0禁用, 1启用',
    `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `guideline_prompt` text COMMENT '规则prompt',
    PRIMARY KEY (`id`),
    KEY `idx_agent_scope_enabled_priority` (`agent_id`,`scope`,`enabled`,`priority`),
    KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COMMENT='合规检查规则引擎配置表';

CREATE TABLE `agents` (
  `id` bigint(64) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `instructions` text,
  `description` text,
  `keywords` varchar(500) DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `created_at` timestamp NOT NULL,
  `updated_at` timestamp NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='agent定义表';


CREATE TABLE `agent_intent` (
    `id` bigint(64) NOT NULL AUTO_INCREMENT,
    `agent_id` bigint(64) NOT NULL COMMENT '所属Agent ID',
    `name` varchar(100) NOT NULL COMMENT '意图名称',
    `description` text COMMENT '意图的详细描述，交给LLM识别用',
    `flow_type` varchar(20) DEFAULT 'LINEAR' COMMENT '流程类型: LINEAR, CONDITIONAL, LOOP, INTERACTIVE',
    `metadata_json` text COMMENT '流程元数据 Map<String, Object> 的 JSON 格式',
    `enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用',
    `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=111 DEFAULT CHARSET=utf8mb4 COMMENT='Agent意图及流程主表';



