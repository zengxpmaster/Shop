# 分销系统数据库设计文档

**版本号：** V1.0  
**日期：** 2026 年 03 月  
**状态：** 初稿

---

## 1. 数据库设计概述

### 1.1 设计原则
- 遵循第三范式（3NF）
- 统一使用InnoDB存储引擎
- 字符集：utf8mb4
- 所有表包含 `created_at` 和 `updated_at` 字段
- 主键统一使用 `bigint` 自增或雪花ID

### 1.2 命名规范
- 表名：小写字母+下划线，复数形式
- 字段名：小写字母+下划线
- 主键：`id`
- 外键：`表名_单数_id`
- 索引：`idx_字段名` / `uk_字段名`（唯一索引）

---

## 2. 数据库表设计

## 2.1 用户域

### 2.1.1 用户表 (users)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | INT | | NO | | 用户ID | PRIMARY KEY |
| username | VARCHAR | 24 | NO | | 用户名 | UNIQUE |
| password_hash | VARCHAR | 64 | NO | | 密码哈希 | |
| phone | VARCHAR | 20 | YES | | 手机号 | UNIQUE |
| email | VARCHAR | 100 | YES | | 邮箱 | UNIQUE |
| nickname | VARCHAR | 32 | YES | | 昵称 | |
| avatar | VARCHAR | 255 | YES | | 头像URL | |
| gender | TINYINT | | YES | 0 | 性别(0:未知,1:男,2:女) | |
| status | TINYINT | | NO | 1 | 状态(0:禁用,1:正常) | |
| user_type | TINYINT | | NO | 1 | 用户类型(0:管理员,1:普通,2:分销商) | |
| last_login_at | INT | | YES | 0 | 最后登录时间 | |
| last_login_ip | VARCHAR | 50 | YES | | 最后登录IP | |
| created_at | INT | | NO | 0 | 创建时间 | |
| updated_at | INT | | NO | 0 | 更新时间 | |

**索引：**
```sql
CREATE INDEX idx_phone ON users(phone);
CREATE INDEX idx_email ON users(email);
CREATE INDEX idx_status ON users(status);
CREATE INDEX idx_created_at ON users(created_at);
```

---

### 2.1.2 用户地址表 (user_addresses)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | INT | | NO | | 地址ID | PRIMARY KEY |
| user_id | INT | | NO | | 用户ID | FOREIGN KEY |
| contact_name | VARCHAR | 32 | NO | | 联系人姓名 | |
| contact_phone | VARCHAR | 11 | NO | | 联系电话 | |
| province | VARCHAR | 50 | NO | | 省份 | |
| city | VARCHAR | 50 | NO | | 城市 | |
| district | VARCHAR | 50 | NO | | 区县 | |
| address | VARCHAR | 255 | NO | | 详细地址 | |
| postal_code | VARCHAR | 12 | YES | | 邮政编码 | |
| is_default | TINYINT | | NO | 0 | 是否默认(0:否,1:是) | |
| created_at | INT | | NO | 0 | 创建时间 | |
| updated_at | INT | | NO | 0 | 更新时间 | |

**索引：**
```sql
CREATE INDEX idx_user_id ON user_addresses(user_id);
CREATE INDEX idx_is_default ON user_addresses(is_default);
```

**外键约束：**
```sql
ALTER TABLE user_addresses 
ADD CONSTRAINT fk_user_addresses_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
```

---

### 2.1.3 分销商信息表 (distributors)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | INT | | NO | | 分销商ID | PRIMARY KEY |
| user_id | INT | | NO | | 用户ID | FOREIGN KEY, UNIQUE |
| level_id | INT | | NO | | 分销等级ID |  |
| invite_code | VARCHAR | 20 | NO | | 邀请码 | UNIQUE |
| total_commission | DECIMAL | 10,2 | NO | 0.00 | 累计佣金 | |
| withdrawable_amount | DECIMAL | 10,2 | NO | 0.00 | 可提现金额 | |
| frozen_amount | DECIMAL | 10,2 | NO | 0.00 | 冻结金额 | |
| total_withdrawn | DECIMAL | 10,2 | NO | 0.00 | 已提现金额 | |
| status | TINYINT | | NO | 1 | 状态(0:禁用,1:正常) | |
| approved_at | INT | | YES | 0 | 审核通过时间 | |
| created_at | INT | | NO | 0 | 创建时间 | |
| updated_at | INT | | NO | 0 | 更新时间 | |

**索引：**
```sql
CREATE INDEX idx_user_id ON distributors(user_id);
CREATE INDEX idx_invite_code ON distributors(invite_code);
CREATE INDEX idx_parent_id ON distributors(parent_id);
CREATE INDEX idx_level ON distributors(level);
CREATE INDEX idx_status ON distributors(status);
```

**外键约束：**
```sql
ALTER TABLE distributors 
ADD CONSTRAINT fk_distributors_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE distributors 
ADD CONSTRAINT fk_distributors_parent 
FOREIGN KEY (parent_id) REFERENCES distributors(id) ON DELETE SET NULL;
```

---

### 2.1.4 分销等级表 (distributor_levels)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | INT | | NO | | 等级ID | PRIMARY KEY |
| name | VARCHAR | 50 | NO | | 等级名称 | |
| min_sales_amount | DECIMAL | 10,2 | NO | 0.00 | 最低销售额要求 | |
| commission_rate | DECIMAL | 5,2 | NO | 0.00 | 佣金比例(%) | |
| description | VARCHAR | 255 | YES | | 等级描述 | |
| status | TINYINT | | NO | 1 | 状态(0:禁用,1:启用) | |
| created_at | INT | | NO | 0 | 创建时间 | |
| updated_at | INT | | NO | 0 | 更新时间 | |

---

## 2.2 商品域

### 2.2.1 商品分类表 (product_categories)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | INT | | NO | | 分类ID | PRIMARY KEY |
| parent_id | INT | | YES | | 父分类ID | FOREIGN KEY |
| name | VARCHAR | 50 | NO | | 分类名称 | |
| sort_order | INT | | NO | 0 | 排序 | |
| icon | VARCHAR | 255 | YES | | 分类图标 | |
| status | TINYINT | | NO | 1 | 状态(0:禁用,1:启用) | |
| created_at | INT | | NO | 0 | 创建时间 | |
| updated_at | INT | | NO | 0 | 更新时间 | |

**索引：**
```sql
CREATE INDEX idx_parent_id ON product_categories(parent_id);
CREATE INDEX idx_sort_order ON product_categories(sort_order);
```

---

### 2.2.2 商品表 (products)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | INT | | NO | | 商品ID | PRIMARY KEY |
| category_id | INT | | NO | | 分类ID | FOREIGN KEY |
| name | VARCHAR | 200 | NO | | 商品名称 | |
| description | TEXT | | YES | | 商品描述 | |
| detail | LONGTEXT | | YES | | 商品详情HTML | |
| cover | VARCHAR | 255 | YES | | 主图 | |
| price | DECIMAL | 10,2 | NO | 0.00 | 售价 | |
| market_price | DECIMAL | 10,2 | YES | | 市场价 | |
| cost_price | DECIMAL | 10,2 | YES | | 成本价 | |
| stock | INT | | NO | 0 | 库存 | |
| sales_count | INT | | NO | 0 | 销量 | |
| commission_amount | DECIMAL | 10,2 | NO | 0.00 | 分销佣金比例/固定金额 | |
| commission_type | TINYINT | 1 | YES | | 佣金类型(1:按比例,2:按固定金额) | |
| status | TINYINT | | NO | 0 | 状态(0:下架,1:上架) | |
| sort_order | INT | | NO | 0 | 排序 | |
| created_at | INT | | NO | 0 | 创建时间 | |
| updated_at | INT | | NO | 0 | 更新时间 | |

**索引：**
```sql
CREATE INDEX idx_category_id ON products(category_id);
CREATE INDEX idx_product_name ON products(product_name);
CREATE INDEX idx_status ON products(status);
CREATE INDEX idx_sort_order ON products(sort_order);
CREATE INDEX idx_created_at ON products(created_at);
```

**外键约束：**
```sql
ALTER TABLE products 
ADD CONSTRAINT fk_products_category 
FOREIGN KEY (category_id) REFERENCES product_categories(id) ON DELETE RESTRICT;
```

---

### 2.2.3 商品SKU表 (product_skus)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | | NO | | SKU ID | PRIMARY KEY |
| product_id | BIGINT | | NO | | 商品ID | FOREIGN KEY |
| sku_name | VARCHAR | 72 | YES | | SKU名称 | |
| sku_code | VARCHAR | 32 | YES | | SKU编码 | |
| specs | TEXT | | YES | | 规格参数(JSON) | |
| price | DECIMAL | 10,2 | YES | | SKU价格 | |
| stock | INT | | NO | 0 | SKU库存 | |
| status | TINYINT | | NO | 1 | 状态(0:禁用,1:启用) | |
| created_at | INT | | NO | 0 | 创建时间 | |
| updated_at | INT | | NO | 0 | 更新时间 | |

**索引：**
```sql
CREATE INDEX idx_product_id ON product_skus(product_id);
```

**外键约束：**
```sql
ALTER TABLE product_skus 
ADD CONSTRAINT fk_product_skus_product 
FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;
```

---

### 2.2.4 商品图片表 (product_images)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | INT | | NO | | 图片ID | PRIMARY KEY |
| product_id | INT | | NO | | 商品ID | FOREIGN KEY |
| image_path | VARCHAR | 255 | NO | | 图片路径 | |
| sort_order | INT | | NO | 0 | 排序 | |
| created_at | INT | | NO | 0 | 创建时间 | |

**索引：**
```sql
CREATE INDEX idx_product_id ON product_images(product_id);
```

**外键约束：**
```sql
ALTER TABLE product_images 
ADD CONSTRAINT fk_product_images_product 
FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;
```

---

## 2.3 订单域

### 2.3.1 订单表 (orders)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | INT | | NO | | 订单ID | PRIMARY KEY |
| order_no | VARCHAR | 24 | NO | | 订单编号 | UNIQUE |
| user_id | INT | | NO | | 用户ID | FOREIGN KEY |
| distributor_id | INT | | YES | | 分销商ID | FOREIGN KEY |
| total_amount | DECIMAL | 10,2 | NO | 0.00 | 订单总额 | |
| pay_amount | DECIMAL | 10,2 | NO | 0.00 | 实付金额 | |
| freight_amount | DECIMAL | 10,2 | NO | 0.00 | 运费 | |
| discount_amount | DECIMAL | 10,2 | NO | 0.00 | 优惠金额 | |
| commission_amount | DECIMAL | 10,2 | NO | 0.00 | 佣金总额 | |
| contact_name | VARCHAR | 50 | NO | | 收货人姓名 | |
| contact_phone | VARCHAR | 20 | NO | | 收货人电话 | |
| province | VARCHAR | 50 | NO | | 省份 | |
| city | VARCHAR | 50 | NO | | 城市 | |
| district | VARCHAR | 50 | NO | | 区县 | |
| address | VARCHAR | 255 | NO | | 详细地址 | |
| remark | VARCHAR | 500 | YES | | 订单备注 | |
| status | TINYINT | | NO | 0 | 订单状态(0:待支付,1:待发货,2:待收货,3:已完成,4:已取消) | |
| pay_status | TINYINT | | NO | 0 | 支付状态(0:未支付,1:已支付) | |
| paid_at | INT | | YES | | 支付时间 | |
| pay_type | TINYINT | | YES | | 支付方式(1:微信,2:支付宝) | |
| shiped_at | INT | | YES | | 发货时间 | |
| delivery_sn | VARCHAR | 32 | YES | | 物流单号 | |
| delivery_company | VARCHAR | 50 | YES | | 物流公司 | |
| received_at | INT | | YES | | 收货时间 | |
| confirmed_at | INT | | YES | | 确认收货时间 | |
| canceled_at | INT | | YES | | 取消时间 | |
| cancel_reason | VARCHAR | 255 | YES | | 取消原因 | |
| created_at | INT | | NO | 0 | 创建时间 | |
| updated_at | INT | | NO | 0 | 更新时间 | |

**索引：**
```sql
CREATE INDEX idx_order_no ON orders(order_no);
CREATE INDEX idx_user_id ON orders(user_id);
CREATE INDEX idx_distributor_id ON orders(distributor_id);
CREATE INDEX idx_order_status ON orders(order_status);
CREATE INDEX idx_pay_status ON orders(pay_status);
CREATE INDEX idx_created_at ON orders(created_at);
```

**外键约束：**
```sql
ALTER TABLE orders 
ADD CONSTRAINT fk_orders_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE RESTRICT;

ALTER TABLE orders 
ADD CONSTRAINT fk_orders_distributor 
FOREIGN KEY (distributor_id) REFERENCES distributors(id) ON DELETE SET NULL;
```

---

### 2.3.2 订单商品明细表 (order_products)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | INT | | NO | | 明细ID | PRIMARY KEY |
| order_id | INT | | NO | | 订单ID | FOREIGN KEY |
| product_id | INT | | NO | | 商品ID | FOREIGN KEY |
| product_sku_id | INT | | YES | | SKU ID | FOREIGN KEY |
| product_name | VARCHAR | 200 | NO | | 商品名称 | |
| product_cover | VARCHAR | 100 | YES | | 商品图片 | |
| product_price | DECIMAL | 10,2 | NO | 0.00 | 商品单价 | |
| quantity | INT | | NO | 1 | 购买数量 | |
| total_price | DECIMAL | 10,2 | NO | 0.00 | 商品总价 | |
| commission_amount | DECIMAL | 5,2 | NO | 0.00 | 分销佣金比例/固定金额 | |
| commission_type | TINYINT | 1 | YES | | 佣金类型(1:按比例,2:按固定金额) | |
| specs | TEXT | | YES | | 规格参数 | |
| created_at | INT | | NO | 0 | 创建时间 | |

**索引：**
```sql
CREATE INDEX idx_order_id ON order_items(order_id);
CREATE INDEX idx_product_id ON order_items(product_id);
```

**外键约束：**
```sql
ALTER TABLE order_items 
ADD CONSTRAINT fk_order_items_order 
FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;

ALTER TABLE order_items 
ADD CONSTRAINT fk_order_items_product 
FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE RESTRICT;

ALTER TABLE order_items 
ADD CONSTRAINT fk_order_items_sku 
FOREIGN KEY (product_sku_id) REFERENCES product_skus(id) ON DELETE SET NULL;
```

---

### 2.3.3 订单日志表 (order_logs)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | INT | | NO | | 日志ID | PRIMARY KEY |
| order_id | INT | | NO | | 订单ID | FOREIGN KEY |
| user_id | INT | | YES | | 操作人ID | |
| order_status | TINYINT | | YES | | 订单状态 | |
| action | VARCHAR | 50 | NO | | 操作动作 | |
| remark | VARCHAR | 500 | YES | | 备注 | |
| created_at | INT | | NO | 0 | 创建时间 | |

**索引：**
```sql
CREATE INDEX idx_order_id ON order_logs(order_id);
CREATE INDEX idx_created_at ON order_logs(created_at);
```

**外键约束：**
```sql
ALTER TABLE order_logs 
ADD CONSTRAINT fk_order_logs_order 
FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;
```

---

### 2.3.4 ~~退款表 (refunds)~~

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | | NO | | 退款ID | PRIMARY KEY |
| refund_no | VARCHAR | 50 | NO | | 退款编号 | UNIQUE |
| order_id | BIGINT | | NO | | 订单ID | FOREIGN KEY |
| order_item_id | BIGINT | | YES | | 订单明细ID | FOREIGN KEY |
| user_id | BIGINT | | NO | | 用户ID | FOREIGN KEY |
| refund_type | TINYINT | | NO | 1 | 退款类型(1:仅退款,2:退货退款) | |
| refund_amount | DECIMAL | 10,2 | NO | 0.00 | 退款金额 | |
| refund_reason | VARCHAR | 500 | NO | | 退款原因 | |
| refund_desc | VARCHAR | 1000 | YES | | 退款说明 | |
| refund_images | JSON | | YES | | 凭证图片 | |
| refund_status | TINYINT | | NO | 0 | 退款状态(0:待审核,1:已通过,2:已拒绝,3:已退款) | |
| audit_time | DATETIME | | YES | | 审核时间 | |
| audit_remark | VARCHAR | 500 | YES | | 审核备注 | |
| refund_time | DATETIME | | YES | | 退款时间 | |
| created_at | DATETIME | | NO | CURRENT_TIMESTAMP | 创建时间 | |
| updated_at | DATETIME | | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 | |

**索引：**
```sql
CREATE INDEX idx_refund_no ON refunds(refund_no);
CREATE INDEX idx_order_id ON refunds(order_id);
CREATE INDEX idx_user_id ON refunds(user_id);
CREATE INDEX idx_refund_status ON refunds(refund_status);
```

**外键约束：**
```sql
ALTER TABLE refunds 
ADD CONSTRAINT fk_refunds_order 
FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;

ALTER TABLE refunds 
ADD CONSTRAINT fk_refunds_item 
FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE SET NULL;

ALTER TABLE refunds 
ADD CONSTRAINT fk_refunds_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
```

---

## 2.4 分销域

### 2.4.1 分销关系表 (distribution_relations)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | | NO | | 关系ID | PRIMARY KEY |
| user_id | BIGINT | | NO | | 用户ID | FOREIGN KEY |
| distributor_id | BIGINT | | NO | | 分销商ID | FOREIGN KEY |
| level | TINYINT | | NO | 1 | 关系层级(1:一级,2:二级) | |
| bind_type | TINYINT | | NO | 1 | 绑定类型(1:点击,2:下单) | |
| bind_time | DATETIME | | NO | | 绑定时间 | |
| expire_time | DATETIME | | YES | | 过期时间 | |
| status | TINYINT | | NO | 1 | 状态(0:失效,1:有效) | |
| created_at | DATETIME | | NO | CURRENT_TIMESTAMP | 创建时间 | |

**索引：**
```sql
CREATE UNIQUE INDEX uk_user_distributor_level ON distribution_relations(user_id, distributor_id, level);
CREATE INDEX idx_user_id ON distribution_relations(user_id);
CREATE INDEX idx_distributor_id ON distribution_relations(distributor_id);
```

**外键约束：**
```sql
ALTER TABLE distribution_relations 
ADD CONSTRAINT fk_dist_rel_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE distribution_relations 
ADD CONSTRAINT fk_dist_rel_distributor 
FOREIGN KEY (distributor_id) REFERENCES distributors(id) ON DELETE CASCADE;
```

---

### 2.4.2 佣金记录表 (distribution_commissions)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | | NO | | 佣金ID | PRIMARY KEY |
| commission_no | VARCHAR | 50 | NO | | 佣金编号 | UNIQUE |
| distributor_id | BIGINT | | NO | | 分销商ID | FOREIGN KEY |
| order_id | BIGINT | | NO | | 订单ID | FOREIGN KEY |
| order_item_id | BIGINT | | YES | | 订单明细ID | FOREIGN KEY |
| from_user_id | BIGINT | | NO | | 来源用户ID | FOREIGN KEY |
| level | TINYINT | | NO | 1 | 分销层级 | |
| commission_amount | DECIMAL | 10,2 | NO | 0.00 | 佣金金额 | |
| commission_rate | DECIMAL | 5,2 | NO | 0.00 | 佣金比例 | |
| order_amount | DECIMAL | 10,2 | NO | 0.00 | 订单金额 | |
| status | TINYINT | | NO | 0 | 状态(0:待结算,1:已结算,2:已失效) | |
| settle_time | DATETIME | | YES | | 结算时间 | |
| freeze_time | DATETIME | | YES | | 冻结时间 | |
| unfreeze_time | DATETIME | | YES | | 解冻时间 | |
| remark | VARCHAR | 500 | YES | | 备注 | |
| created_at | DATETIME | | NO | CURRENT_TIMESTAMP | 创建时间 | |
| updated_at | DATETIME | | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 | |

**索引：**
```sql
CREATE INDEX idx_commission_no ON distribution_commissions(commission_no);
CREATE INDEX idx_distributor_id ON distribution_commissions(distributor_id);
CREATE INDEX idx_order_id ON distribution_commissions(order_id);
CREATE INDEX idx_from_user_id ON distribution_commissions(from_user_id);
CREATE INDEX idx_status ON distribution_commissions(status);
CREATE INDEX idx_settle_time ON distribution_commissions(settle_time);
```

**外键约束：**
```sql
ALTER TABLE distribution_commissions 
ADD CONSTRAINT fk_dist_comm_distributor 
FOREIGN KEY (distributor_id) REFERENCES distributors(id) ON DELETE CASCADE;

ALTER TABLE distribution_commissions 
ADD CONSTRAINT fk_dist_comm_order 
FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;

ALTER TABLE distribution_commissions 
ADD CONSTRAINT fk_dist_comm_item 
FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE SET NULL;

ALTER TABLE distribution_commissions 
ADD CONSTRAINT fk_dist_comm_user 
FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE;
```

---

### 2.4.3 分销等级表 (distribution_levels)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | | NO | | 等级ID | PRIMARY KEY |
| level_name | VARCHAR | 50 | NO | | 等级名称 | |
| level_value | TINYINT | | NO | | 等级值 | UNIQUE |
| commission_rate_1 | DECIMAL | 5,2 | NO | 0.00 | 一级佣金比例(%) | |
| commission_rate_2 | DECIMAL | 5,2 | YES | 0.00 | 二级佣金比例(%) | |
| min_sales_amount | DECIMAL | 10,2 | NO | 0.00 | 最低销售额 | |
| min_team_size | INT | | NO | 0 | 最低团队人数 | |
| upgrade_condition | JSON | | YES | | 升级条件(JSON) | |
| description | VARCHAR | 500 | YES | | 等级描述 | |
| status | TINYINT | | NO | 1 | 状态(0:禁用,1:启用) | |
| created_at | DATETIME | | NO | CURRENT_TIMESTAMP | 创建时间 | |
| updated_at | DATETIME | | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 | |

---

### 2.4.4 邀请码表 (invite_codes)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | | NO | | 邀请码ID | PRIMARY KEY |
| distributor_id | BIGINT | | NO | | 分销商ID | FOREIGN KEY |
| invite_code | VARCHAR | 20 | NO | | 邀请码 | UNIQUE |
| code_type | TINYINT | | NO | 1 | 类型(1:通用,2:商品) | |
| product_id | BIGINT | | YES | | 商品ID | FOREIGN KEY |
| max_use_count | INT | | YES | | 最大使用次数 | |
| used_count | INT | | NO | 0 | 已使用次数 | |
| expire_time | DATETIME | | YES | | 过期时间 | |
| status | TINYINT | | NO | 1 | 状态(0:禁用,1:启用) | |
| created_at | DATETIME | | NO | CURRENT_TIMESTAMP | 创建时间 | |

**索引：**
```sql
CREATE INDEX idx_distributor_id ON invite_codes(distributor_id);
CREATE INDEX idx_product_id ON invite_codes(product_id);
```

**外键约束：**
```sql
ALTER TABLE invite_codes 
ADD CONSTRAINT fk_invite_codes_distributor 
FOREIGN KEY (distributor_id) REFERENCES distributors(id) ON DELETE CASCADE;

ALTER TABLE invite_codes 
ADD CONSTRAINT fk_invite_codes_product 
FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;
```

---

## 2.5 财务域

### 2.5.1 钱包表 (wallets)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | | NO | | 钱包ID | PRIMARY KEY |
| user_id | BIGINT | | NO | | 用户ID | FOREIGN KEY, UNIQUE |
| balance | DECIMAL | 10,2 | NO | 0.00 | 可用余额 | |
| frozen_amount | DECIMAL | 10,2 | NO | 0.00 | 冻结金额 | |
| total_income | DECIMAL | 10,2 | NO | 0.00 | 累计收入 | |
| total_withdrawn | DECIMAL | 10,2 | NO | 0.00 | 累计提现 | |
| status | TINYINT | | NO | 1 | 状态(0:冻结,1:正常) | |
| created_at | DATETIME | | NO | CURRENT_TIMESTAMP | 创建时间 | |
| updated_at | DATETIME | | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 | |

**索引：**
```sql
CREATE INDEX idx_user_id ON wallets(user_id);
```

**外键约束：**
```sql
ALTER TABLE wallets 
ADD CONSTRAINT fk_wallets_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
```

---

### 2.5.2 钱包流水表 (wallet_transactions)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | | NO | | 流水ID | PRIMARY KEY |
| transaction_no | VARCHAR | 50 | NO | | 流水编号 | UNIQUE |
| wallet_id | BIGINT | | NO | | 钱包ID | FOREIGN KEY |
| user_id | BIGINT | | NO | | 用户ID | FOREIGN KEY |
| type | TINYINT | | NO | | 类型(1:收入,2:支出) | |
| business_type | TINYINT | | NO | | 业务类型(1:佣金,2:提现,3:退款) | |
| amount | DECIMAL | 10,2 | NO | 0.00 | 金额 | |
| balance_before | DECIMAL | 10,2 | NO | 0.00 | 变更前余额 | |
| balance_after | DECIMAL | 10,2 | NO | 0.00 | 变更后余额 | |
| related_type | VARCHAR | 50 | YES | | 关联类型(order/commission/withdraw) | |
| related_id | BIGINT | | YES | | 关联ID | |
| remark | VARCHAR | 500 | YES | | 备注 | |
| created_at | DATETIME | | NO | CURRENT_TIMESTAMP | 创建时间 | |

**索引：**
```sql
CREATE INDEX idx_transaction_no ON wallet_transactions(transaction_no);
CREATE INDEX idx_wallet_id ON wallet_transactions(wallet_id);
CREATE INDEX idx_user_id ON wallet_transactions(user_id);
CREATE INDEX idx_type ON wallet_transactions(type);
CREATE INDEX idx_business_type ON wallet_transactions(business_type);
CREATE INDEX idx_created_at ON wallet_transactions(created_at);
```

**外键约束：**
```sql
ALTER TABLE wallet_transactions 
ADD CONSTRAINT fk_wallet_trans_wallet 
FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE;

ALTER TABLE wallet_transactions 
ADD CONSTRAINT fk_wallet_trans_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
```

---

### 2.5.3 提现申请表 (withdrawals)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | | NO | | 提现ID | PRIMARY KEY |
| withdrawal_no | VARCHAR | 50 | NO | | 提现编号 | UNIQUE |
| user_id | BIGINT | | NO | | 用户ID | FOREIGN KEY |
| wallet_id | BIGINT | | NO | | 钱包ID | FOREIGN KEY |
| amount | DECIMAL | 10,2 | NO | 0.00 | 提现金额 | |
| fee_amount | DECIMAL | 10,2 | NO | 0.00 | 手续费 | |
| actual_amount | DECIMAL | 10,2 | NO | 0.00 | 实际到账金额 | |
| withdraw_type | TINYINT | | NO | 1 | 提现方式(1:微信,2:支付宝,3:银行卡) | |
| account_name | VARCHAR | 50 | NO | | 账户姓名 | |
| account_number | VARCHAR | 100 | NO | | 账号 | |
| bank_name | VARCHAR | 100 | YES | | 银行名称 | |
| status | TINYINT | | NO | 0 | 状态(0:待审核,1:审核通过,2:审核拒绝,3:打款中,4:已完成) | |
| audit_time | DATETIME | | YES | | 审核时间 | |
| audit_remark | VARCHAR | 500 | YES | | 审核备注 | |
| payer_id | BIGINT | | YES | | 打款人ID | |
| pay_time | DATETIME | | YES | | 打款时间 | |
| pay_sn | VARCHAR | 100 | YES | | 支付流水号 | |
| reject_reason | VARCHAR | 500 | YES | | 拒绝原因 | |
| created_at | DATETIME | | NO | CURRENT_TIMESTAMP | 创建时间 | |
| updated_at | DATETIME | | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 | |

**索引：**
```sql
CREATE INDEX idx_withdrawal_no ON withdrawals(withdrawal_no);
CREATE INDEX idx_user_id ON withdrawals(user_id);
CREATE INDEX idx_wallet_id ON withdrawals(wallet_id);
CREATE INDEX idx_status ON withdrawals(status);
CREATE INDEX idx_created_at ON withdrawals(created_at);
```

**外键约束：**
```sql
ALTER TABLE withdrawals 
ADD CONSTRAINT fk_withdrawals_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE withdrawals 
ADD CONSTRAINT fk_withdrawals_wallet 
FOREIGN KEY (wallet_id) REFERENCES wallets(id) ON DELETE CASCADE;
```

---

## 2.6 支付域

### 2.6.1 支付订单表 (payment_orders)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | | NO | | 支付ID | PRIMARY KEY |
| payment_no | VARCHAR | 50 | NO | | 支付编号 | UNIQUE |
| order_id | BIGINT | | NO | | 订单ID | FOREIGN KEY |
| user_id | BIGINT | | NO | | 用户ID | FOREIGN KEY |
| pay_channel | TINYINT | | NO | | 支付渠道(1:微信,2:支付宝) | |
| pay_type | TINYINT | | NO | | 支付类型(1:公众号,2:小程序,3:APP) | |
| trade_no | VARCHAR | 100 | YES | | 第三方交易号 | |
| amount | DECIMAL | 10,2 | NO | 0.00 | 支付金额 | |
| status | TINYINT | | NO | 0 | 状态(0:待支付,1:支付成功,2:支付失败,3:已退款) | |
| pay_time | DATETIME | | YES | | 支付时间 | |
| expire_time | DATETIME | | YES | | 过期时间 | |
| notify_data | JSON | | YES | | 回调数据 | |
| created_at | DATETIME | | NO | CURRENT_TIMESTAMP | 创建时间 | |
| updated_at | DATETIME | | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 | |

**索引：**
```sql
CREATE INDEX idx_payment_no ON payment_orders(payment_no);
CREATE INDEX idx_order_id ON payment_orders(order_id);
CREATE INDEX idx_user_id ON payment_orders(user_id);
CREATE INDEX idx_trade_no ON payment_orders(trade_no);
CREATE INDEX idx_status ON payment_orders(status);
```

**外键约束：**
```sql
ALTER TABLE payment_orders 
ADD CONSTRAINT fk_payment_orders_order 
FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE;

ALTER TABLE payment_orders 
ADD CONSTRAINT fk_payment_orders_user 
FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;
```

---

### 2.6.2 支付渠道表 (payment_channels)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | | NO | | 渠道ID | PRIMARY KEY |
| channel_name | VARCHAR | 50 | NO | | 渠道名称 | |
| channel_code | VARCHAR | 20 | NO | | 渠道编码 | UNIQUE |
| app_id | VARCHAR | 100 | YES | | AppID | |
| app_secret | VARCHAR | 255 | YES | | AppSecret | |
| mch_id | VARCHAR | 50 | YES | | 商户号 | |
| api_key | VARCHAR | 255 | YES | | API密钥 | |
| notify_url | VARCHAR | 255 | YES | | 回调URL | |
| config | JSON | | YES | | 其他配置 | |
| status | TINYINT | | NO | 1 | 状态(0:禁用,1:启用) | |
| created_at | DATETIME | | NO | CURRENT_TIMESTAMP | 创建时间 | |
| updated_at | DATETIME | | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 | |

---

## 2.7 系统域

### 2.7.1 系统配置表 (system_configs)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | | NO | | 配置ID | PRIMARY KEY |
| config_key | VARCHAR | 100 | NO | | 配置键 | UNIQUE |
| config_value | TEXT | | YES | | 配置值 | |
| config_type | VARCHAR | 50 | YES | | 配置类型 | |
| description | VARCHAR | 500 | YES | | 配置描述 | |
| status | TINYINT | | NO | 1 | 状态(0:禁用,1:启用) | |
| created_at | DATETIME | | NO | CURRENT_TIMESTAMP | 创建时间 | |
| updated_at | DATETIME | | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 | |

**索引：**
```sql
CREATE INDEX idx_config_key ON system_configs(config_key);
CREATE INDEX idx_config_type ON system_configs(config_type);
```

---

### 2.7.2 管理员表 (admin_users)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | | NO | | 管理员ID | PRIMARY KEY |
| username | VARCHAR | 50 | NO | | 用户名 | UNIQUE |
| password_hash | VARCHAR | 255 | NO | | 密码哈希 | |
| real_name | VARCHAR | 50 | YES | | 真实姓名 | |
| phone | VARCHAR | 20 | YES | | 手机号 | |
| email | VARCHAR | 100 | YES | | 邮箱 | |
| avatar | VARCHAR | 255 | YES | | 头像 | |
| role_id | BIGINT | | YES | | 角色ID | |
| status | TINYINT | | NO | 1 | 状态(0:禁用,1:启用) | |
| last_login_at | DATETIME | | YES | | 最后登录时间 | |
| last_login_ip | VARCHAR | 50 | YES | | 最后登录IP | |
| created_at | DATETIME | | NO | CURRENT_TIMESTAMP | 创建时间 | |
| updated_at | DATETIME | | NO | CURRENT_TIMESTAMP ON UPDATE | 更新时间 | |

---

### 2.7.3 操作日志表 (operation_logs)

| 字段名 | 数据类型 | 长度 | 可空 | 默认值 | 说明 | 约束 |
|--------|----------|------|------|--------|------|------|
| id | BIGINT | | NO | | 日志ID | PRIMARY KEY |
| admin_id | BIGINT | | YES | | 管理员ID | |
| module | VARCHAR | 50 | NO | | 模块 | |
| action | VARCHAR | 50 | NO | | 操作 | |
| method | VARCHAR | 10 | NO | | 请求方法 | |
| url | VARCHAR | 255 | NO | | 请求URL | |
| params | JSON | | YES | | 请求参数 | |
| ip | VARCHAR | 50 | YES | | IP地址 | |
| user_agent | VARCHAR | 500 | YES | | User-Agent | |
| result | TINYINT | | YES | | 结果(0:失败,1:成功) | |
| error_msg | VARCHAR | 1000 | YES | | 错误信息 | |
| duration | INT | | YES | | 耗时(ms) | |
| created_at | DATETIME | | NO | CURRENT_TIMESTAMP | 创建时间 | |

**索引：**
```sql
CREATE INDEX idx_admin_id ON operation_logs(admin_id);
CREATE INDEX idx_module ON operation_logs(module);
CREATE INDEX idx_action ON operation_logs(action);
CREATE INDEX idx_created_at ON operation_logs(created_at);
```

---

## 3. 数据库ER图

```mermaid
erDiagram
    USERS ||--o{ USER_ADDRESSES : has
    USERS ||--o| DISTRIBUTORS : becomes
    USERS ||--o{ ORDERS : places
    USERS ||--o| WALLETS : has
    USERS ||--o{ WALLET_TRANSACTIONS : generates
    USERS ||--o{ REFUNDS : requests
    USERS ||--o{ WITHDRAWALS : applies
    
    DISTRIBUTORS ||--o{ DISTRIBUTORS : refers
    DISTRIBUTORS ||--o{ DISTRIBUTION_RELATIONS : manages
    DISTRIBUTORS ||--o{ DISTRIBUTION_COMMISSIONS : earns
    DISTRIBUTORS ||--o{ INVITE_CODES : generates
    
    PRODUCT_CATEGORIES ||--o{ PRODUCTS : contains
    PRODUCTS ||--o{ PRODUCT_SKUS : has
    PRODUCTS ||--o{ PRODUCT_IMAGES : has
    PRODUCTS ||--o{ ORDER_ITEMS : includes
    
    ORDERS ||--o{ ORDER_ITEMS : contains
    ORDERS ||--o{ ORDER_LOGS : logs
    ORDERS ||--o{ REFUNDS : refunds
    ORDERS ||--o| PAYMENT_ORDERS : pays
    
    DISTRIBUTION_COMMISSIONS }o--|| ORDERS : from
    DISTRIBUTION_COMMISSIONS }o--|| USERS : benefits
    
    WALLETS ||--o{ WALLET_TRANSACTIONS : records
    WALLETS ||--o{ WITHDRAWALS : withdraws
    
    ADMIN_USERS ||--o{ OPERATION_LOGS : operates

    USERS {
        bigint id PK
        varchar username UK
        varchar phone UK
        varchar email UK
        tinyint user_type
        datetime created_at
    }
    
    USER_ADDRESSES {
        bigint id PK
        bigint user_id FK
        varchar contact_name
        varchar contact_phone
        tinyint is_default
    }
    
    DISTRIBUTORS {
        bigint id PK
        bigint user_id FK UK
        varchar invite_code UK
        bigint parent_id FK
        decimal total_commission
        decimal withdrawable_amount
    }
    
    PRODUCT_CATEGORIES {
        bigint id PK
        bigint parent_id FK
        varchar category_name
        int sort_order
    }
    
    PRODUCTS {
        bigint id PK
        bigint category_id FK
        varchar product_name
        varchar product_sn UK
        decimal price
        int stock
        decimal commission_rate
        tinyint status
    }
    
    PRODUCT_SKUS {
        bigint id PK
        bigint product_id FK
        varchar sku_code
        json specs
        int stock
    }
    
    ORDERS {
        bigint id PK
        varchar order_no UK
        bigint user_id FK
        bigint distributor_id FK
        decimal total_amount
        decimal pay_amount
        tinyint order_status
        tinyint pay_status
        datetime created_at
    }
    
    ORDER_ITEMS {
        bigint id PK
        bigint order_id FK
        bigint product_id FK
        int quantity
        decimal product_price
        decimal commission_amount
    }
    
    DISTRIBUTION_RELATIONS {
        bigint id PK
        bigint user_id FK
        bigint distributor_id FK
        tinyint level
        datetime bind_time
    }
    
    DISTRIBUTION_COMMISSIONS {
        bigint id PK
        varchar commission_no UK
        bigint distributor_id FK
        bigint order_id FK
        bigint from_user_id FK
        decimal commission_amount
        tinyint status
        datetime settle_time
    }
    
    WALLETS {
        bigint id PK
        bigint user_id FK UK
        decimal balance
        decimal frozen_amount
    }
    
    WALLET_TRANSACTIONS {
        bigint id PK
        varchar transaction_no UK
        bigint wallet_id FK
        bigint user_id FK
        tinyint type
        decimal amount
        datetime created_at
    }
    
    WITHDRAWALS {
        bigint id PK
        varchar withdrawal_no UK
        bigint user_id FK
        bigint wallet_id FK
        decimal amount
        tinyint status
        datetime created_at
    }
    
    REFUNDS {
        bigint id PK
        varchar refund_no UK
        bigint order_id FK
        bigint user_id FK
        decimal refund_amount
        tinyint refund_status
    }
    
    PAYMENT_ORDERS {
        bigint id PK
        varchar payment_no UK
        bigint order_id FK
        bigint user_id FK
        tinyint pay_channel
        decimal amount
        tinyint status
    }
    
    INVITE_CODES {
        bigint id PK
        bigint distributor_id FK
        varchar invite_code UK
        bigint product_id FK
        int used_count
    }
    
    ADMIN_USERS {
        bigint id PK
        varchar username UK
        varchar phone
        bigint role_id
    }
    
    OPERATION_LOGS {
        bigint id PK
        bigint admin_id FK
        varchar module
        varchar action
        datetime created_at
    }
    
    SYSTEM_CONFIGS {
        bigint id PK
        varchar config_key UK
        text config_value
        varchar config_type
    }
```

---

## 4. 数据库初始化脚本

### 4.1 创建数据库

```sql
CREATE DATABASE IF NOT EXISTS distribution_system 
DEFAULT CHARACTER SET utf8mb4 
DEFAULT COLLATE utf8mb4_unicode_ci;

USE distribution_system;
```

### 4.2 基础数据初始化

```sql
-- 插入系统配置
INSERT INTO system_configs (config_key, config_value, config_type, description) VALUES
('distribution_level', '2', 'distribution', '分销层级'),
('commission_freeze_days', '7', 'distribution', '佣金冻结天数'),
('auto_confirm_days', '7', 'order', '自动确认收货天数'),
('min_withdraw_amount', '10.00', 'finance', '最小提现金额'),
('withdraw_fee_rate', '0.00', 'finance', '提现手续费率');

-- 插入分销等级
INSERT INTO distribution_levels (level_name, level_value, commission_rate_1, commission_rate_2, min_sales_amount, min_team_size) VALUES
('普通会员', 1, 10.00, 0.00, 0.00, 0),
('金牌分销商', 2, 15.00, 5.00, 1000.00, 10),
('钻石分销商', 3, 20.00, 10.00, 5000.00, 50);

-- 插入支付渠道
INSERT INTO payment_channels (channel_name, channel_code, status) VALUES
('微信支付', 'wechat', 1),
('支付宝', 'alipay', 1);
```

---

## 5. 性能优化建议

### 5.1 索引优化
- 所有外键字段建立索引
- 查询频繁的字段建立组合索引
- 定期分析表，优化索引

### 5.2 分表策略
- `orders` 表：按月份分表（orders_202310, orders_202311...）
- `order_logs` 表：按月份分表
- `wallet_transactions` 表：按用户ID哈希分表

### 5.3 读写分离
- 主库：写操作
- 从库：读操作（商品列表、订单查询等）

### 5.4 缓存策略
- Redis缓存：商品信息、用户信息、系统配置
- 缓存过期时间：商品30分钟，用户信息10分钟

---

## 6. 数据安全

### 6.1 敏感字段加密
- 用户密码：bcrypt加密
- 支付密钥：AES加密存储
- 身份证号、银行卡号：脱敏存储

### 6.2 数据备份
- 每日全量备份
- Binlog实时备份
- 备份保留30天

### 6.3 权限控制
- 数据库账号最小权限原则
- 禁止远程root登录
- 应用账号仅授予必要权限