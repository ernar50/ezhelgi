"""v6 skins: steppe villagers (20 looks), Abai, Shoqan, al-Farabi, Qorqyt ata,
balbal (statue), baqsy shadow, dau (giant) and the qaraqshy bandit.
Uses the 64x64 player-skin layout (head, hat, body, arm, leg).
"""
import os
import random

import v5_skins as V
from v5_skins import Skin, c, shade, noisy, vgrad

T = (0, 0, 0, 0)


def head(s, skin, hair, beard=None, moustache=None, eyes="#2a1c14", brows=None, fringe=2, bald_sides=False,
         old=False, female=False, glow=None):
    rng = s.rng
    V.face_base(s, skin, hair, fringe_rows=fringe, side_hair=not bald_sides)
    f = {}
    br = brows or (shade(hair, 0.9) if not old else c("#d8d4cc"))
    for x in (1, 2, 5, 6):
        f[(x, 3)] = br
    ec = c(eyes) if isinstance(eyes, str) else eyes
    if glow:
        f[(1, 4)] = c(glow); f[(2, 4)] = c(glow); f[(5, 4)] = c(glow); f[(6, 4)] = c(glow)
    else:
        f[(1, 4)] = c("#f4f0e8"); f[(2, 4)] = ec; f[(5, 4)] = ec; f[(6, 4)] = c("#f4f0e8")
    f[(3, 5)] = shade(skin, 0.85)
    f[(4, 5)] = shade(skin, 0.85)
    f[(3, 6)] = c("#8a4a40") if not female else c("#c04050")
    f[(4, 6)] = c("#8a4a40") if not female else c("#c04050")
    if old:
        f[(1, 5)] = shade(skin, 0.85); f[(6, 5)] = shade(skin, 0.85)
    if female:
        f[(1, 5)] = c("#e8a8a0"); f[(6, 5)] = c("#e8a8a0")
    if moustache:
        m = c(moustache)
        for x in (2, 3, 4, 5):
            f[(x, 6)] = m
        f[(1, 7)] = m; f[(6, 7)] = m
        f[(3, 6)] = shade(m, 0.8); f[(4, 6)] = shade(m, 0.8)
    if beard:
        b = c(beard)
        for x in range(1, 7):
            f[(x, 7)] = noisy(b, rng, 0.06)
        for x in (2, 5):
            f[(x, 6)] = b
        # борода на боках и снизу
        for yy in (6, 7):
            for xx in range(4, 8):
                s.set(xx, 8 + yy, noisy(b, rng, 0.06))
                s.set(16 + 7 - xx, 8 + yy, noisy(b, rng, 0.06))
        for xx in range(8):
            for yy in range(8):
                s.set(16 + xx, yy, noisy(b, rng, 0.06))
    V.put_front(s, f)


def clear_hat(s):
    for y in range(0, 16):
        for x in range(32, 64):
            s.set(x, y, T)


def hat_taqiya(s, col, pattern):
    """Тюбетейка: верх головы + один ряд по кругу."""
    clear_hat(s)
    a, b = c(col), c(pattern)

    def side(x, y, w, h):
        if y <= 1:
            return b if (x + y) % 3 == 0 else a
        return T
    s.box(32, 0, 8, 8, 8, side, top=lambda x, y, w, h: b if (x * y) % 5 == 1 else a, bottom=lambda *k: T)


def hat_borik(s, fur, top):
    """Бөрік — меховая шапка с бархатным верхом."""
    clear_hat(s)
    F, Tp = c(fur), c(top)

    def side(x, y, w, h):
        if y <= 1:
            return noisy(Tp, s.rng, 0.05)
        if y <= 3:
            return noisy(F, s.rng, 0.12)
        return T
    s.box(32, 0, 8, 8, 8, side, top=lambda *k: noisy(Tp, s.rng, 0.05), bottom=lambda *k: T)


def hat_qalpaq(s):
    """Ақ қалпақ — белая войлочная шапка с чёрными полями."""
    clear_hat(s)
    W, K = c("#f2eee4"), c("#1e1c1c")

    def side(x, y, w, h):
        if y <= 1:
            return W
        if y == 2:
            return K if (x % 4) else c("#c8a040")
        return T
    s.box(32, 0, 8, 8, 8, side, top=lambda x, y, w, h: W if (x + y) % 6 else c("#e0dccf"), bottom=lambda *k: T)


def hat_kimeshek(s, trim):
    """Кимешек — белый головной убор замужней женщины (закрывает голову и шею)."""
    clear_hat(s)
    W, R = c("#f4f2ec"), c(trim)

    def side(x, y, w, h):
        if y == 2:
            return R
        return W if (x + y) % 7 else c("#e2ded6")

    def front(x, y, w, h):
        if y <= 1:
            return W
        if y == 2:
            return R
        if x in (0, 7) or y == 7:
            return W
        return T
    s.box(32, 0, 8, 8, 8, side, front=front, top=lambda *k: W, bottom=lambda *k: T)


def hat_oramal(s, col):
    clear_hat(s)
    a = c(col)

    def side(x, y, w, h):
        if y <= 2 or (x in (0, w - 1) and y < 6):
            return noisy(a, s.rng, 0.06) if (x + y) % 5 else c("#f0e0a0")
        return T
    s.box(32, 0, 8, 8, 8, side, front=lambda x, y, w, h: noisy(a, s.rng, 0.06) if y <= 1 else T,
          back=lambda x, y, w, h: noisy(a, s.rng, 0.06), top=lambda *k: noisy(a, s.rng, 0.06), bottom=lambda *k: T)


def hat_uki(s, col):
    """Девичья тақия с пучком перьев филина (үкі)."""
    hat_taqiya(s, col, "#e8b83c")
    for (x, y) in ((11, 0), (12, 0), (11, 1)):
        s.set(32 + x - 8 + 8, y, c("#f8f6f0"))


def hat_turban(s):
    clear_hat(s)
    W = c("#f4f0e6")

    def side(x, y, w, h):
        if y <= 3:
            return W if (x + y * 2) % 5 else c("#ddd6c4")
        return T
    s.box(32, 0, 8, 8, 8, side, top=lambda x, y, w, h: W if (x + y) % 4 else c("#e4ddcc"), bottom=lambda *k: T)


def chapan(s, robe, trim, sash=None, open_front=None, pants="#3a2a20", boots="#2a1a12", long=True, skin=None,
           buttons=None, epaulettes=None):
    rng = s.rng
    R, Tr = c(robe), c(trim)
    S = c(sash) if sash else None
    inner = c(open_front) if open_front else None

    def body_front(x, y, w, h):
        if y == 0:
            return Tr
        if S and y in (7, 8):
            return S
        if inner and 3 <= x <= 4 and y < 7:
            return inner
        if x in (2, 5) and y < 11:
            return Tr
        if buttons and x in (3, 4) and y % 2 == 1 and y < 7:
            return c(buttons)
        return noisy(vgrad(R, y, h), rng, 0.04)

    def body_side(x, y, w, h):
        if S and y in (7, 8):
            return S
        if y == 0:
            return Tr
        return noisy(vgrad(R, y, h), rng, 0.04)
    s.box(16, 16, 8, 12, 4, body_side, front=body_front, top=lambda *k: Tr, bottom=lambda *k: R)
    sk = c(skin) if skin else c("#c89870")

    def arm(x, y, w, h):
        if y >= 11:
            return noisy(sk, rng, 0.03)
        if y in (9, 10):
            return Tr
        if epaulettes and y == 0:
            return c(epaulettes)
        return noisy(vgrad(R, y, h), rng, 0.04)
    s.box(40, 16, 4, 12, 4, arm, top=lambda *k: c(epaulettes) if epaulettes else R, bottom=lambda *k: sk)
    P, B = c(pants), c(boots)

    def leg(x, y, w, h):
        if y >= 8:
            return noisy(B, rng, 0.04) if y < 11 else shade(B, 0.7)
        if long and y <= 5:
            return noisy(vgrad(R, y + 6, h + 6), rng, 0.04) if y < 5 else Tr
        return noisy(P, rng, 0.04)
    s.box(0, 16, 4, 12, 4, leg)


def dress(s, top, skirt, trim, apron=None, skin="#e0b090"):
    rng = s.rng
    Tp, Sk, Tr = c(top), c(skirt), c(trim)
    A = c(apron) if apron else None

    def body_front(x, y, w, h):
        if y == 0 or y == 6:
            return Tr
        if A and y > 6 and 2 <= x <= 5:
            return A
        if y > 6:
            return noisy(Sk, rng, 0.04)
        if x in (3, 4) and y % 2:
            return c("#e8b83c")
        return noisy(Tp, rng, 0.04)
    s.box(16, 16, 8, 12, 4, lambda x, y, w, h: Tr if y in (0, 6) else noisy(Tp if y < 6 else Sk, rng, 0.04),
          front=body_front)
    sk = c(skin)

    def arm(x, y, w, h):
        if y >= 11:
            return noisy(sk, rng, 0.03)
        if y == 10:
            return Tr
        return noisy(c("#f2ece0") if y > 3 else Tp, rng, 0.03)
    s.box(40, 16, 4, 12, 4, arm)

    def leg(x, y, w, h):
        if y >= 10:
            return c("#5a1a1a") if y == 10 else c("#3a1010")
        if y == 9:
            return Tr
        return noisy(vgrad(Sk, y, h, 1.0, 0.88), rng, 0.04)
    s.box(0, 16, 4, 12, 4, leg)


# ------------------------------------------------------------------ 20 ауылдасов
SKINS = ["#e2b48c", "#d6a47a", "#c89068", "#b8805a", "#e8c09a"]
HAIRS = ["#1a1412", "#2a1c14", "#3a2618", "#1e1a18"]
ROBES = ["#1e3a6a", "#6a1e22", "#2e5a3a", "#4a2a5a", "#5a3a1e", "#1e1e24", "#7a5a2a", "#2a4a6a"]
TRIMS = ["#e8b83c", "#c0392b", "#f2ece0", "#2bb5a6", "#d89a3a"]


def villager(i):
    rng = random.Random(1000 + i)
    s = Skin(2000 + i)
    female = i % 2 == 1
    old = i % 5 == 4
    skin = rng.choice(SKINS)
    hair = "#d8d4cc" if old else rng.choice(HAIRS)
    if female:
        head(s, c(skin), c(hair), female=True, old=old, eyes=rng.choice(["#2a1c14", "#3a2414", "#1a1a1a"]))
        tops = ["#8e1c26", "#1e3a6a", "#2e5a3a", "#5a2a5a", "#7a5a2a", "#1e5a5a"]
        dress(s, rng.choice(tops), rng.choice(["#f2ece0", "#8e1c26", "#2e3a5a", "#5a3a2a"]), rng.choice(TRIMS),
              apron="#f2ece0" if i % 3 == 0 else None, skin=skin)
        if old or i % 4 == 3:
            hat_kimeshek(s, rng.choice(["#c0392b", "#2e5a3a", "#1e3a6a"]))
        elif i % 4 == 1:
            hat_uki(s, rng.choice(["#8e1c26", "#1e3a6a", "#2e2e2e"]))
        else:
            hat_oramal(s, rng.choice(["#c0392b", "#e8b83c", "#2bb5a6", "#f2ece0"]))
    else:
        beard = "#e8e4dc" if old else (hair if i % 3 == 0 else None)
        moustache = None if beard else (hair if i % 4 != 2 else None)
        head(s, c(skin), c(hair), beard=beard, moustache=moustache, old=old, bald_sides=True)
        chapan(s, rng.choice(ROBES), rng.choice(TRIMS), sash=rng.choice(["#c0392b", "#e8b83c", "#2a1a12", "#2bb5a6"]),
               open_front="#f2ece0" if i % 3 else None, pants=rng.choice(["#3a2a20", "#2a2a3a", "#4a3a2a"]),
               skin=skin, long=i % 2 == 0)
        k = i % 6
        if k in (0, 4):
            hat_borik(s, rng.choice(["#6a4a2e", "#3a2a1e", "#8a6a4a"]), rng.choice(["#8e1c26", "#1e3a6a", "#2e5a3a"]))
        elif k == 2:
            hat_qalpaq(s)
        else:
            hat_taqiya(s, rng.choice(["#1e1e24", "#1e3a6a", "#4a1e1e"]), rng.choice(["#e8b83c", "#f2ece0"]))
    return s


# ------------------------------------------------------------------ великие люди
def abai():
    s = Skin(3001)
    head(s, c("#d4a47a"), c("#1a1412"), beard="#1e1814", moustache="#1e1814", eyes="#1a120c")
    hat_taqiya(s, "#1e1e24", "#4a4a5a")
    chapan(s, "#2a2a30", "#5a4a3a", sash="#4a3a2a", open_front="#f2ece0", pants="#1e1e24", boots="#141010",
           skin="#d4a47a")
    return s


def shoqan():
    s = Skin(3002)
    head(s, c("#d8aa82"), c("#1a1412"), moustache="#1a1412", eyes="#1a120c", bald_sides=True)
    clear_hat(s)
    # мундир офицера с золотыми пуговицами и эполетами
    chapan(s, "#1e2a3a", "#c8a040", sash=None, pants="#1e2a3a", boots="#0e0c0c", long=False,
           skin="#d8aa82", buttons="#e8c050", epaulettes="#e8c050")
    return s


def farabi():
    s = Skin(3003)
    head(s, c("#c89470"), c("#9a968e"), beard="#cfcac0", moustache="#cfcac0", old=True, eyes="#2a1c14")
    hat_turban(s)
    chapan(s, "#1e3a6a", "#e8b83c", sash="#e8b83c", open_front="#f2ece0", pants="#1e2a4a", boots="#3a2a1a",
           skin="#c89470")
    return s


def qorqyt():
    s = Skin(3004)
    head(s, c("#caa07a"), c("#f0eee8"), beard="#f4f2ec", moustache="#f4f2ec", old=True, eyes="#3a5a7a")
    hat_qalpaq(s)
    chapan(s, "#ece6d8", "#c8a040", sash="#c8a040", open_front="#d8d0c0", pants="#d8d0c0", boots="#6a4a2a",
           skin="#caa07a")
    return s


# ------------------------------------------------------------------ балбал, бақсы, дәу, қарақшы
def balbal(awake):
    s = Skin(3010 + awake)
    rng = s.rng
    stone = c("#8c8c88")

    def st(x, y, w, h):
        k = rng.uniform(0.85, 1.08)
        if (x * 7 + y * 3) % 17 == 0:
            k *= 0.75  # трещины
        if (x + y * 5) % 23 == 0:
            return c("#6e8a62")  # мох
        return shade(stone, k)
    for (u, v, w, h, d) in ((0, 0, 8, 8, 8), (16, 16, 8, 12, 4), (40, 16, 4, 12, 4), (0, 16, 4, 12, 4)):
        s.box(u, v, w, h, d, st)
    clear_hat(s)
    carve = c("#55554f")
    f = {(1, 3): carve, (2, 3): carve, (5, 3): carve, (6, 3): carve,
         (3, 4): carve, (4, 4): carve, (3, 5): carve, (4, 5): carve,
         (2, 6): carve, (3, 6): carve, (4, 6): carve, (5, 6): carve, (1, 7): carve, (6, 7): carve}
    if awake:
        f[(1, 4)] = c("#ff8a2a"); f[(2, 4)] = c("#ffd24a"); f[(5, 4)] = c("#ffd24a"); f[(6, 4)] = c("#ff8a2a")
    else:
        f[(1, 4)] = carve; f[(2, 4)] = shade(stone, 0.7); f[(5, 4)] = shade(stone, 0.7); f[(6, 4)] = carve
    V.put_front(s, f)
    # пояс и чаша в руке
    for x in range(8):
        s.set(20 + x, 28, carve)
    for (x, y) in ((44, 27), (45, 27), (46, 27), (47, 27), (45, 28), (46, 28)):
        s.set(x, y, c("#a8a49c"))
    s.save_name = "balbal_awake" if awake else "balbal"
    return s


def baqsy():
    s = Skin(3020)
    a = 175
    skin = c("#b8a8d8", a)
    head(s, skin, c("#2a1e3a", a), beard="#d8d0f0", moustache="#d8d0f0", glow="#f0f0ff", old=True)
    clear_hat(s)
    owl = c("#f6f2ea", 220)

    def side(x, y, w, h):
        if y <= 1:
            return c("#3a2a5a", 200) if (x + y) % 3 else c("#c8a040", 220)
        return T
    s.box(32, 0, 8, 8, 8, side, top=lambda x, y, w, h: owl if (x in (3, 4) and y in (3, 4)) else c("#3a2a5a", 200),
          bottom=lambda *k: T)
    rng = s.rng

    def robe(x, y, w, h):
        base = c("#3a2a5a", 170) if (x + y) % 5 else c("#5a4a8a", 170)
        if y % 4 == 0 and x % 2 == 0:
            return c("#e8b83c", 200)  # бахрома и подвески
        return base
    s.box(16, 16, 8, 12, 4, robe)
    s.box(40, 16, 4, 12, 4, lambda x, y, w, h: skin if y >= 11 else robe(x, y, w, h))

    def leg(x, y, w, h):
        alpha = max(30, int(170 * (1 - y / 13)))
        return (70, 50, 110, alpha)
    s.box(0, 16, 4, 12, 4, leg)
    return s


def dau():
    s = Skin(3030)
    rng = s.rng
    skin = c("#b88a62")
    head(s, skin, c("#141010"), beard="#1a1210", moustache="#1a1210", eyes="#8a1a10", brows=c("#0c0808"))
    clear_hat(s)

    def body(x, y, w, h):
        if y <= 6:
            return noisy(c("#6a4a2a"), rng, 0.15) if (x + y) % 3 else c("#4a3220")  # меховая безрукавка
        if y in (7, 8):
            return c("#3a2412")
        return noisy(c("#5a3a22"), rng, 0.06)
    s.box(16, 16, 8, 12, 4, body)

    def arm(x, y, w, h):
        if y <= 2:
            return noisy(c("#6a4a2a"), rng, 0.15)
        if y in (8, 9):
            return c("#3a2412")
        return noisy(skin, rng, 0.05)
    s.box(40, 16, 4, 12, 4, arm)
    s.box(0, 16, 4, 12, 4, lambda x, y, w, h: noisy(c("#4a3020"), rng, 0.06) if y < 9 else c("#2a1a10"))
    return s


def qaraqshy():
    s = Skin(3040)
    rng = s.rng
    head(s, c("#c08a62"), c("#141010"), eyes="#1a120c")
    # чёрный платок закрывает нижнюю часть лица
    for x in range(8):
        for y in (5, 6, 7):
            s.set(8 + x, 8 + y, c("#1a1818"))
    hat_borik(s, "#2a2018", "#1a1818")
    chapan(s, "#2a2420", "#6a1e1e", sash="#8a1e1e", pants="#1e1a18", boots="#100c0a", long=False, skin="#c08a62")
    return s


def run(out_dir):
    os.makedirs(os.path.join(out_dir, "auyldas"), exist_ok=True)
    for i in range(20):
        villager(i).img.save(os.path.join(out_dir, "auyldas", f"auyldas_{i}.png"))
    for name, fn in (("abai", abai), ("shoqan", shoqan), ("al_farabi", farabi), ("qorqyt_ata", qorqyt),
                     ("baqsy_kolenkesi", baqsy), ("dau", dau), ("qaraqshy", qaraqshy)):
        fn().img.save(os.path.join(out_dir, name + ".png"))
    balbal(False).img.save(os.path.join(out_dir, "balbal.png"))
    balbal(True).img.save(os.path.join(out_dir, "balbal_awake.png"))
