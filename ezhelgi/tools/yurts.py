"""Builds yurt and aul (village) structure templates as block dictionaries.

Coordinates: x, z horizontal, y up. y = 0 is the floor (it replaces the ground).
Every yurt is built with its door facing south (+z); its center is at (c, c)
where c = size_x // 2, which KiizUyItem relies on.
"""
import math
import random

NS = "ezhelgi"
FACINGS = ["north", "east", "south", "west"]  # clockwise order
DIR = {"north": (0, -1), "south": (0, 1), "east": (1, 0), "west": (-1, 0)}


class Template:
    def __init__(self, sx, sy, sz):
        self.size = [sx, sy, sz]
        self.blocks = {}

    def put(self, x, y, z, name, props=None, nbt=None):
        if 0 <= x < self.size[0] and 0 <= y < self.size[1] and 0 <= z < self.size[2]:
            self.blocks[(x, y, z)] = (name, dict(props or {}), nbt)

    def get(self, x, y, z):
        return self.blocks.get((x, y, z))

    def paste(self, other, ox, oy, oz, rot=0):
        """Copy another template rotated clockwise rot*90 degrees, origin = its min corner."""
        for (x, y, z), (name, props, nbt) in other.blocks.items():
            nx, nz = x, z
            sx, sz = other.size[0], other.size[2]
            for _ in range(rot % 4):
                nx, nz = sz - 1 - nz, nx
                sx, sz = sz, sx
            props = dict(props)
            if "facing" in props and props["facing"] in FACINGS:
                props["facing"] = FACINGS[(FACINGS.index(props["facing"]) + rot) % 4]
            self.put(ox + nx, oy + y, oz + nz, name, props, nbt)

def stairs(name, facing, half="bottom"):
    return name, {"facing": facing, "half": half, "shape": "straight", "waterlogged": "false"}


def carpet_of(dx, dz, rad, inner_r):
    if rad <= 1.6:
        return f"{NS}:ornek"
    if rad <= 3.0:
        return f"{NS}:syrmak"
    ang = (math.degrees(math.atan2(dz, dx)) + 360) % 360
    if rad <= inner_r - 1.2:
        return f"{NS}:kok_syrmak" if int(ang // 45) % 2 == 0 else f"{NS}:alasha"
    return f"{NS}:tekemet"


def build_yurt(R, wall_h, slope, path_len, seed, loot=f"{NS}:chests/yurt", decor=True):
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
        dx, dz = x - c, z - c
        return dx == 0 or dz == 0 or abs(dx) == abs(dz)

    door_z = max(z for z in range(N) if is_wall(c, z))
    peak = max(roof_h(x, z) for x in range(N) for z in range(N) if inside(x, z))

    for x in range(N):
        for z in range(N):
            if not inside(x, z):
                # карниз (свес крыши) вокруг стены
                r = rad(x, z)
                if r <= R + 1.5:
                    t.put(x, wall_h + 1, z, f"{NS}:kiiz_slab", {"type": "bottom", "waterlogged": "false"})
                continue
            t.put(x, 0, z, "minecraft:spruce_planks")
            wall = is_wall(x, z)
            for y in range(1, roof_h(x, z)):
                t.put(x, y, z, "minecraft:air")
            if wall:
                ang = math.degrees(math.atan2(z - c, x - c)) % 30
                pole = ang < 6 or ang > 24
                for y in range(1, wall_h + 1):
                    if y == wall_h:
                        t.put(x, y, z, f"{NS}:baskur")
                    elif pole:
                        t.put(x, y, z, f"{NS}:kerege")
                    else:
                        t.put(x, y, z, f"{NS}:kiiz")
            # крыша
            h = roof_h(x, z)
            r = rad(x, z)
            if r <= 1.6:
                t.put(x, peak, z, f"{NS}:shanyrak")
                for y in range(h, peak):
                    t.put(x, y, z, "minecraft:air")
                continue
            # сосед ближе к центру
            best = None
            for name, (dx, dz) in DIR.items():
                nx, nz = x + dx, z + dz
                if inside(nx, nz) and (best is None or rad(nx, nz) < rad(*best[1])):
                    best = (name, (nx, nz))
            higher = best and (rad(*best[1]) <= 1.6 or roof_h(*best[1]) > h)
            rib = is_rib(x, z)
            if higher:
                block = "minecraft:oak_stairs" if rib else f"{NS}:kiiz_stairs"
                name, props = stairs(block, best[0])
                t.put(x, h, z, name, props)
            else:
                t.put(x, h, z, "minecraft:oak_planks" if rib else f"{NS}:kiiz")

    # шаңырақ поднят над крышей: кольцо из ступеней вокруг
    for x in range(N):
        for z in range(N):
            r = rad(x, z)
            if 1.6 < r <= 2.6 and inside(x, z):
                for y in range(roof_h(x, z) + 1, peak):
                    t.put(x, y, z, f"{NS}:kiiz")
    t.put(c, peak + 1, c, "minecraft:spruce_slab", {"type": "bottom", "waterlogged": "false"})

    # дверь
    for y, half in ((1, "lower"), (2, "upper")):
        t.put(c, y, door_z, "minecraft:dark_oak_door",
              {"facing": "north", "half": half, "hinge": "left", "open": "false", "powered": "false"})
    if wall_h >= 4:
        t.put(c, 3, door_z, f"{NS}:baskur")
    for side in (-1, 1):
        for y in range(1, min(wall_h, 4)):
            t.put(c + side, y, door_z, f"{NS}:kerege")

    if decor:
        inner_r = R - 0.5
        for x in range(N):
            for z in range(N):
                if inside(x, z) and not is_wall(x, z):
                    t.put(x, 1, z, carpet_of(x - c, z - c, rad(x, z), inner_r))
        # сандық у задней стены, свет под шаңыраком, бочки по бокам
        back_z = min(z for z in range(N) if inside(c, z) and not is_wall(c, z))
        t.put(c, 1, back_z, "minecraft:chest", {"facing": "south", "type": "single", "waterlogged": "false"},
              {"id": (8, "minecraft:chest"), "LootTable": (8, loot)})
        t.put(c, peak - 1, c, "minecraft:lantern", {"hanging": "true", "waterlogged": "false"})
        for side in (-1, 1):
            bx = c + side * int(R - 1.5)
            if inside(bx, c) and not is_wall(bx, c):
                t.put(bx, 1, c, "minecraft:barrel", {"facing": "up", "open": "false"})
                t.put(bx, 2, c, "minecraft:lantern", {"hanging": "false", "waterlogged": "false"})

    # дорожка из ковров и огни у входа
    for k in range(1, path_len + 1):
        z = door_z + k
        for dx in (-1, 0, 1):
            t.put(c + dx, 0, z, "minecraft:spruce_planks")
            t.put(c + dx, 1, z, f"{NS}:syrmak" if dx else f"{NS}:tekemet")
    if path_len >= 2:
        fz = door_z + path_len
        for side in (-2, 2):
            t.put(c + side, 0, fz, "minecraft:stone_bricks")
            t.put(c + side, 1, fz, "minecraft:stone_bricks")
            t.put(c + side, 2, fz, "minecraft:campfire",
                  {"facing": "south", "lit": "true", "signal_fire": "false", "waterlogged": "false"})
        for side in (-2, 2):
            t.put(c + side, 0, door_z + 1, "minecraft:spruce_planks")
            t.put(c + side, 1, door_z + 1, "minecraft:spruce_fence",
                  {"east": "false", "north": "false", "south": "false", "west": "false", "waterlogged": "false"})
            t.put(c + side, 2, door_z + 1, "minecraft:lantern", {"hanging": "false", "waterlogged": "false"})
    return t


def build_aul():
    big = build_yurt(6.5, 4, 0.75, 3, seed=1)
    small = build_yurt(4.0, 3, 0.8, 2, seed=2)
    W = 48
    t = Template(W, big.size[1] + 1, W)
    mid = W // 2
    # площадь посередине: утоптанная земля и очаг с казаном
    rng = random.Random(7)
    for x in range(W):
        for z in range(W):
            d = math.hypot(x - mid, z - mid - 2)
            if d <= 9.5:
                t.put(x, 0, z, "minecraft:dirt_path" if rng.random() < 0.6 else "minecraft:coarse_dirt")
    t.put(mid, 0, mid + 2, "minecraft:stone_bricks")
    t.put(mid, 1, mid + 2, "minecraft:campfire",
          {"facing": "south", "lit": "true", "signal_fire": "false", "waterlogged": "false"})
    t.put(mid + 1, 1, mid + 2, "minecraft:cauldron")
    for dx, dz in ((-3, 0), (3, 0), (0, 3), (0, -3)):
        t.put(mid + dx, 1, mid + 2 + dz, "minecraft:spruce_slab", {"type": "bottom", "waterlogged": "false"})
    # большая юрта на севере дверью к площади (на юг)
    t.paste(big, mid - big.size[0] // 2, 0, 0, rot=0)
    # малые юрты: запад дверью на восток, восток дверью на запад, юг дверью на север
    t.paste(small, 0, 0, mid - small.size[0] // 2 + 2, rot=3)
    t.paste(small, W - small.size[2], 0, mid - small.size[0] // 2 + 2, rot=1)
    t.paste(small, mid - small.size[0] // 2, 0, W - small.size[2], rot=2)
    # коновязь
    for dz in range(-2, 3):
        t.put(mid + 7, 1, mid - 5 + dz, "minecraft:spruce_fence",
              {"east": "false", "north": "true" if dz > -2 else "false", "south": "true" if dz < 2 else "false",
               "west": "false", "waterlogged": "false"})
    return t
