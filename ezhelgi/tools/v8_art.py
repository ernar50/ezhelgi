"""v8: облики (шабандозы, ақыны, келін, жыршы), национальная одежда (иконки и экипировка),
асық и сақа, ряд асыков.
"""
import math
import random
from PIL import Image

import v5_skins as V
import v6_skins as S6
from v5_skins import Skin, c, shade, noisy, vgrad
from v7_armor import Tex, shade as sh, GOLD, RED, CRIMSON, WHITE, LEATHER, DLEATHER, FOX, CLOTH_BLUE, STEEL, TURQ
from v7_items import outline, from_rows

T = (0, 0, 0, 0)


# ------------------------------------------------------------------ облики
def shabandoz(team):
    s = Skin(8100 + team)
    skin = ["#c89068", "#d6a47a", "#e2b48c"][team]
    if team == 2:
        # юный наездник бәйге
        S6.head(s, c(skin), c("#1a1412"), eyes="#2a1c14", bald_sides=True)
        S6.hat_taqiya(s, "#c0392b", "#e8b83c")
        S6.chapan(s, "#e8e2d4", "#c0392b", sash="#c0392b", pants="#2a2a3a", boots="#2a1a12", long=False, skin=skin)
    else:
        robe = "#8e1c26" if team == 0 else "#1e3a8a"
        S6.head(s, c(skin), c("#1a1412"), moustache="#1a1412", eyes="#2a1c14", bald_sides=True)
        S6.hat_borik(s, "#4a3420", robe)
        S6.chapan(s, robe, "#e8b83c", sash="#2a1a12", pants="#3a2a20", boots="#1a120c", long=False, skin=skin)
    return s


def aqyn(i):
    s = Skin(8200 + i)
    if i == 1:
        S6.head(s, c("#e2b48c"), c("#1a1412"), female=True, eyes="#2a1c14")
        S6.dress(s, "#1e5a5a", "#f2ece0", "#e8b83c", skin="#e2b48c")
        S6.hat_uki(s, "#1e3a6a")
    else:
        S6.head(s, c("#c89068"), c("#1a1412"), beard="#1a1412", moustache="#1a1412", eyes="#1a120c", bald_sides=True)
        S6.hat_qalpaq(s)
        S6.chapan(s, "#4a2a5a", "#e8b83c", sash="#e8b83c", open_front="#f2ece0", skin="#c89068")
    return s


def kelin():
    """Невеста: белое платье с красным камзолом, высокий сәукеле с золотом и жемчугом."""
    s = Skin(8300)
    S6.head(s, c("#e8c09a"), c("#1a1412"), female=True, eyes="#2a1c14")
    S6.dress(s, "#a01e2a", "#f6f2ea", "#e8b83c", skin="#e8c09a")
    S6.clear_hat(s)
    W, R, G = c("#f6f2ea"), c("#a01e2a"), c("#e8b83c")

    def side(x, y, w, h):
        if y <= 3:
            return G if (x + y) % 3 == 0 else (R if y <= 1 else W)
        if y in (4, 5) and x in (0, w - 1):
            return G  # подвески у висков
        return T

    def front(x, y, w, h):
        if y <= 1:
            return R
        if y == 2:
            return G if x % 2 else c("#f0f0f0")  # жемчуг
        if y == 3:
            return W
        if x in (0, 7) and y <= 7:
            return G if y % 2 else c("#f0f0f0")
        return T
    s.box(32, 0, 8, 8, 8, side, top=lambda x, y, w, h: G if (x + y) % 2 else R, bottom=lambda *k: T, front=front)
    return s


def zhyrshy():
    s = Skin(8400)
    S6.head(s, c("#c08a62"), c("#d8d4cc"), beard="#e8e4dc", moustache="#e8e4dc", old=True, eyes="#2a1c14")
    S6.hat_borik(s, "#6a4a2e", "#2e5a3a")
    S6.chapan(s, "#2e5a3a", "#e8b83c", sash="#c0392b", open_front="#f2ece0", skin="#c08a62")
    return s


SKINS = {"shabandoz_red": lambda: shabandoz(0), "shabandoz_blue": lambda: shabandoz(1), "shabandoz_jockey": lambda: shabandoz(2),
         "aqyn_0": lambda: aqyn(0), "aqyn_1": lambda: aqyn(1), "kelin": kelin, "zhyrshy": zhyrshy}


# ------------------------------------------------------------------ экипировка одежды (64x32)
VELVET_BLUE = [(96, 120, 200), (60, 84, 160), (40, 60, 128), (28, 44, 96), (18, 30, 68), (10, 18, 44)]
VELVET_RED = [(220, 80, 80), (176, 40, 48), (140, 24, 36), (104, 16, 28), (72, 10, 20), (44, 6, 12)]
FELT_WHITE = [(255, 255, 255), (246, 242, 234), (226, 220, 208), (200, 194, 180), (160, 154, 142), (110, 104, 96)]
GREEN_V = [(110, 180, 120), (64, 140, 90), (40, 106, 66), (28, 78, 48), (18, 54, 32), (10, 34, 20)]


def velvet(pal):
    def f(x, y, w, h):
        i = 2 + (1 if (x * 5 + y * 3) % 7 == 0 else 0) - (1 if (x + y) % 9 == 0 else 0)
        return sh(pal, i)
    return f


def trimmed(base, trim, edges):
    """edges: функция (x, y, w, h) -> True, если тут золотая кайма."""
    def f(x, y, w, h):
        if edges(x, y, w, h):
            return sh(trim, 1 if (x + y) % 2 else 2)
        return base(x, y, w, h)
    return f


def clothes_textures():
    out = {}
    # шапан — длинный бархатный халат с золотой каймой по борту и обшлагам
    t = Tex()
    body = trimmed(velvet(VELVET_BLUE), GOLD, lambda x, y, w, h: y == 0 or y >= h - 1)
    front = trimmed(velvet(VELVET_BLUE), GOLD, lambda x, y, w, h: x in (3, 4) or y >= h - 1 or y == 0)
    t.box(16, 16, 8, 12, 4, body, top=velvet(VELVET_BLUE), bottom=velvet(VELVET_BLUE), front=front)
    t.box(40, 16, 4, 12, 4, trimmed(velvet(VELVET_BLUE), GOLD, lambda x, y, w, h: y in (10, 11)), top=velvet(VELVET_BLUE))
    out["shapan"] = t.img
    # камзол — безрукавка с пуговицами
    t = Tex()
    vest = trimmed(velvet(VELVET_RED), GOLD, lambda x, y, w, h: y == 0 or y == 8)

    def vest_front(x, y, w, h):
        if y > 8:
            return None
        if x in (3, 4) and y % 2 == 1:
            return sh(GOLD, 0)
        return vest(x, y, w, h)
    t.box(16, 16, 8, 12, 4, lambda x, y, w, h: vest(x, y, w, h) if y <= 8 else None, top=vest, front=vest_front)
    out["kamzol"] = t.img
    # тақия — тюбетейка с узором
    t = Tex()
    tak = lambda x, y, w, h: (sh(GOLD, 1) if (x + y) % 3 == 0 else sh(GREEN_V, 3)) if y <= 2 else None
    t.box(0, 0, 8, 8, 8, tak, top=lambda x, y, w, h: sh(GOLD, 1) if (x * y) % 5 == 1 else sh(GREEN_V, 3))
    out["taqiya"] = t.img
    # кимешек — белый, закрывает голову и шею, вышивка вокруг лица
    t = Tex()
    kim = lambda x, y, w, h: sh(RED, 2) if y == 2 else sh(FELT_WHITE, 1 if (x + y) % 5 else 2)

    def kim_front(x, y, w, h):
        if y <= 1:
            return sh(FELT_WHITE, 1)
        if y == 2:
            return sh(RED, 2)
        if x in (0, 7):
            return sh(RED, 2) if y % 2 else sh(FELT_WHITE, 1)
        return None
    t.box(0, 0, 8, 8, 8, kim, top=lambda *a: sh(FELT_WHITE, 1), front=kim_front)
    out["kimeshek"] = t.img
    # сәукеле — свадебный убор: красный бархат, золото, жемчужные подвески
    t = Tex()

    def sauk(x, y, w, h):
        if y <= 4:
            return sh(GOLD, 1) if (x + y) % 3 == 0 else (sh(VELVET_RED, 2) if y <= 2 else sh(FELT_WHITE, 1))
        if x in (0, w - 1):
            return sh(GOLD, 0) if y % 2 else (240, 240, 240, 255)
        return None

    def sauk_front(x, y, w, h):
        if y <= 2:
            return sh(VELVET_RED, 2) if (x + y) % 3 else sh(GOLD, 0)
        if y == 3:
            return sh(GOLD, 1) if x % 2 else (240, 240, 240, 255)
        if x in (0, 7):
            return sh(GOLD, 0) if y % 2 else (240, 240, 240, 255)
        return None
    t.box(0, 0, 8, 8, 8, sauk, top=lambda x, y, w, h: sh(GOLD, 1) if (x + y) % 2 else sh(VELVET_RED, 2), front=sauk_front)
    for x in range(3, 5):  # навершие с перьями
        for y in range(3, 5):
            t.px[40 + x, y] = (250, 250, 250, 255)
    out["saukele"] = t.img
    # бөрік с перьями филина (үкі)
    t = Tex()
    bor = lambda x, y, w, h: sh(FOX, 2 if (x + y) % 3 else 3) if 2 <= y <= 3 else (sh(VELVET_RED, 2) if y <= 1 else None)
    t.box(0, 0, 8, 8, 8, bor, top=lambda x, y, w, h: sh(VELVET_RED, 2))
    for (x, y) in ((3, 3), (4, 3), (3, 4), (4, 2)):
        t.px[40 + x, y] = (248, 248, 244, 255) if (x + y) % 2 else (200, 196, 188, 255)
    out["uki_borik"] = t.img
    # келін көйлек — свадебное платье: белое с красным камзолом и золотом
    t = Tex()

    def dress(x, y, w, h):
        if y <= 6:
            return sh(VELVET_RED, 2) if (x + y) % 6 else sh(GOLD, 1)
        return sh(FELT_WHITE, 1 if (x + y) % 4 else 2)
    t.box(16, 16, 8, 12, 4, dress, top=dress)
    t.box(40, 16, 4, 12, 4, lambda x, y, w, h: sh(GOLD, 1) if y in (9, 10) else sh(FELT_WHITE, 1 if (x + y) % 3 else 2))
    out["kelin_koilek"] = t.img
    return out


# ------------------------------------------------------------------ иконки
ICONS = {
    "shapan": ([
        "................",
        "....gg....gg....",
        "...bbbg..gbbb...",
        "..bbbbbggbbbbb..",
        ".bbbbbbggbbbbbb.",
        ".bbb.bbggbb.bbb.",
        ".bbb.bbggbb.bbb.",
        ".gg..bbggbb..gg.",
        ".....bbggbb.....",
        ".....bbggbb.....",
        "....bbbggbbb....",
        "....bbbggbbb....",
        "....bbbggbbb....",
        "....gggggggg....",
        "................",
        "................"], {"b": "#2a3e8e", "g": "#e8b83c"}),
    "kamzol": ([
        "................",
        "................",
        "....rr....rr....",
        "...rrrg..grrr...",
        "...rrrrggrrrr...",
        "...rrrrgyrrrr...",
        "...rrrrggrrrr...",
        "...rrrrgyrrrr...",
        "...rrrrggrrrr...",
        "...rrrrgyrrrr...",
        "...rrrrggrrrr...",
        "...gggggggggg...",
        "................",
        "................",
        "................",
        "................"], {"r": "#9a1e2a", "g": "#e8b83c", "y": "#fff1a8"}),
    "taqiya": ([
        "................",
        "................",
        "................",
        "................",
        "......gggg......",
        "....gggggggg....",
        "...ggyggggygg...",
        "..ggggyggyggggg.",
        "..gygggyyggggyg.",
        "..yyyyyyyyyyyy..",
        "..gggggggggggg..",
        "................",
        "................",
        "................",
        "................",
        "................"], {"g": "#1e5a3a", "y": "#e8b83c"}),
    "kimeshek": ([
        "................",
        ".....wwwwww.....",
        "....wwwwwwww....",
        "...wwwwwwwwww...",
        "...wwrrrrrrww...",
        "...wwr....rww...",
        "...wwr....rww...",
        "...wwr....rww...",
        "...wwrrrrrrww...",
        "..wwwwwwwwwwww..",
        ".wwwwwwwwwwwwww.",
        ".wwwwwrrrrwwwww.",
        "..wwwwwwwwwwww..",
        "................",
        "................",
        "................"], {"w": "#f4f0e6", "r": "#c0392b"}),
    "saukele": ([
        ".......f........",
        "......fwf.......",
        ".......g........",
        "......rgr.......",
        "......rrr.......",
        ".....rgrgr......",
        ".....rrrrr......",
        "....rrgrgrr.....",
        "....rrrrrrr.....",
        "...gpgpgpgpg....",
        "...wwwwwwwww....",
        "...p.......p....",
        "...g.......g....",
        "...p.......p....",
        "...g.......g....",
        "................"], {"r": "#a01e2a", "g": "#e8b83c", "w": "#f6f2ea", "p": "#ffffff", "f": "#f8f8f4"}),
    "uki_borik": ([
        "........ff......",
        ".......fwf......",
        "........f.......",
        "......rrrr......",
        ".....rrrrrr.....",
        "....rrrrrrrr....",
        "...ooooooooo....",
        "..ooooooooooo...",
        "..oyoooyoooyo...",
        "..ooooooooooo...",
        "...ooooooooo....",
        "................",
        "................",
        "................",
        "................",
        "................"], {"r": "#9a1e2a", "o": "#c8803a", "y": "#e8b060", "f": "#f8f8f4", "w": "#c8c4bc"}),
    "kelin_koilek": ([
        "................",
        "....rr....rr....",
        "...rrrg..grrr...",
        "..rrrrrggrrrrr..",
        "..wrrrrggrrrrw..",
        "..wrrrrggrrrrw..",
        "..ww.rrggrr.ww..",
        "..gg.gggggg.gg..",
        ".....wwwwww.....",
        "....wwwwwwww....",
        "...wwwwwwwwww...",
        "...wwwgwwgwww...",
        "..wwwwwwwwwwww..",
        "..gggggggggggg..",
        "................",
        "................"], {"r": "#a01e2a", "g": "#e8b83c", "w": "#f6f2ea"}),
    "kumis_belbeu": ([
        "................",
        "................",
        "................",
        "................",
        "................",
        "................",
        "llllllllllllllll",
        "lsslsslsslsslssl",
        "lsslsslsslsslssl",
        "llllllllllllllll",
        "................",
        "................",
        "................",
        "................",
        "................",
        "................"], {"l": "#5a3a1e", "s": "#d8dce4"}),
    "altyn_belbeu": ([
        "................",
        "................",
        "................",
        "................",
        "................",
        "................",
        "llllllllllllllll",
        "lgglggtggtgglggl",
        "lgglggtggtgglggl",
        "llllllllllllllll",
        "................",
        "................",
        "................",
        "................",
        "................",
        "................"], {"l": "#3a2414", "g": "#e8b83c", "t": "#2bb5a6"}),
    "asyq": ([
        "................",
        "................",
        "................",
        "................",
        "......bbbb......",
        ".....bwwwwb.....",
        "....bwwbbwwb....",
        "....bwb..bwb....",
        "....bwwbbwwb....",
        "....bwwwwwwb....",
        ".....bwwwwb.....",
        "......bbbb......",
        "................",
        "................",
        "................",
        "................"], {"b": "#b8a888", "w": "#f2ead6"}),
    "saqa": ([
        "................",
        "................",
        "................",
        ".....rrrrrr.....",
        "....rwwwwwwr....",
        "...rwwrrrrwwr...",
        "...rwr....rwr...",
        "...rwr....rwr...",
        "...rwwrrrrwwr...",
        "...rwwwwwwwwr...",
        "....rwwwwwwr....",
        ".....rrrrrr.....",
        "................",
        "................",
        "................",
        "................"], {"r": "#a01e2a", "w": "#e0705a"}),
}


def icons():
    return {n: outline(from_rows(rows, pal)) for n, (rows, pal) in ICONS.items()}


def bone_texture():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    for y in range(16):
        for x in range(16):
            v = (x * 7 + y * 3) % 5
            px[x, y] = (242, 234, 214, 255) if v else (214, 202, 178, 255)
    return img


def asyq_models(ns):
    """Модели ряда асыков: от 0 до 5 косточек."""
    models = {}
    xs = [1.5, 4.5, 7.5, 10.5, 13.5]
    for n in range(6):
        els = []
        for i in range(n):
            x = xs[i]
            els.append({"from": [x - 0.5, 0, 7], "to": [x + 1.5, 1.5, 9.5],
                        "faces": {f: {"uv": [0, 0, 2, 2], "texture": "#bone"} for f in ("north", "south", "east", "west", "up", "down")}})
            els.append({"from": [x - 0.25, 1.5, 7.5], "to": [x + 1.25, 2.25, 9],
                        "faces": {f: {"uv": [2, 0, 4, 1], "texture": "#bone"} for f in ("north", "south", "east", "west", "up")}})
        # меловая черта, у которой стоит ряд
        els.append({"from": [0, 0, 12], "to": [16, 0.1, 12.5],
                    "faces": {"up": {"uv": [0, 15, 16, 16], "texture": "#bone"}}})
        models[n] = {"textures": {"bone": f"{ns}:block/asyq_bone", "particle": f"{ns}:block/asyq_bone"}, "elements": els}
    return models


# ------------------------------------------------------------------ жыны трёх цветов (перекраска текстуры модели)
def zhyn_variants(src):
    """Из общей текстуры жына делает три: чёрный, красный, синий. Рога и глаза остаются."""
    tints = {"zhyn_qara": ((18, 16, 22), (70, 66, 80)), "zhyn_qyzyl": ((110, 14, 10), (230, 90, 40)),
             "zhyn_kok": ((14, 40, 110), (90, 180, 240))}
    out = {}
    base = src.convert("RGBA")
    for name, (dark, light) in tints.items():
        img = base.copy()
        px = img.load()
        for y in range(img.size[1]):
            for x in range(img.size[0]):
                r, g, b, a = px[x, y]
                if a == 0:
                    continue
                if r > 200 and g > 190:  # глаза и рога
                    continue
                lum = (r * 0.3 + g * 0.59 + b * 0.11) / 160.0
                lum = max(0.0, min(1.0, lum))
                px[x, y] = tuple(int(dark[i] + (light[i] - dark[i]) * lum) for i in range(3)) + (a,)
        out[name] = img
    return out


def darwish_skin():
    s = Skin(8500)
    S6.head(s, c("#b88a62"), c("#e8e4dc"), beard="#f2f0ea", moustache="#f2f0ea", old=True, eyes="#2a1c14")
    S6.hat_turban(s)
    S6.chapan(s, "#e8e2d4", "#8a6a3a", sash="#5a3a1e", open_front="#d8d0c0", pants="#c8c0b0", boots="#5a3a1e", skin="#b88a62")
    return s


CAVE_ICONS = {
    "zhylan_tumary": ([
        "................",
        "......gggg......",
        ".....g....g.....",
        "....g......g....",
        "....g......g....",
        ".....g....g.....",
        "......gttg......",
        ".....ssssss.....",
        "....sssttsss....",
        "....sstsstss....",
        ".....ssssss.....",
        "......ssss......",
        ".......ss.......",
        "................",
        "................",
        "................"], {"g": "#c8a040", "s": "#3a8a40", "t": "#e8d060"}),
    "shoiyn_qulaq": ([
        "................",
        ".....dddd.......",
        "....dlllld......",
        "...dlliilld.....",
        "...dli..ild.....",
        "...dli...ld.....",
        "...dlli..ld.....",
        "....dli.ild.....",
        ".....dliild.....",
        ".....dlllld.....",
        "......dlld......",
        "......dlld......",
        ".......dd.......",
        "................",
        "................",
        "................"], {"d": "#2a2c30", "l": "#6a6e76", "i": "#a8acb4"}),
    "qola": ([
        "................",
        "................",
        "................",
        "................",
        "....lllllll.....",
        "...lbbbbbbbl....",
        "..lbbbbbbbbbd...",
        "..ddddddddddd...",
        "................",
        "................",
        "................",
        "................",
        "................",
        "................",
        "................",
        "................"], {"l": "#f0c080", "b": "#c88a40", "d": "#7a4a1e"}),
    "qola_qanjar": ([
        "................",
        "...........ll...",
        "..........lbl...",
        ".........lbd....",
        "........lbd.....",
        ".......lbd......",
        "......lbd.......",
        ".....lbd........",
        "...gglbd........",
        "....ggd.........",
        "....wgg.........",
        "...w..g.........",
        "..w.............",
        ".w..............",
        "................",
        "................"], {"l": "#f0c080", "b": "#c88a40", "d": "#7a4a1e", "g": "#a07020", "w": "#5a3a1e"}),
    "qola_balta": ([
        "................",
        "......lllb......",
        ".....lbbbbb.....",
        "....lbbbbbbd....",
        "....bbbbwbbd....",
        ".....bbbwbd.....",
        "......ddwd......",
        "........w.......",
        "........w.......",
        "........w.......",
        "........w.......",
        "........w.......",
        "........w.......",
        "........w.......",
        "................",
        "................"], {"l": "#f0c080", "b": "#c88a40", "d": "#7a4a1e", "w": "#5a3a1e"}),
}


def cave_icons():
    return {n: outline(from_rows(rows, pal)) for n, (rows, pal) in CAVE_ICONS.items()}
