"""v6 assets: items, sounds, loot, recipes, spawns, paintings, structures, books and translations.
Called from gen_assets.py after v5: v6_assets.run(gen_assets_module).
"""
import json
import os

G = None
NS = "ezhelgi"


def e(n):
    return f"{NS}:{n}"


MEAT = {"r": "#c0464a", "R": "#8a2a2e", "w": "#f0d8d0", "b": "#6a3a20", "B": "#9a5a30", "y": "#d8a060"}
ART = {
    "jylqy_eti": ([
        "................",
        "................",
        "......rrrr......",
        "....rrRrrrrr....",
        "...rrrrRrrrrw...",
        "..rrRrrrrrrrww..",
        "..rrrrrRrrrrww..",
        "..rRrrrrrrRrw...",
        "..rrrrRrrrrrw...",
        "...rrrrrrrRw....",
        "....rrRrrrw.....",
        ".....rrrww......",
        "................",
        "................",
        "................",
        "................"], MEAT),
    "pisken_jylqy_eti": ([
        "................",
        "................",
        "......bbbb......",
        "....bbBbbbbb....",
        "...bbbbBbbbby...",
        "..bbBbbbbbbbyy..",
        "..bbbbbBbbbbyy..",
        "..bBbbbbbbBby...",
        "..bbbbBbbbbby...",
        "...bbbbbbbBy....",
        "....bbBbbby.....",
        ".....bbbyy......",
        "................",
        "................",
        "................",
        "................"], MEAT),
    "aqboken_eti": ([
        "................",
        "................",
        "................",
        ".......rrr......",
        ".....rrRrrr.....",
        "....rrrrRrrw....",
        "...rrRrrrrrww...",
        "...rrrrrRrrw....",
        "....rrRrrrw.....",
        ".....rrrrw......",
        "......www.......",
        ".....ww.........",
        "....ww..........",
        "................",
        "................",
        "................"], MEAT),
    "pisken_aqboken_eti": ([
        "................",
        "................",
        "................",
        ".......bbb......",
        ".....bbBbbb.....",
        "....bbbbBbby....",
        "...bbBbbbbbyy...",
        "...bbbbbBbby....",
        "....bbBbbby.....",
        ".....bbbby......",
        "......www.......",
        ".....ww.........",
        "....ww..........",
        "................",
        "................",
        "................"], MEAT),
    "torsyk": ([
        "......kk........",
        "......ss........",
        ".....kkkk.......",
        "....kllllk......",
        "...klLllllk.....",
        "..kllllLlllk....",
        "..klgggggglk....",
        "..klLlllllLk....",
        "..klllllllllk...",
        "..kllLllllllk...",
        "..klllllllLlk...",
        "...kllllllllk...",
        "....kllllllk....",
        ".....kkkkkk.....",
        "................",
        "................"], {"k": "#3a2412", "l": "#9a6a3a", "L": "#7a4e26", "s": "#d8d0c0", "g": "#e8b83c"}),
    "torsyk_qymyz": ([
        "......kk........",
        "......ww........",
        ".....kkkk.......",
        "....kllllk......",
        "...klLllllk.....",
        "..kllllLlllk....",
        "..klggrgglk.....",
        "..klLlllllLk....",
        "..klllwwlllk....",
        "..kllLwwwlllk...",
        "..klllwwlLlk....",
        "...kllllllllk...",
        "....kllllllk....",
        ".....kkkkkk.....",
        "................",
        "................"], {"k": "#3a2412", "l": "#9a6a3a", "L": "#7a4e26", "w": "#f4f0e4", "g": "#e8b83c", "r": "#c0392b"}),
    "torsyk_shubat": ([
        "......kk........",
        "......ww........",
        ".....kkkk.......",
        "....kllllk......",
        "...klLllllk.....",
        "..kllllLlllk....",
        "..klggtgglk.....",
        "..klLlllllLk....",
        "..klllwwlllk....",
        "..kllLwwwlllk...",
        "..klllwwlLlk....",
        "...kllllllllk...",
        "....kllllllk....",
        ".....kkkkkk.....",
        "................",
        "................"], {"k": "#3a2412", "l": "#b08a5a", "L": "#8a6a3a", "w": "#f4ecd8", "g": "#e8b83c", "t": "#2bb5a6"}),
    "kieli_qyl": ([
        "................",
        "..........ppp...",
        ".........p...p..",
        "........p.....p.",
        "........p.....p.",
        ".......w.p...p..",
        "......w...ppp...",
        ".....w..........",
        "....w...........",
        "...w............",
        "..w.....g.......",
        ".ww....ggg......",
        "ww......g.......",
        "................",
        "................",
        "................"], {"p": "#a88ae8", "w": "#f4f0ff", "g": "#e8d8ff"}),
    "ezhelgi_tas": ([
        "................",
        "................",
        "....kkkkkk......",
        "...kssssssk.....",
        "..kssdssssskk...",
        "..ksssssdssssk..",
        "..ksdssmsssdsk..",
        "..kssssssssssk..",
        "..kssssdssssk...",
        "..ksmssssssk....",
        "...kssssdsk.....",
        "....kkkkkk......",
        "................",
        "................",
        "................",
        "................"], {"k": "#3a3a38", "s": "#8c8c88", "d": "#5a5a56", "m": "#6e8a62"}),
    "tas_kaila": ([
        "................",
        "...ssssss.......",
        "..ssddddss......",
        ".ss......ssb....",
        ".s......bwsb....",
        "s......bw..s....",
        "......bw...s....",
        ".....bw.........",
        "....bw..........",
        "...bw...........",
        "..bw............",
        ".bw.............",
        "bw..............",
        "................",
        "................",
        "................"], {"s": "#8c8c88", "d": "#5a5a56", "b": "#4a2e16", "w": "#7a4e26"}),
    "tas_balta": ([
        "................",
        "......sss.......",
        ".....sssss......",
        ".....ssddsb.....",
        "......sssbw.....",
        ".......sbw......",
        "......bw........",
        ".....bw.........",
        "....bw..........",
        "...bw...........",
        "..bw............",
        ".bw.............",
        "bw..............",
        "................",
        "................",
        "................"], {"s": "#8c8c88", "d": "#5a5a56", "b": "#4a2e16", "w": "#7a4e26"}),
    "tas_kurek": ([
        "............sss.",
        "...........ssss.",
        "..........sssss.",
        ".........bsssd..",
        "........bw.ss...",
        ".......bw.......",
        "......bw........",
        ".....bw.........",
        "....bw..........",
        "...bw...........",
        "..bw............",
        ".bw.............",
        "bw..............",
        "................",
        "................",
        "................"], {"s": "#8c8c88", "d": "#5a5a56", "b": "#4a2e16", "w": "#7a4e26"}),
    "dau_shoqpary": ([
        ".........kkkk...",
        "........kwwwwk..",
        ".......kwWwwiwk.",
        ".......kwwiwwwk.",
        ".......kwwwWwwk.",
        "........kwwwwk..",
        ".......kwwkkk...",
        "......kwk.......",
        ".....kwk........",
        "....kwk.........",
        "...kwk..........",
        "..kwk...........",
        ".kbk............",
        "kbk.............",
        ".k..............",
        "................"], {"k": "#2a1a0e", "w": "#7a5230", "W": "#5a3a1e", "i": "#6a6a70", "b": "#c0392b"}),
    "otyrar_tengesi": ([
        "................",
        "................",
        "......kkkk......",
        "....kkmmmmkk....",
        "...kmmllmmmmk...",
        "...kmldddmmmk...",
        "..kmmdmmdmmmmk..",
        "..kmmmddmmdmmk..",
        "..kmmdmmdmmmmk..",
        "..kmmmmddddmmk..",
        "...kmmmmmmmmk...",
        "...kmmmmmmmmk...",
        "....kkmmmmkk....",
        "......kkkk......",
        "................",
        "................"], {"k": "#5a3a10", "m": "#b8822a", "l": "#f0c860", "d": "#7a5418"}),
    "qoljazba": ([
        "................",
        "..kkkkkkkkkkk...",
        ".kppppppppppk...",
        ".kpllllllllpkk..",
        ".kppppppppppkrk.",
        ".kpllllllppppkrk",
        ".kppppppppppkrk.",
        ".kplllllllllpk..",
        ".kppppppppppk...",
        ".kpllllpgppppk..",
        ".kppppppppppk...",
        ".kpllllllllpk...",
        ".kppppppppppk...",
        "..kkkkkkkkkkk...",
        "................",
        "................"], {"k": "#5a3a1a", "p": "#e8d8a8", "l": "#6a4a2a", "r": "#c0392b", "g": "#e8b83c"}),
    "qumyra": ([
        "................",
        "......kkk.......",
        ".....kcck.......",
        "......kk........",
        ".....kcck.......",
        "....kcccck......",
        "...kccbbcck.....",
        "...kcbttbck.....",
        "...kccbbcck.....",
        "...kcccccck.....",
        "....kcccck......",
        ".....kcck.......",
        "....kkkkkk......",
        "................",
        "................",
        "................"], {"k": "#4a2410", "c": "#c06a3a", "b": "#2bb5a6", "t": "#f0e0b0"}),
}

EGGS = {
    "balbal_spawn_egg": ("#8c8c88", "#5a5a56"),
    "baqsy_kolenkesi_spawn_egg": ("#3a2a5a", "#a88ae8"),
    "dau_spawn_egg": ("#6a4a2a", "#b88a62"),
    "aqboken_spawn_egg": ("#ceaa78", "#f4ecdc"),
    "barys_spawn_egg": ("#d6d2c8", "#303030"),
    "qorqyt_ata_spawn_egg": ("#ece6d8", "#c8a040"),
    "qaraqshy_spawn_egg": ("#2a2420", "#8a1e1e"),
    "auyldas_spawn_egg": ("#1e3a6a", "#e8b83c"),
    "abai_spawn_egg": ("#2a2a30", "#f2ece0"),
    "shoqan_spawn_egg": ("#1e2a3a", "#e8c050"),
    "al_farabi_spawn_egg": ("#1e3a6a", "#f4f0e6"),
}


def hexa(h):
    h = h.lstrip("#")
    return (int(h[0:2], 16), int(h[2:4], 16), int(h[4:6], 16), 255)


def textures():
    for name, (rows, pal) in ART.items():
        c = G.Canvas(16, 16, seed=len(name) * 11)
        for y, row in enumerate(rows):
            for x, ch in enumerate(row[:16]):
                if ch != "." and ch in pal:
                    c.set(x, y, hexa(pal[ch]))
        c.noise(0.04)
        if name not in ("kieli_qyl", "tas_kaila", "tas_balta", "tas_kurek", "dau_shoqpary"):
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


def models():
    handheld = ("tas_kaila", "tas_balta", "tas_kurek", "dau_shoqpary")
    for name in ART:
        G.simple_item(name, handheld=name in handheld)
    for name in EGGS:
        G.simple_item(name)
    # огромная дубина дәу
    G.write_json(f"{G.A}/models/item/dau_shoqpary.json", {
        "parent": "minecraft:item/handheld", "textures": {"layer0": f"{NS}:item/dau_shoqpary"},
        "display": {"thirdperson_righthand": {"rotation": [0, -90, 55], "translation": [0, 6, 1], "scale": [1.5, 1.5, 1.0]},
                    "firstperson_righthand": {"rotation": [0, -90, 25], "translation": [1.13, 3.2, 1.13], "scale": [1.0, 1.0, 1.0]}}})


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
        "entity.balbal.awake": entry("balbal", ev("minecraft:block.grindstone.use", 0.5, 1.5), ev("minecraft:entity.iron_golem.repair", 0.5)),
        "entity.baqsy_kolenkesi.wave": entry("baqsy_wave", {"name": f"{NS}:kobyz/kobyz1", "pitch": 0.7},
                                             {"name": f"{NS}:kobyz/kobyz3", "pitch": 0.65}),
        "entity.baqsy_kolenkesi.ambient": entry("baqsy", ev("minecraft:particle.soul_escape", 0.7, 2.0), ev("minecraft:entity.evoker.ambient", 0.6)),
        "entity.dau.roar": entry("dau_roar", ev("minecraft:entity.ravager.roar", 0.6, 2.0)),
        "entity.dau.slam": entry("dau_slam", ev("minecraft:entity.generic.explode", 0.6, 1.5), ev("minecraft:block.stone.break", 0.5, 2.0)),
        "entity.dau.hit": entry("dau_hit", ev("minecraft:entity.iron_golem.attack", 0.6, 1.5)),
        "entity.dau.ambient": entry("dau", ev("minecraft:entity.ravager.ambient", 0.6, 1.5), ev("minecraft:entity.polar_bear.warning", 0.5)),
        "entity.dau.hurt": entry(None, ev("minecraft:entity.ravager.hurt", 0.6)),
        "entity.dau.death": entry(None, ev("minecraft:entity.ravager.death", 0.55)),
        "entity.aqboken.ambient": entry("saiga", ev("minecraft:entity.goat.ambient", 1.3)),
        "entity.aqboken.hurt": entry(None, ev("minecraft:entity.goat.hurt", 1.3)),
        "entity.aqboken.death": entry(None, ev("minecraft:entity.goat.death", 1.3)),
        "entity.barys.ambient": entry("barys", ev("minecraft:entity.ocelot.ambient", 0.6), ev("minecraft:entity.cat.purr", 0.6)),
        "entity.barys.angry": entry("barys_angry", ev("minecraft:entity.cat.hiss", 0.5, 1.5), ev("minecraft:entity.polar_bear.warning", 1.4)),
        "entity.barys.hurt": entry(None, ev("minecraft:entity.ocelot.hurt", 0.6)),
        "entity.barys.death": entry(None, ev("minecraft:entity.ocelot.death", 0.6)),
        "entity.qorqyt_ata.kui": entry("korkyt", {"name": f"{NS}:kobyz/korkyt", "stream": True}),
        "entity.qorqyt_ata.vanish": entry("korkyt_vanish", ev("minecraft:block.amethyst_block.resonate", 0.8, 1.5),
                                          ev("minecraft:entity.illusioner.mirror_move", 1.2)),
    })
    G.write_json(path, data)


def it(name, w, lo=1, hi=1):
    x = {"type": "minecraft:item", "name": name, "weight": w}
    if hi > 1:
        x["modifier"] = [{"type": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}}]
    return x


def loot():
    D, W = G.D, G.write_json
    kp = {"type": "minecraft:killed_by_player"}

    def table(path, pools, kind=None):
        if kind is None:
            kind = "minecraft:entity" if path.startswith("entities") else "minecraft:chest"
        W(f"{D}/loot_table/{path}.json", {"type": kind, "pools": pools, "random_sequence": f"{NS}:{path}"})
    table("entities/balbal", [{"rolls": 1, "entries": [it(e("ezhelgi_tas"), 1, 1, 2)]},
                              {"rolls": 1, "entries": [it("minecraft:cobblestone", 1, 1, 4)]}])
    table("entities/baqsy_kolenkesi", [{"rolls": 1, "entries": [it(e("kieli_qyl"), 1)]},
                                       {"rolls": 1, "entries": [it("minecraft:soul_sand", 1), {"type": "minecraft:empty", "weight": 2}]}])
    table("entities/dau", [{"rolls": 1, "entries": [it("minecraft:bone", 1, 3, 8)]},
                           {"rolls": 1, "entries": [it(e("saka_gold"), 2, 2, 5), it("minecraft:gold_ingot", 3, 3, 8)]},
                           {"rolls": 1, "entries": [it("minecraft:leather", 1, 2, 5)]}])
    table("entities/aqboken", [{"rolls": 1, "entries": [it(e("aqboken_eti"), 1, 1, 2)]},
                               {"rolls": 1, "entries": [it("minecraft:leather", 1), {"type": "minecraft:empty", "weight": 1}]}])
    table("entities/barys", [{"rolls": 1, "entries": [it("minecraft:leather", 1, 1, 2)]},
                             {"rolls": 1, "entries": [it("minecraft:white_wool", 1), {"type": "minecraft:empty", "weight": 2}]}])
    table("entities/qaraqshy", [{"rolls": 1, "entries": [it("minecraft:emerald", 2, 1, 2), it("minecraft:gold_nugget", 3, 2, 6),
                                                         {"type": "minecraft:empty", "weight": 4}]},
                                {"rolls": 1, "condition": kp, "entries": [it(e("otyrar_tengesi"), 1, 1, 3),
                                                                          {"type": "minecraft:empty", "weight": 3}]}])
    for n in ("auyldas", "abai", "shoqan", "al_farabi", "qorqyt_ata"):
        table("entities/" + n, [])
    goods = [it(e("syrmak"), 8, 1, 3), it(e("alasha"), 8, 1, 3), it(e("kumys"), 10, 1, 3), it(e("shubat"), 8, 1, 3),
             it(e("baursak"), 12, 2, 6), it(e("kazy"), 8, 1, 3), it("minecraft:emerald", 6, 1, 4), it(e("saka_gold"), 6, 1, 3),
             it("minecraft:gold_ingot", 6, 1, 3), it("minecraft:sugar", 6, 2, 6), it("minecraft:cocoa_beans", 4, 1, 4),
             it("minecraft:amethyst_shard", 4, 1, 4), it(e("torsyk"), 3), it("minecraft:saddle", 3)]
    table("chests/caravanserai", [{"rolls": {"type": "minecraft:uniform", "min": 4, "max": 7}, "entries": goods}])
    table("chests/caravanserai_ruin", [{"rolls": {"type": "minecraft:uniform", "min": 2, "max": 5}, "entries": [
        it("minecraft:bone", 10, 1, 4), it("minecraft:rotten_flesh", 8, 1, 3), it(e("saka_gold"), 4, 1, 2),
        it("minecraft:gold_nugget", 8, 2, 8), it(e("otyrar_tengesi"), 4, 1, 3), it(e("syrmak"), 4), it("minecraft:string", 6, 1, 4)]}])
    table("chests/otrar_cache", [
        {"rolls": {"type": "minecraft:uniform", "min": 3, "max": 6}, "entries": [
            it(e("otyrar_tengesi"), 14, 2, 6), it(e("saka_gold"), 10, 1, 4), it("minecraft:gold_ingot", 10, 2, 5),
            it(e("qumyra"), 6), it(e("qoljazba"), 4), it("minecraft:emerald", 8, 1, 4), it("minecraft:brush", 3),
            it("minecraft:book", 6, 1, 3), it("minecraft:lapis_lazuli", 6, 2, 8)]},
        {"rolls": 1, "entries": [it(e("altyn_qanjar"), 2), it(e("bugy_tumary"), 1), it(e("qasqyr_bilezigi"), 1),
                                 it("minecraft:golden_apple", 3), {"type": "minecraft:empty", "weight": 8}]}])
    table("chests/bandit", [{"rolls": {"type": "minecraft:uniform", "min": 3, "max": 6}, "entries": [
        it(e("otyrar_tengesi"), 10, 1, 4), it("minecraft:emerald", 8, 1, 3), it(e("qylysh"), 3), it("minecraft:arrow", 8, 4, 12),
        it(e("kurt"), 8, 2, 5), it(e("saka_gold"), 5, 1, 3), it("minecraft:leather", 6, 1, 4), it(e("qumyra"), 3)]}])
    table("chests/dau", [{"rolls": {"type": "minecraft:uniform", "min": 3, "max": 6}, "entries": [
        it("minecraft:bone", 12, 2, 6), it("minecraft:gold_ingot", 10, 2, 6), it(e("saka_gold"), 8, 1, 4),
        it("minecraft:iron_ingot", 10, 2, 6), it("minecraft:cooked_beef", 8, 2, 5), it("minecraft:diamond", 2, 1, 2),
        it(e("ezhelgi_tas"), 4, 1, 2), it(e("dau_shoqpary"), 1)]}])
    table("archaeology/otrar", [{"rolls": 1, "entries": [
        it(e("otyrar_tengesi"), 10), it(e("qumyra"), 5), it(e("qoljazba"), 2), it("minecraft:gold_nugget", 6),
        it("minecraft:emerald", 3), it(e("saka_gold"), 2), it("minecraft:brick", 6), it("minecraft:lapis_lazuli", 4),
        it("minecraft:arms_up_pottery_sherd", 2), it("minecraft:burn_pottery_sherd", 2)]}], kind="minecraft:archaeology")


def recipes():
    sh, sl, W, D = G.shaped, G.shapeless, G.write_json, G.D
    for old in ("kumys", "shubat"):
        p = f"{D}/recipe/{old}.json"
        if os.path.exists(p):
            os.remove(p)
    # инструменты: нужна киелі қыл (струна из тени бақсы)
    sh("dombyra", ["  Q", " T ", "PP "], {"Q": e("kieli_qyl"), "T": "minecraft:stick", "P": "#minecraft:planks"}, e("dombyra"))
    sh("qobyz", ["  Q", "PLS", "PP "], {"Q": e("kieli_qyl"), "S": "minecraft:string", "P": "#minecraft:planks",
                                        "L": "minecraft:leather"}, e("qobyz"))
    sh("torsyk", [" S ", "L L", "LLL"], {"S": "minecraft:string", "L": "minecraft:leather"}, e("torsyk"))
    sh("kazy", ["MMS"], {"M": e("pisken_jylqy_eti"), "S": "minecraft:string"}, e("kazy"), 3, "misc")
    sh("tas_kaila", ["SSS", " T ", " T "], {"S": e("ezhelgi_tas"), "T": "minecraft:stick"}, e("tas_kaila"), category="equipment")
    sh("tas_balta", ["SS", "ST", " T"], {"S": e("ezhelgi_tas"), "T": "minecraft:stick"}, e("tas_balta"), category="equipment")
    sh("tas_kurek", ["S", "T", "T"], {"S": e("ezhelgi_tas"), "T": "minecraft:stick"}, e("tas_kurek"), category="equipment")
    for kind, time in (("smelting", 200), ("smoking", 100), ("campfire_cooking", 600)):
        for raw, cooked in (("jylqy_eti", "pisken_jylqy_eti"), ("aqboken_eti", "pisken_aqboken_eti")):
            W(f"{D}/recipe/{cooked}_{kind}.json", {"type": f"minecraft:{kind}", "category": "food",
                                                   "ingredient": e(raw), "result": {"id": e(cooked)},
                                                   "experience": 0.35, "cookingtime": time})
    W(f"{D}/tags/item/ezhelgi_tas_repairable.json", {"values": [e("ezhelgi_tas")]})


def tags_spawns():
    D, MC, W = G.D, G.MC, G.write_json
    W(f"{MC}/tags/item/pickaxes.json", {"values": [e("tas_kaila")]})
    W(f"{MC}/tags/item/axes.json", {"values": [e("tas_balta")]})
    W(f"{MC}/tags/item/shovels.json", {"values": [e("tas_kurek")]})
    W(f"{MC}/tags/item/swords.json", {"values": [e("qylysh"), e("altyn_qylysh"), e("aruaq_qylysh"), e("tyrnaq_qanjar"),
                                                 e("qamshy"), e("dau_shoqpary")]})
    W(f"{MC}/tags/item/meat.json", {"values": [e("jylqy_eti"), e("pisken_jylqy_eti"), e("aqboken_eti"), e("pisken_aqboken_eti")]})
    W(f"{D}/tags/worldgen/biome/saiga_habitat.json", {"values": ["#ezhelgi:steppe", "minecraft:desert", "minecraft:windswept_savanna"]})
    W(f"{D}/tags/worldgen/biome/mountains.json", {"values": [
        "#minecraft:is_mountain", "minecraft:windswept_hills", "minecraft:windswept_gravelly_hills", "minecraft:windswept_forest"]})
    W(f"{D}/tags/worldgen/biome/barys_habitat.json", {"values": [
        "minecraft:snowy_slopes", "minecraft:grove", "minecraft:jagged_peaks", "minecraft:frozen_peaks",
        "minecraft:stony_peaks", "minecraft:windswept_hills", "minecraft:snowy_taiga"]})
    W(f"{D}/tags/worldgen/biome/caravanserai.json", {"values": ["#ezhelgi:steppe", "minecraft:desert"]})
    W(f"{D}/tags/worldgen/biome/ruins.json", {"values": ["minecraft:desert", "#minecraft:is_badlands", "minecraft:savanna",
                                                         "minecraft:savanna_plateau"]})
    mod = f"{D}/forge/biome_modifier"

    def add(name, tag, weight, lo, hi):
        W(f"{mod}/add_{name}.json", {"type": "forge:add_spawns", "biomes": tag, "spawners": [
            {"type": e(name), "count": {"type": "minecraft:uniform", "min_inclusive": lo, "max_inclusive": hi}, "weight": weight}]})
    add("aqboken", "#ezhelgi:saiga_habitat", 8, 4, 8)
    add("barys", "#ezhelgi:barys_habitat", 4, 1, 1)
    add("dau", "#ezhelgi:mountains", 2, 1, 1)
    add("balbal", "#ezhelgi:steppe", 3, 1, 1)
    add("qorqyt_ata", "#ezhelgi:mountains", 1, 1, 1)
    # картины
    for name, (w, h, _) in __import__("v6_paintings").PAINTINGS.items():
        W(f"{D}/painting_variant/{name}.json", {"asset_id": e(name), "width": w, "height": h,
                                               "title": {"translate": f"painting.ezhelgi.{name}.title", "color": "yellow"},
                                               "author": {"translate": "painting.ezhelgi.author", "color": "gray"}})
    W(f"{MC}/tags/painting_variant/placeable.json", {"values": [e(n) for n in __import__("v6_paintings").PAINTINGS]})


def pool(name, elements):
    G.write_json(f"{G.D}/worldgen/template_pool/{name}.json", {
        "fallback": "minecraft:empty",
        "elements": [{"weight": w, "element": {
            "element_type": "minecraft:single_pool_element", "location": e(loc),
            "processors": "minecraft:empty", "projection": "rigid"}} for loc, w in elements]})


def structure(name, biomes, pool_name, adapt, depth, spacing, separation, salt, monsters=None):
    overrides = {}
    if monsters:
        overrides["monster"] = {"bounding_box": "full", "spawns": [
            {"type": t, "count": {"type": "minecraft:uniform", "min_inclusive": lo, "max_inclusive": hi}, "weight": w}
            for t, w, lo, hi in monsters]}
    G.write_json(f"{G.D}/worldgen/structure/{name}.json", {
        "type": "minecraft:jigsaw", "biomes": biomes, "step": "surface_structures",
        "spawn_overrides": overrides, "terrain_adaptation": adapt,
        "start_pool": e(pool_name), "size": 1, "start_height": {"absolute": depth},
        "project_start_to_heightmap": "WORLD_SURFACE_WG", "max_distance_from_center": 116,
        "use_expansion_hack": False})
    G.write_json(f"{G.D}/worldgen/structure_set/{name}.json", {
        "structures": [{"structure": e(name), "weight": 1}],
        "placement": {"type": "minecraft:random_spread", "spacing": spacing, "separation": separation, "salt": salt}})


def worldgen():
    import v5_world
    import v6_world
    for name, tpl in v5_world.build_all().items():
        if name.startswith("runa/"):
            G.write_template(name, tpl)
    for name, tpl in v6_world.build_all().items():
        G.write_template(name, tpl)
    # рунные камни: возле них появляются тени бақсы и балбалы
    structure("runa_tas", "#ezhelgi:rune_stone", "runa/start", "beard_thin", -1, 26, 9, 640021,
              monsters=[(e("baqsy_kolenkesi"), 3, 1, 1), (e("balbal"), 2, 1, 1), ("minecraft:zombie", 4, 1, 2)])
    pool("caravanserai/start", [("caravanserai/intact", 1)])
    structure("caravanserai", "#ezhelgi:caravanserai", "caravanserai/start", "beard_box", -1, 46, 16, 271828)
    pool("caravanserai_ruin/start", [("caravanserai/ruined", 1)])
    structure("caravanserai_ruin", "#ezhelgi:ruins", "caravanserai_ruin/start", "beard_box", -1, 50, 18, 314159,
              monsters=[("minecraft:husk", 6, 1, 3), ("minecraft:zombie", 3, 1, 2), (e("qaraqshy"), 5, 1, 2)])
    pool("bori_musini/start", [("bori_musini", 1)])
    structure("bori_musini", "#ezhelgi:has_kurgan", "bori_musini/start", "beard_box", -1, 72, 26, 161803)
    pool("otyrar/start", [("otyrar", 1)])
    structure("otyrar", "#ezhelgi:ruins", "otyrar/start", "beard_thin", -6, 96, 40, 1219,
              monsters=[(e("qaraqshy"), 8, 1, 2), ("minecraft:husk", 4, 1, 2)])
    pool("dau_mekeni/start", [("dau_mekeni", 1)])
    structure("dau_mekeni", "#ezhelgi:mountains", "dau_mekeni/start", "beard_thin", -10, 60, 22, 271)


BOOKS = {
    1: ["Қара сөздер (Слова назидания) — 45 коротких философских рассуждений, которые Абай Құнанбайұлы записывал в 1890-х годах.",
        "В первом Слове Абай размышляет, как прожить оставшиеся годы с пользой, и решает доверить бумаге свои мысли.",
        "Абай зовёт молодёжь учиться: наука, ремесло и честный труд ценнее богатства, праздности и пустых споров.",
        "Абай перевёл на казахский язык стихи Пушкина, Лермонтова и басни Крылова и сочинял к своим стихам мелодии."],
    2: ["Человек становится человеком через знание, совесть и доброе сердце — эту мысль Абай повторяет во многих Словах.",
        "Он осуждает лень, зависть, бахвальство и обман — то, что мешает людям жить в согласии.",
        "В одном из Слов о первенство спорят Воля (Қайрат), Разум (Ақыл) и Сердце (Жүрек), и Наука мирит их: нужно всё вместе.",
        "Абай мечтал о «толық адам» — цельном человеке, у которого ум, воля и сердце служат добру."],
    3: ["Абай родился в 1845 году в Чингизских горах. С детства учил арабский, персидский и русский языки.",
        "Его песни и стихи пели в аулах ещё при жизни поэта, многие из них казахи поют и сегодня.",
        "Абай верил, что казахам нужно учиться у других народов и сохранять при этом свой язык и обычаи.",
        "Главный совет Қара сөздер в пересказе: не гонись за похвалой, а ищи знание и будь полезен людям."],
}

NAMES = {
    "item.ezhelgi.jylqy_eti": ("Сырая конина", "Шикі жылқы еті", "Raw Horse Meat"),
    "item.ezhelgi.pisken_jylqy_eti": ("Жареная конина", "Піскен жылқы еті", "Cooked Horse Meat"),
    "item.ezhelgi.aqboken_eti": ("Сырое мясо сайгака", "Шикі ақбөкен еті", "Raw Saiga Meat"),
    "item.ezhelgi.pisken_aqboken_eti": ("Жареное мясо сайгака", "Піскен ақбөкен еті", "Cooked Saiga Meat"),
    "item.ezhelgi.torsyk": ("Торсык (пустой)", "Торсық (бос)", "Torsyk Flask (empty)"),
    "item.ezhelgi.torsyk_qymyz": ("Торсык с кумысом", "Қымызды торсық", "Torsyk of Kumys"),
    "item.ezhelgi.torsyk_shubat": ("Торсык с шубатом", "Шұбатты торсық", "Torsyk of Shubat"),
    "item.ezhelgi.kieli_qyl": ("Киелі қыл (мистическая струна)", "Киелі қыл", "Sacred String"),
    "item.ezhelgi.ezhelgi_tas": ("Древний камень", "Ежелгі тас", "Ancient Stone"),
    "item.ezhelgi.tas_kaila": ("Кирка из древнего камня", "Тас кайла", "Ancient Stone Pickaxe"),
    "item.ezhelgi.tas_balta": ("Топор из древнего камня", "Тас балта", "Ancient Stone Axe"),
    "item.ezhelgi.tas_kurek": ("Лопата из древнего камня", "Тас күрек", "Ancient Stone Shovel"),
    "item.ezhelgi.dau_shoqpary": ("Дубина дәу", "Дәу шоқпары", "Giant's Club"),
    "item.ezhelgi.otyrar_tengesi": ("Монета Отрара", "Отырар теңгесі", "Otrar Coin"),
    "item.ezhelgi.qoljazba": ("Древняя рукопись", "Ежелгі қолжазба", "Ancient Manuscript"),
    "item.ezhelgi.qumyra": ("Глиняный кувшин", "Құмыра", "Clay Jug"),
    "item.ezhelgi.balbal_spawn_egg": ("Яйцо призыва балбала", "Балбал жұмыртқасы", "Balbal Spawn Egg"),
    "item.ezhelgi.baqsy_kolenkesi_spawn_egg": ("Яйцо призыва тени бақсы", "Бақсы көлеңкесі жұмыртқасы", "Baqsy Shadow Spawn Egg"),
    "item.ezhelgi.dau_spawn_egg": ("Яйцо призыва дәу", "Дәу жұмыртқасы", "Dau Spawn Egg"),
    "item.ezhelgi.aqboken_spawn_egg": ("Яйцо призыва сайгака", "Ақбөкен жұмыртқасы", "Saiga Spawn Egg"),
    "item.ezhelgi.barys_spawn_egg": ("Яйцо призыва снежного барса", "Барыс жұмыртқасы", "Snow Leopard Spawn Egg"),
    "item.ezhelgi.qorqyt_ata_spawn_egg": ("Яйцо призыва Қорқыт ата", "Қорқыт ата жұмыртқасы", "Qorqyt Ata Spawn Egg"),
    "item.ezhelgi.qaraqshy_spawn_egg": ("Яйцо призыва разбойника", "Қарақшы жұмыртқасы", "Bandit Spawn Egg"),
    "item.ezhelgi.auyldas_spawn_egg": ("Яйцо призыва степного жителя", "Ауылдас жұмыртқасы", "Steppe Villager Spawn Egg"),
    "item.ezhelgi.abai_spawn_egg": ("Яйцо призыва Абая", "Абай жұмыртқасы", "Abai Spawn Egg"),
    "item.ezhelgi.shoqan_spawn_egg": ("Яйцо призыва Шокана", "Шоқан жұмыртқасы", "Shoqan Spawn Egg"),
    "item.ezhelgi.al_farabi_spawn_egg": ("Яйцо призыва аль-Фараби", "Әл-Фараби жұмыртқасы", "Al-Farabi Spawn Egg"),
    "entity.ezhelgi.balbal": ("Балбал", "Балбал", "Balbal"),
    "entity.ezhelgi.baqsy_kolenkesi": ("Тень бақсы", "Бақсы көлеңкесі", "Baqsy Shadow"),
    "entity.ezhelgi.dau": ("Дәу (великан)", "Дәу", "Dau the Giant"),
    "entity.ezhelgi.aqboken": ("Сайгак", "Ақбөкен", "Saiga"),
    "entity.ezhelgi.barys": ("Снежный барс", "Барыс", "Snow Leopard"),
    "entity.ezhelgi.qorqyt_ata": ("Қорқыт ата", "Қорқыт ата", "Qorqyt Ata"),
    "entity.ezhelgi.qaraqshy": ("Разбойник", "Қарақшы", "Bandit"),
    "entity.ezhelgi.auyldas": ("Степной житель", "Ауылдас", "Steppe Villager"),
    "entity.ezhelgi.abai": ("Абай Құнанбайұлы", "Абай Құнанбайұлы", "Abai Qunanbaiuly"),
    "entity.ezhelgi.shoqan": ("Шоқан Уәлиханов", "Шоқан Уәлиханов", "Shoqan Ualikhanov"),
    "entity.ezhelgi.al_farabi": ("Әл-Фараби", "Әл-Фараби", "Al-Farabi"),
    "tooltip.ezhelgi.torsyk": ("ПКМ — выпить, Shift+ПКМ — перелить бутылку, ПКМ по кобыле/верблюдице — надоить",
                               "ТЖБ — ішу, Shift+ТЖБ — бөтелкеден құю, биені/інгенді сауу",
                               "RMB drink, Shift+RMB pour a bottle in, RMB a mare/camel to milk"),
    "tooltip.ezhelgi.torsyk.portions": ("Порций: %s / %s", "Үлес: %s / %s", "Portions: %s / %s"),
    "message.ezhelgi.milk.horse_empty": ("Кобыла уже подоена — подожди немного", "Бие сауылды — сәл күт", "This mare was just milked"),
    "message.ezhelgi.milk.camel_empty": ("Верблюдица уже подоена — подожди немного", "Інген сауылды — сәл күт", "This camel was just milked"),
    "message.ezhelgi.korkyt.greet": ("Қорқыт ата: «Послушай, путник, мой кюй…»", "Қорқыт ата: «Тыңда, жолаушы, күйімді…»",
                                     "Qorqyt Ata: \"Listen, traveller, to my kui...\""),
    "message.ezhelgi.korkyt.gift": ("Қорқыт ата дарит тебе кобыз и исчезает…", "Қорқыт ата саған қобыз сыйлап, ғайып болды…",
                                    "Qorqyt Ata gives you a kobyz and vanishes..."),
    "painting.ezhelgi.author": ("Әл-Фараби", "Әл-Фараби", "Al-Farabi"),
    "painting.ezhelgi.juldyzdy_tun.title": ("Звёздная ночь над аулом", "Жұлдызды түн", "Starry Night over the Aul"),
    "painting.ezhelgi.temir_qazyq.title": ("Темір қазық — Полярная звезда", "Темірқазық", "Temir Qazyq, the Pole Star"),
    "painting.ezhelgi.jeti_qaraqshy.title": ("Жеті қарақшы — Большая Медведица", "Жеті қарақшы", "Seven Robbers (Big Dipper)"),
    "painting.ezhelgi.ay_dala.title": ("Луна над степью", "Ай мен дала", "Moon over the Steppe"),
    "painting.ezhelgi.astrolabe.title": ("Астролябия", "Астролябия", "Astrolabe"),
    "painting.ezhelgi.urker.title": ("Үркер — Плеяды", "Үркер", "Urker (Pleiades)"),
    "subtitles.ezhelgi.balbal": ("Балбал оживает", "Балбал тірілді", "Balbal awakens"),
    "subtitles.ezhelgi.baqsy_wave": ("Волна кобыза", "Қобыз толқыны", "Kobyz wave"),
    "subtitles.ezhelgi.baqsy": ("Шёпот бақсы", "Бақсы сыбыры", "Baqsy whispers"),
    "subtitles.ezhelgi.dau_roar": ("Рёв дәу", "Дәу ақырады", "Dau roars"),
    "subtitles.ezhelgi.dau_slam": ("Удар дубиной о землю", "Шоқпар жерге тиді", "Club slam"),
    "subtitles.ezhelgi.dau_hit": ("Удар дубины", "Шоқпар соққысы", "Club hit"),
    "subtitles.ezhelgi.dau": ("Дәу ворчит", "Дәу күңкілдейді", "Dau grumbles"),
    "subtitles.ezhelgi.saiga": ("Сайгак блеет", "Ақбөкен маңырайды", "Saiga bleats"),
    "subtitles.ezhelgi.barys": ("Барыс урчит", "Барыс мырылдайды", "Snow leopard purrs"),
    "subtitles.ezhelgi.barys_angry": ("Барыс шипит", "Барыс ысылдайды", "Snow leopard hisses"),
    "subtitles.ezhelgi.korkyt": ("Кюй Қорқыта", "Қорқыт күйі", "Qorqyt's kui"),
    "subtitles.ezhelgi.korkyt_vanish": ("Қорқыт ата исчезает", "Қорқыт ата ғайып болды", "Qorqyt Ata vanishes"),
}


def run(g):
    global G
    G = g
    textures()
    models()
    sounds()
    loot()
    recipes()
    tags_spawns()
    worldgen()
    import v6_skins
    import v6_paintings
    v6_skins.run(os.path.join(G.A, "textures/entity"))
    v6_paintings.run(os.path.join(G.A, "textures/painting"))
    names = dict(NAMES)
    for vol, pages in BOOKS.items():
        for i, text in enumerate(pages, 1):
            # книги на русском во всех языках, кроме казахского заголовка
            names[f"book.ezhelgi.qara_soz.{vol}.{i}"] = (text, text, text)
    G.NAMES.update(names)
