"""v8 worldgen: город Туркестан, Чарынский каньон, скала Окжетпес, пещера Ақмешіт,
древние медные рудники, пещера отшельника и подземный мир Ер Төстіка с Шойынқұлақ.
"""
import math
import random

from yurts import Template

NS = "ezhelgi"
AIR = "minecraft:air"
SLAB_B = {"type": "bottom", "waterlogged": "false"}
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


def barrel(t, x, y, z, loot=None):
    nbt = {"id": (8, "minecraft:barrel")}
    if loot:
        nbt["LootTable"] = (8, e(loot))
    t.put(x, y, z, "minecraft:barrel", {"facing": "up", "open": "false"}, nbt)


def mob(nbt_id, **extra):
    d = {"id": (8, nbt_id), "PersistenceRequired": (1, 1)}
    d.update(extra)
    return d


def lectern_book(t, x, y, z, facing, title, author, pages):
    """Пюпитр с книгой: pages — ключи перевода страниц."""
    book = {"id": (8, "minecraft:written_book"), "count": (3, 1), "components": (10, {
        "minecraft:written_book_content": (10, {
            "title": (10, {"raw": (8, title)}),
            "author": (8, author),
            "pages": (9, (10, [{"raw": (10, {"translate": (8, p)})} for p in pages])),
        })})}
    t.put(x, y, z, "minecraft:lectern", {"facing": facing, "has_book": "true", "powered": "false"},
          {"id": (8, "minecraft:lectern"), "Book": (10, book), "Page": (3, 0)})


def tree(t, x, y, z, rng, log="minecraft:oak_log", leaves="minecraft:oak_leaves", h=None):
    h = h or rng.randint(4, 6)
    for k in range(h):
        t.put(x, y + k, z, log, {"axis": "y"})
    for dy in range(h - 2, h + 2):
        r = 2 if dy < h else 1
        for dx in range(-r, r + 1):
            for dz in range(-r, r + 1):
                if abs(dx) + abs(dz) <= r + 1 and (dx, dz) != (0, 0) or dy >= h:
                    if (x + dx, y + dy, z + dz) not in t.blocks or t.blocks[(x + dx, y + dy, z + dz)][0] == AIR:
                        t.put(x + dx, y + dy, z + dz, leaves, {"distance": "1", "persistent": "true", "waterlogged": "false"})


# ------------------------------------------------------------------ Туркестан
def turkistan():
    S, H = 65, 28
    t = Template(S, H, S)
    rng = random.Random(1520)
    brick = lambda: rng.choice(["minecraft:mud_bricks", "minecraft:mud_bricks", "minecraft:packed_mud", "minecraft:bricks"])
    for x in range(S):
        for z in range(S):
            t.put(x, 0, z, "minecraft:packed_mud" if rng.random() < 0.3 else "minecraft:coarse_dirt")
            for y in range(1, H):
                t.put(x, y, z, AIR)
    c = S // 2
    # улицы
    for i in range(3, S - 3):
        for w in range(-2, 3):
            t.put(c + w, 0, i, "minecraft:dirt_path" if rng.random() < 0.7 else "minecraft:gravel")
            t.put(i, 0, c + w, "minecraft:dirt_path" if rng.random() < 0.7 else "minecraft:gravel")
    # крепостные стены с зубцами и башнями
    lo, hi = 1, S - 2
    for a in range(lo, hi + 1):
        for (x, z) in ((a, lo), (a, hi), (lo, a), (hi, a)):
            for y in range(1, 8):
                t.put(x, y, z, brick())
            if a % 2 == 0:
                t.put(x, 8, z, "minecraft:mud_brick_wall", WALL)
    for (tx, tz) in ((lo, lo), (lo, hi), (hi, lo), (hi, hi), (lo, c), (hi, c)):
        for x in range(tx - 3, tx + 4):
            for z in range(tz - 3, tz + 4):
                if math.hypot(x - tx, z - tz) <= 3.2:
                    for y in range(1, 12):
                        t.put(x, y, z, brick() if y % 5 else "minecraft:light_blue_glazed_terracotta")
                    if math.hypot(x - tx, z - tz) > 2.0 and (x + z) % 2 == 0:
                        t.put(x, 12, z, "minecraft:mud_brick_wall", WALL)
    # ворота: юг и север, с изразцовой аркой
    for gz in (lo, hi):
        for x in range(c - 2, c + 3):
            for y in range(1, 6):
                t.put(x, y, gz, AIR)
        for x in range(c - 3, c + 4):
            for y in range(6, 10):
                t.put(x, y, gz, "minecraft:blue_glazed_terracotta" if (x + y) % 2 else "minecraft:light_blue_glazed_terracotta")
    # хауз (водоём) на перекрёстке
    for x in range(c - 2, c + 3):
        for z in range(c - 2, c + 3):
            edge = abs(x - c) == 2 or abs(z - c) == 2
            t.put(x, 0, z, "minecraft:smooth_sandstone")
            t.put(x, 1, z, "minecraft:smooth_sandstone_slab" if edge else "minecraft:water", SLAB_B if edge else None)
    # базар вдоль восточно-западной улицы: прилавки с навесами
    colors = ["red", "yellow", "blue", "green", "orange", "cyan", "purple", "white"]
    k = 0
    for side in (-1, 1):
        for sx in list(range(6, c - 4, 6)) + list(range(c + 6, S - 6, 6)):
            sz = c + side * 5
            col = colors[k % len(colors)]
            k += 1
            for dx in (-1, 1):
                for dz in (-1, 1):
                    for y in range(1, 4):
                        t.put(sx + dx, y, sz + dz, "minecraft:spruce_fence", FENCE)
            for dx in range(-2, 3):
                for dz in range(-2, 3):
                    t.put(sx + dx, 4, sz + dz, f"minecraft:{col}_wool" if (dx + dz) % 2 else "minecraft:white_wool")
            t.put(sx, 1, sz - side, "minecraft:spruce_slab", SLAB_B)
            barrel(t, sx - 1, 1, sz + side, "chests/turkistan_bazaar" if k % 3 == 0 else None)
            t.put(sx + 1, 1, sz + side, e(rng.choice(["syrmak", "kok_syrmak", "alasha", "ornek"])))
            if k % 2 == 0:
                t.entity(sx + 0.5, 1, sz + 0.5, mob(e("keruenshi") if k % 4 == 0 else e("auyldas"), Variant=(3, rng.randrange(20))))
    # мечеть с голубым куполом и минаретом (северо-восток)
    mx, mz, ms = c + 15, c - 15, 13
    for x in range(mx - ms // 2, mx + ms // 2 + 1):
        for z in range(mz - ms // 2, mz + ms // 2 + 1):
            edge = abs(x - mx) == ms // 2 or abs(z - mz) == ms // 2
            for y in range(1, 9):
                if edge:
                    t.put(x, y, z, "minecraft:light_blue_glazed_terracotta" if y == 6 else brick())
            t.put(x, 9, z, "minecraft:mud_brick_slab", SLAB_B)
            if not edge:
                t.put(x, 0, z, "minecraft:smooth_sandstone")
                t.put(x, 1, z, e("kok_syrmak") if (x + z) % 2 else e("syrmak"))
    for y in range(1, 5):
        for dx in (-1, 0, 1):
            t.put(mx + dx, y, mz + ms // 2, AIR)
    for x in range(mx - 5, mx + 6):
        for z in range(mz - 5, mz + 6):
            for y in range(0, 6):
                d = math.sqrt((x - mx) ** 2 + (z - mz) ** 2 + (y * 1.1) ** 2)
                if 4.2 <= d <= 5.3:
                    t.put(x, 10 + y, z, "minecraft:light_blue_glazed_terracotta" if (x + z + y) % 3 else "minecraft:cyan_terracotta")
    t.put(mx, 16, mz, "minecraft:gold_block")
    nx, nz = mx + 7, mz + 7
    for y in range(1, 22):
        for (dx, dz) in ((0, 0), (1, 0), (0, 1), (1, 1)):
            t.put(nx + dx, y, nz + dz, brick() if y % 4 else "minecraft:light_blue_glazed_terracotta")
    for (dx, dz) in ((0, 0), (1, 0), (0, 1), (1, 1)):
        t.put(nx + dx, 22, nz + dz, "minecraft:mud_brick_slab", SLAB_B)
    t.put(nx, 23, nz, "minecraft:lantern", LANTERN)
    t.put(mx, 8, mz, "minecraft:lantern", HLANTERN)
    # дома с плоскими крышами
    houses = [(10, 10), (22, 10), (10, 22), (10, 44), (22, 52), (44, 44), (52, 52), (44, 54), (54, 40)]
    for (hx, hz) in houses:
        w, d = rng.randint(5, 7), rng.randint(5, 7)
        for x in range(hx, hx + w):
            for z in range(hz, hz + d):
                edge = x in (hx, hx + w - 1) or z in (hz, hz + d - 1)
                for y in range(1, 5):
                    if edge:
                        t.put(x, y, z, brick())
                t.put(x, 5, z, "minecraft:mud_brick_slab", SLAB_B)
                if not edge:
                    t.put(x, 0, z, "minecraft:spruce_planks")
        door_x = hx + w // 2
        t.put(door_x, 1, hz + d - 1, "minecraft:spruce_door", {"facing": "south", "half": "lower", "hinge": "left", "open": "false", "powered": "false"})
        t.put(door_x, 2, hz + d - 1, "minecraft:spruce_door", {"facing": "south", "half": "upper", "hinge": "left", "open": "false", "powered": "false"})
        t.put(hx, 3, hz + d // 2, "minecraft:iron_bars")
        t.put(hx + 1, 1, hz + 1, "minecraft:red_bed", {"facing": "south", "occupied": "false", "part": "head"})
        t.put(hx + 1, 1, hz + 2, "minecraft:red_bed", {"facing": "south", "occupied": "false", "part": "foot"})
        t.put(hx + w - 2, 1, hz + 1, "minecraft:furnace", {"facing": "south", "lit": "false"})
        t.put(hx + w - 2, 3, hz + 1, "minecraft:lantern", HLANTERN)
        t.entity(hx + w / 2, 1, hz + d + 1.5, mob(e("auyldas"), Variant=(3, rng.randrange(20)), MaybeNotable=(1, 1)))
    # сады с деревьями
    for (gx, gz) in ((44, 10), (54, 22), (14, 34)):
        for x in range(gx, gx + 7):
            for z in range(gz, gz + 7):
                t.put(x, 0, z, "minecraft:grass_block", {"snowy": "false"})
        tree(t, gx + 3, 1, gz + 3, rng)
    t.entity(c + 3.5, 1, c + 3.5, mob(e("aqsaqal")))
    t.entity(c - 3.5, 1, c + 3.5, mob(e("kuishi"), Variant=(3, 4)))
    t.entity(c - 3.5, 1, c - 3.5, mob(e("zhyrshy")))
    return t


# ------------------------------------------------------------------ Чарынский каньон
def sharyn():
    S, H = 64, 34
    t = Template(S, H, S)
    rng = random.Random(1964)
    layers = ["minecraft:red_terracotta", "minecraft:orange_terracotta", "minecraft:terracotta", "minecraft:red_sandstone",
              "minecraft:smooth_red_sandstone", "minecraft:brown_terracotta", "minecraft:red_terracotta"]

    def gorge(z):
        return S / 2 + 8 * math.sin(z * 0.11) + 3 * math.sin(z * 0.31)

    for x in range(S):
        for z in range(S):
            gx = gorge(z)
            d = abs(x - gx)
            width = 5 + 2 * math.sin(z * 0.2)
            if d < width:
                top = 1
            else:
                top = int(min(26, 6 + (d - width) * 2.5 + 3 * math.sin(x * 0.4 + z * 0.3)))
                top = max(2, top)
                top = top - (top % 3) + 1  # уступы-террасы
            for y in range(0, top):
                t.put(x, y, z, layers[(y // 2 + int(x * 0.05)) % len(layers)])
            for y in range(top, H):
                t.put(x, y, z, AIR)
            if d < 1.6:
                t.put(x, 0, z, "minecraft:water")  # река Чарын
            elif d < width and rng.random() < 0.05:
                t.put(x, 1, z, "minecraft:short_grass")
    # «Долина замков» — столбы-останцы в каньоне
    for (px, pz, ph) in ((S // 2 + 4, 14, 14), (S // 2 - 6, 30, 18), (S // 2 + 6, 46, 12), (S // 2 - 3, 56, 16)):
        gx = int(gorge(pz))
        px = gx + (px - S // 2)
        for x in range(px - 2, px + 3):
            for z in range(pz - 2, pz + 3):
                if abs(x - px) + abs(z - pz) <= 3:
                    for y in range(1, ph - abs(x - px) - abs(z - pz)):
                        t.put(x, y, z, layers[(y // 2) % len(layers)])
    # ясени у реки
    for z in (8, 22, 36, 50):
        gx = int(gorge(z))
        tree(t, gx + 3, 1, z, rng, "minecraft:birch_log", "minecraft:birch_leaves", 5)
    # тайная пещера в стене каньона
    cz = 26
    gx = int(gorge(cz))
    wall_x = gx + 7
    for k in range(0, 12):
        x = wall_x + k
        for z in range(cz - 1, cz + 2):
            for y in range(1, 4):
                t.put(x, y, z, AIR)
    for x in range(wall_x + 10, wall_x + 17):
        for z in range(cz - 4, cz + 5):
            for y in range(1, 6):
                if 0 <= x < S:
                    t.put(x, y, z, AIR)
    if wall_x + 15 < S:
        chest(t, wall_x + 15, 1, cz, "chests/sharyn_cave", "west")
        t.put(wall_x + 13, 4, cz, "minecraft:lantern", HLANTERN)
        t.put(wall_x + 15, 1, cz + 2, "minecraft:skeleton_skull", {"rotation": "4", "powered": "false"})
    # редкие звери на уступах
    for (ax, az, kind) in ((gx - 10, 10, "arqar"), (gx - 12, 40, "tau_teke"), (gx + 12, 52, "tau_teke"), (gx + 9, 6, "manul")):
        if 0 <= ax < S:
            top = 1
            while (ax, top, az) in t.blocks and t.blocks[(ax, top, az)][0] != AIR and top < H - 1:
                top += 1
            t.entity(ax + 0.5, top, az + 0.5, mob(e(kind)))
    return t


# ------------------------------------------------------------------ Окжетпес
def oqzhetpes():
    S, H = 49, 44
    t = Template(S, H, S)
    rng = random.Random(1888)
    c = S // 2
    for x in range(S):
        for z in range(S):
            r = math.hypot(x - c, z - c)
            for y in range(1, H):
                t.put(x, y, z, AIR)
            if r < 18:
                depth = int(4 * (1 - (r / 18) ** 2)) + 1
                for y in range(0, 4):
                    t.put(x, y, z, "minecraft:water" if y >= 4 - depth else "minecraft:gravel")
                t.put(x, 0, z, "minecraft:gravel")
            elif r < 21:
                t.put(x, 3, z, "minecraft:sand")
                for y in range(0, 3):
                    t.put(x, y, z, "minecraft:sandstone")
            else:
                for y in range(0, 3):
                    t.put(x, y, z, "minecraft:dirt")
                t.put(x, 3, z, "minecraft:grass_block", {"snowy": "false"})
    # скала Окжетпес: груда округлых гранитных глыб
    rock = lambda: rng.choice(["minecraft:granite", "minecraft:granite", "minecraft:polished_granite", "minecraft:andesite", "minecraft:stone"])
    for y in range(0, 36):
        rad = 6.5 - y * 0.14 + 0.8 * math.sin(y * 0.7)
        for x in range(c - 8, c + 9):
            for z in range(c - 8, c + 9):
                if math.hypot(x - c, (z - c) * 1.1) <= rad:
                    t.put(x, y, z, rock())
    # узкая тропа-уступ по спирали к вершине
    yf, a = 4.0, 0.0
    while yf < 35:
        y = int(yf)
        rad = 6.5 - y * 0.14 + 0.8 * math.sin(y * 0.7) + 1.2
        x, z = int(round(c + math.cos(a) * rad)), int(round(c + math.sin(a) * rad))
        t.put(x, y - 1, z, "minecraft:cobblestone")
        for k in range(3):
            t.put(x, y + k, z, AIR)
        a += 0.8 / rad
        yf += 0.45
    # вершина, до которой «стрела не долетает»
    top = 36
    for x in range(c - 2, c + 3):
        for z in range(c - 2, c + 3):
            t.put(x, top - 1, z, "minecraft:polished_granite")
    chest(t, c, top, c, "chests/oqzhetpes_top", "south")
    t.put(c + 1, top, c, "minecraft:white_banner", {"rotation": "0"})
    # Жұмбақтас — загадочный камень-сфинкс в воде
    jx, jz = c + 12, c - 6
    for y in range(0, 7):
        for x in range(jx - 3, jx + 4):
            for z in range(jz - 2, jz + 3):
                if (x - jx) ** 2 / 9.0 + (z - jz) ** 2 / 4.0 + (y / 6.0) ** 2 <= 1:
                    t.put(x, y + 1, z, "minecraft:granite" if (x + y) % 3 else "minecraft:stone")
    # берег: сосны, скамья и пюпитр с легендой
    for (px, pz) in ((4, 6), (8, 42), (42, 44), (45, 10), (3, 26), (24, 46)):
        tree(t, px, 4, pz, rng, "minecraft:spruce_log", "minecraft:spruce_leaves", 7)
    bx, bz = c, c + 21
    lectern_book(t, bx, 4, bz, "north", "Ақжетпес аңызы", "Жыршы",
                 ["legend.ezhelgi.oqzhetpes.%d" % i for i in range(4)])
    t.put(bx - 2, 4, bz, "minecraft:spruce_slab", SLAB_B)
    t.put(bx + 2, 4, bz, "minecraft:spruce_slab", SLAB_B)
    return t


# ------------------------------------------------------------------ пещера Ақмешіт
def aqmeshit():
    S, H = 45, 36
    t = Template(S, H, S)
    rng = random.Random(1111)
    c, cy = S // 2, 12
    rx, ry = 19, 13
    for x in range(S):
        for z in range(S):
            for y in range(0, H):
                d = ((x - c) / rx) ** 2 + ((z - c) / rx) ** 2 + ((y - cy) / ry) ** 2
                hole = math.hypot(x - c, z - c) < 5.5 and y > cy
                if d <= 1.0 or hole:
                    t.put(x, y, z, AIR)
                elif d <= 1.35:
                    t.put(x, y, z, rng.choice(["minecraft:stone", "minecraft:tuff", "minecraft:calcite", "minecraft:stone",
                                               "minecraft:dripstone_block"]))
    # пол оазиса: земля, трава, мох
    for x in range(S):
        for z in range(S):
            for y in range(0, cy):
                if (x, y, z) in t.blocks and t.blocks[(x, y, z)][0] == AIR and (x, y - 1, z) in t.blocks \
                        and t.blocks[(x, y - 1, z)][0] != AIR:
                    lit = math.hypot(x - c, z - c) < 11
                    t.put(x, y - 1, z, "minecraft:grass_block" if lit else "minecraft:moss_block", {"snowy": "false"} if lit else None)
                    if rng.random() < 0.18:
                        t.put(x, y, z, rng.choice(["minecraft:short_grass", "minecraft:fern", "minecraft:poppy", "minecraft:dandelion",
                                                   "minecraft:moss_carpet"]))
                    break
    def ground(x, z):
        y = 1
        while y < H - 1 and not (t.blocks.get((x, y, z), (AIR,))[0] in (AIR, "minecraft:short_grass", "minecraft:fern",
                                                                           "minecraft:poppy", "minecraft:dandelion", "minecraft:moss_carpet")
                                 and t.blocks.get((x, y - 1, z), (AIR,))[0] not in (AIR,)):
            y += 1
        return y
    floor = ground(c, c)
    # деревья под световым колодцем
    for (dx, dz) in ((-4, 2), (3, -4), (5, 4), (-2, -6)):
        tree(t, c + dx, ground(c + dx, c + dz), c + dz, rng, h=rng.randint(6, 8))
    # родник и ручей
    sx, sz = c - 8, c + 7
    for x in range(sx - 2, sx + 3):
        for z in range(sz - 2, sz + 3):
            if math.hypot(x - sx, z - sz) <= 2.2:
                t.put(x, ground(x, z) - 1, z, "minecraft:water")
    # светящиеся кристаллы в стенах
    for _ in range(26):
        a, b = rng.uniform(0, 2 * math.pi), rng.uniform(-0.6, 0.9)
        x = int(c + math.cos(a) * rx * math.cos(b) * 1.02)
        z = int(c + math.sin(a) * rx * math.cos(b) * 1.02)
        y = int(cy + ry * math.sin(b) * 1.02)
        if 0 <= x < S and 0 <= z < S and 0 <= y < H:
            t.put(x, y, z, rng.choice(["minecraft:ochre_froglight", "minecraft:verdant_froglight", "minecraft:amethyst_block"]))
    # обитатели: Шахмаран у родника, редкие звери
    for (x, z, kind) in ((sx + 3, sz, "shahmaran"), (c + 6, c - 3, "manul"), (c - 3, c - 8, "qosayaq"), (c + 2, c + 8, "arqar")):
        t.entity(x + 0.5, ground(x, z), z + 0.5, mob(e(kind)))
    return t


# ------------------------------------------------------------------ древние медные рудники
def kone_ken():
    S, H = 45, 12
    t = Template(S, H, S)
    rng = random.Random(1300)
    rock = lambda: rng.choice(["minecraft:stone", "minecraft:stone", "minecraft:andesite", "minecraft:tuff"])

    def tunnel(x0, z0, x1, z1):
        n = max(abs(x1 - x0), abs(z1 - z0))
        for i in range(n + 1):
            x = round(x0 + (x1 - x0) * i / n)
            z = round(z0 + (z1 - z0) * i / n)
            along_x = abs(x1 - x0) >= abs(z1 - z0)
            for w in (-1, 0, 1):
                xx, zz = (x, z + w) if along_x else (x + w, z)
                t.put(xx, 0, zz, "minecraft:gravel" if rng.random() < 0.3 else rock())
                for y in range(1, 4):
                    t.put(xx, y, zz, AIR)
                t.put(xx, 4, zz, rock())
            # стенки с медной рудой
            for w in (-2, 2):
                xx, zz = (x, z + w) if along_x else (x + w, z)
                for y in range(1, 4):
                    v = rng.random()
                    t.put(xx, y, zz, "minecraft:copper_ore" if v < 0.18 else ("minecraft:raw_copper_block" if v < 0.22 else rock()))
            # деревянные крепи
            if i % 4 == 0:
                for w in (-1, 1):
                    xx, zz = (x, z + w) if along_x else (x + w, z)
                    for y in range(1, 3):
                        t.put(xx, y, zz, "minecraft:spruce_fence", FENCE)
                for w in (-1, 0, 1):
                    xx, zz = (x, z + w) if along_x else (x + w, z)
                    t.put(xx, 3, zz, "minecraft:spruce_planks")
    tunnel(2, 22, 42, 22)
    for bx in (8, 20, 32):
        tunnel(bx, 4, bx, 40)
    # плавильня — большая выработка
    for x in range(16, 27):
        for z in range(26, 35):
            for y in range(1, 6):
                t.put(x, y, z, AIR)
            t.put(x, 0, z, "minecraft:packed_mud")
            t.put(x, 6, z, rock())
    for (fx, fz) in ((18, 33), (21, 33), (24, 33)):
        t.put(fx, 1, fz, "minecraft:blast_furnace", {"facing": "north", "lit": "false"})
        t.put(fx, 2, fz, "minecraft:mud_bricks")
    t.put(21, 1, 29, "minecraft:campfire", CAMPFIRE)
    t.put(19, 1, 28, "minecraft:anvil", {"facing": "east"})
    chest(t, 25, 1, 27, "chests/kone_ken", "west")
    chest(t, 17, 1, 27, "chests/kone_ken", "east")
    for (lx, lz) in ((18, 30), (24, 30), (8, 22), (32, 22), (20, 10), (8, 38)):
        t.put(lx, 3 if (lx, lz) not in ((18, 30), (24, 30)) else 5, lz, "minecraft:lantern", HLANTERN)
    # тупики со старыми сундуками
    chest(t, 8, 1, 5, "chests/kone_ken", "south")
    chest(t, 32, 1, 39, "chests/kone_ken", "north")
    # спящий пещерный дәу в дальней выработке
    for x in range(36, 44):
        for z in range(18, 27):
            for y in range(1, 7):
                t.put(x, y, z, AIR)
            t.put(x, 0, z, "minecraft:gravel")
    t.entity(40.5, 1, 22.5, mob(e("dau"), Sleeping=(1, 1)))
    for (x, z) in ((38, 19), (42, 25)):
        t.put(x, 1, z, "minecraft:bone_block", {"axis": "y"})
    return t


# ------------------------------------------------------------------ пещера отшельника
def darwish_ungiri():
    S, H = 19, 11
    t = Template(S, H, S)
    rng = random.Random(777)
    c = S // 2
    for x in range(S):
        for z in range(S):
            for y in range(0, H):
                d = ((x - c) / 8.5) ** 2 + ((z - c) / 8.5) ** 2 + (y / 9.5) ** 2
                if d <= 0.7 and y >= 1:
                    t.put(x, y, z, AIR)
                elif d <= 1.0:
                    t.put(x, y, z, rng.choice(["minecraft:stone", "minecraft:mossy_cobblestone", "minecraft:andesite", "minecraft:stone"]))
    for x in range(S):
        for z in range(S):
            if ((x - c) / 8.5) ** 2 + ((z - c) / 8.5) ** 2 <= 0.7:
                t.put(x, 0, z, "minecraft:packed_mud")
    # вход с юга
    for z in range(c + 4, S):
        for y in range(1, 4):
            for x in (c - 1, c, c + 1):
                t.put(x, y, z, AIR)
    # келья: войлок, книги, лампады, пюпитр
    for x in range(c - 2, c + 3):
        t.put(x, 1, c - 4, "minecraft:bookshelf")
        t.put(x, 2, c - 4, "minecraft:chiseled_bookshelf")
    t.put(c - 4, 1, c, e("tekemet"))
    t.put(c - 4, 1, c + 1, e("tekemet"))
    t.put(c + 3, 1, c - 2, "minecraft:lectern", {"facing": "west", "has_book": "false", "powered": "false"})
    chest(t, c + 3, 1, c + 1, "chests/darwish", "west")
    t.put(c, 1, c, "minecraft:candle", {"candles": "4", "lit": "true", "waterlogged": "false"})
    t.put(c - 3, 1, c - 3, "minecraft:candle", {"candles": "2", "lit": "true", "waterlogged": "false"})
    t.put(c + 3, 1, c - 3, "minecraft:candle", {"candles": "3", "lit": "true", "waterlogged": "false"})
    t.put(c, 5, c, "minecraft:soul_lantern", HLANTERN)
    t.put(c - 2, 1, c + 3, "minecraft:water_cauldron", {"level": "3"})
    t.put(c + 2, 1, c + 3, "minecraft:flower_pot")
    t.entity(c + 0.5, 1, c - 1.5, mob(e("darwish")))
    return t


# ------------------------------------------------------------------ колодец и подземный мир Ер Төстіка
def zher_asty():
    S = 57
    CAV = 30         # высота пещеры
    SHAFT = 66       # глубина колодца над пещерой
    H = CAV + SHAFT + 6
    t = Template(S, H, S)
    rng = random.Random(1914)
    c, cy = S // 2, 14
    rx, ry = 26, 14
    deep = lambda: rng.choice(["minecraft:deepslate", "minecraft:deepslate", "minecraft:cobbled_deepslate", "minecraft:tuff",
                               "minecraft:blackstone"])
    # пещера-царство
    for x in range(S):
        for z in range(S):
            for y in range(0, CAV):
                d = ((x - c) / rx) ** 2 + ((z - c) / rx) ** 2 + ((y - cy) / ry) ** 2
                if d <= 1.0 and y >= 2:
                    t.put(x, y, z, AIR)
                elif d <= 1.25 or y < 2:
                    t.put(x, y, z, deep())
    for x in range(S):
        for z in range(S):
            if ((x - c) / rx) ** 2 + ((z - c) / rx) ** 2 <= 0.97:
                t.put(x, 1, z, "minecraft:mycelium" if rng.random() < 0.5 else "minecraft:moss_block")
    # подземная река
    for x in range(2, S - 2):
        rz = c + 9 + int(4 * math.sin(x * 0.18))
        for dz in (-1, 0, 1):
            if ((x - c) / rx) ** 2 + ((rz + dz - c) / rx) ** 2 <= 0.95:
                t.put(x, 1, rz + dz, "minecraft:water")
                t.put(x, 0, rz + dz, "minecraft:gravel")
    # светящиеся грибы-великаны
    for (gx, gz, gh) in ((c - 12, c - 6, 8), (c - 6, c + 2, 6), (c + 10, c - 10, 9), (c + 14, c + 3, 7), (c - 16, c + 6, 7), (c + 4, c - 16, 6)):
        for y in range(2, 2 + gh):
            t.put(gx, y, gz, "minecraft:mushroom_stem", {"down": "true", "east": "true", "north": "true", "south": "true", "up": "true", "west": "true"})
        for dx in range(-3, 4):
            for dz in range(-3, 4):
                if abs(dx) + abs(dz) <= 4:
                    t.put(gx + dx, 2 + gh, gz + dz, "minecraft:shroomlight" if (dx + dz) % 3 == 0 else
                          ("minecraft:red_mushroom_block" if (gx + gz) % 2 else "minecraft:brown_mushroom_block"),
                          None if (dx + dz) % 3 == 0 else {"down": "false", "east": "true", "north": "true", "south": "true", "up": "true", "west": "true"})
    for _ in range(40):
        x, z = rng.randrange(6, S - 6), rng.randrange(6, S - 6)
        if ((x - c) / rx) ** 2 + ((z - c) / rx) ** 2 <= 0.8 and t.blocks.get((x, 2, z), (AIR,))[0] == AIR:
            t.put(x, 2, z, rng.choice(["minecraft:red_mushroom", "minecraft:brown_mushroom"]))
    # замок Шойынқұлақ
    kx, kz, ks = c - 4, c - 14, 15
    for x in range(kx - ks // 2, kx + ks // 2 + 1):
        for z in range(kz - ks // 2, kz + ks // 2 + 1):
            edge = abs(x - kx) == ks // 2 or abs(z - kz) == ks // 2
            for y in range(2, 13):
                if edge:
                    t.put(x, y, z, "minecraft:polished_blackstone_bricks" if (y + x) % 5 else "minecraft:cracked_polished_blackstone_bricks")
                elif y == 2:
                    t.put(x, 1, z, "minecraft:polished_blackstone")
                else:
                    t.put(x, y, z, AIR)
            if edge and (x + z) % 2 == 0:
                t.put(x, 13, z, "minecraft:polished_blackstone_brick_wall", WALL)
    for (tx, tz) in ((kx - ks // 2, kz - ks // 2), (kx + ks // 2, kz - ks // 2), (kx - ks // 2, kz + ks // 2), (kx + ks // 2, kz + ks // 2)):
        for x in range(tx - 2, tx + 3):
            for z in range(tz - 2, tz + 3):
                if math.hypot(x - tx, z - tz) <= 2.2:
                    for y in range(2, 17):
                        t.put(x, y, z, "minecraft:polished_blackstone_bricks")
                    t.put(x, 17, z, "minecraft:shroomlight" if (x, z) == (tx, tz) else "minecraft:blackstone_slab", None if (x, z) == (tx, tz) else SLAB_B)
    for y in range(2, 7):  # ворота
        for x in range(kx - 1, kx + 2):
            t.put(x, y, kz + ks // 2, AIR)
    for x in range(kx - 2, kx + 3):  # трон
        t.put(x, 2, kz - 5, "minecraft:gold_block" if x == kx else "minecraft:polished_blackstone_bricks")
    chest(t, kx - 4, 2, kz - 5, "chests/zher_asty_hoard", "south")
    chest(t, kx + 4, 2, kz - 5, "chests/zher_asty_hoard", "south")
    for (lx, lz) in ((kx - 4, kz), (kx + 4, kz)):
        t.put(lx, 11, lz, "minecraft:soul_lantern", HLANTERN)
    t.entity(kx + 0.5, 2, kz - 1.5, mob(e("shoiynqulaq")))
    t.entity(c + 12.5, 2, c + 1.5, mob(e("dau"), Sleeping=(1, 1)))
    t.entity(c - 15.5, 2, c + 10.5, mob(e("dau"), Sleeping=(1, 1)))
    # глубокий колодец: шахта с лестницей до самой пещеры
    wx, wz = c + 8, c + 14
    for y in range(cy + ry - 2, H - 4):
        for dx in range(-2, 3):
            for dz in range(-2, 3):
                edge = abs(dx) == 2 or abs(dz) == 2
                if edge:
                    t.put(wx + dx, y, wz + dz, "minecraft:cobblestone" if rng.random() < 0.6 else "minecraft:mossy_cobblestone")
                else:
                    t.put(wx + dx, y, wz + dz, AIR)
        t.put(wx, y, wz - 1, "minecraft:ladder", {"facing": "south", "waterlogged": "false"})
    for y in range(2, cy + ry - 2):  # лестница спускается по столбу до дна
        t.put(wx, y, wz - 2, "minecraft:cobblestone")
        t.put(wx, y, wz - 1, "minecraft:ladder", {"facing": "south", "waterlogged": "false"})
    for dx in range(-1, 2):  # вода внизу смягчит падение
        for dz in range(0, 2):
            t.put(wx + dx, 1, wz + dz, "minecraft:water")
    top = H - 4
    for dx in range(-2, 3):  # оголовок колодца на поверхности
        for dz in range(-2, 3):
            if abs(dx) == 2 or abs(dz) == 2:
                t.put(wx + dx, top, wz + dz, "minecraft:stone_bricks")
                t.put(wx + dx, top + 1, wz + dz, "minecraft:stone_brick_wall", WALL) if (dx + dz) % 2 == 0 else None
    for (dx, dz) in ((-2, -2), (2, -2), (-2, 2), (2, 2)):
        for y in range(top + 1, top + 4):
            t.put(wx + dx, y, wz + dz, "minecraft:spruce_fence", FENCE)
    for dx in range(-2, 3):
        for dz in range(-2, 3):
            t.put(wx + dx, top + 4, wz + dz, "minecraft:spruce_slab", SLAB_B)
    t.put(wx, top + 3, wz, "minecraft:iron_chain", {"axis": "y", "waterlogged": "false"})
    return t, wx, wz


def build_all():
    zt, _, _ = zher_asty()
    return {
        "turkistan": turkistan(),
        "sharyn": sharyn(),
        "oqzhetpes": oqzhetpes(),
        "aqmeshit": aqmeshit(),
        "kone_ken": kone_ken(),
        "darwish_ungiri": darwish_ungiri(),
        "zher_asty": zt,
    }


def structure(g, name, biomes, adapt, height, spacing, separation, salt, project=True, monsters=None, step="surface_structures"):
    overrides = {}
    if monsters:
        overrides["monster"] = {"bounding_box": "full", "spawns": [
            {"type": t, "count": {"type": "minecraft:uniform", "min_inclusive": lo, "max_inclusive": hi}, "weight": w}
            for t, w, lo, hi in monsters]}
    data = {"type": "minecraft:jigsaw", "biomes": biomes, "step": step, "spawn_overrides": overrides,
            "terrain_adaptation": adapt, "start_pool": e(name + "/start"), "size": 1, "start_height": height,
            "max_distance_from_center": 116, "use_expansion_hack": False}
    if project:
        data["project_start_to_heightmap"] = "WORLD_SURFACE_WG"
    g.write_json(f"{g.D}/worldgen/structure/{name}.json", data)
    g.write_json(f"{g.D}/worldgen/structure_set/{name}.json", {
        "structures": [{"structure": e(name), "weight": 1}],
        "placement": {"type": "minecraft:random_spread", "spacing": spacing, "separation": separation, "salt": salt}})
    g.write_json(f"{g.D}/worldgen/template_pool/{name}/start.json", {
        "fallback": "minecraft:empty",
        "elements": [{"weight": 1, "element": {"element_type": "minecraft:single_pool_element", "location": e(name),
                                                 "processors": "minecraft:empty", "projection": "rigid"}}]})


def run(g):
    templates = build_all()
    for name, tpl in templates.items():
        g.write_template(name, tpl)
    a = lambda y: {"absolute": y}
    structure(g, "turkistan", "#ezhelgi:turkistan", "beard_box", a(-1), 110, 44, 15200)
    structure(g, "sharyn", "#ezhelgi:sharyn", "beard_thin", a(-2), 90, 36, 19640)
    structure(g, "oqzhetpes", "#ezhelgi:oqzhetpes", "beard_box", a(-4), 100, 40, 18880)
    structure(g, "aqmeshit", "#ezhelgi:aqmeshit", "none", a(-31), 80, 32, 11110)
    structure(g, "kone_ken", "#ezhelgi:kone_ken", "bury",
              {"type": "minecraft:uniform", "min_inclusive": {"absolute": 8}, "max_inclusive": {"absolute": 36}},
              64, 24, 13000, project=False, monsters=[(e("zhylan"), 6, 1, 2), ("minecraft:zombie", 4, 1, 2), (e("zhyn"), 2, 1, 1)],
              step="underground_structures")
    structure(g, "darwish_ungiri", "#ezhelgi:mountains", "beard_thin", a(-1), 72, 28, 7770)
    structure(g, "zher_asty", "#ezhelgi:steppe", "none", a(-(templates["zher_asty"].size[1] - 5)), 110, 44, 19140,
              monsters=[(e("zhyn"), 6, 1, 2), (e("zhylan"), 4, 1, 3)])
