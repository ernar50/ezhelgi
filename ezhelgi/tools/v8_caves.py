"""v8: места (Туркестан, Чарын, Окжетпес) и пещеры (Ақмешіт, Кене кен, келья дервиша, подземный мир Ер Төстіка),
пещерные существа. Вызывается из v8_assets.run.
"""
import json
import os

from PIL import Image

import v8_art
import v8_world
import v7_items

NS = "ezhelgi"
G = None


def e(n):
    return f"{NS}:{n}"


def W(path, obj):
    G.write_json(path, obj)


def save(img, rel):
    path = os.path.join(G.A, "textures", rel + ".png")
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path)


def it(name, w, lo=1, hi=1, ench=False, extra=None):
    d = {"type": "minecraft:item", "name": name if ":" in name else e(name), "weight": w}
    mods = []
    if hi > 1 or lo != 1:
        mods.append({"type": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}})
    if ench:
        mods.append({"type": "minecraft:enchant_randomly", "options": "#minecraft:on_random_loot"})
    if extra:
        mods += extra
    if mods:
        d["modifier"] = mods
    return d


def chest(name, rolls, entries, extra_pools=()):
    pools = [{"rolls": {"type": "minecraft:uniform", "min": rolls[0], "max": rolls[1]}, "entries": entries}]
    pools += list(extra_pools)
    W(f"{G.D}/loot_table/chests/{name}.json", {"type": "minecraft:chest", "pools": pools,
                                                "random_sequence": f"{NS}:chests/{name}"})


def one_of(entries, empty=0):
    if empty:
        entries = entries + [{"type": "minecraft:empty", "weight": empty}]
    return {"rolls": 1, "entries": entries}


EPIC = [f"{s}_{p}" for s in ("qobylandy", "alpamys", "er_targyn") for p in ("dulyga", "sauyt", "shalbar", "etik")]


def loot():
    chest("turkistan_bazaar", (3, 6), [
        it("saka_gold", 12, 2, 6), it("otyrar_tengesi", 14, 2, 8), it("baursak", 16, 3, 8), it("kurt", 12, 2, 6),
        it("qumyra", 8), it("qoljazba", 6), it("tekemet", 6, 1, 3), it("minecraft:emerald", 10, 2, 6),
        it("minecraft:dried_kelp", 4, 2, 6), it("minecraft:apple", 10, 2, 6), it("minecraft:book", 4, 1, 3),
        it("tymaq", 2), it("shapan", 3), it("taqiya", 4), it("kumis_belbeu", 2)])
    chest("sharyn_cave", (4, 7), [
        it("saka_gold", 14, 3, 8), it("minecraft:gold_ingot", 10, 2, 5), it("minecraft:diamond", 4, 1, 3),
        it("minecraft:redstone", 10, 4, 12), it("arqar_muiizi", 8, 1, 2), it("muiiz_sadaq", 3, ench=True),
        it("minecraft:name_tag", 4), it("minecraft:saddle", 6), it("arqar_syrgasy", 2), it("minecraft:experience_bottle", 6, 2, 6)])
    chest("oqzhetpes_top", (4, 6), [
        it("minecraft:diamond", 8, 1, 4), it("saka_gold", 12, 4, 10), it("minecraft:golden_apple", 6),
        it("minecraft:enchanted_golden_apple", 1), it("saukele", 3), it("kelin_koilek", 3), it("uki_borik", 3),
        it("minecraft:book", 5, 1, 1, ench=True), it("tumar", 4), it("altyn_belbeu", 2)])
    chest("kone_ken", (4, 8), [
        it("qola", 18, 2, 6), it("qola_qanjar", 6, ench=True), it("qola_balta", 6, ench=True),
        it("minecraft:raw_copper", 16, 4, 12), it("minecraft:copper_ingot", 12, 2, 8), it("minecraft:iron_pickaxe", 3),
        it("minecraft:torch", 12, 4, 12), it("minecraft:bread", 8, 2, 4), it("minecraft:raw_gold", 6, 1, 4),
        it("qumyra", 4), it("tas_kaila", 4)])
    chest("darwish", (3, 5), [
        it("minecraft:book", 10, 1, 1, ench=True), it("qoljazba", 12, 1, 3), it("minecraft:candle", 10, 2, 5),
        it("minecraft:paper", 10, 3, 9), it("minecraft:honey_bottle", 6, 1, 3), it("minecraft:experience_bottle", 8, 2, 5),
        it("kurt", 8, 2, 5), it("tumar", 3)])
    chest("zher_asty_hoard", (5, 9), [
        it("saka_gold", 18, 6, 16), it("minecraft:gold_block", 6, 1, 3), it("minecraft:diamond", 10, 2, 6),
        it("minecraft:netherite_scrap", 3, 1, 2), it("minecraft:enchanted_golden_apple", 2),
        it("minecraft:book", 8, 1, 1, ench=True), it("minecraft:experience_bottle", 10, 4, 10),
        it("qola", 8, 3, 8), it("dau_shoqpary", 3)],
        [one_of([it(p, 1) for p in EPIC], empty=4), one_of([it("shoiyn_qulaq", 1)], empty=2)])
    # мобы
    ent = lambda n, pools: W(f"{G.D}/loot_table/entities/{n}.json", {"type": "minecraft:entity", "pools": pools,
                                                                      "random_sequence": f"{NS}:entities/{n}"})
    looting = {"type": "minecraft:enchanted_count_increase", "enchantment": "minecraft:looting",
               "count": {"type": "minecraft:uniform", "min": 0, "max": 1}}
    ent("zhylan", [{"rolls": 1, "entries": [it("minecraft:string", 3, 0, 2, extra=[looting]),
                                            it("minecraft:spider_eye", 2, extra=[looting]), {"type": "minecraft:empty", "weight": 2}]}])
    ent("zhyn", [{"rolls": 1, "entries": [it("minecraft:blaze_powder", 3, 1, 2), it("minecraft:ender_pearl", 2),
                                          it("minecraft:glowstone_dust", 3, 1, 3), it("minecraft:experience_bottle", 1)]}])
    ent("shahmaran", [{"rolls": 1, "entries": [it("zhylan_tumary", 1)]},
                      {"rolls": 1, "entries": [it("minecraft:emerald", 1, 4, 10)]}])
    ent("shoiynqulaq", [{"rolls": 1, "entries": [it("shoiyn_qulaq", 1)]},
                        {"rolls": 1, "entries": [it("minecraft:iron_block", 1, 2, 5)]},
                        {"rolls": 1, "entries": [it("saka_gold", 1, 10, 20)]},
                        {"rolls": 2, "entries": [it(p, 1) for p in EPIC]}])
    ent("darwish", [])


def biome_tags():
    D = G.D
    W(f"{D}/tags/worldgen/biome/turkistan.json", {"values": ["minecraft:plains", "minecraft:desert", "minecraft:savanna"]})
    W(f"{D}/tags/worldgen/biome/sharyn.json", {"values": ["minecraft:badlands", "minecraft:wooded_badlands", "minecraft:eroded_badlands",
                                                           "minecraft:savanna", "minecraft:windswept_savanna", "minecraft:desert"]})
    W(f"{D}/tags/worldgen/biome/oqzhetpes.json", {"values": ["minecraft:forest", "minecraft:birch_forest", "minecraft:taiga",
                                                              "minecraft:old_growth_birch_forest", "minecraft:old_growth_pine_taiga"]})
    W(f"{D}/tags/worldgen/biome/aqmeshit.json", {"values": ["#ezhelgi:mountains", "minecraft:savanna_plateau",
                                                             "minecraft:windswept_savanna", "minecraft:meadow"]})
    W(f"{D}/tags/worldgen/biome/kone_ken.json", {"values": ["#ezhelgi:steppe", "minecraft:desert", "minecraft:savanna", "minecraft:badlands"]})


def spawns():
    mod = f"{G.D}/forge/biome_modifier"
    W(f"{mod}/add_zhylan.json", {"type": "forge:add_spawns", "biomes": "#minecraft:is_overworld",
                                  "spawners": [{"type": e("zhylan"), "count": {"type": "minecraft:uniform", "min_inclusive": 1, "max_inclusive": 2}, "weight": 12}]})
    W(f"{mod}/add_zhyn.json", {"type": "forge:add_spawns", "biomes": "#minecraft:is_overworld",
                                "spawners": [{"type": e("zhyn"), "count": {"type": "minecraft:uniform", "min_inclusive": 1, "max_inclusive": 1}, "weight": 4}]})


EGGS = {"zhylan": ("#5a6a30", "#d2c482"), "zhyn": ("#2a2036", "#c02a2a"), "shahmaran": ("#78963c", "#eebe3c"),
        "shoiynqulaq": ("#46484e", "#966450"), "darwish": ("#e8e2d4", "#5a3a1e")}


def art():
    A = G.A
    src = os.path.join(A, "textures/entity/zhyn.png")
    if os.path.exists(src):
        for n, img in v8_art.zhyn_variants(Image.open(src)).items():
            img.save(os.path.join(A, "textures/entity", n + ".png"))
    v8_art.darwish_skin().img.save(os.path.join(A, "textures/entity/darwish.png"))
    for n, img in v8_art.cave_icons().items():
        save(img, "item/" + n)
        G.simple_item(n, handheld=n in ("qola_qanjar", "qola_balta"))
    for n, (b, s) in EGGS.items():
        save(v7_items.egg(b, s, len(n) + 3), f"item/{n}_spawn_egg")
        G.simple_item(f"{n}_spawn_egg")


def data():
    D = G.D
    W(f"{D}/tags/item/qola_repairable.json", {"values": [e("qola")]})
    for tag, item in (("swords", "qola_qanjar"), ("axes", "qola_balta")):
        path = f"{G.MC}/tags/item/{tag}.json"
        cur = json.load(open(path, encoding="utf-8")) if os.path.exists(path) else {"replace": False, "values": []}
        if e(item) not in cur["values"]:
            cur["values"].append(e(item))
        W(path, cur)
    sh = G.shaped
    sh("qola_qanjar", [" Q", "S "], {"Q": e("qola"), "S": "minecraft:stick"}, e("qola_qanjar"), category="equipment")
    sh("qola_balta", ["QQ", "QS", " S"], {"Q": e("qola"), "S": "minecraft:stick"}, e("qola_balta"), category="equipment")
    # қола = медь + олово (в Minecraft — медь и немного железа)
    W(f"{D}/recipe/qola.json", {"type": "minecraft:crafting_shapeless", "category": "misc",
                                "ingredients": ["minecraft:copper_ingot", "minecraft:copper_ingot", "minecraft:copper_ingot",
                                                "minecraft:iron_nugget"],
                                "result": {"id": e("qola"), "count": 2}})


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
        "entity.zhylan.hiss": entry("zhylan", ev("minecraft:entity.cat.hiss", 0.6, 0.8), ev("minecraft:entity.llama.spit", 0.5, 0.6)),
        "entity.zhyn.ambient": entry("zhyn", ev("minecraft:entity.vex.ambient", 0.6, 1.0), ev("minecraft:entity.ghast.ambient", 1.6, 0.4)),
        "entity.zhyn.hurt": entry(None, ev("minecraft:entity.vex.hurt", 0.6)),
        "entity.zhyn.death": entry(None, ev("minecraft:entity.vex.death", 0.5), ev("minecraft:entity.blaze.death", 0.7, 0.6)),
        "entity.shahmaran.speak": entry("shahmaran", ev("minecraft:entity.illusioner.ambient", 0.75), ev("minecraft:entity.evoker.ambient", 0.9, 0.8)),
        "entity.shoiynqulaq.roar": entry("shoiynqulaq", ev("minecraft:entity.ravager.roar", 0.5, 2.5)),
        "entity.shoiynqulaq.clap": entry("shoiyn_clap", ev("minecraft:block.anvil.land", 0.5, 3.0), ev("minecraft:entity.warden.sonic_boom", 0.9, 2.0)),
        "entity.shoiynqulaq.hurt": entry(None, ev("minecraft:entity.ravager.hurt", 0.5, 2.0), ev("minecraft:block.anvil.hit", 0.6, 1.0)),
        "entity.shoiynqulaq.death": entry(None, ev("minecraft:entity.ravager.death", 0.4, 3.0)),
    })
    W(path, data)


def run(g):
    global G
    G = g
    v8_world.run(g)
    biome_tags()
    loot()
    spawns()
    art()
    data()
    sounds()
    import v8_lang_caves
    G.NAMES.update(v8_lang_caves.names())
