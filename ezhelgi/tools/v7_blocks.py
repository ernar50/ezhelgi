"""v7: блоки — флаги (3D полотнище на древке), петроглифы Тамгалы, поющий песок,
доска тоғыз құмалақ и камешки құмалақ. Плюс узоры для знамён (banner patterns).
"""
import math
import random
from PIL import Image, ImageDraw

from v7_armor import shade, GOLD, STEEL, TURQ, RED, LEATHER, DLEATHER, WHITE

# ------------------------------------------------------------------ цвета флагов
SKY = (0, 175, 202)          # голубой флага Казахстана
SKY_D = (0, 140, 170)
SUN = (254, 197, 0)
SUN_D = (214, 156, 0)
ALASH_G = (40, 130, 70)
ALASH_Y = (240, 200, 40)
ALASH_R = (200, 40, 40)
NAVY = (30, 60, 130)
IVORY = (244, 238, 222)
CRIM = (170, 30, 40)


def _img(w, h, c=(0, 0, 0, 0)):
    return Image.new("RGBA", (w, h), c)


def sun_eagle(d, cx, cy, s, col, col_d):
    """Солнце с 32 лучами и степной беркут под ним (масштаб s)."""
    r = 3.2 * s
    for k in range(32):
        a = k * math.pi / 16
        r1, r2 = r + 1.0 * s, r + (2.6 if k % 2 == 0 else 1.9) * s
        d.line([(cx + math.cos(a) * r1, cy + math.sin(a) * r1), (cx + math.cos(a) * r2, cy + math.sin(a) * r2)],
               fill=col, width=max(1, int(s * 0.5)))
    d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=col)
    # беркут: распахнутые крылья под солнцем
    ey = cy + r + 2.6 * s
    for side in (-1, 1):
        pts = [(cx, ey - 0.4 * s), (cx + side * 2.0 * s, ey - 1.6 * s), (cx + side * 5.5 * s, ey - 2.4 * s),
               (cx + side * 8.5 * s, ey - 1.6 * s), (cx + side * 6.5 * s, ey + 0.2 * s), (cx + side * 3.0 * s, ey + 0.8 * s)]
        d.polygon(pts, fill=col)
        for f in range(3):  # перья
            fx = cx + side * (4.5 + f * 1.4) * s
            d.line([(fx, ey - 1.6 * s), (fx + side * 0.6 * s, ey + 0.2 * s)], fill=col_d, width=1)
    d.polygon([(cx - 0.8 * s, ey - 0.8 * s), (cx + 0.8 * s, ey - 0.8 * s), (cx + 0.5 * s, ey + 1.6 * s), (cx - 0.5 * s, ey + 1.6 * s)],
              fill=col)
    d.ellipse([cx - 0.7 * s, ey - 1.6 * s, cx + 0.7 * s, ey - 0.3 * s], fill=col)


def ram_horn_band(img, x0, y0, w, h, col):
    """Вертикальная полоса орнамента «қошқар мүйіз» (как у древка флага Казахстана)."""
    px = img.load()
    unit = [
        "1....1",
        "11..11",
        ".1111.",
        "..11..",
        ".1111.",
        "11..11",
        "1....1",
        "......",
    ]
    uw = len(unit[0])
    sx = w / uw
    for y in range(h):
        row = unit[int(y / sx) % len(unit)]
        for x in range(w):
            if row[min(uw - 1, int(x / sx))] == "1":
                px[x0 + x, y0 + y] = col + (255,)


def crescent_star(d, cx, cy, r, moon, star):
    d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=moon)
    d.ellipse([cx - r + r * 0.55, cy - r * 0.95, cx + r * 1.4, cy + r * 0.95], fill=None, outline=None)
    return


def shanyraq(d, cx, cy, r, col, w=1):
    """Шаңырақ — венец юрты: круг и перекрещённые дуги."""
    d.ellipse([cx - r, cy - r, cx + r, cy + r], outline=col, width=w)
    d.line([(cx - r, cy), (cx + r, cy)], fill=col, width=w)
    d.line([(cx, cy - r), (cx, cy + r)], fill=col, width=w)
    rr = r * 0.62
    d.arc([cx - r * 1.6, cy - rr, cx + r * 0.2, cy + rr], 300, 60, fill=col, width=w)
    d.arc([cx - r * 0.2, cy - rr, cx + r * 1.6, cy + rr], 120, 240, fill=col, width=w)


# Тамги жузов — условные (стилизованные) знаки
TAMGA = {
    "uly": [  # «тарақ» — гребень
        "1.1.1",
        "1.1.1",
        "11111",
        "..1..",
        "..1..",
    ],
    "orta": [  # «шөміш» — круг с ручкой
        ".111.",
        "1...1",
        "1...1",
        ".111.",
        "..1..",
        "..1..",
    ],
    "kishi": [  # «қос сызық» — двойная черта с крюком
        "1.1..",
        "1.1..",
        "1.1..",
        "1.111",
        "1....",
    ],
}


def tamga(img, cx, cy, kind, scale, col):
    rows = TAMGA[kind]
    px = img.load()
    h, w = len(rows), len(rows[0])
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch == "1":
                for yy in range(scale):
                    for xx in range(scale):
                        X = int(cx - w * scale / 2 + x * scale + xx)
                        Y = int(cy - h * scale / 2 + y * scale + yy)
                        if 0 <= X < img.size[0] and 0 <= Y < img.size[1]:
                            px[X, Y] = col + (255,)


def flag_image(kind, w=48, h=24):
    """Полотнище флага w x h (2:1)."""
    img = _img(w, h)
    d = ImageDraw.Draw(img)
    if kind == "tu_qazaqstan":
        d.rectangle([0, 0, w, h], fill=SKY)
        ram_horn_band(img, 1, 0, 6, h, SUN)
        sun_eagle(d, w * 0.56, h * 0.30, 1.0, SUN, SUN_D)
    elif kind == "tu_alash":
        third = h // 3
        d.rectangle([0, 0, w, third], fill=ALASH_G)
        d.rectangle([0, third, w, 2 * third], fill=ALASH_Y)
        d.rectangle([0, 2 * third, w, h], fill=ALASH_R)
        # белый полумесяц и жёлтая звезда в левом верхнем углу
        d.ellipse([3, 0.5, 10, 7.5], fill=(250, 250, 250))
        d.ellipse([5, 0.2, 12, 7.2], fill=ALASH_G)
        star(d, 12.5, 4, 2.4, ALASH_Y)
    elif kind == "tu_handyq":
        d.rectangle([0, 0, w, h], fill=NAVY)
        d.rectangle([1, 1, w - 2, h - 2], outline=SUN)
        ram_horn_band(img, 3, 2, 4, h - 4, SUN)
        shanyraq(d, w * 0.58, h * 0.5, 6.5, SUN, 1)
    elif kind.startswith("tu_") and kind.endswith("_juz"):
        k = kind[3:-4]
        bg = {"uly": SKY_D, "orta": IVORY, "kishi": CRIM}[k]
        fg = {"uly": SUN, "orta": NAVY, "kishi": SUN}[k]
        d.rectangle([0, 0, w, h], fill=bg)
        # кайма-зубцы снизу и сверху
        for x in range(0, w, 4):
            d.polygon([(x, 0), (x + 2, 2), (x + 4, 0)], fill=fg)
            d.polygon([(x, h - 1), (x + 2, h - 3), (x + 4, h - 1)], fill=fg)
        tamga(img, w * 0.55, h * 0.5, k, 3, fg)
    shade_cloth(img)
    return img


def star(d, cx, cy, r, col):
    pts = []
    for k in range(10):
        a = -math.pi / 2 + k * math.pi / 5
        rr = r if k % 2 == 0 else r * 0.45
        pts.append((cx + math.cos(a) * rr, cy + math.sin(a) * rr))
    d.polygon(pts, fill=col)


def shade_cloth(img):
    """Лёгкие складки ткани: вертикальные полосы света и тени."""
    px = img.load()
    w, h = img.size
    for x in range(w):
        k = 1.0 + 0.07 * math.sin(x / w * math.pi * 4)
        for y in range(h):
            c = px[x, y]
            if c[3]:
                px[x, y] = (min(255, int(c[0] * k)), min(255, int(c[1] * k)), min(255, int(c[2] * k)), 255)


def flag_texture(kind):
    """64x64: полотнище 48x24 сверху-слева, древко (u 12–14), золото навершия и шашақ (u 14–16)."""
    tex = _img(64, 64)
    tex.paste(flag_image(kind), (0, 0))
    px = tex.load()
    rng = random.Random(len(kind))
    for y in range(64):
        for x in range(48, 56):  # древко: тёмное дерево с золотыми кольцами
            c = (92, 58, 32) if x in (49, 50, 53) else (120, 78, 44) if x < 54 else (70, 44, 24)
            if y % 16 in (0, 1):
                c = shade(GOLD, 2)[:3]
            px[x, y] = c + (255,)
    for y in range(0, 16):
        for x in range(56, 64):
            px[x, y] = shade(GOLD, 1 if (x + y) % 3 else 0)
    for y in range(16, 48):
        for x in range(56, 64):  # конский волос шашақ
            px[x, y] = (40, 30, 26, 255) if (x + rng.randint(0, 1)) % 2 else (70, 54, 44, 255)
    return tex


def flag_item_icon(kind):
    img = _img(16, 16)
    px = img.load()
    for y in range(1, 16):
        px[2, y] = (120, 78, 44, 255)
        px[3, y] = (82, 52, 28, 255)
    px[2, 0] = shade(GOLD, 0); px[3, 0] = shade(GOLD, 2)
    f = flag_image(kind, 48, 24).resize((12, 6), Image.BOX)
    fp = f.load()
    for y in range(6):
        for x in range(12):
            wave = int(round(math.sin(x / 11 * math.pi * 1.5) * 0.8))
            c = fp[x, y]
            if c[3]:
                px[4 + x, 1 + y + wave] = (c[0], c[1], c[2], 255)
    for y in range(1, 6):  # шашақ
        px[1, y] = (50, 38, 30, 255)
    return img


# ------------------------------------------------------------------ модели флага
def flag_models(ns):
    """Шаблон модели флага: древко 2 блока, полотнище из 4 полос зигзагом (развевается)."""
    el = []
    tex = "#flag"
    # древко
    el.append({"from": [7, 0, 7], "to": [9, 30, 9], "faces": {
        f: {"uv": [12, 0, 12.5 if f in ("north", "south") else 12.5, 16], "texture": tex} for f in ("north", "south", "east", "west")
    } | {"down": {"uv": [12, 0, 12.5, 0.5], "texture": tex}}})
    for f in ("north", "south", "east", "west"):
        el[-1]["faces"][f]["uv"] = [12, 0, 12.5, 16]
    # навершие
    el.append({"from": [6.5, 30, 6.5], "to": [9.5, 32, 9.5], "faces": {
        f: {"uv": [14, 0, 15, 1], "texture": tex} for f in ("north", "south", "east", "west", "up", "down")}})
    # шашақ — конский хвост у навершия
    el.append({"from": [6, 22, 7.5], "to": [7, 30, 8.5], "faces": {
        f: {"uv": [14, 4, 14.5, 12], "texture": tex} for f in ("north", "south", "east", "west", "up", "down")}})
    # полотнище
    W, y0, y1 = 22.0, 19.0, 30.0
    proj = W / 4
    sw = proj / math.cos(math.radians(22.5))
    for k in range(4):
        u0, u1 = 3 * k, 3 * k + 3
        if k % 2 == 0:
            hinge = 9 + k * proj
            frm, to, ang = [hinge, y0, 7.75], [hinge + sw, y1, 8.25], 22.5
        else:
            hinge = 9 + (k + 1) * proj
            frm, to, ang = [hinge - sw, y0, 7.75], [hinge, y1, 8.25], -22.5
        faces = {
            "south": {"uv": [u0, 0, u1, 6], "texture": tex},
            "north": {"uv": [u1, 0, u0, 6], "texture": tex},
            "up": {"uv": [u0, 0, u1, 0.25], "texture": tex},
            "down": {"uv": [u0, 5.75, u1, 6], "texture": tex},
        }
        if k == 3:
            faces["east"] = {"uv": [11.75, 0, 12, 6], "texture": tex}
        el.append({"from": [round(v, 3) for v in frm], "to": [round(v, 3) for v in to],
                   "rotation": {"angle": ang, "axis": "y", "origin": [round(hinge, 3), 24, 8]},
                   "faces": faces})
    base = {"textures": {"particle": "#flag"}, "elements": el}
    return base


# ------------------------------------------------------------------ петроглифы Тамгалы
GLYPHS = {
    "kunbasty": [  # солнцеголовый
        "...1.1.1...",
        "....111....",
        "..1111111..",
        "...11.11...",
        "..1111111..",
        "....111....",
        "...1.1.1...",
        ".....1.....",
        "...11111...",
        "..1.111.1..",
        ".1..111..1.",
        ".....1.....",
        "....1.1....",
        "...1...1...",
        "..11...11..",
    ],
    "biy": [  # хоровод
        ".1....1....1.",
        "111..111..111",
        ".1....1....1.",
        "1111111111111",
        ".1....1....1.",
        ".1....1....1.",
        "1.1..1.1..1.1",
        "1.1..1.1..1.1",
    ],
    "ogiz": [  # бык
        "1........1..",
        ".1......1...",
        "..111111....",
        "..1111111111",
        "...111111111",
        "...111111111",
        "...11....11.",
        "...1.1..1.1.",
        "...1.1..1.1.",
    ],
    "at": [  # конь
        "..11........",
        ".1111.......",
        "11.11.......",
        "....11111111",
        "....111111111",
        "....11111111.",
        "....1.1..1.1.",
        "...1..1..1..1",
        "...1..1..1..1",
    ],
    "bugy": [  # олень с ветвистыми рогами
        "1.1.1.......",
        ".111........",
        "1.1.1.1.....",
        "..111.......",
        "...11.......",
        "...111111111",
        "....11111111",
        "....1.1..1.1",
        "....1.1..1.1",
    ],
    "tuie": [  # верблюд
        "....1...1....",
        "...111.111...",
        "1.1111111111.",
        "111111111111.",
        ".1..11111111.",
        "....1.1..1.1.",
        "....1.1..1.1.",
        "....1.1..1.1.",
    ],
    "mergen": [  # лучник
        "...11....",
        "...11..1.",
        "..1111..1",
        ".1.11.1.1",
        "1..11..11",
        "...11...1",
        "...11..1.",
        "..1..1...",
        "..1..1...",
        ".1....1..",
    ],
    "arba": [  # колесница
        "....1111111....",
        "..1....1....1..",
        ".1.1...1...1.1.",
        "1.111..1..111.1",
        ".1.1...1...1.1.",
        "..1....1....1..",
    ],
}


def varnish(seed, w=16, h=16):
    """Скала с «пустынным загаром»: тёмно-коричневая, с трещинами."""
    rng = random.Random(seed)
    img = _img(w, h)
    px = img.load()
    for y in range(h):
        for x in range(w):
            v = rng.random()
            base = (78, 52, 38) if v < 0.5 else (64, 42, 32) if v < 0.85 else (96, 66, 46)
            px[x, y] = base + (255,)
    for _ in range(3):  # трещины
        x, y = rng.randrange(w), rng.randrange(h)
        for _ in range(rng.randint(4, 9)):
            if 0 <= x < w and 0 <= y < h:
                px[x, y] = (40, 26, 20, 255)
            x += rng.choice((-1, 0, 1))
            y += 1
    return img


def glyph_texture(name, seed):
    img = varnish(seed)
    px = img.load()
    rows = GLYPHS[name]
    h, w = len(rows), max(len(r) for r in rows)
    ox, oy = (16 - w) // 2, (16 - h) // 2
    rng = random.Random(seed * 7)
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch == "1":
                c = (214, 160, 104) if rng.random() < 0.7 else (186, 132, 84)
                px[ox + x, oy + y] = c + (255,)
    return img


def an_qum_texture():
    rng = random.Random(77)
    img = _img(16, 16)
    px = img.load()
    for y in range(16):
        for x in range(16):
            ripple = math.sin((x * 0.5 + y * 1.3) * 1.2)
            base = (226, 200, 140) if ripple > 0.35 else (208, 178, 116) if ripple < -0.5 else (218, 190, 128)
            n = rng.randint(-6, 6)
            px[x, y] = (base[0] + n, base[1] + n, base[2] + n, 255)
    return img


# ------------------------------------------------------------------ тоғыз құмалақ
def togyz_top():
    """Верх доски 64x32 (две половины по 32x32): 2 ряда по 9 лунок и два қазана посередине."""
    W, H = 64, 32
    img = _img(W, H)
    px = img.load()
    rng = random.Random(9)
    for y in range(H):
        for x in range(W):
            grain = math.sin(x * 0.35 + math.sin(y * 0.9) * 1.5)
            base = (150, 96, 52) if grain > 0.3 else (132, 82, 42) if grain < -0.5 else (142, 90, 48)
            n = rng.randint(-5, 5)
            px[x, y] = (base[0] + n, base[1] + n, base[2] + n, 255)
    d = ImageDraw.Draw(img)
    d.rectangle([0, 0, W - 1, H - 1], outline=(92, 56, 26))
    d.rectangle([1, 1, W - 2, H - 2], outline=shade(GOLD, 3)[:3])
    for side in (0, 1):
        cy = 7.7 if side == 1 else H - 8.7      # верхний ряд — дальний (соперник)
        for c in range(9):
            cx = (c + 0.5) * W / 9 - 0.5
            d.ellipse([cx - 2.4, cy - 4.0, cx + 2.4, cy + 4.0], fill=(52, 30, 14))
            d.ellipse([cx - 1.6, cy - 2.8, cx + 2.0, cy + 3.4], fill=(74, 44, 22))
            d.point([(cx - 1, cy - 3)], fill=(38, 22, 10))
    # қазаны — длинные желоба по центру
    d.rounded_rectangle([3, 13, W / 2 - 2, 18], radius=2, fill=(70, 42, 22))
    d.rounded_rectangle([W / 2 + 1, 13, W - 4, 18], radius=2, fill=(70, 42, 22))
    for x in range(4, W - 4, 4):  # золотая насечка-ою по краю
        px[x, 2] = shade(GOLD, 1)
        px[x, H - 3] = shade(GOLD, 1)
    return img


def togyz_side():
    img = _img(16, 16)
    px = img.load()
    for y in range(16):
        for x in range(16):
            px[x, y] = (112, 70, 36, 255) if (x + y * 3) % 7 else (98, 60, 30, 255)
    for x in range(16):
        px[x, 13] = shade(GOLD, 2) if x % 3 else shade(GOLD, 0)
    return img


def qumalaq_texture(dark=False):
    img = _img(16, 16)
    px = img.load()
    pal = [(250, 246, 236), (226, 220, 206), (196, 188, 172), (150, 142, 128)] if not dark else \
        [(240, 100, 80), (210, 60, 46), (170, 36, 30), (120, 22, 20)]
    for y in range(16):
        for x in range(16):
            i = 1 + (x + y) // 12
            if (x, y) in ((5, 5), (6, 5), (5, 6)):
                i = 0
            px[x, y] = pal[min(3, i)] + (255,)
    return img


# ------------------------------------------------------------------ узоры знамён (banner patterns)
BANNER = (20, 40)   # лицевая сторона знамени
SHIELD = (12, 22)   # лицевая сторона щита


def pattern_mask(name, w, h):
    """Белая маска узора на холсте w x h (лицевая сторона)."""
    img = _img(w, h)
    d = ImageDraw.Draw(img)
    W = (255, 255, 255)
    s = w / 20.0
    if name == "kz_kun":
        r = 3.0 * s
        cx, cy = w / 2, h * 0.30
        for k in range(32):
            a = k * math.pi / 16
            r2 = r + (2.8 if k % 2 == 0 else 2.0) * s
            d.line([(cx + math.cos(a) * (r + 0.7 * s), cy + math.sin(a) * (r + 0.7 * s)),
                    (cx + math.cos(a) * r2, cy + math.sin(a) * r2)], fill=W, width=1)
        d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=W)
    elif name == "kz_qyran":
        cx, ey = w / 2, h * 0.30 + 6.5 * s
        for side in (-1, 1):
            d.polygon([(cx, ey), (cx + side * 2 * s, ey - 1.6 * s), (cx + side * 5 * s, ey - 2.6 * s),
                       (cx + side * 8.5 * s, ey - 1.8 * s), (cx + side * 6 * s, ey + 0.4 * s), (cx + side * 2.6 * s, ey + 1 * s)], fill=W)
        d.polygon([(cx - 0.9 * s, ey - 1 * s), (cx + 0.9 * s, ey - 1 * s), (cx + 0.6 * s, ey + 2.4 * s), (cx - 0.6 * s, ey + 2.4 * s)], fill=W)
        d.ellipse([cx - 0.9 * s, ey - 2.4 * s, cx + 0.9 * s, ey - 0.6 * s], fill=W)
    elif name == "kz_oiu":
        ram_horn_band(img, 0, 0, max(3, int(4 * s)), h, (255, 255, 255))
    elif name == "alash_ai":
        r = 3.4 * s
        cx, cy = w * 0.32, h * 0.12
        d.ellipse([cx - r, cy - r, cx + r, cy + r], fill=W)
        d.ellipse([cx - r + 1.6 * s, cy - r - 0.2 * s, cx + r + 1.6 * s, cy + r - 0.2 * s], fill=(0, 0, 0, 0))
    elif name == "alash_juldyz":
        star(d, w * 0.62, h * 0.12, 2.6 * s, W)
    elif name == "shanyraq":
        shanyraq(d, w / 2, h * 0.42, 5.5 * s, W, max(1, int(s)))
    elif name.startswith("tamga_"):
        tamga(img, w / 2, h * 0.42, name[6:], max(2, int(2.4 * s)), (255, 255, 255))
    elif name == "qoshqar_muiz":
        # кайма из бараньих рогов по краю
        unit = ["1..1", ".11.", "1..1"]
        for y in range(h):
            for x in range(w):
                if x < 3 or x >= w - 3 or y < 3 or y >= h - 3:
                    ux, uy = (x if (y < 3 or y >= h - 3) else y) % 4, (min(x, w - 1 - x, y, h - 1 - y)) % 3
                    if unit[uy][ux] == "1":
                        img.putpixel((x, y), (255, 255, 255, 255))
    # бинаризация: узоры знамён рисуются по альфе
    px = img.load()
    for y in range(h):
        for x in range(w):
            if px[x, y][3] > 100:
                px[x, y] = (255, 255, 255, 255)
            else:
                px[x, y] = (0, 0, 0, 0)
    return img


PATTERNS = ["kz_kun", "kz_qyran", "kz_oiu", "alash_ai", "alash_juldyz", "shanyraq", "tamga_uly", "tamga_orta",
            "tamga_kishi", "qoshqar_muiz"]


def banner_pattern_texture(name):
    tex = _img(64, 64)
    front = pattern_mask(name, *BANNER)
    tex.paste(front, (1, 1))
    tex.paste(front.transpose(Image.Transpose.FLIP_LEFT_RIGHT), (22, 1))
    return tex


def shield_pattern_texture(name):
    tex = _img(64, 64)
    front = pattern_mask(name, *SHIELD)
    tex.paste(front, (1, 1))
    return tex
