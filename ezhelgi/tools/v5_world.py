"""v5 worldgen templates: rune stones, the giant Baiterek tree and Burabay mountain.
y = 0 is the ground layer (replaces the surface block).
"""
import math
import random

from yurts import Template

NS = "ezhelgi"
RUNE = (f"{NS}:runa_tasy", {"lit": "true"})
SLAB_B = {"type": "bottom", "waterlogged": "false"}
SLAB_T = {"type": "top", "waterlogged": "false"}
LEAVES = {"persistent": "true", "distance": "1", "waterlogged": "false"}
LANTERN = {"hanging": "false", "waterlogged": "false"}
LOG_Y = {"axis": "y"}


def e(n):
    return f"{NS}:{n}"


def rune(t, x, y, z):
    t.put(x, y, z, *RUNE)


# ------------------------------------------------------------------ руна тас
def stele():
    t = Template(5, 5, 5)
    rng = random.Random(1)
    for x in range(5):
        for z in range(5):
            if math.hypot(x - 2, z - 2) <= 2.3:
                t.put(x, 0, z, "minecraft:mossy_cobblestone" if rng.random() < 0.5 else "minecraft:cobblestone")
    rune(t, 2, 1, 2)
    rune(t, 2, 2, 2)
    t.put(2, 3, 2, "minecraft:stone_brick_slab", SLAB_B)
    return t


def circle():
    """Круг камней с рунной стелой в центре."""
    S = 11
    t = Template(S, 5, S)
    c = S // 2
    rng = random.Random(2)
    for x in range(S):
        for z in range(S):
            r = math.hypot(x - c, z - c)
            if r <= 5.2:
                t.put(x, 0, z, "minecraft:coarse_dirt" if rng.random() < 0.3 else "minecraft:grass_block",
                      None if rng.random() < 0.3 else {"snowy": "false"})
    for a in range(0, 360, 45):
        x = round(c + 4.5 * math.cos(math.radians(a)))
        z = round(c + 4.5 * math.sin(math.radians(a)))
        t.put(x, 1, z, "minecraft:mossy_cobblestone")
        if a % 90 == 0:
            t.put(x, 2, z, "minecraft:cobblestone_wall", {"east": "none", "north": "none", "south": "none", "west": "none",
                                                          "up": "true", "waterlogged": "false"})
    rune(t, c, 1, c)
    rune(t, c, 2, c)
    rune(t, c, 3, c)
    t.put(c, 4, c, "minecraft:chiseled_stone_bricks")
    return t


def balbal():
    """Балбалы: каменные изваяния в ряд, первое — с рунами."""
    t = Template(9, 4, 3)
    for i, x in enumerate((1, 4, 7)):
        t.put(x, 0, 1, "minecraft:cobblestone")
        if i == 0:
            rune(t, x, 1, 1)
            rune(t, x, 2, 1)
        else:
            t.put(x, 1, 1, "minecraft:stone")
            t.put(x, 2, 1, "minecraft:chiseled_stone_bricks")
        t.put(x, 3, 1, "minecraft:skeleton_skull", {"rotation": "8", "powered": "false"}) if i == 1 else None
    return t


# ------------------------------------------------------------------ Бәйтерек
def baiterek():
    S = 25
    H = 60
    t = Template(S, H, S)
    c = S // 2
    rng = random.Random(7)
    wood = "minecraft:birch_wood"
    TRUNK_TOP = 36
    # корни
    for k in range(10):
        a = k * math.pi * 2 / 10 + rng.uniform(-0.2, 0.2)
        for r in range(2, 8):
            y = max(0, 2 - r // 3)
            x, z = round(c + math.cos(a) * r), round(c + math.sin(a) * r)
            t.put(x, y, z, wood)
            if r < 4:
                t.put(x, y + 1, z, wood)
    # ствол: белый, сужается кверху
    for y in range(0, TRUNK_TOP + 1):
        rad = 3.0 - 1.4 * (y / TRUNK_TOP)
        for x in range(S):
            for z in range(S):
                if math.hypot(x - c, z - c) <= rad:
                    t.put(x, y, z, wood)
    # спиральная лестница из плит вокруг ствола
    h = 1.0
    ang = 0.0
    steps = 0
    while h < TRUNK_TOP - 1:
        rad = 3.0 - 1.4 * (h / TRUNK_TOP) + 1.6
        x = round(c + math.cos(ang) * rad)
        z = round(c + math.sin(ang) * rad)
        y = int(h)
        t.put(x, y, z, "minecraft:birch_slab", SLAB_T if h - y >= 0.5 else SLAB_B)
        for dy in (1, 2, 3):
            if (x, y + dy, z) in t.blocks and t.blocks[(x, y + dy, z)][0] == "minecraft:birch_slab":
                del t.blocks[(x, y + dy, z)]
        if steps % 14 == 7:
            t.put(round(c + math.cos(ang) * (rad + 1)), y + 1, round(c + math.sin(ang) * (rad + 1)), "minecraft:lantern", LANTERN)
        ang += 1.0 / rad
        h += 0.5
        steps += 1
    # крона-чаша: ветви расходятся вверх и наружу
    branch_tips = []
    for k in range(9):
        a = k * math.pi * 2 / 9
        for s in range(0, 16):
            y = TRUNK_TOP - 2 + int(s * 0.75)
            r = 1.2 + s * 0.62
            x, z = round(c + math.cos(a) * r), round(c + math.sin(a) * r)
            t.put(x, y, z, wood)
        branch_tips.append((round(c + math.cos(a) * r), TRUNK_TOP - 2 + int(15 * 0.75), round(c + math.sin(a) * r)))
    # листва вокруг кончиков ветвей и по кругу чаши
    for (bx, by, bz) in branch_tips:
        for x in range(bx - 3, bx + 4):
            for y in range(by - 2, by + 3):
                for z in range(bz - 3, bz + 4):
                    d = math.sqrt((x - bx) ** 2 + ((y - by) * 1.3) ** 2 + (z - bz) ** 2)
                    if d <= 3.0 - rng.random() * 0.8 and (x, y, z) not in t.blocks:
                        t.put(x, y, z, "minecraft:birch_leaves", LEAVES)
    # гнездо из сена на вершине ствола
    NEST_Y = TRUNK_TOP + 1
    for x in range(S):
        for z in range(S):
            r = math.hypot(x - c, z - c)
            if r <= 4.2:
                t.put(x, NEST_Y, z, "minecraft:hay_block", LOG_Y)
            elif r <= 5.2:
                t.put(x, NEST_Y + 1, z, "minecraft:hay_block", {"axis": "x" if (x + z) % 2 else "z"})
    # золотое яйцо Самұрыка (сияет ночью) и сундук гнезда
    EY = NEST_Y + 4
    for x in range(c - 3, c + 4):
        for y in range(NEST_Y + 1, NEST_Y + 9):
            for z in range(c - 3, c + 4):
                d = math.sqrt((x - c) ** 2 + ((y - EY) / 1.35) ** 2 + (z - c) ** 2)
                if d <= 2.6:
                    t.put(x, y, z, "minecraft:glowstone" if d < 1.4 else "minecraft:yellow_stained_glass")
    t.put(c + 3, NEST_Y + 1, c, "minecraft:chest", {"facing": "west", "type": "single", "waterlogged": "false"},
          {"id": (8, "minecraft:chest"), "LootTable": (8, e("chests/samuryk_uyasy"))})
    # выход лестницы на гнездо
    for y in range(TRUNK_TOP - 3, NEST_Y + 1):
        pass
    # Самұрыки
    bird = {"id": (8, e("samuryk")), "PersistenceRequired": (1, 1)}
    t.entity(c - 3 + 0.5, NEST_Y + 1, c + 0.5, bird)
    t.entity(c + 0.5, NEST_Y + 2, c - 3 + 0.5, dict(bird))
    return t


# ------------------------------------------------------------------ Бурабай
def burabay():
    S = 61
    c = S // 2
    rng = random.Random(11)
    peaks = [(c, c, 40.0, 9.0), (c - 7, c + 4, 30.0, 6.0), (c + 6, c - 6, 25.0, 7.0), (c + 3, c + 9, 16.0, 5.0)]
    R = 21.0

    def height(x, z):
        r = math.hypot(x - c, z - c)
        if r > R:
            return -1
        h = 0.0
        for (px, pz, ph, w) in peaks:
            d = math.hypot(x - px, z - pz)
            h = max(h, ph * math.exp(-(d / w) ** 2 * 0.9))
        base = 6.0 * (1 - r / R)
        noise = (math.sin(x * 0.9) + math.cos(z * 1.1) + math.sin((x + z) * 0.6)) * 0.9
        return max(0, int(h + base + noise + rng.random() * 0.6))

    hm = {}
    for x in range(S):
        for z in range(S):
            hm[(x, z)] = height(x, z)
    H = max(hm.values()) + 14
    t = Template(S, H, S)
    rocks = ["minecraft:granite", "minecraft:granite", "minecraft:granite", "minecraft:stone", "minecraft:andesite",
             "minecraft:cobblestone", "minecraft:polished_granite"]
    for (x, z), h in hm.items():
        if h < 0:
            continue
        steep = max(abs(h - hm.get((x + dx, z + dz), h)) for dx, dz in ((1, 0), (-1, 0), (0, 1), (0, -1))
                    if hm.get((x + dx, z + dz), -1) >= 0) if h > 0 else 0
        for y in range(0, h + 1):
            soft = h <= 4 or (h < 13 and steep <= 1)
            if y == h and soft:
                top = "minecraft:podzol" if rng.random() < 0.3 else "minecraft:grass_block"
                t.put(x, y, z, top, {"snowy": "false"})
            elif y >= h - 2 and soft:
                t.put(x, y, z, "minecraft:dirt")
            else:
                t.put(x, y, z, rocks[(x * 7 + y * 3 + z * 5 + rng.randint(0, 2)) % len(rocks)])
        # сосны на пологих склонах
        if 1 <= h < 14 and steep <= 1 and rng.random() < 0.035:
            pine(t, x, h + 1, z, rng)
    # озеро у подножия (Боровое) и скала Жұмбақтас
    lx, lz, lr = c, c + 22, 7.0
    for x in range(S):
        for z in range(S):
            d = math.hypot(x - lx, z - lz)
            if d <= lr and hm[(x, z)] <= 2:
                depth = 3 if d < lr - 2 else 2
                for y in range(0, 3):
                    t.put(x, y, z, "minecraft:water" if y >= 3 - depth else "minecraft:sand", {"level": "0"} if y >= 3 - depth else None)
                t.put(x, -1, z, "minecraft:sand") if False else None
                for y in range(3, 8):
                    if (x, y, z) in t.blocks:
                        del t.blocks[(x, y, z)]
            elif lr < d <= lr + 1.5 and hm[(x, z)] <= 3:
                t.put(x, 2, z, "minecraft:sand")
    for (dx, dy, dz) in ((0, 2, 0), (1, 2, 0), (0, 2, 1), (0, 3, 0), (1, 3, 0), (0, 4, 0), (0, 5, 0), (-1, 2, 0), (0, 3, 1)):
        t.put(lx + dx, dy, lz + dz, "minecraft:granite" if dy < 5 else "minecraft:polished_granite")
    # избушка кемпір на уступе
    hx, hz = c + 12, c - 2
    floor = max(hm[(x, z)] for x in range(hx - 2, hx + 3) for z in range(hz - 2, hz + 3) if hm[(x, z)] >= 0)
    hut(t, hx, floor, hz, rng)
    # тропинка из ступеней к избушке с юга
    for k in range(0, 14):
        x, z = hx, hz + 3 + k
        y = max(0, floor - k)
        if hm.get((x, z), -1) < 0:
            break
        for yy in range(y + 1, y + 4):
            if (x, yy, z) in t.blocks:
                del t.blocks[(x, yy, z)]
        t.put(x, y, z, "minecraft:cobblestone_stairs", {"facing": "north", "half": "bottom", "shape": "straight", "waterlogged": "false"})
    return t


def pine(t, x, y, z, rng):
    h = rng.randint(6, 9)
    for k in range(h):
        t.put(x, y + k, z, "minecraft:spruce_log", LOG_Y)
    for k in range(2, h + 2):
        r = max(0, int((h + 2 - k) * 0.45))
        if k % 2 == 1 and r > 0:
            r -= 1
        for dx in range(-r, r + 1):
            for dz in range(-r, r + 1):
                if abs(dx) + abs(dz) <= r + 0.5 and (dx or dz or k >= h):
                    if (x + dx, y + k, z + dz) not in t.blocks:
                        t.put(x + dx, y + k, z + dz, "minecraft:spruce_leaves", LEAVES)
    t.put(x, y + h + 1, z, "minecraft:spruce_leaves", LEAVES)


def hut(t, cx, fy, cz, rng):
    """Покосившаяся избушка жалмауыз кемпір: тёмное дерево, паутина, котёл."""
    for x in range(cx - 3, cx + 4):
        for z in range(cz - 3, cz + 4):
            for y in range(fy + 1, fy + 8):
                if (x, y, z) in t.blocks:
                    del t.blocks[(x, y, z)]
            t.put(x, fy, z, "minecraft:dark_oak_planks" if abs(x - cx) <= 2 and abs(z - cz) <= 2 else "minecraft:cobblestone")
            for y in range(fy - 6, fy):
                if (x, y, z) not in t.blocks and y >= 0:
                    t.put(x, y, z, "minecraft:cobblestone")
    for x in range(cx - 2, cx + 3):
        for z in range(cz - 2, cz + 3):
            edge = abs(x - cx) == 2 or abs(z - cz) == 2
            corner = abs(x - cx) == 2 and abs(z - cz) == 2
            for y in range(fy + 1, fy + 4):
                if corner:
                    t.put(x, y, z, "minecraft:dark_oak_log", LOG_Y)
                elif edge:
                    window = y == fy + 2 and (x == cx or z == cz) and not (z == cz + 2 and x == cx)
                    t.put(x, y, z, "minecraft:black_stained_glass_pane" if window and rng.random() < 0.5 else
                          ("minecraft:mossy_cobblestone" if y == fy + 1 and rng.random() < 0.4 else "minecraft:dark_oak_planks"))
            t.put(x, fy + 4, z, "minecraft:spruce_planks")
    for (x, z) in ((cx - 1, cz - 1), (cx, cz), (cx + 1, cz - 1)):
        t.put(x, fy + 5, z, "minecraft:spruce_slab", SLAB_B)
    # дверной проём на юг
    t.put(cx, fy + 1, cz + 2, "minecraft:air")
    t.put(cx, fy + 2, cz + 2, "minecraft:air")
    t.put(cx - 1, fy + 1, cz - 1, "minecraft:cauldron")
    t.put(cx + 1, fy + 1, cz - 1, "minecraft:chest", {"facing": "south", "type": "single", "waterlogged": "false"},
          {"id": (8, "minecraft:chest"), "LootTable": (8, e("chests/kempir"))})
    t.put(cx - 1, fy + 3, cz + 1, "minecraft:cobweb")
    t.put(cx + 1, fy + 3, cz + 1, "minecraft:cobweb")
    t.put(cx, fy + 3, cz, "minecraft:soul_lantern", {"hanging": "true", "waterlogged": "false"})
    t.put(cx - 1, fy + 1, cz + 1, "minecraft:skeleton_skull", {"rotation": "4", "powered": "false"})
    t.put(cx + 3, fy + 1, cz + 2, "minecraft:skeleton_skull", {"rotation": "0", "powered": "false"})
    t.put(cx - 3, fy + 1, cz + 3, "minecraft:dead_bush")
    rune(t, cx + 3, fy + 1, cz - 3)
    t.entity(cx + 0.5, fy + 1, cz + 0.5, {"id": (8, e("zhalmauyz_kempir")), "PersistenceRequired": (1, 1)})


def build_all():
    return {
        "runa/stele": stele(),
        "runa/circle": circle(),
        "runa/balbal": balbal(),
        "baiterek": baiterek(),
        "burabay": burabay(),
    }
