"""v5: new golden mask (Altyn betperde) — sculpted 3D face mask with a tall
red headdress, golden arrows and animal plaques. All surfaces sit outside the
player's hat layer (0.8..15.2 block units), so the mask is never hidden by a skin.
"""
import math
import random

from PIL import Image

G = None  # gen_assets module, set by run()

GL = (255, 236, 150, 255)
GM = (240, 190, 56, 255)
GD = (186, 128, 26, 255)
GK = (112, 70, 14, 255)
GX = (60, 36, 10, 255)
TQ = (46, 184, 170, 255)
TQD = (24, 120, 112, 255)
RD = (150, 26, 24, 255)
RDD = (104, 16, 16, 255)
RDL = (184, 48, 40, 255)


def mix(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3)) + (255,)


def gold_ramp(t):
    """t in 0..1: 0 dark, 1 bright."""
    t = max(0.0, min(1.0, t))
    stops = [(0.0, GX), (0.25, GK), (0.5, GD), (0.75, GM), (1.0, GL)]
    for (t0, c0), (t1, c1) in zip(stops, stops[1:]):
        if t <= t1:
            return mix(c0, c1, (t - t0) / (t1 - t0))
    return GL


class Tex:
    def __init__(self, size=32, seed=1):
        self.img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
        self.px = self.img.load()
        self.n = size
        self.rng = random.Random(seed)

    def set(self, x, y, c):
        if 0 <= x < self.n and 0 <= y < self.n:
            self.px[x, y] = c

    def fill(self, fn):
        for y in range(self.n):
            for x in range(self.n):
                c = fn(x, y)
                if c is not None:
                    self.px[x, y] = c

    def save(self, rel):
        G.os.makedirs(G.os.path.dirname(G.os.path.join(G.A, "textures", rel + ".png")), exist_ok=True)
        self.img.save(G.os.path.join(G.A, "textures", rel + ".png"))


def face_texture():
    """Front view of the mask, 32x32 = 16x16 block units (viewer coordinates)."""
    t = Tex(32, 501)
    rng = t.rng
    # face plate covers x 2..30, y 7..29 (json x 1..15, y 1.6..12.4)
    cx = 15.5

    def height(x, y):
        # pseudo height map of a stylised face -> used for lighting
        dx = (x - cx) / 14.0
        dy = (y - 18.0) / 11.0
        h = 1.0 - 0.55 * dx * dx - 0.35 * dy * dy
        # cheekbones
        for sx in (9.5, 21.5):
            h += 0.18 * math.exp(-((x - sx) ** 2 / 10.0 + (y - 21.5) ** 2 / 6.0))
        # eye sockets
        for sx in (10.0, 21.0):
            h -= 0.22 * math.exp(-((x - sx) ** 2 / 9.0 + (y - 16.0) ** 2 / 3.0))
        return h

    def shade(x, y):
        hx = height(x + 0.5, y) - height(x - 0.5, y)
        hy = height(x, y + 0.5) - height(x, y - 0.5)
        light = 0.62 + height(x, y) * 0.25 - hy * 2.2 - hx * 1.2
        return light

    def pix(x, y):
        if not (2 <= x <= 29 and 7 <= y <= 30):
            return None
        # rounded jaw corners
        if y >= 26 and abs(x - cx) > 13.5 - (y - 26) * 1.5:
            return None
        if y <= 8 and (x <= 2 or x >= 29):
            return None
        light = shade(x, y) + rng.uniform(-0.03, 0.03)
        c = gold_ramp(light)
        return c
    t.fill(pix)
    # almond eyes: dark slits with a bright rim (as on Saka masks)
    for ex in (10, 21):
        for x in range(ex - 4, ex + 4):
            w = 1.6 * (1 - ((x - ex + 0.5) / 4.2) ** 2)
            for y in range(13, 20):
                d = abs(y - 16.2)
                if d <= w:
                    t.set(x, y, GX if d < w - 0.6 else GK)
            # upper lid highlight
            yt = int(round(16.2 - w - 0.6))
            t.set(x, yt, GL)
        t.set(ex - 1, 16, (20, 14, 6, 255))
        t.set(ex, 16, (20, 14, 6, 255))
    # nasolabial lines and lips
    for i in range(4):
        t.set(11 - i // 2, 21 + i, GK)
        t.set(20 + i // 2, 21 + i, GK)
    for x in range(12, 20):
        t.set(x, 25, GX if 13 <= x <= 18 else GK)
        t.set(x, 26, GD if 13 <= x <= 18 else None or GD)
    for x in range(13, 19):
        t.set(x, 24, GM)
    # forehead: small turquoise rhomb (tumar)
    for (dx, dy) in ((0, -1), (-1, 0), (0, 0), (1, 0), (0, 1)):
        t.set(15 + dx, 10 + dy, TQ if (dx, dy) != (0, 0) else TQD)
        t.set(16 + dx, 10 + dy, TQ if (dx, dy) != (0, 0) else TQD)
    # rim around the plate
    for y in range(32):
        for x in range(32):
            if t.px[x, y][3] and any(not (0 <= x + a < 32 and 0 <= y + b < 32) or t.px[x + a, y + b][3] == 0
                                     for a, b in ((1, 0), (-1, 0), (0, 1), (0, -1))):
                t.px[x, y] = GD if y > 14 else GM
    t.save("item/betperde_face")


def metal(rel, seed, base=0.62, spread=0.18, highlight_top=True, pattern=None):
    t = Tex(32, seed)

    def pix(x, y):
        light = base + (0.18 * (1 - y / 31) if highlight_top else 0) + t.rng.uniform(-spread / 3, spread / 3)
        if pattern:
            p = pattern(x, y)
            if p is not None:
                return p if isinstance(p, tuple) else gold_ramp(light + p)
        return gold_ramp(light)
    t.fill(pix)
    t.save(rel)
    return t


def red_felt():
    t = Tex(32, 502)

    def pix(x, y):
        k = t.rng.uniform(-0.06, 0.06)
        c = RD if (x * 7 + y * 3) % 11 else RDD
        return tuple(max(0, min(255, int(ch * (1 + k)))) for ch in c[:3]) + (255,)
    t.fill(pix)
    # rows of small golden plaques (the Issyk headdress is covered with them)
    for row, y0 in enumerate(range(2, 32, 8)):
        for x0 in range(1 + (row % 2) * 4, 32, 8):
            for (dx, dy, c) in ((1, 0, GL), (0, 1, GM), (1, 1, GM), (2, 1, GD), (1, 2, GD)):
                t.set(x0 + dx, y0 + dy, c)
    t.save("item/betperde_red")


def band_texture():
    def pat(x, y):
        if y in (0, 1):
            return 0.35
        if y in (30, 31):
            return -0.35
        if 9 <= y <= 22:
            # turquoise rhombs between golden spirals
            cx = (x // 8) * 8 + 4
            d = abs(x - cx) + abs(y - 15.5)
            if d <= 4:
                return TQ if d <= 3 else TQD
            if d <= 5:
                return 0.25
        if y in (6, 25):
            return -0.25
        return None
    metal("item/betperde_band", 503, pattern=pat)


def plaque_texture():
    """Golden plaque with an argali (ibex) and a snow leopard in animal style."""
    def pat(x, y):
        return None
    t = metal("item/betperde_plaque", 504, base=0.7, pattern=pat)
    argali = [
        "...kkk......",
        "..k...k.....",
        "..k..kk.....",
        "...kkk......",
        "....kkkkkkk.",
        "...kkkkkkkk.",
        "...k.k...kk.",
        "...k.k...k.k",
    ]
    for oy, (ox, flip) in ((4, (10, False)), (18, (10, True))):
        for y, row in enumerate(argali):
            for x, ch in enumerate(row):
                if ch == "k":
                    xx = ox + (11 - x if flip else x)
                    t.set(xx, oy + y, GK)
                    t.set(xx + 1, oy + y + 1, GD) if t.px[min(31, xx + 1), min(31, oy + y + 1)] != GK else None
    t.save("item/betperde_plaque")


def side_texture():
    def pat(x, y):
        # scale armour (golden scales with turquoise dots)
        sy = y % 6
        sx = (x + (y // 6) * 3) % 6
        if sy == 5:
            return -0.3
        if sx == 0:
            return -0.2
        if sy == 2 and sx == 3:
            return TQ
        return 0.1 - sy * 0.04
    metal("item/betperde_side", 505, pattern=pat)


def auto_uv(face, fr, to):
    x0, y0, z0 = fr
    x1, y1, z1 = to
    if face == "north":
        return [16 - x1, 16 - y1, 16 - x0, 16 - y0]
    if face == "south":
        return [x0, 16 - y1, x1, 16 - y0]
    if face == "west":
        return [z0, 16 - y1, z1, 16 - y0]
    if face == "east":
        return [16 - z1, 16 - y1, 16 - z0, 16 - y0]
    if face == "up":
        return [x0, z0, x1, z1]
    return [x0, 16 - z1, x1, 16 - z0]


def clamp_uv(uv):
    # keep inside 0..16 (cap tiers above y=16 get wrapped)
    out = []
    for v in uv:
        while v < 0:
            v += 16
        while v > 16:
            v -= 16
        out.append(v)
    if out[1] > out[3] and uv[3] - uv[1] > 0:
        out[1], out[3] = 0, uv[3] - uv[1]
    if out[0] > out[2] and uv[2] - uv[0] > 0:
        out[0], out[2] = 0, uv[2] - uv[0]
    return out


def el(fr, to, tex, faces=None, front=None, rotation=None):
    f = {}
    for d in faces or ("north", "south", "east", "west", "up", "down"):
        tx = front if (front and d == "north") else tex
        f[d] = {"uv": clamp_uv(auto_uv(d, fr, to)), "texture": "#" + tx}
    e = {"from": fr, "to": to, "faces": f}
    if rotation:
        e["rotation"] = rotation
    return e


def mask_model():
    E = []
    # --- face
    E.append(el([1.0, 0.8, 0.2], [15.0, 12.4, 0.8], "gold", front="face"))
    E.append(el([2.2, 9.0, -0.3], [13.8, 9.8, 0.2], "gold"))                 # brow ridge
    E.append(el([7.1, 4.8, -0.7], [8.9, 9.0, 0.2], "gold"))                  # nose
    E.append(el([6.8, 4.4, -0.5], [9.2, 5.2, 0.2], "gold"))                  # nose wings
    E.append(el([4.6, 3.8, -0.3], [7.2, 4.4, 0.2], "gold"))                  # moustache
    E.append(el([8.8, 3.8, -0.3], [11.4, 4.4, 0.2], "gold"))
    E.append(el([4.0, 3.4, -0.3], [4.8, 4.0, 0.2], "gold"))                  # curled ends
    E.append(el([11.2, 3.4, -0.3], [12.0, 4.0, 0.2], "gold"))
    E.append(el([5.6, 0.2, -0.1], [10.4, 1.8, 0.8], "side"))                 # beard plate
    # --- ear guards and neck guard
    E.append(el([0.2, 1.0, 0.2], [0.8, 12.4, 12.0], "side"))
    E.append(el([15.2, 1.0, 0.2], [15.8, 12.4, 12.0], "side"))
    E.append(el([0.8, 3.0, 15.2], [15.2, 12.4, 15.8], "red"))
    E.append(el([0.2, 2.0, 12.0], [0.8, 12.4, 15.8], "red"))
    E.append(el([15.2, 2.0, 12.0], [15.8, 12.4, 15.8], "red"))
    # --- diadem
    E.append(el([0.0, 12.4, 0.0], [16.0, 15.2, 16.0], "band"))
    # --- tall headdress
    tiers = [(0.8, 15.2, 18.8), (2.0, 18.8, 22.0), (3.2, 22.0, 24.8), (4.6, 24.8, 27.2), (6.0, 27.2, 29.2)]
    for inset, y0, y1 in tiers:
        E.append(el([inset, y0, inset], [16 - inset, y1, 16 - inset], "red"))
    E.append(el([7.0, 29.2, 7.0], [9.0, 30.6, 9.0], "gold"))                  # finial
    E.append(el([6.2, 30.6, 7.4], [9.8, 31.6, 8.6], "gold"))                  # little bird
    E.append(el([7.4, 31.6, 7.6], [8.6, 32.0, 8.4], "gold"))
    # golden arrows on the front of the headdress
    for x0 in (4.2, 10.8):
        E.append(el([x0, 15.2, 0.2], [x0 + 1.0, 26.0, 0.8], "gold"))
        E.append(el([x0 - 0.5, 26.0, 0.2], [x0 + 1.5, 27.2, 0.8], "gold"))
        E.append(el([x0 + 0.1, 27.2, 0.2], [x0 + 0.9, 28.4, 0.8], "gold"))
    # central plaque with argali
    E.append(el([6.4, 15.6, -0.1], [9.6, 23.8, 0.5], "gold", front="plaque"))
    # snow leopard plaques on both sides
    E.append(el([0.2, 15.8, 4.0], [0.8, 18.4, 12.0], "gold", faces=("west", "east", "up", "down", "north", "south")))
    E.append(el([15.2, 15.8, 4.0], [15.8, 18.4, 12.0], "gold"))
    # golden wings rising from the sides
    E.append(el([0.0, 19.0, 6.0], [1.4, 26.0, 9.0], "gold", rotation={"angle": 22.5, "axis": "z", "origin": [1.4, 19.0, 7.5]}))
    E.append(el([14.6, 19.0, 6.0], [16.0, 26.0, 9.0], "gold", rotation={"angle": -22.5, "axis": "z", "origin": [14.6, 19.0, 7.5]}))
    return E


def run(g):
    global G
    G = g
    face_texture()
    metal("item/betperde_gold", 506)
    red_felt()
    band_texture()
    plaque_texture()
    side_texture()
    G.write_json(f"{G.A}/models/item/altyn_betperde.json", {
        "textures": {"face": "ezhelgi:item/betperde_face", "gold": "ezhelgi:item/betperde_gold",
                     "red": "ezhelgi:item/betperde_red", "band": "ezhelgi:item/betperde_band",
                     "plaque": "ezhelgi:item/betperde_plaque", "side": "ezhelgi:item/betperde_side",
                     "particle": "ezhelgi:item/betperde_gold"},
        "elements": mask_model(),
        "display": {
            "gui": {"rotation": [10, 200, 0], "translation": [0, -4.5, 0], "scale": [0.4, 0.4, 0.4]},
            "ground": {"rotation": [0, 0, 0], "translation": [0, 2, 0], "scale": [0.3, 0.3, 0.3]},
            "fixed": {"rotation": [0, 180, 0], "translation": [0, -4, 0], "scale": [0.42, 0.42, 0.42]},
            "thirdperson_righthand": {"rotation": [0, 0, 0], "translation": [0, 1, 1], "scale": [0.32, 0.32, 0.32]},
            "firstperson_righthand": {"rotation": [0, 135, 0], "translation": [0, 2, 0], "scale": [0.32, 0.32, 0.32]},
            "head": {"rotation": [0, 0, 0], "translation": [0, 0, 0], "scale": [1, 1, 1]}}})
