# Mall-X 微服务电商平台 · 开发环境配置指南

> 给第一次接手本项目的开发者：**如何在 0 到 1 跑起来** —— 中间件部署配置、账号信息、服务启动顺序、常见坑。

## 1. 架构总览

```
浏览器 (Vue3 前端 :5173)
   │  /api 代理
   ▼
mall-gateway (SpringCloud Gateway :8090)   ← 路由 + JWT 鉴权 + 限流
   │
   ├─ mall-auth-service     :8010   登录/注册/JWT
   ├─ mall-user-service     :8011   用户资料/地址/员工
   ├─ mall-merchant-service :8012   商家/店铺/提现/审核
   ├─ mall-product-service  :8013   类目/商品/SKU/广告/图片上传
   ├─ mall-order-service    :8014   购物车/下单/库存/物流
   ├─ mall-pay-service      :8015   支付单/回调/退款
   └─ mall-report-service   :8016   报表看板
```

服务注册发现：**Nacos**（所有服务 + 网关注册到同一 Nacos）。

## 2. 中间件配置清单（重要！）

| 中间件 | 地址 | 端口 | 用途 | 账号/密码 |
|---|---|---|---|---|
| **MySQL** | `localhost` | 3306 | 核心数据（库：`mall_x`，表 13 张） | `root` / `root`（各服务 yml 写死，按本机改） |
| **Nacos** | `192.168.193.131` | 8848（API）/ 9848 | 注册发现 + 配置中心 | 默认即用户名密码可空 |
| **Redis** | `192.168.193.131` | 6379 | 购物车/库存原子扣减/幂等 | 无密码 |
| **RocketMQ** | `192.168.193.131` | 9876（namesrv）/ 10909-10911（broker） | 订单超时取消延迟消息 | 无鉴权 |
| **MinIO** | `192.168.193.131` | 9000（API）/ 9001（控制台） | 图片上传（商品图/店铺 logo/收款码/广告图），桶 `mall-x` | `root` / `12345678` |
| **Sentinel** | `192.168.193.131` | 8858 | 限流熔断 Dashboard（可选） | 默认 |

> ⚠️ **连线主机说明**：Nacos/Redis/RocketMQ/MinIO 均部署在 **192.168.193.131**（Docker 容器），与开发机同网段即可访问。MySQL 在开发机 `localhost`。
> ⚠️ **MinIO 桶**：代码自动创建 `mall-x` 桶 + 公开读策略（`s3:GetObject`），无需手动建桶。

## 3. 一键部署中间件（docker compose）

```bash
cd docker
docker compose up -d          # 启动 nacos/redis/rocketmq(3容器)/sentinel
# 查看地址
docker compose ps
```

容器清单（`docker/docker-compose.yml`）：

| 容器 | 镜像 | 端口映射 |
|---|---|---|
| mall-nacos | nacos/nacos-server:v2.3.2 | 8848, 9848 |
| mall-redis | redis:7.2 | 6379 |
| mall-rocketmq-namesrv | apache/rocketmq:5.1.4 | 9876 |
| mall-rocketmq-broker | apache/rocketmq:5.1.4 | 10909, 10911 |
| mall-rocketmq-dashboard | apacherocketmq/rocketmq-dashboard | 8180 |
| mall-sentinel-dashboard | bluereach/sentinel-dashboard:1.8.6 | 8858 |

> **RocketMQ broker 的坑**：broker 容器需挂载 `broker.conf` 并写入 `brokerIP1=<宿主机IP>`（见 `docker/broker.conf`，默认 192.168.193.131），否则延迟消息发送失败。

## 4. 初始化数据库

```bash
mysql -uroot -proot -e "CREATE DATABASE IF NOT EXISTS mall_x DEFAULT CHARACTER SET utf8mb4;"
mysql -uroot -proot mall_x < docs/init.sql
```

`docs/init.sql` 含建表（13 表）+ 种子数据（商品 13 / SKU 23 / 广告 4 / 商家 2 / 用户 3）。

**演示账号**（密码均为 `123456`）：

| 账号 | 角色 | 说明 |
|---|---|---|
| `admin` | 平台管理员 | 后台 `/admin` |
| `user1` | 普通用户 | 商城前台，有收货地址/历史订单 |
| `seller1` / `seller2` | 商家 | 后台 `/admin`（type=2 菜单），已通过审核 |
| `newseller` | 商家（待审） | 用于演示入驻审核流程 |

## 5. 启动后端

```bash
cd mall-backend
mvn -pl mall-common install -DskipTests   # 先装公共依赖
mvn -pl mall-auth-service      package -DskipTests
mvn -pl mall-user-service      package -DskipTests
...（任意服务同理打包；或 mvn package 全量）
# 逐个启动（按端口，无需固定顺序；网关在服务就绪后启动）
java -jar mall-auth-service/target/mall-auth-service-0.1.0-SNAPSHOT.jar
java -jar mall-user-service/target/mall-user-service-0.1.0-SNAPSHOT.jar
java -jar mall-merchant-service/target/mall-merchant-service-0.1.0-SNAPSHOT.jar
java -jar mall-product-service/target/mall-product-service-0.1.0-SNAPSHOT.jar
java -jar mall-order-service/target/mall-order-service-0.1.0-SNAPSHOT.jar
java -jar mall-pay-service/target/mall-pay-service-0.1.0-SNAPSHOT.jar
java -jar mall-report-service/target/mall-report-service-0.1.0-SNAPSHOT.jar
java -jar mall-gateway/target/mall-gateway-0.1.0-SNAPSHOT.jar
```

> 启动后检查：`curl localhost:8090/api/portal/categories` 应返回 200。

> **生产/其他开发者改 IP**：全部服务 yml 里 Nacos/Redis/MQ/MinIO 地址为 192.168.193.131——如换机器需全局替换（或用环境变量注入）。

## 6. 启动前端

```bash
cd mall-frontend
npm install
npm run dev      # http://localhost:5173
```

前端 `/api` 代理到网关 `localhost:8090`（vite.config.ts）。**前端渲染图片**：商品图等从 MinIO 返回 URL（`http://192.168.193.131:9000/mall-x/...`），浏览器需能直连该地址。

## 7. 各个服务 yml 需要改什么（接手清单）

| 服务 | Nacos | MySQL | Redis | RocketMQ | MinIO |
|---|---|---|---|---|---|
| mall-auth-service | ✅ 192.168.193.131 | ✅ localhost | - | - | - |
| mall-user-service | ✅ | ✅ | - | - | - |
| mall-merchant-service | ✅ | ✅ | - | - | ✅ 图片上传 |
| mall-product-service | ✅ | ✅ | - | - | ✅ 图片上传 |
| mall-order-service | ✅ | ✅ | ✅ 192.168.193.131 | ✅ 192.168.193.131:9876 | - |
| mall-pay-service | ✅ | ✅ | ✅(分布式锁 Redisson) | - | - |
| mall-report-service | ✅ | ✅ | - | - | - |
| mall-gateway | ✅ | - | - | - | - |

> MinIO 配置片段（product/merchant 的 `mall.minio.*`）：
> ```yaml
> mall:
>   minio:
>     endpoint: http://192.168.193.131:9000
>     access-key: root
>     secret-key: "12345678"
>     bucket: mall-x
> ```

## 8. 常用验证脚本

```bash
python scripts/smoke.py             # 后端全链路冒烟（23 项：登录3身份/购物车/下单/支付/退款/提现/报表）
pip install playwright && python -m playwright install chromium
python scripts/playwright_walk.py   # 前端走查 15 项
python scripts/playwright_walk2.py  # 前端走查第 2 波 9 项（分页/订单/个人中心/结算支付）
python scripts/gen_images.py        # 重新生成商品/广告占位图（public/img）
```

## 9. 常见问题（坑）

1. **Port 8014 already in use**：order-service 已被启动(多个实例),杀掉旧进程:`netstat -ano | grep ":8014"` → `taskkill /PID <pid> /F`
2. **下单报"库存暂不可用"**：Redis 缺 `stock:{skuId}` key。order-service 每次启动会自动预热；运行中新上架的 SKU 由定时补预热(2 分钟)兜底,等 2 分钟即可。
3. **审核/提现报"商家不存在/用户已存在"**：雪花 ID 19 位超出 JS Number 精度 —— 前端已用 `json-bigint` 安全解析,无需处理;若自己写前端请沿用该方案。
4. **上传图片报"系统繁忙"**：文件大于 20MB(后端限制)或网关限流(产品接口 QPS 默认 20,可用 Sentinel Dashboard 调整)。
5. **轮播图/广告图不显示**：MinIO 桶需要公开读策略(代码自动设置)或浏览器能访问 9000 端口。
6. **注册"系统繁忙"**：表单校验失败(如缺电话)时请直接看提示 —— 校验异常已透传真实字段信息。

---

## 附：目录结构速览

```
狗屎/
├── README.md              ← 本文件
├── mall-frontend/         Vue3+TS 前端（商城 + 管理后台）
├── mall-backend/          微服务后端（7 服务 + gateway + common）
│   ├── mall-common/       ← 公共库（Result/异常/JWT/MinIO 上传组件/幂等）
│   └── mall-<域>-service/
├── docker/docker-compose.yml   中间件编排
├── docs/                  init.sql · requirements · architecture · api · 改良计划
└── scripts/               smoke.py · playwright 走查 · gen_images.py
```
