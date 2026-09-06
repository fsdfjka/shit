# Mall-X 部署说明（里程碑 9.2）

> 环境约定：中间件主机 `192.168.193.131`（Nacos/Redis/RocketMQ/Sentinel Dashboard 均容器化于此）；
> MySQL 按约定在 `localhost:3306`（库 `mall_x`，脚本 docs/init.sql 已建库）。

## 1. 中间件启动

```bash
# 目标主机 192.168.193.131 上
git clone <repo> && cd docker
docker compose up -d
```

| 组件 | 端口 | 说明 |
|---|---|---|
| Nacos | 8848 / 9848 | 服务注册+配置中心（standalone） |
| Redis | 6379 | 无密码（演示）；生产按 compose 注释启用 auth 后同步各服务 `spring.data.redis.password` |
| RocketMQ NameServer / Broker | 9876 / 10909 / 10911 | brokerIP1 已指宿主 IP（broker.conf） |
| RocketMQ Dashboard | 8180 | 可视化 Topic/消费 观察延迟消息 |
| Sentinel Dashboard | 8858 | 服务侧规则热更新用（当前用静态规则，可选） |

## 2. 数据库

```bash
# 在 MySQL localhost 执行（或任何数据库主机）
mysql -u root -p < docs/init.sql
# 各服务 application.yml 的 datasource.password 改为本机实际密码
```

## 3. 微服务打包与镜像

```bash
cd mall-backend
mvn -pl mall-auth-service,mall-user-service,mall-merchant-service,mall-product-service,mall-order-service,mall-pay-service,mall-report-service,mall-gateway -am clean package -DskipTests

# 逐服务构建镜像（JAR 路径按模块名替换）
docker build -t mall/mall-auth-service:latest -f ../docker/Dockerfile \
  --build-arg JAR=mall-auth-service/target/mall-auth-service-0.1.0-SNAPSHOT.jar .
```

## 4. 启动顺序（依赖 Nacos 可用）

1. `mall-gateway`（8090）—— 也可最后启动
2. `mall-auth-service`（8010）→ `mall-user-service`（8011）→ `mall-merchant-service`（8012）→
   `mall-product-service`（8013）→ `mall-order-service`（8014，需 RBCF SMQ）→
   `mall-pay-service`（8015）→ `mall-report-service`（8016）
3. 健康检查：`curl http://<host>:<port>/ping`（各服务自带）

## 5. 前端

```bash
cd mall-frontend
npm install && npm run build      # 产物 dist/
# dev 模式：npm run dev（vite 代理 /api -> localhost:8090）
# 生产：把 dist/ 部署到任意静态服务，接口地址由 VITE_API_BASE 指向网关
```

## 6. 演示账密（docs/init.sql 种子）

| 角色 | 账号 | 密码 |
|---|---|---|
| 平台管理员 | admin | 123456 |
| 用户 | user1 | 123456 |
| 商家 | seller1 / seller2 | 123456 |

## 7. 大纲任务 14 延迟消息演示参数

`mall-order-service` 的 `mall.order.timeout-delay-level`：
- `16` = 30 分钟（语义值）
- `3` = 10 秒（演示快速观察超时取消链路：下单 → 等 10 秒 → 状态机自动取消 + 库存回补）
