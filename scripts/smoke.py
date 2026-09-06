# -*- coding: utf-8 -*-
"""Mall-X 全链路冒烟测试（经网关 localhost:8090，前提：8 服务 + MySQL 已在跑）
链路：登录3身份 -> 游客浏览 -> 加购 -> 拆单下单 -> Mock支付回调 -> 发货 -> 确认收货 -> 退款 -> 提现 -> 报表
"""
import json
import urllib.request
import urllib.error
import urllib.parse
import sys

BASE = "http://localhost:8090"
PASS = 0
FAIL = 0


def call(method, path, body=None, token=None, params=None):
    url = BASE + path
    if params:
        url += "?" + urllib.parse.urlencode({k: v for k, v in params.items() if v is not None})
    data = json.dumps(body).encode() if body is not None else None
    req = urllib.request.Request(url, data=data, method=method)
    req.add_header("Content-Type", "application/json")
    if token:
        req.add_header("Authorization", "Bearer " + token)
    try:
        with urllib.request.urlopen(req, timeout=10) as resp:
            return json.loads(resp.read().decode())
    except urllib.error.HTTPError as e:
        try:
            return json.loads(e.read().decode())
        except Exception:
            return {"code": e.code, "message": str(e)}


def check(name, resp, expect_code=200):
    global PASS, FAIL
    ok = resp.get("code") == expect_code
    if ok:
        PASS += 1
        print(f"  ✔ {name}")
    else:
        FAIL += 1
        print(f"  ✘ {name} -> code={resp.get('code')} msg={resp.get('message')}")
    return resp


def main():
    global PASS, FAIL
    print("== 1. 登录三身份 ==")
    login_user = check("用户登录 user1", call("POST", "/api/auth/login", {"username": "user1", "password": "123456"}))
    login_seller = check("商家登录 seller1", call("POST", "/api/auth/login", {"username": "seller1", "password": "123456"}))
    login_admin = check("管理员登录 admin", call("POST", "/api/auth/login", {"username": "admin", "password": "123456"}))
    user_tk = login_user.get("data", {}).get("token")
    seller_tk = login_seller.get("data", {}).get("token")
    admin_tk = login_admin.get("data", {}).get("token")

    print("== 2. 游客浏览（无需 token）==")
    check("类目列表", call("GET", "/api/portal/categories"))
    check("广告位", call("GET", "/api/portal/adverts"))
    products = check("商品列表", call("GET", "/api/portal/products", params={"page": 1, "size": 12}))
    detail = check("商品详情", call("GET", "/api/portal/products/1"))
    sku_id = detail["data"]["skus"][0]["id"] if detail.get("data") and detail["data"].get("skus") else 1
    print(f"     用 SKU id={sku_id}")

    print("== 3. 购物车（add + merge 游客车）==")
    check("加入购物车", call("POST", "/api/portal/carts", {"skuId": sku_id, "count": 2}, token=user_tk))
    check("游客车合并", call("POST", "/api/portal/carts/merge", {"items": [{"skuId": sku_id, "count": 1}]}, token=user_tk))
    carts = check("购物车列表", call("GET", "/api/portal/carts", token=user_tk))
    print(f"     购物车 {len(carts.get('data') or [])} 行")

    print("== 4. 下单（拆单 + 库存预扣/条件扣）==")
    import uuid
    order_resp = check("下单", call("POST", "/api/order/create", {
        "reqId": str(uuid.uuid4()),
        "items": [{"skuId": sku_id, "count": 1}],
        "receiverName": "王小明", "receiverPhone": "13800138000",
        "receiverAddress": "广东省深圳市南山区科技园路1号", "fromCart": True,
    }, token=user_tk))
    order_no = (order_resp.get("data") or [None])[0]
    if not order_no:
        print("   ✘ 下单未返回订单号，链路终止（快照可查日志）")
        finalize()
        return
    print(f"     订单号 {order_no}")

    print("== 5. 支付（Mock 渠道 + 回调幂等链路）==")
    pay_resp = check("创建支付单", call("POST", "/api/pay/create", {"orderNo": order_no}, token=user_tk))
    pay_no = pay_resp.get("data")
    r = call("POST", "/api/pay/mock/callback", {"payNo": pay_no, "tradeNo": "T1", "amount": "0.01"})
    if r.get("code") == 400:
        PASS += 1
        print("  ✔ 金额不一致回调拒绝入账（符合预期 400）")
    else:
        FAIL += 1
        print(f"  ✘ 金额不一致回调未被拒 -> {r}")
    check("正确金额回调（支付成功）", call("POST", "/api/pay/mock/callback", {"payNo": pay_no, "tradeNo": "T1", "amount": "3299.00"}))
    check("支付状态查询", call("GET", f"/api/pay/status/{order_no}", token=user_tk))

    print("== 6. 商家发货 ==")
    check("商家订单列表", call("GET", "/api/merchant/orders", params={"page": 1}, token=seller_tk))
    check("发货", call("PUT", f"/api/merchant/orders/{order_no}/ship",
                       {"logisticsCompany": "顺丰速运", "trackingNo": "SF123456"}, token=seller_tk))

    print("== 7. 确认收货 + 退款 ==")
    check("确认收货", call("PUT", f"/api/order/{order_no}/receive", token=user_tk))
    check("申请退款（Mock 即时到账）", call("POST", "/api/pay/refund", {"orderNo": order_no, "reason": "冒烟测试"}, token=user_tk))

    print("== 8. 商家提现（balance 校验 + 驳回回补）==")
    check("提现申请", call("POST", "/api/merchant/withdrawal",
                           {"bankName": "招商银行", "accountNo": "6225881234567890", "holder": "张三", "amount": 100}, token=seller_tk))
    wr = call("GET", "/api/merchant/withdrawal", params={"page": 1}, token=seller_tk)
    wid = (wr.get("data", {}).get("records") or [{}])[0].get("id")
    if wid:
        check("管理员驳回想提现（余额回补）", call("PUT", f"/api/admin/withdrawals/{wid}/process",
                                             {"pass": False, "reason": "冒烟驳回"}, token=admin_tk))

    print("== 9. 报表（管理员看板）==")
    check("报表看板", call("GET", "/api/report/dashboard", token=admin_tk))
    check("商家本店看板", call("GET", "/api/report/dashboard", token=seller_tk))

    finalize()


def finalize():
    print(f"\n===== 冒烟结果：PASS {PASS} / FAIL {FAIL} =====")
    sys.exit(0 if FAIL == 0 else 1)


if __name__ == "__main__":
    main()
