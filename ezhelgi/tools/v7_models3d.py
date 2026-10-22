"""v7: объёмные модели предметов — қалқан (круглый щит), щит из чешуи айдахара,
домбыра и қобыз. Возвращает JSON-модели и текстуры.
"""
import math
import random
from PIL import Image, ImageDraw

from v7_armor import shade, GOLD, STEEL, LEATHER, DLEATHER, GREEN, RED, BRONZE

# ------------------------------------------------------------------ щиты
SHIELD_DISPLAY = {
    "thirdperson_righthand": {"rotation": [0, 90, 0], "translation": [10, 6, -4], "scale": [1, 1, 1]},
    "thirdperson_lefthand": {"rotation": [0, 90, 0], "translation": [10, 6, 12], "scale": [1, 1, 1]},
    "firstperson_righthand": {"rotation": [0, 180, 5], "translation": [-10, 1.75, -10], "scale": [1.25, 1.25, 1.25]},
    "firstperson_lefthand": {"rotation": [0, 180, 5], "translation": [10, 0, -10], "scale": [1.25, 1.25, 1.25]},
    "gui": {"rotation": [15, -25, -5], "translation": [2, 3, 0], "scale": [0.65, 0.65, 0.65]},
    "fixed": {"rotation": [0, 180, 0], "translation": [-4.5, 4.5, -5], "scale": [0.55, 0.55, 0.55]},
    "ground": {"rotation": [0, 0, 0], "translation": [2, 4, 2], "scale": [0.25, 0.25, 0.25]},
}
SHIELD_BLOCKING = dict(SHIELD_DISPLAY, **{
    "thirdperson_righthand": {"rotation": [45, 155, 0], "translation": [-3.49, 11, -2], "scale": [1, 1, 1]},
    "thirdperson_lefthand": {"rotation": [45, 155, 0], "translation": [11.51, 7, 2.5], "scale": [1, 1, 1]},
    "firstperson_righthand": {"rotation": [0, 180, -5], "translation": [-15, 3.25, -11], "scale": [1.25, 1.25, 1.25]},
    "firstperson_lefthand": {"rotation": [0, 180, -5], "translation": [5, 5, -11], "scale": [1.25, 1.25, 1.25]},
})


def _face(tex, uv):
    return {"uv": [round(v, 3) for v in uv], "texture": tex}


def shield_elements(boss=True):
    """Круглый щит диаметром 14 в плоскости XY, лицевая сторона смотрит на +Z (юг)."""
    els = []
    disks = [(3, 1, 13, 15), (1, 3, 15, 13), (2, 2, 14, 14)]
    for (x0, y0, x1, y1) in disks:
        els.append({"from": [x0, y0, 9], "to": [x1, y1, 10], "faces": {
            "south": _face("#front", [x0, 16 - y1, x1, 16 - y0]),
            "north": _face("#back", [x1, 16 - y1, x0, 16 - y0]),
            "east": _face("#back", [0, 0, 1, 16]), "west": _face("#back", [0, 0, 1, 16]),
            "up": _face("#back", [0, 0, 16, 1]), "down": _face("#back", [0, 0, 16, 1])}})
    # умбон и навершие умбона
    if boss:
        els.append({"from": [6, 6, 10], "to": [10, 10, 11.25], "faces": {
        f: _face("#back", [12, 12, 16, 16] if f == "south" else [12, 12, 16, 13.25]) for f in ("south", "east", "west", "up", "down")}})
        els.append({"from": [7, 7, 11.25], "to": [9, 9, 12], "faces": {
        f: _face("#back", [12, 12, 14, 14]) for f in ("south", "east", "west", "up", "down")}})
    # рукоять
    els.append({"from": [7, 5, 4], "to": [9, 11, 9], "faces": {
        f: _face("#back", [8, 12, 10, 16]) for f in ("north", "east", "west", "up", "down")}})
    return els


def shield_model(front, back, blocking=False, boss=True):
    return {"gui_light": "front", "textures": {"particle": back, "front": front, "back": back},
            "display": SHIELD_BLOCKING if blocking else SHIELD_DISPLAY, "elements": shield_elements(boss)}


def qalqan_front():
    """Лицевая сторона қалқана 64x64: кожа и дерево, стальной обод, золотой ою-круг, заклёпки."""
    S = 64
    img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    px = img.load()
    rng = random.Random(11)
    c = (S - 1) / 2
    for y in range(S):
        for x in range(S):
            r = math.hypot(x - c, y - c) / (S / 2)
            a = math.atan2(y - c, x - c)
            if r > 1.0:
                continue
            if r > 0.88:
                i = 1 if (x + y) < S else 3
                col = shade(STEEL, i)
            elif r > 0.80:
                col = shade(STEEL, 4)
            elif 0.56 < r < 0.68:
                # золотой пояс с завитками қошқар мүйіз
                k = (a / (2 * math.pi) * 24) % 1
                band = 0.62 + 0.035 * math.sin(k * 2 * math.pi * 2)
                col = shade(GOLD, 1 if abs(r - band) < 0.02 else 2 if r < 0.62 else 3)
            else:
                v = rng.random()
                col = shade(RED, 3 if v < 0.6 else 2) if r > 0.30 else shade(LEATHER, 2 if v < 0.6 else 3)
                # лучи от центра
                if r > 0.30 and int((a / (2 * math.pi)) * 16) % 2 == 0 and r < 0.56:
                    col = shade(RED, 2)
            px[x, y] = col
    d = ImageDraw.Draw(img)
    for k in range(12):  # заклёпки
        a = k * math.pi / 6
        x, y = c + math.cos(a) * S * 0.42, c + math.sin(a) * S * 0.42
        d.ellipse([x - 1.5, y - 1.5, x + 1.5, y + 1.5], fill=shade(STEEL, 0))
    return img


def aidahar_front():
    S = 64
    img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    px = img.load()
    c = (S - 1) / 2
    for y in range(S):
        for x in range(S):
            r = math.hypot(x - c, y - c) / (S / 2)
            if r > 1.0:
                continue
            if r > 0.88:
                col = shade(GOLD, 1 if (x + y) < S else 3)
            else:
                row = y // 5
                ox = (row % 2) * 3
                cx = (x + ox) % 6
                cy = y % 5
                i = 2 if cx < 3 else 3
                if cy == 0 and cx in (1, 2):
                    i = 0
                if cy == 4 or cx == 5:
                    i = 5
                col = shade(GREEN, i)
            px[x, y] = col
    d = ImageDraw.Draw(img)
    # глаз дракона
    d.ellipse([c - 9, c - 6, c + 9, c + 6], fill=shade(GOLD, 1))
    d.ellipse([c - 2.5, c - 6, c + 2.5, c + 6], fill=(30, 10, 6, 255))
    d.ellipse([c - 1, c - 4, c + 1, c - 1], fill=(255, 120, 60, 255))
    return img


def shield_back(pal_wood=LEATHER):
    img = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    px = img.load()
    for y in range(64):
        for x in range(64):
            px[x, y] = shade(DLEATHER, 2 if (x // 4 + y // 16) % 2 else 3)
    for y in range(48, 64):  # умбон/рукоять (uv 12..16 и 8..10)
        for x in range(48, 64):
            px[x, y] = shade(STEEL, 1 if (x + y) % 5 else 0) if (x - 48) + (y - 48) < 14 else shade(STEEL, 3)
        for x in range(32, 40):
            px[x, y] = shade(LEATHER, 1 if x % 3 else 3)
    return img


# ------------------------------------------------------------------ домбыра и қобыз
INSTR_DISPLAY = {
    "thirdperson_righthand": {"rotation": [0, 90, -35], "translation": [0, 3, 1], "scale": [1.1, 1.1, 1.1]},
    "thirdperson_lefthand": {"rotation": [0, -90, 35], "translation": [0, 3, 1], "scale": [1.1, 1.1, 1.1]},
    "firstperson_righthand": {"rotation": [0, -90, 35], "translation": [1.13, 3.2, 1.13], "scale": [0.8, 0.8, 0.8]},
    "firstperson_lefthand": {"rotation": [0, 90, -35], "translation": [1.13, 3.2, 1.13], "scale": [0.8, 0.8, 0.8]},
    "gui": {"rotation": [0, 0, 0], "translation": [0, 0, 0], "scale": [1, 1, 1]},
    "ground": {"rotation": [0, 0, 0], "translation": [0, 2, 0], "scale": [0.5, 0.5, 0.5]},
    "fixed": {"rotation": [0, 180, 0], "translation": [0, 0, 0], "scale": [1, 1, 1]},
}

ROT = {"angle": -45, "axis": "z", "origin": [8, 8, 8.5]}


def box(frm, to, faces, rot=True):
    e = {"from": [round(v, 3) for v in frm], "to": [round(v, 3) for v in to], "faces": faces}
    if rot:
        e["rotation"] = dict(ROT)
    return e


def all_faces(tex, uv):
    return {f: _face(tex, uv) for f in ("north", "south", "east", "west", "up", "down")}


def dombyra_model():
    t = "#0"
    els = []
    # корпус-қазан: грушевидный, из трёх ступеней; дека спереди (+Z)
    for (x0, y0, x1, y1, z0) in ((5, -2, 11, 3, 7), (4.5, -1, 11.5, 2, 7.25), (6, 3, 10, 5, 7.25), (7, 5, 9, 6.5, 7.5)):
        f = all_faces(t, [8, 0, 16, 8])
        f["south"] = _face(t, [x0 - 4, 7 - y1, x1 - 4, 7 - y0])  # дека: регион [0..8, 0..9]
        f["north"] = _face(t, [8, 0, 16, 8])
        els.append(box([x0, y0, z0], [x1, y1, 10], f))
    # подставка-тиек
    els.append(box([6.5, 0.2, 10], [9.5, 0.7, 10.6], all_faces(t, [14, 12, 16, 13])))
    # гриф
    f = all_faces(t, [11, 0, 12, 12])
    f["south"] = _face(t, [9, 0, 10.5, 12])
    els.append(box([7.25, 6.5, 8.25], [8.75, 18.5, 9.75], f))
    # голова и колки
    els.append(box([7, 18.5, 8], [9, 21.5, 9.75], all_faces(t, [12.5, 0, 14.5, 4])))
    els.append(box([5.75, 19.25, 8.5], [7, 19.75, 9.25], all_faces(t, [15, 0, 16, 1])))
    els.append(box([9, 20.25, 8.5], [10.25, 20.75, 9.25], all_faces(t, [15, 0, 16, 1])))
    return {"gui_light": "front", "textures": {"0": "ezhelgi:item/dombyra_3d", "particle": "ezhelgi:item/dombyra_3d"},
            "display": INSTR_DISPLAY, "elements": els}


def dombyra_texture():
    S = 64
    img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    px = img.load()
    rng = random.Random(3)
    # дека (u 0..8, v 0..9 → 32x36 px): светлая ель с волокнами, ою-инкрустация, голосники
    for y in range(36):
        for x in range(32):
            g = math.sin(x * 0.9 + rng.random() * 0.4)
            px[x, y] = (232, 196, 132, 255) if g > 0.2 else (218, 180, 116, 255)
    d = ImageDraw.Draw(img)
    for (cx, cy) in ((10, 14), (22, 14), (16, 20)):  # голосники
        d.ellipse([cx - 1.2, cy - 1.2, cx + 1.2, cy + 1.2], fill=(52, 30, 14, 255))
    # ою вокруг голосников (костяная инкрустация)
    for k in range(16):
        a = k * math.pi / 8
        x, y = 16 + math.cos(a) * 7, 17 + math.sin(a) * 6
        d.point([(x, y)], fill=(250, 244, 226, 255))
    d.line([(15, 0), (15, 36)], fill=(240, 236, 220, 255))   # две струны
    d.line([(17, 0), (17, 36)], fill=(240, 236, 220, 255))
    # корпус сзади/сбоку (u 8..16, v 0..8): клёпка из тёмного ореха с полосами
    for y in range(32):
        for x in range(32, 64):
            px[x, y] = (110, 62, 30, 255) if (x // 3) % 2 else (86, 46, 22, 255)
    # гриф спереди (u 9..10.5, v 0..12): тёмный, лады-перевязки и струны
    for y in range(48):
        for x in range(36, 42):
            c = (70, 40, 20, 255)
            if y % 4 == 0:
                c = (196, 184, 160, 255)
            if x in (38, 40):
                c = (240, 236, 220, 255)
            px[x, y] = c
    for y in range(48):  # бока грифа (u 11..12)
        for x in range(44, 48):
            px[x, y] = (86, 50, 24, 255)
    for y in range(16):  # голова (u 12.5..14.5, v 0..4)
        for x in range(50, 58):
            px[x, y] = (92, 54, 26, 255) if (x + y) % 5 else shade(GOLD, 2)
    for y in range(4):  # колки
        for x in range(60, 64):
            px[x, y] = (240, 230, 210, 255)
    for y in range(48, 52):  # тиек
        for x in range(56, 64):
            px[x, y] = (240, 230, 210, 255)
    return img


def qobyz_model():
    t = "#0"
    els = []
    # нижняя чаша, затянутая кожей (спереди +Z)
    f = all_faces(t, [8, 0, 16, 8])
    f["south"] = _face(t, [0, 0, 6, 5])
    els.append(box([5, -1, 7], [11, 4, 10], f))
    # верхняя открытая часть чаши: задняя стенка и бортики
    els.append(box([5, 4, 7], [11, 7.5, 7.75], all_faces(t, [8, 0, 16, 4])))
    els.append(box([5, 4, 7.75], [5.75, 7.5, 10], all_faces(t, [8, 0, 9, 4])))
    els.append(box([10.25, 4, 7.75], [11, 7.5, 10], all_faces(t, [8, 0, 9, 4])))
    # подставка и струны из конского волоса
    els.append(box([6.5, 1.5, 10], [9.5, 2, 10.6], all_faces(t, [14, 12, 16, 13])))
    els.append(box([7.4, 1.5, 9.9], [7.6, 16, 10.1], all_faces(t, [15, 8, 15.5, 12])))
    els.append(box([8.4, 1.5, 9.9], [8.6, 16, 10.1], all_faces(t, [15, 8, 15.5, 12])))
    # гриф и загнутая голова
    els.append(box([7.25, 7.5, 7.75], [8.75, 16, 9.5], all_faces(t, [11, 0, 12, 12])))
    els.append(box([7, 16, 7.5], [9, 19, 9.5], all_faces(t, [12.5, 0, 14.5, 4])))
    els.append(box([7, 18, 9.5], [9, 19, 10.75], all_faces(t, [12.5, 0, 14.5, 4])))
    # смычок (садақ) — лежит внизу, не наклонён
    els.append(box([1, 0.2, 11], [15, 0.8, 11.6], all_faces(t, [8, 12, 14, 13]), rot=False))
    els.append(box([1.5, 0.8, 11.1], [14.5, 1.1, 11.4], all_faces(t, [15, 8, 15.5, 12]), rot=False))
    return {"gui_light": "front", "textures": {"0": "ezhelgi:item/qobyz_3d", "particle": "ezhelgi:item/qobyz_3d"},
            "display": INSTR_DISPLAY, "elements": els}


def qobyz_texture():
    S = 64
    img = Image.new("RGBA", (S, S), (0, 0, 0, 0))
    px = img.load()
    rng = random.Random(5)
    for y in range(20):  # кожаная дека (u 0..6, v 0..5)
        for x in range(24):
            v = rng.random()
            px[x, y] = (236, 216, 180, 255) if v < 0.7 else (220, 198, 160, 255)
    for x in range(24):
        px[x, 0] = (150, 100, 56, 255)
        px[x, 19] = (150, 100, 56, 255)
    for y in range(32):  # чаша — тёмное дерево с резьбой
        for x in range(32, 64):
            px[x, y] = (96, 56, 28, 255) if (x + y // 2) % 6 else shade(GOLD, 3)
    for y in range(48):  # гриф
        for x in range(44, 48):
            px[x, y] = (80, 46, 22, 255)
    for y in range(16):  # голова
        for x in range(50, 58):
            px[x, y] = (88, 52, 26, 255) if (x * y) % 7 else (230, 210, 170, 255)
    for y in range(32, 48):  # конский волос струн
        for x in range(60, 62):
            px[x, y] = (226, 220, 206, 255)
    for y in range(48, 52):  # смычок и подставка
        for x in range(32, 56):
            px[x, y] = (120, 74, 36, 255)
        for x in range(56, 64):
            px[x, y] = (238, 228, 206, 255)
    return img
