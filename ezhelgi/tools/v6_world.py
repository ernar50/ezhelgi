"""v6 worldgen templates: caravanserai (intact and ruined), the great wolf statue,
the ruins of Otrar and the camp of the dau (giant) at a cave mouth.
"""
import math
import random

from yurts import Template, villager_nbt

NS = "ezhelgi"
AIR = "minecraft:air"
SLAB_B = {"type": "bottom", "waterlogged": "false"}
SLAB_T = {"type": "top", "waterlogged": "false"}
LANTERN = {"hanging": "false", "waterlogged": "false"}
HLANTERN = {"hanging": "true", "waterlogged": "false"}
WALL = {"east": "none", "north": "none", "south": "none", "west": "none", "up": "true", "waterlogged": "false"}
FENCE = {"east": "false", "north": "false", "south": "false", "west": "false", "waterlogged": "false"}
CAMPFIRE = {"facing": "south", "lit": "true", "signal_fire": "false", "waterlogged": "false"}


def e(n):
    return f"{NS}:{n}"


def chest(t, x, y, z, loot, facing="south"):
    t.put(x, y, z, "minecraft:chest", {"facing": facing, "type": "single", "waterlogged": "false"},
          {"id": (8, "minecraft:chest"), "LootTable": (8, e(loot))})


def bed(t, x, y, z, facing, color="red"):
    dx, dz = {"north": (0, -1), "south": (0, 1), "east": (1, 0), "west": (-1, 0)}[facing]
    t.put(x, y, z, f"minecraft:{color}_bed", {"facing": facing, "occupied": "false", "part": "head"})
    t.put(x - dx, y, z - dz, f"minecraft:{color}_bed", {"facing": facing, "occupied": "false", "part": "foot"})


def mob(nbt_id, persistent=True, **extra):
    d = {"id": (8, nbt_id)}
    if persistent:
        d["PersistenceRequired"] = (1, 1)
    d.update(extra)
    return d


# ------------------------------------------------------------------ керуен-сарай
def caravanserai(ruined=False):
    S, H = 35, 12
    t = Template(S, H, S)
    c = S // 2
    rng = random.Random(41 if not ruined else 42)
    brick = lambda: ("minecraft:mud_bricks" if rng.random() < 0.8 else "minecraft:packed_mud")
    base = "minecraft:smooth_sandstone"
    # двор
    for x in range(S):
        for z in range(S):
            t.put(x, 0, z, "minecraft:dirt_path" if rng.random() < 0.5 else ("minecraft:coarse_dirt" if rng.random() < 0.6 else "minecraft:sand"))
            for y in range(1, 9):
                t.put(x, y, z, AIR)
    # внешние стены
    lo, hi = 1, S - 2
    for x in range(lo, hi + 1):
        for z in range(lo, hi + 1):
            edge = x in (lo, hi) or z in (lo, hi)
            if not edge:
                continue
            for y in range(1, 7):
                t.put(x, y, z, base if y == 1 else brick())
            if (x + z) % 2 == 0:
                t.put(x, 7, z, "minecraft:mud_brick_wall", WALL)
    # угловые башни
    for tx, tz in ((lo, lo), (lo, hi), (hi, lo), (hi, hi)):
        for x in range(tx - 2, tx + 3):
            for z in range(tz - 2, tz + 3):
                if math.hypot(x - tx, z - tz) <= 2.4:
                    for y in range(1, 9):
                        t.put(x, y, z, base if y == 1 else brick())
                    t.put(x, 9, z, "minecraft:mud_brick_wall", WALL) if math.hypot(x - tx, z - tz) > 1.4 and (x + z) % 2 == 0 else None
                    if math.hypot(x - tx, z - tz) <= 1.4:
                        t.put(x, 9, z, "minecraft:mud_brick_slab", SLAB_B)
    # ворота на юг
    for x in range(c - 1, c + 2):
        for y in range(1, 5):
            t.put(x, y, hi, AIR)
    for x in range(c - 2, c + 3):
        t.put(x, 5, hi, "minecraft:chiseled_sandstone")
    t.put(c - 2, 4, hi, "minecraft:chiseled_sandstone")
    t.put(c + 2, 4, hi, "minecraft:chiseled_sandstone")
    # кельи вдоль северной, западной и восточной стен
    D = 5  # глубина комнаты

    def room_row(axis, fixed, inward, length_from, length_to, kind):
        k = 0
        pos = length_from
        while pos + 5 <= length_to:
            a0, a1 = pos, pos + 5
            for a in range(a0, a1 + 1):
                for d in range(0, D + 1):
                    coord = fixed + inward * d
                    x, z = (a, coord) if axis == "x" else (coord, a)
                    wall = a in (a0, a1) or d == D
                    for y in range(1, 5):
                        if wall:
                            t.put(x, y, z, brick())
                    t.put(x, 5, z, "minecraft:mud_brick_slab", SLAB_B)
            # проём во двор
            mid = (a0 + a1) // 2
            coord = fixed + inward * D
            for y in (1, 2):
                x, z = (mid, coord) if axis == "x" else (coord, mid)
                t.put(x, y, z, AIR)
            x, z = (mid, coord) if axis == "x" else (coord, mid)
            t.put(x, 3, z, "minecraft:spruce_trapdoor", {"facing": "north", "half": "top", "open": "false",
                                                         "powered": "false", "waterlogged": "false"})
            # обстановка
            inner = lambda a, d: (a, fixed + inward * d) if axis == "x" else (fixed + inward * d, a)
            facing_in = {("x", 1): "south", ("x", -1): "north", ("z", 1): "east", ("z", -1): "west"}[(axis, inward)]
            facing_out = {"south": "north", "north": "south", "east": "west", "west": "east"}[facing_in]
            rk = kind[k % len(kind)]
            if rk == "sleep":
                hx, hz = inner(a0 + 1, 1)
                ox, oz = inner(a0 + 1, 2)
                t.put(hx, 1, hz, "minecraft:red_bed", {"facing": facing_out, "occupied": "false", "part": "head"})
                t.put(ox, 1, oz, "minecraft:red_bed", {"facing": facing_out, "occupied": "false", "part": "foot"})
                bx, bz = inner(a1 - 1, 1)
                chest(t, bx, 1, bz, "chests/caravanserai_ruin" if ruined else "chests/caravanserai", facing_in)
                cx2, cz2 = inner(a0 + 3, 1)
                t.put(cx2, 1, cz2, e("syrmak"))
            elif rk == "store":
                for a in range(a0 + 1, a1):
                    bx, bz = inner(a, 1)
                    t.put(bx, 1, bz, "minecraft:barrel", {"facing": "up", "open": "false"})
                    if rng.random() < 0.6:
                        t.put(bx, 2, bz, "minecraft:barrel", {"facing": "up", "open": "false"})
                bx, bz = inner(a0 + 2, 3)
                t.put(bx, 1, bz, "minecraft:hay_block", {"axis": "y"})
                bx, bz = inner(a1 - 1, 3)
                chest(t, bx, 1, bz, "chests/caravanserai_ruin" if ruined else "chests/caravanserai", facing_out)
            elif rk == "stable":
                for a in range(a0 + 1, a1):
                    bx, bz = inner(a, 1)
                    t.put(bx, 1, bz, "minecraft:hay_block", {"axis": "y"})
                for y in (1, 2):
                    x, z = (mid, coord) if axis == "x" else (coord, mid)
                    t.put(x, y, z, AIR)
                if not ruined:
                    ex, ez = inner(mid, 3)
                    t.entity(ex + 0.5, 1, ez + 0.5, mob("minecraft:camel") if k % 2 else mob("minecraft:horse"))
            ix, iz = inner(mid, 2)
            t.put(ix, 4, iz, "minecraft:lantern", HLANTERN)
            pos += 5
            k += 1

    room_row("x", lo + 1, 1, lo + 3, hi - 3, ["sleep", "store", "sleep", "sleep", "store"])
    room_row("z", lo + 1, 1, lo + D + 2, hi - 3, ["sleep", "stable", "sleep"])
    room_row("z", hi - 1, -1, lo + D + 2, hi - 3, ["stable", "store", "stable"])
    # колодец и торговые ряды во дворе
    for x in range(c - 2, c + 3):
        for z in range(c - 2, c + 3):
            edge = abs(x - c) == 2 or abs(z - c) == 2
            t.put(x, 0, z, "minecraft:stone_bricks")
            t.put(x, 1, z, "minecraft:stone_brick_wall" if edge else "minecraft:water",
                  dict(WALL) if edge else {"level": "0"})
    for (dx, dz) in ((-2, -2), (2, -2), (-2, 2), (2, 2)):
        t.put(c + dx, 2, c + dz, "minecraft:spruce_fence", FENCE)
        t.put(c + dx, 3, c + dz, "minecraft:spruce_fence", FENCE)
    for x in range(c - 2, c + 3):
        for z in range(c - 2, c + 3):
            t.put(x, 4, z, "minecraft:spruce_slab", SLAB_B)
    if not ruined:
        for i, (sx, sz) in enumerate(((c - 6, c + 6), (c + 6, c + 6), (c - 6, c + 2), (c + 6, c + 2))):
            t.put(sx, 1, sz, e(["syrmak", "alasha", "ornek", "kok_syrmak"][i]))
            t.put(sx, 1, sz - 1, "minecraft:barrel", {"facing": "up", "open": "false"})
            t.put(sx + (1 if sx < c else -1), 1, sz, "minecraft:spruce_fence", FENCE)
            t.put(sx + (1 if sx < c else -1), 2, sz, "minecraft:lantern", LANTERN)
        for i, (vx, vz) in enumerate(((c - 5, c + 5), (c + 5, c + 4), (c, c + 8))):
            t.entity(vx + 0.5, 1, vz + 0.5, villager_nbt(rng))
        t.entity(c + 4.5, 1, c + 9.5, mob("minecraft:camel"))
    else:
        # разрушения: дыры в стенах, обвалы крыш, паутина и песок
        for (x, y, z), (name, props, nbt) in list(t.blocks.items()):
            if name in ("minecraft:mud_bricks", "minecraft:packed_mud", "minecraft:mud_brick_wall",
                        "minecraft:mud_brick_slab") and y >= 2:
                n = (math.sin(x * 0.7) + math.cos(z * 0.6) + math.sin((x + z) * 0.31)) * 2.2
                if y > 5 + n or rng.random() < 0.12:
                    t.put(x, y, z, AIR)
                elif rng.random() < 0.15:
                    t.put(x, y, z, "minecraft:cracked_stone_bricks" if rng.random() < 0.3 else "minecraft:packed_mud")
        for _ in range(40):
            x, z = rng.randrange(2, S - 2), rng.randrange(2, S - 2)
            if (x, 1, z) in t.blocks and t.blocks[(x, 1, z)][0] == AIR:
                t.put(x, 1, z, "minecraft:sand" if rng.random() < 0.6 else "minecraft:cobweb")
        for _ in range(14):
            x, y, z = rng.randrange(2, S - 2), rng.randint(2, 4), rng.randrange(2, S - 2)
            if (x, y, z) in t.blocks and t.blocks[(x, y, z)][0] == AIR:
                t.put(x, y, z, "minecraft:cobweb")
        t.put(c, 1, c - 4, "minecraft:skeleton_skull", {"rotation": "6", "powered": "false"})
    return t


# ------------------------------------------------------------------ Бөрі мүсіні (статуя волка)
def wolf_statue():
    S, H = 31, 46
    t = Template(S, H, S)
    c = S // 2
    rng = random.Random(51)
    # постамент со ступенями
    for x in range(S):
        for z in range(S):
            r = max(abs(x - c), abs(z - c))
            if r <= 9:
                t.put(x, 0, z, "minecraft:stone_bricks")
            if r <= 8:
                t.put(x, 1, z, "minecraft:polished_andesite")
            if r <= 7:
                t.put(x, 2, z, "minecraft:chiseled_stone_bricks" if (x + z) % 4 == 0 and r == 7 else "minecraft:stone_bricks")
            if r <= 6:
                t.put(x, 3, z, "minecraft:polished_andesite")
    k = 2.4
    B = 4  # верх постамента

    def ell(p, cen, rad):
        return sum(((p[i] - cen[i]) / rad[i]) ** 2 for i in range(3)) <= 1.0

    def capsule(p, a, b, r):
        ax, ay, az = a
        bx, by, bz = b
        px, py, pz = p
        abx, aby, abz = bx - ax, by - ay, bz - az
        L = abx * abx + aby * aby + abz * abz
        tt = max(0.0, min(1.0, ((px - ax) * abx + (py - ay) * aby + (pz - az) * abz) / L))
        qx, qy, qz = ax + abx * tt, ay + aby * tt, az + abz * tt
        return (px - qx) ** 2 + (py - qy) ** 2 + (pz - qz) ** 2 <= r * r

    # волк сидит мордой на юг (+z) и воет на небо (единицы ~ блоки до масштаба k)
    parts = [
        ("e", (0, 3.2, -1.5), (2.8, 2.6, 3.4)),       # задние лапы/круп
        ("e", (0, 6.0, 1.0), (2.5, 3.6, 2.6)),        # грудь
        ("c", (-1.1, 0.5, 2.0), (-1.1, 5.0, 2.0), 0.75),  # передние лапы
        ("c", (1.1, 0.5, 2.0), (1.1, 5.0, 2.0), 0.75),
        ("c", (0, 8.0, 1.6), (0, 10.4, 2.8), 1.6),   # шея
        ("e", (0, 11.0, 3.1), (1.4, 1.4, 1.6)),       # голова
        ("c", (0, 11.4, 3.8), (0, 13.6, 5.0), 0.65),  # морда вверх
        ("c", (-0.8, 11.8, 2.4), (-0.9, 13.0, 2.0), 0.35),  # уши
        ("c", (0.8, 11.8, 2.4), (0.9, 13.0, 2.0), 0.35),
        ("c", (0, 1.0, -4.5), (2.6, 0.6, -2.0), 0.8),  # хвост вокруг лап
        ("c", (2.6, 0.6, -2.0), (2.6, 0.6, 1.8), 0.7),
    ]
    mats = ["minecraft:stone", "minecraft:andesite", "minecraft:polished_andesite", "minecraft:stone",
            "minecraft:cobblestone", "minecraft:tuff"]
    for x in range(S):
        for z in range(S):
            for y in range(B, H):
                p = ((x - c) / k, (y - B) / k, (z - c) / k)
                hit = False
                for part in parts:
                    if part[0] == "e" and ell(p, part[1], part[2]):
                        hit = True
                        break
                    if part[0] == "c" and capsule(p, part[1], part[2], part[3]):
                        hit = True
                        break
                if hit:
                    t.put(x, y, z, mats[(x * 3 + y * 7 + z * 5 + rng.randint(0, 1)) % len(mats)])
    # глаза-светильники
    ey = B + int(11.3 * k)
    ez = c + int(round(4.2 * k))
    for ex in (c - 1, c + 1):
        for zz in range(ez - 2, ez + 3):
            if (ex, ey, zz) in t.blocks and (ex, ey, zz + 1) not in t.blocks:
                t.put(ex, ey, zz, "minecraft:sea_lantern")
                break
    # надпись-руны на постаменте
    for x in (c - 2, c, c + 2):
        t.put(x, 2, c + 7, e("runa_tasy"), {"lit": "true"})
    for dx, dz in ((-7, -7), (7, -7), (-7, 7), (7, 7)):
        t.put(c + dx, 4, c + dz, "minecraft:lantern", LANTERN)
    return t


# ------------------------------------------------------------------ Отырар
def otrar():
    S = 63
    G = 5          # уровень земли
    H = G + 22
    t = Template(S, H, S)
    rng = random.Random(61)
    c = S // 2

    def brick():
        r = rng.random()
        return "minecraft:mud_bricks" if r < 0.6 else ("minecraft:packed_mud" if r < 0.85 else "minecraft:terracotta")

    def erosion(x, z):
        return (math.sin(x * 0.37) + math.cos(z * 0.29) + math.sin((x - z) * 0.21)) * 1.6

    # земля: песок, глина и щебень
    for x in range(S):
        for z in range(S):
            r = rng.random()
            t.put(x, G, z, "minecraft:sand" if r < 0.35 else ("minecraft:coarse_dirt" if r < 0.7 else
                                                            ("minecraft:gravel" if r < 0.85 else "minecraft:packed_mud")))
    # крепостная стена с башнями
    lo, hi = 3, S - 4
    for x in range(lo, hi + 1):
        for z in range(lo, hi + 1):
            if not (x in (lo, lo + 1, hi - 1, hi) or z in (lo, lo + 1, hi - 1, hi)):
                continue
            top = G + int(6 + erosion(x, z))
            if rng.random() < 0.08:
                top = G + 1
            for y in range(G + 1, top + 1):
                t.put(x, y, z, brick())
    towers = [(lo, lo), (lo, hi), (hi, lo), (hi, hi), (c, lo), (lo, c), (hi, c)]
    for tx, tz in towers:
        top = G + int(9 + erosion(tx, tz) * 1.4)
        for x in range(tx - 3, tx + 4):
            for z in range(tz - 3, tz + 4):
                d = math.hypot(x - tx, z - tz)
                if d <= 3.2:
                    hh = top - int(max(0, d - 1.5) * 1.5) - (rng.random() < 0.2)
                    for y in range(G + 1, hh + 1):
                        t.put(x, y, z, brick())
    # ворота на юг
    for x in range(c - 2, c + 3):
        for z in (hi - 1, hi):
            for y in range(G + 1, G + 6):
                t.put(x, y, z, AIR)
    # дома с подвалами и тайниками
    houses = [(9, 9, 8, 7), (9, 31, 7, 6), (40, 9, 9, 7), (9, 22, 7, 8), (44, 24, 8, 8),
              (10, 40, 9, 7), (24, 44, 7, 7), (42, 42, 9, 8), (30, 30, 7, 6)]
    for i, (hx, hz, w, d) in enumerate(houses):
        for x in range(hx, hx + w):
            for z in range(hz, hz + d):
                edge = x in (hx, hx + w - 1) or z in (hz, hz + d - 1)
                t.put(x, G, z, "minecraft:packed_mud")
                if edge:
                    top = G + max(1, int(2.5 + erosion(x * 1.3, z * 1.3)))
                    for y in range(G + 1, top + 1):
                        t.put(x, y, z, brick())
        # дверной проём
        dx = hx + w // 2
        for y in (G + 1, G + 2):
            t.put(dx, y, hz + d - 1, AIR)
        if i % 2 == 0:
            # подвал
            for x in range(hx + 1, hx + w - 1):
                for z in range(hz + 1, hz + d - 1):
                    for y in range(1, G):
                        edge = x in (hx + 1, hx + w - 2) or z in (hz + 1, hz + d - 2)
                        t.put(x, y, z, brick() if (edge or y == 1) else AIR)
            lx, lz = hx + 2, hz + 2
            t.put(lx, G, lz, AIR)
            for y in range(2, G + 1):
                t.put(lx, y, lz, "minecraft:scaffolding", {"bottom": "false", "distance": "0", "waterlogged": "false"})
            chest(t, hx + w - 3, 2, hz + 2, "chests/otrar_cache", "west")
            t.put(hx + 2, 2, hz + d - 3, "minecraft:barrel", {"facing": "up", "open": "false"})
            t.put(hx + w // 2, 4, hz + d // 2, "minecraft:lantern", HLANTERN)
            # тайник за фальшивой стеной
            sx = hx + w - 2
            t.put(sx, 2, hz + d - 3, AIR)
            chest(t, sx, 2, hz + d - 3, "chests/otrar_cache", "west")
            t.put(sx - 1, 2, hz + d - 3, "minecraft:packed_mud")
            for x in range(hx + 2, hx + w - 2):
                if rng.random() < 0.5:
                    t.put(x, 2, hz + 2, "minecraft:suspicious_gravel", {"dusted": "0"},
                          {"id": (8, "minecraft:brushable_block"), "LootTable": (8, e("archaeology/otrar"))})
    # минарет (обломанный)
    mx, mz = 50, 12
    for y in range(G + 1, G + 19):
        r = 2.4 if y < G + 12 else 1.6
        for x in range(mx - 3, mx + 4):
            for z in range(mz - 3, mz + 4):
                d = math.hypot(x - mx, z - mz)
                if r - 1.0 < d <= r:
                    if y > G + 16 and (x + z) % 2:
                        continue
                    t.put(x, y, z, "minecraft:bricks" if (y // 2) % 3 else "minecraft:terracotta")
                elif d <= r - 1.0:
                    t.put(x, y, z, AIR)
    for y in range(G + 1, G + 17):
        a = y * 0.9
        t.put(mx + round(math.cos(a) * 0.8), y, mz + round(math.sin(a) * 0.8), "minecraft:mud_brick_slab", SLAB_T if y % 2 else SLAB_B)
    # цитадель на холме
    for x in range(c - 8, c + 9):
        for z in range(10, 22):
            for y in range(G + 1, G + 4):
                t.put(x, y, z, "minecraft:packed_mud" if y < G + 3 else "minecraft:coarse_dirt")
            edge = x in (c - 8, c + 8) or z in (10, 21)
            if edge:
                top = G + 4 + max(0, int(2 + erosion(x, z)))
                for y in range(G + 4, top + 1):
                    t.put(x, y, z, brick())
    chest(t, c, G + 4, 12, "chests/otrar_cache")
    # археологические находки на поверхности
    for _ in range(26):
        x, z = rng.randrange(6, S - 6), rng.randrange(6, S - 6)
        if t.blocks.get((x, G + 1, z), (AIR,))[0] == AIR:
            t.put(x, G, z, "minecraft:suspicious_sand", {"dusted": "0"},
                  {"id": (8, "minecraft:brushable_block"), "LootTable": (8, e("archaeology/otrar"))})
    # лагерь разбойников
    bx, bz = 30, 50
    t.put(bx, G, bz, "minecraft:stone_bricks")
    t.put(bx, G + 1, bz, "minecraft:campfire", CAMPFIRE)
    for dx, dz in ((-2, 0), (2, 0), (0, -2)):
        t.put(bx + dx, G + 1, bz + dz, "minecraft:stripped_spruce_log", {"axis": "x" if dz else "z"})
    chest(t, bx + 3, G + 1, bz - 2, "chests/bandit", "west")
    for k, (qx, qz) in enumerate(((bx - 3, bz + 2), (bx + 3, bz + 2), (14, 30), (48, 36), (c, 16), (36, 20))):
        t.entity(qx + 0.5, G + 1 + (3 if k == 4 else 0), qz + 0.5, mob(e("qaraqshy"), Mode=(3, 1)))
    return t


# ------------------------------------------------------------------ лагерь дәу у пещеры
def dau_camp():
    S = 25
    G = 9
    H = G + 10
    t = Template(S, H, S)
    rng = random.Random(71)
    c = S // 2
    # каменная скала с входом в пещеру на севере
    for x in range(S):
        for z in range(0, 11):
            hh = int(7 - abs(x - c) * 0.45 - (10 - z) * 0.15 + rng.random() * 1.5)
            for y in range(G, G + max(0, hh) + 1):
                t.put(x, y, z, "minecraft:stone" if rng.random() < 0.7 else
                      ("minecraft:cobblestone" if rng.random() < 0.6 else "minecraft:mossy_cobblestone"))
    # спуск в пещеру
    for k in range(0, 9):
        z = 9 - k
        y = G - k + 1
        for x in range(c - 1, c + 2):
            for yy in range(y, y + 4):
                t.put(x, yy, z, AIR)
            t.put(x, y - 1, z, "minecraft:cobblestone_stairs" if x == c else "minecraft:cobblestone",
                  {"facing": "north", "half": "bottom", "shape": "straight", "waterlogged": "false"} if x == c else None)
    # пещерная камора
    for x in range(c - 4, c + 5):
        for z in range(0, 5):
            for y in range(1, 6):
                edge = abs(x - c) == 4 or z == 0 or y == 5
                t.put(x, y, z, "minecraft:stone" if edge else AIR)
            t.put(x, 0, z, "minecraft:gravel")
    chest(t, c - 2, 1, 1, "chests/dau")
    chest(t, c + 2, 1, 1, "chests/dau")
    for (x, z) in ((c - 3, 3), (c + 3, 3), (c, 1)):
        t.put(x, 1, z, "minecraft:bone_block", {"axis": "y"})
    t.put(c, 4, 2, "minecraft:lantern", HLANTERN)
    # лагерь снаружи
    for x in range(S):
        for z in range(11, S):
            if math.hypot(x - c, z - 18) <= 7:
                t.put(x, G, z, "minecraft:coarse_dirt" if rng.random() < 0.6 else "minecraft:dirt_path")
                for y in range(G + 1, G + 6):
                    t.put(x, y, z, AIR)
    t.put(c, G, 18, "minecraft:magma_block")
    t.put(c, G + 1, 18, "minecraft:campfire", CAMPFIRE)
    t.put(c + 1, G + 1, 18, "minecraft:cauldron")
    for dx, dz, axis in ((-3, 0, "z"), (3, 0, "z"), (0, 3, "x")):
        for k in (-1, 0, 1):
            x = c + dx + (k if axis == "x" else 0)
            z = 18 + dz + (k if axis == "z" else 0)
            t.put(x, G + 1, z, "minecraft:spruce_log", {"axis": axis})
    # рёбра съеденных быков — арки из костяных блоков
    for k in range(3):
        z = 14 + k * 2
        for y in range(G + 1, G + 4):
            t.put(c - 5, y, z, "minecraft:bone_block", {"axis": "y"})
            t.put(c + 5, y, z, "minecraft:bone_block", {"axis": "y"}) if k == 1 else None
        t.put(c - 4, G + 4, z, "minecraft:bone_block", {"axis": "x"})
    for (x, z, r) in ((c - 2, 22, "4"), (c + 3, 21, "12"), (c + 5, 16, "8")):
        t.put(x, G + 1, z, "minecraft:skeleton_skull", {"rotation": r, "powered": "false"})
    t.put(c + 4, G + 1, 22, "minecraft:hay_block", {"axis": "y"})
    t.entity(c + 2.5, G + 1, 20.5, mob(e("dau")))
    return t


def build_all():
    return {
        "caravanserai/intact": caravanserai(False),
        "caravanserai/ruined": caravanserai(True),
        "bori_musini": wolf_statue(),
        "otyrar": otrar(),
        "dau_mekeni": dau_camp(),
    }
