"""v7: доспехи батыров — иконки 16x16 и текстуры экипировки 64x32.
Материалы рисуются процедурно: кольчуга, ламеллярные пластины, зерцала, золото с ою, кожа, мех.
"""
import math
import random
from PIL import Image

# ------------------------------------------------------------------ палитры (свет → тень)
STEEL = [(240, 244, 250), (204, 210, 220), (160, 168, 180), (112, 118, 132), (70, 74, 86), (40, 42, 50)]
DSTEEL = [(186, 190, 198), (144, 148, 158), (104, 108, 120), (72, 76, 86), (48, 50, 58), (28, 28, 34)]
BLUE = [(150, 176, 220), (104, 132, 186), (72, 98, 150), (50, 70, 116), (34, 48, 84), (20, 28, 52)]
GOLD = [(255, 244, 170), (250, 210, 90), (226, 168, 44), (178, 120, 24), (122, 78, 14), (74, 44, 8)]
BRONZE = [(250, 214, 160), (222, 168, 100), (186, 126, 64), (142, 90, 40), (98, 60, 24), (60, 34, 12)]
LEATHER = [(196, 142, 92), (164, 112, 66), (132, 86, 46), (102, 64, 32), (72, 44, 22), (44, 26, 12)]
DLEATHER = [(120, 84, 60), (96, 64, 44), (74, 48, 32), (56, 36, 24), (40, 26, 16), (24, 16, 10)]
RED = [(240, 110, 96), (214, 64, 52), (176, 38, 34), (136, 24, 26), (96, 16, 20), (60, 10, 14)]
CRIMSON = [(236, 90, 120), (200, 48, 82), (160, 28, 60), (120, 18, 44), (84, 12, 32), (52, 8, 20)]
TURQ = [(170, 250, 236), (96, 220, 206), (44, 176, 170), (24, 128, 130), (14, 88, 94), (8, 54, 60)]
GREEN = [(150, 210, 120), (100, 170, 80), (64, 132, 56), (42, 98, 40), (28, 68, 28), (16, 42, 18)]
FOX = [(255, 196, 120), (238, 150, 64), (206, 112, 40), (160, 80, 28), (112, 54, 20), (70, 32, 12)]
WHITE = [(255, 255, 255), (238, 236, 230), (214, 210, 200), (180, 176, 166), (140, 136, 128), (96, 92, 86)]
FEATHER = [(250, 250, 246), (226, 224, 216), (196, 192, 184), (150, 146, 140), (100, 96, 92), (60, 58, 56)]
CLOTH_BLUE = [(120, 150, 210), (80, 110, 176), (56, 82, 140), (40, 60, 108), (28, 42, 78), (16, 26, 50)]


def shade(pal, i):
    return pal[max(0, min(len(pal) - 1, i))] + (255,)


# ------------------------------------------------------------------ узоры материалов
def mail(pal, x, y, light=0):
    """Кольчуга: кольца 2x2 в шахматном порядке, блик сверху-слева."""
    ox = (y // 2) % 2
    cx, cy = (x + ox) % 2, y % 2
    if cx == 0 and cy == 0:
        i = 1
    elif cx == 1 and cy == 1:
        i = 3
    else:
        i = 2
    return shade(pal, i + light)


def lamellar(pal, lace, x, y, light=0):
    """Ламеллы: вертикальные пластинки 2x3, ряды смещены, между рядами — шнуровка."""
    row = y // 4
    ox = row % 2
    cx = (x + ox) % 3
    cy = y % 4
    if cy == 3:
        return shade(lace, 2 if (x + row) % 2 else 3)
    if cx == 2:
        return shade(pal, 5)
    i = 1 if cx == 0 else 3
    if cy == 0:
        i -= 1
    return shade(pal, i + light)


def scales(pal, x, y, light=0):
    """Чешуя / пластинки берен: полукруглые чешуйки, ряды смещены."""
    row = y // 3
    ox = (row % 2) * 2
    cx = (x + ox) % 4
    cy = y % 3
    if cy == 2 and cx in (0, 3):
        return shade(pal, 5)
    if cx == 3:
        return shade(pal, 4)
    if cy == 0 and cx == 1:
        return shade(pal, 0 + light)
    return shade(pal, (1 if cx == 1 else 2) + light + (1 if cy == 2 else 0))


def plate(pal, x, y, w, h, light=0):
    """Полированная пластина с бликом по диагонали."""
    t = (x / max(1, w - 1)) * 0.7 + (y / max(1, h - 1)) * 0.3
    i = 1 + int(t * 3)
    if abs((x - y * 0.5) - w * 0.25) < 0.8:
        i = 0
    return shade(pal, i + light)


def cloth(pal, x, y, light=0):
    i = 2 + (1 if (x * 3 + y * 5) % 7 == 0 else 0) - (1 if (x + y * 2) % 11 == 0 else 0)
    return shade(pal, i + light)


def leather(pal, x, y, light=0):
    i = 2 + (1 if (x * 7 + y * 3) % 5 == 0 else 0)
    return shade(pal, i + light)


def fur(pal, x, y, light=0):
    i = 1 + ((x * 5 + y * 3) % 4 == 0) + ((x + y) % 3 == 0)
    return shade(pal, i + light)


def oiu_band(pal, x, y, h, light=0):
    """Золотая лента с завитками «қошқар мүйіз»."""
    if y == 0:
        return shade(pal, 0 + light)
    if y == h - 1:
        return shade(pal, 4)
    k = x % 6
    mid = h // 2
    curl = {(0, mid): 0, (1, mid - 1): 0, (2, mid - 1): 1, (3, mid): 1, (4, mid + 1 if h > 3 else mid): 0, (5, mid): 1}
    if (k, y) in curl:
        return shade(pal, 0)
    return shade(pal, 2 + light)


# ------------------------------------------------------------------ иконки по маскам
def icon(mask, regions, outline_dark=0.55, seed=0):
    """mask — 16 строк; regions: символ -> функция (x, y, ctx) -> RGBA."""
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    bounds = {}
    for y, row in enumerate(mask):
        for x, ch in enumerate(row[:16]):
            if ch != ".":
                b = bounds.setdefault(ch, [99, 99, -1, -1])
                b[0] = min(b[0], x); b[1] = min(b[1], y); b[2] = max(b[2], x); b[3] = max(b[3], y)
    for y, row in enumerate(mask):
        for x, ch in enumerate(row[:16]):
            if ch == "." or ch not in regions:
                continue
            b = bounds[ch]
            ctx = {"x0": b[0], "y0": b[1], "w": b[2] - b[0] + 1, "h": b[3] - b[1] + 1}
            px[x, y] = regions[ch](x, y, ctx)
    # тёмный цветной контур: темнее соседнего пикселя
    out = img.copy()
    op = out.load()
    for y in range(16):
        for x in range(16):
            if px[x, y][3]:
                continue
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < 16 and 0 <= ny < 16 and px[nx, ny][3]:
                    c = px[nx, ny]
                    op[x, y] = (int(c[0] * outline_dark * 0.6), int(c[1] * outline_dark * 0.6), int(c[2] * outline_dark * 0.6), 255)
                    break
    return out


def R(pal, kind="plate", light=0, extra=None):
    """Фабрика функций региона."""
    def f(x, y, c):
        lx, ly = x - c["x0"], y - c["y0"]
        if kind == "mail":
            return mail(pal, x, y, light)
        if kind == "plate":
            return plate(pal, lx, ly, c["w"], c["h"], light)
        if kind == "scales":
            return scales(pal, lx, ly, light)
        if kind == "lamellar":
            return lamellar(pal, extra, lx, ly, light)
        if kind == "cloth":
            return cloth(pal, x, y, light)
        if kind == "leather":
            return leather(pal, x, y, light)
        if kind == "fur":
            return fur(pal, x, y, light)
        if kind == "band":
            return oiu_band(pal, lx, ly, c["h"], light)
        if kind == "flat":
            return shade(pal, light)
        if kind == "vgrad":  # вертикальный градиент
            return shade(pal, light + int(ly / max(1, c["h"] - 1) * 3))
        if kind == "hgrad":
            return shade(pal, light + int(lx / max(1, c["w"] - 1) * 3))
        if kind == "cone":  # конус шлема: блик слева, тень справа, рёбра
            t = lx / max(1, c["w"] - 1)
            i = 1 if t < 0.3 else 2 if t < 0.6 else 3
            if lx == 1:
                i = 0
            return shade(pal, i + light)
        raise ValueError(kind)
    return f


# ------------------------------------------------------------------ маски
M_DULYGA = [
    ".......FF.......",
    "......FFf.......",
    ".......gg.......",
    ".......CC.......",
    "......CCCC......",
    ".....CCCCCC.....",
    "....CCCCCCCC....",
    "...CCCCCCCCCC...",
    "...CCCCCCCCCC...",
    "..BBBBBBBBBBBB..",
    "..MMMM.gg.MMMM..",
    "..MMMM.gg.MMMM..",
    "..MMMM....MMMM..",
    "..MMMMM..MMMMM..",
    "...MMMMMMMMMM...",
    "....MMMMMMMM....",
]
M_JUNGAR_HELM = [
    ".......gg.......",
    ".......SS.......",
    ".......SS.......",
    "......gggg......",
    ".....CCCCCC.....",
    "....CCCCCCCC....",
    "...CCCCCCCCCC...",
    "...CCCCCCCCCC...",
    "..BBBBBBBBBBBB..",
    "..LLLL....LLLL..",
    ".LLLLL....LLLLL.",
    ".LLLLL....LLLLL.",
    ".LLLLLL..LLLLLL.",
    ".LLLLLLLLLLLLLL.",
    "..LLLLLLLLLLLL..",
    "................",
]
M_EPIC_HELM = [
    "......FFF.......",
    ".....FFfFF......",
    "......FgF.......",
    ".......gg.......",
    "......CCCC......",
    ".....CCCCCC.....",
    "....CCCCCCCC....",
    "...CCCCCCCCCC...",
    "..BBBBBBBBBBBB..",
    "..PPPP.gg.PPPP..",
    "..PPPP.gg.PPPP..",
    "..PPP.....PPPP..",
    "..PPPM...MPPPP..",
    "...MMMMMMMMMM...",
    "...MMMMMMMMMM...",
    "....MMMMMMMM....",
]
M_TYMAQ = [
    "................",
    "......TTTT......",
    ".....TTTTTT.....",
    "....TTTTTTTT....",
    "....TTTTTTTT....",
    "...FFFFFFFFFF...",
    "..FFFFFFFFFFFF..",
    "..FFFFFFFFFFFF..",
    "..FFF......FFF..",
    "..FFF......FFF..",
    "..FFF......FFF..",
    "..FFFF....FFFF..",
    "...FFF....FFF...",
    "...FFF....FFF...",
    "....FF....FF....",
    "................",
]
M_CHEST_MAIL = [
    "................",
    "...MMMM..MMMM...",
    "..MMMMMddMMMMM..",
    ".MMMMMMMMMMMMMM.",
    ".MMMMMMMMMMMMMM.",
    ".MMMMMMMMMMMMMM.",
    ".MMM.MMMMMM.MMM.",
    ".MMM.MMMMMM.MMM.",
    "....MMMMMMMM....",
    "....BBBBBBBB....",
    "....MMMMMMMM....",
    "....MMMMMMMM....",
    "....MMMMMMMM....",
    "....MMMMMMMM....",
    "................",
    "................",
]
M_CHEST_LONG = [
    "...BBBB..BBBB...",
    "..MMMMMddMMMMM..",
    ".MMMMMMMMMMMMMM.",
    ".MMMMMMMMMMMMMM.",
    ".MMMMMMMMMMMMMM.",
    ".MMM.MMMMMM.MMM.",
    ".MMM.MMMMMM.MMM.",
    ".MM..MMMMMM..MM.",
    "....LLLLLLLL....",
    "....MMMMMMMM....",
    "...MMMMMMMMMM...",
    "...MMMMMMMMMM...",
    "...MMMMMMMMMM...",
    "...MMMMMMMMMM...",
    "...MMMM..MMMM...",
    "................",
]
M_CHEST_PLATE = [
    "................",
    "...PPPB..BPPP...",
    "..PPPPPddPPPPP..",
    ".PPPSSSSSSSSPPP.",
    ".PPPSSSSSSSSPPP.",
    ".PPPSSSSSSSSPPP.",
    ".PPPSSSSSSSSPPP.",
    ".MMM.SSSSSS.MMM.",
    ".MMM.SSSSSS.MMM.",
    "....BBBBBBBB....",
    "....SSSSSSSS....",
    "....SSSSSSSS....",
    "....MMMMMMMM....",
    "....MMMMMMMM....",
    "................",
    "................",
]
M_CHEST_MIRROR = [
    "................",
    "...PPPP..PPPP...",
    "..MMMMMddMMMMM..",
    ".PPMMOOOOOOMMPP.",
    ".PPMOOOOOOOOMPP.",
    ".PPMOOOOOOOOMPP.",
    ".PPMOOOOOOOOMPP.",
    ".MMMMOOOOOOMMMM.",
    ".MMM.MMMMMM.MMM.",
    "....BBBBBBBB....",
    "....PPPPPPPP....",
    "....PPPPPPPP....",
    "....MMMMMMMM....",
    "....MMMMMMMM....",
    "................",
    "................",
]
M_CHEST_LAM = [
    "................",
    "...LLLL..LLLL...",
    "..LLLLLddLLLLL..",
    ".LLLLLLLLLLLLLL.",
    ".LLLLLLLLLLLLLL.",
    ".LLLLLLLLLLLLLL.",
    ".LLLLLLLLLLLLLL.",
    ".LLL.LLLLLL.LLL.",
    ".LLL.LLLLLL.LLL.",
    "....BBBBBBBB....",
    "....LLLLLLLL....",
    "...LLLLLLLLLL...",
    "...LLLLLLLLLL...",
    "...LLLL..LLLL...",
    "................",
    "................",
]
M_CHEST_EPIC = [
    "...GGG....GGG...",
    "..PPPPP..PPPPP..",
    ".PPPPPPddPPPPPP.",
    ".PPPSOOOOOOSPPP.",
    ".PPPSOOOOOOSPPP.",
    ".PPPSOOOOOOSPPP.",
    ".PPPSSOOOOSSPPP.",
    ".MMM.SSSSSS.MMM.",
    ".MMM.SSSSSS.MMM.",
    ".GG.BBBBBBBB.GG.",
    "....SSSSSSSS....",
    "...MMMMMMMMMM...",
    "...MMMMMMMMMM...",
    "...MMMM..MMMM...",
    "................",
    "................",
]
M_LEGS = [
    "................",
    "...BBBBBBBBBB...",
    "...CCCCCCCCCC...",
    "...CCCCCCCCCC...",
    "...CCCCCCCCCC...",
    "...CCCCC.CCCC...",
    "...CCCC..CCCC...",
    "...MMMM..MMMM...",
    "...MMMM..MMMM...",
    "...MMMM..MMMM...",
    "...MMMM..MMMM...",
    "...MMMM..MMMM...",
    "...KKKK..KKKK...",
    "...KKKK..KKKK...",
    "................",
    "................",
]
M_BOOTS = [
    "................",
    "................",
    "................",
    "...BBBB...BBBB..",
    "...LLLL...LLLL..",
    "...LLLL...LLLL..",
    "...LLLL...LLLL..",
    "...LLLL...LLLL..",
    "...LLLL...LLLL..",
    "...LLLL...LLLL..",
    "..LLLLL..LLLLL..",
    ".LLLLLL.LLLLLL..",
    "LLLLLLLLLLLLLL..",
    "SSSSSS.SSSSSS...",
    "................",
    "................",
]

# ------------------------------------------------------------------ наборы
# Каждый набор: палитры для частей и узор основного материала.
SETS = {
    "batyr": dict(cone=STEEL, mail=STEEL, plate=STEEL, band=GOLD, leather=LEATHER, cloth=RED, feather=FEATHER, accent=RED),
    "jungar": dict(cone=DSTEEL, mail=DSTEEL, plate=DSTEEL, band=BRONZE, leather=DLEATHER, cloth=CRIMSON, lam=DSTEEL, lace=RED,
                   feather=RED, accent=RED),
    "qobylandy": dict(cone=BLUE, mail=STEEL, plate=BLUE, band=GOLD, leather=DLEATHER, cloth=RED, feather=FEATHER, accent=RED,
                      mirror=GOLD),
    "alpamys": dict(cone=WHITE, mail=STEEL, plate=WHITE, band=GOLD, leather=LEATHER, cloth=TURQ, feather=FEATHER, accent=TURQ,
                    mirror=TURQ),
    "er_targyn": dict(cone=BRONZE, mail=STEEL, plate=CRIMSON, band=GOLD, leather=DLEATHER, cloth=CRIMSON, feather=GOLD,
                      accent=GOLD, mirror=STEEL),
}


def armor_icons():
    """Возвращает {имя предмета: Image 16x16}."""
    out = {}
    b = SETS["batyr"]
    out["dulyga"] = icon(M_DULYGA, {"F": R(FEATHER, "flat", 1), "f": R(FEATHER, "flat", 3), "g": R(GOLD, "flat", 1),
                                    "C": R(STEEL, "cone"), "B": R(GOLD, "band"), "M": R(STEEL, "mail")})
    out["kireuke"] = icon(M_CHEST_MAIL, {"M": R(STEEL, "mail"), "d": R(RED, "flat", 2), "B": R(LEATHER, "vgrad", 1)})
    out["kobe_sauyt"] = icon(M_CHEST_LONG, {"M": R(STEEL, "mail", 1), "d": R(RED, "flat", 2), "B": R(GOLD, "band"),
                                            "L": R(LEATHER, "vgrad", 1)})
    out["beren_sauyt"] = icon(M_CHEST_PLATE, {"P": R(STEEL, "plate"), "S": R(STEEL, "scales"), "M": R(STEEL, "mail", 1),
                                              "B": R(GOLD, "band"), "d": R(RED, "flat", 2)})
    out["sharaina"] = icon(M_CHEST_MIRROR, {"P": R(STEEL, "plate"), "M": R(STEEL, "mail", 1), "O": mirror_fn(STEEL, GOLD),
                                            "B": R(GOLD, "band"), "d": R(RED, "flat", 2)})
    out["batyr_shalbar"] = icon(M_LEGS, {"B": R(GOLD, "band"), "C": R(RED, "cloth", 1), "M": R(STEEL, "mail"),
                                         "K": R(LEATHER, "vgrad", 1)})
    out["batyr_etik"] = icon(M_BOOTS, {"B": R(GOLD, "band"), "L": R(LEATHER, "leather"), "S": R(DLEATHER, "flat", 3)})
    lam = lambda x, y, c: lamellar(DSTEEL, RED, x - c["x0"], y - c["y0"])
    out["jungar_dulyga"] = icon(M_JUNGAR_HELM, {"g": R(BRONZE, "flat", 1), "S": R(DSTEEL, "flat", 2), "C": R(DSTEEL, "cone"),
                                                "B": R(BRONZE, "band"), "L": lam})
    out["jungar_sauyt"] = icon(M_CHEST_LAM, {"L": lam, "d": R(CRIMSON, "flat", 2), "B": R(BRONZE, "band")})
    out["jungar_shalbar"] = icon(M_LEGS, {"B": R(BRONZE, "band"), "C": R(CRIMSON, "cloth", 1), "M": lam,
                                          "K": R(DLEATHER, "vgrad", 1)})
    out["jungar_etik"] = icon(M_BOOTS, {"B": R(BRONZE, "band"), "L": R(DLEATHER, "leather"), "S": R(DLEATHER, "flat", 4)})
    out["tymaq"] = icon(M_TYMAQ, {"T": R(CLOTH_BLUE, "cloth", 0), "F": R(FOX, "fur")})
    for name, s in (("qobylandy", SETS["qobylandy"]), ("alpamys", SETS["alpamys"]), ("er_targyn", SETS["er_targyn"])):
        out[name + "_dulyga"] = icon(M_EPIC_HELM, {"F": R(s["feather"], "flat", 1), "f": R(s["feather"], "flat", 3),
                                                    "g": R(GOLD, "flat", 1), "C": R(s["cone"], "cone"), "B": R(GOLD, "band"),
                                                    "P": R(s["plate"], "plate"), "M": R(STEEL, "mail")})
        out[name + "_sauyt"] = icon(M_CHEST_EPIC, {"G": R(GOLD, "plate"), "P": R(s["plate"], "plate"), "d": R(s["accent"], "flat", 2),
                                                    "S": R(s["plate"], "scales"), "O": mirror_fn(s["mirror"], GOLD),
                                                    "M": R(STEEL, "mail"), "B": R(GOLD, "band")})
        out[name + "_shalbar"] = icon(M_LEGS, {"B": R(GOLD, "band"), "C": R(s["cloth"], "cloth", 1), "M": R(s["plate"], "scales"),
                                               "K": R(GOLD, "vgrad", 1)})
        out[name + "_etik"] = icon(M_BOOTS, {"B": R(GOLD, "band"), "L": R(s["plate"], "scales"), "S": R(GOLD, "flat", 3)})
    return out


def mirror_fn(pal, rim):
    """Зерцало — круглая полированная бляха с ободом и бликом."""
    def f(x, y, c):
        cx = c["x0"] + (c["w"] - 1) / 2
        cy = c["y0"] + (c["h"] - 1) / 2
        r = math.hypot((x - cx) / (c["w"] / 2), (y - cy) / (c["h"] / 2))
        if r > 0.82:
            return shade(rim, 2 if (x + y) % 2 else 1)
        dx, dy = x - cx, y - cy
        if dx + dy < -1.5:
            return shade(pal, 0)
        if dx + dy > 1.5:
            return shade(pal, 3)
        return shade(pal, 1 if abs(dx) < 0.6 and abs(dy) < 0.6 else 2)
    return f


# ------------------------------------------------------------------ текстуры экипировки 64x32
class Tex:
    def __init__(self):
        self.img = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
        self.px = self.img.load()

    def face(self, u, v, w, h, fn):
        for y in range(h):
            for x in range(w):
                c = fn(x, y, w, h)
                if c:
                    self.px[u + x, v + y] = c

    def box(self, u, v, w, h, d, side, top=None, bottom=None, front=None, back=None, rows=None):
        """Развёртка коробки как в игре; side/front… — fn(x, y, w, h)."""
        def lim(fn):
            if rows is None or fn is None:
                return fn
            return lambda x, y, W, H: fn(x, y, W, H) if y in rows else None
        if top:
            self.face(u + d, v, w, d, top)
        if bottom:
            self.face(u + d + w, v, w, d, bottom)
        self.face(u, v + d, d, h, lim(side))
        self.face(u + d, v + d, w, h, lim(front or side))
        self.face(u + d + w, v + d, d, h, lim(side))
        self.face(u + 2 * d + w, v + d, w, h, lim(back or side))

    def clear(self, u, v, w, h):
        for y in range(h):
            for x in range(w):
                self.px[u + x, v + y] = (0, 0, 0, 0)


def F(kind, pal, light=0, extra=None):
    """Функция грани по материалу: fn(x, y, w, h)."""
    def f(x, y, w, h):
        if kind == "mail":
            return mail(pal, x, y, light)
        if kind == "lamellar":
            return lamellar(pal, extra, x, y, light)
        if kind == "scales":
            return scales(pal, x, y, light)
        if kind == "plate":
            return plate(pal, x, y, w, h, light)
        if kind == "cloth":
            return cloth(pal, x, y, light)
        if kind == "leather":
            return leather(pal, x, y, light)
        if kind == "fur":
            return fur(pal, x, y, light)
        if kind == "band":
            return oiu_band(pal, x, y, h, light)
        if kind == "flat":
            return shade(pal, light)
        raise ValueError(kind)
    return f


def rows_fn(spec):
    """spec: список (до_строки_включительно, fn) — выбирает fn по номеру строки."""
    def f(x, y, w, h):
        for upto, fn in spec:
            if y <= upto:
                return fn(x, y, w, h) if fn else None
        return None
    return f


def helmet(t, cone_pal, band_pal, lower_fn, nasal_pal=GOLD, plume=None, face_open=True, cheek=None):
    """Шлем на голове 8x8x8 (u0 v0): купол сверху, обод, кольчужная бармица снизу."""
    cone = lambda x, y, w, h: plate(cone_pal, x, y, w, h)
    band = lambda x, y, w, h: oiu_band(band_pal, x, y - 3, 3) if 3 <= y <= 5 else None
    side = rows_fn([(2, cone), (4, lambda x, y, w, h: oiu_band(band_pal, x, y - 3, 2)), (7, lower_fn)])
    front = rows_fn([(2, cone), (4, lambda x, y, w, h: oiu_band(band_pal, x, y - 3, 2)), (7, lower_fn)])

    def top(x, y, w, h):
        # купол: радиальные рёбра к центру
        cx, cy = x - 3.5, y - 3.5
        if abs(cx) < 1 and abs(cy) < 1:
            return shade(band_pal, 1)
        if abs(cx) - abs(cy) < 0.6 and abs(cy) - abs(cx) < 0.6:
            return shade(cone_pal, 1)
        return shade(cone_pal, 2 + (1 if cx + cy > 0 else 0))
    t.box(0, 0, 8, 8, 8, side, top=top, bottom=None, front=front, back=side)
    if face_open:
        t.clear(8 + 1, 8 + 5, 6, 3)  # лицо видно
        for y in range(5, 8):  # наносник
            t.px[8 + 3, 8 + y] = shade(nasal_pal, 1)
            t.px[8 + 4, 8 + y] = shade(nasal_pal, 3)
    if cheek:  # нащёчники
        for y in range(5, 8):
            for x in (1, 6):
                t.px[8 + x, 8 + y] = shade(cheek, 2 if x == 1 else 3)
    if plume:  # плюмаж из перьев филина на шляпном слое сверху
        for x in range(3, 5):
            for y in range(3, 5):
                t.px[40 + x, 0 + y] = shade(plume, 1 if (x + y) % 2 else 2)


def chest(t, body_fn, arm_fn, belt_pal=None, front_fn=None, collar=None):
    """Нагрудник: тело 8x12x4 (16,16) и рукава 4x12x4 (40,16)."""
    t.box(16, 16, 8, 12, 4, body_fn, top=body_fn, bottom=body_fn, front=front_fn or body_fn)
    t.box(40, 16, 4, 12, 4, arm_fn, top=arm_fn, bottom=arm_fn)
    if belt_pal:
        for u in range(16, 40):
            for y in (28, 29):
                pass
        # пояс по всему телу (строки 8–9 боковых граней)
        for (u, w) in ((16, 4), (20, 8), (28, 4), (32, 8)):
            for x in range(w):
                for i, y in enumerate((20 + 8, 20 + 9)):
                    t.px[u + x, y] = shade(belt_pal, 1 if i == 0 else 3)
            t.px[20 + 3, 28] = shade(GOLD, 0)
            t.px[20 + 4, 28] = shade(GOLD, 2)
    if collar:
        for x in range(8):
            t.px[20 + x, 20] = shade(collar, 1 if x % 2 else 2)


def boots(t, fn, cuff_pal):
    t.box(0, 16, 4, 12, 4, rows_fn([(6, None), (7, lambda x, y, w, h: shade(cuff_pal, 1 if x % 2 else 2)), (11, fn)]),
          bottom=lambda x, y, w, h: shade(DLEATHER, 4))


def legs_layer(t, waist_fn, leg_fn, belt_pal):
    t.box(16, 16, 8, 12, 4, rows_fn([(6, None), (7, lambda x, y, w, h: shade(belt_pal, 1)), (11, waist_fn)]))
    t.box(0, 16, 4, 12, 4, leg_fn, top=leg_fn)


def mirror_face(pal, rim, cx, cy, r):
    """Наложение круглого зерцала на грань."""
    def deco(fn):
        def f(x, y, w, h):
            d = math.hypot(x - cx, y - cy)
            if d <= r + 0.5:
                if d > r - 0.6:
                    return shade(rim, 1 if (x + y) % 2 else 2)
                if (x - cx) + (y - cy) < -0.5:
                    return shade(pal, 0)
                return shade(pal, 2 if (x - cx) + (y - cy) > 0.5 else 1)
            return fn(x, y, w, h)
        return f
    return deco


def equipment():
    """Возвращает {asset: (humanoid Image, leggings Image or None)}."""
    out = {}
    m = lambda pal, l=0: F("mail", pal, l)

    # --- батыр: дулыға, шалбар, етік
    t = Tex()
    helmet(t, STEEL, GOLD, m(STEEL), plume=FEATHER)
    boots(t, F("leather", LEATHER), GOLD)
    lg = Tex()
    legs_layer(lg, F("cloth", RED, 1), rows_fn([(5, F("cloth", RED, 1)), (11, m(STEEL))]), LEATHER)
    out["batyr"] = (t.img, lg.img)

    # --- кіреуке: кольчужная рубаха на красном кафтане
    t = Tex()
    chest(t, m(STEEL), rows_fn([(6, m(STEEL)), (11, F("cloth", RED, 1))]), belt_pal=LEATHER, collar=RED)
    out["kireuke"] = (t.img, None)

    # --- көбе: длинная тяжёлая кольчуга с золотым оплечьем
    t = Tex()
    body = rows_fn([(0, F("band", GOLD)), (11, m(STEEL, 1))])
    chest(t, body, rows_fn([(1, F("band", GOLD)), (11, m(STEEL, 1))]), belt_pal=LEATHER)
    out["kobe_sauyt"] = (t.img, None)

    # --- берен: пластинчатый доспех из лучшей стали
    t = Tex()
    body = rows_fn([(0, F("band", GOLD)), (7, F("scales", STEEL)), (11, m(STEEL, 1))])
    chest(t, body, rows_fn([(3, F("plate", STEEL)), (4, F("flat", GOLD, 2)), (11, F("scales", STEEL))]), belt_pal=GOLD)
    out["beren_sauyt"] = (t.img, None)

    # --- шарайна: зерцала на груди и спине поверх кольчуги
    t = Tex()
    base = rows_fn([(0, F("band", GOLD)), (11, m(STEEL, 1))])
    front = mirror_face(STEEL, GOLD, 3.5, 3.5, 3)(base)
    chest(t, base, rows_fn([(3, F("plate", STEEL)), (11, m(STEEL, 1))]), belt_pal=LEATHER, front_fn=front)
    t.box(16, 16, 8, 12, 4, base, back=front, rows=range(0, 8))
    out["sharaina"] = (t.img, None)

    # --- джунгарский ламеллярный доспех
    lam = F("lamellar", DSTEEL, 0, RED)
    t = Tex()
    helmet(t, DSTEEL, BRONZE, lam, nasal_pal=BRONZE, plume=RED)
    chest(t, lam, rows_fn([(2, F("plate", DSTEEL)), (11, lam)]), belt_pal=CRIMSON)
    boots(t, F("leather", DLEATHER), BRONZE)
    lg = Tex()
    legs_layer(lg, lam, rows_fn([(6, lam), (11, F("cloth", CRIMSON, 1))]), CRIMSON)
    out["jungar_sauyt"] = (t.img, lg.img)

    # --- тымақ из лисьего меха
    t = Tex()
    fur_f = F("fur", FOX)
    t.box(0, 0, 8, 8, 8, rows_fn([(2, F("cloth", CLOTH_BLUE)), (7, fur_f)]), top=F("cloth", CLOTH_BLUE),
          front=rows_fn([(2, F("cloth", CLOTH_BLUE)), (4, fur_f)]))
    out["tymaq"] = (t.img, None)

    # --- эпические наборы
    for name in ("qobylandy", "alpamys", "er_targyn"):
        s = SETS[name]
        t = Tex()
        helmet(t, s["cone"], GOLD, F("scales", s["plate"]), plume=s["feather"], cheek=s["plate"])
        base = rows_fn([(0, F("band", GOLD)), (7, F("scales", s["plate"])), (11, m(STEEL, 1))])
        front = mirror_face(s["mirror"], GOLD, 3.5, 3.5, 2.6)(base)
        chest(t, base, rows_fn([(1, F("band", GOLD)), (4, F("plate", s["plate"])), (11, F("scales", s["plate"]))]),
              belt_pal=GOLD, front_fn=front, collar=s["accent"])
        boots(t, F("scales", s["plate"]), GOLD)
        lg = Tex()
        legs_layer(lg, F("cloth", s["cloth"], 1), rows_fn([(5, F("cloth", s["cloth"], 1)), (11, F("scales", s["plate"]))]), GOLD)
        out[name] = (t.img, lg.img)
    return out


# ------------------------------------------------------------------ отделка (trims): серые ключи палитры trim_base
TRIM_GREYS = {"1": 224, "2": 192, "3": 160, "4": 128, "5": 96}

MOTIFS = {
    # қошқар мүйіз — два бараньих рога, расходящихся от стебля
    "qoshqar_muiz": [
        ".11..11.",
        "1..11..1",
        "1.2..2.1",
        ".2.33.2.",
        "...33...",
        "...44...",
    ],
    # түйе табан — след верблюда: трилистник
    "tuie_taban": [
        "..1..1..",
        ".121121.",
        "..2..2..",
        "...33...",
        "..3443..",
        "...44...",
    ],
    # құс қанаты — крылья птицы
    "qus_qanaty": [
        "1......1",
        "21....12",
        "321..123",
        ".432234.",
        "..4334..",
        "...44...",
    ],
    # сыңар мүйіз — одиночный рог-завиток
    "synar_muiz": [
        "..111...",
        ".1...1..",
        ".1.22.1.",
        ".1..2.1.",
        "..33.3..",
        "....3...",
    ],
    # тұмарша — треугольные обереги
    "tumarsha": [
        "11111111",
        ".122221.",
        "..1331..",
        "...11...",
        "...44...",
        "..4..4..",
    ],
}

# мелкие мотивы для шлема, рукавов и сапог (4 px)
SMALL = {
    "qoshqar_muiz": ["1..1", ".22.", "2..2"],
    "tuie_taban": [".1.1", "1.1.", ".2.."],
    "qus_qanaty": ["1..1", "2112", ".33."],
    "synar_muiz": [".11.", "1..2", ".22."],
    "tumarsha": ["1111", ".22.", "..3."],
}


def _grey(ch):
    g = TRIM_GREYS[ch]
    return (g, g, g, 255)


def _stamp(t, u, v, rows, mirror=False):
    for y, row in enumerate(rows):
        for x, ch in enumerate(row):
            if ch in TRIM_GREYS:
                xx = len(row) - 1 - x if mirror else x
                t.px[u + xx, v + y] = _grey(ch)


def _hline(t, u, v, w, ch="2"):
    for x in range(w):
        t.px[u + x, v] = _grey(ch if x % 2 else str(min(5, int(ch) + 1)))


def trim(pattern):
    """Текстуры отделки (humanoid, humanoid_leggings) для узора."""
    big = MOTIFS[pattern]
    small = SMALL[pattern]
    t = Tex()
    # шлем: кант по ободу и мотив на лбу, по бокам и сзади
    for (u, w) in ((0, 8), (8, 8), (16, 8), (24, 8)):
        _hline(t, u, 8 + 4, w, "2")
    _stamp(t, 8 + 2, 8 + 0, small)
    _stamp(t, 0 + 2, 8 + 1, small)
    _stamp(t, 16 + 2, 8 + 1, small, mirror=True)
    _stamp(t, 24 + 2, 8 + 0, small)
    # грудь: большой мотив, кант по вороту и подолу
    _stamp(t, 20, 20 + 1, big)
    _stamp(t, 32, 20 + 1, big)
    for (u, w) in ((16, 4), (20, 8), (28, 4), (32, 8)):
        _hline(t, u, 20, w, "1")
        _hline(t, u, 31, w, "3")
    # рукава: наплечник и обшлаг
    for (u, w) in ((40, 4), (44, 4), (48, 4), (52, 4)):
        _hline(t, u, 20, w, "1")
        _hline(t, u, 21, w, "3")
        _hline(t, u, 31, w, "2")
    _stamp(t, 44, 23, small)
    # сапоги: голенище и мотив спереди
    for (u, w) in ((0, 4), (4, 4), (8, 4), (12, 4)):
        _hline(t, u, 20 + 7, w, "1")
    _stamp(t, 4, 20 + 8, small)
    lg = Tex()
    # поножи: пояс и лампасы
    for (u, w) in ((16, 4), (20, 8), (28, 4), (32, 8)):
        _hline(lg, u, 20 + 7, w, "2")
    _stamp(lg, 20 + 2, 20 + 8, small)
    for (u, w) in ((0, 4), (4, 4), (8, 4), (12, 4)):
        _hline(lg, u, 20, w, "1")
    for y in range(21, 30):
        lg.px[0 + 1, y] = _grey("2" if y % 2 else "3")   # лампас сбоку
        lg.px[8 + 2, y] = _grey("2" if y % 2 else "3")
    _stamp(lg, 4, 22, small)
    return t.img, lg.img
