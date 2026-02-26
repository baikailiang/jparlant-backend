# API 接口测试文档

本文档提供了所有API接口的测试用例。可以使用curl命令或Postman进行测试。

## 基础信息
- 基础URL: `http://localhost:8080/api`
- Content-Type: `application/json`

---

## 1. Agent管理接口

### 1.1 获取所有Agent
```bash
curl -X GET http://localhost:8080/api/agents
```

### 1.2 获取单个Agent
```bash
curl -X GET http://localhost:8080/api/agents/1
```

### 1.3 创建Agent
```bash
curl -X POST http://localhost:8080/api/agents \
  -H "Content-Type: application/json" \
  -d '{
    "name": "客服助手",
    "instructions": "你是一个专业的客服助手",
    "description": "处理客户咨询",
    "keywords": "客服,咨询",
    "status": true
  }'
```

### 1.4 更新Agent
```bash
curl -X PUT http://localhost:8080/api/agents/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "客服助手V2",
    "instructions": "你是一个专业的客服助手",
    "description": "处理客户咨询和投诉",
    "keywords": "客服,咨询,投诉",
    "status": true
  }'
```

### 1.5 删除Agent
```bash
curl -X DELETE http://localhost:8080/api/agents/1
```

---

## 2. 意图管理接口

### 2.1 获取Agent的所有意图
```bash
curl -X GET http://localhost:8080/api/agents/1/intents
```

### 2.2 获取单个意图
```bash
curl -X GET http://localhost:8080/api/intents/1
```

### 2.3 创建意图
```bash
curl -X POST http://localhost:8080/api/intents \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": 1,
    "name": "订单查询",
    "description": "帮助用户查询订单状态",
    "flowType": "LINEAR",
    "metadataJson": "{\"timeout\": 30}",
    "enabled": true
  }'
```

### 2.4 更新意图
```bash
curl -X PUT http://localhost:8080/api/intents/1 \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": 1,
    "name": "订单查询优化",
    "description": "帮助用户快速查询订单状态",
    "flowType": "CONDITIONAL",
    "metadataJson": "{\"timeout\": 60}",
    "enabled": true
  }'
```

### 2.5 删除意图
```bash
curl -X DELETE http://localhost:8080/api/intents/1
```

---

## 3. 步骤管理接口

### 3.1 获取意图的所有步骤
```bash
curl -X GET http://localhost:8080/api/intents/1/steps
```

### 3.2 获取单个步骤
```bash
curl -X GET http://localhost:8080/api/steps/1
```

### 3.3 创建步骤
```bash
curl -X POST http://localhost:8080/api/steps \
  -H "Content-Type: application/json" \
  -d '{
    "intentId": 1,
    "name": "收集订单号",
    "description": "从用户处获取订单号",
    "belongToPhase": "UNDERSTANDING",
    "priority": 1,
    "stepType": "INPUT",
    "prompt": "请提供您的订单号",
    "expectedInputsJson": "{\"orderNo\": \"string\"}",
    "validationJson": "{\"orderNo\": {\"required\": true}}",
    "dependencies": "",
    "canSkip": false,
    "skipToPrompt": "",
    "coreActionsJson": "[]"
  }'
```

### 3.4 更新步骤
```bash
curl -X PUT http://localhost:8080/api/steps/1 \
  -H "Content-Type: application/json" \
  -d '{
    "intentId": 1,
    "name": "收集订单号",
    "description": "从用户处获取订单号，支持多种格式",
    "belongToPhase": "UNDERSTANDING",
    "priority": 1,
    "stepType": "INPUT",
    "prompt": "请提供您的订单号（支持数字或字母）",
    "expectedInputsJson": "{\"orderNo\": \"string\"}",
    "validationJson": "{\"orderNo\": {\"required\": true, \"pattern\": \"^[A-Z0-9]+$\"}}",
    "dependencies": "",
    "canSkip": false,
    "skipToPrompt": "",
    "coreActionsJson": "[]"
  }'
```

### 3.5 删除步骤
```bash
curl -X DELETE http://localhost:8080/api/steps/1
```

### 3.6 批量更新步骤
```bash
curl -X PUT http://localhost:8080/api/steps/batch \
  -H "Content-Type: application/json" \
  -d '[
    {
      "id": 1,
      "priority": 1,
      "belongToPhase": "UNDERSTANDING"
    },
    {
      "id": 2,
      "priority": 2,
      "belongToPhase": "PROCESSING"
    }
  ]'
```

---

## 4. 合规规则接口

### 4.1 获取Agent的所有合规规则
```bash
curl -X GET http://localhost:8080/api/agents/1/compliance-rules
```

### 4.2 获取单个规则
```bash
curl -X GET http://localhost:8080/api/compliance-rules/1
```

### 4.3 创建规则
```bash
curl -X POST http://localhost:8080/api/compliance-rules \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": 1,
    "name": "敏感词过滤",
    "description": "过滤政治敏感词",
    "scope": "ALL",
    "keywords": "[\"敏感词1\", \"敏感词2\"]",
    "parameters": "{\"regex_patterns\": [\"pattern1\", \"pattern2\"]}",
    "conditionExpr": "",
    "blockedResponse": "您的输入包含敏感内容，请重新输入",
    "categories": "[\"安全\", \"合规\"]",
    "priority": 10,
    "enabled": true,
    "guidelinePrompt": "请注意避免使用敏感词"
  }'
```

### 4.4 更新规则
```bash
curl -X PUT http://localhost:8080/api/compliance-rules/1 \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": 1,
    "name": "敏感词过滤V2",
    "description": "过滤政治和暴力敏感词",
    "scope": "INPUT",
    "keywords": "[\"敏感词1\", \"敏感词2\", \"敏感词3\"]",
    "parameters": "{\"regex_patterns\": [\"pattern1\", \"pattern2\"]}",
    "conditionExpr": "",
    "blockedResponse": "您的输入包含不当内容，请修改后重新输入",
    "categories": "[\"安全\", \"合规\", \"内容审核\"]",
    "priority": 20,
    "enabled": true,
    "guidelinePrompt": "请注意文明用语"
  }'
```

### 4.5 删除规则
```bash
curl -X DELETE http://localhost:8080/api/compliance-rules/1
```

---

## 5. 术语库接口

### 5.1 获取Agent的所有术语
```bash
curl -X GET http://localhost:8080/api/agents/1/glossary
```

### 5.2 获取单个术语
```bash
curl -X GET http://localhost:8080/api/glossary/1
```

### 5.3 创建术语
```bash
curl -X POST http://localhost:8080/api/glossary \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": 1,
    "name": "API",
    "definition": "应用程序编程接口",
    "category": "技术术语",
    "synonyms": "[\"接口\", \"API接口\"]",
    "relatedNames": "[\"REST API\", \"GraphQL\"]",
    "examples": "{\"usage\": \"调用API获取数据\"}",
    "priority": 5
  }'
```

### 5.4 更新术语
```bash
curl -X PUT http://localhost:8080/api/glossary/1 \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": 1,
    "name": "API",
    "definition": "应用程序编程接口（Application Programming Interface）",
    "category": "技术术语",
    "synonyms": "[\"接口\", \"API接口\", \"程序接口\"]",
    "relatedNames": "[\"REST API\", \"GraphQL\", \"WebSocket\"]",
    "examples": "{\"usage\": \"通过API调用第三方服务\", \"code\": \"fetch('/api/data')\"}",
    "priority": 10
  }'
```

### 5.5 删除术语
```bash
curl -X DELETE http://localhost:8080/api/glossary/1
```

---

## 响应格式示例

### 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "name": "客服助手",
    ...
  }
}
```

### 错误响应
```json
{
  "code": 404,
  "message": "Agent不存在",
  "data": null
}
```

---

## 测试步骤建议

1. 首先测试Agent管理接口，创建一个Agent
2. 使用创建的Agent ID测试意图管理接口
3. 使用创建的意图ID测试步骤管理接口
4. 测试合规规则和术语库接口
5. 测试更新和删除操作

## 使用Postman测试

可以将以上curl命令导入到Postman中，或者创建一个Collection进行批量测试。

建议创建环境变量：
- `base_url`: http://localhost:8080/api
- `agent_id`: 动态设置为创建的Agent ID
- `intent_id`: 动态设置为创建的Intent ID
