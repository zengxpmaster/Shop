# 分销系统软件设计说明书 (Software Design Specification)

**项目名称：** 分销商城系统  
**版本号：** V1.0  
**日期：** 2026 年 03 月  
**状态：** 初稿

---

## 1. 引言

### 1.1 编写目的
本文档旨在为分销系统的开发团队提供详细的技术设计指导。文档基于需求分析报告、模块设计文档及数据库设计文档，明确了系统架构、模块逻辑、接口规范、安全策略及部署方案，确保开发工作的一致性、规范性和可维护性。

### 1.2 适用范围
本文档适用于系统设计人员、后端开发人员、前端开发人员、测试人员及运维人员。

### 1.3 术语定义
| 术语 | 定义 |
|------|------|
| 分销商 | 拥有推广权限，可通过分享商品获取佣金的用户 |
| 佣金 | 分销商推广商品成交后获得的奖励金额 |
| 冻结期 | 佣金结算前的一段观察期（如 7 天），防止退货导致佣金误发 |

### 1.4 参考文献
1. 《分销系统需求分析报告》
2. 《分销系统模块设计文档》
3. 《分销系统数据库设计文档》

---

## 2. 系统架构设计

### 2.1 逻辑架构
系统采用分层架构设计，确保各层职责单一，耦合度低。

```mermaid
graph TD
    Client[客户端 (小程序/H5/Admin)] --> Gateway[API 网关]
    Gateway --> Auth[认证服务]
    Gateway --> Business[业务服务集群]
    
    subgraph Business Services
        UserSvc[用户服务]
        ProductSvc[商品服务]
        OrderSvc[订单服务]
        DistSvc[分销服务]
        FinanceSvc[金融服务]
        PaySvc[支付服务]
    end
    
    Business --> Cache[Redis 缓存]
    Business --> MQ[消息队列]
    Business --> DB[MySQL 数据库]
    Business --> OSS[对象存储]
```

### 2.2 技术栈选型
| 层次 | 技术选型 | 说明 |
|------|----------|------|
| **前端** | Uni-app / Vue3 | 多端兼容（小程序、H5、App） |
| **管理端** | Vue3 + Element Plus | 后台管理系统 |
| **后端** | Spring Boot 2.7+ | 核心业务逻辑 |
| **数据库** | MySQL 8.0 | 关系型数据存储 |
| **缓存** | Redis 6.0+ | 会话、热点数据、分布式锁 |
| **消息队列** | RabbitMQ / RocketMQ | 异步解耦（返利计算、通知） |
| **文件存储** | 阿里云 OSS / 腾讯云 COS | 图片、视频存储 |
| **部署** | Docker + K8s | 容器化部署 |

### 2.3 物理部署架构
- **开发环境 (Dev)**：本地开发，连接开发库。
- **测试环境 (Test)**：集成测试，连接测试库。
- **生产环境 (Prod)**：
    - 负载均衡：Nginx / SLB
    - 应用服务器：至少 2 节点集群
    - 数据库：主从复制，读写分离
    - 缓存：Redis 哨兵模式

---

## 3. 模块详细设计

### 3.1 用户模块 (User Module)
- **核心类**：`UserService`, `UserRepository`, `DistributorService`
- **关键逻辑**：
    - **注册登录**：使用 JWT 生成 Token，有效期 2 小时，Refresh Token 有效期 7 天。
    - **分销资格**：用户注册默认为普通用户，满足条件（如消费满额）或申请后升级为分销商 (`user_type` 变更)。
    - **关系绑定**：下单时检查 `distribution_relations` 表，若不存在则创建绑定关系。

### 3.2 商品模块 (Product Module)
- **核心类**：`ProductService`, `StockService`
- **关键逻辑**：
    - **库存扣减**：下单预扣库存（Redis Lua 脚本），支付成功后实扣，取消订单回滚。
    - **佣金配置**：商品表 `commission_rate` 字段存储百分比，支持单独设置固定金额 `commission_amount`。

### 3.3 订单模块 (Order Module)
- **核心类**：`OrderService`, `OrderStateMachine`
- **关键逻辑**：
    - **状态机**：
      ```java
      // 伪代码
      if (event == PAY_SUCCESS) state = WAIT_SHIP;
      if (event == SHIP) state = WAIT_RECEIVE;
      if (event == CONFIRM_RECEIVE) state = COMPLETED;
      ```
    - **超时取消**：使用延迟队列（RabbitMQ TTL 或 Redis Key 过期监听）处理 30 分钟未支付订单。

### 3.4 分销模块 (Distribution Module) - **核心**
- **核心类**：`CommissionService`, `RelationService`
- **关键逻辑**：
    - **关系链查找**：通过 `distributors` 表的 `parent_id` 递归查找上级，限制层级 depth <= 2。
    - **返利计算触发**：监听订单 `CONFIRM_RECEIVE` 事件。
    - **返利计算流程**：
      ```mermaid
      sequenceDiagram
          participant Order as 订单服务
          participant MQ as 消息队列
          participant Dist as 分销服务
          participant DB as 数据库
          
          Order->>MQ: 发送订单确认消息 (orderId)
          MQ->>Dist: 消费消息
          Dist->>DB: 查询订单及上级关系
          Dist->>Dist: 计算佣金 (金额 * 比例)
          Dist->>DB: 插入佣金记录 (状态：冻结)
          Dist->>DB: 更新分销商累计收益
      ```
    - **结算任务**：定时任务（Daily Job）扫描 `distribution_commissions` 表，将 `freeze_time + 7 天` 且无售后的记录状态改为 `可提现`。

### 3.5 财务模块 (Finance Module)
- **核心类**：`WalletService`, `WithdrawService`
- **关键逻辑**：
    - **事务一致性**：佣金入账、钱包余额更新、流水记录必须在同一事务中。
    - **提现审核**：商家后台审核通过后，调用支付接口企业付款，或标记为线下打款。

### 3.6 支付模块 (Payment Module)
- **核心类**：`PaymentStrategy`, `WechatPayService`, `AlipayService`
- **关键逻辑**：
    - **策略模式**：根据 `pay_channel` 选择具体的支付实现类。
    - **幂等性**：支付回调接口需保证幂等，防止重复入账。

---

## 4. 接口设计规范

### 4.1 通信协议
- **协议**：HTTPS
- **数据格式**：JSON
- **字符编码**：UTF-8

### 4.2 统一响应结构
```json
{
  "code": 200,          // 业务状态码
  "message": "success", // 提示信息
  "data": {},           // 业务数据
  "timestamp": 1697356800000
}
```

### 4.3 认证机制
- **Header**：`Authorization: Bearer <JWT_TOKEN>`
- **例外**：登录、注册、商品列表、商品详情接口无需认证。

### 4.4 关键接口示例
**创建订单**
- **URL**: `POST /api/orders`
- **Request**:
  ```json
  {
    "items": [{"product_id": 101, "sku_id": 202, "quantity": 1}],
    "address_id": 55,
    "remark": "请尽快发货"
  }
  ```
- **Logic**: 校验库存 -> 锁定库存 -> 创建订单 -> 绑定分销关系 -> 返回订单号。

**获取佣金明细**
- **URL**: `GET /api/distribution/commissions?page=1&size=10`
- **Response**:
  ```json
  {
    "list": [
      {
        "order_no": "ORD20231015001",
        "amount": 15.50,
        "status": 1, // 0:冻结，1:可提现
        "settle_time": "2023-10-22 10:00:00"
      }
    ],
    "total": 100
  }
  ```

---

## 5. 数据库设计概要

### 5.1 核心关系说明
- **用户 - 分销商**：1 对 1 (`users.id` = `distributors.user_id`)
- **分销商 - 分销商**：1 对多 自关联 (`distributors.parent_id` -> `distributors.id`)
- **订单 - 佣金**：1 对多 (`orders.id` -> `distribution_commissions.order_id`)，一个订单可能产生多级佣金。
- **用户 - 钱包**：1 对 1 (`users.id` = `wallets.user_id`)

### 5.2 关键索引策略
- `orders(user_id, created_at)`：用户订单列表查询优化。
- `distribution_commissions(distributor_id, status, settle_time)`：佣金结算任务查询优化。
- `products(status, created_at)`：商品列表查询优化。

### 5.3 数据一致性保障
- **本地事务**：单个服务内操作使用 `@Transactional`。
- **分布式事务**：涉及跨服务（如订单完成触发佣金），采用**最终一致性**方案（消息队列 + 重试机制）。
- **对账**：每日凌晨运行对账脚本，比对订单表与佣金表、钱包流水表。

---

## 6. 安全设计

### 6.1 认证与授权
- **密码存储**：使用 BCrypt 加密，加盐处理。
- **权限控制**：基于 RBAC 模型，管理员接口需校验 `@PreAuthorize("hasRole('ADMIN')")`。
- **接口防刷**：基于 IP 和用户 ID 进行限流（Redis RateLimiter），例如下单接口 10 次/分钟。

### 6.2 数据安全
- **敏感信息**：手机号、身份证、银行卡号在数据库中加密存储，日志中脱敏显示（如 138****0000）。
- **SQL 注入**：使用 MyBatis 参数绑定，禁止字符串拼接 SQL。
- **XSS 防护**：前端输入过滤，后端输出转义。

### 6.3 业务安全
- **佣金篡改**：佣金计算必须在后端完成，前端仅展示，不接受前端传入的佣金金额。
- **越权访问**：查询订单、佣金时，必须校验当前登录用户 ID 是否与数据归属 ID 一致。
- **合规控制**：代码中硬编码限制分销层级最大为 2 级，防止配置错误导致合规风险。

---

## 7. 异常处理与日志

### 7.1 全局异常处理
使用 `@RestControllerAdvice` 捕获异常，统一返回错误格式。
- **业务异常**：`BusinessException` (code: 4000-5999)
- **系统异常**：`SystemException` (code: 500)
- **认证异常**：`AuthException` (code: 401)

### 7.2 日志规范
- **工具**：SLF4J + Logback
- **级别**：
    - `ERROR`: 系统错误、异常堆栈
    - `WARN`: 业务警告（如库存不足、审核拒绝）
    - `INFO`: 关键业务操作（如下单、支付、提现）
    - `DEBUG`: 开发调试信息（生产环境关闭）
- **链路追踪**：集成 SkyWalking 或 Zipkin，生成 `TraceID` 贯穿整个请求链路。

---

## 8. 部署与运维

### 8.1 环境配置
使用 `application-dev.yml`, `application-prod.yml` 分离配置。
敏感配置（数据库密码、支付密钥）通过环境变量或配置中心（Nacos/Apollo）注入。

### 8.2 CI/CD 流程
1.  **代码提交**：Git Push 到 Master 分支。
2.  **自动化构建**：Jenkins/GitLab CI 触发 Maven 构建。
3.  **单元测试**：运行 JUnit 测试，覆盖率需 > 80%。
4.  **镜像打包**：构建 Docker 镜像并推送至仓库。
5.  **部署**：K8s 滚动更新 Pod。

### 8.3 监控告警
- **应用监控**：Spring Boot Admin 监控 JVM 状态、接口耗时。
- **业务监控**：监控订单量、支付成功率、佣金发放异常。
- **告警渠道**：钉钉/企业微信机器人、邮件、短信。

---

## 9. 开发计划与里程碑

| 阶段 | 时间周期 | 主要任务 | 交付物 |
|------|----------|----------|--------|
| **第一阶段** | Week 1-2 | 基础架构搭建、用户模块、商品模块 | 可运行的基础商城 |
| **第二阶段** | Week 3-4 | 订单模块、支付模块、物流对接 | 可完成购物流程 |
| **第三阶段** | Week 5-6 | 分销模块、佣金计算、钱包提现 | 分销核心功能完成 |
| **第四阶段** | Week 7 | 管理后台、数据统计、系统设置 | 完整管理功能 |
| **第五阶段** | Week 8 | 测试、修复、压力测试、部署 | 上线版本 |

---

## 10. 附录

### 10.1 代码规范
- 遵循阿里巴巴 Java 开发手册。
- 字段命名：驼峰命名法 (camelCase)。
- 常量定义：全大写下划线分隔 (UPPER_CASE)。
- 注释：公共方法必须有 Javadoc，复杂逻辑必须有行内注释。