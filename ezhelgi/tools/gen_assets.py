"""Generates item/block/armor textures, JSON models, recipes, loot tables, tags,
worldgen, the kurgan structure (.nbt) and language files for the Ezhelgi mod.
Run from the project root: python3 tools/gen_assets.py
"""
import gzip
import io
import json
import math
import os
import sys
import random
import struct
from PIL import Image

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
RES = os.path.join(ROOT, "src/main/resources")
A = os.path.join(RES, "assets/ezhelgi")
D = os.path.join(RES, "data/ezhelgi")
MC = os.path.join(RES, "data/minecraft")
NS = "ezhelgi"


def write_json(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as fh:
        json.dump(obj, fh, ensure_ascii=False, indent=2)
        fh.write("\n")


def hexc(h):
    h = h.lstrip("#")
    return tuple(int(h[i:i + 2], 16) for i in (0, 2, 4)) + (255,)


# ------------------------------------------------------------------ canvas helpers
class Canvas:
    def __init__(self, w=16, h=16, seed=0):
        self.img = Image.new("RGBA", (w, h), (0, 0, 0, 0))
        self.px = self.img.load()
        self.w, self.h = w, h
        self.rng = random.Random(seed)

    def set(self, x, y, c):
        if 0 <= x < self.w and 0 <= y < self.h:
            self.px[x, y] = hexc(c) if isinstance(c, str) else c

    def get(self, x, y):
        return self.px[x, y]

    def line(self, x0, y0, x1, y1, c):
        n = max(abs(x1 - x0), abs(y1 - y0))
        for i in range(n + 1):
            t = i / n if n else 0
            self.set(round(x0 + (x1 - x0) * t), round(y0 + (y1 - y0) * t), c)

    def rect(self, x0, y0, x1, y1, c):
        for y in range(y0, y1 + 1):
            for x in range(x0, x1 + 1):
                self.set(x, y, c)

    def disc(self, cx, cy, r, c):
        for y in range(self.h):
            for x in range(self.w):
                if (x - cx) ** 2 + (y - cy) ** 2 <= r * r:
                    self.set(x, y, c)

    def outline(self, c="#2a1c10"):
        col = hexc(c)
        pts = []
        for y in range(self.h):
            for x in range(self.w):
                if self.px[x, y][3] == 0:
                    for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                        nx, ny = x + dx, y + dy
                        if 0 <= nx < self.w and 0 <= ny < self.h and self.px[nx, ny][3] > 0 and self.px[nx, ny] != col:
                            pts.append((x, y))
                            break
        for p in pts:
            self.px[p] = col

    def noise(self, amount=0.08, only_opaque=True):
        for y in range(self.h):
            for x in range(self.w):
                r, g, b, a = self.px[x, y]
                if a == 0 and only_opaque:
                    continue
                k = 1 + self.rng.uniform(-amount, amount)
                self.px[x, y] = (min(255, int(r * k)), min(255, int(g * k)), min(255, int(b * k)), a)

    def save(self, rel):
        path = os.path.join(A, "textures", rel + ".png")
        os.makedirs(os.path.dirname(path), exist_ok=True)
        self.img.save(path)


def from_art(rows, pal, seed=0, outline=None):
    c = Canvas(len(rows[0]), len(rows), seed)
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch != ".":
                c.set(x, y, pal[ch])
    if outline:
        c.outline(outline)
    return c


GOLD = {"l": "#fff3a8", "m": "#f2c230", "d": "#b9831a", "k": "#7a4f0c"}
IRON = {"l": "#f4f6f8", "m": "#c4c9cf", "d": "#8a9098", "k": "#4f545b"}


# ------------------------------------------------------------------ item textures
def sword(name, blade, guard, grip, gem):
    c = Canvas(seed=hash(name) & 0xffff)
    # curved sabre: a slight arc from hilt (3,12) to tip (14,1)
    pts = []
    for i in range(12):
        t = i / 11
        x = 3 + 11 * t
        y = 12 - 11 * t - math.sin(t * math.pi) * 1.3
        pts.append((round(x), round(y)))
    for (x, y) in pts[1:]:
        c.set(x, y, blade["m"])
        c.set(x + 1, y, blade["l"])
        c.set(x, y + 1, blade["d"])
    c.set(14, 1, blade["l"])
    c.set(15, 0, blade["l"])
    # guard
    c.line(2, 10, 5, 13, guard)
    c.set(3, 11, gem)
    # grip and pommel
    c.line(2, 13, 0, 15, grip)
    c.set(1, 13, grip)
    c.set(0, 15, guard)
    c.outline("#20160c")
    return c


def items():
    # Сақ алтыны: слиток с "звериным стилем"
    art = [
        "................",
        "................",
        "................",
        "................",
        "......kkkkkk....",
        "....kkllllmmk...",
        "...kllmmmmmmdk..",
        "..kllmmkmmkmmdk.",
        "..klmmkkmkkmmdk.",
        "..kmmmmkkkmmmdk.",
        "..kdmmmmmmmmddk.",
        "...kddddddddkk..",
        "....kkkkkkkk....",
        "................",
        "................",
        "................",
    ]
    from_art(art, GOLD).save("item/saka_gold")

    sword("qylysh", IRON, "#d9a521", "#5a3418", "#c0392b").save("item/qylysh")
    sword("altyn_qylysh", GOLD, "#c0392b", "#3b2410", "#2e86de").save("item/altyn_qylysh")

    # Найза: копьё с красной кисточкой
    c = Canvas(seed=5)
    c.line(0, 15, 10, 5, "#7a4a22")
    c.line(1, 15, 11, 5, "#9c6630")
    for (x, y) in [(11, 4), (12, 3), (13, 2), (14, 1), (12, 4), (13, 3), (11, 3), (12, 2), (13, 1), (15, 0)]:
        c.set(x, y, IRON["m"])
    c.set(13, 2, IRON["l"]); c.set(14, 1, IRON["l"]); c.set(15, 0, IRON["l"])
    c.set(11, 4, IRON["d"])
    for (x, y) in [(9, 5), (10, 6), (9, 6), (8, 6), (9, 7), (8, 7), (7, 8)]:
        c.set(x, y, "#c0392b")
    c.outline("#20160c")
    c.save("item/naiza")

    # Садақ: лук + 3 кадра натяжения
    def bow(pull, name):
        c = Canvas(seed=7 + pull)
        arc = [(2, 12), (2, 11), (3, 10), (3, 9), (4, 8), (4, 7), (5, 6), (6, 5), (7, 4), (8, 4), (9, 3), (10, 3), (11, 2), (12, 2)]
        for (x, y) in arc:
            c.set(x, y, "#8b5a2b")
            c.set(x + 1, y + 1, "#5e3a1a")
        c.set(1, 13, "#d9a521"); c.set(13, 1, "#d9a521")
        c.set(2, 13, "#c0392b"); c.set(13, 2, "#c0392b")
        ox = [0, 2, 3, 4][pull]
        mx, my = 7 + ox, 7 + ox
        c.line(2, 13, mx, my, "#e8e0c8")
        c.line(mx, my, 13, 2, "#e8e0c8")
        if pull:
            c.line(mx - 1, my - 1, mx - 7 + ox, my - 7 + ox, "#9c9c9c")
            c.set(max(0, mx - 8 + ox), max(0, my - 8 + ox), IRON["l"])
        c.outline("#20160c")
        c.save("item/" + name)
    bow(0, "sadaq")
    for i in range(3):
        bow(i + 1, "sadaq_pulling_%d" % i)

    # Доспех Золотого человека
    pal = dict(GOLD, r="#c0392b", b="#2e86de", w="#fff8e0")
    helmet = [
        "........k.......",
        ".......klk......",
        ".......kmk......",
        "......kmldk.....",
        "......kmrdk.....",
        ".....kmlmmdk....",
        ".....kmmrmdk....",
        "....kmlmmmmdk...",
        "....kmmmrmmdk...",
        "...kmlmmmmmmdk..",
        "...krmrmrmrmrk..",
        "...kmmmmmmmmdk..",
        "...km......mdk..",
        "...kk......kkk..",
        "................",
        "................",
    ]
    from_art(helmet, pal).save("item/altyn_adam_helmet")
    chest = [
        "................",
        "..kkkk....kkkk..",
        ".kmlmmkkkkmmldk.",
        ".kmmmmmmmmmmmdk.",
        ".kmrmmlmmlmmrdk.",
        "..kkmmmmmmmmkk..",
        "...kmmrmmrmmk...",
        "...kmlmmmmmdk...",
        "...kmmmrrmmdk...",
        "...kmlmmmmmdk...",
        "...kmmrmmrmdk...",
        "...krrrrrrrrk...",
        "...kmmmmmmmdk...",
        "...kkkkkkkkkk...",
        "................",
        "................",
    ]
    from_art(chest, pal).save("item/altyn_adam_chestplate")
    legs = [
        "................",
        "...kkkkkkkkkk...",
        "...krrrrrrrrk...",
        "...kmlmmmmmdk...",
        "...kmmmkkmmdk...",
        "...kmlmkkmmdk...",
        "...kmmdkkmmdk...",
        "...kmlmkkmmdk...",
        "...kmmdkkmmdk...",
        "...krmrkkrmrk...",
        "...kmmdkkmmdk...",
        "...kmldkkmldk...",
        "...kkkkkkkkkk...",
        "................",
        "................",
        "................",
    ]
    from_art(legs, pal).save("item/altyn_adam_leggings")
    boots = [
        "................",
        "................",
        "................",
        "................",
        "...kkkk..kkkk...",
        "...kmlk..kmlk...",
        "...kmdk..kmdk...",
        "...krrk..krrk...",
        "...kmdk..kmdk...",
        "...kmdk..kmdk...",
        "..kmmdk..kmmdk..",
        ".kmlmdk..kmlmdk.",
        ".krrrrk..krrrrk.",
        ".kkkkkk..kkkkkk.",
        "................",
        "................",
    ]
    from_art(boots, pal).save("item/altyn_adam_boots")

    # Еда
    foodpal = {
        "o": "#3a2412", "b": "#c98a3c", "B": "#e3ad5c", "y": "#f5d27a", "w": "#f6f1e4", "W": "#ffffff",
        "g": "#d8d0bc", "r": "#8e2f22", "R": "#b8483a", "f": "#f0e6d0", "c": "#e8e8f0", "C": "#bcc8d8",
        "n": "#f2e2a8", "m": "#7a3a22", "M": "#a65a36", "h": "#6b4a2a", "s": "#7fa040", "x": "#dfeaf2",
    }
    beshbarmak = [
        "................",
        "................",
        "................",
        "......M.m.......",
        "....nMnmnMn.....",
        "...nnmMnnmnn....",
        "..onMnnmMnnMno..",
        "..ohnnsnnnsnho..",
        "..ohhhhhhhhhho..",
        "...ohhhhhhhho...",
        "....ohhhhhho....",
        ".....oooooo.....",
        "................",
        "................",
        "................",
        "................",
    ]
    from_art(beshbarmak, foodpal).save("item/beshbarmak")
    baursak = [
        "................",
        "................",
        "................",
        "................",
        "......oooo......",
        ".....oyBBbo.....",
        "....oyBBbbbo....",
        "....oBBbbbbo....",
        "..oooobbbboooo..",
        ".oyBBboooooyBBo.",
        ".oBBbbbo.oBBbbo.",
        ".oBbbbbo.oBbbbo.",
        ".obbbbbo.obbbbo.",
        "..ooooo...oooo..",
        "................",
        "................",
    ]
    from_art(baursak, foodpal).save("item/baursak")
    kurt = [
        "................",
        "................",
        "................",
        "................",
        "......ooo.......",
        ".....oWwgo......",
        ".....owwgo......",
        "...ooogggooo....",
        "..oWwgooooWwgo..",
        "..owwwgo.owwgo..",
        "..owwgo..oggo...",
        "...oooo...oo....",
        "................",
        "................",
        "................",
        "................",
    ]
    from_art(kurt, foodpal).save("item/kurt")
    kazy = [
        "................",
        "................",
        "................",
        ".....oooooo.....",
        "....oRRrRRRo....",
        "...oRfRrRfRro...",
        "..oRro....oRro..",
        "..oRo......oRo..",
        "..ofo......ofo..",
        "..oRro....oRro..",
        "...orRfRRrRfo...",
        "....ohoRRRRo....",
        "....oho.oooo....",
        ".....o..........",
        "................",
        "................",
    ]
    from_art(kazy, foodpal).save("item/kazy")

    def bottle(name, liquid, light):
        art = [
            "................",
            "................",
            "......ohho......",
            "......ohho......",
            ".......xx.......",
            ".......xx.......",
            "......oxxo......",
            ".....oxxxxo.....",
            "....oxLLLLxo....",
            "....oLlLLLLo....",
            "....oLLLLLLo....",
            "....oLLLLLLo....",
            "....oLLLLLLo....",
            ".....oLLLLo.....",
            "......oooo......",
            "................",
        ]
        pal = dict(foodpal, L=liquid, l=light)
        from_art(art, pal).save("item/" + name)
    bottle("kumys", "#ece4d0", "#ffffff")
    bottle("shubat", "#f3e6c0", "#fffbe8")

    # Домбыра
    c = Canvas(seed=9)
    c.disc(4.5, 11.0, 3.6, "#8b5a2b")
    c.disc(4.5, 11.0, 2.4, "#a96e36")
    c.set(4, 11, "#3a2412"); c.set(5, 11, "#3a2412")
    c.line(6, 8, 14, 0, "#6b4220")
    c.line(7, 8, 15, 0, "#8b5a2b")
    c.line(3, 13, 13, 1, "#efe7d0")
    c.set(14, 0, "#d9a521"); c.set(13, 0, "#d9a521")
    c.outline("#20160c")
    c.save("item/dombyra")

    # Киіз үй (набор юрты)
    yurt = [
        "................",
        "................",
        "................",
        ".......hh.......",
        "......owwo......",
        ".....owwwwo.....",
        "....owwwwwwo....",
        "...owwwwwwwwo...",
        "..oRRRRRRRRRRo..",
        "..owwhwwwwhwwo..",
        "..owhwhwwhwhwo..",
        "..owwhwoowhwwo..",
        "..owhwhoohwhwo..",
        "..oRRRRooRRRRo..",
        "..oooooooooooo..",
        "................",
    ]
    from_art(yurt, foodpal).save("item/kiiz_uy")

    # Яйца призыва
    def egg(name, base, spots):
        c = Canvas(seed=hash(name) & 0xff)
        for y in range(16):
            for x in range(16):
                dx, dy = (x - 7.5) / 5.2, (y - 8.8) / 6.6
                if dy < 0:
                    dx *= 1.15
                if dx * dx + dy * dy <= 1:
                    c.set(x, y, base)
        rng = random.Random(name)
        for _ in range(9):
            x, y = rng.randint(4, 11), rng.randint(4, 13)
            if c.get(x, y)[3]:
                c.set(x, y, spots)
                if c.get(x + 1, y)[3]:
                    c.set(x + 1, y, spots)
        c.noise(0.05)
        c.outline("#1c1c1c")
        c.save("item/" + name)
    egg("kokbori_spawn_egg", "#7a8494", "#3f4654")
    egg("tulpar_spawn_egg", "#eee6d2", "#d6a035")
    egg("berkut_spawn_egg", "#60402a", "#c4924a")


# ------------------------------------------------------------------ block textures
def blocks():
    # Киіз (войлок)
    c = Canvas(seed=21)
    c.rect(0, 0, 15, 15, "#e9dfc8")
    c.noise(0.07)
    for i in range(16):
        if i % 2 == 0:
            c.set(i, 0, "#8e2f22"); c.set(i, 15, "#8e2f22"); c.set(0, i, "#8e2f22"); c.set(15, i, "#8e2f22")
    c.save("block/kiiz")

    # Кереге (решётка)
    c = Canvas(seed=22)
    c.rect(0, 0, 15, 15, "#e8d8b4")
    c.noise(0.04)
    for y in range(16):
        for x in range(16):
            a, b = (x + y) % 6 == 0, (x - y) % 6 == 0
            if a and b:
                c.set(x, y, "#5e2c10")
            elif a or b:
                c.set(x, y, "#9a4e22")
    c.save("block/kerege")

    # Шаңырақ (вид сверху: круг с перекладинами)
    c = Canvas(seed=23)
    c.rect(0, 0, 15, 15, "#8b5a2b")
    for y in range(16):
        if y % 4 == 3:
            c.line(0, y, 15, y, "#6e4520")
    c.noise(0.05)
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - 7.5, y - 7.5)
            if 5.2 <= d <= 7.2:
                c.set(x, y, "#c78a3e" if d < 6.2 else "#5a3518")
    c.line(2, 7, 13, 7, "#c78a3e"); c.line(2, 8, 13, 8, "#5a3518")
    c.line(7, 2, 7, 13, "#c78a3e"); c.line(8, 2, 8, 13, "#5a3518")
    c.line(3, 3, 12, 12, "#c78a3e"); c.line(3, 12, 12, 3, "#c78a3e")
    c.save("block/shanyrak")

    # Сырмақ (орнамент «қошқар мүйіз»)
    c = Canvas(seed=24)
    c.rect(0, 0, 15, 15, "#a8281e")
    c.noise(0.05)
    horn = "#f0d9a0"
    for (ox, oy) in ((0, 0), (8, 8), (8, 0), (0, 8)):
        flip = 1 if (ox + oy) % 16 == 0 else -1
        cx = ox + 3.5
        cy = oy + 3.5
        for (x, y) in [(0, 2), (1, 1), (2, 1), (3, 2), (3, 3), (2, 3), (4, 4), (5, 3), (6, 3), (7, 4)]:
            xx = ox + (x if flip == 1 else 7 - x)
            c.set(xx, oy + y + 1, horn)
    for i in range(16):
        c.set(i, 0, "#2c3e70"); c.set(i, 15, "#2c3e70")
    c.save("block/syrmak")


# ------------------------------------------------------------------ armor equipment textures
ARMOR = {
    "red": (156, 30, 28, 255), "red_d": (112, 20, 20, 255),
    "gl": (255, 230, 128, 255), "g": (238, 184, 52, 255), "gd": (178, 120, 22, 255), "gk": (110, 70, 12, 255),
    "turq": (40, 170, 160, 255), "sole": (70, 44, 24, 255),
}


def _armor_px(kind, x, y, w, h, rng):
    """Pattern of one face pixel of the Golden Man armor."""
    A = ARMOR
    if kind == "gold":
        base = A["g"] if (x * 2 + y) % 7 else A["gl"]
        if x == 0 or y == 0:
            base = A["gl"]
        if x == w - 1 or y == h - 1:
            base = A["gd"]
        return base
    if kind == "band":  # золотой пояс/обод с бирюзовыми вставками
        if y == 0:
            return A["gl"]
        if y == h - 1:
            return A["gd"]
        if x % 3 == 1 and y == 1:
            return A["turq"]
        return A["g"]
    if kind == "plaques":  # красный кафтан, нашитые золотые треугольные бляшки
        tx, ty = (x + (y // 4) * 2) % 4, y % 4
        if ty == 1 and tx in (0, 1, 2):
            return A["gl"] if tx == 1 else A["g"]
        if ty == 2 and tx == 1:
            return A["gd"]
        return A["red"] if (x * 3 + y) % 9 else A["red_d"]
    if kind == "sole":
        return A["sole"]
    if kind == "skin":
        return (176, 120, 82, 255) if (x + y) % 6 else (164, 110, 74, 255)
    return None


def _armor_rect(c, x0, y0, w, h, kind, rows=None):
    rng = random.Random(x0 * 131 + y0)
    for y in range(h):
        if rows is not None and y not in rows:
            continue
        for x in range(w):
            col = _armor_px(kind(y) if callable(kind) else kind, x, y, w, h, rng)
            if col:
                k = 1 + rng.uniform(-0.05, 0.05)
                c.set(x0 + x, y0 + y, (min(255, int(col[0] * k)), min(255, int(col[1] * k)), min(255, int(col[2] * k)), 255))


def _box(c, u, v, w, h, d, side_kind, top_kind=None, bottom_kind=None, front_kind=None, rows=None):
    """Paint every face of a box-UV region like the game does (u, v = texture offset)."""
    if top_kind:
        _armor_rect(c, u + d, v, w, d, top_kind)
    if bottom_kind:
        _armor_rect(c, u + d + w, v, w, d, bottom_kind)
    _armor_rect(c, u, v + d, d, h, side_kind, rows)                     # right
    _armor_rect(c, u + d, v + d, w, h, front_kind or side_kind, rows)   # front
    _armor_rect(c, u + d + w, v + d, d, h, side_kind, rows)             # left
    _armor_rect(c, u + 2 * d + w, v + d, w, h, side_kind, rows)         # back


def armor_layers():
    # --- humanoid: шлем, кафтан с рукавами, сапоги
    c = Canvas(64, 32, seed=31)
    # шлем: золотой обод сверху, красная шапка с бляшками, открытое лицо
    head_kind = lambda y: "band" if y <= 2 else ("gold" if y == 7 else "plaques")
    _box(c, 0, 0, 8, 8, 8, head_kind, top_kind="gold")
    # лицо: убираем середину передней грани (видно лицо игрока)
    for y in range(4, 8):
        for x in range(1, 7):
            c.set(8 + x, 8 + y, (0, 0, 0, 0))
    for x in range(8):
        c.set(8 + x, 8 + 3, ARMOR["gd"] if x in (0, 7) else (ARMOR["gl"] if x % 2 else ARMOR["g"]))
    # кафтан: воротник, бляшки, золотой пояс
    body_kind = lambda y: "gold" if y == 0 else ("band" if y in (8, 9, 10) else "plaques")
    _box(c, 16, 16, 8, 12, 4, body_kind, top_kind="gold", bottom_kind="plaques")
    for y in range(1, 8):  # золотая застёжка по центру
        c.set(20 + 3, 20 + y, ARMOR["gl"]); c.set(20 + 4, 20 + y, ARMOR["gd"])
    # рукава: золотые наплечники и манжеты
    arm_kind = lambda y: "gold" if y in (0, 1, 10, 11) else "plaques"
    _box(c, 40, 16, 4, 12, 4, arm_kind, top_kind="gold", bottom_kind="plaques")
    # сапоги: только нижние 5 рядов ноги
    boot_kind = lambda y: "band" if y == 7 else "plaques"
    _box(c, 0, 16, 4, 12, 4, boot_kind, bottom_kind="sole", rows=range(7, 12))
    c.save("entity/equipment/humanoid/altyn_adam")

    # --- humanoid_leggings: штаны и пояс
    c = Canvas(64, 32, seed=32)
    _box(c, 16, 16, 8, 12, 4, lambda y: "band" if y in (8, 9, 10) else "plaques", rows=range(7, 12))
    leg_kind = lambda y: "band" if y == 0 else "plaques"
    _box(c, 0, 16, 4, 12, 4, leg_kind, top_kind="plaques", rows=range(0, 9))
    c.save("entity/equipment/humanoid_leggings/altyn_adam")


# ------------------------------------------------------------------ models & item definitions
def model_json():
    simple_items = ["saka_gold", "altyn_adam_helmet", "altyn_adam_chestplate", "altyn_adam_leggings",
                    "altyn_adam_boots", "beshbarmak", "baursak", "kurt", "kazy", "kumys", "shubat",
                    "kiiz_uy", "kokbori_spawn_egg", "tulpar_spawn_egg", "berkut_spawn_egg"]
    handheld = ["qylysh", "altyn_qylysh", "naiza", "dombyra"]
    for n in simple_items + handheld:
        write_json(f"{A}/models/item/{n}.json", {
            "parent": "minecraft:item/handheld" if n in handheld else "minecraft:item/generated",
            "textures": {"layer0": f"{NS}:item/{n}"}})
        write_json(f"{A}/items/{n}.json", {"model": {"type": "minecraft:model", "model": f"{NS}:item/{n}"}})
    # bow
    for n in ["sadaq", "sadaq_pulling_0", "sadaq_pulling_1", "sadaq_pulling_2"]:
        write_json(f"{A}/models/item/{n}.json", {"parent": "minecraft:item/bow", "textures": {"layer0": f"{NS}:item/{n}"}})
    write_json(f"{A}/items/sadaq.json", {"model": {
        "type": "minecraft:condition",
        "on_false": {"type": "minecraft:model", "model": f"{NS}:item/sadaq"},
        "on_true": {
            "type": "minecraft:range_dispatch",
            "entries": [
                {"model": {"type": "minecraft:model", "model": f"{NS}:item/sadaq_pulling_1"}, "threshold": 0.65},
                {"model": {"type": "minecraft:model", "model": f"{NS}:item/sadaq_pulling_2"}, "threshold": 0.9}],
            "fallback": {"type": "minecraft:model", "model": f"{NS}:item/sadaq_pulling_0"},
            "property": "minecraft:use_duration", "scale": 0.05},
        "property": "minecraft:using_item"}})

    for b in ["kiiz", "kerege", "shanyrak"]:
        write_json(f"{A}/blockstates/{b}.json", {"variants": {"": {"model": f"{NS}:block/{b}"}}})
        write_json(f"{A}/models/block/{b}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"{NS}:block/{b}"}})
        write_json(f"{A}/items/{b}.json", {"model": {"type": "minecraft:model", "model": f"{NS}:block/{b}"}})
    write_json(f"{A}/blockstates/syrmak.json", {"variants": {"": {"model": f"{NS}:block/syrmak"}}})
    write_json(f"{A}/models/block/syrmak.json", {"parent": "minecraft:block/carpet", "textures": {"wool": f"{NS}:block/syrmak"}})
    write_json(f"{A}/items/syrmak.json", {"model": {"type": "minecraft:model", "model": f"{NS}:block/syrmak"}})

    write_json(f"{A}/equipment/altyn_adam.json", {"layers": {
        "humanoid": [{"texture": f"{NS}:altyn_adam"}],
        "humanoid_leggings": [{"texture": f"{NS}:altyn_adam"}]}})


# ------------------------------------------------------------------ data
def shaped(name, pattern, key, result, count=1, category="misc"):
    write_json(f"{D}/recipe/{name}.json", {
        "type": "minecraft:crafting_shaped", "category": category,
        "key": key, "pattern": pattern, "result": {"id": result, "count": count}})


def shapeless(name, ingredients, result, count=1, category="misc"):
    write_json(f"{D}/recipe/{name}.json", {
        "type": "minecraft:crafting_shapeless", "category": category,
        "ingredients": ingredients, "result": {"id": result, "count": count}})


def recipes():
    e = lambda n: f"{NS}:{n}"
    shaped("saka_gold", [" G ", "GAG", " G "], {"G": "minecraft:gold_ingot", "A": "minecraft:amethyst_shard"}, e("saka_gold"))
    shaped("qylysh", ["  I", " I ", "S  "], {"I": "minecraft:iron_ingot", "S": "minecraft:stick"}, e("qylysh"), category="equipment")
    shaped("altyn_qylysh", ["  G", " G ", "S  "], {"G": e("saka_gold"), "S": "minecraft:stick"}, e("altyn_qylysh"), category="equipment")
    shaped("naiza", ["  I", " W ", "S  "], {"I": "minecraft:iron_ingot", "W": "minecraft:red_wool", "S": "minecraft:stick"}, e("naiza"), category="equipment")
    shaped("sadaq", [" SL", "H L", " SL"], {"S": "minecraft:stick", "L": "minecraft:string", "H": "minecraft:leather"}, e("sadaq"), category="equipment")
    g = {"G": e("saka_gold")}
    shaped("altyn_adam_helmet", [" G ", "GGG", "G G"], g, e("altyn_adam_helmet"), category="equipment")
    shaped("altyn_adam_chestplate", ["G G", "GGG", "GGG"], g, e("altyn_adam_chestplate"), category="equipment")
    shaped("altyn_adam_leggings", ["GGG", "G G", "G G"], g, e("altyn_adam_leggings"), category="equipment")
    shaped("altyn_adam_boots", ["G G", "G G"], g, e("altyn_adam_boots"), category="equipment")
    shapeless("beshbarmak", ["minecraft:bowl", "minecraft:cooked_mutton", "minecraft:cooked_mutton", "minecraft:wheat", "minecraft:baked_potato"], e("beshbarmak"))
    shapeless("baursak", ["minecraft:wheat", "minecraft:wheat", "minecraft:egg", "minecraft:sugar"], e("baursak"), 6)
    shapeless("kurt", ["minecraft:milk_bucket"], e("kurt"), 6)
    shapeless("kazy", ["minecraft:cooked_beef", "minecraft:cooked_beef", "minecraft:string"], e("kazy"), 3)
    shapeless("kumys", ["minecraft:milk_bucket", "minecraft:glass_bottle", "minecraft:glass_bottle", "minecraft:glass_bottle", "minecraft:sugar"], e("kumys"), 3)
    shapeless("shubat", ["minecraft:milk_bucket", "minecraft:glass_bottle", "minecraft:glass_bottle", "minecraft:glass_bottle", "minecraft:sand"], e("shubat"), 3)
    shaped("dombyra", ["  S", " T ", "PP "], {"S": "minecraft:string", "T": "minecraft:stick", "P": "#minecraft:planks"}, e("dombyra"))
    shapeless("kiiz", ["minecraft:white_wool", "minecraft:white_wool", "minecraft:leather"], e("kiiz"), 3, "building")
    shaped("kerege", ["S S", " S ", "S S"], {"S": "minecraft:stick"}, e("kerege"), 4, "building")
    shaped("shanyrak", [" S ", "SLS", " S "], {"S": "minecraft:stick", "L": "#minecraft:logs"}, e("shanyrak"), 1, "building")
    shaped("syrmak", ["RW"], {"R": "minecraft:red_wool", "W": "minecraft:white_wool"}, e("syrmak"), 4, "building")
    shaped("kiiz_uy", ["KSK", "EEE", "MKM"], {"K": e("kiiz"), "S": e("shanyrak"), "E": e("kerege"), "M": e("syrmak")}, e("kiiz_uy"))


def loot():
    for b in ["kiiz", "kerege", "shanyrak", "syrmak"]:
        write_json(f"{D}/loot_table/blocks/{b}.json", {
            "type": "minecraft:block",
            "pools": [{"condition": {"type": "minecraft:survives_explosion"},
                       "entries": [{"type": "minecraft:item", "name": f"{NS}:{b}"}], "rolls": 1}],
            "random_sequence": f"{NS}:blocks/{b}"})

    def entity(n, item, lo, hi):
        write_json(f"{D}/loot_table/entities/{n}.json", {
            "type": "minecraft:entity",
            "pools": [{"entries": [{"type": "minecraft:item", "name": item,
                                    "modifier": [{"type": "minecraft:set_count",
                                                  "count": {"type": "minecraft:uniform", "min": lo, "max": hi}}]}],
                       "rolls": 1}],
            "random_sequence": f"{NS}:entities/{n}"})
    entity("tulpar", "minecraft:leather", 1, 3)
    entity("berkut", "minecraft:feather", 1, 3)
    entity("kokbori", "minecraft:bone", 0, 2)

    def it(name, w, lo=1, hi=1):
        e = {"type": "minecraft:item", "name": name, "weight": w}
        if hi > 1:
            e["modifier"] = [{"type": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}}]
        return e
    write_json(f"{D}/loot_table/chests/kurgan.json", {
        "type": "minecraft:chest",
        "pools": [
            {"rolls": {"type": "minecraft:uniform", "min": 3, "max": 5}, "entries": [
                it(f"{NS}:saka_gold", 20, 1, 4),
                it("minecraft:gold_ingot", 25, 2, 6),
                it("minecraft:gold_nugget", 20, 4, 12),
                it(f"{NS}:kurt", 10, 2, 5),
                it(f"{NS}:kumys", 8, 1, 2),
                it("minecraft:bone", 12, 1, 4),
                it("minecraft:arrow", 10, 4, 10)]},
            {"rolls": 1, "entries": [
                it(f"{NS}:altyn_qylysh", 3),
                it(f"{NS}:altyn_adam_helmet", 3),
                it(f"{NS}:altyn_adam_chestplate", 2),
                it(f"{NS}:altyn_adam_leggings", 2),
                it(f"{NS}:altyn_adam_boots", 3),
                it(f"{NS}:qylysh", 6),
                it(f"{NS}:naiza", 6),
                it(f"{NS}:sadaq", 6),
                it(f"{NS}:dombyra", 4),
                it("minecraft:golden_apple", 4),
                {"type": "minecraft:empty", "weight": 10}]}],
        "random_sequence": f"{NS}:chests/kurgan"})


def tags():
    e = lambda n: f"{NS}:{n}"
    write_json(f"{D}/tags/item/saka_gold_repairable.json", {"values": [e("saka_gold")]})
    write_json(f"{MC}/tags/item/swords.json", {"values": [e("qylysh"), e("altyn_qylysh")]})
    write_json(f"{MC}/tags/item/spears.json", {"values": [e("naiza")]})
    write_json(f"{MC}/tags/item/enchantable/bow.json", {"values": [e("sadaq")]})
    write_json(f"{MC}/tags/item/enchantable/durability.json", {"values": [e("sadaq")]})
    write_json(f"{MC}/tags/item/head_armor.json", {"values": [e("altyn_adam_helmet")]})
    write_json(f"{MC}/tags/item/chest_armor.json", {"values": [e("altyn_adam_chestplate")]})
    write_json(f"{MC}/tags/item/leg_armor.json", {"values": [e("altyn_adam_leggings")]})
    write_json(f"{MC}/tags/item/foot_armor.json", {"values": [e("altyn_adam_boots")]})
    write_json(f"{MC}/tags/item/piglin_loved.json", {"values": [e("saka_gold")]})
    write_json(f"{MC}/tags/block/mineable/axe.json", {"values": [e("kerege"), e("shanyrak")]})
    steppe = ["minecraft:plains", "minecraft:sunflower_plains", "minecraft:savanna", "minecraft:savanna_plateau"]
    write_json(f"{D}/tags/worldgen/biome/steppe.json", {"values": steppe})
    write_json(f"{D}/tags/worldgen/biome/has_kurgan.json", {"values": steppe + ["minecraft:meadow", "minecraft:windswept_savanna"]})
    write_json(f"{D}/tags/worldgen/biome/berkut_habitat.json", {"values": [
        "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills", "minecraft:meadow",
        "minecraft:stony_peaks", "minecraft:savanna_plateau", "minecraft:plains"]})


def spawns():
    mod = f"{D}/forge/biome_modifier"
    write_json(f"{mod}/add_kokbori.json", {"type": "forge:add_spawns", "biomes": f"#{NS}:steppe",
                                          "spawners": [{"type": f"{NS}:kokbori", "count": {"type": "minecraft:uniform", "min_inclusive": 2, "max_inclusive": 4}, "weight": 6}]})
    write_json(f"{mod}/add_tulpar.json", {"type": "forge:add_spawns", "biomes": f"#{NS}:steppe",
                                         "spawners": [{"type": f"{NS}:tulpar", "count": {"type": "minecraft:uniform", "min_inclusive": 1, "max_inclusive": 3}, "weight": 3}]})
    write_json(f"{mod}/add_berkut.json", {"type": "forge:add_spawns", "biomes": f"#{NS}:berkut_habitat",
                                         "spawners": [{"type": f"{NS}:berkut", "count": {"type": "minecraft:uniform", "min_inclusive": 1, "max_inclusive": 2}, "weight": 4}]})


# ------------------------------------------------------------------ kurgan structure
def nbt_payload(tag, value):
    out = io.BytesIO()

    def w_str(s):
        b = s.encode("utf-8")
        out.write(struct.pack(">H", len(b)))
        out.write(b)

    def w(tag, v):
        if tag == 1:
            out.write(struct.pack(">b", v))
        elif tag == 6:
            out.write(struct.pack(">d", v))
        elif tag == 3:
            out.write(struct.pack(">i", v))
        elif tag == 8:
            w_str(v)
        elif tag == 9:
            etag, items = v
            out.write(struct.pack(">bi", etag, len(items)))
            for x in items:
                w(etag, x)
        elif tag == 10:
            for name, (t, val) in v.items():
                out.write(struct.pack(">b", t))
                w_str(name)
                w(t, val)
            out.write(b"\x00")
    w(tag, value)
    return out.getvalue()


def kurgan():
    S = 13  # size x/z
    Hh = 10
    palette = []
    pindex = {}

    def state(name, props=None):
        key = (name, tuple(sorted((props or {}).items())))
        if key not in pindex:
            entry = {"Name": (8, name)}
            if props:
                entry["Properties"] = (10, {k: (8, v) for k, v in props.items()})
            pindex[key] = len(palette)
            palette.append(entry)
        return pindex[key]

    blocks = {}
    rng = random.Random(1337)
    c = (S - 1) / 2
    SURF = 3  # layer of the natural surface (structure is sunk 3 blocks)

    def put(x, y, z, name, props=None, nbt=None):
        blocks[(x, y, z)] = (state(name, props), nbt)

    def brick():
        r = rng.random()
        return "minecraft:mossy_stone_bricks" if r < 0.25 else ("minecraft:cracked_stone_bricks" if r < 0.4 else "minecraft:stone_bricks")

    # burial chamber (layers 0..3), 5x5 outer
    for x in range(4, 9):
        for z in range(4, 9):
            put(x, 0, z, brick())
            put(x, 4, z, brick())
            for y in (1, 2, 3):
                if x in (4, 8) or z in (4, 8):
                    put(x, y, z, brick())
                else:
                    put(x, y, z, "minecraft:air")
    put(6, 1, 6, "minecraft:chest", {"facing": "south", "type": "single", "waterlogged": "false"},
        {"id": (8, "minecraft:chest"), "LootTable": (8, f"{NS}:chests/kurgan")})
    put(5, 1, 5, "minecraft:candle", {"candles": "3", "lit": "false", "waterlogged": "false"})
    put(7, 1, 7, "minecraft:skeleton_skull", {"rotation": "8", "powered": "false"})
    put(5, 1, 7, "minecraft:gold_block")

    # mound of earth above the surface
    for x in range(S):
        for z in range(S):
            r = math.hypot(x - c, z - c)
            if r <= 1.6:
                top = 8
            elif r <= 3.2:
                top = 7
            elif r <= 4.6:
                top = 6
            elif r <= 5.8:
                top = 5
            elif r <= 6.6:
                top = 4
            else:
                continue
            for y in range(SURF, top + 1):
                if (x, y, z) in blocks:
                    continue
                if y == top:
                    put(x, y, z, "minecraft:grass_block", {"snowy": "false"})
                else:
                    put(x, y, z, "minecraft:coarse_dirt" if rng.random() < 0.3 else "minecraft:dirt")
    # stone balbal on the top
    put(6, 9, 6, "minecraft:chiseled_stone_bricks")
    # ring of standing stones around the kurgan
    for ang in range(0, 360, 45):
        x = round(c + 6.4 * math.cos(math.radians(ang)))
        z = round(c + 6.4 * math.sin(math.radians(ang)))
        if 0 <= x < S and 0 <= z < S:
            put(x, 5, z, "minecraft:cobblestone_wall" if ang % 90 else "minecraft:mossy_cobblestone",
                {"east": "none", "north": "none", "south": "none", "west": "none", "up": "true", "waterlogged": "false"} if ang % 90 else None)

    block_list = []
    for (x, y, z), (st, nbt) in sorted(blocks.items()):
        entry = {"state": (3, st), "pos": (9, (3, [x, y, z]))}
        if nbt:
            entry["nbt"] = (10, nbt)
        block_list.append(entry)
    root = {
        "DataVersion": (3, 3955),
        "size": (9, (3, [S, Hh, S])),
        "palette": (9, (10, palette)),
        "blocks": (9, (10, block_list)),
        "entities": (9, (10, [])),
    }
    raw = b"\x0a" + struct.pack(">H", 0) + nbt_payload(10, root)
    path = f"{D}/structure/kurgan.nbt"
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with gzip.open(path, "wb") as fh:
        fh.write(raw)

    write_json(f"{D}/worldgen/template_pool/kurgan/start.json", {
        "fallback": "minecraft:empty",
        "elements": [{"weight": 1, "element": {
            "element_type": "minecraft:single_pool_element", "location": f"{NS}:kurgan",
            "processors": "minecraft:empty", "projection": "rigid"}}]})
    write_json(f"{D}/worldgen/structure/kurgan.json", {
        "type": "minecraft:jigsaw",
        "biomes": f"#{NS}:has_kurgan",
        "step": "surface_structures",
        "spawn_overrides": {},
        "terrain_adaptation": "beard_thin",
        "start_pool": f"{NS}:kurgan/start",
        "size": 1,
        "start_height": {"absolute": -SURF},
        "project_start_to_heightmap": "WORLD_SURFACE_WG",
        "max_distance_from_center": 80,
        "use_expansion_hack": False})
    write_json(f"{D}/worldgen/structure_set/kurgan.json", {
        "structures": [{"structure": f"{NS}:kurgan", "weight": 1}],
        "placement": {"type": "minecraft:random_spread", "spacing": 28, "separation": 10, "salt": 1991121}})


# ------------------------------------------------------------------ language
NAMES = {
    # key: (ru, kk, en)
    "item.ezhelgi.saka_gold": ("Сакское золото", "Сақ алтыны", "Saka Gold"),
    "item.ezhelgi.qylysh": ("Кылыш (сабля батыра)", "Қылыш", "Qylysh Sabre"),
    "item.ezhelgi.altyn_qylysh": ("Золотой кылыш", "Алтын қылыш", "Golden Qylysh"),
    "item.ezhelgi.naiza": ("Найза (копьё)", "Найза", "Naiza Spear"),
    "item.ezhelgi.sadaq": ("Садак (лук кочевника)", "Садақ", "Sadaq Bow"),
    "item.ezhelgi.altyn_adam_helmet": ("Шлем Золотого человека", "Алтын адам бас киімі", "Golden Man Helmet"),
    "item.ezhelgi.altyn_adam_chestplate": ("Доспех Золотого человека", "Алтын адам сауыты", "Golden Man Chestplate"),
    "item.ezhelgi.altyn_adam_leggings": ("Поножи Золотого человека", "Алтын адам балақшасы", "Golden Man Leggings"),
    "item.ezhelgi.altyn_adam_boots": ("Сапоги Золотого человека", "Алтын адам етігі", "Golden Man Boots"),
    "item.ezhelgi.beshbarmak": ("Бешбармак", "Бешбармақ", "Beshbarmak"),
    "item.ezhelgi.baursak": ("Баурсак", "Бауырсақ", "Baursak"),
    "item.ezhelgi.kurt": ("Курт", "Құрт", "Kurt"),
    "item.ezhelgi.kazy": ("Казы", "Қазы", "Kazy"),
    "item.ezhelgi.kumys": ("Кумыс", "Қымыз", "Kumys"),
    "item.ezhelgi.shubat": ("Шубат", "Шұбат", "Shubat"),
    "item.ezhelgi.dombyra": ("Домбра", "Домбыра", "Dombyra"),
    "item.ezhelgi.kiiz_uy": ("Юрта (набор)", "Киіз үй", "Yurt Kit"),
    "item.ezhelgi.kokbori_spawn_egg": ("Яйцо призыва кокбори", "Көкбөрі жұмыртқасы", "Kokbori Spawn Egg"),
    "item.ezhelgi.tulpar_spawn_egg": ("Яйцо призыва тулпара", "Тұлпар жұмыртқасы", "Tulpar Spawn Egg"),
    "item.ezhelgi.berkut_spawn_egg": ("Яйцо призыва беркута", "Бүркіт жұмыртқасы", "Berkut Spawn Egg"),
    "block.ezhelgi.kiiz": ("Войлок (киiз)", "Киіз", "Kiiz Felt"),
    "block.ezhelgi.kerege": ("Кереге (стена юрты)", "Кереге", "Kerege Lattice"),
    "block.ezhelgi.shanyrak": ("Шанырак", "Шаңырақ", "Shanyrak"),
    "block.ezhelgi.syrmak": ("Сырмак (ковёр)", "Сырмақ", "Syrmak Carpet"),
    "entity.ezhelgi.kokbori": ("Кокбори", "Көкбөрі", "Kokbori"),
    "entity.ezhelgi.tulpar": ("Тулпар", "Тұлпар", "Tulpar"),
    "entity.ezhelgi.berkut": ("Беркут", "Бүркіт", "Berkut"),
    "itemGroup.ezhelgi": ("Древний Казахстан", "Ежелгі Қазақстан", "Ancient Kazakhstan"),
    "message.ezhelgi.kiiz_uy.no_room": ("Здесь мало места для юрты: нужна ровная площадка 9×9 и высота 6 блоков.",
                                        "Киіз үйге орын жетпейді: 9×9 тегіс алаң және 6 блок биіктік керек.",
                                        "Not enough room for a yurt: needs a flat 9×9 area, 6 blocks high."),
}


# ------------------------------------------------------------------ yurts, carpets and the aul
def write_template(path, tpl, data_version=3955):
    palette, pindex, block_list = [], {}, []
    for (x, y, z), (name, props, nbt) in sorted(tpl.blocks.items()):
        key = (name, tuple(sorted(props.items())))
        if key not in pindex:
            entry = {"Name": (8, name)}
            if props:
                entry["Properties"] = (10, {k: (8, v) for k, v in props.items()})
            pindex[key] = len(palette)
            palette.append(entry)
        e = {"state": (3, pindex[key]), "pos": (9, (3, [x, y, z]))}
        if nbt:
            e["nbt"] = (10, nbt)
        block_list.append(e)
    root = {
        "DataVersion": (3, data_version),
        "size": (9, (3, list(tpl.size))),
        "palette": (9, (10, palette)),
        "blocks": (9, (10, block_list)),
        "entities": (9, (10, [
            {"pos": (9, (6, [float(x), float(y), float(z)])),
             "blockPos": (9, (3, [int(math.floor(x)), int(y), int(math.floor(z))])),
             "nbt": (10, nbt)}
            for (x, y, z, nbt) in getattr(tpl, "entities", [])])),
    }
    raw = b"\x0a" + struct.pack(">H", 0) + nbt_payload(10, root)
    full = f"{D}/structure/{path}.nbt"
    os.makedirs(os.path.dirname(full), exist_ok=True)
    with gzip.GzipFile(full, "wb", mtime=0) as fh:
        fh.write(raw)


CARPET_MOTIF = [
    "........",
    ".11..11.",
    "1..11..1",
    "1.1..1.1",
    ".1....1.",
    "...11...",
    "...11...",
    "........",
]


def motif_carpet(name, bg, c1, c2, seed):
    c = Canvas(seed=seed)
    c.rect(0, 0, 15, 15, bg)
    c.noise(0.05)
    for cy in (0, 8):
        for cx in (0, 8):
            col = c1 if (cx + cy) % 16 == 0 else c2
            for y, row in enumerate(CARPET_MOTIF):
                for x, ch in enumerate(row):
                    if ch == "1":
                        c.set(cx + x, cy + y, col)
    c.save("block/" + name)


def yurt_textures():
    # Киіз: чистый белый войлок
    c = Canvas(seed=41)
    c.rect(0, 0, 15, 15, "#f1eee5")
    c.noise(0.035)
    rng = random.Random(41)
    for _ in range(14):
        x, y = rng.randint(0, 15), rng.randint(0, 15)
        c.set(x, y, "#e2ddd0")
    c.save("block/kiiz")

    # Басқұр: белый войлок с красной лентой и золотым зигзагом
    c = Canvas(seed=42)
    c.rect(0, 0, 15, 15, "#f1eee5")
    c.noise(0.03)
    c.rect(0, 4, 15, 11, "#a3201c")
    c.line(0, 4, 15, 4, "#d9a521"); c.line(0, 11, 15, 11, "#d9a521")
    for x in range(16):
        y = 6 + (x % 4 if x % 8 < 4 else 3 - x % 4)
        c.set(x, y, "#f0c040")
        c.set(x, 13 - y + 4, "#2c4a8c") if 5 <= 13 - y + 4 <= 10 else None
    c.save("block/baskur")

    motif_carpet("syrmak", "#a3201c", "#f0dcaa", "#e8b23a", 43)
    motif_carpet("kok_syrmak", "#1f4e8c", "#f4f1e8", "#e8b23a", 44)

    # Текемет: бежевый войлок с коричневыми завитками и рамкой
    c = Canvas(seed=45)
    c.rect(0, 0, 15, 15, "#d6c193")
    c.noise(0.06)
    for i in range(16):
        c.set(i, 0, "#6b3f1f"); c.set(i, 15, "#6b3f1f"); c.set(0, i, "#6b3f1f"); c.set(15, i, "#6b3f1f")
    for y, row in enumerate(CARPET_MOTIF):
        for x, ch in enumerate(row):
            if ch == "1":
                c.set(4 + x, 4 + y, "#7a4a24")
    c.save("block/tekemet")

    # Алаша: тканые полосы
    c = Canvas(seed=46)
    stripes = ["#b4251f", "#b4251f", "#e8b23a", "#2e7d3a", "#2e7d3a", "#f1eee5", "#1f4e8c", "#1f4e8c"]
    for y in range(16):
        c.line(0, y, 15, y, stripes[y % 8])
    for x in range(16):
        c.set(x, 5 if x % 2 else 4, "#f1eee5")
        c.set(x, 13 if x % 2 else 12, "#e8b23a")
    c.noise(0.05)
    c.save("block/alasha")

    # Өрнек: чёрный с золотой сеткой ромбов
    c = Canvas(seed=47)
    c.rect(0, 0, 15, 15, "#1d1a1a")
    c.noise(0.04)
    for y in range(16):
        for x in range(16):
            if (x + y) % 8 == 0 or (x - y) % 8 == 0:
                c.set(x, y, "#e2a92a")
            if (x % 8 == 4 and y % 8 == 4):
                c.set(x, y, "#b4251f")
    c.save("block/ornek")

    # иконка большой юрты
    pal = {"o": "#3a2412", "w": "#f6f1e4", "W": "#ffffff", "g": "#e8b23a", "R": "#b4251f", "h": "#8b5a2b", "d": "#6b3f1f"}
    art = [
        "................",
        ".......hh.......",
        "......oggo......",
        ".....owgwwo.....",
        "....owwgwwwo....",
        "...owwwgwwwwo...",
        "..owwwwgwwwwwo..",
        ".oRRRRRRRRRRRRo.",
        ".ogggggggggggggo",
        ".owwhwwwwwwhwwo.",
        ".owwhwwddwwhwwo.",
        ".owwhwwdgdwhwwo.",
        ".owwhwwddwwhwwo.",
        ".oRRRRRddRRRRRo.",
        ".oooooooooooooo.",
        "................",
    ]
    from_art([r[:16].ljust(16, ".") for r in art], pal).save("item/ulken_kiiz_uy")


def yurt_models():
    blk = lambda n: f"{NS}:block/{n}"
    for b in ["baskur"]:
        write_json(f"{A}/blockstates/{b}.json", {"variants": {"": {"model": blk(b)}}})
        write_json(f"{A}/models/block/{b}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": blk(b)}})
        write_json(f"{A}/items/{b}.json", {"model": {"type": "minecraft:model", "model": blk(b)}})
    for b in ["kok_syrmak", "tekemet", "alasha", "ornek"]:
        write_json(f"{A}/blockstates/{b}.json", {"variants": {"": {"model": blk(b)}}})
        write_json(f"{A}/models/block/{b}.json", {"parent": "minecraft:block/carpet", "textures": {"wool": blk(b)}})
        write_json(f"{A}/items/{b}.json", {"model": {"type": "minecraft:model", "model": blk(b)}})
    tex = {"bottom": blk("kiiz"), "top": blk("kiiz"), "side": blk("kiiz")}
    write_json(f"{A}/models/block/kiiz_stairs.json", {"parent": "minecraft:block/stairs", "textures": tex})
    write_json(f"{A}/models/block/kiiz_stairs_inner.json", {"parent": "minecraft:block/inner_stairs", "textures": tex})
    write_json(f"{A}/models/block/kiiz_stairs_outer.json", {"parent": "minecraft:block/outer_stairs", "textures": tex})
    write_json(f"{A}/models/block/kiiz_slab.json", {"parent": "minecraft:block/slab", "textures": tex})
    write_json(f"{A}/models/block/kiiz_slab_top.json", {"parent": "minecraft:block/slab_top", "textures": tex})
    base = {"east": 0, "south": 90, "west": 180, "north": 270}
    variants = {}
    for facing, y0 in base.items():
        for half in ("bottom", "top"):
            for shape in ("straight", "inner_left", "inner_right", "outer_left", "outer_right"):
                model = blk("kiiz_stairs" + ("_inner" if shape.startswith("inner") else "_outer" if shape.startswith("outer") else ""))
                y = y0
                if half == "bottom" and shape.endswith("left"):
                    y = y0 - 90
                if half == "top" and shape.endswith("right"):
                    y = y0 + 90
                v = {"model": model}
                y %= 360
                if half == "top":
                    v["x"] = 180
                if y:
                    v["y"] = y
                if half == "top" or y:
                    v["uvlock"] = True
                variants[f"facing={facing},half={half},shape={shape}"] = v
    write_json(f"{A}/blockstates/kiiz_stairs.json", {"variants": variants})
    write_json(f"{A}/blockstates/kiiz_slab.json", {"variants": {
        "type=bottom": {"model": blk("kiiz_slab")},
        "type=top": {"model": blk("kiiz_slab_top")},
        "type=double": {"model": blk("kiiz")}}})
    write_json(f"{A}/items/kiiz_stairs.json", {"model": {"type": "minecraft:model", "model": blk("kiiz_stairs")}})
    write_json(f"{A}/items/kiiz_slab.json", {"model": {"type": "minecraft:model", "model": blk("kiiz_slab")}})
    write_json(f"{A}/models/item/ulken_kiiz_uy.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/ulken_kiiz_uy"}})
    write_json(f"{A}/items/ulken_kiiz_uy.json", {"model": {"type": "minecraft:model", "model": f"{NS}:item/ulken_kiiz_uy"}})


def yurt_data():
    e = lambda n: f"{NS}:{n}"
    for b in ["baskur", "kiiz_stairs", "kiiz_slab", "kok_syrmak", "tekemet", "alasha", "ornek"]:
        write_json(f"{D}/loot_table/blocks/{b}.json", {
            "type": "minecraft:block",
            "pools": [{"condition": {"type": "minecraft:survives_explosion"},
                       "entries": [{"type": "minecraft:item", "name": e(b)}], "rolls": 1}],
            "random_sequence": f"{NS}:blocks/{b}"})
    shaped("kiiz_stairs", ["K  ", "KK ", "KKK"], {"K": e("kiiz")}, e("kiiz_stairs"), 4, "building")
    shaped("kiiz_slab", ["KKK"], {"K": e("kiiz")}, e("kiiz_slab"), 6, "building")
    shapeless("baskur", [e("kiiz"), e("kiiz"), "minecraft:red_dye", "minecraft:gold_nugget"], e("baskur"), 2, "building")
    shaped("kok_syrmak", ["BW"], {"B": "minecraft:blue_wool", "W": "minecraft:white_wool"}, e("kok_syrmak"), 4, "building")
    shaped("tekemet", ["NW"], {"N": "minecraft:brown_wool", "W": "minecraft:white_wool"}, e("tekemet"), 4, "building")
    shapeless("alasha", ["minecraft:red_wool", "minecraft:yellow_wool", "minecraft:green_wool", "minecraft:blue_wool"], e("alasha"), 6, "building")
    shapeless("ornek", ["minecraft:black_wool", "minecraft:black_wool", "minecraft:gold_nugget"], e("ornek"), 4, "building")
    shaped("ulken_kiiz_uy", ["BSB", "KUK", "MMM"], {"B": e("baskur"), "S": e("shanyrak"), "K": e("kiiz_stairs"),
                                                    "U": e("kiiz_uy"), "M": e("syrmak")}, e("ulken_kiiz_uy"))

    def it(name, w, lo=1, hi=1):
        x = {"type": "minecraft:item", "name": name, "weight": w}
        if hi > 1:
            x["modifier"] = [{"type": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}}]
        return x
    write_json(f"{D}/loot_table/chests/yurt.json", {
        "type": "minecraft:chest",
        "pools": [{"rolls": {"type": "minecraft:uniform", "min": 3, "max": 6}, "entries": [
            it(e("baursak"), 20, 2, 8), it(e("kurt"), 18, 2, 6), it(e("kazy"), 12, 1, 3), it(e("kumys"), 12, 1, 3),
            it(e("shubat"), 8, 1, 2), it(e("beshbarmak"), 6), it("minecraft:wheat", 10, 2, 6),
            it(e("syrmak"), 6, 1, 3), it(e("alasha"), 6, 1, 3), it("minecraft:saddle", 4), it(e("dombyra"), 3),
            it(e("saka_gold"), 2), it("minecraft:gold_ingot", 4, 1, 2)]}],
        "random_sequence": f"{NS}:chests/yurt"})
    write_json(f"{MC}/tags/block/stairs.json", {"values": [e("kiiz_stairs")]})
    write_json(f"{MC}/tags/block/slabs.json", {"values": [e("kiiz_slab")]})
    write_json(f"{MC}/tags/item/stairs.json", {"values": [e("kiiz_stairs")]})
    write_json(f"{MC}/tags/item/slabs.json", {"values": [e("kiiz_slab")]})
    # тұлпар: место для седла и конской брони
    write_json(f"{MC}/tags/entity_type/can_equip_saddle.json", {"values": [e("tulpar")]})
    write_json(f"{MC}/tags/entity_type/can_wear_horse_armor.json", {"values": [e("tulpar")]})

    # шаблоны юрт, аулов, стоянок и кургана золотого воина
    import yurts
    write_template("yurt/yurt_small", yurts.yurt("aq_small", 2))
    write_template("yurt/yurt_big", yurts.yurt("aq_big", 1))
    write_template("yurt/yurt_qonyr", yurts.yurt("qonyr", 3))
    write_template("yurt/yurt_otau", yurts.yurt("otau", 4))
    write_template("yurt/yurt_han", yurts.yurt("han", 5))
    write_template("aul/small", yurts.build_aul_small())
    write_template("aul/medium", yurts.build_aul_medium())
    write_template("aul/large", yurts.build_aul_large())
    write_template("camp/camp_yurt", yurts.build_camp(0))
    write_template("camp/camp_fire", yurts.build_camp(1))
    write_template("altyn_qorym", yurts.build_burial())

    def pool(name, elements):
        write_json(f"{D}/worldgen/template_pool/{name}.json", {
            "fallback": "minecraft:empty",
            "elements": [{"weight": w, "element": {
                "element_type": "minecraft:single_pool_element", "location": f"{NS}:{loc}",
                "processors": "minecraft:empty", "projection": "rigid"}} for loc, w in elements]})

    def structure(name, biomes, pool_name, adapt, depth, spacing, separation, salt):
        write_json(f"{D}/worldgen/structure/{name}.json", {
            "type": "minecraft:jigsaw",
            "biomes": biomes,
            "step": "surface_structures",
            "spawn_overrides": {},
            "terrain_adaptation": adapt,
            "start_pool": f"{NS}:{pool_name}",
            "size": 1,
            "start_height": {"absolute": depth},
            "project_start_to_heightmap": "WORLD_SURFACE_WG",
            "max_distance_from_center": 116,
            "use_expansion_hack": False})
        write_json(f"{D}/worldgen/structure_set/{name}.json", {
            "structures": [{"structure": f"{NS}:{name}", "weight": 1}],
            "placement": {"type": "minecraft:random_spread", "spacing": spacing, "separation": separation, "salt": salt}})

    pool("aul/start", [("aul/small", 5), ("aul/medium", 3), ("aul/large", 1)])
    structure("aul", f"#{NS}:steppe", "aul/start", "beard_box", -1, 34, 8, 772031)
    pool("camp/start", [("camp/camp_yurt", 1), ("camp/camp_fire", 1)])
    structure("camp", f"#{NS}:has_kurgan", "camp/start", "beard_thin", -1, 18, 6, 553901)
    pool("altyn_qorym/start", [("altyn_qorym", 1)])
    structure("altyn_qorym", f"#{NS}:steppe", "altyn_qorym/start", "beard_thin", -5, 44, 16, 918273)
    write_template("altyn_adam_qorgany", yurts.build_burial(boss=True))
    pool("altyn_adam_qorgany/start", [("altyn_adam_qorgany", 1)])
    structure("altyn_adam_qorgany", f"#{NS}:has_kurgan", "altyn_adam_qorgany/start", "beard_thin", -8, 64, 24, 330517)
    for old_file in (f"{D}/structure/aul.nbt",):
        if os.path.exists(old_file):
            os.remove(old_file)


NAMES.update({
    "item.ezhelgi.ulken_kiiz_uy": ("Большая юрта (набор)", "Үлкен киіз үй", "Big Yurt Kit"),
    "block.ezhelgi.kiiz_stairs": ("Войлочные ступени", "Киіз баспалдақ", "Kiiz Stairs"),
    "block.ezhelgi.kiiz_slab": ("Войлочная плита", "Киіз тақта", "Kiiz Slab"),
    "block.ezhelgi.baskur": ("Баскур (узорная лента)", "Басқұр", "Baskur Band"),
    "block.ezhelgi.kok_syrmak": ("Синий сырмак", "Көк сырмақ", "Blue Syrmak"),
    "block.ezhelgi.tekemet": ("Текемет", "Текемет", "Tekemet Felt Rug"),
    "block.ezhelgi.alasha": ("Алаша (полосатый ковёр)", "Алаша", "Alasha Rug"),
    "block.ezhelgi.ornek": ("Орнек (узорный ковёр)", "Өрнек", "Ornek Rug"),
    "message.ezhelgi.kiiz_uy.no_room": ("Здесь мало места для юрты: нужна свободная ровная площадка.",
                                        "Киіз үйге орын жетпейді: бос тегіс алаң керек.",
                                        "Not enough room for a yurt: needs a clear flat area."),
})


# ------------------------------------------------------------------ v3: воин, украшения, новые юрты
def stairs_and_slab(name, tex_block):
    blk = lambda n: f"{NS}:block/{n}"
    tex = {"bottom": blk(tex_block), "top": blk(tex_block), "side": blk(tex_block)}
    write_json(f"{A}/models/block/{name}_stairs.json", {"parent": "minecraft:block/stairs", "textures": tex})
    write_json(f"{A}/models/block/{name}_stairs_inner.json", {"parent": "minecraft:block/inner_stairs", "textures": tex})
    write_json(f"{A}/models/block/{name}_stairs_outer.json", {"parent": "minecraft:block/outer_stairs", "textures": tex})
    write_json(f"{A}/models/block/{name}_slab.json", {"parent": "minecraft:block/slab", "textures": tex})
    write_json(f"{A}/models/block/{name}_slab_top.json", {"parent": "minecraft:block/slab_top", "textures": tex})
    base = {"east": 0, "south": 90, "west": 180, "north": 270}
    variants = {}
    for facing, y0 in base.items():
        for half in ("bottom", "top"):
            for shape in ("straight", "inner_left", "inner_right", "outer_left", "outer_right"):
                suffix = "_inner" if shape.startswith("inner") else "_outer" if shape.startswith("outer") else ""
                y = y0
                if half == "bottom" and shape.endswith("left"):
                    y = y0 - 90
                if half == "top" and shape.endswith("right"):
                    y = y0 + 90
                y %= 360
                v = {"model": blk(name + "_stairs" + suffix)}
                if half == "top":
                    v["x"] = 180
                if y:
                    v["y"] = y
                if half == "top" or y:
                    v["uvlock"] = True
                variants[f"facing={facing},half={half},shape={shape}"] = v
    write_json(f"{A}/blockstates/{name}_stairs.json", {"variants": variants})
    write_json(f"{A}/blockstates/{name}_slab.json", {"variants": {
        "type=bottom": {"model": blk(name + "_slab")},
        "type=top": {"model": blk(name + "_slab_top")},
        "type=double": {"model": blk(tex_block)}}})
    write_json(f"{A}/items/{name}_stairs.json", {"model": {"type": "minecraft:model", "model": blk(name + "_stairs")}})
    write_json(f"{A}/items/{name}_slab.json", {"model": {"type": "minecraft:model", "model": blk(name + "_slab")}})


def cube_block(name):
    blk = f"{NS}:block/{name}"
    write_json(f"{A}/blockstates/{name}.json", {"variants": {"": {"model": blk}}})
    write_json(f"{A}/models/block/{name}.json", {"parent": "minecraft:block/cube_all", "textures": {"all": blk}})
    write_json(f"{A}/items/{name}.json", {"model": {"type": "minecraft:model", "model": blk}})


def simple_item(name, handheld=False):
    write_json(f"{A}/models/item/{name}.json", {
        "parent": "minecraft:item/handheld" if handheld else "minecraft:item/generated",
        "textures": {"layer0": f"{NS}:item/{name}"}})
    write_json(f"{A}/items/{name}.json", {"model": {"type": "minecraft:model", "model": f"{NS}:item/{name}"}})


ACC_PAL = dict(GOLD, t="#2bb5a6", r="#c0392b", b="#3a2412", w="#fff8e0", g="#8a8f98")

ACCESSORY_ART = {
    "barys_alqasy": [  # гривна с головами снежных барсов
        "................",
        "................",
        "....kkkkkkkk....",
        "...kmllllllmk...",
        "..kml......mdk..",
        ".kml........mdk.",
        ".kml........mdk.",
        ".kmk........kdk.",
        ".kmk........kdk.",
        "..kmk......kdk..",
        "..kmlkk..kkmdk..",
        "..kmtmk..kmtdk..",
        "...kmmk..kmdk...",
        "....kk....kk....",
        "................",
        "................",
    ],
    "bugy_tumary": [  # олень с ветвистыми рогами (сакский стиль)
        "................",
        "..k.k.k.........",
        "..lklkl.k.......",
        "..mlmlmkl.......",
        "...kmmmlk.......",
        "....kmmk........",
        "...kmtmk........",
        "...kmmmmkkkkk...",
        "..kmllllllllmk..",
        "..kmmmmmmmmmdk..",
        "..kmdkkkkkkmdk..",
        "...kdk....kdk...",
        "...kmk....kmk...",
        "....k......k....",
        "................",
        "................",
    ],
    "qasqyr_bilezigi": [  # браслет с головой волка
        "................",
        "................",
        ".....kkkkkk.....",
        "...kkmlllmmkk...",
        "..kml......mdk..",
        ".kml..kggk..mdk.",
        ".kml.kggggk.mdk.",
        ".kmk.kgtgtk.kdk.",
        ".kmk..kggk..kdk.",
        ".kml...kk...mdk.",
        "..kml......mdk..",
        "...kkmmmmmdkk...",
        ".....kkkkkk.....",
        "................",
        "................",
        "................",
    ],
    "arqar_syrgasy": [  # серьги с закрученными рогами архара
        "................",
        "...kk......kk...",
        "..k..k....k..k..",
        "..k..k....k..k..",
        "...kk......kk...",
        "...km......mk...",
        "..kllmk..kmllk..",
        ".klk.mk..km.klk.",
        ".kmk.mk..km.kmk.",
        "..kmmmk..kmmmk..",
        "...ktk....ktk...",
        "...kmk....kmk...",
        "....k......k....",
        "................",
        "................",
        "................",
    ],
    "tulpar_juzigi": [  # перстень с крылатым конём
        "................",
        "................",
        "..kk..kkkk..kk..",
        ".kwlkkmllmkklwk.",
        ".kwwlkmrrmklwwk.",
        "..kwwkmmmmkwwk..",
        "...kkkmmmmkkk...",
        ".....kkddkk.....",
        "....km....mk....",
        "...km......mk...",
        "...kl......dk...",
        "...km......mk...",
        "....km....mk....",
        ".....kkkkkk.....",
        "................",
        "................",
    ],
    "burkit_belbeui": [  # поясная бляха с беркутом
        "................",
        "................",
        "................",
        ".kkkkkkkkkkkkkk.",
        ".kllllllllllllk.",
        ".klk...kk...kdk.",
        ".kl.k.kmmk.k.dk.",
        ".kl..kmtmmk..dk.",
        ".klkkmmmmmmkkdk.",
        ".kl.kmmmmmmk.dk.",
        ".kl...k..k...dk.",
        ".kmddddddddddmk.",
        ".kkkkkkkkkkkkkk.",
        "................",
        "................",
        "................",
    ],
}

KIT_ART = {
    "qonyr_uy": ({"o": "#2a1a10", "q": "#7a4a2a", "Q": "#94603a", "w": "#efe6d2", "h": "#5a3518", "d": "#3a2412"}, [
        "................",
        ".......hh.......",
        "......oqqo......",
        ".....oqQqqo.....",
        "....oqwwwwqo....",
        "...oqqqqqqqqo...",
        "..oqqQqqqqQqqo..",
        ".owwwwwwwwwwwwo.",
        ".oqwqwqwqwqwqwo.",
        ".oqqqqqqqqqqqqo.",
        ".oqqqqqddqqqqqo.",
        ".oqqqqqddqqqqqo.",
        ".owqwqwddwqwqwo.",
        ".owwwwwddwwwwwo.",
        ".oooooooooooooo.",
        "................"]),
    "otau": ({"o": "#3a2412", "w": "#f6f1e4", "R": "#b4251f", "r": "#d0402c", "d": "#6b2a1a", "g": "#e8b23a"}, [
        "................",
        "................",
        ".......gg.......",
        "......oRRo......",
        ".....owRwwo.....",
        "....owwRwwwo....",
        "...owwwRwwwwo...",
        "..oRRRRRRRRRRo..",
        "..owwwwwwwwwwo..",
        "..owrwwddwwrwo..",
        "..owwwwddwwwwo..",
        "..owrwwddwwrwo..",
        "..oRRRRddRRRRo..",
        "..oooooooooooo..",
        "................",
        "................"]),
    "han_ordasy": ({"o": "#3a2412", "w": "#f6f1e4", "g": "#f2c230", "G": "#b9831a", "R": "#b4251f", "d": "#4a2a14"}, [
        "......gGGg......",
        ".......gg.......",
        "......owwo......",
        "....owwgwwwo....",
        "...owwwgwwwwo...",
        "..owwwwgwwwwwo..",
        ".owwwwwgwwwwwwo.",
        "oggggggggggggggo",
        "owwgwwwwwwwwgwwo",
        "owwgwwwddwwwgwwo",
        "owwgwwdggdwwgwwo",
        "owwgwwdggdwwgwwo",
        "oRRRRRRddRRRRRRo",
        "oggggggggggggggo",
        "oooooooooooooooo",
        "................"]),
}


def v3_textures():
    for name, art in ACCESSORY_ART.items():
        from_art(art, ACC_PAL).save("item/" + name)
    for name, (pal, art) in KIT_ART.items():
        from_art(art, pal).save("item/" + name)

    # Алтын қанжар — акинак с «бабочкой» у рукояти
    c = Canvas(seed=61)
    for i in range(7):
        x, y = 6 + i, 9 - i
        c.set(x, y, GOLD["m"]); c.set(x + 1, y, GOLD["l"]); c.set(x, y + 1, GOLD["d"])
    c.set(13, 2, GOLD["l"]); c.set(14, 1, GOLD["l"])
    for (x, y) in [(4, 9), (5, 10), (6, 11), (3, 8), (7, 12), (5, 9), (6, 10)]:
        c.set(x, y, "#c0392b" if (x + y) % 2 else GOLD["m"])
    c.line(4, 11, 2, 13, "#5a3418")
    c.set(1, 14, GOLD["l"]); c.set(2, 14, GOLD["m"]); c.set(1, 13, GOLD["m"])
    c.outline("#20160c")
    c.save("item/altyn_qanjar")

    # яйцо золотого воина
    c = Canvas(seed=62)
    for y in range(16):
        for x in range(16):
            dx, dy = (x - 7.5) / 5.2, (y - 8.8) / 6.6
            if dy < 0:
                dx *= 1.15
            if dx * dx + dy * dy <= 1:
                c.set(x, y, "#e8b23a" if (x + y) % 4 else "#b4251f")
    c.noise(0.05)
    c.outline("#1c1c1c")
    c.save("item/altyn_jauynger_spawn_egg")

    # блоки: коричневый войлок и орнаменты
    c = Canvas(seed=63)
    c.rect(0, 0, 15, 15, "#7a4a2a")
    c.noise(0.05)
    rng = random.Random(63)
    for _ in range(12):
        c.set(rng.randint(0, 15), rng.randint(0, 15), "#6a3e22")
    c.save("block/qonyr_kiiz")
    motif_carpet("qonyr_oiu", "#7a4a2a", "#f1eee5", "#f1eee5", 64)
    motif_carpet("aq_oiu", "#f1eee5", "#7a4a2a", "#a3201c", 65)

    # GUI окна аксессуаров
    g = Canvas(256, 256, seed=66)
    W, H = 176, 166
    g.rect(0, 0, W - 1, H - 1, "#c6c6c6")
    for x in range(W):
        g.set(x, 0, "#000000"); g.set(x, H - 1, "#000000")
        g.set(x, 1, "#ffffff"); g.set(x, H - 2, "#555555")
    for y in range(H):
        g.set(0, y, "#000000"); g.set(W - 1, y, "#000000")
        g.set(1, y, "#ffffff"); g.set(W - 2, y, "#555555")

    def slot(x, y, gold=False):
        x0, y0 = x - 1, y - 1
        g.rect(x0, y0, x0 + 17, y0 + 17, "#8b8b8b")
        dark, light = ("#7a4f0c", "#fff3a8") if gold else ("#373737", "#ffffff")
        for i in range(18):
            g.set(x0 + i, y0, dark); g.set(x0, y0 + i, dark)
            g.set(x0 + i, y0 + 17, light); g.set(x0 + 17, y0 + i, light)
        if gold:
            g.set(x0 + 8, y0 + 8, "#b9831a"); g.set(x0 + 9, y0 + 9, "#b9831a")
            g.set(x0 + 8, y0 + 9, "#e8b23a"); g.set(x0 + 9, y0 + 8, "#e8b23a")
    for (sx, sy) in [(53, 34), (71, 34), (89, 34), (107, 34)]:
        slot(sx, sy, gold=True)
    # орнамент «қошқар мүйіз» по бокам от слотов
    for ox in (20, 134):
        for yy, row in enumerate(CARPET_MOTIF):
            for xx, ch in enumerate(row):
                if ch == "1":
                    g.set(ox + xx * 2, 30 + yy * 2, "#b9831a"); g.set(ox + xx * 2 + 1, 30 + yy * 2, "#b9831a")
    for row in range(3):
        for col in range(9):
            slot(8 + col * 18, 84 + row * 18)
    for col in range(9):
        slot(8 + col * 18, 142)
    g.save("gui/accessories")

    warrior_skin()


def warrior_skin():
    c = Canvas(64, 64, seed=71)
    SK, SKD, HAIR = (176, 120, 82, 255), (150, 98, 64, 255), (34, 24, 20, 255)

    def face(fname, x, y, w, h):
        pass
    # голова: смуглая кожа, чёрные волосы, глаза и усы
    head_kind = lambda y: "hair" if y <= 1 else "skin"
    for (u, v, w, h) in [(0, 8, 8, 8), (16, 8, 8, 8), (24, 8, 8, 8)]:  # бока и затылок
        for yy in range(h):
            for xx in range(w):
                c.set(u + xx, v + yy, HAIR if (yy <= 3 or (u == 24)) else SK)
    for xx in range(8):
        for yy in range(8):
            c.set(8 + xx, yy, HAIR)          # макушка
            c.set(16 + xx, yy, SKD)          # низ головы
            col = SK
            if yy <= 1:
                col = HAIR
            if yy == 3 and xx in (1, 2, 5, 6):
                col = HAIR
            if yy == 4 and xx in (1, 6):
                col = (240, 240, 240, 255)
            if yy == 4 and xx in (2, 5):
                col = (40, 30, 24, 255)
            if yy == 6 and 2 <= xx <= 5:
                col = HAIR
            c.set(8 + xx, 8 + yy, col)
    # головной убор (слой шапки): золотой обод с бирюзой и красная шапка
    _box(c, 32, 0, 8, 8, 8, lambda y: "band" if y <= 2 else "none", top_kind="gold")
    for xx in range(8):
        for yy in range(3, 8):
            for u0 in (32, 40, 48, 56):
                pass
    # кафтан, рукава с кистями рук, штаны и сапоги
    _box(c, 16, 16, 8, 12, 4, lambda y: "gold" if y == 0 else ("band" if y in (8, 9, 10) else "plaques"),
         top_kind="gold", bottom_kind="plaques")
    for y in range(1, 8):
        c.set(23, 20 + y, ARMOR["gl"]); c.set(24, 20 + y, ARMOR["gd"])
    _box(c, 40, 16, 4, 12, 4, lambda y: "gold" if y in (0, 1, 8, 9) else ("skin" if y >= 10 else "plaques"),
         top_kind="gold", bottom_kind="skin")
    _box(c, 0, 16, 4, 12, 4, lambda y: "band" if y == 7 else ("gold" if y >= 8 else "plaques"),
         top_kind="plaques", bottom_kind="sole")
    c.save("entity/altyn_jauynger")


def v3_models():
    cube_block("qonyr_kiiz")
    cube_block("qonyr_oiu")
    cube_block("aq_oiu")
    stairs_and_slab("qonyr_kiiz", "qonyr_kiiz")
    for name in list(ACCESSORY_ART) + list(KIT_ART) + ["altyn_jauynger_spawn_egg"]:
        simple_item(name)
    simple_item("altyn_qanjar", handheld=True)


def v3_data():
    e = lambda n: f"{NS}:{n}"
    for b in ["qonyr_kiiz", "qonyr_kiiz_stairs", "qonyr_kiiz_slab", "qonyr_oiu", "aq_oiu"]:
        write_json(f"{D}/loot_table/blocks/{b}.json", {
            "type": "minecraft:block",
            "pools": [{"condition": {"type": "minecraft:survives_explosion"},
                       "entries": [{"type": "minecraft:item", "name": e(b)}], "rolls": 1}],
            "random_sequence": f"{NS}:blocks/{b}"})
    # рецепты
    shapeless("qonyr_kiiz", ["minecraft:brown_wool", "minecraft:brown_wool", "minecraft:leather"], e("qonyr_kiiz"), 3, "building")
    shaped("qonyr_kiiz_stairs", ["K  ", "KK ", "KKK"], {"K": e("qonyr_kiiz")}, e("qonyr_kiiz_stairs"), 4, "building")
    shaped("qonyr_kiiz_slab", ["KKK"], {"K": e("qonyr_kiiz")}, e("qonyr_kiiz_slab"), 6, "building")
    shapeless("qonyr_oiu", [e("qonyr_kiiz"), e("qonyr_kiiz"), "minecraft:white_dye"], e("qonyr_oiu"), 2, "building")
    shapeless("aq_oiu", [e("kiiz"), e("kiiz"), "minecraft:brown_dye"], e("aq_oiu"), 2, "building")
    shaped("altyn_qanjar", [" G", "S "], {"G": e("saka_gold"), "S": "minecraft:stick"}, e("altyn_qanjar"), category="equipment")
    G, S = "minecraft:gold_ingot", e("saka_gold")
    shaped("qonyr_uy", ["OSO", "QUQ", "MMM"], {"O": e("qonyr_oiu"), "S": e("shanyrak"), "Q": e("qonyr_kiiz_stairs"),
                                               "U": e("kiiz_uy"), "M": e("tekemet")}, e("qonyr_uy"))
    shaped("otau", ["BSB", "KUK", "AAA"], {"B": e("baskur"), "S": e("shanyrak"), "K": e("kiiz_stairs"),
                                          "U": e("kiiz_uy"), "A": e("aq_oiu")}, e("otau"))
    shaped("han_ordasy", ["GSG", "UUU", "OOO"], {"G": "minecraft:gold_block", "S": e("shanyrak"),
                                                "U": e("ulken_kiiz_uy"), "O": e("ornek")}, e("han_ordasy"))

    def it(name, w, lo=1, hi=1):
        x = {"type": "minecraft:item", "name": name, "weight": w}
        if hi > 1:
            x["modifier"] = [{"type": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}}]
        return x
    acc = [e(n) for n in ACCESSORY_ART]
    write_json(f"{D}/loot_table/entities/altyn_jauynger.json", {
        "type": "minecraft:entity",
        "pools": [
            {"rolls": 1, "entries": [it("minecraft:gold_nugget", 1, 3, 9)]},
            {"rolls": 1, "condition": {"type": "minecraft:killed_by_player"},
             "entries": [it(e("saka_gold"), 6, 1, 3), {"type": "minecraft:empty", "weight": 4}]},
            {"rolls": 1, "condition": {"type": "minecraft:killed_by_player"},
             "entries": [it(a, 1) for a in acc] + [it(e("altyn_qanjar"), 2), {"type": "minecraft:empty", "weight": 70}]}],
        "random_sequence": f"{NS}:entities/altyn_jauynger"})
    write_json(f"{D}/loot_table/chests/altyn_qorym.json", {
        "type": "minecraft:chest",
        "pools": [
            {"rolls": {"type": "minecraft:uniform", "min": 4, "max": 7}, "entries": [
                it(e("saka_gold"), 25, 2, 6), it("minecraft:gold_ingot", 25, 3, 9), it("minecraft:gold_block", 4),
                it("minecraft:golden_apple", 6), it(e("kumys"), 6, 1, 3), it("minecraft:arrow", 8, 6, 16)]},
            {"rolls": {"type": "minecraft:uniform", "min": 1, "max": 2}, "entries":
                [it(a, 5) for a in acc] + [
                it(e("altyn_qanjar"), 6), it(e("altyn_qylysh"), 5), it(e("altyn_adam_helmet"), 4),
                it(e("altyn_adam_chestplate"), 3), it(e("altyn_adam_leggings"), 3), it(e("altyn_adam_boots"), 4),
                it("minecraft:enchanted_golden_apple", 1)]}],
        "random_sequence": f"{NS}:chests/altyn_qorym"})
    write_json(f"{D}/loot_table/chests/han.json", {
        "type": "minecraft:chest",
        "pools": [
            {"rolls": {"type": "minecraft:uniform", "min": 3, "max": 6}, "entries": [
                it(e("saka_gold"), 15, 1, 4), it("minecraft:gold_ingot", 20, 2, 6), it(e("beshbarmak"), 10),
                it(e("kumys"), 12, 1, 3), it(e("kazy"), 10, 1, 3), it("minecraft:emerald", 10, 1, 4)]},
            {"rolls": 1, "entries": [it(a, 3) for a in acc] + [it(e("altyn_qylysh"), 3), it(e("dombyra"), 4),
                                                              {"type": "minecraft:empty", "weight": 6}]}],
        "random_sequence": f"{NS}:chests/han"})
    write_json(f"{D}/loot_table/chests/camp.json", {
        "type": "minecraft:chest",
        "pools": [{"rolls": {"type": "minecraft:uniform", "min": 2, "max": 5}, "entries": [
            it(e("baursak"), 20, 2, 6), it(e("kurt"), 18, 2, 5), it(e("kazy"), 10, 1, 2), it(e("kumys"), 10, 1, 2),
            it("minecraft:leather", 12, 1, 4), it("minecraft:arrow", 10, 4, 12), it("minecraft:saddle", 4),
            it("minecraft:lead", 6), it(e("sadaq"), 3), it(e("tekemet"), 6, 1, 3)]}],
        "random_sequence": f"{NS}:chests/camp"})
    # золотой воин изредка бродит по степи ночью
    write_json(f"{D}/forge/biome_modifier/add_altyn_jauynger.json", {
        "type": "forge:add_spawns", "biomes": f"#{NS}:steppe",
        "spawners": [{"type": e("altyn_jauynger"), "count": {"type": "minecraft:uniform", "min_inclusive": 1, "max_inclusive": 2}, "weight": 4}]})


NAMES.update({
    "item.ezhelgi.altyn_qanjar": ("Золотой кинжал (акинак)", "Алтын қанжар", "Golden Dagger"),
    "item.ezhelgi.altyn_jauynger_spawn_egg": ("Яйцо призыва золотого воина", "Алтын жауынгер жұмыртқасы", "Golden Warrior Spawn Egg"),
    "item.ezhelgi.qonyr_uy": ("Коричневая юрта (набор)", "Қоңыр үй", "Brown Yurt Kit"),
    "item.ezhelgi.otau": ("Отау — свадебная юрта (набор)", "Отау", "Otau Wedding Yurt Kit"),
    "item.ezhelgi.han_ordasy": ("Ханская орда (набор)", "Хан ордасы", "Khan's Horde Yurt Kit"),
    "item.ezhelgi.barys_alqasy": ("Гривна снежного барса", "Барыс алқасы", "Snow Leopard Torc"),
    "item.ezhelgi.bugy_tumary": ("Амулет оленя", "Бұғы тұмары", "Deer Amulet"),
    "item.ezhelgi.qasqyr_bilezigi": ("Браслет волка", "Қасқыр білезігі", "Wolf Bracelet"),
    "item.ezhelgi.arqar_syrgasy": ("Серьги архара", "Арқар сырғасы", "Argali Earrings"),
    "item.ezhelgi.tulpar_juzigi": ("Перстень тулпара", "Тұлпар жүзігі", "Tulpar Ring"),
    "item.ezhelgi.burkit_belbeui": ("Поясная бляха беркута", "Бүркіт белбеуі", "Eagle Belt Plaque"),
    "block.ezhelgi.qonyr_kiiz": ("Коричневый войлок", "Қоңыр киіз", "Brown Kiiz Felt"),
    "block.ezhelgi.qonyr_kiiz_stairs": ("Коричневые войлочные ступени", "Қоңыр киіз баспалдақ", "Brown Kiiz Stairs"),
    "block.ezhelgi.qonyr_kiiz_slab": ("Коричневая войлочная плита", "Қоңыр киіз тақта", "Brown Kiiz Slab"),
    "block.ezhelgi.qonyr_oiu": ("Коричневый войлок с орнаментом", "Қоңыр ою", "Brown Ornament Felt"),
    "block.ezhelgi.aq_oiu": ("Белый войлок с орнаментом", "Ақ ою", "White Ornament Felt"),
    "entity.ezhelgi.altyn_jauynger": ("Золотой воин", "Алтын жауынгер", "Golden Warrior"),
    "container.ezhelgi.accessories": ("Украшения", "Әшекейлер", "Accessories"),
    "tooltip.ezhelgi.accessory": ("Носится в слоте украшений (кнопка ◆ у брони)", "Әшекей ұяшығына тағылады (◆ батырмасы)", "Worn in an accessory slot (◆ button by armor)"),
    "tooltip.ezhelgi.barys_alqasy": ("+4 к здоровью", "+4 денсаулық", "+4 max health"),
    "tooltip.ezhelgi.bugy_tumary": ("+12% к скорости", "+12% жылдамдық", "+12% speed"),
    "tooltip.ezhelgi.qasqyr_bilezigi": ("+2 к урону", "+2 соққы күші", "+2 attack damage"),
    "tooltip.ezhelgi.arqar_syrgasy": ("Ночное зрение", "Түнгі көру", "Night vision"),
    "tooltip.ezhelgi.tulpar_juzigi": ("Меньше урона от падения", "Құлағанда аз зақым", "Less fall damage"),
    "tooltip.ezhelgi.burkit_belbeui": ("+3 к броне", "+3 сауыт", "+3 armor"),
})


# ------------------------------------------------------------------ v4: маска, звуки, арба, джунгары, Алтын Адам
MASK_FACE = [
    "................",
    "..llllllllllll..",
    ".lmmmmmmmmmmmml.",
    ".lmkkkmmmmkkkml.",
    ".lmmmmmmmmmmmml.",
    ".lmkkkkmmkkkkml.",
    ".lmmmmmmmmmmmml.",
    ".lmmmmmdmmmmmml.",
    ".lmmmmmdmmmmmml.",
    ".lmmmmddddmmmml.",
    ".lmmmmmmmmmmmml.",
    ".lmmmkkkkkkmmml.",
    ".lmmmmmmmmmmmml.",
    "..lmmmmmmmmmml..",
    "...ldmmmmmmdl...",
    "....dddddddd....",
]


def mask_assets():
    from_art(MASK_FACE, dict(GOLD, k="#3a2412")).save("item/betperde_face")
    c = Canvas(seed=81)
    c.rect(0, 0, 15, 15, "#e2a92a")
    c.noise(0.08)
    for x in range(16):
        c.set(x, 0, "#fff3a8"); c.set(x, 15, "#9a6a12")
    c.save("item/betperde_gold")
    # красный головной убор с золотыми фигурками зверей
    c = Canvas(seed=82)
    c.rect(0, 0, 15, 15, "#9e1d1a")
    c.noise(0.06)
    deer = ["..k.k...", "..kkk...", "...kk...", ".kkkkkk.", ".k.kk.k.", ".k....k."]
    for oy, ox in ((1, 1), (9, 8)):
        for y, row in enumerate(deer):
            for x, ch in enumerate(row):
                if ch == "k":
                    c.set(ox + x, oy + y, "#e8b23a")
    c.save("item/betperde_red")
    c = Canvas(seed=83)
    c.rect(0, 0, 15, 15, "#e2a92a")
    for x in range(0, 16, 3):
        c.set(x, 7, "#2bb5a6"); c.set(x + 1, 8, "#2bb5a6")
    c.line(0, 3, 15, 3, "#9a6a12"); c.line(0, 12, 15, 12, "#9a6a12")
    c.noise(0.05)
    c.save("item/betperde_band")

    def box(fr, to, tex, north=None):
        faces = {}
        for d in ("north", "south", "east", "west", "up", "down"):
            faces[d] = {"uv": [0, 0, 16, 16], "texture": "#" + (north if (d == "north" and north) else tex)}
        return {"from": fr, "to": to, "faces": faces}
    elements = [
        box([3.2, 4.0, 1.0], [12.8, 12.6, 1.6], "gold", north="face"),
        box([1.2, 2.0, 1.6], [1.6, 13.0, 14.8], "red"),
        box([14.4, 2.0, 1.6], [14.8, 13.0, 14.8], "red"),
        box([1.6, 2.0, 14.4], [14.4, 13.0, 14.8], "red"),
        box([1.2, 12.6, 1.2], [14.8, 14.6, 14.8], "band"),
        box([1.6, 14.6, 1.6], [14.4, 18.6, 14.4], "red"),
        box([2.8, 18.6, 2.8], [13.2, 22.6, 13.2], "red"),
        box([4.0, 22.6, 4.0], [12.0, 26.4, 12.0], "red"),
        box([5.6, 26.4, 5.6], [10.4, 29.6, 10.4], "red"),
        box([7.0, 29.6, 7.0], [9.0, 32.0, 9.0], "gold"),
        box([7.4, 14.6, 1.1], [8.6, 28.0, 1.6], "gold"),
        box([3.0, 15.0, 1.2], [5.0, 21.0, 1.6], "gold"),
        box([11.0, 15.0, 1.2], [13.0, 21.0, 1.6], "gold"),
    ]
    write_json(f"{A}/models/item/altyn_betperde.json", {
        "textures": {"face": f"{NS}:item/betperde_face", "gold": f"{NS}:item/betperde_gold",
                     "red": f"{NS}:item/betperde_red", "band": f"{NS}:item/betperde_band",
                     "particle": f"{NS}:item/betperde_gold"},
        "elements": elements,
        "display": {
            "gui": {"rotation": [15, 200, 0], "translation": [0, -3.5, 0], "scale": [0.42, 0.42, 0.42]},
            "ground": {"rotation": [0, 0, 0], "translation": [0, 2, 0], "scale": [0.3, 0.3, 0.3]},
            "fixed": {"rotation": [0, 180, 0], "translation": [0, -3, 0], "scale": [0.45, 0.45, 0.45]},
            "thirdperson_righthand": {"rotation": [0, 0, 0], "translation": [0, 1, 1], "scale": [0.35, 0.35, 0.35]},
            "firstperson_righthand": {"rotation": [0, 135, 0], "translation": [0, 2, 0], "scale": [0.35, 0.35, 0.35]},
            "head": {"rotation": [0, 0, 0], "translation": [0, 0, 0], "scale": [1, 1, 1]}}})
    write_json(f"{A}/items/altyn_betperde.json", {"model": {"type": "minecraft:model", "model": f"{NS}:item/altyn_betperde"}})


def sounds_json():
    def ev(name, pitch=1.0, volume=1.0):
        return {"name": name, "type": "event", "pitch": pitch, "volume": volume}
    write_json(f"{A}/sounds.json", {
        "item.dombyra.kui": {"subtitle": "subtitles.ezhelgi.dombyra",
                             "sounds": [{"name": f"{NS}:dombyra/kui{i}"} for i in range(1, 5)]},
        "entity.altyn_jauynger.ambient": {"subtitle": "subtitles.ezhelgi.warrior",
                                          "sounds": [ev("minecraft:entity.vindicator.ambient", 0.8), ev("minecraft:entity.pillager.ambient", 0.75)]},
        "entity.altyn_jauynger.hurt": {"sounds": [ev("minecraft:entity.vindicator.hurt", 0.8)]},
        "entity.altyn_jauynger.death": {"sounds": [ev("minecraft:entity.vindicator.death", 0.8)]},
        "entity.altyn_jauynger.cry": {"subtitle": "subtitles.ezhelgi.cry",
                                      "sounds": [ev("minecraft:entity.vindicator.celebrate", 0.75, 1.5), ev("minecraft:entity.pillager.celebrate", 0.7, 1.5)]},
        "entity.jungar.ambient": {"subtitle": "subtitles.ezhelgi.jungar",
                                  "sounds": [ev("minecraft:entity.pillager.ambient", 1.1), ev("minecraft:entity.pillager.celebrate", 1.05)]},
        "entity.jungar.hurt": {"sounds": [ev("minecraft:entity.pillager.hurt", 1.1)]},
        "entity.jungar.death": {"sounds": [ev("minecraft:entity.pillager.death", 1.1)]},
        "entity.altyn_adam.ambient": {"subtitle": "subtitles.ezhelgi.altyn_adam",
                                      "sounds": [ev("minecraft:entity.evoker.ambient", 0.55, 1.5), ev("minecraft:entity.ravager.ambient", 0.8)]},
        "entity.altyn_adam.hurt": {"sounds": [ev("minecraft:entity.iron_golem.hurt", 0.8), ev("minecraft:entity.evoker.hurt", 0.6)]},
        "entity.altyn_adam.death": {"sounds": [ev("minecraft:entity.wither.death", 1.2, 0.8)]},
        "entity.altyn_adam.roar": {"subtitle": "subtitles.ezhelgi.roar",
                                   "sounds": [ev("minecraft:entity.ravager.roar", 0.75, 2.0)]},
        "entity.altyn_adam.slam": {"subtitle": "subtitles.ezhelgi.slam",
                                   "sounds": [ev("minecraft:entity.generic.explode", 0.7, 1.5), ev("minecraft:block.anvil.land", 0.5, 1.5)]},
    })


def humanoid_skin(path, head_fn, hat_kind, body_kind, arm_kind, leg_kind, extra=None, seed=90):
    c = Canvas(64, 64, seed=seed)
    head_fn(c)
    _box(c, 32, 0, 8, 8, 8, hat_kind, top_kind=hat_kind(0) if callable(hat_kind) else hat_kind)
    _box(c, 16, 16, 8, 12, 4, body_kind, top_kind="gold" if body_kind(0) == "gold" else body_kind(0), bottom_kind=body_kind(11))
    _box(c, 40, 16, 4, 12, 4, arm_kind, top_kind=arm_kind(0), bottom_kind="skin")
    _box(c, 0, 16, 4, 12, 4, leg_kind, top_kind=leg_kind(0), bottom_kind="sole")
    if extra:
        extra(c)
    c.save(path)


def face(c, skin, hair, eyes=(240, 240, 240, 255), pupil=(40, 30, 24, 255), beard=None, gold=False):
    for (u, v) in [(0, 8), (16, 8), (24, 8)]:
        for yy in range(8):
            for xx in range(8):
                c.set(u + xx, v + yy, hair if (yy <= 3 or u == 24) else skin)
    for xx in range(8):
        for yy in range(8):
            c.set(8 + xx, yy, hair)
            c.set(16 + xx, yy, skin)
            col = skin
            if yy <= 1:
                col = hair
            if yy == 3 and xx in (1, 2, 5, 6):
                col = hair
            if yy == 4 and xx in (1, 6):
                col = eyes
            if yy == 4 and xx in (2, 5):
                col = pupil
            if beard and yy >= 6 and 1 <= xx <= 6:
                col = beard
            c.set(8 + xx, 8 + yy, col)


def v4_textures():
    mask_assets()
    sounds_json()
    # дополнительные цвета для костюмов
    ARMOR.update({"robe": (30, 52, 86, 255), "robe_d": (20, 36, 62, 255), "iron": (120, 124, 132, 255),
                  "iron_d": (82, 86, 94, 255), "fur": (92, 70, 48, 255), "fur_d": (70, 52, 34, 255),
                  "sash": (170, 30, 30, 255), "boot": (40, 30, 26, 255), "trous": (110, 30, 26, 255)})
    old_px = globals()["_armor_px"]

    def px(kind, x, y, w, h, rng):
        A = ARMOR
        if kind == "robe":
            return A["robe"] if (x + y) % 6 else A["robe_d"]
        if kind == "lamellar":
            if y % 3 == 0:
                return A["iron_d"]
            return A["iron"] if (x + (y // 3)) % 2 else A["iron_d"]
        if kind == "fur":
            return A["fur"] if (x * 3 + y) % 4 else A["fur_d"]
        if kind == "sash":
            return A["sash"]
        if kind == "boot":
            return A["boot"]
        if kind == "trous":
            return A["trous"] if (x + y) % 5 else A["robe_d"]
        if kind == "redtop":
            return A["sash"]
        return old_px(kind, x, y, w, h, rng)
    globals()["_armor_px"] = px

    # Жоңғар: меховая шапка с красным верхом, синий халат с железными пластинами
    def jungar_head(c):
        face(c, (196, 150, 108, 255), (24, 20, 18, 255), beard=None)
        for xx in range(8):
            c.set(8 + xx, 8 + 6, (60, 40, 30, 255)) if 2 <= xx <= 5 else None
    humanoid_skin("entity/jungar", jungar_head,
                  lambda y: "redtop" if y <= 1 else ("fur" if y <= 4 else "none"),
                  lambda y: "fur" if y == 0 else ("sash" if y in (7, 8) else ("lamellar" if y <= 6 else "robe")),
                  lambda y: "lamellar" if y <= 3 else ("robe" if y <= 9 else "skin"),
                  lambda y: "robe" if y <= 3 else ("trous" if y <= 6 else "boot"), seed=91)

    # Алтын Адам: золотое лицо, тяжёлый золотой кафтан
    def adam_head(c):
        face(c, (236, 184, 52, 255), (176, 120, 22, 255), eyes=(40, 30, 20, 255), pupil=(20, 14, 10, 255),
             beard=(214, 160, 40, 255))
    humanoid_skin("entity/altyn_adam", adam_head,
                  lambda y: "band" if y <= 2 else "none",
                  lambda y: "gold" if y in (0, 1) else ("band" if y in (7, 8, 9) else "plaques"),
                  lambda y: "gold" if y <= 3 or y in (8, 9) else ("skin" if y >= 10 else "plaques"),
                  lambda y: "plaques" if y <= 5 else ("band" if y == 6 else "gold"), seed=92)
    globals()["_armor_px"] = old_px

    # яйца призыва и арба
    def egg(name, base, spots):
        c = Canvas(seed=hash(name) & 0xff)
        for y in range(16):
            for x in range(16):
                dx, dy = (x - 7.5) / 5.2, (y - 8.8) / 6.6
                if dy < 0:
                    dx *= 1.15
                if dx * dx + dy * dy <= 1:
                    c.set(x, y, base if (x * 2 + y) % 5 else spots)
        c.noise(0.05)
        c.outline("#1c1c1c")
        c.save("item/" + name)
    egg("jungar_spawn_egg", "#1e3456", "#787c84")
    egg("altyn_adam_spawn_egg", "#f2c230", "#9e1d1a")
    pal = {"o": "#2a1a10", "w": "#8e643a", "W": "#b0844e", "d": "#5e3e20", "i": "#4a4a50", "r": "#a42420"}
    art = [
        "................",
        "................",
        "................",
        "................",
        "..oooooooooooo..",
        "..oWWWWWWWWWWo..",
        "..owrrrrrrrrwo..",
        "..owwwwwwwwwwooo",
        "..oddddddddddo.o",
        "..oooooooooooo.o",
        "...oio....oio..o",
        "..oiwio..oiwio..",
        "..owdwo..owdwo..",
        "..oiwio..oiwio..",
        "...oio....oio...",
        "................",
    ]
    from_art(art, pal).save("item/arba")


def v4_models():
    for n in ["jungar_spawn_egg", "altyn_adam_spawn_egg", "arba"]:
        simple_item(n)
    # домбыра: держится наискось двумя руками
    write_json(f"{A}/models/item/dombyra.json", {
        "parent": "minecraft:item/handheld",
        "textures": {"layer0": f"{NS}:item/dombyra"},
        "display": {
            "thirdperson_righthand": {"rotation": [0, 90, -35], "translation": [0, 3, 1], "scale": [1.1, 1.1, 1.1]},
            "thirdperson_lefthand": {"rotation": [0, -90, 35], "translation": [0, 3, 1], "scale": [1.1, 1.1, 1.1]},
            "firstperson_righthand": {"rotation": [0, -90, 35], "translation": [1.13, 3.2, 1.13], "scale": [0.8, 0.8, 0.8]}}})


def v4_data():
    e = lambda n: f"{NS}:{n}"

    def it(name, w, lo=1, hi=1):
        x = {"type": "minecraft:item", "name": name, "weight": w}
        if hi > 1:
            x["modifier"] = [{"type": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}}]
        return x
    acc = [e(n) for n in ACCESSORY_ART]
    write_json(f"{D}/loot_table/entities/jungar.json", {
        "type": "minecraft:entity",
        "pools": [
            {"rolls": 1, "entries": [it(e("kurt"), 4, 1, 3), it(e("baursak"), 4, 1, 3), it("minecraft:arrow", 3, 2, 6),
                                     {"type": "minecraft:empty", "weight": 5}]},
            {"rolls": 1, "condition": {"type": "minecraft:killed_by_player"},
             "entries": [it(e("qylysh"), 2), it("minecraft:gold_nugget", 6, 2, 6), it(e("saka_gold"), 1),
                         {"type": "minecraft:empty", "weight": 12}]}],
        "random_sequence": f"{NS}:entities/jungar"})
    write_json(f"{D}/loot_table/entities/altyn_adam.json", {
        "type": "minecraft:entity",
        "pools": [
            {"rolls": 1, "entries": [it(e("altyn_betperde"), 1)]},
            {"rolls": 1, "entries": [it(e("saka_gold"), 1, 8, 16)]},
            {"rolls": 2, "entries": [it(a, 1) for a in acc]},
            {"rolls": 1, "entries": [it(e("altyn_qylysh"), 3), it(e("altyn_adam_chestplate"), 2),
                                     it(e("altyn_adam_helmet"), 2), it("minecraft:enchanted_golden_apple", 1)]},
            {"rolls": 1, "entries": [it("minecraft:gold_block", 1, 2, 5)]}],
        "random_sequence": f"{NS}:entities/altyn_adam"})
    write_json(f"{D}/loot_table/entities/arba.json", {
        "type": "minecraft:entity",
        "pools": [{"rolls": 1, "entries": [it(e("arba"), 1)]}],
        "random_sequence": f"{NS}:entities/arba"})
    shaped("arba", ["L L", "PCP", "S S"], {"L": "#minecraft:logs", "P": "#minecraft:planks", "C": "minecraft:chest",
                                          "S": "minecraft:stick"}, e("arba"))
    write_json(f"{D}/forge/biome_modifier/add_jungar.json", {
        "type": "forge:add_spawns", "biomes": f"#{NS}:steppe",
        "spawners": [{"type": e("jungar"), "count": {"type": "minecraft:uniform", "min_inclusive": 2, "max_inclusive": 4}, "weight": 10}]})


def unlock_recipes():
    # все рецепты мода сразу открыты в книге рецептов
    recipes = sorted(f"{NS}:" + f[:-5] for f in os.listdir(f"{D}/recipe") if f.endswith(".json"))
    write_json(f"{D}/advancement/recipes/all.json", {
        "criteria": {"tick": {"trigger": "minecraft:tick"}},
        "requirements": [["tick"]],
        "rewards": {"recipes": recipes}})




NAMES.update({
    "item.ezhelgi.altyn_betperde": ("Золотая маска Алтын Адама", "Алтын бетперде", "Golden Mask"),
    "item.ezhelgi.arba": ("Арба (телега)", "Арба", "Arba Cart"),
    "item.ezhelgi.jungar_spawn_egg": ("Яйцо призыва джунгара", "Жоңғар жұмыртқасы", "Jungar Spawn Egg"),
    "item.ezhelgi.altyn_adam_spawn_egg": ("Яйцо призыва Алтын Адама", "Алтын Адам жұмыртқасы", "Altyn Adam Spawn Egg"),
    "entity.ezhelgi.jungar": ("Джунгар", "Жоңғар", "Jungar"),
    "entity.ezhelgi.altyn_adam": ("Алтын Адам", "Алтын Адам", "Altyn Adam"),
    "entity.ezhelgi.arba": ("Арба", "Арба", "Arba"),
    "message.ezhelgi.arba.hitched": ("Арба прицеплена к коню", "Арба атқа жегілді", "Cart hitched to the horse"),
    "message.ezhelgi.arba.unhitched": ("Арба отцеплена", "Арба ағытылды", "Cart unhitched"),
    "subtitles.ezhelgi.dombyra": ("Играет домбра", "Домбыра шертіледі", "Dombyra plays"),
    "subtitles.ezhelgi.warrior": ("Золотой воин бормочет", "Алтын жауынгер", "Golden warrior mutters"),
    "subtitles.ezhelgi.cry": ("Боевой клич", "Ұран", "Battle cry"),
    "subtitles.ezhelgi.jungar": ("Джунгар кричит", "Жоңғар айғайлайды", "Jungar shouts"),
    "subtitles.ezhelgi.altyn_adam": ("Алтын Адам рычит", "Алтын Адам", "Altyn Adam growls"),
    "subtitles.ezhelgi.roar": ("Рёв Алтын Адама", "Алтын Адамның айқайы", "Altyn Adam roars"),
    "subtitles.ezhelgi.slam": ("Удар о землю", "Жерге соққы", "Ground slam"),
})


def lang():
    for i, code in enumerate(["ru_ru", "kk_kz", "en_us"]):
        write_json(f"{A}/lang/{code}.json", {k: v[i] for k, v in NAMES.items()})


if __name__ == "__main__":
    items()
    blocks()
    armor_layers()
    model_json()
    recipes()
    loot()
    tags()
    spawns()
    kurgan()
    yurt_textures()
    yurt_models()
    yurt_data()
    v3_textures()
    v3_models()
    v3_data()
    v4_textures()
    v4_models()
    v4_data()
    import v5_assets
    v5_assets.run(sys.modules[__name__])
    unlock_recipes()
    lang()
    print("assets done")
