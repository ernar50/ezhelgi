"""Builds yurts, auls (villages), camps and the golden warrior burial as templates.

Coordinates: x, z horizontal, y up. y = 0 is the floor (it replaces the ground).
Every yurt is built with its door facing south (+z); its center is at (c, c)
where c = size_x // 2, which KiizUyItem relies on.
"""
import math
import random

NS = "ezhelgi"
FACINGS = ["north", "east", "south", "west"]  # clockwise order
DIR = {"north": (0, -1), "south": (0, 1), "east": (1, 0), "west": (-1, 0)}
AIR = "minecraft:air"


def e(n):
    return f"{NS}:{n}"


class Template:
    def __init__(self, sx, sy, sz):
        self.size = [sx, sy, sz]
        self.blocks = {}
        self.entities = []  # (x, y, z, nbt-dict)

    def put(self, x, y, z, name, props=None, nbt=None):
        if 0 <= x < self.size[0] and 0 <= y < self.size[1] and 0 <= z < self.size[2]:
            self.blocks[(x, y, z)] = (name, dict(props or {}), nbt)

    def entity(self, x, y, z, nbt):
        self.entities.append((x, y, z, nbt))

    def _rot(self, x, z, sx, sz, rot):
        for _ in range(rot % 4):
            x, z = sz - 1 - z, x
            sx, sz = sz, sx
        return x, z

    def paste(self, other, ox, oy, oz, rot=0):
        """Copy another template rotated clockwise rot*90 degrees, origin = its min corner."""
        sx, sz = other.size[0], other.size[2]
        for (x, y, z), (name, props, nbt) in other.blocks.items():
            nx, nz = self._rot(x, z, sx, sz, rot)
            props = dict(props)
            if "facing" in props and props["facing"] in FACINGS:
                props["facing"] = FACINGS[(FACINGS.index(props["facing"]) + rot) % 4]
            self.put(ox + nx, oy + y, oz + nz, name, props, nbt)
        for (x, y, z, nbt) in other.entities:
            nx, nz = self._rot(int(x), int(z), sx, sz, rot)
            self.entity(ox + nx + 0.5, oy + y, oz + nz + 0.5, nbt)


def stairs(name, facing, half="bottom"):
    return name, {"facing": facing, "half": half, "shape": "straight", "waterlogged": "false"}


SLAB = {"type": "bottom", "waterlogged": "false"}
FENCE = {"east": "false", "north": "false", "south": "false", "west": "false", "waterlogged": "false"}
LANTERN = {"hanging": "false", "waterlogged": "false"}
CAMPFIRE = {"facing": "south", "lit": "true", "signal_fire": "false", "waterlogged": "false"}

# ------------------------------------------------------------------ стили юрт
STYLES = {
    # белая юрта с деревянными рёбрами (классика)
    "aq": dict(wall=e("kiiz"), wall_low=e("kiiz"), pole=e("kerege"), band=e("baskur"),
               roof=e("kiiz"), roof_stairs=e("kiiz_stairs"), eave=e("kiiz_slab"),
               rib="minecraft:oak_planks", rib_stairs="minecraft:oak_stairs", roof_ring=None,
               crown=e("shanyrak"), door="minecraft:dark_oak_door", floor="minecraft:spruce_planks",
               carpets=[e("ornek"), e("syrmak"), e("kok_syrmak"), e("alasha"), e("tekemet")]),
    # коричневая юрта с белым орнаментом (как на скриншоте)
    "qonyr": dict(wall=e("qonyr_kiiz"), wall_low=e("aq_oiu"), pole=None, band=e("qonyr_oiu"),
                  roof=e("qonyr_kiiz"), roof_stairs=e("qonyr_kiiz_stairs"), eave=e("kiiz_slab"),
                  rib=None, rib_stairs=None, roof_ring=e("qonyr_oiu"),
                  crown=e("shanyrak"), door="minecraft:spruce_door", floor="minecraft:spruce_planks",
                  carpets=[e("syrmak"), e("tekemet"), e("ornek"), e("alasha"), e("tekemet")]),
    # отау — свадебная юрта: белая, красные рёбра
    "otau": dict(wall=e("kiiz"), wall_low=e("aq_oiu"), pole=None, band=e("baskur"),
                 roof=e("kiiz"), roof_stairs=e("kiiz_stairs"), eave=e("kiiz_slab"),
                 rib="minecraft:mangrove_planks", rib_stairs="minecraft:mangrove_stairs", roof_ring=None,
                 crown=e("shanyrak"), door="minecraft:mangrove_door", floor="minecraft:birch_planks",
                 carpets=[e("syrmak"), e("syrmak"), e("alasha"), e("syrmak"), e("ornek")]),
    # хан ордасы — огромная белая юрта с золотом
    "han": dict(wall=e("kiiz"), wall_low=e("aq_oiu"), pole="minecraft:gold_block", band=e("baskur"),
                roof=e("kiiz"), roof_stairs=e("kiiz_stairs"), eave=e("kiiz_slab"),
                rib="minecraft:dark_oak_planks", rib_stairs="minecraft:dark_oak_stairs", roof_ring=e("baskur"),
                crown=e("shanyrak"), door="minecraft:dark_oak_door", floor="minecraft:dark_oak_planks",
                carpets=[e("ornek"), e("ornek"), e("syrmak"), e("kok_syrmak"), e("alasha")]),
    # қос — маленькая походная коричневая юрта
    "qos": dict(wall=e("qonyr_kiiz"), wall_low=e("qonyr_kiiz"), pole=e("kerege"), band=e("qonyr_oiu"),
                roof=e("qonyr_kiiz"), roof_stairs=e("qonyr_kiiz_stairs"), eave=None,
                rib="minecraft:spruce_planks", rib_stairs="minecraft:spruce_stairs", roof_ring=None,
                crown=e("shanyrak"), door="minecraft:spruce_door", floor="minecraft:spruce_planks",
                carpets=[e("tekemet"), e("tekemet"), e("alasha"), e("tekemet"), e("tekemet")]),
}


def build_yurt(R, wall_h, slope, path_len, seed, style="aq", loot=e("chests/yurt"), decor=True):
    S = STYLES[style]
    rng = random.Random(seed)
    c = int(math.ceil(R)) + 2
    N = 2 * c + 1
    roof_peak = wall_h + 1 + int(math.floor((R + 0.5) * slope)) + 2
    t = Template(N, roof_peak + 3, N + path_len + 1)

    def rad(x, z):
        return math.hypot(x - c, z - c)

    def inside(x, z):
        return rad(x, z) <= R + 0.5

    def is_wall(x, z):
        if not inside(x, z):
            return False
        return any(not inside(x + dx, z + dz) for dx, dz in DIR.values())

    def roof_h(x, z):
        return wall_h + 1 + int(math.floor((R + 0.5 - rad(x, z)) * slope))

    def is_rib(x, z):
        if S["rib"] is None:
            return False
        dx, dz = x - c, z - c
        return dx == 0 or dz == 0 or abs(dx) == abs(dz)

    door_z = max(z for z in range(N) if is_wall(c, z))
    peak = max(roof_h(x, z) for x in range(N) for z in range(N) if inside(x, z))
    ring_h = wall_h + 1 + int(math.floor((R + 0.5) * slope * 0.5))

    for x in range(N):
        for z in range(N):
            if not inside(x, z):
                r = rad(x, z)
                if S["eave"] and r <= R + 1.5:
                    t.put(x, wall_h + 1, z, S["eave"], SLAB)
                continue
            t.put(x, 0, z, S["floor"])
            wall = is_wall(x, z)
            for y in range(1, roof_h(x, z)):
                t.put(x, y, z, AIR)
            if wall:
                ang = math.degrees(math.atan2(z - c, x - c)) % 30
                pole = S["pole"] and (ang < 6 or ang > 24)
                for y in range(1, wall_h + 1):
                    if y == wall_h:
                        t.put(x, y, z, S["band"])
                    elif pole:
                        t.put(x, y, z, S["pole"])
                    elif y == 1:
                        t.put(x, y, z, S["wall_low"])
                    else:
                        t.put(x, y, z, S["wall"])
            # крыша
            h = roof_h(x, z)
            r = rad(x, z)
            if r <= 1.6:
                t.put(x, peak, z, S["crown"])
                for y in range(h, peak):
                    t.put(x, y, z, AIR)
                continue
            best = None
            for name, (dx, dz) in DIR.items():
                nx, nz = x + dx, z + dz
                if inside(nx, nz) and (best is None or rad(nx, nz) < rad(*best[1])):
                    best = (name, (nx, nz))
            higher = best and (rad(*best[1]) <= 1.6 or roof_h(*best[1]) > h)
            rib = is_rib(x, z)
            if S["roof_ring"] and h == ring_h and not higher:
                t.put(x, h, z, S["roof_ring"])
            elif higher:
                name, props = stairs(S["rib_stairs"] if rib else S["roof_stairs"], best[0])
                t.put(x, h, z, name, props)
            else:
                t.put(x, h, z, S["rib"] if rib else S["roof"])

    for x in range(N):
        for z in range(N):
            r = rad(x, z)
            if 1.6 < r <= 2.6 and inside(x, z):
                for y in range(roof_h(x, z) + 1, peak):
                    t.put(x, y, z, S["roof"])
    t.put(c, peak + 1, c, "minecraft:gold_block" if style == "han" else "minecraft:spruce_slab",
          None if style == "han" else SLAB)

    # дверь
    for y, half in ((1, "lower"), (2, "upper")):
        t.put(c, y, door_z, S["door"],
              {"facing": "north", "half": half, "hinge": "left", "open": "false", "powered": "false"})
    if wall_h >= 4:
        for y in range(3, wall_h):
            t.put(c, y, door_z, S["band"])
    for side in (-1, 1):
        for y in range(1, min(wall_h, 4)):
            t.put(c + side, y, door_z, e("kerege") if style != "han" else "minecraft:gold_block")

    if decor:
        pal = S["carpets"]
        for x in range(N):
            for z in range(N):
                if inside(x, z) and not is_wall(x, z):
                    r = rad(x, z)
                    ang = (math.degrees(math.atan2(z - c, x - c)) + 360) % 360
                    if r <= 1.6:
                        k = pal[0]
                    elif r <= 3.0:
                        k = pal[1]
                    elif r <= R - 1.7:
                        k = pal[2] if int(ang // 45) % 2 == 0 else pal[3]
                    else:
                        k = pal[4]
                    t.put(x, 1, z, k)
        back_z = min(z for z in range(N) if inside(c, z) and not is_wall(c, z))
        t.put(c, 1, back_z, "minecraft:chest", {"facing": "south", "type": "single", "waterlogged": "false"},
              {"id": (8, "minecraft:chest"), "LootTable": (8, loot)})
        t.put(c, peak - 1, c, "minecraft:lantern", {"hanging": "true", "waterlogged": "false"})
        for side in (-1, 1):
            bx = c + side * int(R - 1.5)
            if inside(bx, c) and not is_wall(bx, c) and abs(bx - c) >= 2:
                t.put(bx, 1, c, "minecraft:barrel", {"facing": "up", "open": "false"})
                t.put(bx, 2, c, "minecraft:lantern", LANTERN)
        if style == "han":
            # тақ — трон хана у задней стены и сундуки по бокам
            t.put(c, 1, back_z + 1, "minecraft:dark_oak_stairs", {"facing": "north", "half": "bottom", "shape": "straight", "waterlogged": "false"})
            for side in (-2, 2):
                t.put(c + side, 1, back_z + 1, "minecraft:chest", {"facing": "south", "type": "single", "waterlogged": "false"},
                      {"id": (8, "minecraft:chest"), "LootTable": (8, e("chests/han"))})

    for k in range(1, path_len + 1):
        z = door_z + k
        for dx in (-1, 0, 1):
            t.put(c + dx, 0, z, S["floor"])
            t.put(c + dx, 1, z, e("syrmak") if dx else e("tekemet"))
    if path_len >= 2:
        fz = door_z + path_len
        for side in (-2, 2):
            t.put(c + side, 0, fz, "minecraft:stone_bricks")
            t.put(c + side, 1, fz, "minecraft:stone_bricks")
            t.put(c + side, 2, fz, "minecraft:campfire", CAMPFIRE)
            t.put(c + side, 0, door_z + 1, S["floor"])
            t.put(c + side, 1, door_z + 1, "minecraft:spruce_fence", FENCE)
            t.put(c + side, 2, door_z + 1, "minecraft:lantern", LANTERN)
    return t


# ------------------------------------------------------------------ животные
def horse_nbt(rng, kind="minecraft:horse"):
    nbt = {"id": (8, kind), "PersistenceRequired": (1, 1)}
    if kind == "minecraft:horse":
        nbt["Variant"] = (3, rng.randint(0, 6) | (rng.randint(0, 4) << 8))
    return nbt


def pen(t, x0, z0, w, d, rng, horses=3, tulpar=False):
    """Загон с лошадьми: забор из еловых столбов и сено."""
    for x in range(x0, x0 + w):
        for z in range(z0, z0 + d):
            t.put(x, 0, z, "minecraft:grass_block", {"snowy": "false"})
            edge = x in (x0, x0 + w - 1) or z in (z0, z0 + d - 1)
            if edge:
                t.put(x, 1, z, "minecraft:spruce_fence", {
                    "east": "true" if x < x0 + w - 1 and z in (z0, z0 + d - 1) else "false",
                    "west": "true" if x > x0 and z in (z0, z0 + d - 1) else "false",
                    "north": "true" if z > z0 and x in (x0, x0 + w - 1) else "false",
                    "south": "true" if z < z0 + d - 1 and x in (x0, x0 + w - 1) else "false",
                    "waterlogged": "false"})
            else:
                t.put(x, 1, z, AIR)
                t.put(x, 2, z, AIR)
    gx = x0 + w // 2
    t.put(gx, 1, z0 + d - 1, "minecraft:spruce_fence_gate",
          {"facing": "south", "in_wall": "false", "open": "false", "powered": "false"})
    t.put(x0 + 1, 1, z0 + 1, "minecraft:hay_block", {"axis": "y"})
    t.put(x0 + w - 2, 1, z0 + 1, "minecraft:water_cauldron", {"level": "3"})
    for i in range(horses):
        hx = x0 + 1 + (i * 2) % max(1, w - 2)
        hz = z0 + 2 + (i * 2) // max(1, w - 2)
        t.entity(hx + 0.5, 1, min(hz, z0 + d - 2) + 0.5, horse_nbt(rng))
    if tulpar:
        t.entity(x0 + w - 2.5, 1, z0 + d - 2.5, {"id": (8, e("tulpar")), "PersistenceRequired": (1, 1)})


def plaza(t, cx, cz, radius, rng):
    for x in range(int(cx - radius) - 1, int(cx + radius) + 2):
        for z in range(int(cz - radius) - 1, int(cz + radius) + 2):
            if math.hypot(x - cx, z - cz) <= radius:
                t.put(x, 0, z, "minecraft:dirt_path" if rng.random() < 0.6 else "minecraft:coarse_dirt")
    t.put(cx, 0, cz, "minecraft:stone_bricks")
    t.put(cx, 1, cz, "minecraft:campfire", CAMPFIRE)
    t.put(cx + 1, 1, cz, "minecraft:cauldron")
    for dx, dz in ((-3, 0), (3, 0), (0, 3), (0, -3)):
        t.put(cx + dx, 1, cz + dz, "minecraft:spruce_slab", SLAB)


def place_yurt(t, y, cx, cz, door):
    """Ставит юрту центром в (cx, cz), дверью в сторону door."""
    rot = {"south": 0, "west": 1, "north": 2, "east": 3}[door]
    sx, sz = y.size[0], y.size[2]
    c = sx // 2
    # где окажется центр (c, c) после поворота
    rx, rz = t._rot(c, c, sx, sz, rot)
    t.paste(y, cx - rx, 0, cz - rz, rot)


def yurt(kind, seed):
    if kind == "aq_small":
        return build_yurt(4.0, 3, 0.8, 2, seed, "aq")
    if kind == "aq_big":
        return build_yurt(6.5, 4, 0.75, 3, seed, "aq")
    if kind == "qonyr":
        return build_yurt(6.5, 4, 0.8, 3, seed, "qonyr")
    if kind == "qonyr_small":
        return build_yurt(4.5, 3, 0.85, 2, seed, "qonyr")
    if kind == "otau":
        return build_yurt(4.5, 3, 0.8, 2, seed, "otau")
    if kind == "han":
        return build_yurt(9.5, 5, 0.7, 4, seed, "han")
    if kind == "qos":
        return build_yurt(3.5, 3, 0.55, 0, seed, "qos")
    raise ValueError(kind)


# ------------------------------------------------------------------ аулы
def build_aul_small():
    rng = random.Random(7)
    t = Template(48, 20, 48)
    mid = 24
    plaza(t, mid, mid + 2, 9.5, rng)
    place_yurt(t, yurt("aq_big", 1), mid, 10, "south")
    place_yurt(t, yurt("aq_small", 2), 8, mid + 2, "east")
    place_yurt(t, yurt("qos", 3), 40, mid + 2, "west")
    place_yurt(t, yurt("otau", 4), mid, 40, "north")
    pen(t, 32, 33, 7, 6, rng, horses=2)
    return t


def build_aul_medium():
    rng = random.Random(11)
    t = Template(64, 20, 64)
    mid = 32
    plaza(t, mid, mid, 11.5, rng)
    place_yurt(t, yurt("qonyr", 1), mid, 12, "south")
    place_yurt(t, yurt("aq_big", 2), 12, mid, "east")
    place_yurt(t, yurt("otau", 3), 52, mid - 4, "west")
    place_yurt(t, yurt("aq_small", 4), 16, 52, "north")
    place_yurt(t, yurt("qos", 5), 32, 54, "north")
    place_yurt(t, yurt("qonyr_small", 6), 48, 52, "north")
    pen(t, 44, 6, 10, 8, rng, horses=4)
    return t


def build_aul_large():
    rng = random.Random(23)
    t = Template(88, 26, 88)
    mid = 44
    plaza(t, mid, mid + 4, 13.5, rng)
    for dx in (-8, 8):
        t.put(mid + dx, 0, mid + 4, "minecraft:stone_bricks")
        t.put(mid + dx, 1, mid + 4, "minecraft:campfire", CAMPFIRE)
    place_yurt(t, yurt("han", 1), mid, 16, "south")
    place_yurt(t, yurt("qonyr", 2), 14, 20, "east")
    place_yurt(t, yurt("qonyr", 3), 74, 20, "west")
    place_yurt(t, yurt("aq_big", 4), 13, 50, "east")
    place_yurt(t, yurt("aq_big", 5), 75, 50, "west")
    place_yurt(t, yurt("otau", 6), 26, 74, "north")
    place_yurt(t, yurt("aq_small", 7), 44, 76, "north")
    place_yurt(t, yurt("qonyr_small", 8), 62, 74, "north")
    place_yurt(t, yurt("qos", 9), 8, 76, "east")
    place_yurt(t, yurt("qos", 10), 80, 76, "west")
    pen(t, 30, 54, 10, 8, rng, horses=4)
    pen(t, 50, 54, 10, 8, rng, horses=3, tulpar=True)
    return t


# ------------------------------------------------------------------ стоянки
def build_camp(variant):
    rng = random.Random(100 + variant)
    t = Template(17, 14, 17)
    c = 8
    for x in range(17):
        for z in range(17):
            if math.hypot(x - c, z - c) <= 3.5:
                t.put(x, 0, z, "minecraft:coarse_dirt" if rng.random() < 0.5 else "minecraft:dirt_path")
    t.put(c, 0, c, "minecraft:stone_bricks")
    t.put(c, 1, c, "minecraft:campfire", CAMPFIRE)
    for dx, dz, axis in ((-2, 0, "z"), (2, 0, "z"), (0, 2, "x")):
        t.put(c + dx, 1, c + dz, "minecraft:stripped_spruce_log", {"axis": axis})
    t.put(c + 1, 1, c - 2, "minecraft:chest", {"facing": "south", "type": "single", "waterlogged": "false"},
          {"id": (8, "minecraft:chest"), "LootTable": (8, e("chests/camp"))})
    t.put(c - 1, 1, c - 2, "minecraft:barrel", {"facing": "up", "open": "false"})
    t.put(c + 3, 1, c + 3, "minecraft:spruce_fence", FENCE)
    t.put(c + 3, 2, c + 3, "minecraft:lantern", LANTERN)
    for dx, dz in ((-1, 3), (1, 3), (-3, -1)):
        t.put(c + dx, 1, c + dz, e("tekemet") if rng.random() < 0.5 else e("alasha"))
    if variant == 0:
        place_yurt(t, yurt("qos", 50), c - 4, c - 4, "south")
    t.entity(c + 5.5, 1, c + 1.5, horse_nbt(rng))
    if variant == 1:
        t.entity(c - 4.5, 1, c + 4.5, horse_nbt(rng))
    return t


# ------------------------------------------------------------------ курган золотого воина
def build_burial(boss=False):
    rng = random.Random(77)
    half = 6 if boss else 4          # полуширина камеры
    ih = 7 if boss else 4            # высота камеры внутри
    SURF = ih + 1                    # камера целиком под землёй
    R = 14.5 if boss else 12.5       # радиус насыпи
    S = 33 if boss else 29
    H = SURF + int(R * 0.65) + 4
    t = Template(S, H, S)
    c = S // 2

    def brick():
        r = rng.random()
        return "minecraft:mossy_stone_bricks" if r < 0.3 else ("minecraft:cracked_stone_bricks" if r < 0.45 else "minecraft:stone_bricks")

    # насыпь
    for x in range(S):
        for z in range(S):
            r = math.hypot(x - c, z - c)
            if r > R:
                continue
            top = SURF + int(max(0, (R - r) * 0.65))
            for y in range(SURF, top + 1):
                t.put(x, y, z, "minecraft:grass_block" if y == top else ("minecraft:coarse_dirt" if rng.random() < 0.3 else "minecraft:dirt"),
                      {"snowy": "false"} if y == top else None)
    # каменная камера 9x9 (внутри 7x7), высота 4
    for x in range(c - half, c + half + 1):
        for z in range(c - half, c + half + 1):
            t.put(x, 0, z, "minecraft:polished_andesite" if (x + z) % 2 else "minecraft:smooth_stone")
            t.put(x, SURF, z, brick())
            for y in range(1, ih + 1):
                edge = x in (c - half, c + half) or z in (c - half, c + half)
                t.put(x, y, z, brick() if edge else AIR)
    # золото и саркофаг
    t.put(c, 1, c, "minecraft:gold_block")
    t.put(c, 1, c - 1, "minecraft:gold_block")
    t.put(c, 2, c, e("ornek"))
    t.put(c, 2, c - 1, e("ornek"))
    for dx in (-(half - 1), half - 1):
        for dz in (-(half - 1), half - 1):
            t.put(c + dx, 1, c + dz, "minecraft:gold_block")
            t.put(c + dx, 2, c + dz, "minecraft:soul_lantern", LANTERN)
    t.put(c - half + 1, 1, c, "minecraft:chest", {"facing": "east", "type": "single", "waterlogged": "false"},
          {"id": (8, "minecraft:chest"), "LootTable": (8, e("chests/altyn_qorym"))})
    t.put(c + half - 1, 1, c, "minecraft:chest", {"facing": "west", "type": "single", "waterlogged": "false"},
          {"id": (8, "minecraft:chest"), "LootTable": (8, e("chests/altyn_qorym"))})
    t.put(c, 1, c - half + 1, "minecraft:skeleton_skull", {"rotation": "0", "powered": "false"})
    # коридор-дромос на юг с лестницей вверх
    for k in range(half + 1, int(R) + 2):
        z = c + k
        floor = min(k - (half + 1), SURF - 1)
        for x in (c - 1, c, c + 1):
            for y in range(floor, floor + 4):
                if x == c and floor < y <= floor + 2:
                    t.put(x, y, z, AIR)
                else:
                    t.put(x, y, z, brick())
        if 0 < k - (half + 1) <= SURF - 1:
            t.put(c, floor, z, "minecraft:stone_brick_stairs", {"facing": "south", "half": "bottom", "shape": "straight", "waterlogged": "false"})
    for y in (1, 2):
        t.put(c, y, c + half, "minecraft:iron_bars", {"east": "true", "west": "true", "north": "false", "south": "false", "waterlogged": "false"})
    # балбалы и кольцо камней
    for ang in range(0, 360, 30):
        x = round(c + (R + 0.9) * math.cos(math.radians(ang)))
        z = round(c + (R + 0.9) * math.sin(math.radians(ang)))
        if 0 <= x < S and 0 <= z < S:
            t.put(x, SURF, z, "minecraft:mossy_cobblestone")
            if ang % 90 == 0:
                t.put(x, SURF + 1, z, "minecraft:chiseled_stone_bricks")
    top_y = SURF + int(R * 0.65) + 1
    t.put(c, top_y, c, "minecraft:chiseled_stone_bricks")
    t.put(c, top_y + 1, c, "minecraft:gold_block")
    # стражи: двое пеших внутри, один конный снаружи
    warrior = lambda mode: {"id": (8, e("altyn_jauynger")), "PersistenceRequired": (1, 1), "Mode": (3, mode)}
    t.entity(c - 2 + 0.5, 1, c + 2 + 0.5, warrior(1))
    t.entity(c + 2 + 0.5, 1, c + 2 + 0.5, warrior(1))
    t.entity(c + 3.5, SURF, S - 1.5, warrior(2))
    if boss:
        # Алтын Адам ждёт у саркофага; вокруг факелы душ и золото
        t.entity(c + 0.5, 1, c - 3 + 0.5, {"id": (8, e("altyn_adam")), "PersistenceRequired": (1, 1)})
        for dx in (-3, 3):
            for y in range(1, 4):
                t.put(c + dx, y, c - half + 1, "minecraft:gold_block" if y < 3 else "minecraft:soul_lantern",
                      None if y < 3 else LANTERN)
    return t
