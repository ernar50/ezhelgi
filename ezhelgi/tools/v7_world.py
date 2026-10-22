"""v7 worldgen: мавзолей Ходжи Ахмеда Ясави, петроглифы Тамгалы, поющий бархан,
корабли высохшего Арала, станция Шёлкового пути с караваном и логово айдахара.
"""
import math
import random

from yurts import Template
import v6_assets as V6

NS = "ezhelgi"
AIR = "minecraft:air"
SLAB_B = {"type": "bottom", "waterlogged": "false"}
SLAB_T = {"type": "top", "waterlogged": "false"}
LANTERN = {"hanging": "false", "waterlogged": "false"}
HLANTERN = {"hanging": "true", "waterlogged": "false"}
CAMPFIRE = {"facing": "south", "lit": "true", "signal_fire": "false", "waterlogged": "false"}
WALL = {"east": "none", "north": "none", "south": "none", "west": "none", "up": "true", "waterlogged": "false"}
BARS = {"east": "false", "north": "false", "south": "false", "west": "false", "waterlogged": "false"}


def e(n):
    return f"{NS}:{n}"


def chest(t, x, y, z, loot, facing="south"):
    t.put(x, y, z, "minecraft:chest", {"facing": facing, "type": "single", "waterlogged": "false"},
          {"id": (8, "minecraft:chest"), "LootTable": (8, e(loot))})


def mob(nbt_id, **extra):
    d = {"id": (8, nbt_id), "PersistenceRequired": (1, 1)}
    d.update(extra)
    return d


# ------------------------------------------------------------------ мавзолей Ясави
def yasawi():
    S, H, G = 43, 44, 5
    t = Template(S, H, S)
    rng = random.Random(1405)
    c = S // 2
    brick = lambda: "minecraft:mud_bricks" if rng.random() < 0.75 else ("minecraft:packed_mud" if rng.random() < 0.6 else "minecraft:bricks")
    tile_b = "minecraft:light_blue_glazed_terracotta"
    tile_d = "minecraft:blue_glazed_terracotta"
    teal = "minecraft:waxed_oxidized_cut_copper"
    # площадь и фундамент
    for x in range(S):
        for z in range(S):
            for y in range(0, G):
                t.put(x, y, z, "minecraft:sandstone")
            t.put(x, G - 1, z, "minecraft:smooth_sandstone" if (x + z) % 4 else "minecraft:cut_sandstone")
            for y in range(G, H):
                t.put(x, y, z, AIR)
    x0, x1, z0, z1 = 7, 35, 9, 37
    wall_h = 16

    def wall(x, z, top):
        for y in range(G, G + top):
            band = (y - G) in (3, 11)
            t.put(x, y, z, tile_b if band and (x + z) % 2 else (tile_d if band else brick()))

    # внешние стены главного корпуса
    for x in range(x0, x1 + 1):
        for z in range(z0, z1 + 1):
            if x in (x0, x1) or z in (z0, z1):
                wall(x, z, wall_h)
            else:
                t.put(x, G - 1, z, "minecraft:smooth_sandstone")
        # плоская кровля
    for x in range(x0, x1 + 1):
        for z in range(z0, z1 + 1):
            t.put(x, G + wall_h, z, "minecraft:mud_brick_slab", SLAB_B)
    # внутренние перегородки: библиотека (запад), казандык (центр), мечеть (восток), гробница (север)
    for z in range(z0, z1 + 1):
        for y in range(G, G + wall_h):
            t.put(14, y, z, brick())
            t.put(28, y, z, brick())
    for x in range(14, 29):
        for y in range(G, G + wall_h):
            t.put(x, y, 27, brick())
    for (dx, dz) in ((14, 18), (28, 18), (21, 27)):  # арочные проходы
        for y in range(G, G + 3):
            for k in (-1, 0, 1):
                if dz == 27:
                    t.put(dx + k, y, dz, AIR)
                else:
                    t.put(dx, y, dz + k, AIR)
    # портал-пештак на юге: огромная стена с аркой-нишей
    for x in range(9, 34):
        for z in range(6, 10):
            for y in range(G, G + 28):
                t.put(x, y, z, brick())
    for x in range(13, 30):
        for y in range(G, G + 22):
            # стрельчатая арка
            half = 8.5 - max(0, (y - G - 14)) * 1.1
            if abs(x - c) <= half:
                for z in range(6, 9):
                    t.put(x, y, z, AIR)
    # изразцовая рамка портала и сталактитовые соты наверху ниши
    for x in range(9, 34):
        for y in range(G, G + 28):
            if x in (9, 10, 32, 33) or y >= G + 25:
                t.put(x, y, 6, tile_b if (x + y) % 3 else tile_d)
    for x in range(c - 6, c + 7):
        t.put(x, G + 21, 8, "minecraft:cyan_terracotta")
        t.put(x, G + 20, 8, "minecraft:mud_brick_wall", WALL) if (x % 2) else None
    # вход
    for y in range(G, G + 4):
        for x in (c - 1, c, c + 1):
            t.put(x, y, 9, AIR)
    # угловые башни-гульдаста по краям портала и сзади
    for (tx, tz, th) in ((8, 7, 32), (34, 7, 32), (x0, z1, 20), (x1, z1, 20)):
        for x in range(tx - 2, tx + 3):
            for z in range(tz - 2, tz + 3):
                if math.hypot(x - tx, z - tz) <= 2.2:
                    for y in range(G, G + th):
                        t.put(x, y, z, brick() if (y - G) % 6 else tile_b)
                    t.put(x, G + th, z, teal if math.hypot(x - tx, z - tz) <= 1.2 else "minecraft:mud_brick_slab",
                          None if math.hypot(x - tx, z - tz) <= 1.2 else SLAB_B)
    # казандык — главный зал с куполом
    hx, hz, hr = c, 18, 8
    for x in range(hx - hr, hx + hr + 1):
        for z in range(hz - hr, hz + hr + 1):
            d = math.hypot(x - hx, z - hz)
            if hr - 1 <= d <= hr + 0.3:
                for y in range(G + wall_h, G + wall_h + 3):  # барабан
                    t.put(x, y, z, tile_b if y == G + wall_h + 1 else brick())
    for x in range(hx - hr, hx + hr + 1):
        for z in range(hz - hr, hz + hr + 1):
            for y in range(0, hr + 1):
                d = math.sqrt((x - hx) ** 2 + (z - hz) ** 2 + (y * 1.0) ** 2)
                if hr - 1 <= d <= hr + 0.3:
                    t.put(x, G + wall_h + 3 + y, z, "minecraft:smooth_sandstone" if y < hr - 1 else "minecraft:cut_sandstone")
    # купол виден изнутри зала
    for x in range(hx - hr, hx + hr + 1):
        for z in range(hz - hr, hz + hr + 1):
            if math.hypot(x - hx, z - hz) < hr - 1:
                t.put(x, G + wall_h, z, AIR)
    # Тайказан — огромный бронзовый котёл в центре зала
    for x in range(hx - 1, hx + 2):
        for z in range(hz - 1, hz + 2):
            t.put(x, G, z, "minecraft:waxed_exposed_copper")
            if (x, z) != (hx, hz):
                t.put(x, G + 1, z, "minecraft:waxed_exposed_cut_copper")
    t.put(hx, G + 1, hz, "minecraft:water_cauldron", {"level": "3"})
    for x in range(15, 28):
        for z in range(10, 27):
            if (x + z) % 3 == 0 and abs(x - hx) > 2:
                t.put(x, G, z, e("kok_syrmak") if (x // 3 + z // 3) % 2 else e("syrmak"))
    t.put(hx, G + wall_h - 1, hz, "minecraft:lantern", HLANTERN)
    # гробница с рёбристым бирюзовым куполом
    tx, tz, tr = c, 32, 5
    for x in range(tx - tr - 1, tx + tr + 2):
        for z in range(tz - tr - 1, tz + tr + 2):
            d = math.hypot(x - tx, z - tz)
            if tr - 1 <= d <= tr + 0.4:
                for y in range(G + wall_h, G + wall_h + 5):
                    band = y == G + wall_h + 2
                    t.put(x, y, z, (tile_b if (x + z) % 2 else tile_d) if band else brick())
    for x in range(tx - tr, tx + tr + 1):
        for z in range(tz - tr, tz + tr + 1):
            for y in range(0, 10):
                ry = y / 9.0
                rad = tr * math.sqrt(max(0.0, 1 - ry ** 1.6))
                d = math.hypot(x - tx, z - tz)
                if rad - 1.1 <= d <= rad + 0.2:
                    ang = math.atan2(z - tz, x - tx)
                    rib = int((ang + math.pi) / (2 * math.pi) * 16) % 2 == 0
                    t.put(x, G + wall_h + 5 + y, z, teal if rib else "minecraft:prismarine_bricks")
    for x in range(tx - tr, tx + tr + 1):
        for z in range(tz - tr, tz + tr + 1):
            if math.hypot(x - tx, z - tz) < tr - 1:
                t.put(x, G + wall_h, z, AIR)
    t.put(tx, G + wall_h + 15, tz, "minecraft:gold_block")
    t.put(tx, G + wall_h + 16, tz, "minecraft:lightning_rod", {"facing": "up", "powered": "false", "waterlogged": "false"})
    # надгробие (саркофаг) из нефрита
    for x in range(tx - 1, tx + 2):
        t.put(x, G, tz, "minecraft:verdant_froglight" if x == tx else "minecraft:dark_prismarine")
    t.put(tx, G + 1, tz, "minecraft:green_carpet")
    for (lx, lz) in ((tx - 3, tz - 2), (tx + 3, tz - 2), (tx - 3, tz + 3), (tx + 3, tz + 3)):
        t.put(lx, G, lz, "minecraft:candle", {"candles": "3", "lit": "true", "waterlogged": "false"})
    # библиотека (запад): стеллажи, пюпитры, сундуки
    for z in range(z0 + 1, z1):
        for y in range(G, G + 4):
            t.put(x0 + 1, y, z, "minecraft:bookshelf" if (z + y) % 5 else "minecraft:chiseled_bookshelf")
            if z in (z0 + 1, z1 - 1) or z % 4 == 0:
                t.put(x0 + 3, y, z, "minecraft:bookshelf") if y < G + 3 and z not in (18, 17, 19) else None
    for z in (12, 23, 30):
        t.put(11, G, z, "minecraft:lectern", {"facing": "west", "has_book": "false", "powered": "false"})
    chest(t, 12, G, 14, "chests/yasawi_library", "east")
    chest(t, 12, G, 32, "chests/yasawi_library", "east")
    for z in range(z0 + 2, z1, 6):
        t.put(10, G + wall_h - 1, z, "minecraft:lantern", HLANTERN)
    # тайник: под ковром в углу библиотеки — люк и лестница вниз
    t.put(12, G - 1, 35, "minecraft:spruce_trapdoor", {"facing": "north", "half": "top", "open": "false", "powered": "false",
                                                     "waterlogged": "false"})
    t.put(12, G, 35, "minecraft:red_carpet")
    for y in range(1, G - 1):
        t.put(12, y, 35, AIR)
        t.put(12, y, 36, "minecraft:mud_bricks")
        t.put(12, y, 35, "minecraft:ladder", {"facing": "north", "waterlogged": "false"})
    for x in range(8, 14):
        for z in range(31, 35):
            for y in range(1, 4):
                t.put(x, y, z, AIR)
            t.put(x, 0, z, "minecraft:mud_bricks")
    t.put(12, 1, 35, "minecraft:ladder", {"facing": "north", "waterlogged": "false"})
    chest(t, 9, 1, 32, "chests/yasawi_cache", "east")
    chest(t, 9, 1, 34, "chests/yasawi_cache", "east")
    t.put(11, 1, 31, "minecraft:lectern", {"facing": "south", "has_book": "false", "powered": "false"})
    t.put(13, 3, 33, "minecraft:soul_lantern", HLANTERN)
    # мечеть (восток): ковры, михраб из изразцов
    for x in range(29, x1):
        for z in range(z0 + 1, z1):
            t.put(x, G, z, e("kok_syrmak") if (x + z) % 2 else e("syrmak"))
    for y in range(G, G + 6):
        for z in range(16, 21):
            t.put(x1, y, z, tile_b if (y + z) % 2 else tile_d)
    for z in range(z0 + 3, z1, 7):
        t.put(31, G + wall_h - 1, z, "minecraft:lantern", HLANTERN)
    # окна-решётки
    for z in range(12, 36, 5):
        for y in (G + 5, G + 6):
            t.put(x0, y, z, "minecraft:iron_bars", BARS)
            t.put(x1, y, z, "minecraft:iron_bars", BARS)
    # хранитель и паломник
    t.entity(c + 2.5, G, 4.5, mob(e("aqsaqal")))
    return t


# ------------------------------------------------------------------ Тамгалы
def tamgaly(glyphs, seed):
    S, H = 31, 16
    t = Template(S, H, S)
    rng = random.Random(seed)
    rock = lambda: rng.choice(["minecraft:brown_terracotta", "minecraft:brown_terracotta", "minecraft:terracotta",
                               "minecraft:packed_mud", "minecraft:tuff"])
    # ущелье: две гряды скал вдоль оси Z
    ridge = []
    for side in (-1, 1):
        for z in range(S):
            cx = S // 2 + side * (7 + int(2 * math.sin(z * 0.35 + seed)))
            hh = 5 + int(3 * math.sin(z * 0.5 + side) + rng.random() * 2)
            for x in range(cx - 3, cx + 4):
                h = hh - abs(x - cx)
                for y in range(0, max(1, h)):
                    t.put(x, y, z, rock())
            ridge.append((cx, z, hh, side))
    # тропа
    for z in range(S):
        for x in range(S // 2 - 1, S // 2 + 2):
            t.put(x, 0, z, "minecraft:coarse_dirt" if rng.random() < 0.5 else "minecraft:dirt_path")
            for y in range(1, 6):
                t.put(x, y, z, AIR)
    # петроглифы на гранях скал, обращённых к тропе
    spots = [(cx - side * 3, z, side) for (cx, z, hh, side) in ridge if z % 4 == 2]
    rng.shuffle(spots)
    for i, g in enumerate(glyphs):
        x, z, side = spots[i]
        facing = "east" if side < 0 else "west"
        t.put(x, 2, z, e("tanba_tas"), {"facing": facing, "glyph": str(g)})
        t.put(x, 1, z, rock())
        t.put(x, 3, z, rock())
    # каменный жертвенник и стоянка
    c = S // 2
    for (dx, dz) in ((-1, -1), (1, -1), (-1, 1), (1, 1)):
        t.put(c + dx, 1, 26 + dz, "minecraft:cobblestone_wall", WALL)
    t.put(c, 1, 26, "minecraft:campfire", {"facing": "south", "lit": "false", "signal_fire": "false", "waterlogged": "false"})
    chest(t, c + 1, 1, 4, "chests/tamgaly", "south")
    t.put(c - 1, 1, 5, "minecraft:skeleton_skull", {"rotation": "6", "powered": "false"})
    return t


# ------------------------------------------------------------------ поющий бархан
def an_qum_tobe():
    S, H = 39, 14
    t = Template(S, H, S)
    rng = random.Random(901)
    c = S // 2
    for x in range(S):
        for z in range(S):
            # асимметричный бархан с крутым подветренным склоном
            dx, dz = (x - c) / 17.0, (z - c) / 11.0
            r = math.hypot(dx, dz)
            if r >= 1:
                continue
            h = 10 * (1 - r ** 1.6) * (0.75 + 0.25 * math.tanh(dz * 2))
            top = int(h)
            for y in range(0, top + 1):
                t.put(x, y, z, "minecraft:sandstone" if y < top - 3 else "minecraft:sand")
            t.put(x, top, z, e("an_qum") if rng.random() < 0.92 else "minecraft:sand")
            if rng.random() < 0.01:
                t.put(x, top + 1, z, "minecraft:dead_bush")
    return t


# ------------------------------------------------------------------ Арал
def ship(t, ox, oz, length, tilt, rng):
    """Ржавый корпус, лежащий на боку в песке."""
    rust = lambda: rng.choice(["minecraft:brown_terracotta", "minecraft:red_terracotta", "minecraft:waxed_exposed_cut_copper",
                               "minecraft:raw_copper_block", "minecraft:brown_terracotta", "minecraft:granite"])
    half_w = 3
    for i in range(length):
        z = oz + i
        taper = 1.0 if 2 < i < length - 3 else (0.4 if i in (0, length - 1) else 0.7)
        w = max(1, int(half_w * taper + 0.5))
        bow = 2 if i >= length - 3 else 0
        for dx in range(-w, w + 1):
            for y in range(0, 7 + bow):
                # сечение корпуса: U-образное
                inner = abs(dx) < w and y >= 1
                bottom = y == 0 or abs(dx) == w
                yy = y + (tilt if dx > 0 else 0)
                if bottom and not (y >= 4 and rng.random() < 0.25):
                    t.put(ox + dx, yy, z, rust())
                elif inner:
                    t.put(ox + dx, yy, z, AIR)
            if i % 3 == 0:
                t.put(ox, 5 + tilt // 2, z, "minecraft:spruce_slab", SLAB_B)  # остатки палубы
    # рубка и мачта
    cz = oz + length // 2
    for dx in range(-1, 2):
        for dz in range(-1, 2):
            for y in range(6, 9):
                if abs(dx) == 1 or abs(dz) == 1:
                    t.put(ox + dx, y, cz + dz, rust() if rng.random() < 0.85 else "minecraft:iron_bars")
    for y in range(6, 9):
        t.put(ox, y, cz, AIR)
    chest(t, ox, 6, cz, "chests/aral_ship", "south")
    for y in range(6, 14):
        if y > 11:
            t.put(ox, y, cz + 3, "minecraft:iron_chain", {"axis": "y", "waterlogged": "false"})
        else:
            t.put(ox, y, cz + 3, "minecraft:stripped_spruce_log", {"axis": "y"})


def aral():
    S, H = 45, 16
    t = Template(S, H, S)
    rng = random.Random(1960)
    for x in range(S):
        for z in range(S):
            v = rng.random()
            t.put(x, 0, z, "minecraft:sand" if v < 0.55 else ("minecraft:calcite" if v < 0.7 else
                                                                ("minecraft:coarse_dirt" if v < 0.85 else "minecraft:smooth_sandstone")))
            for y in range(1, H):
                t.put(x, y, z, AIR)
            if rng.random() < 0.012:
                t.put(x, 1, z, "minecraft:dead_bush")
            if rng.random() < 0.006:
                t.put(x, 1, z, "minecraft:bone_block", {"axis": rng.choice(["x", "z"])})
    ship(t, 12, 6, 18, 1, rng)
    ship(t, 32, 18, 14, 0, rng)
    # рыбацкая лодка и сети
    for i in range(5):
        t.put(22 + i, 1, 38, "minecraft:spruce_planks" if i in (0, 4) else "minecraft:spruce_slab", None if i in (0, 4) else SLAB_B)
    for (x, z) in ((25, 33), (26, 34), (27, 33)):
        t.put(x, 1, z, "minecraft:cobweb")
    return t


# ------------------------------------------------------------------ Шёлковый путь
def jibek_joly():
    L, Wd, H = 49, 21, 10
    t = Template(L, H, Wd)
    rng = random.Random(1300)
    mid = Wd // 2
    for x in range(L):
        for z in range(Wd):
            for y in range(1, H):
                t.put(x, y, z, AIR)
        for z in range(mid - 1, mid + 2):  # дорога
            t.put(x, 0, z, "minecraft:dirt_path" if rng.random() < 0.7 else
                  ("minecraft:gravel" if rng.random() < 0.5 else "minecraft:coarse_dirt"))
        if x % 12 == 0:  # верстовые столбы
            t.put(x, 1, mid - 2, "minecraft:stone_bricks")
            t.put(x, 2, mid - 2, "minecraft:stone_brick_wall", WALL)
            t.put(x, 3, mid - 2, "minecraft:lantern", LANTERN)
    # станция-рабат: колодец, навес, коновязь, товары
    sx = L // 2
    for x in range(sx - 3, sx + 4):
        for z in range(mid + 2, mid + 8):
            t.put(x, 0, z, "minecraft:packed_mud")
    for (x, z) in ((sx - 3, mid + 2), (sx + 3, mid + 2), (sx - 3, mid + 7), (sx + 3, mid + 7)):
        for y in range(1, 4):
            t.put(x, y, z, "minecraft:stripped_spruce_log", {"axis": "y"})
    for x in range(sx - 3, sx + 4):
        for z in range(mid + 2, mid + 8):
            t.put(x, 4, z, "minecraft:white_wool" if (x + z) % 2 else "minecraft:red_wool")
    t.put(sx, 1, mid + 5, e("syrmak"))
    t.put(sx - 1, 1, mid + 5, e("kok_syrmak"))
    chest(t, sx + 2, 1, mid + 6, "chests/jibek_joly", "west")
    t.put(sx - 2, 1, mid + 6, "minecraft:barrel", {"facing": "up", "open": "false"})
    t.put(sx - 2, 2, mid + 6, "minecraft:barrel", {"facing": "up", "open": "false"})
    t.put(sx + 2, 1, mid + 3, "minecraft:hay_block", {"axis": "y"})
    t.put(sx, 3, mid + 4, "minecraft:lantern", HLANTERN)
    # колодец
    wx, wz = sx, mid - 5
    for dx in (-1, 0, 1):
        for dz in (-1, 0, 1):
            if (dx, dz) != (0, 0):
                t.put(wx + dx, 1, wz + dz, "minecraft:cobblestone")
            t.put(wx + dx, 0, wz + dz, "minecraft:cobblestone")
    t.put(wx, 0, wz, "minecraft:water")
    t.put(wx, 1, wz, AIR)
    # коновязь
    for x in range(sx + 6, sx + 11):
        t.put(x, 1, mid + 3, "minecraft:spruce_fence", {"east": "true" if x < sx + 10 else "false",
                                                       "west": "true" if x > sx + 6 else "false",
                                                       "north": "false", "south": "false", "waterlogged": "false"})
    # караван: купец-керуенші сам приводит верблюдов
    t.entity(sx - 6.5, 1, mid + 0.5, mob(e("keruenshi"), Variant=(3, rng.randrange(20))))
    t.entity(sx + 0.5, 1, mid + 4.5, mob(e("kuishi"), Variant=(3, rng.randrange(20))))
    return t


# ------------------------------------------------------------------ логово айдахара
def aidahar_uyasy():
    S, H = 33, 18
    t = Template(S, H, S)
    rng = random.Random(888)
    c = S // 2
    for x in range(S):
        for z in range(S):
            r = math.hypot(x - c, z - c)
            if r > c:
                continue
            # кратер: кольцевой вал и чаша
            rim = 7 * math.exp(-((r - 12) / 3.2) ** 2)
            floor = 2 if r < 10 else 2 + int(rim)
            for y in range(0, floor + 1):
                v = rng.random()
                t.put(x, y, z, "minecraft:blackstone" if v < 0.45 else ("minecraft:basalt" if v < 0.7 else
                                                                         ("minecraft:magma_block" if v < 0.78 and y == floor else
                                                                          "minecraft:cobbled_deepslate")),
                      {"axis": "y"} if v >= 0.45 and v < 0.7 else None)
            for y in range(floor + 1, H):
                t.put(x, y, z, AIR)
            # клад: золото и кости в чаше
            if r < 8 and rng.random() < 0.25:
                t.put(x, 3, z, rng.choice(["minecraft:gold_block", "minecraft:raw_gold_block", "minecraft:bone_block",
                                           "minecraft:gold_block"]))
            if 10 < r < 14 and rng.random() < 0.06:
                for y in range(floor + 1, floor + 4):
                    t.put(x, y, z, "minecraft:bone_block", {"axis": "y"})
    chest(t, c - 3, 3, c, "chests/aidahar_hoard", "east")
    chest(t, c + 3, 3, c + 1, "chests/aidahar_hoard", "west")
    for (x, z) in ((c, c - 4), (c - 4, c + 3), (c + 4, c - 3)):
        t.put(x, 3, z, "minecraft:skeleton_skull", {"rotation": str(rng.randrange(16)), "powered": "false"})
    t.entity(c + 0.5, 6, c + 0.5, mob(e("aidahar")))
    return t


def build_all():
    return {
        "yasawi": yasawi(),
        "tamgaly/a": tamgaly([0, 1, 2, 3, 4], 11),
        "tamgaly/b": tamgaly([3, 4, 5, 6, 7], 12),
        "an_qum_tobe": an_qum_tobe(),
        "aral": aral(),
        "jibek_joly": jibek_joly(),
        "aidahar_uyasy": aidahar_uyasy(),
    }


def run(g):
    for name, tpl in build_all().items():
        g.write_template(name, tpl)
    pool, structure = V6.pool, V6.structure
    pool("yasawi/start", [("yasawi", 1)])
    structure("yasawi", "#ezhelgi:yasawi", "yasawi/start", "beard_box", -5, 120, 48, 1166)
    pool("tamgaly/start", [("tamgaly/a", 1), ("tamgaly/b", 1)])
    structure("tamgaly", "#ezhelgi:tamgaly", "tamgaly/start", "beard_thin", -1, 56, 20, 3000)
    pool("an_qum_tobe/start", [("an_qum_tobe", 1)])
    structure("an_qum_tobe", "#ezhelgi:dune", "an_qum_tobe/start", "beard_thin", -1, 40, 14, 4242)
    pool("aral/start", [("aral", 1)])
    structure("aral", "#ezhelgi:aral", "aral/start", "beard_thin", -1, 72, 28, 1960)
    pool("jibek_joly/start", [("jibek_joly", 1)])
    structure("jibek_joly", "#ezhelgi:caravanserai", "jibek_joly/start", "beard_thin", -1, 36, 12, 1300,
              monsters=None)
    pool("aidahar_uyasy/start", [("aidahar_uyasy", 1)])
    structure("aidahar_uyasy", "#ezhelgi:peaks", "aidahar_uyasy/start", "beard_thin", -2, 80, 32, 8888)
