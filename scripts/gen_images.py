# -*- coding: utf-8 -*-
"""Mall-X 商品图/广告图/类目图标生成器（Pillow）
统一风格：浅灰渐变底 + 白色产品剪影 + 地面阴影；广告图为橙红渐变横幅。
输出到 mall-frontend/public/img/【product|ad|cat】/
"""
import os
from PIL import Image, ImageDraw, ImageFont

ROOT = os.path.join(os.path.dirname(os.path.abspath(__file__)), "..", "mall-frontend", "public", "img")
P_DIR = os.path.join(ROOT, "product")
A_DIR = os.path.join(ROOT, "ad")
C_DIR = os.path.join(ROOT, "cat")
for d in (P_DIR, A_DIR, C_DIR):
    os.makedirs(d, exist_ok=True)

W, H = 1200, 900  # 商品图 4:3

# ---------- 渐变与基础 ----------
def vert_grad(draw, box, c1, c2):
    x1, y1, x2, y2 = box
    for y in range(y1, y2):
        t = (y - y1) / max(y2 - y1, 1)
        col = tuple(int(c1[i] + (c2[i] - c1[i]) * t) for i in range(3))
        draw.line([(x1, y), (x2, y)], fill=col)

def hgrad(draw, box, c1, c2):
    x1, y1, x2, y2 = box
    for x in range(x1, x2):
        t = (x - x1) / max(x2 - x1, 1)
        col = tuple(int(c1[i] + (c2[i] - c1[i]) * t) for i in range(3))
        draw.line([(x, y1), (x, y2)], fill=col)

def shadow(draw, cx, cy, rx, ry=28, alpha=46):
    img = draw._image
    ov = Image.new("RGBA", img.size, (0, 0, 0, 0))
    d = ImageDraw.Draw(ov)
    d.ellipse((cx - rx, cy - ry, cx + rx, cy + ry), fill=(30, 35, 50, alpha))
    img.alpha_composite(ov)

def rounded(draw, box, r, fill, outline=None, width=4):
    x1, y1, x2, y2 = box
    # 圆角矩形手工绘制（Pillow 的 rounded_rectangle 亦可，这里兼容）
    draw.rounded_rectangle(box, radius=r, fill=fill, outline=outline, width=width)

def grayscale_flat():
    img = Image.new("RGBA", (W, H), (255, 255, 255, 255))
    d = ImageDraw.Draw(img)
    vert_grad(d, (0, 0, W, H), (246, 247, 250), (225, 228, 235))
    return img, d

WHITE = (255, 255, 255)
INK = (170, 175, 190)
DARK = (200, 205, 216)

# ---------- 产品绘制 ----------
def phone(d):
    shadow(d, 600, 760, 210)
    rounded(d, (430, 120, 770, 760), 36, WHITE, INK, 5)
    rounded(d, (462, 152, 738, 700), 24, (238, 241, 247))
    d.ellipse((620, 128, 660, 168), fill=(210, 214, 224))
    d.ellipse((586, 128, 610, 152), fill=(214, 218, 228))
    # 屏幕内容
    rounded(d, (486, 176, 714, 640), 16, (40, 46, 66))
    d.rectangle((486, 176, 714, 360), fill=(52, 60, 88))
    d.ellipse((520, 210, 680, 310), fill=(64, 74, 106))
    d.line((486, 370, 714, 370), fill=(64, 74, 106), width=6)

def earphone(d):
    shadow(d, 600, 740, 190)
    # 左侧耳机
    d.ellipse((380, 250, 560, 470), fill=WHITE, outline=INK, width=5)
    rounded(d, (430, 430, 520, 760), 44, WHITE, INK, 5)
    d.ellipse((410, 290, 530, 400), fill=(238, 241, 247))
    # 右侧耳机
    d.ellipse((640, 250, 820, 470), fill=WHITE, outline=INK, width=5)
    rounded(d, (680, 430, 770, 760), 44, WHITE, INK, 5)
    d.ellipse((670, 290, 790, 400), fill=(238, 241, 247))

def tshirt(d):
    shadow(d, 600, 760, 260)
    d.polygon([(600, 190), (760, 150), (900, 260), (830, 480), (760, 430), (760, 760), (440, 760), (440, 430), (370, 480), (300, 260), (440, 150)], fill=WHITE, outline=INK)
    d.arc((540, 150, 660, 320), start=0, end=180, fill=DARK, width=8)
    d.ellipse((560, 240, 640, 300), fill=(238, 241, 247))

def watch(d):
    shadow(d, 600, 760, 160)
    rounded(d, (540, 130, 580, 560), 20, WHITE, INK, 4)
    rounded(d, (620, 130, 660, 560), 20, WHITE, INK, 4)
    rounded(d, (440, 290, 760, 610), 48, WHITE, INK, 5)
    d.ellipse((470, 320, 730, 580), fill=(238, 241, 247))
    d.line((600, 450, 600, 380), fill=DARK, width=10)
    d.line((600, 450, 650, 480), fill=DARK, width=10)
    d.ellipse((580, 430, 620, 470), fill=(190, 196, 210))

def charger(d):
    shadow(d, 600, 750, 210)
    d.ellipse((390, 220, 810, 620), fill=WHITE, outline=INK, width=5)
    d.ellipse((430, 260, 770, 580), fill=(238, 241, 247))
    rounded(d, (520, 330, 680, 500), 24, WHITE, INK, 4)
    d.line((495, 620, 450, 760), fill=DARK, width=12)
    rounded(d, (405, 760, 495, 790), 14, DARK)

def speaker(d):
    shadow(d, 600, 760, 240)
    rounded(d, (370, 210, 830, 700), 52, WHITE, INK, 5)
    for cx in (510, 690):
        for cy in (330, 450, 570):
            d.ellipse((cx - 52, cy - 52, cx + 52, cy + 52), fill=(232, 235, 242))

def keyboard(d):
    shadow(d, 600, 730, 280, ry=34)
    rounded(d, (320, 330, 880, 640), 26, WHITE, INK, 5)
    for r in range(3):
        for c in range(10):
            x1 = 356 + c * 50
            y1 = 366 + r * 54
            rounded(d, (x1, y1, x1 + 42, y1 + 40), 8, (228, 232, 240))
    rounded(d, (356, 520, 596, 570), 10, (228, 232, 240))
    rounded(d, (620, 520, 844, 570), 10, (228, 232, 240))

def cap(d):
    shadow(d, 600, 720, 240)
    d.pieslice((420, 260, 780, 620), 180, 360, fill=WHITE, outline=INK, width=5)
    d.ellipse((380, 560, 820, 760), fill=WHITE, outline=INK, width=5)
    d.arc((450, 300, 750, 580), 180, 360, fill=DARK, width=6)

def scarf(d):
    shadow(d, 600, 780, 250, ry=26)
    rounded(d, (360, 520, 840, 660), 30, WHITE, INK, 5)
    rounded(d, (520, 640, 680, 820), 30, WHITE, INK, 5)
    rounded(d, (560, 560, 640, 660), 24, (238, 241, 247))

def hoodie(d):
    shadow(d, 600, 770, 260)
    d.polygon([(600, 180), (780, 140), (920, 300), (850, 500), (760, 440), (760, 780), (440, 780), (440, 440), (350, 500), (280, 300), (420, 140)], fill=WHITE, outline=INK)
    d.pieslice((520, 140, 680, 300), 180, 360, fill=(238, 241, 247), outline=DARK)
    d.line((560, 300, 530, 420), fill=DARK, width=8)
    d.line((640, 300, 670, 420), fill=DARK, width=8)
    rounded(d, (450, 560, 750, 700), 40, (238, 241, 247))

def slipper(d):
    shadow(d, 600, 740, 280, ry=30)
    d.ellipse((340, 520, 860, 760), fill=WHITE, outline=INK, width=5)
    d.pieslice((380, 120, 820, 560), 0, 180, fill=WHITE, outline=INK, width=5)
    d.arc((400, 160, 800, 540), 0, 180, fill=DARK, width=6)

def backpack(d):
    shadow(d, 600, 770, 220)
    rounded(d, (540, 130, 720, 200), 30, WHITE, INK, 5)
    rounded(d, (400, 200, 800, 740), 60, WHITE, INK, 5)
    rounded(d, (460, 640, 740, 760), 30, (238, 241, 247))
    rounded(d, (450, 320, 750, 600), 30, (238, 241, 247))

PRODUCTS = [
    ("phone", phone), ("earphone", earphone), ("tshirt", tshirt),
    ("watch", watch), ("charger", charger), ("speaker", speaker),
    ("keyboard", keyboard), ("cap", cap), ("scarf", scarf),
    ("hoodie", hoodie), ("slipper", slipper), ("backpack", backpack),
]

for name, fn in PRODUCTS:
    img, d = grayscale_flat()
    fn(d)
    img.convert("RGB").save(os.path.join(P_DIR, f"{name}.jpg"), quality=92)

# ---------- 广告横幅（1200x400 橙红渐变 + 白色标题） ----------
def ad_banner(fname, title, sub, c1=(255, 80, 0), c2=(226, 62, 0)):
    aw, ah = 1200, 400
    img = Image.new("RGBA", (aw, ah), (255, 255, 255, 255))
    d = ImageDraw.Draw(img)
    hgrad(d, (0, 0, aw, ah), c1, c2)
    try:
        f_big = ImageFont.truetype("C:/Windows/Fonts/msyhbd.ttc", 84)
        f_sub = ImageFont.truetype("C:/Windows/Fonts/msyh.ttc", 38)
    except Exception:
        f_big = ImageFont.load_default()
        f_sub = f_big
    d.text((80, 120), title, font=f_big, fill=(255, 255, 255))
    d.text((84, 250), sub, font=f_sub, fill=(255, 231, 220))
    # 右侧圆环装饰
    d.ellipse((920, -140, 1340, 300), outline=(255, 255, 255), width=14)
    d.ellipse((1000, 260, 1180, 440), outline=(255, 200, 170), width=10)
    img.convert("RGB").save(os.path.join(A_DIR, fname), quality=92)

ad_banner("ad1.jpg", "开学季数码专场", "旗舰直降 · 满 199 减 30")
ad_banner("ad2.jpg", "新品服饰上市", "秋冬上新 · 两件 8 折", (24, 42, 84), (18, 32, 64))
ad_banner("ad3.jpg", "数码周大促", "蓝牙耳机 Pro 限时秒杀", (255, 120, 0), (230, 80, 0))
ad_banner("ad4.jpg", "焕新穿搭季", "全场服饰 · 满 100 返 10", (230, 62, 46), (200, 40, 30))

# ---------- 类目图标（96x96 圆底 PNG） ----------
def cat_icon(fname, ch, bg=(255, 80, 0)):
    s = 96
    img = Image.new("RGBA", (s, s), (0, 0, 0, 0))
    d = ImageDraw.Draw(img)
    d.ellipse((4, 4, s - 4, s - 4), fill=bg)
    try:
        f = ImageFont.truetype("C:/Windows/Fonts/msyhbd.ttc", 52)
    except Exception:
        f = ImageFont.load_default()
    bbox = d.textbbox((0, 0), ch, font=f)
    d.text(((s - (bbox[2] - bbox[0])) / 2 - bbox[0], (s - (bbox[3] - bbox[1])) / 2 - bbox[1]), ch, font=f, fill=(255, 255, 255))
    img.save(os.path.join(C_DIR, fname))

cat_icon("phone.png", "📱" if False else "机")
cat_icon("appliance.png", "电")
cat_icon("clothes.png", "衣")

print("生成完成：", os.path.join(ROOT))
