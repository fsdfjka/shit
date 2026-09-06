# Mall-X 接口文档（核心端点清单）

> 统一约定：经网关 `http://localhost:8090` 访问；响应体 `{code, message, data}`（code=200 成功）；
> 认证头 `Authorization: Bearer <token>`；网关校验后向下游转发 `X-User-Id / X-User-Type / X-Username`。
> 网关鉴权规则：游客白名单（登录注册/商城浏览），其余按前缀匹配 type（见 architecture.md §4）。

## 认证 /api/auth/**
| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| POST | /api/auth/login | 白名单 | 登录，返回 {token, type, id, username} |
| POST | /api/auth/register | 白名单 | 用户注册 |
| POST | /api/auth/register/merchant | 白名单 | 商家入驻申请（applyStatus=0 待审核） |

## 商城前台 /api/portal/**
| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET | /api/portal/categories | 游客 | 类目列表 |
| GET | /api/portal/adverts | 游客 | 启用中的广告位 |
| GET | /api/portal/products | 游客 | 商品分页 `?page&size&categoryId&keyword`，含 minPrice/shopName |
| GET | /api/portal/products/{id} | 游客 | 商品详情 + SKU 全量 |
| GET | /api/portal/carts | type=0 | 我的购物车（联表） |
| POST | /api/portal/carts | type=0 | 加入购物车 {skuId, count} |
| POST | /api/portal/carts/merge | type=0 | 游客购物车合并（localStorage→线上） |
| PUT | /api/portal/carts/{skuId}/count | type=0 | 修改数量 |
| PUT | /api/portal/carts/{skuId}/checked | type=0 | 勾选/取消 |
| DELETE | /api/portal/carts/{skuId} | type=0 | 删除行 |

## 用户域 /api/user/**（type=0）、/api/admin/users/**（type=1）
| 方法 | 路径 | 说明 |
|---|---|---|
| GET/PUT | /api/user/profile | 个人资料 |
| GET/POST/PUT/DELETE | /api/user/addresses(/{id}) | 收货地址 CRUD（默认地址单事务） |
| GET/POST | /api/admin/users | 员工列表/创建 |
| PUT | /api/admin/users/{id}/status?status= | 启用/禁用 |

## 商家域 /api/merchant/**（type=2）
| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /api/merchant/profile、/api/merchant/shop | 商家资料/店铺（含 balance） |
| PUT | /api/merchant/shop | 店铺设置（名称/logo/介绍/地址/收款码） |
| POST/GET | /api/merchant/withdrawal | 提现申请（原子扣余额）/ 记录 |

## 商品域
| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| GET/POST/PUT/DELETE | /api/admin/categories(/{id}) | type=1 | 类目管理（删除校验） |
| GET/POST/PUT/DELETE | /api/admin/adverts(/{id}) | type=1 | 广告管理 |
| GET | /api/product/list | type=2 | 我的商品分页 |
| POST/PUT/DELETE | /api/product(/{id}) | type=2 | 商品保存（含 SKU 行）/删除 |
| PUT | /api/product/{id}/status?status= | type=2 | 上架/下架 |

## 订单域
| 方法 | 路径 | 权限 | 说明 |
|---|---|---|---|
| POST | /api/order/create | type=0 | 下单（items+收货快照+reqId），返回拆单 orders |
| GET | /api/order/list?status&page&size | type=0 | 我的订单（含明细快照） |
| GET | /api/order/{orderNo} | type=0 | 订单详情 |
| PUT | /api/order/{orderNo}/cancel | type=0 | 取消（0→4+关支付单+回补库存） |
| PUT | /api/order/{orderNo}/receive | type=0 | 确认收货（2→3） |
| GET/PUT | /api/merchant/orders(/{orderNo}/ship) | type=2 | 店铺订单/发货（1→2+物流信息） |
| GET/PUT | /api/admin/orders(/{orderNo}/ship) | type=1 | 全局订单/代发货 |

## 支付域 /api/pay/**（type=0）、/api/admin/refunds（type=1）
| 方法 | 路径 | 说明 |
|---|---|---|
| POST | /api/pay/create | 创建支付单（返回 payNo；Mock 渠道） |
| POST | /api/pay/mock/callback | 模拟渠道回调（金额核对+锁+唯一索引+状态机；真实沙箱见 AlipayGatewayClient） |
| GET | /api/pay/status/{orderNo} | 支付状态（轮询） |
| POST | /api/pay/refund | 申请退款（out_request_no 幂等；订单 1→5→6；余额扣回） |
| GET | /api/admin/refunds?status | 退款记录 |

## 报表 /api/report/**（type=1 全平台 / type=2 传 merchantId 本店）
| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /api/report/dashboard?merchantId= | 营收/进账/出账/入驻/近7日趋势/进出账流水 |

## 公共
| 方法 | 路径 | 说明 |
|---|---|---|
| GET | /api/.../ping | 各服务存活检查（直接访问服务端口） |

## 占位与 TODO（实现文档同步标注）
- 支付宝真实沙箱（密钥留白）：`AlipayGatewayClient` 接口 + `application.yml` 密钥位注释
- 压测/JUnit：里程碑 9.1 联调阶段（大纲任务 15）
