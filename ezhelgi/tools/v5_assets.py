"""v5 assets: item/block textures, models, sounds.json entries, loot, recipes,
spawns, worldgen (rune stones, Baiterek, Burabay mountain) and translations.
Called from gen_assets.py: v5_assets.run(gen_assets_module).
"""
import json
import math
import os

G = None
NS = "ezhelgi"


def e(n):
    return f"{NS}:{n}"


# ------------------------------------------------------------------ item art
ART = {
    "qobyz": ([
        "................",
        ".............kk.",
        "............kwk.",
        "...........kwk..",
        "..........kwk...",
        ".........kwk....",
        "....ss..kwk.....",
        "...sllssbk......",
        "..slllllsb......",
        "..slllllbs......",
        "..sddlllbs......",
        "..sdddlbs.......",
        "...sddds........",
        "....sss.........",
        "...............h",
        "hhhhhhhhhhhhhhh.",
    ], {"k": "#5a3a1e", "w": "#9a6a3a", "s": "#4a2e16", "l": "#e2c9a0", "d": "#7a4e26", "b": "#c8a46a",
        "h": "#d8d0c0"}),
    "qamshy": ([
        "...........ll...",
        "..........l..l..",
        ".........l....l.",
        "........l......l",
        ".......l.......l",
        "......L.......l.",
        ".....L.......l..",
        "....LL......l...",
        "...gL.......l...",
        "..hg.......l....",
        ".hh.......t.....",
        "hh..............",
        "h...............",
        "................",
        "................",
        "................",
    ], {"l": "#6a3a1a", "L": "#8a5226", "g": "#e8b83c", "h": "#5a3a20", "t": "#c03030"}),
    "synyq_qylysh": ([
        "................",
        "................",
        "................",
        "................",
        "........m.......",
        ".......mlm......",
        "......mlm.......",
        ".....mlm........",
        "....mlm.........",
        "..gmlm..........",
        "...gm...........",
        "..b.g...........",
        ".b..............",
        "b...............",
        "................",
        "................",
    ], {"m": "#7a8c96", "l": "#b8c8d0", "g": "#8a7a40", "b": "#3a2a1a"}),
    "aruaq_qylysh": ([
        "..............lc",
        ".............lmc",
        "............lmc.",
        "...........lmc..",
        "..........lmc...",
        ".........lmc....",
        "........lmc.....",
        ".......lmc......",
        "......lmc.......",
        "..g..lmc........",
        "...gtmc.........",
        "....gg..........",
        "...b.gt.........",
        "..b.............",
        ".b..............",
        "y...............",
    ], {"l": "#e8ffff", "m": "#7ad8f0", "c": "#3a8cb8", "g": "#f2c230", "t": "#2bb5a6", "b": "#3a2412",
        "y": "#f2c230"}),
    "aruaq_spectral": ([
        "..............lc",
        ".............lmc",
        "............lmc.",
        "...........lmc..",
        "..........lmc...",
        ".........lmc....",
        "........lmc.....",
        ".......lmc......",
        "......lmc.......",
        "..g..lmc........",
        "...glmc.........",
        "....gg..........",
        "...m.gl.........",
        "..m.............",
        ".m..............",
        "l...............",
    ], {"l": "#e8ffffd0", "m": "#8ae0ffb0", "c": "#4aa0d0a0", "g": "#b8f0ffc0"}),
    "albasty_shashy": ([
        "......aab.......",
        ".....abbaa......",
        ".....ab.ba......",
        "....ab..ab......",
        "....ab...ba.....",
        "...ab....ab.....",
        "...ab.....ba....",
        "..ab......ab....",
        "..ab.......ba...",
        "..a.b......ab...",
        ".a..b.......ba..",
        ".a...b......a.b.",
        "a....b.....a..b.",
        "a.....b....a...b",
        "......b...a.....",
        "................",
    ], {"a": "#d8c890", "b": "#a89860"}),
    "zhez_tyrnaq": ([
        "................",
        "................",
        "..........l.....",
        ".........lm.....",
        "........lmd.....",
        ".......lmd......",
        "......lmd.......",
        ".....lmd........",
        "....lmd.........",
        "...lmd..........",
        "..smd...........",
        ".ssd............",
        ".ss.............",
        "................",
        "................",
        "................",
    ], {"l": "#f2b07a", "m": "#c8743a", "d": "#8a4a22", "s": "#e8d8c0"}),
    "tyrnaq_qanjar": ([
        "................",
        ".............l..",
        "............lm..",
        "...........lmd..",
        "..........lmd...",
        ".........lmd....",
        "........lmd.....",
        ".......lmd......",
        "......lmd.......",
        "..c..lmd........",
        "...cmd..........",
        "....cc..........",
        "...b.c..........",
        "..b.............",
        ".b..............",
        "c...............",
    ], {"l": "#f2b07a", "m": "#c8743a", "d": "#8a4a22", "c": "#a85a2a", "b": "#3a2412"}),
    "samuryk_jumyrtqasy": ([
        "................",
        "......kkkk......",
        ".....kllmmk.....",
        "....kllmmmmk....",
        "....klmmmmmk....",
        "...klmmmtmmmk...",
        "...kmmmtttmmk...",
        "...kmmmmtmmdk...",
        "..kmrmmmmmrmdk..",
        "..kmmrmmmrmmdk..",
        "..kmmmrrrmmmdk..",
        "..kmmmmmmmmddk..",
        "...kmmmmmmddk...",
        "...kddmmmdddk...",
        ".....kddddk.....",
        "......kkkk......",
    ], {"k": "#6a4210", "l": "#fff3a8", "m": "#f2c230", "d": "#b9831a", "t": "#2bb5a6", "r": "#c0392b"}),
    "samuryk_qauyrsyn": ([
        "..............t.",
        ".............tTt",
        "............tTt.",
        "...........yTy..",
        "..........yry...",
        ".........yrRy...",
        "........yrRy....",
        ".......yrRy.....",
        "......yrRy......",
        ".....yrRy.......",
        "....yrRy........",
        "...yrry.........",
        "..yyry..........",
        "..q.y...........",
        ".q..............",
        "q...............",
    ], {"t": "#2bb5a6", "T": "#1a7a72", "y": "#f2c230", "r": "#e86a24", "R": "#b8321c", "q": "#fff3a8"}),
    "tumar": ([
        "........ss......",
        ".......s..s.....",
        "......s....s....",
        ".....s......s...",
        "....s........s..",
        "....kkkkkkkkkk..",
        "....kllllllllk..",
        "....kltllgltlk..",
        ".....klllllk....",
        ".....kllgllk....",
        "......klllk.....",
        "......kltlk.....",
        ".......klk......",
        ".......kk.......",
        "........r.......",
        ".......rrr......",
    ], {"s": "#d8d0c0", "k": "#4a2e16", "l": "#9a6a3a", "t": "#2bb5a6", "g": "#f2c230", "r": "#c0392b"}),
}

EGGS = {
    "zheztyrnak_spawn_egg": ("#f2ece0", "#c8743a"),
    "batyr_aruagy_spawn_egg": ("#9fd4f0", "#3a8cb8"),
    "albasty_spawn_egg": ("#d8dcd0", "#c9b98a"),
    "zhalmauyz_kempir_spawn_egg": ("#4a3046", "#7aff5a"),
    "samuryk_spawn_egg": ("#f2c230", "#c0392b"),
}


def hexa(h):
    h = h.lstrip("#")
    a = int(h[6:8], 16) if len(h) == 8 else 255
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), a)


def item_textures():
    for name, (rows, pal) in ART.items():
        c = G.Canvas(16, 16, seed=len(name) * 7)
        for y, row in enumerate(rows):
            for x, ch in enumerate(row):
                if ch != ".":
                    c.set(x, y, hexa(pal[ch]))
        if name not in ("aruaq_spectral", "albasty_shashy", "qamshy", "samuryk_qauyrsyn"):
            c.outline("#1c140c")
        c.save("item/" + name)
    for name, (base, spots) in EGGS.items():
        c = G.Canvas(seed=len(name))
        for y in range(16):
            for x in range(16):
                dx, dy = (x - 7.5) / 5.2, (y - 8.8) / 6.6
                if dy < 0:
                    dx *= 1.15
                if dx * dx + dy * dy <= 1:
                    c.set(x, y, base if (x * 2 + y) % 5 else spots)
        c.noise(0.05)
        c.outline("#1c1c1c")
        c.save("item/" + name)


# Орхоно-енисейские руны (упрощённые 5x7 знаки)
RUNES = [
    ["..#..", ".###.", "#.#.#", "..#..", "..#..", "..#..", "..#.."],
    ["#...#", ".#.#.", "..#..", "..#..", ".#.#.", "#...#", "....."],
    ["####.", "#..#.", "#..#.", "####.", "...#.", "...#.", "...#."],
    ["#....", "##...", "#.#..", "#..#.", "#...#", "#....", "#...."],
    ["..#..", ".#.#.", "#...#", "..#..", "..#..", ".#.#.", "#...#"],
    ["#.#.#", "#.#.#", ".###.", "..#..", "..#..", "..#..", "....."],
]


def rune_textures():
    for lit in (True, False):
        c = G.Canvas(16, 16, seed=77)
        for y in range(16):
            for x in range(16):
                v = c.rng.uniform(0.85, 1.1)
                base = (98, 102, 108) if (x * 3 + y * 5) % 11 else (84, 88, 94)
                if (x + y * 2) % 13 == 0:
                    base = (70, 96, 72)  # мох
                c.set(x, y, tuple(int(ch * v) for ch in base) + (255,))
        glyphs = [RUNES[0], RUNES[3]] if lit else [RUNES[2], RUNES[5]]
        for gi, glyph in enumerate([RUNES[0], RUNES[3]]):
            ox = 2 + gi * 7
            for y, row in enumerate(glyph):
                for x, ch in enumerate(row):
                    if ch == "#":
                        if lit:
                            c.set(ox + x, 4 + y, (110, 250, 236, 255))
                            if x + 1 < 5 and row[x + 1] != "#":
                                c.set(ox + x + 1, 4 + y, (40, 150, 150, 255))
                        else:
                            c.set(ox + x, 4 + y, (52, 54, 58, 255))
        for x in range(16):
            c.set(x, 0, (70, 72, 78, 255))
            c.set(x, 15, (60, 62, 66, 255))
            c.set(x, 2, (120, 124, 130, 255) if x % 3 else (90, 94, 100, 255))
            c.set(x, 13, (120, 124, 130, 255) if x % 4 else (90, 94, 100, 255))
        c.save("block/runa_tasy" + ("" if lit else "_off"))
        # top face
        t = G.Canvas(16, 16, seed=78)
        for y in range(16):
            for x in range(16):
                v = t.rng.uniform(0.85, 1.1)
                t.set(x, y, tuple(int(ch * v) for ch in (104, 108, 114)) + (255,))
        if lit:
            for (x, y) in ((7, 7), (8, 7), (7, 8), (8, 8), (6, 7), (9, 8), (7, 6), (8, 9)):
                t.set(x, y, (110, 250, 236, 255))
        t.save("block/runa_tasy_top" + ("" if lit else "_off"))


def models():
    W = G.write_json
    A = G.A
    for name in ART:
        handheld = name in ("qamshy", "synyq_qylysh", "aruaq_qylysh", "aruaq_spectral", "tyrnaq_qanjar")
        if name == "qobyz":
            continue
        G.simple_item(name, handheld=handheld)
    for name in EGGS:
        G.simple_item(name)
    # қобыз держат вертикально, как виолончель
    W(f"{A}/models/item/qobyz.json", {
        "parent": "minecraft:item/handheld", "textures": {"layer0": f"{NS}:item/qobyz"},
        "display": {
            "thirdperson_righthand": {"rotation": [0, 90, 20], "translation": [0, 2, 1.5], "scale": [1.0, 1.0, 1.0]},
            "thirdperson_lefthand": {"rotation": [0, -90, -20], "translation": [0, 2, 1.5], "scale": [1.0, 1.0, 1.0]},
            "firstperson_righthand": {"rotation": [0, -90, 15], "translation": [1.13, 3.2, 1.13], "scale": [0.8, 0.8, 0.8]}}})
    W(f"{A}/items/qobyz.json", {"model": {"type": "minecraft:model", "model": f"{NS}:item/qobyz"}})
    # руна тас: два состояния
    blk = lambda n: f"{NS}:block/{n}"
    for suf in ("", "_off"):
        W(f"{A}/models/block/runa_tasy{suf}.json", {"parent": "minecraft:block/cube_column",
                                                    "textures": {"side": blk("runa_tasy" + suf), "end": blk("runa_tasy_top" + suf)}})
    W(f"{A}/blockstates/runa_tasy.json", {"variants": {"lit=true": {"model": blk("runa_tasy")},
                                                      "lit=false": {"model": blk("runa_tasy_off")}}})
    W(f"{A}/items/runa_tasy.json", {"model": {"type": "minecraft:model", "model": blk("runa_tasy")}})


# ------------------------------------------------------------------ sounds.json
def sounds():
    path = f"{G.A}/sounds.json"
    data = json.load(open(path, encoding="utf-8"))

    def ev(name, pitch=1.0, volume=1.0):
        return {"name": name, "type": "event", "pitch": pitch, "volume": volume}

    def entry(sub, *snds):
        d = {"sounds": list(snds)}
        if sub:
            d["subtitle"] = "subtitles.ezhelgi." + sub
        return d
    data.update({
        "item.kobyz.play": entry("kobyz", *[{"name": f"{NS}:kobyz/kobyz{i}"} for i in (1, 2, 3)]),
        "item.qamshy.crack": entry("qamshy", {"name": f"{NS}:qamshy/crack1"}, {"name": f"{NS}:qamshy/crack2"}),
        "block.runa.activate": entry("rune", ev("minecraft:block.beacon.activate", 1.3), ev("minecraft:block.amethyst_block.resonate", 0.8, 1.5)),
        "block.runa.curse": entry("rune_curse", ev("minecraft:entity.wither.spawn", 1.6, 0.6), ev("minecraft:ambient.soul_sand_valley.mood", 1.0, 1.5)),
        "entity.zheztyrnak.ambient": entry("zheztyrnak", ev("minecraft:entity.allay.ambient_without_item", 0.55, 0.7)),
        "entity.zheztyrnak.angry": entry("zheztyrnak_angry", ev("minecraft:entity.vex.ambient", 0.6), ev("minecraft:entity.fox.screech", 0.55)),
        "entity.zheztyrnak.shriek": entry("zheztyrnak_shriek", ev("minecraft:entity.vex.charge", 0.5, 1.5), ev("minecraft:entity.fox.screech", 0.45, 1.5)),
        "entity.zheztyrnak.claw": entry("claw", ev("minecraft:entity.player.attack.sweep", 1.5), ev("minecraft:item.trident.hit", 1.4)),
        "entity.zheztyrnak.leap": entry("leap", ev("minecraft:entity.goat.long_jump", 1.2), ev("minecraft:entity.phantom.flap", 1.3)),
        "entity.zheztyrnak.hurt": entry(None, ev("minecraft:entity.vex.hurt", 0.7)),
        "entity.zheztyrnak.death": entry(None, ev("minecraft:entity.vex.death", 0.6), ev("minecraft:entity.witch.death", 1.3)),
        "entity.batyr_aruagy.ambient": entry("aruaq", ev("minecraft:particle.soul_escape", 0.6, 2.0), ev("minecraft:entity.wither_skeleton.ambient", 0.6)),
        "entity.batyr_aruagy.hurt": entry(None, ev("minecraft:entity.wither_skeleton.hurt", 0.7)),
        "entity.batyr_aruagy.death": entry(None, ev("minecraft:entity.wither_skeleton.death", 0.6), ev("minecraft:particle.soul_escape", 0.5, 2.0)),
        "entity.batyr_aruagy.parry": entry("parry", ev("minecraft:item.shield.block", 0.8), ev("minecraft:block.anvil.land", 1.8, 0.5)),
        "entity.batyr_aruagy.vanish": entry("aruaq_vanish", ev("minecraft:particle.soul_escape", 0.4, 2.0)),
        "entity.albasty.ambient": entry("albasty", ev("minecraft:entity.ghast.ambient", 1.6, 0.4), ev("minecraft:entity.warden.nearby_closer", 1.4, 0.5)),
        "entity.albasty.hurt": entry(None, ev("minecraft:entity.ghast.hurt", 1.5, 0.6)),
        "entity.albasty.death": entry(None, ev("minecraft:entity.ghast.death", 1.4, 0.7)),
        "entity.albasty.vanish": entry("albasty_vanish", ev("minecraft:entity.illusioner.mirror_move", 0.7), ev("minecraft:entity.enderman.teleport", 0.5)),
        "entity.albasty.appear": entry("albasty_appear", ev("minecraft:entity.enderman.teleport", 0.5), ev("minecraft:entity.warden.nearby_closest", 1.5, 0.6)),
        "entity.zhalmauyz_kempir.laugh": entry("kempir", ev("minecraft:entity.witch.celebrate", 0.75), ev("minecraft:entity.witch.ambient", 0.7)),
        "entity.zhalmauyz_kempir.hurt": entry(None, ev("minecraft:entity.witch.hurt", 0.75)),
        "entity.zhalmauyz_kempir.death": entry(None, ev("minecraft:entity.witch.death", 0.7), ev("minecraft:entity.evoker.death", 0.8)),
        "entity.zhalmauyz_kempir.cast": entry("kempir_cast", ev("minecraft:entity.evoker.prepare_attack", 0.8), ev("minecraft:entity.illusioner.cast_spell", 0.7)),
        "entity.zhalmauyz_kempir.bolt": entry("kempir_bolt", ev("minecraft:entity.evoker.cast_spell", 0.6), ev("minecraft:entity.shulker.shoot", 0.6)),
        "entity.zhalmauyz_kempir.snare": entry("kempir_snare", ev("minecraft:block.sculk_vein.place", 0.6, 1.5), ev("minecraft:block.big_dripleaf.tilt_down", 0.6)),
        "entity.zhalmauyz_kempir.summon": entry("kempir_summon", ev("minecraft:entity.evoker.prepare_summon", 0.7)),
        "entity.samuryk.ambient": entry("samuryk", ev("minecraft:entity.parrot.ambient", 0.5), ev("minecraft:entity.phantom.ambient", 1.8, 0.5)),
        "entity.samuryk.hurt": entry(None, ev("minecraft:entity.parrot.hurt", 0.5)),
        "entity.samuryk.death": entry(None, ev("minecraft:entity.parrot.death", 0.5)),
    })
    G.write_json(path, data)


# ------------------------------------------------------------------ data
def it(name, w, lo=1, hi=1, cond=None):
    x = {"type": "minecraft:item", "name": name, "weight": w}
    if hi > 1:
        x["modifier"] = [{"type": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}}]
    return x


def loot():
    D = G.D
    W = G.write_json
    kp = {"type": "minecraft:killed_by_player"}

    def table(path, pools):
        W(f"{D}/loot_table/{path}.json", {"type": "minecraft:entity" if path.startswith("entities") else "minecraft:chest",
                                          "pools": pools, "random_sequence": f"{NS}:{path}"})
    table("entities/zheztyrnak", [
        {"rolls": 1, "entries": [it("minecraft:copper_ingot", 1, 1, 3)]},
        {"rolls": 1, "condition": kp, "entries": [it(e("zhez_tyrnaq"), 2), {"type": "minecraft:empty", "weight": 3}]}])
    table("entities/batyr_aruagy", [
        {"rolls": 1, "entries": [it(e("synyq_qylysh"), 1)]},
        {"rolls": 1, "entries": [it("minecraft:soul_sand", 2), it("minecraft:bone", 3, 1, 2), {"type": "minecraft:empty", "weight": 3}]}])
    table("entities/albasty", [
        {"rolls": 1, "entries": [it("minecraft:string", 2, 1, 2), {"type": "minecraft:empty", "weight": 1}]},
        {"rolls": 1, "condition": kp, "entries": [it(e("albasty_shashy"), 1), {"type": "minecraft:empty", "weight": 3}]}])
    table("entities/zhalmauyz_kempir", [
        {"rolls": 1, "entries": [it(e("saka_gold"), 1, 3, 7)]},
        {"rolls": 1, "entries": [it(e("qobyz"), 1)]},
        {"rolls": 2, "entries": [it("minecraft:ender_pearl", 3, 1, 2), it("minecraft:amethyst_shard", 3, 2, 5),
                                 it(e("albasty_shashy"), 2), it("minecraft:emerald", 3, 2, 5), it(e("runa_tasy"), 1)]}])
    table("entities/samuryk", [{"rolls": 1, "entries": [it(e("samuryk_qauyrsyn"), 1, 1, 3)]}])
    table("chests/samuryk_uyasy", [
        {"rolls": 1, "entries": [it(e("samuryk_jumyrtqasy"), 1)]},
        {"rolls": {"type": "minecraft:uniform", "min": 3, "max": 5}, "entries": [
            it(e("samuryk_qauyrsyn"), 10, 1, 3), it(e("saka_gold"), 6, 1, 3), it("minecraft:gold_nugget", 10, 3, 9),
            it("minecraft:golden_apple", 3), it("minecraft:cod", 6, 2, 4), it(e("kumys"), 4)]}])
    table("chests/kempir", [
        {"rolls": {"type": "minecraft:uniform", "min": 4, "max": 7}, "entries": [
            it("minecraft:bone", 10, 2, 6), it("minecraft:spider_eye", 8, 1, 3), it("minecraft:amethyst_shard", 8, 2, 6),
            it("minecraft:ender_pearl", 4, 1, 2), it(e("saka_gold"), 6, 1, 3), it("minecraft:emerald", 5, 1, 4),
            it(e("albasty_shashy"), 3), it(e("runa_tasy"), 2), it("minecraft:glowstone_dust", 6, 2, 6),
            it(e("qobyz"), 2), it(e("kurt"), 6, 2, 5)]}])


def recipes():
    sh, sl = G.shaped, G.shapeless
    sh("qobyz", ["  S", "PLS", "PP "], {"S": "minecraft:string", "P": "#minecraft:planks", "L": "minecraft:leather"},
       e("qobyz"))
    sh("qamshy", ["  L", " L ", "T  "], {"L": "minecraft:leather", "T": "minecraft:stick"}, e("qamshy"), category="equipment")
    sl("aruaq_qylysh", [e("synyq_qylysh"), e("saka_gold"), e("saka_gold"), "minecraft:soul_sand"], e("aruaq_qylysh"),
       category="equipment")
    sh("tumar", [" S ", "LHL", " P "], {"S": "minecraft:string", "L": "minecraft:leather", "H": e("albasty_shashy"),
                                        "P": "minecraft:paper"}, e("tumar"), category="equipment")
    sh("tyrnaq_qanjar", ["C", "C", "S"], {"C": e("zhez_tyrnaq"), "S": "minecraft:stick"}, e("tyrnaq_qanjar"),
       category="equipment")


def tags_and_spawns():
    D, MC, W = G.D, G.MC, G.write_json
    W(f"{D}/tags/worldgen/biome/zheztyrnak_habitat.json", {"values": [
        "#minecraft:is_forest", "minecraft:plains", "minecraft:sunflower_plains", "minecraft:savanna", "minecraft:meadow"]})
    W(f"{D}/tags/worldgen/biome/aruaq_habitat.json", {"values": [
        "#ezhelgi:has_kurgan", "minecraft:taiga", "minecraft:old_growth_pine_taiga", "minecraft:forest"]})
    W(f"{D}/tags/worldgen/biome/albasty_habitat.json", {"values": [
        "#minecraft:is_river", "#minecraft:is_beach", "minecraft:swamp", "minecraft:mangrove_swamp",
        "minecraft:plains", "minecraft:forest", "minecraft:birch_forest", "minecraft:taiga", "minecraft:savanna"]})
    W(f"{D}/tags/worldgen/biome/rune_stone.json", {"values": [
        "#ezhelgi:has_kurgan", "#minecraft:is_forest", "#minecraft:is_taiga", "minecraft:desert",
        "minecraft:snowy_plains", "minecraft:windswept_hills"]})
    W(f"{D}/tags/worldgen/biome/baiterek.json", {"values": [
        "minecraft:plains", "minecraft:sunflower_plains", "minecraft:meadow", "minecraft:savanna", "minecraft:birch_forest"]})
    mod = f"{D}/forge/biome_modifier"

    def add(name, tag, weight, lo, hi):
        W(f"{mod}/add_{name}.json", {"type": "forge:add_spawns", "biomes": tag, "spawners": [
            {"type": e(name), "count": {"type": "minecraft:uniform", "min_inclusive": lo, "max_inclusive": hi}, "weight": weight}]})
    add("zheztyrnak", "#ezhelgi:zheztyrnak_habitat", 3, 1, 1)
    add("batyr_aruagy", "#ezhelgi:aruaq_habitat", 6, 1, 2)
    add("albasty", "#ezhelgi:albasty_habitat", 12, 1, 1)
    W(f"{MC}/tags/item/swords.json", {"values": [e("qylysh"), e("altyn_qylysh"), e("aruaq_qylysh"), e("tyrnaq_qanjar"), e("qamshy")]})
    W(f"{MC}/tags/block/mineable/pickaxe.json", {"values": [e("runa_tasy")]})
    W(f"{D}/loot_table/blocks/runa_tasy.json", {
        "type": "minecraft:block", "random_sequence": f"{NS}:blocks/runa_tasy",
        "pools": [{"rolls": 1, "condition": {"type": "minecraft:survives_explosion"},
                   "entries": [{"type": "minecraft:item", "name": e("runa_tasy")}]}]})


# ------------------------------------------------------------------ worldgen
def pool(name, elements):
    G.write_json(f"{G.D}/worldgen/template_pool/{name}.json", {
        "fallback": "minecraft:empty",
        "elements": [{"weight": w, "element": {
            "element_type": "minecraft:single_pool_element", "location": e(loc),
            "processors": "minecraft:empty", "projection": "rigid"}} for loc, w in elements]})


def structure(name, biomes, pool_name, adapt, depth, spacing, separation, salt):
    G.write_json(f"{G.D}/worldgen/structure/{name}.json", {
        "type": "minecraft:jigsaw", "biomes": biomes, "step": "surface_structures",
        "spawn_overrides": {}, "terrain_adaptation": adapt,
        "start_pool": e(pool_name), "size": 1, "start_height": {"absolute": depth},
        "project_start_to_heightmap": "WORLD_SURFACE_WG", "max_distance_from_center": 116,
        "use_expansion_hack": False})
    G.write_json(f"{G.D}/worldgen/structure_set/{name}.json", {
        "structures": [{"structure": e(name), "weight": 1}],
        "placement": {"type": "minecraft:random_spread", "spacing": spacing, "separation": separation, "salt": salt}})


def worldgen():
    import v5_world
    for name, tpl in v5_world.build_all().items():
        G.write_template(name, tpl)
    pool("runa/start", [("runa/stele", 4), ("runa/circle", 2), ("runa/balbal", 2)])
    structure("runa_tas", "#ezhelgi:rune_stone", "runa/start", "beard_thin", -1, 26, 9, 640021)
    pool("baiterek/start", [("baiterek", 1)])
    structure("baiterek", "#ezhelgi:baiterek", "baiterek/start", "beard_box", -2, 72, 28, 158323)
    pool("burabay/start", [("burabay", 1)])
    structure("burabay", "#ezhelgi:steppe", "burabay/start", "beard_box", -2, 88, 34, 470117)


# ------------------------------------------------------------------ translations
NAMES = {
    "item.ezhelgi.qobyz": ("Кобыз", "Қобыз", "Kobyz"),
    "item.ezhelgi.qamshy": ("Камчи (плеть)", "Қамшы", "Qamshy Whip"),
    "item.ezhelgi.synyq_qylysh": ("Сломанный меч батыра", "Сынық қылыш", "Broken Batyr Sword"),
    "item.ezhelgi.aruaq_qylysh": ("Клинок аруаха", "Аруақ қылышы", "Spirit Blade"),
    "item.ezhelgi.aruaq_spectral": ("Призрачный меч", "Елес қылыш", "Spectral Sword"),
    "item.ezhelgi.albasty_shashy": ("Прядь волос албасты", "Албасты шашы", "Albasty Hair"),
    "item.ezhelgi.zhez_tyrnaq": ("Медный коготь", "Жез тырнақ", "Copper Claw"),
    "item.ezhelgi.tyrnaq_qanjar": ("Кинжал из медных когтей", "Тырнақ қанжар", "Copper Claw Dagger"),
    "item.ezhelgi.samuryk_jumyrtqasy": ("Яйцо Самрука", "Самұрық жұмыртқасы", "Samruk Egg"),
    "item.ezhelgi.samuryk_qauyrsyn": ("Перо Самрука", "Самұрық қауырсыны", "Samruk Feather"),
    "item.ezhelgi.tumar": ("Тумар (оберег)", "Тұмар", "Tumar Amulet"),
    "item.ezhelgi.zheztyrnak_spawn_egg": ("Яйцо призыва жезтырнак", "Жезтырнақ жұмыртқасы", "Zheztyrnak Spawn Egg"),
    "item.ezhelgi.batyr_aruagy_spawn_egg": ("Яйцо призыва духа батыра", "Батыр аруағы жұмыртқасы", "Batyr Spirit Spawn Egg"),
    "item.ezhelgi.albasty_spawn_egg": ("Яйцо призыва албасты", "Албасты жұмыртқасы", "Albasty Spawn Egg"),
    "item.ezhelgi.zhalmauyz_kempir_spawn_egg": ("Яйцо призыва жалмауыз кемпир", "Жалмауыз кемпір жұмыртқасы", "Zhalmauyz Kempir Spawn Egg"),
    "item.ezhelgi.samuryk_spawn_egg": ("Яйцо призыва Самрука", "Самұрық жұмыртқасы (шақыру)", "Samruk Spawn Egg"),
    "block.ezhelgi.runa_tasy": ("Рунный камень", "Руна тас", "Rune Stone"),
    "entity.ezhelgi.zheztyrnak": ("Жезтырнак", "Жезтырнақ", "Zheztyrnak"),
    "entity.ezhelgi.batyr_aruagy": ("Дух батыра", "Батыр аруағы", "Batyr Spirit"),
    "entity.ezhelgi.albasty": ("Албасты", "Албасты", "Albasty"),
    "entity.ezhelgi.zhalmauyz_kempir": ("Жалмауыз кемпир", "Жалмауыз кемпір", "Zhalmauyz Kempir"),
    "entity.ezhelgi.samuryk": ("Самрук", "Самұрық", "Samruk"),
    "tooltip.ezhelgi.tumar": ("Отгоняет фантомов, чешуйниц, летучих мышей, вексов и албасты",
                              "Фантомдарды, күйелерді, жарқанаттарды және албастыны қуады",
                              "Repels phantoms, silverfish, bats, vexes and albasty"),
    "message.ezhelgi.samuryk.hatched": ("Из золотого яйца вылупился птенец Самрука! Корми его рыбой.",
                                        "Алтын жұмыртқадан Самұрық балапаны шықты! Оны балықпен қоректендір.",
                                        "A Samruk chick hatched from the golden egg! Feed it fish."),
    "message.ezhelgi.samuryk.sit": ("Самрук ждёт здесь", "Самұрық осында күтеді", "Samruk waits here"),
    "message.ezhelgi.samuryk.follow": ("Самрук летит за тобой", "Самұрық соңыңнан ұшады", "Samruk follows you"),
    "message.ezhelgi.rune.kuch": ("Руна «Күш» — сила!", "«Күш» руна — күш!", "Rune of Strength!"),
    "message.ezhelgi.rune.jel": ("Руна «Жел» — скорость ветра!", "«Жел» руна — жел жылдамдығы!", "Rune of Wind — speed!"),
    "message.ezhelgi.rune.sau": ("Руна «Сау» — исцеление!", "«Сау» руна — сауығу!", "Rune of Healing!"),
    "message.ezhelgi.rune.qalqan": ("Руна «Қалқан» — защита!", "«Қалқан» руна — қорғаныс!", "Rune of the Shield!"),
    "message.ezhelgi.rune.kun": ("Руна «Күн» — видишь во тьме!", "«Күн» руна — қараңғыда көресің!", "Rune of the Sun — night vision!"),
    "message.ezhelgi.rune.qanat": ("Руна «Қанат» — крылья!", "«Қанат» руна — қанат!", "Rune of Wings — jump!"),
    "message.ezhelgi.rune.ot": ("Руна «От» — огонь не страшен!", "«От» руна — от қорқынышты емес!", "Rune of Fire — fire resistance!"),
    "message.ezhelgi.rune.temir": ("Руна «Темір» — быстрые руки!", "«Темір» руна — шапшаң қол!", "Rune of Iron — haste!"),
    "message.ezhelgi.rune.tengri": ("Руна «Тәңір» — небесная защита!", "«Тәңір» руна — көк қорғанысы!", "Rune of Tengri — absorption!"),
    "message.ezhelgi.rune.su": ("Руна «Су» — дыши под водой!", "«Су» руна — су астында тыныс!", "Rune of Water — water breathing!"),
    "message.ezhelgi.rune.baq": ("Руна «Бақ» — удача!", "«Бақ» руна — бақыт!", "Rune of Luck!"),
    "message.ezhelgi.rune.curse": ("Проклятая руна! Из-под земли встают мёртвые…", "Қарғыс руна! Жерден өлілер тұрды…",
                                   "A cursed rune! The dead rise…"),
    "subtitles.ezhelgi.kobyz": ("Звучит кобыз", "Қобыз сарыны", "Kobyz plays"),
    "subtitles.ezhelgi.qamshy": ("Щелчок камчи", "Қамшы шартылы", "Whip cracks"),
    "subtitles.ezhelgi.rune": ("Руна пробуждается", "Руна оянды", "Rune awakens"),
    "subtitles.ezhelgi.rune_curse": ("Проклятие руны", "Руна қарғысы", "Rune curse"),
    "subtitles.ezhelgi.zheztyrnak": ("Девушка напевает", "Қыз ыңылдайды", "A maiden hums"),
    "subtitles.ezhelgi.zheztyrnak_angry": ("Жезтырнак шипит", "Жезтырнақ ысылдайды", "Zheztyrnak hisses"),
    "subtitles.ezhelgi.zheztyrnak_shriek": ("Визг жезтырнак", "Жезтырнақ шыңғырады", "Zheztyrnak shrieks"),
    "subtitles.ezhelgi.claw": ("Удар когтями", "Тырнақ соққысы", "Claws slash"),
    "subtitles.ezhelgi.leap": ("Длинный прыжок", "Ұзын секіріс", "Long leap"),
    "subtitles.ezhelgi.aruaq": ("Стон духа", "Аруақ күңіренеді", "Spirit moans"),
    "subtitles.ezhelgi.parry": ("Дух отбил удар", "Аруақ соққыны қайтарды", "Spirit parries"),
    "subtitles.ezhelgi.aruaq_vanish": ("Дух растаял", "Аруақ ғайып болды", "Spirit fades"),
    "subtitles.ezhelgi.albasty": ("Шёпот албасты", "Албасты сыбыры", "Albasty whispers"),
    "subtitles.ezhelgi.albasty_vanish": ("Албасты исчезла", "Албасты жоқ болды", "Albasty vanishes"),
    "subtitles.ezhelgi.albasty_appear": ("Албасты за спиной!", "Албасты артыңда!", "Albasty behind you!"),
    "subtitles.ezhelgi.kempir": ("Кемпир хохочет", "Кемпір қарқылдайды", "Kempir cackles"),
    "subtitles.ezhelgi.kempir_cast": ("Кемпир колдует", "Кемпір сиқырлайды", "Kempir casts"),
    "subtitles.ezhelgi.kempir_bolt": ("Чёрная молния", "Қара найзағай", "Black bolt"),
    "subtitles.ezhelgi.kempir_snare": ("Путы", "Тұзақ", "Snare"),
    "subtitles.ezhelgi.kempir_summon": ("Кемпир зовёт нечисть", "Кемпір пәлені шақырады", "Kempir summons"),
    "subtitles.ezhelgi.samuryk": ("Крик Самрука", "Самұрық шақырады", "Samruk calls"),
}


def run(g):
    global G
    G = g
    item_textures()
    rune_textures()
    models()
    sounds()
    loot()
    recipes()
    tags_and_spawns()
    worldgen()
    import v5_skins
    v5_skins.run(os.path.join(G.A, "textures/entity"))
    import v5_mask
    v5_mask.run(G)
    G.NAMES.update(NAMES)
