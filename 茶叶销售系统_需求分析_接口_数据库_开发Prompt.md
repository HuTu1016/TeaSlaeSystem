# 茶叶销售系统（Spring Boot + Vue）从 0 到 1 需求/接口/数据库/开发 Prompt

> 依据《基于 Spring Boot 的茶叶销售系统设计与实现》开题报告提炼，并补全电商必备能力：购物车、订单、支付、物流、售后。

## 1. 项目目标与范围

### 1.1 目标
- 为消费者提供：便捷购买 + 个性化筛选/搜索 + 有茶文化内涵的商品信息与冲泡教程。
- 为商家提供：商品/库存/订单/售后/评论运营能力。
- 为管理员提供：内容与评论审核、商家与商品治理、数据看板。

### 1.2 三类核心角色
- **消费者（USER）**：浏览、搜索、下单、支付、评价、售后、会员。
- **商家（MERCHANT）**：上架商品、处理订单/发货、处理售后、回复评论、活动维护。
- **管理员（ADMIN）**：用户/商家管理、商品与内容审核、评论审核、平台配置与数据统计。

### 1.3 技术栈建议（与开题一致）
- **后端**：Spring Boot + Spring Security + JWT + MyBatis + MySQL + Redis
- **前端**：Vue + Element UI + Axios + Vue Router + ECharts
- **搜索增强**：Elasticsearch（商品搜索/联想）
- **可选增强**：协同过滤/召回（推荐）

---

## 2. 功能点梳理（MVP + 增强）

### 2.1 用户端（消费者）
- **账号与安全**
  - 注册/登录/退出
  - JWT 会话、刷新令牌（可选）
  - 找回密码（可选，短信/邮箱）
- **商品浏览与检索**（开题明确）
  - 商品列表（分页）
  - 按：品类、产地、工艺、价格区间筛选
  - 关键词搜索（MVP MySQL like；增强 ES）
  - 热门/新品/推荐（可用 Redis 缓存）
- **个性化推荐（开题明确）**
  - 首页推荐：规则推荐（同品类/同产地/同工艺 + 热销/新品）
  - 详情页推荐：相似商品推荐（同类目 + 价格区间接近）
  - 增强：协同过滤/召回（基于用户行为数据 `user_event`）
- **商品详情与茶文化展示**（开题明确）
  - 规格参数、口感描述、冲泡方法、产地环境
  - 关联文章/教程/视频链接（MVP 推荐做文章/教程列表 + 详情）
  - 溯源信息（批次/产区/检测/证书，开题提到“品质溯源”）
- **茶文化传播（开题明确）**
  - 茶文化文章列表/详情
  - 冲泡教程列表/详情（可与文章共用一张表，用 `type` 区分）
  - 内容与商品关联展示（详情页展示推荐文章/教程）
- **品质溯源查询（开题明确）**
  - 用户通过 `traceCode` 查询溯源信息（支持扫码/输入码）
  - 仅展示已审核通过的溯源记录
- **购物车（新增）**
  - 加入购物车、修改数量、选中/取消、删除
  - 结算页（地址、优惠、运费、发票可选）
- **订单（新增）**
  - 创建订单、取消订单
  - 订单列表/详情
  - 确认收货
- **支付（开题提到支付/会员；这里补全实现）**
  - 支付下单（MVP：模拟支付；增强：对接支付宝/微信）
  - 支付记录查询
- **物流（新增）**
  - 查看发货信息、物流单号、轨迹（MVP 可只存轨迹文本）
- **评论反馈（开题明确）**
  - 评分、评论、晒单（图片）
  - 查看商家回复
- **售后（新增）**
  - 退款/退货/换货申请
  - 售后进度查询、补充材料、撤销申请
- **会员体系（开题明确）**
  - 普通/VIP/超级会员
  - 权益：折扣、专属活动、优先发货（可映射为发货 SLA）
- **营销与互动机制（开题痛点补齐，MVP 可选）**
  - 活动页：限时折扣/满减活动列表与详情
  - 优惠券：领取、查看、下单使用（与会员折扣可叠加规则需明确，MVP 可先不叠加）
  - 热门搜索与热词推荐（提升探索与转化）
- **个人中心**
  - 收货地址管理
  - 收藏/足迹（可选）

### 2.2 商家端
- 商品管理：新增/编辑/上下架、图片、规格 SKU、库存
- 品质溯源管理：维护商品批次溯源信息、上传证书/检测报告、提交审核
- 订单管理：接单、发货、取消/关闭、备注
- 售后管理：审核、退款处理、退货入库确认
- 评论运营：查看/回复评论
- 数据：销量/订单趋势（ECharts）

### 2.3 管理端
- 用户/商家管理：启用/禁用、角色分配
- 商品治理：类目管理、敏感信息审查、违规下架
- 评论审核（开题明确）
- 溯源审核：商家提交的溯源信息审核通过后才对外展示
- 内容管理：茶文化文章/冲泡教程审核与发布
- 运营配置：会员等级/折扣、活动（可选）、首页推荐位
- 数据看板：销售额、订单量、热销品、售后率

---

## 3. 核心业务流程（文字版泳道）

### 3.1 浏览-加购-下单-支付-履约
1. 用户浏览商品列表/搜索 -> 进入详情
2. 选择 SKU/数量 -> 加入购物车
3. 购物车勾选商品 -> 提交结算（地址/优惠）
4. 创建订单（冻结库存）
5. 支付成功 -> 订单状态变更为已支付
6. 商家发货 -> 录入物流单号
7. 用户确认收货 -> 可评价

### 3.2 售后（退款/退货）
1. 用户在订单项发起售后（原因/图片/说明）
2. 商家审核：通过/拒绝
3. 通过后：
   - 仅退款：发起退款 -> 完成
   - 退货退款：用户寄回 -> 商家确认入库 -> 退款 -> 完成

### 3.3 评论审核
1. 用户提交评论（初始状态：待审核）
2. 管理员审核通过后展示；不通过则拒绝（可记录原因）
3. 商家可对通过的评论回复

### 3.4 品质溯源维护与展示
1. 商家为商品/批次创建溯源记录（产区、采摘/生产信息、证书/检测报告链接）
2. 商家提交审核
3. 管理员审核通过后：
   - 商品详情页可展示溯源摘要
   - 用户可通过 `traceCode` 查询完整溯源信息

---

## 4. 非功能需求（落地约束）
- **安全**
  - JWT + Spring Security；接口按角色鉴权
  - 密码 BCrypt
  - 关键操作审计：登录、发货、退款、上架/下架
- **性能**
  - Redis：热门商品、首页推荐、会话/验证码、购物车（可选）
  - 列表接口必须分页
- **一致性**
  - 下单冻结库存，支付失败/超时释放（可选：延时任务）
- **可用性**
  - 统一错误码；前端可提示可恢复操作

### 4.1 MVP 必做实现细节（从 0 到 1 落地清单）

#### 4.1.1 认证与登录方式（MVP 推荐组合）
- **注册方式**
  - **手机号 + 密码**（MVP）
  - **用户名 + 密码**（可选，与手机号二选一或并存）
  - 邮箱注册（可选）
- **登录方式**
  - **账号（手机号/用户名/邮箱任一）+ 密码**（MVP）
  - **短信验证码登录**（可选增强，涉及短信通道；MVP 可先做“图形验证码 + 密码登录”）
- **找回密码**（可选增强）
  - 手机号验证码重置密码

#### 4.1.2 密码与风控（MVP）
- **密码规则（建议）**
  - 长度：8-20
  - 至少包含：字母 + 数字（是否强制特殊字符可选）
- **登录失败限制（建议）**
  - 单账号连续失败 5 次：冻结 10 分钟（Redis 计数）
  - 记录失败原因但不暴露具体“账号不存在/密码错误”（前端统一提示“账号或密码错误”）
- **验证码策略（建议）**
  - 连续失败达到阈值后要求图形验证码

#### 4.1.3 Token 策略（MVP）
- `accessToken`：有效期建议 2 小时
- `refreshToken`：MVP 可不做；若做：有效期 7-30 天 + 服务器侧可撤销（落库或 Redis 黑名单）
- **退出登录**：MVP 可仅前端删除 Token；增强：服务端 Token 黑名单/版本号。

#### 4.1.4 请求链路字段（建议强制）
- `X-Request-Id`：前端生成（UUID）并随每个请求携带；后端若缺失则生成并回传
- 日志中必须打印：`requestId`、`userId`、`role`、`uri`、`method`、`elapsedMs`

#### 4.1.5 订单/支付的幂等（MVP 必做）
- **下单幂等**：前端提交订单时携带 `Idempotency-Key`（UUID）
  - 后端将 `Idempotency-Key + userId` 记录到 Redis（或表）
  - 重复请求直接返回第一次创建的 `orderNo/orderId`
- **支付回调幂等**：`payNo` 唯一；回调重复时必须返回成功但不重复改状态

#### 4.1.6 库存并发（MVP 推荐实现）
- 下单扣库存（MVP 简化）
  - `UPDATE product_sku SET stock=stock-? WHERE id=? AND stock>=?`
  - 受影响行数为 0 -> 库存不足（错误码 `40901`）
- 增强：冻结库存 + 超时释放（可用延迟任务/定时扫描 `CREATED` 超时订单）

#### 4.1.7 关键日志（必须）
- **业务日志**：下单、支付成功、发货、确认收货、申请售后、审核售后、退款
- **安全审计**：登录成功/失败、权限拒绝、管理员审核操作
- **敏感信息脱敏**：手机号、身份证（若后续加入）、密码绝不输出；Token 仅输出前后 6 位

#### 4.1.8 图片上传约束（评论晒单/售后举证）
- M​VP 可先用“图片 URL”字段（由前端上传到对象存储后传 URL）；或后端提供上传接口
- 限制建议：
  - 单图 <= 2MB
  - 最多 6 张
  - 格式：jpg/png/webp

#### 4.1.9 个性化推荐（MVP 规则推荐 + 可扩展）
- **MVP 推荐策略（不依赖算法）**
  - 首页：热销 TopN（按 `sales_count`）+ 新品（按 `created_at`）
  - 详情页：同类目 + 价格区间接近 + 排除自身
  - 用户有行为时：最近浏览类目优先
- **行为采集（为协同过滤/ES/搜索热词做准备）**
  - 记录：浏览商品、搜索关键词、加购、下单、支付
  - MVP 可先落库 `user_event`，后续再做离线/在线推荐

#### 4.1.10 溯源信息展示边界（MVP）
- 仅展示 `APPROVED` 的溯源记录
- `traceCode` 不存在：`40401`
- `traceCode` 存在但未审核：对用户返回 `40401`（避免暴露）

#### 4.1.11 Elasticsearch（搜索增强，MVP 使用）
- **目标**：解决开题报告提到的“搜索精准度不足”，关键词检索走 ES（支持中文分词、联想、排序）。
- **索引设计（建议）**
  - 索引名：`tea_product_v1`
  - 文档：以 `productId` 为主键，聚合：标题/副标题/类目/产地/工艺/口感/价格区间/销量/上架状态/商家
  - SKU：MVP 可只索引 `minPrice/maxPrice` 与 `inStock`（是否有库存）
- **中文分词**
  - 推荐：IK 分词（需 ES 安装 `analysis-ik` 插件）
  - 若环境无法安装插件：先用 `smartcn` 分词（效果弱于 IK，但可运行）
- **数据同步策略（必须落地，含数据库层）**
  - 全量：启动时/管理员触发，从 MySQL 批量导入到 ES（Bulk）
  - 增量：使用 Outbox 表记录商品变更事件，由后台任务消费并更新 ES（重试/死信）
- **降级策略（建议）**
  - ES 不可用时：关键词搜索可临时降级为 MySQL like（仅用于可用性兜底，日志告警）

##### 4.1.11.1 开发顺序（强制按顺序落地）
1. **数据库层**：建 `search_outbox_event` 表；在商品变更事务中写入 Outbox。
2. **ES 索引**：确定 `tea_product_v1` mapping/settings（分词、completion suggest）。
3. **全量导入**：实现 Reindex（MySQL -> ES Bulk），并提供管理员触发接口。
4. **增量同步**：实现 Outbox 消费任务（重试/失败告警）。
5. **搜索接口**：`/api/search/products`、`/api/search/suggest`。
6. **热词统计**：从 `user_event` 的 SEARCH 统计（缓存 60 秒）。
7. **前端接入**：搜索页/筛选/排序/联想。

#### 4.1.12 营销活动（MVP 可选）
- **活动范围**
  - `ALL`：全场
  - `CATEGORY`：指定类目
  - `PRODUCT`：指定商品
- **折扣类型（建议）**
  - `DISCOUNT_RATE`：按折扣率（如 9 折）
  - `REDUCE_AMOUNT`：立减（如 -10 元）
  - `THRESHOLD_REDUCE`：满减（如满 99-10）
- **优惠券使用规则（MVP 推荐）**
  - 同一订单最多使用 1 张优惠券
  - 优惠券与会员折扣：MVP 先不叠加（避免规则复杂），后续可扩展

### 4.2 日志/审计/可观测性设计（MVP 必做）

#### 4.2.1 日志分层
- **访问日志（Access Log）**：每个请求一条（method、uri、status、elapsedMs）
- **业务日志（Biz Log）**：关键业务动作（下单、支付成功、发货、售后、审核）
- **安全审计（Audit Log）**：登录、权限拒绝、管理员/商家关键操作

#### 4.2.2 统一日志字段（建议用 MDC）
- `requestId`：来自 `X-Request-Id`（无则后端生成）
- `userId` / `role`
- `ip` / `userAgent`
- `biz`：业务对象关键标识（如 `orderNo`、`payNo`、`afterSaleNo`）

#### 4.2.3 脱敏与限长
- `phone`：`138****5678`
- `Authorization`：不打印；如需排查仅保留前后 6 位
- 回调原文 `notify_raw`：限长（例如 8KB），并过滤敏感字段

#### 4.2.4 建议新增审计表（可选，但强烈建议 MVP 做）
- 表名建议：`audit_log`
  - `id` BIGINT PK
  - `request_id` VARCHAR(64)
  - `actor_user_id` BIGINT
  - `actor_role` VARCHAR(20)
  - `action` VARCHAR(50)（LOGIN_SUCCESS、ORDER_CREATE、ORDER_SHIP、AFTER_SALE_APPROVE...）
  - `biz_type` VARCHAR(30)（ORDER/PAYMENT/AFTER_SALE/REVIEW/PRODUCT）
  - `biz_id` VARCHAR(64)（orderNo/payNo...）
  - `detail_json` JSON（限字段）
  - `ip` VARCHAR(45)
  - `ua` VARCHAR(255)
  - `created_at` DATETIME

---

## 5. 数据字典（状态枚举建议）

### 5.1 订单状态 `order_status`
- `CREATED`：已创建待支付
- `PAID`：已支付
- `CANCELLED`：已取消
- `SHIPPED`：已发货
- `COMPLETED`：已完成（确认收货）
- `CLOSED`：已关闭（售后完结后平台关闭等）

### 5.2 支付状态 `pay_status`
- `UNPAID` / `PAYING` / `PAID` / `FAILED` / `REFUNDED`

### 5.3 售后类型/状态
- `after_sale_type`：`REFUND_ONLY` / `RETURN_REFUND` / `EXCHANGE`
- `after_sale_status`：`APPLIED` / `MERCHANT_APPROVED` / `MERCHANT_REJECTED` / `BUYER_SHIPPED_BACK` / `MERCHANT_RECEIVED` / `REFUNDING` / `COMPLETED` / `CANCELLED`

### 5.4 评论状态
- `PENDING` / `APPROVED` / `REJECTED`

---

## 6. 接口设计（REST API）

### 6.1 约定
- **Base URL**：`/api`
- **认证**：`Authorization: Bearer <JWT>`
- **分页**：`page`(从 1 开始)、`pageSize`
- **幂等**：`Idempotency-Key: <uuid>`（仅对“创建类接口”要求，如创建订单/支付单）
- **链路追踪**：`X-Request-Id: <uuid>`（建议前端每次请求必传）
- **统一响应**（建议）
  - `code`：0 成功；非 0 为错误
  - `message`
  - `data`
- **错误码示例**
  - `40101` 未登录/Token 失效
  - `40301` 无权限
  - `40401` 资源不存在
  - `40901` 库存不足

#### 6.1.1 统一响应示例

成功：

```json
{"code":0,"message":"OK","data":{"id":1}}
```

失败：

```json
{"code":40901,"message":"库存不足","data":null}
```

### 6.2 认证与账号
- `POST /api/auth/register`
  - 入参：`phone/email/username`，`password`
- `POST /api/auth/login`
  - 入参：`account`，`password`
  - 出参：`accessToken`，`expiresIn`，`role`，`userInfo`
- `POST /api/auth/logout`

#### 6.2.1 注册/登录校验与边界（MVP）
- **注册校验**
  - `phone`：11 位数字（按国内手机号）；唯一
  - `username`：4-20；允许字母数字下划线；唯一
  - `password`：8-20；至少字母+数字
  - **边界与失败**：
    - 手机号/用户名已存在：`40902`
    - 参数不合法：`40001`
- **登录校验**
  - `account`：可为 phone/username/email
  - 失败次数限制：`42901`（Too Many Requests / 账号被临时锁定）
  - **日志**：记录 `account`（脱敏）、`ip`、`ua`、成功/失败原因码

#### 6.2.2 刷新 Token（可选增强，建议预留）
- `POST /api/auth/refresh`
  - 入参：`refreshToken`
  - 出参：新的 `accessToken`，`expiresIn`

### 6.3 用户与地址
- `GET /api/users/me`
- `PUT /api/users/me`（昵称/头像等）
- `GET /api/addresses`
- `POST /api/addresses`
- `PUT /api/addresses/{id}`
- `DELETE /api/addresses/{id}`

### 6.4 类目与商品
- `GET /api/categories`（树形）
- `GET /api/products`
  - Query：`keyword`，`categoryId`，`origin`，`process`，`minPrice`，`maxPrice`，`sort`
- `GET /api/products/{id}`（含：详情、图片、SKU、文化内容、溯源摘要）
- `GET /api/products/{id}/trace`（返回溯源摘要与 `traceCode`，未审核时对用户返回空或 404）

### 6.4.1 推荐与行为采集
- `GET /api/recommendations/home`（首页推荐）
  - Query：`limit`（默认 20，最大 50）
- `GET /api/recommendations/products/{productId}`（相似推荐）
  - Query：`limit`（默认 12，最大 30）
- `POST /api/events`（用户行为上报；允许匿名）
  - 入参：`type`（VIEW_PRODUCT/SEARCH/ADD_TO_CART/CREATE_ORDER/PAY_SUCCESS），`payload`（JSON）

### 6.4.2 溯源查询（用户侧）
- `GET /api/traces/{traceCode}`（用户扫码/输入码查询，需仅返回已审核信息）

### 6.4.3 搜索增强（Elasticsearch）
- `GET /api/search/products`
  - Query：
    - `keyword`（0-50；为空表示不按关键词，仅筛选/排序）
    - `categoryId`、`origin`、`process`、`minPrice`、`maxPrice`
    - `sort`：`relevance|sales_desc|price_asc|price_desc|newest`
    - `page`、`pageSize`
  - 说明：keyword 命中权重：`title` > `subtitle` > `origin/process/taste`
- `GET /api/search/suggest`
  - Query：`keyword`（1-20）
  - 返回：联想词列表（ES completion / prefix）
- `GET /api/search/hot-words`
  - Query：`limit`（默认 10，最大 50）
  - 统计来源：`user_event` 的 SEARCH（MVP），或后续切到 ES terms agg

#### 管理端（搜索索引维护）
- `POST /api/admin/search/reindex`（全量重建索引；仅管理员）

#### 商家商品管理
- `POST /api/merchant/products`
- `PUT /api/merchant/products/{id}`
- `POST /api/merchant/products/{id}/on-shelf`
- `POST /api/merchant/products/{id}/off-shelf`
- `POST /api/merchant/products/{id}/skus` / `PUT /api/merchant/skus/{skuId}`

#### 商家溯源管理
- `POST /api/merchant/products/{id}/traces`（创建溯源记录）
- `PUT /api/merchant/traces/{traceId}`（编辑）
- `POST /api/merchant/traces/{traceId}/submit`（提交审核）
- `GET /api/merchant/traces`（列表/筛选 status）

### 6.5 购物车
- `GET /api/cart`
- `POST /api/cart/items`
  - 入参：`skuId`，`quantity`
- `PUT /api/cart/items/{itemId}`
- `DELETE /api/cart/items/{itemId}`
- `POST /api/cart/clear`（可选）

### 6.6 订单
- `POST /api/orders`
  - 入参：`addressId`，`items:[{skuId,quantity}]`（或 `cartItemIds`），`couponId`（可选）
  - 出参：`orderId`，`payAmount`
- `GET /api/orders`（列表）
- `GET /api/orders/{orderId}`（详情+状态流转）
- `POST /api/orders/{orderId}/cancel`
- `POST /api/orders/{orderId}/confirm-receipt`

#### 6.6.1 订单创建规则（MVP 明确）
- `items` 至少 1 条，最多建议 50 条（防止超大订单）
- `quantity`：1-999（上限可配置）
- 下单时必须校验：
  - SKU 存在且可售（sku.status=1）
  - 商品已上架（product.status=ON_SHELF）
  - 库存充足（并发扣减，失败返回 `40901`）
- 订单金额计算：
  - `totalAmount` = Σ(price * quantity)
  - `payAmount` = totalAmount * 会员折扣（向下保留 2 位，规则写死或可配置）
- 幂等：支持 `Idempotency-Key` 请求头

#### 6.6.2 取消/确认收货边界
- 仅 `CREATED` 可取消；已支付不可直接取消（需走退款/售后）
- 仅 `SHIPPED` 可确认收货；已完成不可重复确认（幂等返回成功）

#### 商家订单履约
- `GET /api/merchant/orders`（按状态筛选）
- `POST /api/merchant/orders/{orderId}/ship`
  - 入参：`carrier`，`trackingNo`，`shipTime`

### 6.7 支付（MVP 模拟）
- `POST /api/payments/{orderId}/create`
  - 出参：`payNo`，`payUrl/mockQr`
- `POST /api/payments/{payNo}/notify`（模拟回调；真实对接时改为网关回调）
- `GET /api/payments/{orderId}`

#### 6.7.1 支付方式设计（MVP 到可扩展）
- `channel`：
  - `MOCK`（MVP 必做，模拟二维码或收银台链接）
  - `ALIPAY`（增强）
  - `WECHAT`（增强）
- 真实支付对接时，建议统一抽象：`PaymentGateway` 接口
  - `createPayment(order)` -> 返回 `payUrl/qrCode`
  - `handleNotify(payload)` -> 验签、幂等、更新订单/支付单

#### 6.7.2 支付临界/失败场景（MVP 必覆盖）
- 订单不存在：`40401`
- 订单已支付：再次 create 应返回已有 `payNo`（幂等）
- 支付金额不一致：回调拒绝（记录安全日志，错误码 `40002`）
- 回调重复：必须幂等（不重复更新、不重复发货）

#### 6.7.3 订单超时关闭（建议 MVP 实现）
- `CREATED` 超过 30 分钟未支付：自动关闭为 `CANCELLED/CLOSED`（二选一）
- 若做冻结库存：关闭订单后释放库存

### 6.8 售后
- `POST /api/after-sales`
  - 入参：`orderItemId`，`type`，`reason`，`amount`（可选），`images[]`
- `GET /api/after-sales`（我的售后列表）
- `GET /api/after-sales/{id}`
- `POST /api/after-sales/{id}/cancel`

#### 6.8.1 售后申请边界（MVP）
- 仅已支付订单可申请售后（至少 `PAID`）
- 退款金额：
  - `applyAmount` 必须 > 0 且 <= 对应 `orderItem` 的 `subtotal`
  - 超额：`40003`
- 一条 `order_item` 同一时间只允许存在一个未完结售后（唯一约束或业务校验）

#### 商家售后处理
- `GET /api/merchant/after-sales`
- `POST /api/merchant/after-sales/{id}/approve`
- `POST /api/merchant/after-sales/{id}/reject`
- `POST /api/merchant/after-sales/{id}/confirm-received`（退货入库确认）
- `POST /api/merchant/after-sales/{id}/refund`（触发退款记录）

### 6.9 评论与审核
- `POST /api/reviews`
  - 入参：`orderItemId`，`rating`，`content`，`images[]`
- `GET /api/products/{id}/reviews`（仅展示 APPROVED）

#### 6.9.1 评论边界（MVP）
- 仅 `COMPLETED` 的订单项可评论
- 评分：1-5
- 内容长度：1-500
- 图片：0-6
- 重复评论：同一 `orderItemId` 只能评论一次（唯一约束）

#### 商家回复
- `POST /api/merchant/reviews/{reviewId}/reply`

#### 管理员审核
- `GET /api/admin/reviews?status=PENDING`
- `POST /api/admin/reviews/{reviewId}/approve`
- `POST /api/admin/reviews/{reviewId}/reject`

#### 管理员溯源审核
- `GET /api/admin/traces?status=PENDING`
- `POST /api/admin/traces/{traceId}/approve`
- `POST /api/admin/traces/{traceId}/reject`

### 6.10 会员
- `GET /api/membership/me`
- `POST /api/membership/upgrade`（MVP 可直接改等级；增强：付费/积分）

### 6.11 内容（茶文化/冲泡教程）
- `GET /api/content/articles`（分页 + type/keyword）
  - Query：`type=CULTURE|BREW_TUTORIAL`，`keyword`，`page`，`pageSize`
- `GET /api/content/articles/{id}`

#### 管理端内容管理
- `POST /api/admin/content/articles`
- `PUT /api/admin/content/articles/{id}`
- `POST /api/admin/content/articles/{id}/publish`

#### 6.11.1 内容与商品关联（MVP 可选）
- `GET /api/products/{id}/articles`（返回推荐文章/教程列表）

#### 6.11.2 首页运营（可选，MVP 可先用静态）
- `GET /api/banners`（首页轮播/推荐位）
- `POST /api/admin/banners` / `PUT /api/admin/banners/{id}` / `POST /api/admin/banners/{id}/enable`

### 6.12 数据看板（商家/管理员）
- `GET /api/merchant/stats/sales-trend?range=7d|30d`
- `GET /api/admin/stats/overview?range=7d|30d`

### 6.13 营销活动与优惠券（可选）

#### 用户侧
- `GET /api/marketing/activities`（当前有效活动）
- `GET /api/marketing/activities/{id}`
- `GET /api/marketing/coupons`（可领取的券列表）
- `POST /api/marketing/coupons/{couponId}/claim`（领取）
- `GET /api/marketing/my-coupons?status=UNUSED|USED|EXPIRED`

#### 管理端
- `POST /api/admin/marketing/activities`
- `PUT /api/admin/marketing/activities/{id}`
- `POST /api/admin/marketing/activities/{id}/enable`
- `POST /api/admin/marketing/coupons`
- `PUT /api/admin/marketing/coupons/{id}`
- `POST /api/admin/marketing/coupons/{id}/enable`

---

## 7. 数据库设计（MySQL）

> 设计原则：一张表只做一件事；订单/支付/售后状态用枚举字段 + 状态流转表；关键查询字段建索引。

### 7.1 核心 ER（文字描述）
- `user` 1-N `address`
- `merchant` 1-N `product`
- `product` 1-N `product_sku`、1-N `product_image`
- `user` 1-1 `cart`，`cart` 1-N `cart_item`
- `order` 1-N `order_item`，`order` 1-1 `payment_record`（或 1-N）
- `order_item` 1-N `after_sale`
- `product` 1-N `review`
- `review` 1-1 `review_reply`（可选）

推荐与溯源相关：
- `product` 1-N `product_trace`
- `product_trace` 1-N `trace_file`
- `user` 1-N `user_event`

### 7.2 表结构（字段建议）

#### 7.2.1 `user`
- `id` BIGINT PK
- `username` VARCHAR(50) UNIQUE
- `phone` VARCHAR(20) UNIQUE
- `password_hash` VARCHAR(100)
- `role` ENUM('USER','MERCHANT','ADMIN')
- `status` TINYINT（1 正常/0 禁用）
- `created_at` DATETIME
- 索引：`idx_user_phone`、`idx_user_username`

#### 7.2.1.1 建议新增（MVP 实用字段）
- `last_login_at` DATETIME NULL
- `last_login_ip` VARCHAR(45) NULL
- `password_updated_at` DATETIME NULL

#### 7.2.2 `merchant`
- `id` BIGINT PK
- `user_id` BIGINT UNIQUE（FK -> user.id）
- `shop_name` VARCHAR(100)
- `shop_desc` VARCHAR(500)
- `created_at` DATETIME

#### 7.2.3 `address`
- `id` BIGINT PK
- `user_id` BIGINT
- `receiver_name` VARCHAR(50)
- `receiver_phone` VARCHAR(20)
- `province/city/district` VARCHAR(50)
- `detail` VARCHAR(200)
- `is_default` TINYINT
- 索引：`idx_address_user`

#### 7.2.4 `category`
- `id` BIGINT PK
- `parent_id` BIGINT
- `name` VARCHAR(50)
- `sort` INT
- `status` TINYINT

#### 7.2.5 `product`
- `id` BIGINT PK
- `merchant_id` BIGINT
- `category_id` BIGINT
- `title` VARCHAR(120)
- `subtitle` VARCHAR(200)
- `origin` VARCHAR(50)（产地）
- `process` VARCHAR(50)（工艺）
- `taste` VARCHAR(200)（口感）
- `brew_method` TEXT（冲泡方法）
- `environment` TEXT（产地环境）
- `status` ENUM('DRAFT','ON_SHELF','OFF_SHELF')
- `min_price` DECIMAL(10,2)
- `max_price` DECIMAL(10,2)
- `sales_count` INT
- `created_at` DATETIME
- 索引：`idx_product_category`、`idx_product_merchant`、`idx_product_status`、`idx_product_origin_process`

#### 7.2.6 `product_image`
- `id` BIGINT PK
- `product_id` BIGINT
- `url` VARCHAR(500)
- `sort` INT

#### 7.2.7 `product_sku`
- `id` BIGINT PK
- `product_id` BIGINT
- `sku_name` VARCHAR(80)（如：250g/罐）
- `price` DECIMAL(10,2)
- `stock` INT
- `status` TINYINT
- 索引：`idx_sku_product`

#### 7.2.7.1 溯源（MVP 最小可用）

##### `product_trace`
- `id` BIGINT PK
- `trace_code` VARCHAR(32) UNIQUE（用户查询码）
- `product_id` BIGINT
- `merchant_id` BIGINT
- `batch_no` VARCHAR(50)
- `origin` VARCHAR(100)（产区更细粒度：省市县/村）
- `pick_date` DATE NULL（采摘日期）
- `process` VARCHAR(50)（工艺）
- `producer` VARCHAR(100)（茶企/茶农）
- `inspection_report_url` VARCHAR(500) NULL（检测报告）
- `certificate_url` VARCHAR(500) NULL（证书）
- `summary` VARCHAR(500)（对外展示摘要）
- `status` ENUM('DRAFT','PENDING','APPROVED','REJECTED')
- `reject_reason` VARCHAR(200) NULL
- `created_at` DATETIME
- `updated_at` DATETIME
- 索引：`idx_trace_product`、`idx_trace_merchant_status`

##### `trace_file`（可选，用于多文件）
- `id` BIGINT PK
- `trace_id` BIGINT
- `type` ENUM('CERT','REPORT','OTHER')
- `url` VARCHAR(500)
- `created_at` DATETIME

#### 7.2.7.2 用户行为（推荐/热词/统计）

##### `user_event`
- `id` BIGINT PK
- `user_id` BIGINT NULL（允许匿名）
- `session_id` VARCHAR(64) NULL
- `type` ENUM('VIEW_PRODUCT','SEARCH','ADD_TO_CART','CREATE_ORDER','PAY_SUCCESS')
- `keyword` VARCHAR(50) NULL（仅 SEARCH 使用，便于统计与索引）
- `payload` JSON
- `created_at` DATETIME
- 索引：`idx_event_user_type_time`、`idx_event_type_time`、`idx_event_keyword_time`

建表 SQL（MySQL 8+）：

```sql
CREATE TABLE IF NOT EXISTS user_event (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NULL,
  session_id VARCHAR(64) NULL,
  type VARCHAR(32) NOT NULL,
  keyword VARCHAR(50) NULL,
  payload JSON NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  KEY idx_event_user_type_time (user_id, type, created_at),
  KEY idx_event_type_time (type, created_at),
  KEY idx_event_keyword_time (keyword, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

若已存在 `user_event` 表（兼容存量库）：

```sql
ALTER TABLE user_event
  ADD COLUMN keyword VARCHAR(50) NULL AFTER type;

CREATE INDEX idx_event_keyword_time ON user_event (keyword, created_at);
```

示例数据（用于热词统计联调）：

```sql
INSERT INTO user_event (user_id, session_id, type, keyword, payload, created_at) VALUES
  (1, NULL, 'SEARCH', '龙井', JSON_OBJECT('keyword','龙井','from','search_bar'), NOW() - INTERVAL 1 HOUR),
  (2, NULL, 'SEARCH', '普洱', JSON_OBJECT('keyword','普洱','from','search_bar'), NOW() - INTERVAL 2 HOUR),
  (NULL, 's-20260104-abc', 'SEARCH', '铁观音', JSON_OBJECT('keyword','铁观音','from','home_hot'), NOW() - INTERVAL 3 HOUR),
  (3, NULL, 'SEARCH', '龙井', JSON_OBJECT('keyword','龙井','from','search_bar'), NOW() - INTERVAL 6 HOUR),
  (1, NULL, 'SEARCH', '龙井茶', JSON_OBJECT('keyword','龙井茶','from','search_bar'), NOW() - INTERVAL 1 DAY),
  (2, NULL, 'SEARCH', '白茶', JSON_OBJECT('keyword','白茶','from','search_bar'), NOW() - INTERVAL 2 DAY);
```

#### 7.2.7.3 ES 同步 Outbox（搜索增强必备，数据库层）

##### `search_outbox_event`
- `id` BIGINT PK
- `biz_type` ENUM('PRODUCT')
- `biz_id` BIGINT（productId）
- `event_type` ENUM('UPSERT','DELETE')
- `payload` JSON NULL（可选：冗余字段减少 join；MVP 可为空）
- `status` ENUM('NEW','PROCESSING','DONE','FAILED')
- `retry_count` INT DEFAULT 0
- `next_retry_at` DATETIME NULL
- `last_error` VARCHAR(200) NULL
- `created_at` DATETIME
- `updated_at` DATETIME
- 索引：`idx_outbox_status_time`、`idx_outbox_biz`

建表 SQL（MySQL 8+）：

```sql
CREATE TABLE IF NOT EXISTS search_outbox_event (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  biz_type VARCHAR(32) NOT NULL,
  biz_id BIGINT NOT NULL,
  event_type VARCHAR(16) NOT NULL,
  payload JSON NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'NEW',
  retry_count INT NOT NULL DEFAULT 0,
  next_retry_at DATETIME NULL,
  last_error VARCHAR(200) NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_outbox_status_time (status, next_retry_at, created_at),
  KEY idx_outbox_biz (biz_type, biz_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

示例数据（用于增量同步联调）：

```sql
INSERT INTO search_outbox_event (biz_type, biz_id, event_type, payload, status, retry_count, next_retry_at, last_error, created_at, updated_at) VALUES
  ('PRODUCT', 1, 'UPSERT', JSON_OBJECT('productId',1), 'NEW', 0, NOW(), NULL, NOW(), NOW()),
  ('PRODUCT', 2, 'UPSERT', JSON_OBJECT('productId',2), 'NEW', 0, NOW(), NULL, NOW(), NOW()),
  ('PRODUCT', 3, 'DELETE', JSON_OBJECT('productId',3), 'NEW', 0, NOW(), NULL, NOW(), NOW());
```

#### 7.2.8 `cart` / `cart_item`
- `cart(id,user_id)`（`user_id` UNIQUE）
- `cart_item(id,cart_id,sku_id,quantity,checked,created_at)`
- 索引：`idx_cartitem_cart`、`idx_cartitem_sku`

#### 7.2.9 `order`
- `id` BIGINT PK
- `order_no` VARCHAR(32) UNIQUE
- `user_id` BIGINT
- `merchant_id` BIGINT（MVP 可只支持单商家订单；增强：拆单）
- `status` ENUM（见 5.1）
- `total_amount` DECIMAL(10,2)
- `pay_amount` DECIMAL(10,2)
- `coupon_id` BIGINT NULL
- `coupon_discount_amount` DECIMAL(10,2) DEFAULT 0
- `pay_status` ENUM（见 5.2）
- `address_snapshot` JSON（下单时地址快照，避免后续修改影响历史）
- `created_at` DATETIME
- `paid_at` DATETIME NULL
- `shipped_at` DATETIME NULL
- `completed_at` DATETIME NULL
- 索引：`idx_order_user`、`idx_order_merchant_status`、`idx_order_no`

#### 7.2.10 `order_item`
- `id` BIGINT PK
- `order_id` BIGINT
- `product_id` BIGINT
- `sku_id` BIGINT
- `title_snapshot` VARCHAR(120)
- `sku_name_snapshot` VARCHAR(80)
- `price` DECIMAL(10,2)
- `quantity` INT
- `subtotal` DECIMAL(10,2)
- 索引：`idx_orderitem_order`、`idx_orderitem_sku`

#### 7.2.11 `payment_record`
- `id` BIGINT PK
- `pay_no` VARCHAR(32) UNIQUE
- `order_id` BIGINT
- `channel` ENUM('MOCK','ALIPAY','WECHAT')
- `status` ENUM('CREATED','PAID','FAILED','REFUNDED')
- `amount` DECIMAL(10,2)
- `created_at` DATETIME
- `paid_at` DATETIME NULL

#### 7.2.11.1 幂等/排查字段建议
- `notify_raw` MEDIUMTEXT NULL（保存回调原文，注意脱敏/限长）
- `notify_at` DATETIME NULL

#### 7.2.12 `shipment`
- `id` BIGINT PK
- `order_id` BIGINT UNIQUE
- `carrier` VARCHAR(50)
- `tracking_no` VARCHAR(50)
- `trace` TEXT（MVP）
- `created_at` DATETIME

#### 7.2.13 `after_sale`
- `id` BIGINT PK
- `after_sale_no` VARCHAR(32) UNIQUE
- `order_id` BIGINT
- `order_item_id` BIGINT
- `user_id` BIGINT
- `merchant_id` BIGINT
- `type` ENUM（见 5.3）
- `status` ENUM（见 5.3）
- `reason` VARCHAR(200)
- `description` VARCHAR(500)
- `apply_amount` DECIMAL(10,2)
- `created_at` DATETIME
- 索引：`idx_aftersale_user`、`idx_aftersale_merchant_status`、`idx_aftersale_order_item`

#### 7.2.13.1 建议新增字段（便于闭环）
- `merchant_comment` VARCHAR(500) NULL（拒绝原因/处理说明）
- `completed_at` DATETIME NULL

#### 7.2.14 `review` / `review_reply`
- `review(id,product_id,order_item_id,user_id,rating,content,status,created_at)`
- `review_reply(id,review_id,merchant_id,content,created_at)`
- 索引：`idx_review_product_status`、`idx_review_order_item`

#### 7.2.14.1 强约束（MVP 推荐）
- `review.order_item_id` UNIQUE（保证不重复评价）

#### 7.2.15 `membership_level` / `user_membership`
- `membership_level(id,code,name,discount_rate,benefits_json,sort)`
- `user_membership(user_id UNIQUE, level_id, updated_at)`

#### 7.2.16 `content_article`（茶文化/冲泡教程）
- `id` BIGINT PK
- `type` ENUM('CULTURE','BREW_TUTORIAL')
- `title` VARCHAR(120)
- `summary` VARCHAR(300) NULL
- `cover_url` VARCHAR(500)
- `tags_json` JSON NULL
- `content` MEDIUMTEXT
- `status` ENUM('DRAFT','PUBLISHED')
- `created_at` DATETIME

#### 7.2.16.1 内容与商品关联（MVP 可选）

##### `content_product_rel`
- `id` BIGINT PK
- `content_id` BIGINT
- `product_id` BIGINT
- `sort` INT
- `created_at` DATETIME
- UNIQUE：`(content_id, product_id)`

#### 7.2.17 `banner`（可选：首页运营）
- `id` BIGINT PK
- `title` VARCHAR(100)
- `image_url` VARCHAR(500)
- `link_type` ENUM('PRODUCT','ARTICLE','URL')
- `link_value` VARCHAR(200)
- `sort` INT
- `status` TINYINT
- `created_at` DATETIME

#### 7.2.18 `marketing_activity`（可选：限时折扣/满减）
- `id` BIGINT PK
- `title` VARCHAR(100)
- `type` ENUM('DISCOUNT_RATE','REDUCE_AMOUNT','THRESHOLD_REDUCE')
- `scope` ENUM('ALL','CATEGORY','PRODUCT')
- `scope_value` VARCHAR(64) NULL（categoryId/productId，scope=ALL 时为空）
- `threshold_amount` DECIMAL(10,2) NULL（满减门槛）
- `discount_rate` DECIMAL(5,2) NULL（如 9.00 表示 9 折）
- `reduce_amount` DECIMAL(10,2) NULL
- `start_at` DATETIME
- `end_at` DATETIME
- `status` ENUM('DRAFT','ENABLED','DISABLED')
- `created_at` DATETIME
- 索引：`idx_activity_status_time`

#### 7.2.19 `coupon` / `user_coupon`（可选：优惠券）

##### `coupon`
- `id` BIGINT PK
- `title` VARCHAR(100)
- `code` VARCHAR(32) UNIQUE NULL（可选，支持兑换码）
- `threshold_amount` DECIMAL(10,2) DEFAULT 0
- `discount_amount` DECIMAL(10,2)
- `total` INT
- `per_user_limit` INT DEFAULT 1
- `start_at` DATETIME
- `end_at` DATETIME
- `status` ENUM('DRAFT','ENABLED','DISABLED')
- `created_at` DATETIME

##### `user_coupon`
- `id` BIGINT PK
- `user_id` BIGINT
- `coupon_id` BIGINT
- `status` ENUM('UNUSED','USED','EXPIRED')
- `claimed_at` DATETIME
- `used_order_id` BIGINT NULL
- UNIQUE：`(user_id, coupon_id, claimed_at)`（或用业务限制 per_user_limit）
- 索引：`idx_user_coupon_user_status`

---

## 8. 前端页面与路由建议（Vue + Element UI）

### 8.1 用户端
- `/login` 登录
- `/` 首页（推荐位/分类入口/热门商品）
- `/products` 商品列表（筛选+分页）
- `/products/:id` 商品详情（文化/冲泡/溯源/评论）
- `/culture` 茶文化/教程内容列表
- `/culture/:id` 茶文化/教程详情
- `/cart` 购物车
- `/checkout` 结算
- `/orders` 订单列表
- `/orders/:id` 订单详情
- `/after-sales` 售后列表
- `/after-sales/:id` 售后详情
- `/me` 个人中心（地址/会员）

### 8.2 商家端
- `/merchant/products` 商品管理
- `/merchant/orders` 订单管理（发货）
- `/merchant/after-sales` 售后处理
- `/merchant/reviews` 评论与回复
- `/merchant/stats` 数据看板

### 8.3 管理端
- `/admin/users` 用户管理
- `/admin/merchants` 商家管理
- `/admin/reviews` 评论审核
- `/admin/content` 内容管理
- `/admin/stats` 平台看板

---

## 9. Cursor 开发 Prompt（前后端一起用）

> 使用方法：将下面 Prompt 复制到 Cursor，对应目录执行生成/迭代。你可以先做 MVP：用户端下单闭环 + 商家发货 + 售后 + 评论审核。

### 9.1 后端 Prompt（Spring Boot）

```text
你是资深 Java 后端工程师。请用 Spring Boot + Spring Security + JWT + MyBatis + MySQL + Redis +es搭建“茶叶销售系统”后端。

要求：
1) 前后端分离，提供 RESTful API，统一返回结构：{code,message,data}。
2) 使用 Spring Security 做基于角色的权限控制（USER/MERCHANT/ADMIN）。
3) JWT 登录鉴权，密码 md5加密。
4) 使用 MyBatis（XML 或注解均可）实现持久层；提供完整的表结构 SQL（MySQL 8）。
5) 订单创建需校验库存并冻结库存（MVP：下单扣库存；增强：冻结+释放）。
6) 支付先实现 MOCK：创建支付单 -> 调用 notify 接口标记支付成功。
7) 售后流程：申请、商家审核、确认收货、退款（写入退款/支付记录）。
8) 评论：用户提交后为 PENDING，管理员审核通过后才在商品详情展示；商家可回复。
9) Redis：缓存热门商品列表（比如首页、热搜），以及用户购物车（可选）。
10) 提供 OpenAPI/Swagger 文档（如 springdoc-openapi）。

请按模块输出并实现：
- auth：register/login/logout
- user：me、地址 CRUD
- catalog：类目、商品列表/详情（支持 category/origin/process/price/keyword 过滤 + 分页）
- merchant：商品 CRUD、上下架、SKU/库存
- cart：购物车 CRUD
- order：创建/取消/列表/详情/确认收货
- payment：mock 支付创建与回调
- shipment：商家发货（carrier/trackingNo）
- after-sale：用户申请、商家处理、退款
- review：用户评论、管理员审核、商家回复
- membership：等级与折扣（下单计算 payAmount 时生效）
- stats：商家/管理员统计接口（按天聚合销售额/订单数）

请给出项目结构、关键代码（Controller/Service/Mapper/Entity/DTO/VO/Security配置）、以及初始化 SQL。保证可运行。
```

### 9.2 前端 Prompt（Vue + Element UI）

```text
你是资深前端工程师。请用 Vue + Element UI + Axios + Vue Router 为“茶叶销售系统”实现前端（用户端+商家端+管理端）。

要求：
1) Axios 封装：baseURL=/api，自动带 Authorization Bearer token；统一错误处理（401 跳登录）。
2) 路由按角色拦截：USER/MERCHANT/ADMIN。
3) 页面需要包含：商品列表筛选、商品详情（文化/冲泡/溯源/评论）、购物车、结算、订单列表/详情、售后申请与进度、个人中心地址管理与会员等级展示。
4) 商家端：商品管理、订单发货、售后处理、评论回复、销售趋势图（ECharts）。
5) 管理端：评论审核、内容发布、数据总览。
6) UI：使用 Element UI 组件，列表分页、表单校验、弹窗确认。

请输出：
- 项目目录结构
- 核心页面组件与路由表
- API 调用层（services）
- 状态管理方案（可用 pinia 或简单 localStorage + reactive）
- 关键页面：products、product-detail、cart、checkout、orders、after-sales、merchant/orders、admin/reviews

保证能对接后端接口，并提供必要的 mock 数据方案（后端未完成时）。
```

### 9.3 联调 Prompt（契约优先）

```text
请以“契约优先”的方式列出前后端联调清单：
- 每个接口的请求/响应 JSON 示例
- 分页字段与时间字段格式
- 状态枚举值
- 失败场景（库存不足、商品下架、重复支付、售后超额退款等）
并生成一份用于 Postman/Apifox 的接口集合（以 JSON 形式输出）。
```

### 9.4 MVP 细粒度 Cursor Prompt（推荐按模块逐段生成）

#### 9.4.1 后端模块 Prompt：认证与安全（Auth + Security）

```text
目标：实现可用于 MVP 的注册/登录/JWT 鉴权/角色权限/风控。

请实现：
1) 数据模型：user 表含 phone/username/password_hash/role/status/last_login_at/last_login_ip。
2) 注册：手机号+密码为主，校验手机号唯一；密码按 8-20 且至少字母+数字。
3) 登录：account 支持 phone/username；错误提示统一“账号或密码错误”。
4) 风控：连续失败 5 次锁定 10 分钟（Redis 计数），返回错误码 42901。
5) JWT：accessToken 2 小时；payload 含 userId/role；使用 Spring Security 过滤器校验。
6) 统一响应结构 + 全局异常处理（参数校验错误 40001；资源不存在 40401；冲突 409xx）。
7) 日志：每次登录成功/失败记录 requestId、脱敏 account、ip、ua、结果码。

请输出：
- Spring Security 配置（角色前缀、放行路径、鉴权失败处理）
- JWT 工具类
- AuthController + DTO + Service + Mapper
- Redis Key 设计：login_fail:{account}
- 单元测试/接口测试用例（至少覆盖：重复注册、密码不合规、锁定、token 失效）
```

#### 9.4.2 后端模块 Prompt：商品与搜索（Catalog）

```text
目标：实现商品列表筛选/分页、商品详情（茶文化字段）、商家上架/下架与 SKU 库存。

要求：
1) 商品列表：支持 keyword（like）、categoryId、origin、process、minPrice/maxPrice、sort；必须分页。
2) 商品详情：返回 product + images + skus；包含 brew_method/environment/taste 等文化字段。
3) 上下架规则：仅商家可操作；下架后不可被用户下单。
4) 边界：
   - price >= 0；stock >= 0
   - title 1-120；subtitle <= 200
5) 缓存：热门商品列表缓存到 Redis（key: hot_products），过期 5 分钟。
6) 日志：商家上架/下架、改价、改库存写入业务日志（含 merchantId/productId）。

输出：Controller/Service/Mapper/DTO/VO + 初始化数据脚本。
```

#### 9.4.3 后端模块 Prompt：购物车（Cart）

```text
目标：实现购物车 CRUD，支持 checked 状态并用于结算。

要求：
1) 加入购物车：同 sku 合并数量；quantity 1-999。
2) 修改数量：若超过库存返回 40901。
3) 查询购物车：返回 sku 信息（标题/图片/价格/库存/是否可售）。
4) 边界：商品下架或 sku 不可售时，购物车项标记为 invalid。
5) 日志：加购/删购记录（userId/skuId/quantity）。
```

#### 9.4.4 后端模块 Prompt：订单（Order）

```text
目标：实现下单闭环：创建订单、取消、查询列表/详情、确认收货。

必须实现的业务规则：
1) 创建订单支持 Idempotency-Key 幂等；重复请求返回同一 orderNo。
2) 库存并发扣减：UPDATE stock where stock>=qty；失败返回 40901。
3) 状态机：CREATED -> PAID -> SHIPPED -> COMPLETED；CREATED 可 CANCELLED。
4) 取消订单：仅 CREATED 可取消；已支付需走售后。
5) 确认收货：仅 SHIPPED 可确认；重复确认幂等。
6) 金额：totalAmount=Σ(price*qty)，payAmount=totalAmount*会员折扣，保留 2 位。
7) 地址快照：下单时写入 address_snapshot JSON。
8) 日志：下单/取消/确认收货/状态变化都记录。

请输出：
- OrderController + DTO/VO
- Service 事务边界（@Transactional）
- Mapper SQL（含扣库存 SQL）
- 错误码与异常映射
- 测试用例：库存不足、重复提交幂等、非法状态流转
```

#### 9.4.5 后端模块 Prompt：支付（Payment，MVP Mock + 可扩展）

```text
目标：实现 mock 支付创建与回调，保证幂等与状态一致。

要求：
1) createPayment(orderId)：
   - 若订单不存在 40401
   - 若订单已支付：返回已有 payNo（幂等）
   - 生成 payNo（唯一）写 payment_record(status=CREATED, channel=MOCK)
   - 返回 mockQr 或 payUrl
2) notify(payNo)：
   - 幂等：若已 PAID 直接返回 success
   - 校验金额一致（若提供 amount 参数）
   - 更新 payment_record=PAID，order.pay_status=PAID，order.status=PAID，写 paid_at
3) 预留接口：PaymentGateway 抽象，为 ALIPAY/WECHAT 扩展留好结构。
4) 日志：记录 payNo、orderNo、金额、回调原文（限长/脱敏）。

输出：PaymentController/Service/Mapper + 表结构字段建议 + 测试用例（重复回调、重复创建、金额不一致）。
```

#### 9.4.6 后端模块 Prompt：发货与物流（Shipment）

```text
目标：商家发货录入物流信息，用户可查询。

规则：
1) 仅 PAID 状态可发货。
2) carrier、trackingNo 必填；trackingNo 长度 6-50。
3) 发货幂等：重复发货不允许（40903），或允许更新但需审计（MVP 建议不允许）。
4) 写 shipment 表并将 order 状态更新为 SHIPPED。
5) 日志：merchantId、orderNo、trackingNo。
```

#### 9.4.7 后端模块 Prompt：售后（After-sale）

```text
目标：实现售后申请/审核/退款闭环。

规则：
1) 仅已支付订单项可申请；同一 orderItem 仅允许一个未完结售后。
2) applyAmount >0 且 <= orderItem.subtotal。
3) 商家审核：approve/reject；reject 需要填写 merchant_comment。
4) 退款：写 payment_record 的 REFUNDED（或单独 refund_record），并将 order/pay 状态更新。
5) 日志：申请/审核/退款全记录，包含 afterSaleNo/orderNo/userId/merchantId。

输出：Controller/Service/Mapper + 状态机校验 + 测试用例（超额、重复申请、非法状态）。
```

#### 9.4.8 后端模块 Prompt：评论与审核（Review + Admin）

```text
目标：用户评价、管理员审核、商家回复。

规则：
1) 仅 COMPLETED 的 orderItem 可评论；orderItemId UNIQUE 防止重复。
2) rating 1-5；content 1-500；images 0-6。
3) 创建评论后 status=PENDING；商品详情仅返回 APPROVED。
4) 管理员审核：approve/reject（记录 reason 可选）。
5) 商家仅可回复 APPROVED 评论；回复一次即可（MVP）。

输出：ReviewController + AdminReviewController + MerchantReplyController + 测试用例。
```

#### 9.4.9 前端模块 Prompt：登录/鉴权/路由与错误处理

```text
目标：实现前端可运行的登录体系与三端路由权限控制。

要求：
1) 登录页：账号+密码表单校验；支持记住我（localStorage）。
2) Axios：请求头自动带 Authorization；响应拦截 401 -> 清 token -> 跳转 /login。
3) 路由守卫：根据后端返回 role 控制访问（USER/MERCHANT/ADMIN）。
4) requestId：每次请求生成 X-Request-Id（uuid）。
5) 错误提示：展示后端 code/message；对 40901（库存不足）给明确提示。

输出：router、axios 封装、auth store（pinia 或 localStorage）。
```

#### 9.4.10 前端模块 Prompt：结算/下单/支付（MVP）

```text
目标：购物车 -> 结算 -> 创建订单 -> mock 支付 -> 订单详情刷新。

要求：
1) 结算页展示选中商品、地址选择、金额（含会员折扣）。
2) 创建订单请求带 Idempotency-Key（uuid）。
3) 支付页：展示 mockQr/payUrl，并提供“我已支付”按钮调用 notify。
4) 边界：库存不足/商品下架时提示并引导返回购物车调整。

输出：checkout 页面、payment 页面、订单详情状态刷新逻辑。
```

#### 9.4.11 后端模块 Prompt：全局异常 + 日志 + 审计（工程化必备）

```text
目标：把系统做成可排障、可验收的 MVP：统一异常返回、请求链路 requestId、关键操作审计落库。

请实现：
1) 全局异常处理：
   - 参数校验失败 -> 40001
   - 未登录/Token 失效 -> 40101
   - 无权限 -> 40301
   - 资源不存在 -> 40401
   - 业务冲突（库存不足/重复提交等）-> 409xx
   - 未知异常 -> 50000（message 固定“系统繁忙，请稍后重试”）
2) requestId：
   - 读取请求头 X-Request-Id；若无则生成
   - 放入 MDC，并在响应头回传
3) 访问日志：输出 method、uri、status、elapsedMs、requestId、userId。
4) 审计日志：新增 audit_log 表与 mapper；用 AOP 或在 Service 层显式写入。
   - 记录：登录成功/失败、下单、支付成功、发货、售后审核、评论审核、商品上下架。
5) 脱敏：手机号、token、password；notify_raw 限长。

请输出：
- Filter/Interceptor（requestId + access log）
- GlobalExceptionHandler
- AuditLogService + AuditLogMapper + 表结构 SQL
- 示例：下单与支付成功时写 audit_log 的具体代码
```

#### 9.4.12 后端模块 Prompt：个性化推荐（MVP 规则推荐 + 行为采集）

```text
目标：实现开题要求的“个性化推荐”，先做可落地 MVP（规则推荐），并把用户行为落库，为后续协同过滤做准备。

请实现：
1) user_event 表与写入接口 POST /api/events（允许匿名，匿名用 sessionId）。
2) 首页推荐 GET /api/recommendations/home：
   - 优先返回热销（sales_count）+ 新品（created_at）组合
   - 若用户近期浏览过类目：提高该类目商品权重（MVP 简单实现：先查最近 20 条 VIEW_PRODUCT 的类目）
   - limit 默认 20，最大 50
3) 详情页相似推荐 GET /api/recommendations/products/{productId}：
   - 同类目 + 价格接近（|price - currentMinPrice|）排序
   - 排除自身
4) 缓存：home 推荐结果缓存 60 秒（Redis），不同 userId 可分 key（MVP 可先不分用户缓存，只做公共热门）。
5) 日志与审计：events 写入不做审计；recommendations 仅 access log。

输出：Controller/Service/Mapper + SQL + 测试用例（limit 越界、匿名事件、无行为返回默认推荐）。
```

#### 9.4.13 后端模块 Prompt：品质溯源（Traceability，MVP）

```text
目标：实现开题要求的“品质溯源”最小可用闭环：商家维护->提交审核->管理员审核->用户查询展示。

请实现：
1) product_trace 表（含 trace_code 唯一、status=DRAFT/PENDING/APPROVED/REJECTED）。
2) 商家接口：
   - POST /api/merchant/products/{id}/traces 创建（默认 DRAFT）
   - PUT /api/merchant/traces/{traceId} 编辑（仅 DRAFT/REJECTED 可编辑）
   - POST /api/merchant/traces/{traceId}/submit 提交审核（DRAFT/REJECTED -> PENDING）
   - GET /api/merchant/traces?status= 查询列表
3) 管理员审核：
   - GET /api/admin/traces?status=PENDING
   - approve：PENDING -> APPROVED
   - reject：PENDING -> REJECTED（需填写 reject_reason）
4) 用户查询：GET /api/traces/{traceCode}
   - 仅返回 APPROVED，否则 40401
5) 商品详情溯源摘要：GET /api/products/{id}/trace（仅返回 APPROVED 的摘要与 traceCode）。
6) 日志/审计：提交审核、审核通过/拒绝都写 audit_log（biz_type=TRACE）。

输出：Controller/Service/Mapper + 状态机校验 + 测试用例（未审核不可查、非法状态编辑、traceCode 不存在）。
```

#### 9.4.14 后端模块 Prompt：茶文化内容中心（Content，MVP）

```text
目标：实现开题报告强调的“茶文化传播/冲泡教程”内容模块（MVP 可上线版本）。

请实现：
1) content_article 表新增 type（CULTURE/BREW_TUTORIAL）、summary、tags_json。
2) 用户侧接口：
   - GET /api/content/articles?type=&keyword=&page=&pageSize=（分页）
   - GET /api/content/articles/{id}
   - 仅返回 PUBLISHED 内容
3) 管理端接口：
   - POST/PUT 发布前保存为 DRAFT
   - publish：DRAFT -> PUBLISHED
4) 内容长度边界：
   - title 1-120
   - summary <= 300
   - content 最大 20000 字符（MVP 先限长）
5) 可选：content_product_rel 关联表，实现商品详情推荐内容 GET /api/products/{id}/articles。
6) 日志/审计：管理员 publish 写 audit_log（biz_type=CONTENT）。

输出：Controller/Service/Mapper + SQL + 测试用例（分页、keyword 搜索、仅 PUBLISHED、publish 幂等）。
```

#### 9.4.15 后端模块 Prompt：Elasticsearch 商品搜索（Search，按开发顺序交付）

```text
目标：实现真正可用的商品搜索（ES），并按顺序交付，包含数据库层的增量同步设计。

开发顺序（必须按此推进）：
A. 数据库层
1) 增加 search_outbox_event 表（见文档），用于记录商品变更事件（UPSERT/DELETE）。
2) 在商品新增/编辑/上下架/SKU 库存关键变更的 Service 事务中：写入 outbox（同事务提交）。
   - 上架/下架：必须触发 UPSERT（更新 status）或 DELETE（按你的策略二选一，建议 UPSERT）。

B. Elasticsearch 索引与 Mapping
3) 新增 ES 客户端配置（RestHighLevelClient 或 Spring Data Elasticsearch）。
4) 创建索引 tea_product_v1（启动时自动创建或提供 admin reindex 接口）。
   - mapping：title/subtitle 用中文分词；origin/process/taste 也可分词；suggest 用 completion。
   - analyzer：优先 IK（analysis-ik 插件），不可用则用 smartcn。

C. 全量建索引
5) 实现 Full Reindex：从 MySQL 分页拉取 product + sku 汇总字段（minPrice/maxPrice/inStock），Bulk 写入 ES。
   - 建议提供：POST /api/admin/search/reindex（管理员专用）。

D. 增量同步
6) 后台任务轮询消费 outbox（每 1-5 秒一批）：
   - 取 NEW 且 next_retry_at<=now 的事件 -> 标记 PROCESSING
   - 调用 ES upsert/delete
   - 成功标 DONE
   - 失败：retry_count+1，next_retry_at 指数退避（如 1m/5m/30m），超过阈值标 FAILED
   - 日志：requestId + outboxId + productId + esIndex

E. 搜索 API
7) GET /api/search/products：
   - keyword 为空：走过滤 + sort
   - keyword 非空：multi_match(title^3, subtitle^2, origin, process, taste)
   - 过滤：categoryId/origin/process/priceRange/status=ON_SHELF
   - sort：relevance/sales_desc/price_asc/price_desc/newest
   - 高亮：title/subtitle（可选）
8) GET /api/search/suggest：ES completion/prefix suggest，keyword 1-20，返回最多 10。
9) GET /api/search/hot-words：从 user_event(SEARCH) 统计 topN，Redis 缓存 60 秒。

F. 降级与验收
10) ES 不可用：/search/products 可降级 MySQL like（仅兜底），同时打印 error 日志并记录监控。

请输出：
- 表结构 SQL：search_outbox_event
- ES index mapping JSON（含 analyzers）
- Reindex 任务（全量）与 Outbox 消费任务（增量）
- SearchController + SearchService（ES DSL 代码）
- 测试用例：keyword 为空/超长、ES 不可用降级、outbox 重试、上架下架后的可搜性
```

#### 9.4.16 后端模块 Prompt：营销活动与优惠券（Marketing，MVP 可选）

```text
目标：补齐开题中“缺乏针对性的营销与互动机制、用户粘性低”的痛点。MVP 先实现：优惠券领取与使用（每单 1 张）。

请实现：
1) coupon/user_coupon 表与基础 CRUD（管理端创建、启用/禁用；用户侧领取）。
2) 领取规则：
   - 每用户 per_user_limit（默认 1）
   - 券库存 total（扣减需并发安全）
   - 时间窗 start_at/end_at
3) 用户接口：
   - GET /api/marketing/coupons（可领券）
   - POST /api/marketing/coupons/{couponId}/claim
   - GET /api/marketing/my-coupons?status=UNUSED
4) 下单使用：在 POST /api/orders 增加入参 couponId（可选）
   - 校验用户拥有且 UNUSED 且未过期
   - 校验满足 threshold_amount
   - 计算优惠：payAmount = payAmount - discount_amount（不得小于 0）
   - 标记 user_coupon=USED，写 used_order_id
5) 幂等：claim 与 create order 均需幂等（避免重复扣券/重复扣库存）
6) 日志/审计：
   - 用户 claim 不落 audit_log（可仅业务日志）
   - 管理端启用/禁用与创建落 audit_log（biz_type=MARKETING）

输出：Controller/Service/Mapper + 事务设计 + 并发扣券 SQL + 测试用例（重复领取、过期、门槛不满足、并发库存）。
```

---

## 10. MVP 建议里程碑（最小可用闭环）
- 用户：注册登录 -> 商品浏览/搜索 -> 加购 -> 下单 -> mock 支付 -> 查看订单 -> 确认收货 -> 评论
- 商家：商品上架 -> 查看订单 -> 发货
- 管理员：评论审核
- 增强：售后、会员折扣、内容与溯源、ES 搜索与推荐

## 11. MVP 边界值/临界值/失败场景清单（用于自测与验收）

### 11.1 账号与安全
- **注册**
  - 手机号已存在 -> 40902
  - 密码长度 = 7（失败），=8（成功），=20（成功），=21（失败）
- **登录**
  - 连续失败 5 次 -> 锁定 10 分钟（42901）
  - token 过期 -> 40101

### 11.2 购物车
- `quantity` = 0（失败），=1（成功），=999（成功），=1000（失败）
- sku 下架/删除：购物车应标 invalid，不允许结算

### 11.3 订单
- items 为空（失败）
- 并发下单导致库存不足（40901）
- 已支付订单再次取消（失败，提示走售后）

### 11.4 支付
- 重复 createPayment 返回同 payNo（幂等）
- 重复 notify 不重复改状态（幂等）
- 金额不一致：拒绝并记录安全日志

### 11.5 售后
- applyAmount > subtotal（失败 40003）
- 同一 orderItem 重复申请未完结售后（失败 40904）

### 11.6 评论
- rating=0/6（失败），rating=1/5（成功）
- content 为空（失败），500（成功），501（失败）
- 未确认收货评论（失败）

### 11.7 溯源
- traceCode 不存在（40401）
- traceCode 未审核（对用户 40401）
- 商家在 PENDING 状态编辑溯源（失败 409xx）

### 11.8 推荐
- limit=0（失败），limit=1（成功），limit=50（成功），limit=51（失败）

### 11.9 内容
- type 非法（40001）
- article 未发布访问（40401 或返回空，MVP 建议 40401）
- title 为空（失败），120（成功），121（失败）

### 11.10 搜索
- products keyword 超长（失败），pageSize>100（失败）
- suggest keyword 为空/超长（失败）
- ES 不可用：触发降级（可用性）且打印告警日志
- sort 非法（失败 40001）

### 11.11 优惠券（可选）
- 重复领取同一券（失败 409xx 或返回已领取）
- 券过期仍领取（失败）
- 下单金额未达门槛仍使用（失败 400xx）

## 12. 关键接口请求/响应 JSON 示例（建议复制到 Apifox/Postman）

### 12.1 注册

`POST /api/auth/register`

```json
{"phone":"13800138000","password":"abc12345","username":"lilu"}
```

### 12.2 登录

`POST /api/auth/login`

```json
{"account":"13800138000","password":"abc12345"}
```

响应示例：

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 7200,
    "role": "USER",
    "userInfo": {
      "id": 1,
      "username": "lilu",
      "phone": "138****8000"
    }
  }
}
```

### 12.3 创建订单（建议携带幂等键）

请求头：
- `Idempotency-Key: 3c9f5a5e-5b2d-4a35-9a1a-1c0d2f6b8a12`

`POST /api/orders`

```json
{
  "addressId": 10,
  "couponId": 9001,
  "items": [
    {"skuId": 1001, "quantity": 2}
  ]
}
```

响应示例：

```json
{"code":0,"message":"OK","data":{"orderId":20001,"orderNo":"T202601040001","payAmount":199.00}}
```

### 12.4 创建支付单（MOCK）

`POST /api/payments/20001/create`

响应示例：

```json
{"code":0,"message":"OK","data":{"payNo":"P202601040001","channel":"MOCK","mockQr":"https://example.com/mockqr/P202601040001"}}
```

### 12.5 支付回调（MOCK notify，幂等）

`POST /api/payments/P202601040001/notify`

```json
{"amount":199.00,"paidAt":"2026-01-04T16:05:00"}
```

### 12.6 申请售后

`POST /api/after-sales`

```json
{
  "orderItemId": 30001,
  "type": "REFUND_ONLY",
  "reason": "不想要了",
  "amount": 99.50,
  "images": ["https://img.example.com/1.jpg"]
}
```

### 12.7 提交评论（待审核）

`POST /api/reviews`

```json
{"orderItemId":30001,"rating":5,"content":"口感很好，回甘明显","images":[]}
```

### 12.8 首页推荐

`GET /api/recommendations/home?limit=20`

响应示例：

```json
{"code":0,"message":"OK","data":{"items":[{"id":1,"title":"西湖龙井","minPrice":99.00,"coverUrl":"https://img.example.com/p1.jpg"}]}}
```

### 12.9 上报浏览行为（匿名也可）

`POST /api/events`

```json
{"type":"VIEW_PRODUCT","payload":{"productId":1,"from":"home"},"sessionId":"s-20260104-abc"}
```

### 12.10 溯源查询

`GET /api/traces/TRC202601040001`

响应示例：

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "traceCode": "TRC202601040001",
    "productId": 1,
    "batchNo": "2026-01-A01",
    "origin": "浙江·杭州·西湖区",
    "pickDate": "2026-03-20",
    "process": "炒青",
    "producer": "某某茶厂",
    "certificateUrl": "https://img.example.com/cert.jpg",
    "inspectionReportUrl": "https://img.example.com/report.jpg",
    "summary": "核心产区，春茶头采，检测合格"
  }
}
```

### 12.11 茶文化内容列表

`GET /api/content/articles?type=CULTURE&page=1&pageSize=10&keyword=龙井`

响应示例：

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "page": 1,
    "pageSize": 10,
    "total": 1,
    "items": [
      {
        "id": 501,
        "type": "CULTURE",
        "title": "龙井茶的历史与产区",
        "summary": "从西湖龙井的起源讲起，了解核心产区与等级划分",
        "coverUrl": "https://img.example.com/a1.jpg",
        "createdAt": "2026-01-04T16:20:00"
      }
    ]
  }
}
```

### 12.12 ES 商品搜索

`GET /api/search/products?keyword=龙井&categoryId=1&minPrice=50&maxPrice=300&sort=relevance&page=1&pageSize=10`

响应示例：

```json
{
  "code": 0,
  "message": "OK",
  "data": {
    "page": 1,
    "pageSize": 10,
    "total": 1,
    "items": [
      {
        "id": 1,
        "title": "西湖龙井",
        "minPrice": 99.00,
        "maxPrice": 199.00,
        "origin": "浙江·杭州",
        "process": "炒青",
        "salesCount": 120,
        "coverUrl": "https://img.example.com/p1.jpg"
      }
    ]
  }
}
```

### 12.13 ES 搜索联想

`GET /api/search/suggest?keyword=龙`

响应示例：

```json
{"code":0,"message":"OK","data":{"items":["龙井","龙井茶","龙井 2025 新茶"]}}
```

### 12.14 搜索热词

`GET /api/search/hot-words?limit=10`

响应示例：

```json
{"code":0,"message":"OK","data":{"items":["龙井","铁观音","普洱"]}}
```

### 12.15 领取优惠券（可选）

`POST /api/marketing/coupons/9001/claim`

响应示例：

```json
{"code":0,"message":"OK","data":{"userCouponId":12001,"couponId":9001,"status":"UNUSED"}}
```

---

## 13. 本地开发与运行（后端示例，Spring Boot）
- 依赖：JDK 8+、Maven 3.6+、MySQL 8、Redis 6（搜索增强可选：Elasticsearch 7.x + IK）。
- 数据库初始化：
  1) 创建库：`CREATE DATABASE tea_system DEFAULT CHARSET utf8mb4;`
  2) 按第 7 章表结构建表（可先建核心表：user/merchant/address/category/product/product_sku/product_image/cart/cart_item/order/order_item/payment_record/shipment/review/after_sale）。
  3) 可导入第 7 章示例数据（user_event/search_outbox_event）用于联调。
- 本地配置示例 `src/main/resources/application.yml`（如使用分 profile，可建 `application-dev.yml`）：
  ```yaml
  server:
    port: 8080
  spring:
    datasource:
      url: jdbc:mysql://localhost:3306/tea_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
      username: root
      password: root
    redis:
      host: localhost
      port: 6379
  jwt:
    secret: "replace-with-32-64-char-secret"
    expireHours: 2
  logging:
    level:
      org.example.teasystem: INFO
  ```
- 启动命令：
  - 开发运行：`mvn spring-boot:run`
  - 打包：`mvn clean package -DskipTests`
- 运行前检查清单：
  - MySQL/Redis 已启动，账号密码正确。
  - `jwt.secret` 已设置且长度 >=32。
  - Swagger：启动后访问 `http://localhost:8080/swagger-ui.html`（或 `/swagger-ui/index.html`）。

## 14. 代码目录速览（TeaSystem 后端）
```
src/main/java/org/example/teasystem
├─TeaSystemApplication.java        # 启动类
├─config/                         # 安全、Redis 等配置
├─common/                         # 常量、结果封装、异常、工具
├─controller/                     # 用户端接口
│  ├─admin/                       # 管理端接口
│  └─merchant/                    # 商家端接口
├─dto/                            # 请求/响应 DTO & VO
├─entity/                         # 数据库实体
├─mapper/                         # MyBatis Mapper（XML 同名于 resources/mapper）
├─security/                       # JWT 过滤器、鉴权工具
└─service/                        # 业务服务层
```

## 15. 前后端联调前 Checklist
- 账号准备：至少各创建 1 个 USER、MERCHANT、ADMIN；为 MERCHANT 绑定店铺。
- 商品与库存：创建 category/product/product_sku，确保 `status=ON_SHELF` 且 `stock>0`。
- 地址：为测试用户建 1 条默认地址。
- 订单流：下单 -> mock 支付 -> 商家发货 -> 用户确认收货 -> 评论；管理员审核评论。
- 售后流：已支付订单项发起售后 -> 商家审核 -> 模拟退款。
- 搜索：若未接 ES，保持 `/api/search/products` 有 MySQL 降级兜底。
- Apifox/Postman：导入第 12 章示例或 9.3 的集合。

## 16. 迭代优先级（2 周 MVP 建议）
1) **Week1**：认证/鉴权、类目商品列表+详情、购物车、订单创建+幂等、mock 支付、商家发货、管理员评论审核、日志/异常框架。
2) **Week2**：售后全链路、会员折扣（下单生效）、内容中心、溯源（商家提交/管理员审核/用户查询）、推荐规则版、搜索降级版（MySQL like；若可部署 ES 则做 Outbox+同步）。
3) **可选增强**：优惠券、营销活动、ES 完整链路、行为热词、数据看板。

## 17. 常见问题与排查
- 登录总是 401：确认 `Authorization: Bearer <token>`、JWT 过期时间、Spring Security 放行路径是否包含 `/api/auth/**`。
- 库存不足误报：检查 SKU `status=1`、商品 `ON_SHELF`，以及扣减 SQL 是否使用 `stock>=?` 乐观并发。
- 重复下单：确保前端传 `Idempotency-Key`，后端 Redis/表记录并返回首次订单。
- 支付回调幂等：回调入口首先按 `payNo` 查询 payment_record，若已 PAID 直接返回 success。
- 购物车显示失效：商品下架或 SKU 不可售时标记 invalid，引导用户调整。
- 搜索不可用：ES 挂掉时降级 MySQL like，并打印告警日志；恢复后执行 admin reindex。
