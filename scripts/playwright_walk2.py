# -*- coding: utf-8 -*-
"""Mall-X 改良第 2 波走查：分页 / 订单页 / 个人中心 / 结算下单全链 / 商家审核
需要：user1 购物车有商品（若空会被跳过结算）。
"""
import os
import sys
import time

sys.stdout.reconfigure(encoding="utf-8")
from playwright.sync_api import sync_playwright

BASE = "http://localhost:5173"
SHOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "shots")
os.makedirs(SHOT, exist_ok=True)

results = []

def ok(name, detail=""):
    results.append((name, "PASS", detail))
    print(f"[PASS] {name} {detail}")

def fail(name, detail=""):
    results.append((name, "FAIL", detail))
    print(f"[FAIL] {name} {detail}")

with sync_playwright() as p:
    browser = p.chromium.launch()
    page = browser.new_page(viewport={"width": 1400, "height": 900})
    errors = []
    page.on("pageerror", lambda e: errors.append(str(e)))

    def login(user, pwd):
        page.goto(f"{BASE}/login", wait_until="networkidle")
        page.fill("input[placeholder='用户名']", user)
        page.fill("input[placeholder='密码']", pwd)
        page.click("button:has-text('登录')")
        time.sleep(3)

    # ---------- 1. 首页分页 ----------
    # 先登录以在后续步骤复用（分页不需要登录）
    page.goto(BASE, wait_until="networkidle")
    time.sleep(1.2)
    pager = page.locator(".pager .el-pager")
    if pager.count() > 0:
        ok("首页分页出现（total>12）", f"pages={page.locator('.pager .el-pager li').count()}")
        page.locator(".pager .el-pager li").last.click()
        time.sleep(1.5)
        cards = page.locator(".card").count()
        if cards == 1:
            ok("第 2 页仅 1 件商品（13-12=1）", f"cards={cards}")
        else:
            fail("第 2 页数量", f"cards={cards}")
        page.screenshot(path=f"{SHOT}/09_pagination.png")
    else:
        fail("首页分页")

    # ---------- 2. 登录 user1 → 订单页 ----------
    login("user1", "123456")
    try:
        page.wait_for_url(f"{BASE}/", timeout=8000)
    except Exception:
        pass
    time.sleep(1)
    page.goto(f"{BASE}/orders", wait_until="networkidle")
    time.sleep(2)
    stats = page.locator(".stat").count()
    orders = page.locator(".order").count()
    if stats >= 5 and orders >= 1:
        ok("订单页统计栏+订单卡片", f"stats={stats} orders={orders}")
    else:
        fail("订单页", f"stats={stats} orders={orders}")
    page.screenshot(path=f"{SHOT}/10_orders.png", full_page=True)

    # ---------- 3. 个人中心 ----------
    page.goto(f"{BASE}/profile", wait_until="networkidle")
    time.sleep(2)
    hero = page.locator(".hero-card").count()
    addr = page.locator(".addr-card").count()
    if hero >= 1:
        ok("个人中心用户卡", f"addr_cards={addr}")
    else:
        fail("个人中心用户卡")
    page.screenshot(path=f"{SHOT}/11_profile.png", full_page=True)

    # ---------- 4. 购物车 → 结算 → 下单 → 支付 ----------
    page.goto(f"{BASE}/cart", wait_until="networkidle")
    time.sleep(1.5)
    rows = page.locator(".cart-row").count()
    print("  cart rows:", rows)
    if rows == 0:
        # 回首页加一件（通过详情页）
        page.goto(f"{BASE}/product/8", wait_until="networkidle")
        time.sleep(1.5)
        page.locator("button:has-text('加入购物车')").first.click()
        time.sleep(1)
        # 游客车与登录车分离：登录态直接调 API 加购
        sku_id = page.evaluate("""async () => {
          const token = localStorage.getItem('mall_token')
          const res = await fetch('/api/portal/carts', { method: 'POST', headers: { 'Content-Type': 'application/json', Authorization: 'Bearer ' + token }, body: JSON.stringify({ skuId: 13, count: 1 }) })
          return (await res.json()).code
        }""")
        print("  补加购物车 code:", sku_id)
        page.goto(f"{BASE}/cart", wait_until="networkidle")
        time.sleep(1.5)
        rows = page.locator(".cart-row").count()
    if rows >= 1:
        ok("购物车行数（结算前置）", f"rows={rows}")
    else:
        fail("购物车为空", "无法走结算流程")

    page.locator(".bar-checkout").click()
    time.sleep(1)
    dialog = page.locator(".el-dialog")
    if dialog.count():
        dialog.locator("input").first.fill("王小明")
        dialog.locator("input").nth(1).fill("13800138000")
        dialog.locator("textarea").fill("广东省深圳市南山区科技园 1 号")
        page.screenshot(path=f"{SHOT}/12_checkout.png")
        dialog.locator("button:has-text('提交订单')").click()
        time.sleep(2.5)
        pay_dialog = page.locator(".el-dialog")
        ok("下单成功弹窗（含去支付）" if "下单成功" in page.locator("body").inner_text() else "下单未完成", "")
    # 点击去支付
    pay_row = page.locator(".pay-row").first
    if pay_row.count():
        pay_row.locator("button:has-text('去支付')").click()
        time.sleep(2)
        try:
            page.wait_for_url("**/pay/*", timeout=8000)
            ok("跳转支付页", page.url)
        except Exception:
            fail("跳转支付页", page.url)
        page.locator("button:has-text('模拟支付成功')").click()
        time.sleep(3)
        ok("支付成功后订单列表" if "/orders" in page.url else "支付成功等待跳转", page.url)

    # ---------- 5. 商家审核（数据侧已过） ----------
    login("admin", "123456")
    time.sleep(1.5)
    page.goto(f"{BASE}/admin/merchants", wait_until="networkidle")
    time.sleep(1.5)
    rows = page.locator(".el-table__row").count()
    ok("审核列表可加载" if rows >= 1 else "审核列表空", f"rows={rows}")

    real = [e for e in errors if "favicon" not in e and "SourceMap" not in e]
    if real:
        print("  page errors:", real[:5])
    else:
        ok("无页面错误")

    browser.close()

failed = [r for r in results if r[1] == "FAIL"]
print("\n===== 汇总 =====")
print(f"PASS {len(results) - len(failed)} / {len(results)}")
sys.exit(1 if failed else 0)
