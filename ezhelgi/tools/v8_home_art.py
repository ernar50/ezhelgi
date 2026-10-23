"""v8: текстуры кухни, мебели и растений (16x16) и иконки блюд и инструментов."""
import math
import random

from PIL import Image

from v7_items import outline, from_rows

T = (0, 0, 0, 0)


def rgb(h):
    h = h.lstrip("#")
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16))


def mix(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


def jitter(col, rng, amt=10):
    return tuple(max(0, min(255, v + rng.randint(-amt, amt))) for v in col[:3]) + (255,)


def tex(fn, seed=0, size=16):
    img = Image.new("RGBA", (size, size), T)
    px = img.load()
    rng = random.Random(seed)
    for y in range(size):
        for x in range(size):
            v = fn(x, y, rng)
            if v is not None:
                px[x, y] = v if len(v) == 4 else v + (255,)
    return img


# ------------------------------------------------------------------ металл, глина, дерево
def iron(seed, base=(52, 52, 56)):
    def f(x, y, r):
        k = 1.0 + 0.12 * math.sin(x * 0.9 + y * 0.3) + (0.08 if (x * 3 + y * 5) % 11 == 0 else 0)
        return jitter(tuple(int(v * k) for v in base), r, 6)
    return tex(f, seed)


def kazan_inside():
    return tex(lambda x, y, r: jitter((30, 30, 34), r, 5), 3)


def liquid(base, dots, seed):
    def f(x, y, r):
        c = base
        if (x * 7 + y * 3 + seed) % 13 == 0:
            c = dots
        elif math.sin((x + y) * 0.7 + seed) > 0.85:
            c = mix(base, (255, 255, 255), 0.25)
        return jitter(c, r, 5)
    return tex(f, seed)


def clay(seed, base=(186, 120, 74)):
    def f(x, y, r):
        c = base
        if y % 5 == 4:
            c = mix(base, (90, 50, 30), 0.25)  # кольца обмазки
        return jitter(c, r, 9)
    return tex(f, seed)


def tandyr_top(lit):
    def f(x, y, r):
        d = math.hypot(x - 7.5, y - 7.5)
        if d < 3.2:
            if lit:
                return jitter(mix((255, 200, 60), (230, 80, 20), d / 3.2), r, 12)
            return jitter((24, 18, 16), r, 4)
        if d < 4.4:
            return jitter((120, 70, 40), r, 6)
        return jitter((186, 120, 74), r, 9)
    return tex(f, 11 if lit else 10)


def tandyr_front(lit):
    base = clay(12)
    px = base.load()
    for y in range(16):
        for x in range(16):
            # арка-устье внизу
            if 5 <= x <= 10 and 9 <= y <= 15 and not (y == 9 and x in (5, 10)):
                px[x, y] = (255, 170, 50, 255) if lit and y > 10 else ((210, 90, 30, 255) if lit else (28, 20, 18, 255))
            elif 4 <= x <= 11 and 8 <= y <= 15 and (x in (4, 11) or y == 8):
                px[x, y] = (110, 64, 36, 255)
    return base


def wood(seed, base, grain=None):
    grain = grain or mix(base, (0, 0, 0), 0.25)

    def f(x, y, r):
        c = grain if (x + (y // 4)) % 5 == 0 or (y % 4 == 0 and x % 7 == 3) else base
        return jitter(c, r, 7)
    return tex(f, seed)


def brass(seed, lit=False):
    def f(x, y, r):
        base = (200, 130, 60)
        k = 0.85 + 0.3 * math.sin(x * 0.55) ** 2
        c = tuple(min(255, int(v * k)) for v in base)
        if y in (4, 11):
            c = (236, 190, 100)  # гравированные пояски
        if y in (5, 10) and x % 3 == 0:
            c = (150, 90, 40)
        if lit and y >= 13 and 5 <= x <= 10:
            c = (255, 150, 40) if (x + y) % 2 else (230, 90, 20)
        return jitter(c, r, 5)
    return tex(f, seed)


# ------------------------------------------------------------------ ткани и орнамент
QOSHQAR = [  # қошқар мүйіз — бараньи рога (12x12 в центре)
    "............",
    ".ff......ff.",
    "f..f....f..f",
    "f.f.f..f.f.f",
    ".f..ff.f..f.",
    "....f.ff....",
    "....ff.f....",
    ".f..f.ff..f.",
    "f.f.f..f.f.f",
    "f..f....f..f",
    ".ff......ff.",
    "............"]


def ornament_cloth(seed, bg, fg, border):
    def f(x, y, r):
        if x in (0, 15) or y in (0, 15):
            return jitter(border, r, 4)
        if x in (1, 14) or y in (1, 14):
            return jitter(fg if (x + y) % 2 else bg, r, 4)
        if 2 <= x <= 13 and 2 <= y <= 13 and QOSHQAR[y - 2][x - 2] == "f":
            return jitter(fg, r, 4)
        return jitter(bg, r, 5)
    return tex(f, seed)


def plain_cloth(seed, bg, stripe=None):
    def f(x, y, r):
        if stripe and y % 4 == 1:
            return jitter(stripe, r, 4)
        return jitter(bg, r, 6)
    return tex(f, seed)


def kebezhe_front():
    def f(x, y, r):
        base = (120, 60, 30)
        if x in (0, 15) or y in (0, 15):
            return jitter((200, 160, 70), r, 6)  # кованая окантовка
        if 6 <= x <= 9 and 6 <= y <= 9:
            return (230, 196, 90, 255) if (x, y) not in ((7, 8), (8, 8)) else (40, 30, 20, 255)  # замок
        if (x + y) % 6 == 0 or (x - y) % 6 == 0:
            return jitter((176, 40, 36), r, 6)  # роспись ромбами
        return jitter(base, r, 8)
    return tex(f, 21)


def besik_wood():
    def f(x, y, r):
        if y in (0, 15):
            return jitter((220, 180, 80), r, 6)
        if (x // 2 + y // 2) % 4 == 0:
            return jitter((210, 160, 60), r, 6)
        return jitter((160, 40, 30), r, 8)
    return tex(f, 22)


# ------------------------------------------------------------------ растения
def tulip(petal, petal_d, stripe=None):
    img = Image.new("RGBA", (16, 16), T)
    px = img.load()
    stem = (70, 130, 50, 255)
    leaf = (90, 150, 70, 255)
    for y in range(7, 16):
        px[8, y] = stem
    for y, xs in ((15, (5, 6, 7)), (14, (5, 6)), (13, (5,)), (15, (9, 10, 11)), (14, (10, 11)), (12, (11,)), (13, (11,))):
        for x in xs:
            px[x, y] = leaf
    cup = {2: (7, 8), 3: (6, 7, 8, 9), 4: (6, 7, 8, 9), 5: (6, 7, 8, 9), 6: (7, 8)}
    for y, xs in cup.items():
        for x in xs:
            c = petal if x in (7, 8) else petal_d
            if stripe and y >= 5 and x in (7, 8):
                c = stripe
            px[x, y] = c + (255,)
    px[6, 2] = petal_d + (255,)
    px[9, 2] = petal_d + (255,)
    return img


def seleu():
    img = Image.new("RGBA", (16, 16), T)
    px = img.load()
    rng = random.Random(31)
    for k in range(7):
        x0 = 2 + k * 2 + rng.randint(-1, 1)
        lean = rng.choice((-1, 0, 1))
        for y in range(15, 1 + rng.randint(0, 4), -1):
            x = x0 + int(lean * (15 - y) / 6)
            if 0 <= x < 16:
                top = y < 7
                c = (226, 224, 214) if top else (150, 160, 110)
                px[x, y] = jitter(c, rng, 8)
                if top and 0 <= x + 1 < 16 and rng.random() < 0.5:
                    px[x + 1, y] = jitter((240, 238, 230), rng, 6)  # пушистые ости
    return img


def leaves(seed, base, fruit=None):
    def f(x, y, r):
        n = r.random()
        if n < 0.12:
            return None
        c = base if n > 0.4 else mix(base, (0, 0, 0), 0.25)
        if n > 0.88:
            c = mix(base, (255, 255, 200), 0.25)
        return jitter(c, r, 8)
    img = tex(f, seed)
    if fruit:
        px = img.load()
        for (ax, ay) in ((3, 4), (11, 2), (8, 10), (13, 12), (2, 12)):
            for dx, dy in ((0, 0), (1, 0), (0, 1), (1, 1)):
                px[ax + dx, ay + dy] = fruit + (255,) if (dx, dy) != (0, 0) else (255, 200, 160, 255)
    return img


def sapling(trunk, leaf):
    img = Image.new("RGBA", (16, 16), T)
    px = img.load()
    for y in range(8, 16):
        px[7, y] = trunk + (255,)
        if y > 11:
            px[8, y] = trunk + (255,)
    rng = random.Random(sum(leaf))
    for y in range(1, 10):
        for x in range(2, 14):
            d = math.hypot(x - 7.5, (y - 5) * 1.2)
            if d < 5 and rng.random() < 0.75:
                px[x, y] = jitter(leaf if rng.random() < 0.7 else mix(leaf, (0, 0, 0), 0.25), rng, 8)
    return img


def log_side(seed):
    def f(x, y, r):
        c = (120, 112, 100)
        if (x * 5 + y // 3) % 7 == 0 or x in (3, 11):
            c = (80, 72, 64)
        if (y + x * 3) % 13 == 0:
            c = (150, 140, 124)
        return jitter(c, r, 8)
    return tex(f, seed)


def log_top(seed):
    def f(x, y, r):
        d = math.hypot(x - 7.5, y - 7.5)
        if d > 7:
            return jitter((96, 88, 78), r, 6)
        ring = int(d * 1.3) % 2
        return jitter((176, 140, 96) if ring else (150, 116, 76), r, 6)
    return tex(f, seed)


def textures():
    return {
        "kazan_iron": iron(1), "kazan_inside": kazan_inside(),
        "kazan_water": liquid((50, 90, 170), (90, 140, 210), 4), "kazan_soup": liquid((176, 120, 48), (230, 200, 120), 5),
        "tandyr_side": clay(7), "tandyr_top": tandyr_top(False), "tandyr_top_lit": tandyr_top(True),
        "tandyr_front": tandyr_front(False), "tandyr_front_lit": tandyr_front(True),
        "samauyr": brass(13), "samauyr_lit": brass(13, True),
        "dastarkhan_top": ornament_cloth(14, (176, 30, 36), (240, 200, 80), (110, 20, 24)),
        "dastarkhan_wood": wood(15, (110, 66, 36)),
        "korpe_top": ornament_cloth(16, (40, 70, 150), (230, 190, 70), (150, 30, 30)),
        "korpe_side": plain_cloth(17, (150, 30, 30), (230, 190, 70)),
        "yastyq": plain_cloth(18, (240, 236, 224), (176, 40, 40)),
        "kebezhe_front": kebezhe_front(), "kebezhe_side": wood(19, (120, 60, 30)),
        "kebezhe_top": ornament_cloth(20, (120, 60, 30), (200, 160, 70), (90, 44, 22)),
        "besik_wood": besik_wood(), "besik_cloth": ornament_cloth(23, (240, 236, 224), (40, 110, 170), (176, 40, 40)),
        "qyzgaldaq_shrenk": tulip((240, 120, 170), (200, 70, 120)),
        "qyzgaldaq_greig": tulip((220, 30, 30), (170, 20, 20), stripe=(30, 20, 20)),
        "seleu": seleu(),
        "sivers_japyragy": leaves(24, (80, 140, 60)), "sivers_japyragy_fruit": leaves(24, (80, 140, 60), fruit=(200, 40, 30)),
        "alma_kosheti": sapling((110, 80, 50), (90, 150, 70)),
        "sekseuil_side": log_side(25), "sekseuil_top": log_top(26),
        "sekseuil_japyragy": leaves(27, (130, 150, 110)), "sekseuil_kosheti": sapling((120, 112, 100), (140, 160, 120)),
    }


# ------------------------------------------------------------------ иконки
ICON_ROWS = {
    "nan": ([
        "................",
        "................",
        "....ddddddd.....",
        "...dlllllllld...",
        "..dlbbbbbbbbld..",
        "..lbbsbbbsbbbl..",
        ".dlbbbbbbbbbbld.",
        ".dlbsbbccbbsbld.",
        ".dlbbbbccbbbbld.",
        ".dlbbsbbbbsbbld.",
        "..lbbbbbbbbbbl..",
        "..dlbbbsbbbbld..",
        "...dlllllllld...",
        "....ddddddd.....",
        "................",
        "................"], {"d": "#7a4a20", "l": "#c88a40", "b": "#e8b870", "s": "#fff0c0", "c": "#a86a30"}),
    "samsa": ([
        "................",
        "................",
        "................",
        ".......l........",
        "......lbl.......",
        ".....lbbbl......",
        "....lbbsbbl.....",
        "...lbbbbbbbl....",
        "..lbbsbbbsbbl...",
        ".lbbbbbbbbbbbl..",
        ".ddddddddddddd..",
        "................",
        "................",
        "................",
        "................",
        "................"], {"d": "#8a5a24", "l": "#c88a40", "b": "#e8b060", "s": "#3a2a1a"}),
    "qaq_et": ([
        "................",
        "...........w....",
        "..........w.....",
        ".......rrrr.....",
        "......rRRRRr....",
        ".....rRRwRRr....",
        "....rRRRRRRr....",
        "...rRRwRRRr.....",
        "..rRRRRRRr......",
        "..rRRRRRr.......",
        "..rRRRRr........",
        "...rrrr.........",
        "................",
        "................",
        "................",
        "................"], {"r": "#4a1a14", "R": "#8a3024", "w": "#e8d8c0"}),
    "qara_shai": ([
        "................",
        "................",
        "................",
        "................",
        "................",
        "...gggggggggg...",
        "...gttttttttg...",
        "...gTttttttTg...",
        "....gttttttg....",
        "....gttttttg....",
        ".....gttttg.....",
        "......gggg......",
        "................",
        "................",
        "................",
        "................"], {"g": "#e8e4dc", "t": "#7a3a14", "T": "#a85a24"}),
    "sutti_shai": ([
        "................",
        "................",
        "................",
        "................",
        "................",
        "...bbbbbbbbbb...",
        "...bttttttttb...",
        "...bwTttttTwb...",
        "....bttttttb....",
        "....bwttttwb....",
        ".....bttttb.....",
        "......bbbb......",
        "................",
        "................",
        "................",
        "................"], {"b": "#2e5e9a", "w": "#f2ece0", "t": "#c8945a", "T": "#e0b480"}),
    "sybyzgy": ([
        "..............ll",
        ".............lbl",
        "............lbd.",
        "...........lbd..",
        "..........lhd...",
        ".........lbd....",
        "........lhd.....",
        ".......lbd......",
        "......lhd.......",
        ".....lbd........",
        "....lbd.........",
        "...lbd..........",
        "..lbd...........",
        ".lbd............",
        "ldd.............",
        "................"], {"l": "#e8d090", "b": "#c8a050", "d": "#7a5a24", "h": "#2a1a10"}),
    "zhetigen": ([
        "................",
        "..wwwwwwwwwwww..",
        "..wbbbbbbbbbbw..",
        "..wsysysysysyw..",
        "..wsysysysysyw..",
        "..wsysysysysyw..",
        "..wsysysysysyw..",
        "..wsysysysysyw..",
        "..wsysysysysyw..",
        "..wsysysysysyw..",
        "..wsysysysysyw..",
        "..wbbbbbbbbbbw..",
        "..wwwwwwwwwwww..",
        "................",
        "................",
        "................"], {"w": "#5a3a1e", "b": "#8a5a2e", "s": "#e8e4dc", "y": "#a87a44"}),
    "shanqobyz": ([
        "................",
        "................",
        ".....iiiii......",
        "....i.....i.....",
        "...i.......i....",
        "...i.......i....",
        "...i.......i....",
        "....i.....i.....",
        ".....i...i......",
        "......igi.......",
        "......igi.......",
        "......igi.......",
        "......igi.......",
        ".......g........",
        ".......y........",
        "................"], {"i": "#8a8f98", "g": "#c8ccd4", "y": "#e8c050"}),
    "dauylpaz": ([
        "................",
        "................",
        "....ssssssss....",
        "...swwwwwwwws...",
        "..swwwwwwwwwws..",
        "..rbbbbbbbbbbr..",
        "..rbrbbbbbbrbr..",
        "..rbbrbbbbrbbr..",
        "..rbbbrbbrbbbr..",
        "..rbbbbrrbbbbr..",
        "...rbbbbbbbbr...",
        "....rrrrrrrr....",
        "................",
        "................",
        "................",
        "................"], {"s": "#7a5a24", "w": "#e8dcc0", "b": "#a0402a", "r": "#5a2a14"}),
}


def bowl_icon(soup, bits, name):
    rows = [
        "................",
        "................",
        "................",
        "................",
        "....ssssssss....",
        "..sxxxxxxxxxxs..",
        ".sxxbxxxbxxbxxs.",
        ".wsxxxbxxxxxxsw.",
        ".wwssssssssssww.",
        "..wwwwwwwwwwww..",
        "...wwwwwwwwww...",
        "....wwwwwwww....",
        ".....dddddd.....",
        "................",
        "................",
        "................"]
    pal = {"s": soup[1], "x": soup[0], "b": bits, "w": "#e8e4dc", "d": "#a8a49c"}
    return outline(from_rows(rows, pal))


def icons():
    out = {n: outline(from_rows(rows, pal)) for n, (rows, pal) in ICON_ROWS.items()}
    out["sorpa"] = bowl_icon(("#c8a050", "#a07830"), "#f0e0a0", "sorpa")
    out["quyrdaq"] = bowl_icon(("#7a3a1e", "#5a2a14"), "#e8c060", "quyrdaq")
    out["palau"] = bowl_icon(("#f0e0b0", "#d0b880"), "#e07a2a", "palau")
    out["nauryz_kozhe"] = bowl_icon(("#f2ece0", "#d8d0c0"), "#c0392b", "nauryz_kozhe")
    return out
