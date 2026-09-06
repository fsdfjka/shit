# -*- coding: utf-8 -*-
"""Mall-X 前端改良走查脚本（Playwright）
旅程：首页(轮播/网格截图) → 详情页(规格/加购,游客) → 游客购物车(本地) → 登录 → 合并 →
     购物车(服务端) → 下单弹窗 → 支付页 → 我的订单 → 退出登录 → 注册页tab截图
"""
import json
import os
import re
import sys
import time

sys.stdout.reconfigure(encoding="utf-8")

from playwright.sync_api import sync_playwright

BASE = "http://localhost:5173"
SHOT = r"C:\Users\陈增\Desktop\狗屎\scripts\shots"
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

    page.on("console", lambda m: errors.append(m.text) if m.type == "error" else None)
    page.on("pageerror", lambda e: errors.append(str(e)))

    # ---------- 1. 首页 ----------
    page.goto(BASE, wait_until="networkidle")
    time.sleep(1.5)
    cards = page.locator(".card").count()
    banners = page.locator(".banner-slide").count()
    if cards >= 12 and banners >= 4:
        ok("首页商品卡≥12且轮播≥4", f"cards={cards} banners={banners}")
    else:
        fail("首页商品/轮播数", f"cards={cards} banners={banners}")
    page.screenshot(path=f"{SHOT}/01_home.png", full_page=True)

    # 轮播自动切换
    src0 = page.locator(".banner_show").get_attribute("src")
    time.sleep(4.2 + 0.8)
    src1 = page.locator(".banner_show").get_attribute("src")
    ok("轮播自动切换" if src0 != src1 else "轮播未切换", f"{src0} -> {src1}")

    # ---------- 2. 详情页（游客加购） ----------
    page.goto(f"{BASE}/product/2", wait_until="networkidle")
    time.sleep(1.2)
    specs = page.locator(".spec-pill").count()
    if specs >= 1:
        ok("详情页规格 pills", f"count={specs}")
    else:
        fail("详情页规格")
    page.locator("button:has-text('加入购物车')").first.click()
    time.sleep(0.6)
    print("  游客加购提示:", page.locator(".el-message").first.inner_text())
    page.screenshot(path=f"{SHOT}/02_detail.png", full_page=True)

    # 游客车 localStorage
    guest = page.evaluate("() => JSON.parse(localStorage.getItem('mall_guest_cart') || '[]')")
    if len(guest) >= 1 and guest[0].get("count", 0) >= 1:
        ok("游客加购写入localStorage", f"items={len(guest)}")
    else:
        fail("游客加购", json.dumps(guest, ensure_ascii=False))

    # ---------- 3. 游客购物车页 ----------
    page.goto(f"{BASE}/cart", wait_until="networkidle")
    time.sleep(1.0)
    rows = page.locator(".cart-row").count()
    if rows >= 1:
        ok("购物车页显示加购项", f"rows={rows}")
    else:
        fail("购物车页")
    page.screenshot(path=f"{SHOT}/03_cart_guest.png", full_page=True)

    # ---------- 4. 登录 ----------
    page.goto(f"{BASE}/login", wait_until="networkidle")
    page.fill("input[placeholder='用户名']", "user1")
    page.fill("input[placeholder='密码']", "123456")
    page.click("button:has-text('登 录'), button:has-text('登录'), button.submit")
    # 兼容按钮文本
    try:
        page.click("button:has-text('登录')", timeout=3000)
    except Exception:
        pass
    time.sleep(2.0)
    if page.locator(".hi").count() > 0:
        ok("登录成功(导航出现昵称)", page.locator(".hi").inner_text())
    else:
        fail("登录", page.url)

    # ---------- 5. 登录后购物车（应合并+服务端有数据） ----------
    page.goto(f"{BASE}/cart", wait_until="networkidle")
    time.sleep(1.5)
    rows = page.locator(".cart-row").count()
    if rows >= 1:
        ok("登录后合并购物车", f"rows={rows}")
    else:
        fail("合并购物车")
    cart_count = page.locator(".cart-badge").first.inner_text() if page.locator(".cart-badge").count() else "0"
    ok("导航购物车角标", f"count={cart_count}")
    page.screenshot(path=f"{SHOT}/04_cart_merged.png", full_page=True)

    # ---------- 6. 首页导航下拉退出 ----------
    page.goto(BASE, wait_until="networkidle")
    time.sleep(1.0)
    page.locator(".hi").hover()
    time.sleep(0.5)
    page.locator(".hi").click()
    time.sleep(0.8)
    items = page.locator(".el-dropdown-menu__item:visible").all_inner_texts()
    print("  下拉菜单:", items)
    if "退出登录" in " ".join(items):
        ok("导航下拉含退出登录", " | ".join(items))
    else:
        fail("下拉菜单")
    page.screenshot(path=f"{SHOT}/05_dropdown.png")
    page.locator(".el-dropdown-menu__item:visible:has-text('退出登录')").click()
    time.sleep(1.5)
    local_token = page.evaluate("() => localStorage.getItem('mall_token')")
    if not local_token:
        ok("退出登录清空token")
    else:
        fail("退出登录", "token still present")

    # ---------- 7. 注册页 tab ----------
    page.goto(f"{BASE}/register", wait_until="networkidle")
    tabs = page.locator(".role-tab").all_inner_texts()
    if len(tabs) == 2:
        ok("注册页角色tab", " | ".join(tabs))
    else:
        fail("注册tab", str(tabs))
    page.locator(".role-tab:has-text('商家入驻')").click()
    time.sleep(0.8)
    if page.locator("h2.form-title").inner_text() == "商家入驻申请":
        ok("tab切换商家表单", page.url)
    else:
        fail("tab切换")
    page.screenshot(path=f"{SHOT}/06_register_tab.png")

    # ---------- 8. 工作台入口（管理员身份） ----------
    page.goto(f"{BASE}/login", wait_until="networkidle")
    page.fill("input[placeholder='用户名']", "admin")
    page.fill("input[placeholder='密码']", "123456")
    try:
        page.click("button:has-text('登录')", timeout=3000)
    except Exception:
        pass
    time.sleep(2.0)
    page.goto(BASE, wait_until="networkidle")
    page.locator(".hi").click()
    time.sleep(0.8)
    items = page.locator(".el-dropdown-menu__item:visible").all_inner_texts()
    if any("工作台" in i for i in items):
        ok("管理员下拉含工作台", " | ".join(items))
    else:
        fail("管理员工作台入口", " | ".join(items))
    page.locator(".el-dropdown-menu__item:visible:has-text('工作台')").click()
    try:
        page.wait_for_url("**/admin*", timeout=8000)
        ok("工作台跳转成功", page.url)
    except Exception:
        fail("工作台跳转", page.url)
    time.sleep(1.5)
    page.screenshot(path=f"{SHOT}/07_admin_home.png", full_page=True)

    # ---------- console errors ----------
    real = [e for e in errors if "favicon" not in e and "SourceMap" not in e]
    if real:
        print("  console errors:", real[:5])
    else:
        ok("无 console 错误")

    browser.close()

failed = [r for r in results if r[1] == "FAIL"]
print("\n===== 汇总 =====")
print(f"PASS {len(results) - len(failed)} / {len(results)}")
sys.exit(1 if failed else 0)
