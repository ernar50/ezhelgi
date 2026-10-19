"""v6: star paintings sold by al-Farabi (32 px per block).
Pure Pillow; deterministic.
"""
import math
import os
import random

from PIL import Image, ImageDraw, ImageFilter

PX = 32


def lerp(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


def sky(w, h, top, mid, bottom, mid_at=0.6):
    img = Image.new("RGB", (w, h))
    px = img.load()
    for y in range(h):
        t = y / max(1, h - 1)
        col = lerp(top, mid, t / mid_at) if t < mid_at else lerp(mid, bottom, (t - mid_at) / (1 - mid_at))
        for x in range(w):
            px[x, y] = col
    return img


def add_glow(img, points, radius, color, strength=1.0):
    """Soft glow: draw on a separate layer, blur, add."""
    layer = Image.new("RGB", img.size, (0, 0, 0))
    d = ImageDraw.Draw(layer)
    for (x, y, r) in points:
        d.ellipse([x - r, y - r, x + r, y + r], fill=color)
    layer = layer.filter(ImageFilter.GaussianBlur(radius))
    a = img.load()
    b = layer.load()
    for y in range(img.size[1]):
        for x in range(img.size[0]):
            p, q = a[x, y], b[x, y]
            a[x, y] = tuple(min(255, int(p[i] + q[i] * strength)) for i in range(3))


def stars(img, n, rng, region=None, bright=(255, 250, 235)):
    px = img.load()
    w, h = img.size
    for _ in range(n):
        x = rng.randrange(w)
        y = rng.randrange(h if region is None else region)
        k = rng.random()
        col = lerp(px[x, y], bright if rng.random() < 0.8 else (200, 220, 255), 0.35 + 0.65 * k)
        px[x, y] = col
        if k > 0.93 and 0 < x < w - 1 and 0 < y < h - 1:
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                px[x + dx, y + dy] = lerp(px[x + dx, y + dy], bright, 0.45)


def hills(img, base, amp, color, rng, seed_phase=0.0, rim=None):
    d = ImageDraw.Draw(img)
    w, h = img.size
    pts = []
    for x in range(w + 1):
        y = base + amp * (math.sin(x * 0.09 + seed_phase) * 0.6 + math.sin(x * 0.23 + seed_phase * 2) * 0.3)
        pts.append((x, y))
    d.polygon(pts + [(w, h), (0, h)], fill=color)
    if rim:
        for (x, y) in pts:
            if 0 <= int(y) < h and x < w:
                img.putpixel((x, int(y)), rim)


def yurt(img, cx, base, r, body=(20, 18, 24), door=(255, 190, 90)):
    d = ImageDraw.Draw(img)
    d.rectangle([cx - r, base - r * 0.9, cx + r, base], fill=body)
    d.polygon([(cx - r - 1, base - r * 0.9), (cx, base - r * 1.7), (cx + r + 1, base - r * 0.9)], fill=body)
    d.rectangle([cx - 1, base - r * 0.7, cx + 1, base], fill=door)
    add_glow(img, [(cx, base - r * 0.35, 2)], 3, (120, 70, 20), 0.9)


def frame(img, color=(120, 84, 40), inner=(200, 160, 80)):
    w, h = img.size
    px = img.load()
    for x in range(w):
        for y in (0, h - 1):
            px[x, y] = color
        px[x, 1] = lerp(px[x, 1], inner, 0.5)
        px[x, h - 2] = lerp(px[x, h - 2], inner, 0.5)
    for y in range(h):
        for x in (0, w - 1):
            px[x, y] = color
        px[1, y] = lerp(px[1, y], inner, 0.5)
        px[w - 2, y] = lerp(px[w - 2, y], inner, 0.5)


# ------------------------------------------------------------------ картины
def juldyzdy_tun():
    rng = random.Random(1)
    img = sky(64, 64, (6, 8, 30), (30, 24, 70), (90, 50, 90), 0.75)
    # Млечный Путь по диагонали
    band = []
    for i in range(160):
        t = rng.random()
        x = 4 + t * 58 + rng.gauss(0, 4)
        y = 6 + t * 34 + rng.gauss(0, 4)
        band.append((x, y, rng.uniform(0.5, 2.0)))
    add_glow(img, band, 4, (70, 60, 110), 0.55)
    add_glow(img, band[::3], 2, (110, 90, 140), 0.4)
    stars(img, 260, rng, region=48)
    hills(img, 50, 3, (16, 14, 26), rng, 1.0, rim=(60, 50, 90))
    hills(img, 55, 2, (10, 10, 16), rng, 3.0)
    yurt(img, 44, 57, 4)
    yurt(img, 52, 58, 2.5)
    frame(img)
    return img


def temir_qazyq():
    rng = random.Random(2)
    img = sky(64, 64, (4, 6, 22), (14, 22, 52), (36, 40, 70), 0.8)
    cx, cy = 34, 20
    px = img.load()
    # круговые следы звёзд вокруг Темірқазық (Полярной звезды)
    for k in range(70):
        r = rng.uniform(3, 46)
        a0 = rng.uniform(0, 2 * math.pi)
        length = rng.uniform(0.5, 1.4)
        tint = lerp((180, 200, 255), (255, 220, 170), rng.random())
        for s in range(int(r * length * 2)):
            a = a0 + s / (2 * r)
            x, y = cx + math.cos(a) * r, cy + math.sin(a) * r
            if 0 <= x < 64 and 0 <= y < 52:
                px[int(x), int(y)] = lerp(px[int(x), int(y)], tint, 0.35)
    add_glow(img, [(cx, cy, 1.5)], 3, (200, 220, 255), 1.0)
    px[cx, cy] = (255, 255, 255)
    # горы
    d = ImageDraw.Draw(img)
    d.polygon([(0, 54), (14, 40), (22, 47), (34, 34), (46, 48), (56, 42), (64, 50), (64, 64), (0, 64)], fill=(14, 14, 22))
    d.polygon([(30, 38), (34, 34), (38, 39)], fill=(200, 210, 230))
    frame(img)
    return img


def jeti_qaraqshy():
    rng = random.Random(3)
    img = sky(128, 64, (5, 8, 28), (22, 30, 70), (120, 70, 80), 0.78)
    stars(img, 320, rng, region=48)
    # Жеті қарақшы — Большая Медведица
    pts = [(22, 14), (36, 12), (48, 18), (60, 22), (70, 30), (92, 28), (90, 14)]
    d = ImageDraw.Draw(img)
    for a, b in zip(pts, pts[1:]):
        d.line([a, b], fill=(90, 110, 170), width=1)
    d.line([pts[3], pts[6]], fill=(90, 110, 170), width=1)
    add_glow(img, [(x, y, 1.2) for x, y in pts], 2.5, (210, 220, 255), 1.0)
    for x, y in pts:
        img.putpixel((x, y), (255, 255, 255))
    hills(img, 52, 3, (18, 16, 22), rng, 0.5, rim=(80, 60, 70))
    # табун лошадей
    for hx in (16, 26, 104, 112):
        d.rectangle([hx, 53, hx + 5, 56], fill=(10, 8, 10))
        d.rectangle([hx + 5, 50, hx + 7, 53], fill=(10, 8, 10))
        for lx in (hx, hx + 4):
            d.line([(lx, 56), (lx, 59)], fill=(10, 8, 10))
    yurt(img, 64, 59, 5)
    frame(img)
    return img


def ay_dala():
    rng = random.Random(4)
    img = sky(64, 32, (10, 16, 44), (40, 50, 90), (90, 90, 120), 0.7)
    stars(img, 70, rng, region=20)
    add_glow(img, [(44, 10, 5)], 5, (120, 120, 100), 0.9)
    d = ImageDraw.Draw(img)
    d.ellipse([39, 5, 49, 15], fill=(250, 244, 214))
    d.ellipse([42, 8, 44, 10], fill=(228, 222, 196))
    d.ellipse([45, 11, 47, 12], fill=(228, 222, 196))
    hills(img, 23, 1.5, (24, 26, 40), rng, 2.0, rim=(110, 110, 130))
    # озеро с лунной дорожкой
    d.rectangle([0, 26, 63, 31], fill=(20, 30, 60))
    for y in range(26, 32):
        for x in range(40 - (y - 26), 49 + (y - 26)):
            if rng.random() < 0.55:
                img.putpixel((x, y), (200, 200, 170))
    frame(img)
    return img


def astrolabe():
    rng = random.Random(5)
    img = sky(64, 64, (8, 10, 34), (16, 20, 52), (24, 24, 60), 0.6)
    stars(img, 120, rng)
    d = ImageDraw.Draw(img)
    cx, cy = 32, 33
    gold, dark, light = (222, 170, 60), (140, 96, 28), (255, 226, 140)
    d.ellipse([cx - 24, cy - 24, cx + 24, cy + 24], fill=dark)
    d.ellipse([cx - 22, cy - 22, cx + 22, cy + 22], fill=gold)
    d.ellipse([cx - 18, cy - 18, cx + 18, cy + 18], fill=(30, 34, 70))
    for k in range(48):
        a = k * math.pi * 2 / 48
        r0 = 19 if k % 4 else 18
        d.line([(cx + math.cos(a) * r0, cy + math.sin(a) * r0), (cx + math.cos(a) * 21, cy + math.sin(a) * 21)], fill=dark)
    d.ellipse([cx - 12, cy - 6, cx + 12, cy + 18], outline=light)
    d.ellipse([cx - 16, cy - 16, cx + 16, cy + 16], outline=(200, 160, 70))
    d.line([(cx - 17, cy), (cx + 17, cy)], fill=(200, 160, 70))
    d.line([(cx, cy - 17), (cx, cy + 17)], fill=(200, 160, 70))
    # ажурная «паутина» с указателями звёзд
    for a in (0.4, 1.3, 2.2, 3.4, 4.6, 5.5):
        x, y = cx + math.cos(a) * 13, cy + math.sin(a) * 13
        d.line([(cx, cy), (x, y)], fill=light)
        d.ellipse([x - 1, y - 1, x + 1, y + 1], fill=(255, 250, 220))
    d.rectangle([cx - 3, cy - 30, cx + 3, cy - 24], fill=gold)
    d.ellipse([cx - 3, cy - 33, cx + 3, cy - 27], outline=gold)
    d.ellipse([cx - 2, cy - 2, cx + 2, cy + 2], fill=light)
    add_glow(img, [(cx, cy, 20)], 6, (60, 40, 10), 0.6)
    frame(img)
    return img


def urker():
    rng = random.Random(6)
    img = sky(32, 32, (6, 10, 40), (14, 22, 60), (20, 26, 70), 0.6)
    stars(img, 40, rng)
    pts = [(10, 12), (14, 9), (17, 13), (20, 10), (15, 16), (12, 18), (21, 17)]
    add_glow(img, [(x, y, 2) for x, y in pts], 3, (70, 110, 200), 0.8)
    add_glow(img, [(x, y, 0.8) for x, y in pts], 1.5, (200, 220, 255), 1.0)
    for x, y in pts:
        img.putpixel((x, y), (255, 255, 255))
    frame(img)
    return img


PAINTINGS = {
    "juldyzdy_tun": (2, 2, juldyzdy_tun),
    "temir_qazyq": (2, 2, temir_qazyq),
    "jeti_qaraqshy": (4, 2, jeti_qaraqshy),
    "ay_dala": (2, 1, ay_dala),
    "astrolabe": (2, 2, astrolabe),
    "urker": (1, 1, urker),
}


def run(tex_dir):
    os.makedirs(tex_dir, exist_ok=True)
    for name, (w, h, fn) in PAINTINGS.items():
        img = fn()
        assert img.size == (w * PX, h * PX), name
        img.save(os.path.join(tex_dir, name + ".png"))
