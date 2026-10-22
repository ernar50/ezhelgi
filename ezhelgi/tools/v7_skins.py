"""v7 skins: аксакал, кюйши (3 облика), керуенші — купцы Шёлкового пути (2 облика),
и Мыстан кемпір (обычная и разгневанная) для модели FolkModel.
"""
import os

import v5_skins as V
import v6_skins as S6
from v5_skins import Skin, c, shade, noisy, vgrad


def aqsaqal():
    s = Skin(7001)
    S6.head(s, c("#c8946c"), c("#e8e6e0"), beard="#f4f2ee", moustache="#f0eee8", old=True, eyes="#2a1c14")
    S6.hat_borik(s, "#e8e2d4", "#1e3a6a")
    S6.chapan(s, "#1e3a6a", "#e8b83c", sash="#e8b83c", open_front="#f2ece0", pants="#2a2a3a", boots="#2a1a12",
              skin="#c8946c")
    return s


def kuishi(i):
    s = Skin(7100 + i)
    skins = ["#d6a47a", "#e2b48c", "#c89068"]
    robes = ["#6a1e22", "#1e5a5a", "#4a2a5a"]
    trims = ["#e8b83c", "#f2ece0", "#e8b83c"]
    if i == 1:
        S6.head(s, c(skins[i]), c("#1a1412"), female=True, eyes="#2a1c14")
        S6.dress(s, robes[i], "#f2ece0", trims[i], skin=skins[i])
        S6.hat_uki(s, "#8e1c26")
    else:
        S6.head(s, c(skins[i]), c("#1a1412"), moustache="#1a1412" if i == 0 else None, bald_sides=True)
        S6.chapan(s, robes[i], trims[i], sash="#e8b83c", open_front="#f2ece0", skin=skins[i], long=i == 2)
        if i == 0:
            S6.hat_taqiya(s, "#1e1e24", "#e8b83c")
        else:
            S6.hat_qalpaq(s)
    return s


def keruenshi(i):
    s = Skin(7200 + i)
    skin = ["#b8805a", "#a87048"][i]
    S6.head(s, c(skin), c("#1a1412"), beard="#1e1814" if i == 0 else None, moustache="#1e1814", eyes="#1a120c",
            bald_sides=True)
    S6.hat_turban(s)
    # полосатый шёлковый халат купца
    robe = ["#5a3a7a", "#7a2a2a"][i]
    S6.chapan(s, robe, "#e8b83c", sash="#e8b83c", open_front="#f2ece0", pants="#3a2a20", boots="#5a3a1e", skin=skin)
    px = s.px
    for y in range(20, 32):
        for x in range(20, 28):
            if x in (21, 26) and px[x, y][3]:
                px[x, y] = c("#e8b83c")
    return s


def mystan(angry):
    """Мыстан кемпір: седая ведьма в лисьем воротнике, с рыжими прядями. Глаза — янтарь лисы."""
    s = Skin(7301 if angry else 7300)
    rng = s.rng
    skin = c("#b08a60")
    hair = c("#c8c0b4")
    V.face_base(s, skin, hair, fringe_rows=1)
    eye = c("#ff8a1a") if angry else c("#e8b040")
    f = {(1, 3): c("#e8e0d4"), (2, 3): c("#e8e0d4"), (5, 3): c("#e8e0d4"), (6, 3): c("#e8e0d4"),
         (1, 4): c("#1a1208"), (2, 4): eye, (5, 4): eye, (6, 4): c("#1a1208"),
         (1, 5): shade(skin, 0.8), (6, 5): shade(skin, 0.8),
         (2, 6): c("#3a2010"), (3, 6): c("#3a2010"), (4, 6): c("#f0e8c8"), (5, 6): c("#3a2010"),
         (1, 7): shade(skin, 0.85), (6, 7): shade(skin, 0.85)}
    if angry:
        f[(2, 3)] = c("#5a3a20"); f[(5, 3)] = c("#5a3a20")
    V.put_front(s, f)
    fox = c("#d8782a")
    fox_l = c("#f2a050")
    white = c("#f4ece0")

    def hat(x, y, w, h):
        if y <= 1:
            return noisy(fox, rng, 0.08) if (x + y) % 3 else fox_l
        return (0, 0, 0, 0)
    s.box(32, 0, 8, 8, 8, hat, top=lambda *a: noisy(fox, rng, 0.08), bottom=lambda *a: (0, 0, 0, 0),
          front=lambda x, y, w, h: (fox_l if x in (0, 7) else fox) if y <= 1 else (0, 0, 0, 0))
    robe = c("#2e3a2a")

    def body_front(x, y, w, h):
        if y <= 1:
            return noisy(fox if (x + y) % 3 else white, rng, 0.06)  # лисий воротник
        if y == 2 and x in (3, 4):
            return c("#e8b040")  # янтарный оберег
        if 7 <= y <= 8:
            return c("#5a2a14")
        if (x * 3 + y) % 11 == 0:
            return c("#6a5030")
        return noisy(vgrad(robe, y, h), rng, 0.07)
    s.box(16, 16, 8, 12, 4, lambda x, y, w, h: noisy(fox, rng, 0.06) if y <= 1 else noisy(vgrad(robe, y, h), rng, 0.07),
          front=body_front)

    def arm(x, y, w, h):
        if y >= 10:
            return noisy(skin, rng, 0.05) if y == 10 else c("#2a2418")
        if y <= 1:
            return noisy(fox, rng, 0.06)
        return noisy(vgrad(robe, y, h), rng, 0.07)
    s.box(40, 16, 4, 12, 4, arm)

    def leg(x, y, w, h):
        if y >= 10:
            return c("#2a1a10")
        return noisy(vgrad(shade(robe, 0.85), y, h), rng, 0.07)
    s.box(0, 16, 4, 12, 4, leg)
    # седые волосы с рыжими (лисьими) прядями
    V.hair_strip(s, hair, 18, rng, wild=True)
    for y in range(33, 51):
        for x in (1, 4, 7):
            if s.px[x, y][3] and (y + x) % 3:
                s.px[x, y] = noisy(fox, rng, 0.08)
    V.nose(s, shade(skin, 0.95))
    V.claws(s, c("#3a2a1a"))
    return s


def run(out_dir):
    aqsaqal().img.save(os.path.join(out_dir, "aqsaqal.png"))
    for i in range(3):
        kuishi(i).img.save(os.path.join(out_dir, f"kuishi_{i}.png"))
    for i in range(2):
        keruenshi(i).img.save(os.path.join(out_dir, f"keruenshi_{i}.png"))
    mystan(False).img.save(os.path.join(out_dir, "mystan_kempir.png"))
    mystan(True).img.save(os.path.join(out_dir, "mystan_kempir_angry.png"))
