"""v8: праздники аула, одежда, асық ату (затем — места, пещеры, природа, кухня, инструменты, мебель).
Вызывается из gen_assets.py после v7.
"""
import os

import v8_art
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


CLOTHES = {"shapan": "chest", "kamzol": "chest", "taqiya": "head", "kimeshek": "head", "saukele": "head",
           "uki_borik": "head", "kelin_koilek": "chest"}
EGGS8 = {"kelin": ("#f6f2ea", "#a01e2a"), "zhyrshy": ("#2e5a3a", "#e8e4dc")}


def aul_assets():
    A, D, MC = G.A, G.D, G.MC
    for n, f in v8_art.SKINS.items():
        f().img.save(os.path.join(A, "textures/entity", n + ".png"))
    for asset, img in v8_art.clothes_textures().items():
        save(img, "entity/equipment/humanoid/" + asset)
        W(f"{A}/equipment/{asset}.json", {"layers": {"humanoid": [{"texture": e(asset)}]}})
    for n, img in v8_art.icons().items():
        save(img, "item/" + n)
        G.simple_item(n)
    for n, (b, s) in EGGS8.items():
        save(v7_items.egg(b, s, len(n)), f"item/{n}_spawn_egg")
        G.simple_item(f"{n}_spawn_egg")
    # ряд асыков
    save(v8_art.bone_texture(), "block/asyq_bone")
    for n, m in v8_art.asyq_models(NS).items():
        W(f"{A}/models/block/asyq_qatar_{n}.json", m)
    variants = {}
    for facing, y in {"north": 0, "east": 90, "south": 180, "west": 270}.items():
        for n in range(6):
            variants[f"count={n},facing={facing}"] = {"model": e(f"block/asyq_qatar_{n}"), "y": y}
    W(f"{A}/blockstates/asyq_qatar.json", {"variants": variants})
    W(f"{A}/models/item/asyq_qatar.json", {"parent": e("block/asyq_qatar_5"),
                                           "display": {"gui": {"rotation": [30, 45, 0], "translation": [0, 3, 0], "scale": [0.9, 0.9, 0.9]}}})
    W(f"{A}/items/asyq_qatar.json", {"model": {"type": "minecraft:model", "model": e("item/asyq_qatar")}})
    # данные
    W(f"{D}/loot_table/blocks/asyq_qatar.json", {"type": "minecraft:block", "pools": [{"rolls": 1, "entries": [
        {"type": "minecraft:item", "name": e("asyq_qatar")}], "condition": {"type": "minecraft:survives_explosion"}}],
        "random_sequence": f"{NS}:blocks/asyq_qatar"})
    for n in ("shabandoz", "serke", "aqyn", "kelin", "zhyrshy"):
        W(f"{D}/loot_table/entities/{n}.json", {"type": "minecraft:entity", "pools": [], "random_sequence": f"{NS}:entities/{n}"})
    W(f"{D}/tags/item/cloth_repairable.json", {"values": ["#minecraft:wool"]})
    W(f"{MC}/tags/item/freeze_immune_wearables.json", {"values": [e("shapan"), e("tymaq"), e("uki_borik")]})
    for epic, structs in (("qobylandy", ["aidahar_uyasy"]), ("alpamys", ["yasawi"]), ("er_targyn", ["tamgaly"])):
        W(f"{D}/tags/worldgen/structure/zhyr_{epic}.json", {"values": [e(s) for s in structs]})
    sh = G.shaped
    sh("saqa", [" N ", "NAN", " N "], {"N": "minecraft:iron_nugget", "A": e("asyq")}, e("saqa"))
    sh("asyq_qatar", ["AAAAA"], {"A": e("asyq")}, e("asyq_qatar"))
    sh("shapan", ["W W", "WGW", "WWW"], {"W": "minecraft:blue_wool", "G": "minecraft:gold_nugget"}, e("shapan"), category="equipment")
    sh("kamzol", ["W W", "WGW"], {"W": "minecraft:red_wool", "G": "minecraft:gold_nugget"}, e("kamzol"), category="equipment")
    sh("taqiya", ["GWG"], {"W": "minecraft:green_wool", "G": "minecraft:gold_nugget"}, e("taqiya"), category="equipment")
    sh("kimeshek", ["WWW", "WRW"], {"W": "minecraft:white_wool", "R": "minecraft:red_dye"}, e("kimeshek"), category="equipment")
    sh("saukele", [" F ", "GRG", "PWP"], {"F": "minecraft:feather", "G": "minecraft:gold_ingot", "R": "minecraft:red_wool",
                                         "W": "minecraft:white_wool", "P": "minecraft:pearlescent_froglight"}, e("saukele"), category="equipment")
    sh("uki_borik", [" F ", "RRR", "LLL"], {"F": "minecraft:feather", "R": "minecraft:red_wool", "L": e("tulki_terisi")},
       e("uki_borik"), category="equipment")
    sh("kelin_koilek", ["R R", "WGW", "WWW"], {"R": "minecraft:red_wool", "W": "minecraft:white_wool", "G": "minecraft:gold_ingot"},
       e("kelin_koilek"), category="equipment")
    sh("kumis_belbeu", ["NLN"], {"N": "minecraft:iron_nugget", "L": "minecraft:leather"}, e("kumis_belbeu"), category="equipment")
    sh("altyn_belbeu", ["GLG", " T "], {"G": e("saka_gold"), "L": "minecraft:leather", "T": "minecraft:prismarine_crystals"},
       e("altyn_belbeu"), category="equipment")


def tags_update():
    """Броня и одежда — в теги слотов (для отделки и зачарований)."""
    import json
    MC = G.MC
    for slot, tag in (("head", "head_armor"), ("chest", "chest_armor")):
        path = f"{MC}/tags/item/{tag}.json"
        data = json.load(open(path, encoding="utf-8"))
        for n, s in CLOTHES.items():
            if s == slot and e(n) not in data["values"]:
                data["values"].append(e(n))
        W(path, data)


def loot_update():
    """Доспехи Ер Тарғына можно найти в сундуке Тамгалы (жыр подсказывает)."""
    import json
    path = f"{G.D}/loot_table/chests/tamgaly.json"
    data = json.load(open(path, encoding="utf-8"))
    data["pools"] = [p for p in data["pools"] if "er_targyn" not in json.dumps(p)]
    data["pools"].append({"rolls": 1, "entries": [{"type": "minecraft:item", "name": e(f"er_targyn_{p}"), "weight": 1}
                                                  for p in ("dulyga", "sauyt", "shalbar", "etik")] + [{"type": "minecraft:empty", "weight": 8}]})
    W(path, data)


def run(g):
    global G
    G = g
    aul_assets()
    tags_update()
    loot_update()
    import v8_caves
    v8_caves.run(g)
    import v8_home
    v8_home.run(g)
    import v8_lang
    G.NAMES.update(v8_lang.names())
