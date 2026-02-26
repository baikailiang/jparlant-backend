# JParlant Backend

JParlant 管理系统后端服务，为 [jparlant-admin](https://gitee.com/sylvara/jparlant-admin) 前端项目提供 RESTful API 支持。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 2.7.14 | 核心框架 |
| Java | 17 | 开发语言 |
| MyBatis | 2.3.1 | ORM 持久层框架 |
| MySQL | 8.x | 关系型数据库 |
| Redis | - | 缓存及消息通知 |
| HikariCP | - | 数据库连接池 |
| Lombok | - | 简化代码 |
| Jackson | - | JSON 序列化 |

## 快速启动

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.x
- Redis (用于缓存通知)

### 1. 数据库和表结构初始化


### 2. 修改配置

编辑 `src/main/resources/application.yml`，配置数据库和 Redis 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jparlant?useSSL=false&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
    username: your_redis_user  # 无认证可删除此行
    password: your_redis_pass  # 无认证可删除此行
```

### 3. 启动服务

**方式一：Maven 插件启动（开发环境推荐）**
```bash
mvn spring-boot:run
```

**方式二：打包后运行**
```bash
mvn clean package -DskipTests
java -jar target/jparlant-backend-1.0.0.jar
```

**方式三：IDE 启动**

在 IntelliJ IDEA 中运行 `JparlantBackendApplication.java` 主类。

### 4. 验证启动

服务启动后访问：http://localhost:9085/api/agents

## 与前端项目[jparlant-admin](https://gitee.com/sylvara/jparlant-admin)配合

### 架构关系

```
┌─────────────────────┐      HTTP API      ┌─────────────────────┐
│   jparlant-admin    │  ───────────────>  │  jparlant-backend   │
│   (Vue 前端)        │   localhost:9085   │  (Spring Boot)      │
└─────────────────────┘                    └─────────────────────┘
                                                    │
                                           ┌───────┴───────┐
                                           │               │
                                        MySQL          Redis
```

### 前端代理配置

前端项目需要配置 API 代理，将 `/api` 请求转发到后端服务：

```javascript
// vite.config.js 或 vue.config.js
proxy: {
  '/api': {
    target: 'http://localhost:9085',
    changeOrigin: true
  }
}
```

### 跨域说明

后端已配置 CORS 允许所有来源访问，前端可直接调用 API，无需额外处理跨域问题。

## API 接口

所有接口统一前缀：`/api`，返回格式：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```


## 配置说明

### 核心配置项

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `server.port` | 9085 | 服务端口 |
| `server.servlet.context-path` | /api | 接口路径前缀 |


### Redis 缓存通知

后端通过 Redis Pub/Sub 发布缓存刷新通知，频道名默认：`jparlant-cache-refresh-topic`，可进行配置覆盖。

**注意：如果 Redis 开启了 ACL（Redis 6.0+），请确保该用户具有 Pub/Sub 相关权限**

