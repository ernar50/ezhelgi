"""v7: иконки предметов (16x16) — пластинка, чешуя айдахара, рог архара, роговой лук,
лисья шкура, мясо дичи, трафарет ою, шаблоны отделки, яйца призыва.
"""
import math
from PIL import Image

from v7_armor import shade, GOLD, STEEL, TURQ, RED, LEATHER, FOX, WHITE, GREEN, CRIMSON, BRONZE, DLEATHER, BLUE


def hexa(h):
    h = h.lstrip("#")
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), 255)


def from_rows(rows, pal):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    for y, row in enumerate(rows):
        for x, ch in enumerate(row[:16]):
            if ch != "." and ch in pal:
                c = pal[ch]
                px[x, y] = hexa(c) if isinstance(c, str) else c
    return img


def outline(img, factor=0.45, color=None):
    out = img.copy()
    px, op = img.load(), out.load()
    w, h = img.size
    for y in range(h):
        for x in range(w):
            if px[x, y][3]:
                continue
            for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                nx, ny = x + dx, y + dy
                if 0 <= nx < w and 0 <= ny < h and px[nx, ny][3]:
                    c = px[nx, ny]
                    op[x, y] = color or (int(c[0] * factor * 0.6), int(c[1] * factor * 0.6), int(c[2] * factor * 0.6), 255)
                    break
    return out


ART = {
    "aidahar_qabyrshagy": ([
        "................",
        "......abba......",
        ".....abbbbc.....",
        "....abbhbbcc....",
        "...abbhhbbbcc...",
        "...abhhbbbbcc...",
        "..abbhbbbbbccd..",
        "..abbbbbbbbccd..",
        "..abbbbbbbcccd..",
        "...abbbbbcccd...",
        "...aabbbbccdd...",
        "....aabbccdd....",
        ".....aaccdd.....",
        "......accd......",
        ".......ad.......",
        "................"],
        {"a": "#7be0a0", "b": "#2fa86a", "h": "#c8ffd8", "c": "#1d7048", "d": "#0f4028"}),
    "arqar_muiizi": ([
        "................",
        "......aaaab.....",
        "....aabbbbbc....",
        "...abbcccccbc...",
        "..abbc....cbc...",
        "..abc.....cbc...",
        ".abc....aabbc...",
        ".abc...abbbc....",
        ".abc...abcc.....",
        ".abbc...cc......",
        "..abbc..........",
        "...abbbc........",
        "....aabbcc......",
        "......abbbcc....",
        "........abbd....",
        "..........d....."],
        {"a": "#f4e6c8", "b": "#d8c094", "c": "#a88a5c", "d": "#6e5636"}),
    "tulki_terisi": ([
        "................",
        "...a........a...",
        "...aa......aa...",
        "....abbbbbba....",
        "....bbcbbcbb....",
        "...abbbbbbbba...",
        "..aabbbbbbbbaa..",
        ".aa.bbbbbbbb.aa.",
        "....bbbbbbbb....",
        "....abbbbbba....",
        "...aa.bbbb.aa...",
        "......bbbb......",
        ".......bb.......",
        ".......bb.......",
        "......wbbw......",
        ".......ww......."],
        {"a": "#c86a20", "b": "#e98c3a", "c": "#3a2416", "w": "#fff4e8"}),
    "an_eti": ([
        "................",
        "................",
        "......aaaa......",
        "....aabbbbaa....",
        "...abbbbccbba...",
        "..abbbcccbbbba..",
        "..abbbbbbbbbba..",
        ".abbbwwbbbbbbba.",
        ".abbwwwbbbcbbba.",
        ".abbbwbbbcccbba.",
        "..abbbbbbbbbba..",
        "..aabbbbbbbbaa..",
        "....aabbbbaa....",
        "......aaaa......",
        "................",
        "................"],
        {"a": "#7a1e22", "b": "#b8343a", "c": "#d8606a", "w": "#f2e2d8"}),
    "pisken_an_eti": ([
        "................",
        "................",
        "......aaaa......",
        "....aabbbbaa....",
        "...abbbbccbba...",
        "..abbbcccbbbba..",
        "..abbbbbbbbbba..",
        ".abbbwwbbbbbbba.",
        ".abbwwwbbbcbbba.",
        ".abbbwbbbcccbba.",
        "..abbbbbbbbbba..",
        "..aabbbbbbbbaa..",
        "....aabbbbaa....",
        "......aaaa......",
        "................",
        "................"],
        {"a": "#4a2410", "b": "#8a4a22", "c": "#b8743c", "w": "#e8c890"}),
    "oiu_banner_pattern": ([
        "................",
        "..aaaaaaaaaaaa..",
        "..abbbbbbbbbba..",
        "..abggbbbbggba..",
        "..abgbggggbgba..",
        "..abbgbbbbgbba..",
        "..abbbgbbgbbba..",
        "..abbbbggbbbba..",
        "..abbbgbbgbbba..",
        "..abbgbbbbgbba..",
        "..abgbggggbgba..",
        "..abggbbbbggba..",
        "..abbbbbbbbbba..",
        "..aaaaaaaaaaaa..",
        "................",
        "................"],
        {"a": "#b89a6a", "b": "#f0e2c0", "g": "#c0392b"}),
}


def disc():
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    for y in range(16):
        for x in range(16):
            d = math.hypot(x - 7.5, y - 7.5)
            if d <= 7.2:
                if d < 1.0:
                    px[x, y] = (24, 20, 18, 255)
                elif d < 2.6:
                    px[x, y] = shade(GOLD, 1 if x + y < 15 else 3)
                elif d < 3.6:
                    px[x, y] = shade(TURQ, 2 if (x + y) % 2 else 1)
                else:
                    groove = int(d * 2) % 2
                    shine = (x - y) in (-5, -4, 4, 5) and x + y < 15
                    px[x, y] = (96, 96, 104, 255) if shine else ((40, 40, 46, 255) if groove else (30, 30, 34, 255))
    return outline(img, color=(16, 16, 18, 255))


def horn_bow(pull):
    """Роговой лук: рога архара + сухожилия, кремовый с тёмными накладками."""
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    arc = [(1, 13), (2, 12), (2, 11), (3, 10), (3, 9), (4, 8), (4, 7), (5, 6), (6, 5), (7, 4), (8, 4), (9, 3), (10, 3),
           (11, 2), (12, 2), (13, 1)]
    for i, (x, y) in enumerate(arc):
        mid = 4 <= i <= 11
        px[x, y] = shade(BRONZE, 4) if mid else hexa("#f2e2c0")
        px[x + 1, y + 1] = shade(DLEATHER, 2) if mid else hexa("#c8a878")
    # загнутые концы (рекурв)
    px[0, 14] = hexa("#f2e2c0"); px[1, 14] = hexa("#c8a878")
    px[14, 0] = hexa("#f2e2c0"); px[14, 1] = hexa("#c8a878")
    px[8, 5] = shade(GOLD, 1); px[7, 6] = shade(GOLD, 2)
    ox = [0, 2, 3, 4][pull]
    mx, my = 7 + ox, 7 + ox

    def line(x0, y0, x1, y1, c):
        n = max(abs(x1 - x0), abs(y1 - y0))
        for k in range(n + 1):
            t = k / n if n else 0
            xx, yy = round(x0 + (x1 - x0) * t), round(y0 + (y1 - y0) * t)
            if 0 <= xx < 16 and 0 <= yy < 16 and px[xx, yy][3] == 0:
                px[xx, yy] = c
    line(1, 14, mx, my, (232, 224, 204, 255))
    line(mx, my, 14, 1, (232, 224, 204, 255))
    if pull:
        line(mx - 1, my - 1, mx - 7 + ox, my - 7 + ox, (150, 150, 150, 255))
        px[max(0, mx - 8 + ox), max(0, my - 8 + ox)] = shade(STEEL, 1)
    return outline(img, color=(32, 22, 12, 255))


TEMPLATE_COLORS = {
    "qoshqar_muiz": (TURQ, GOLD),
    "tuie_taban": (BRONZE, RED),
    "qus_qanaty": (BLUE, GOLD),
    "synar_muiz": (CRIMSON, GOLD),
    "tumarsha": ([(206, 170, 236), (170, 124, 214), (130, 84, 180), (96, 58, 140), (66, 38, 100), (40, 22, 64)], GOLD),
}

TEMPLATE_MOTIF = {
    "qoshqar_muiz": [".11..11.", "1..11..1", "1.1..1.1", ".1.11.1.", "...11...", "...11..."],
    "tuie_taban": ["..1..1..", ".11.11..", "..1..1..", "...11...", "..1111..", "...11..."],
    "qus_qanaty": ["1......1", "11....11", "111..111", ".111111.", "..1111..", "...11..."],
    "synar_muiz": ["..111...", ".1...1..", ".1.11.1.", ".1..1.1.", "..11.1..", "....1..."],
    "tumarsha": ["11111111", ".111111.", "..1111..", "...11...", "...11...", "..1..1.."],
}


def template(pattern):
    """Шаблон отделки: наклонная каменно-металлическая табличка с цветным ою."""
    base, motif_pal = TEMPLATE_COLORS[pattern]
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    # табличка-ромбоид
    for y in range(1, 15):
        for x in range(2, 14):
            sx = x - (14 - y) // 4
            if 1 <= sx <= 11 or (y in (1, 14) and 2 <= sx <= 10):
                i = 2
                if y <= 2 or sx <= 1:
                    i = 1
                if y >= 13 or sx >= 11:
                    i = 4
                px[x, y] = shade(base, i)
    motif = TEMPLATE_MOTIF[pattern]
    for my, row in enumerate(motif):
        for mx, ch in enumerate(row):
            if ch == "1":
                x, y = 4 + mx - (14 - (4 + my)) // 4 + 1, 4 + my + 1
                if px[x, y][3]:
                    px[x, y] = shade(motif_pal, 1 if (mx + my) % 3 else 0)
    return outline(img)


def egg(base, spots, seed=0):
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = img.load()
    for y in range(16):
        for x in range(16):
            dx, dy = (x - 7.5) / 5.2, (y - 8.8) / 6.6
            if dy < 0:
                dx *= 1.15
            if dx * dx + dy * dy <= 1:
                spot = ((x * 7 + y * 13 + seed) % 11) < 3
                hl = dx < -0.3 and dy < -0.2
                c = hexa(spots if spot else base)
                if hl:
                    c = tuple(min(255, int(v * 1.2)) for v in c[:3]) + (255,)
                px[x, y] = c
    return outline(img, color=(28, 28, 28, 255))


EGGS = {
    "arqar": ("#a07850", "#f0e6d0"), "qaraquiryq": ("#d8b080", "#ffffff"), "tau_teke": ("#6a5a48", "#2a2018"),
    "ayu": ("#6a4228", "#3a2214"), "manul": ("#b8a890", "#3a3a3a"), "qarsaq": ("#c89060", "#f4e8d8"),
    "itbalyq": ("#8a9298", "#40464c"), "qosayaq": ("#e0c8a0", "#2a2018"), "przewalski": ("#c89a5a", "#2a1a10"),
    "qaraqus": ("#3a2e28", "#d8c8a8"), "aidahar": ("#1f6a44", "#d8402a"), "mystan_kempir": ("#3a2a40", "#e8782a"),
    "aqsaqal": ("#f2ece0", "#2e5e9a"), "kuishi": ("#7a2a20", "#e8c050"), "keruenshi": ("#5a3a7a", "#e8c050"),
}


def all_items():
    out = {}
    for name, (rows, pal) in ART.items():
        out[name] = outline(from_rows(rows, pal))
    out["music_disc_adai"] = disc()
    out["muiiz_sadaq"] = horn_bow(0)
    for i in range(3):
        out[f"muiiz_sadaq_pulling_{i}"] = horn_bow(i + 1)
    for p in TEMPLATE_COLORS:
        out[f"{p}_armor_trim_smithing_template"] = template(p)
    for n, (b, s) in EGGS.items():
        out[f"{n}_spawn_egg"] = egg(b, s, len(n))
    return out
