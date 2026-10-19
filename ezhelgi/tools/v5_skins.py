"""v5: skins (64x64, player-skin layout + hair/nose/claws in the lower half)
for Zheztyrnak, Albasty, Zhalmauyz kempir and the Batyr spirit.
"""
import os
import random

from PIL import Image

OUT = None  # textures/entity directory, set by run()


def c(h, a=255):
    h = h.lstrip("#")
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), a)


def shade(col, k):
    return tuple(max(0, min(255, int(v * k))) for v in col[:3]) + (col[3],)


class Skin:
    def __init__(self, seed):
        self.img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
        self.px = self.img.load()
        self.rng = random.Random(seed)

    def set(self, x, y, col):
        if 0 <= x < 64 and 0 <= y < 64:
            self.px[x, y] = col

    def rect(self, x0, y0, w, h, fn):
        """fn(x, y, w, h) -> colour (local coordinates)."""
        for yy in range(h):
            for xx in range(w):
                col = fn(xx, yy, w, h)
                if col is not None:
                    self.set(x0 + xx, y0 + yy, col)

    def box(self, u, v, w, h, d, side, front=None, back=None, top=None, bottom=None, right=None, left=None):
        """Paint a box net; every face function gets local (x, y, w, h)."""
        self.rect(u + d, v, w, d, top or side)
        self.rect(u + d + w, v, w, d, bottom or side)
        self.rect(u, v + d, d, h, right or side)
        self.rect(u + d, v + d, w, h, front or side)
        self.rect(u + d + w, v + d, d, h, left or side)
        self.rect(u + 2 * d + w, v + d, w, h, back or side)

    def save(self, name):
        self.img.save(os.path.join(OUT, name + ".png"))


def noisy(col, rng, amt=0.06):
    return shade(col, 1 + rng.uniform(-amt, amt))


def vgrad(col, y, h, top=1.08, bottom=0.88):
    t = y / max(1, h - 1)
    return shade(col, top + (bottom - top) * t)


# ------------------------------------------------------------------ helpers for faces
def face_base(s, skin, hair, fringe_rows=2, side_hair=True):
    """Head: hair on top/back/sides, skin on the face."""
    rng = s.rng

    def top(x, y, w, h):
        return noisy(hair, rng, 0.08)

    def back(x, y, w, h):
        return noisy(shade(hair, 0.95), rng, 0.08)

    def side(x, y, w, h):
        # hair covers the upper part and the back half of the sides
        if y < fringe_rows + 1 or (side_hair and (x <= 3 or y < 4)):
            return noisy(hair, rng, 0.08)
        return noisy(skin, rng, 0.03)

    def side_l(x, y, w, h):
        return side(w - 1 - x, y, w, h)

    def front(x, y, w, h):
        if y < fringe_rows:
            return noisy(hair, rng, 0.08)
        if side_hair and (x == 0 or x == w - 1) and y < 7:
            return noisy(shade(hair, 0.9), rng, 0.05)
        return noisy(skin, rng, 0.03)

    def bottom(x, y, w, h):
        return shade(skin, 0.8)
    s.box(0, 0, 8, 8, 8, side, front=front, back=back, top=top, bottom=bottom, right=side, left=side_l)


def put_front(s, pts):
    """pts: dict (x, y) -> colour in head-front coordinates (0..7)."""
    for (x, y), col in pts.items():
        s.set(8 + x, 8 + y, col)


def hair_strip(s, hair, length, rng, wild=False, braid=False, deco=None):
    """Hair box at (0,32): 9 x length x 1. Visible face is the back one."""
    dark = shade(hair, 0.75)
    light = shade(hair, 1.2)

    def strands(x, y, w, h):
        if wild and y > h - 4 and (x * 7 + y) % 3 == 0:
            return (0, 0, 0, 0)
        if y >= h - 2 and x in (0, w - 1):
            return (0, 0, 0, 0)
        if braid and 3 <= x <= 5:
            # коса посередине
            if (y + (x == 4)) % 3 == 0:
                return dark
            return light if x == 4 else hair
        if x % 3 == 0:
            return noisy(dark, rng, 0.05)
        if (x + y) % 5 == 0:
            return noisy(light, rng, 0.05)
        return noisy(hair, rng, 0.06)
    s.box(0, 32, 9, length, 1, strands)
    if deco:
        for (x, y, col) in deco:
            s.set(11 + x, 33 + y, col)


def nose(s, col):
    s.box(24, 32, 1, 2, 2, lambda x, y, w, h: shade(col, 1.1 - 0.1 * y))


def claws(s, col):
    for y in range(32, 42):
        for x in range(32, 37):
            k = 1.15 - (y - 32) * 0.04
            s.set(x, y, shade(col, k if (x + y) % 3 else k * 0.8))


# ------------------------------------------------------------------ Жезтырнақ
def zheztyrnak(angry):
    s = Skin(601 if not angry else 602)
    rng = s.rng
    skin = c("#f2d2b8") if not angry else c("#d9c8b4")
    hair = c("#1c1414")
    copper = c("#c8743a")
    face_base(s, skin, hair, fringe_rows=2)
    lip = c("#c2283a")
    if not angry:
        eyes = {
            (1, 3): c("#2a1c18"), (2, 3): c("#2a1c18"), (5, 3): c("#2a1c18"), (6, 3): c("#2a1c18"),  # брови
            (1, 4): c("#ffffff"), (2, 4): c("#3a2414"), (5, 4): c("#3a2414"), (6, 4): c("#ffffff"),
            (1, 5): shade(skin, 0.92), (6, 5): shade(skin, 0.92),
            (3, 4): shade(copper, 1.1), (4, 4): copper, (3, 5): copper, (4, 5): shade(copper, 0.85),  # медный нос
            (3, 6): lip, (4, 6): lip, (2, 6): shade(skin, 0.95), (5, 6): shade(skin, 0.95),
            (1, 6): c("#f0a8a0"), (6, 6): c("#f0a8a0"),  # румянец
        }
    else:
        glow = c("#ff9a3a")
        eyes = {
            (1, 3): c("#120808"), (2, 3): c("#120808"), (5, 3): c("#120808"), (6, 3): c("#120808"),
            (1, 4): glow, (2, 4): c("#ffd27a"), (5, 4): c("#ffd27a"), (6, 4): glow,
            (1, 5): c("#4a2a2a"), (2, 5): c("#4a2a2a"), (5, 5): c("#4a2a2a"), (6, 5): c("#4a2a2a"),
            (3, 4): shade(copper, 1.1), (4, 4): copper, (3, 5): copper, (4, 5): shade(copper, 0.85),
            (2, 6): c("#3a0c10"), (3, 6): c("#f4f0e0"), (4, 6): c("#f4f0e0"), (5, 6): c("#3a0c10"),
            (3, 7): c("#3a0c10"), (4, 7): c("#3a0c10"),
        }
    put_front(s, eyes)
    # головной убор-шапочка с пером (такия): слой шапки
    red = c("#a8202a")
    gold = c("#e8b83c")

    def hat_side(x, y, w, h):
        if y <= 1:
            return gold if (x + y) % 2 == 0 else red
        return (0, 0, 0, 0)

    def hat_top(x, y, w, h):
        if 2 <= x <= 5 and 2 <= y <= 5:
            return red if (x + y) % 3 else gold
        return (0, 0, 0, 0)
    s.box(32, 0, 8, 8, 8, hat_side, top=hat_top, bottom=lambda *a: (0, 0, 0, 0))
    # платье: белая рубаха с красным камзолом и золотой тесьмой
    white = c("#f2ece0")
    kamzol = c("#8e1c26") if not angry else c("#5e1218")

    def body_front(x, y, w, h):
        if y == 0:
            return gold
        if 2 <= x <= 5 and y <= 9:
            # белая рубаха в вырезе, ряд пуговиц
            if x in (3, 4) and y % 3 == 1:
                return gold
            return noisy(white, rng, 0.03)
        if y == 10:
            return gold
        return noisy(vgrad(kamzol, y, h), rng, 0.04)

    def body_side(x, y, w, h):
        if y == 10 or y == 0:
            return gold
        return noisy(vgrad(kamzol, y, h), rng, 0.04)
    s.box(16, 16, 8, 12, 4, body_side, front=body_front, back=body_side, top=lambda *a: gold,
          bottom=lambda *a: noisy(white, rng, 0.04))

    # рукава: белые с вышивкой, медные пальцы
    def arm(x, y, w, h):
        if y >= 10:
            return copper if y == 11 else noisy(skin, rng, 0.03)
        if y == 9:
            return gold
        if (x + y) % 4 == 0 and y > 2:
            return c("#c03030")
        return noisy(vgrad(white, y, h), rng, 0.03)
    s.box(40, 16, 4, 12, 4, arm, top=lambda *a: noisy(white, rng, 0.03), bottom=lambda *a: copper)

    # длинная юбка и сафьяновые сапожки
    def leg(x, y, w, h):
        if y >= 10:
            return c("#6a1a1a") if y == 10 else c("#401010")
        if y == 8:
            return gold
        if y == 9 and x % 2 == 0:
            return c("#c03030")
        return noisy(vgrad(white, y, h, 1.0, 0.9), rng, 0.03)
    s.box(0, 16, 4, 12, 4, leg)
    hair_strip(s, hair, 17, rng, braid=True, deco=[(4, 15, gold), (4, 16, c("#2bb5a6"))])
    nose(s, copper)
    claws(s, copper)
    s.save("zheztyrnak_angry" if angry else "zheztyrnak")


# ------------------------------------------------------------------ Албасты
def albasty(angry):
    s = Skin(611 if not angry else 612)
    rng = s.rng
    skin = c("#d8dcd0") if not angry else c("#c4cbbd")
    hair = c("#c9b98a")
    face_base(s, skin, hair, fringe_rows=3)
    if not angry:
        f = {(1, 4): c("#3a3a40"), (2, 4): c("#101014"), (5, 4): c("#101014"), (6, 4): c("#3a3a40"),
             (1, 5): c("#8a9088"), (2, 5): c("#8a9088"), (5, 5): c("#8a9088"), (6, 5): c("#8a9088"),
             (3, 6): c("#6a5a5a"), (4, 6): c("#6a5a5a")}
    else:
        red = c("#ff3030")
        f = {(1, 4): c("#200808"), (2, 4): red, (5, 4): red, (6, 4): c("#200808"),
             (1, 5): c("#3a2a2a"), (2, 5): c("#3a2a2a"), (5, 5): c("#3a2a2a"), (6, 5): c("#3a2a2a"),
             (2, 6): c("#140808"), (3, 6): c("#140808"), (4, 6): c("#140808"), (5, 6): c("#140808"),
             (3, 7): c("#140808"), (4, 7): c("#140808")}
    # волосы закрывают пол-лица
    for y in range(3, 8):
        f[(0, y)] = noisy(hair, rng, 0.08)
        if y < 6:
            f[(7, y)] = noisy(shade(hair, 0.9), rng, 0.08)
    put_front(s, f)
    # «шапка»: спутанные пряди поверх головы
    def hat(x, y, w, h):
        if y <= 2 or (x in (0, w - 1) and y <= 6) or rng.random() < 0.08:
            return noisy(shade(hair, 1.05), rng, 0.1)
        return (0, 0, 0, 0)
    s.box(32, 0, 8, 8, 8, hat, top=lambda *a: noisy(hair, rng, 0.1), bottom=lambda *a: (0, 0, 0, 0),
          front=lambda x, y, w, h: noisy(hair, rng, 0.1) if (y <= 1 or (x == 0 and y < 7)) else (0, 0, 0, 0))
    gown = c("#e6e2d6") if not angry else c("#cdc6b4")

    def robe(x, y, w, h):
        col = noisy(vgrad(gown, y, h, 1.05, 0.8), rng, 0.05)
        if (x * 5 + y * 3) % 17 == 0:
            col = shade(col, 0.8)
        return col
    s.box(16, 16, 8, 12, 4, robe)

    def arm(x, y, w, h):
        if y >= 10:
            return noisy(skin, rng, 0.04) if y < 11 else c("#3a3a30")  # длинные тёмные ногти
        return robe(x, y, w, h)
    s.box(40, 16, 4, 12, 4, arm)

    def leg(x, y, w, h):
        if y >= 11:
            # ступни повёрнуты назад — видно пятку спереди
            return noisy(skin, rng, 0.04)
        if y == 10 and (x + y) % 2:
            return (0, 0, 0, 0)
        return robe(x, y, w, h)
    s.box(0, 16, 4, 12, 4, leg)
    hair_strip(s, hair, 22, rng, wild=True)
    nose(s, skin)
    claws(s, c("#3a3a30"))
    s.save("albasty_angry" if angry else "albasty")


# ------------------------------------------------------------------ Жалмауыз кемпір
def kempir(cast):
    s = Skin(621 if not cast else 622)
    rng = s.rng
    skin = c("#a8865e")
    hair = c("#b8b4ac")
    face_base(s, skin, hair, fringe_rows=1)
    eye = c("#7aff5a") if cast else c("#f0e070")
    f = {(1, 3): c("#e0dcd4"), (2, 3): c("#e0dcd4"), (5, 3): c("#e0dcd4"), (6, 3): c("#e0dcd4"),  # седые брови
         (1, 4): c("#1a1208"), (2, 4): eye, (5, 4): eye, (6, 4): c("#1a1208"),
         (1, 5): shade(skin, 0.8), (6, 5): shade(skin, 0.8), (2, 2): shade(skin, 0.85), (5, 2): shade(skin, 0.85),
         (2, 6): c("#3a2010"), (3, 6): c("#f0e8c8"), (4, 6): c("#3a2010"), (5, 6): c("#3a2010"),
         (3, 7): shade(skin, 0.85), (4, 7): shade(skin, 0.85), (1, 7): shade(skin, 0.85), (6, 7): shade(skin, 0.85)}
    put_front(s, f)
    # платок (жаулық) — тёмный с узором
    scarf = c("#3a2a48")

    def hat(x, y, w, h):
        if y <= 2:
            return noisy(scarf, rng, 0.06) if (x + y) % 4 else c("#7a5a3a")
        return (0, 0, 0, 0)
    s.box(32, 0, 8, 8, 8, hat, top=lambda *a: noisy(scarf, rng, 0.06), bottom=lambda *a: (0, 0, 0, 0),
          front=lambda x, y, w, h: noisy(scarf, rng, 0.06) if y == 0 else (0, 0, 0, 0),
          back=lambda x, y, w, h: noisy(scarf, rng, 0.06) if y <= 5 else (0, 0, 0, 0))
    robe = c("#4a3046")
    patch = c("#6a5030")
    bone = c("#e8e0c8")

    def body_front(x, y, w, h):
        if y == 1 and x % 2 == 0:
            return bone  # ожерелье из костей
        if y == 2 and x in (3, 4):
            return c("#c03020")  # талисман
        if 7 <= y <= 8:
            return c("#2a1a14")  # кушак
        if (x, y) in ((1, 5), (2, 5), (1, 6), (6, 9), (5, 10)):
            return patch
        return noisy(vgrad(robe, y, h), rng, 0.07)
    s.box(16, 16, 8, 12, 4, lambda x, y, w, h: noisy(vgrad(robe, y, h), rng, 0.07), front=body_front)

    def arm(x, y, w, h):
        if y >= 10:
            return noisy(skin, rng, 0.05) if y == 10 else c("#2a2418")
        return noisy(vgrad(robe, y, h), rng, 0.07)
    s.box(40, 16, 4, 12, 4, arm)

    def leg(x, y, w, h):
        if y >= 10:
            return c("#2a1a10")
        return noisy(vgrad(shade(robe, 0.9), y, h), rng, 0.07)
    s.box(0, 16, 4, 12, 4, leg)
    hair_strip(s, hair, 14, rng, wild=True)
    nose(s, shade(skin, 0.95))
    claws(s, c("#2a2418"))
    s.save("zhalmauyz_kempir_cast" if cast else "zhalmauyz_kempir")


# ------------------------------------------------------------------ Батыр аруағы
def ghost():
    s = Skin(631)
    rng = s.rng

    def g(hexcol, a):
        return c(hexcol, a)
    skin = g("#bfe8ff", 150)
    beard = g("#e8f8ff", 175)
    steel = g("#8ec8e8", 190)
    steel_d = g("#5f9cc4", 185)
    glow = g("#e0ffff", 255)
    face_base(s, skin, steel, fringe_rows=2, side_hair=False)
    f = {(1, 4): glow, (2, 4): g("#60f0ff", 255), (5, 4): g("#60f0ff", 255), (6, 4): glow,
         (1, 3): steel_d, (2, 3): steel_d, (5, 3): steel_d, (6, 3): steel_d}
    for x in range(1, 7):
        f[(x, 6)] = beard
        f[(x, 7)] = beard
    f[(3, 5)] = beard
    f[(4, 5)] = beard
    put_front(s, f)

    # шлем-дулыга с кольчужной бармицей (слой шапки)
    def helm(x, y, w, h):
        if y <= 2:
            return steel if (x + y) % 3 else steel_d
        if y >= 3:
            return g("#7ab4d8", 165) if (x + y) % 2 == 0 else g("#5f9cc4", 120)  # кольчуга
        return (0, 0, 0, 0)

    def helm_front(x, y, w, h):
        if y <= 1:
            return steel if (x + y) % 3 else steel_d
        if y == 2 and x in (3, 4):
            return steel_d  # наносник
        if y == 3 and x in (3, 4):
            return steel_d
        return (0, 0, 0, 0)
    s.box(32, 0, 8, 8, 8, helm, front=helm_front, top=lambda x, y, w, h: steel if (x + y) % 2 else glow,
          bottom=lambda *a: (0, 0, 0, 0))

    def lamellar(x, y, w, h):
        if y % 3 == 0:
            return steel_d
        col = steel if (x + y // 3) % 2 else g("#a8daf4", 190)
        return col

    def body_front(x, y, w, h):
        if y in (8, 9):
            return g("#d8f4ff", 210)  # пояс
        if y == 9 and x in (3, 4):
            return glow
        return lamellar(x, y, w, h)
    s.box(16, 16, 8, 12, 4, lamellar, front=body_front)

    def arm(x, y, w, h):
        if y >= 10:
            return skin
        if y <= 3:
            return lamellar(x, y, w, h)
        return g("#9fd4f0", 160)
    s.box(40, 16, 4, 12, 4, arm)

    def leg(x, y, w, h):
        # ноги растворяются в дымке
        a = int(170 * (1 - y / 13))
        base = (150, 210, 240, max(25, a))
        return base if (x + y) % 3 else (120, 190, 230, max(20, a - 20))
    s.box(0, 16, 4, 12, 4, leg)
    s.save("batyr_aruagy")


def run(out_dir):
    global OUT
    OUT = out_dir
    zheztyrnak(False)
    zheztyrnak(True)
    albasty(False)
    albasty(True)
    kempir(False)
    kempir(True)
    ghost()
