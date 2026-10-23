"""v8: кухня аула (казан, тандыр, кептіргіш, самауыр), мебель (дастархан, көрпе, кебеже, бесік),
растения (тюльпаны, ковыль, яблоня Сиверса, саксаул), инструменты, перекати-поле — модели, данные, тексты.
Вызывается из v8_assets.run.
"""
import os

import v7_items
import v8_home_art as ART

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


FACES = ("north", "south", "east", "west", "up", "down")


def box(fr, to, tex, faces=FACES, side=None, top=None, bottom=None, front=None):
    """Элемент модели. tex — текстура по умолчанию; side/top/bottom/front — переопределения."""
    f = {}
    for face in faces:
        t = tex
        if face in ("north", "south", "east", "west") and side:
            t = side
        if face == "up" and top:
            t = top
        if face == "down" and bottom:
            t = bottom
        if face == "north" and front:
            t = front
        f[face] = {"texture": "#" + t}
    return {"from": list(fr), "to": list(to), "faces": f}


def model(name, textures, elements, particle=None):
    tx = dict(textures)
    tx["particle"] = tx.get(particle or next(iter(textures)))
    W(f"{G.A}/models/block/{name}.json", {"parent": "minecraft:block/block", "textures": tx, "elements": elements})


def facing_state(name, models_by_state=None, extra=""):
    variants = {}
    for facing, y in {"north": 0, "east": 90, "south": 180, "west": 270}.items():
        if models_by_state:
            for st, m in models_by_state.items():
                variants[f"facing={facing},{st}"] = {"model": e("block/" + m), "y": y} if y else {"model": e("block/" + m)}
        else:
            variants[f"facing={facing}"] = {"model": e("block/" + name), "y": y} if y else {"model": e("block/" + name)}
    W(f"{G.A}/blockstates/{name}.json", {"variants": variants})


def block_item(name, model_name=None):
    W(f"{G.A}/items/{name}.json", {"model": {"type": "minecraft:model", "model": e("block/" + (model_name or name))}})


def flat_item(name, texture):
    W(f"{G.A}/models/item/{name}.json", {"parent": "minecraft:item/generated", "textures": {"layer0": e("block/" + texture)}})
    W(f"{G.A}/items/{name}.json", {"model": {"type": "minecraft:model", "model": e("item/" + name)}})


def self_drop(name, survives=True):
    pool = {"rolls": 1, "entries": [{"type": "minecraft:item", "name": e(name)}]}
    if survives:
        pool["condition"] = {"type": "minecraft:survives_explosion"}
    W(f"{G.D}/loot_table/blocks/{name}.json", {"type": "minecraft:block", "pools": [pool], "random_sequence": f"{NS}:blocks/{name}"})


# ------------------------------------------------------------------ модели блоков
def kitchen_models():
    t = {"iron": e("block/kazan_iron"), "inside": e("block/kazan_inside")}
    body = [
        box((4, 0, 4), (12, 1, 12), "iron"),
        box((3, 1, 3), (13, 8, 13), "iron", top="inside"),
        box((2, 7, 2), (14, 10, 3), "iron"), box((2, 7, 13), (14, 10, 14), "iron"),
        box((2, 7, 3), (3, 10, 13), "iron"), box((13, 7, 3), (14, 10, 13), "iron"),
        box((0.5, 8, 7), (2, 9, 9), "iron"), box((14, 8, 7), (15.5, 9, 9), "iron"),
    ]
    model("kazan_0", t, body)
    model("kazan_1", dict(t, liquid=e("block/kazan_water")), body + [box((3, 8.5, 3), (13, 8.5, 13), "liquid", faces=("up",))])
    soup = dict(t, liquid=e("block/kazan_soup"))
    model("kazan_2", soup, body + [box((3, 9, 3), (13, 9, 13), "liquid", faces=("up",))])
    model("kazan_3", soup, body + [box((3, 9.3, 3), (13, 9.3, 13), "liquid", faces=("up",))])
    W(f"{G.A}/blockstates/kazan.json", {"variants": {f"stage={i}": {"model": e(f"block/kazan_{i}")} for i in range(4)}})
    block_item("kazan", "kazan_3")

    for lit in (False, True):
        sfx = "_lit" if lit else ""
        tt = {"side": e("block/tandyr_side"), "top": e("block/tandyr_top" + sfx), "front": e("block/tandyr_front" + sfx)}
        model("tandyr" + sfx, tt, [
            box((1, 0, 1), (15, 10, 15), "side", front="front", bottom="side", top="side"),
            box((2, 10, 2), (14, 13, 14), "side", top="side"),
            box((4, 13, 4), (12, 15, 12), "side", top="top"),
        ])
    facing_state("tandyr", {"lit=false": "tandyr", "lit=true": "tandyr_lit"})
    block_item("tandyr")

    w = {"wood": "minecraft:block/stripped_spruce_log"}
    model("keptirgish", w, [
        box((0, 0, 7), (2, 16, 9), "wood"), box((14, 0, 7), (16, 16, 9), "wood"),
        box((0, 0, 5), (2, 1, 11), "wood"), box((14, 0, 5), (16, 1, 11), "wood"),
        box((2, 13, 7.5), (14, 14, 8.5), "wood"), box((2, 8, 7.5), (14, 9, 8.5), "wood"),
    ])
    facing_state("keptirgish")
    block_item("keptirgish")

    for lit in (False, True):
        sfx = "_lit" if lit else ""
        tt = {"brass": e("block/samauyr"), "base": e("block/samauyr" + sfx), "wood": "minecraft:block/dark_oak_planks"}
        model("samauyr" + sfx, tt, [
            box((5, 0, 5), (11, 2, 11), "base"), box((4, 2, 4), (12, 10, 12), "brass"),
            box((5, 10, 5), (11, 11, 11), "brass"), box((7, 11, 7), (9, 15, 9), "brass"),
            box((6, 15, 6), (10, 16, 10), "brass"), box((7, 4, 1.5), (9, 5, 4), "brass"),
            box((7.5, 3, 1.5), (8.5, 4, 2.5), "brass"),
            box((2.5, 7, 7), (4, 9, 9), "wood"), box((12, 7, 7), (13.5, 9, 9), "wood"),
        ])
    facing_state("samauyr", {"lit=false": "samauyr", "lit=true": "samauyr_lit"})
    block_item("samauyr")


def furniture_models():
    d = {"top": e("block/dastarkhan_top"), "wood": e("block/dastarkhan_wood"), "bowl": "minecraft:block/white_terracotta",
         "bread": e("block/dastarkhan_wood")}
    model("dastarkhan", d, [
        box((0, 5, 0), (16, 7, 16), "wood", top="top"),
        box((1, 0, 1), (3, 5, 3), "wood"), box((13, 0, 1), (15, 5, 3), "wood"),
        box((1, 0, 13), (3, 5, 15), "wood"), box((13, 0, 13), (15, 5, 15), "wood"),
        box((6, 7, 6), (10, 8.5, 10), "bowl"),
    ], particle="wood")
    facing_state("dastarkhan")
    block_item("dastarkhan")

    k = {"top": e("block/korpe_top"), "side": e("block/korpe_side"), "pillow": e("block/yastyq")}
    model("korpe", k, [box((0, 0, 0), (16, 3, 16), "side", top="top"), box((2, 3, 11), (14, 6, 15), "pillow")])
    facing_state("korpe")
    block_item("korpe")

    b = {"front": e("block/kebezhe_front"), "side": e("block/kebezhe_side"), "top": e("block/kebezhe_top")}
    model("kebezhe", b, [
        box((1, 2, 2), (15, 13, 14), "side", front="front", top="top"),
        box((0.5, 13, 1.5), (15.5, 15, 14.5), "side", top="top"),
        box((1, 0, 2), (3, 2, 4), "side"), box((13, 0, 2), (15, 2, 4), "side"),
        box((1, 0, 12), (3, 2, 14), "side"), box((13, 0, 12), (15, 2, 14), "side"),
    ], particle="side")
    facing_state("kebezhe")
    block_item("kebezhe")

    s = {"wood": e("block/besik_wood"), "cloth": e("block/besik_cloth")}
    model("besik", s, [
        box((3, 0, 1), (4, 2, 15), "wood"), box((12, 0, 1), (13, 2, 15), "wood"),
        box((3, 2, 1), (13, 11, 2), "wood"), box((3, 2, 14), (13, 11, 15), "wood"),
        box((3, 4, 2), (4, 8, 14), "wood"), box((12, 4, 2), (13, 8, 14), "wood"),
        box((4, 3, 2), (12, 5, 14), "cloth"),
        box((7.5, 11, 1), (8.5, 12, 15), "wood"),
    ], particle="wood")
    facing_state("besik")
    block_item("besik")


def plant_models():
    A = G.A
    for n in ("qyzgaldaq_shrenk", "qyzgaldaq_greig"):
        W(f"{A}/models/block/{n}.json", {"parent": "minecraft:block/cross", "textures": {"cross": e("block/" + n)}})
        W(f"{A}/blockstates/{n}.json", {"variants": {"wild=false": {"model": e("block/" + n)}, "wild=true": {"model": e("block/" + n)}}})
        flat_item(n, n)
    W(f"{A}/models/block/seleu.json", {"parent": "minecraft:block/cross", "textures": {"cross": e("block/seleu")}})
    W(f"{A}/blockstates/seleu.json", {"variants": {"": {"model": e("block/seleu")}}})
    flat_item("seleu", "seleu")
    for n in ("alma_kosheti", "sekseuil_kosheti"):
        W(f"{A}/models/block/{n}.json", {"parent": "minecraft:block/cross", "textures": {"cross": e("block/" + n)}})
        W(f"{A}/blockstates/{n}.json", {"variants": {f"stage={i}": {"model": e("block/" + n)} for i in range(2)}})
        flat_item(n, n)
    for n, tx in (("sivers_japyragy", "sivers_japyragy"), ("sivers_japyragy_fruit", "sivers_japyragy_fruit"),
                  ("sekseuil_japyragy", "sekseuil_japyragy")):
        W(f"{A}/models/block/{n}.json", {"parent": "minecraft:block/leaves", "textures": {"all": e("block/" + tx)}})
    W(f"{A}/blockstates/sivers_japyragy.json", {"variants": {
        "fruit=false": {"model": e("block/sivers_japyragy")}, "fruit=true": {"model": e("block/sivers_japyragy_fruit")}}})
    W(f"{A}/blockstates/sekseuil_japyragy.json", {"variants": {"": {"model": e("block/sekseuil_japyragy")}}})
    block_item("sivers_japyragy")
    block_item("sekseuil_japyragy")
    W(f"{A}/models/block/sekseuil.json", {"parent": "minecraft:block/cube_column",
                                          "textures": {"side": e("block/sekseuil_side"), "end": e("block/sekseuil_top")}})
    W(f"{A}/models/block/sekseuil_horizontal.json", {"parent": "minecraft:block/cube_column_horizontal",
                                                     "textures": {"side": e("block/sekseuil_side"), "end": e("block/sekseuil_top")}})
    W(f"{A}/blockstates/sekseuil.json", {"variants": {
        "axis=y": {"model": e("block/sekseuil")},
        "axis=z": {"model": e("block/sekseuil_horizontal"), "x": 90},
        "axis=x": {"model": e("block/sekseuil_horizontal"), "x": 90, "y": 90}}})
    block_item("sekseuil")


# ------------------------------------------------------------------ данные
def loot():
    D = G.D
    for n in ("kazan", "tandyr", "keptirgish", "samauyr", "dastarkhan", "korpe", "kebezhe", "besik",
              "qyzgaldaq_shrenk", "qyzgaldaq_greig", "seleu", "alma_kosheti", "sekseuil", "sekseuil_kosheti"):
        self_drop(n)
    shears = {"type": "minecraft:match_tool", "predicate": {"items": "minecraft:shears"}}
    for leaves, sapling, fruit in (("sivers_japyragy", "alma_kosheti", True), ("sekseuil_japyragy", "sekseuil_kosheti", False)):
        entries = [{"type": "minecraft:alternatives", "children": [
            {"type": "minecraft:item", "name": e(leaves), "condition": shears},
            {"type": "minecraft:item", "name": e(sapling),
             "condition": {"type": "minecraft:all_of", "terms": [
                 {"type": "minecraft:survives_explosion"},
                 {"type": "minecraft:table_bonus", "enchantment": "minecraft:fortune",
                  "chances": [0.05, 0.0625, 0.083333336, 0.1]}]}}]}]
        pools = [{"rolls": 1, "entries": entries}]
        pools.append({"rolls": 1, "entries": [{"type": "minecraft:item", "name": "minecraft:stick",
                                               "modifier": [{"type": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": 1, "max": 2}}]}],
                      "condition": {"type": "minecraft:all_of", "terms": [
                          {"type": "minecraft:inverted", "term": shears},
                          {"type": "minecraft:random_chance", "chance": 0.04}]}})
        if fruit:
            pools.append({"rolls": 1, "entries": [{"type": "minecraft:item", "name": "minecraft:apple"}],
                          "condition": {"type": "minecraft:all_of", "terms": [
                              {"type": "minecraft:inverted", "term": shears},
                              {"type": "minecraft:random_chance", "chance": 0.08}]}})
        W(f"{D}/loot_table/blocks/{leaves}.json", {"type": "minecraft:block", "pools": pools, "random_sequence": f"{NS}:blocks/{leaves}"})
    W(f"{D}/loot_table/entities/qanbaq.json", {"type": "minecraft:entity", "pools": [], "random_sequence": f"{NS}:entities/qanbaq"})
    W(f"{D}/loot_table/entities/seat.json", {"type": "minecraft:entity", "pools": [], "random_sequence": f"{NS}:entities/seat"})
    # кебеже в юртах аула
    def it(name, w, lo=1, hi=1):
        d = {"type": "minecraft:item", "name": name if ":" in name else e(name), "weight": w}
        if hi > 1:
            d["modifier"] = [{"type": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}}]
        return d
    W(f"{D}/loot_table/chests/kebezhe.json", {"type": "minecraft:chest", "pools": [{"rolls": {"type": "minecraft:uniform", "min": 3, "max": 6},
        "entries": [it("baursak", 16, 2, 6), it("kurt", 14, 2, 5), it("nan", 12, 1, 3), it("qaq_et", 10, 1, 3),
                    it("minecraft:white_wool", 8, 2, 6), it("minecraft:red_wool", 6, 1, 4), it("asyq", 10, 2, 8),
                    it("taqiya", 3), it("kimeshek", 3), it("kamzol", 2), it("saka_gold", 4, 1, 3), it("sybyzgy", 2),
                    it("shanqobyz", 2), it("minecraft:bowl", 6, 1, 3), it("minecraft:glass_bottle", 6, 1, 3)]}],
        "random_sequence": f"{NS}:chests/kebezhe"})


def add_tag(path, values):
    import json
    cur = json.load(open(path, encoding="utf-8")) if os.path.exists(path) else {"replace": False, "values": []}
    for v in values:
        if v not in cur["values"]:
            cur["values"].append(v)
    W(path, cur)


def tags():
    D, MC = G.D, G.MC
    add_tag(f"{MC}/tags/block/leaves.json", [e("sivers_japyragy"), e("sekseuil_japyragy")])
    add_tag(f"{MC}/tags/item/leaves.json", [e("sivers_japyragy"), e("sekseuil_japyragy")])
    add_tag(f"{MC}/tags/block/logs_that_burn.json", [e("sekseuil")])
    add_tag(f"{MC}/tags/item/logs_that_burn.json", [e("sekseuil")])
    add_tag(f"{MC}/tags/block/logs.json", [e("sekseuil")])
    add_tag(f"{MC}/tags/item/logs.json", [e("sekseuil")])
    add_tag(f"{MC}/tags/block/saplings.json", [e("alma_kosheti"), e("sekseuil_kosheti")])
    add_tag(f"{MC}/tags/item/saplings.json", [e("alma_kosheti"), e("sekseuil_kosheti")])
    add_tag(f"{MC}/tags/block/small_flowers.json", [e("qyzgaldaq_shrenk"), e("qyzgaldaq_greig")])
    add_tag(f"{MC}/tags/item/small_flowers.json", [e("qyzgaldaq_shrenk"), e("qyzgaldaq_greig")])
    add_tag(f"{MC}/tags/block/replaceable_by_trees.json", [e("seleu")])
    add_tag(f"{MC}/tags/block/mineable/axe.json", [e(n) for n in ("keptirgish", "dastarkhan", "kebezhe", "besik", "sekseuil")])
    add_tag(f"{MC}/tags/block/mineable/pickaxe.json", [e(n) for n in ("kazan", "tandyr", "samauyr")])
    add_tag(f"{MC}/tags/block/mineable/hoe.json", [e("sivers_japyragy"), e("sekseuil_japyragy")])
    W(f"{D}/tags/worldgen/biome/sekseuil_habitat.json", {"values": ["minecraft:desert", "minecraft:badlands", "minecraft:eroded_badlands",
                                                                     "#ezhelgi:desert_steppe"]})
    W(f"{D}/tags/worldgen/biome/alma_habitat.json", {"values": ["minecraft:forest", "minecraft:flower_forest", "minecraft:meadow",
                                                                 "minecraft:windswept_forest", "minecraft:birch_forest", "minecraft:cherry_grove"]})
    W(f"{D}/tags/worldgen/biome/qanbaq_habitat.json", {"values": ["#ezhelgi:steppe", "minecraft:desert", "minecraft:badlands",
                                                                   "minecraft:savanna", "minecraft:savanna_plateau"]})
    W(f"{D}/tags/worldgen/structure/has_fair.json", {"values": [e("caravanserai")]})


def recipes():
    sh = G.shaped
    sh("kazan", ["I I", "I I", "III"], {"I": "minecraft:iron_ingot"}, e("kazan"))
    sh("tandyr", [" C ", "C C", "CCC"], {"C": "minecraft:terracotta"}, e("tandyr"))
    sh("keptirgish", ["SSS", "S S", "S S"], {"S": "minecraft:stick"}, e("keptirgish"))
    sh("samauyr", [" C ", "CBC", "CCC"], {"C": "minecraft:copper_ingot", "B": "minecraft:bucket"}, e("samauyr"))
    sh("dastarkhan", ["WWW", "PPP", "S S"], {"W": "minecraft:red_carpet", "P": "#minecraft:planks", "S": "minecraft:stick"}, e("dastarkhan"))
    sh("korpe", ["RRR", "WWW"], {"R": "minecraft:red_wool", "W": "minecraft:white_wool"}, e("korpe"), count=2)
    sh("kebezhe", ["PGP", "PCP", "S S"], {"P": "#minecraft:planks", "G": "minecraft:gold_nugget", "C": "minecraft:chest",
                                          "S": "minecraft:stick"}, e("kebezhe"))
    sh("besik", ["S S", "PWP", "S S"], {"S": "minecraft:stick", "P": "#minecraft:planks", "W": "minecraft:white_wool"}, e("besik"))
    sh("sybyzgy", ["  R", " R ", "R  "], {"R": "minecraft:sugar_cane"}, e("sybyzgy"), category="equipment")
    sh("zhetigen", ["PPP", "SSS", "PPP"], {"P": "#minecraft:planks", "S": "minecraft:string"}, e("zhetigen"), category="equipment")
    sh("shanqobyz", [" I ", "I I", " N "], {"I": "minecraft:iron_ingot", "N": "minecraft:iron_nugget"}, e("shanqobyz"), category="equipment")
    sh("dauylpaz", ["LLL", "PSP", "PPP"], {"L": "minecraft:leather", "P": "#minecraft:planks", "S": "minecraft:stick"}, e("dauylpaz"),
       category="equipment")
    D = G.D
    W(f"{D}/recipe/seleu_string.json", {"type": "minecraft:crafting_shapeless", "category": "misc",
                                        "ingredients": [e("seleu"), e("seleu"), e("seleu")], "result": {"id": "minecraft:string", "count": 1}})
    W(f"{D}/recipe/qyzgaldaq_shrenk_dye.json", {"type": "minecraft:crafting_shapeless", "category": "misc",
                                                "ingredients": [e("qyzgaldaq_shrenk")], "result": {"id": "minecraft:pink_dye", "count": 1}})
    W(f"{D}/recipe/qyzgaldaq_greig_dye.json", {"type": "minecraft:crafting_shapeless", "category": "misc",
                                               "ingredients": [e("qyzgaldaq_greig")], "result": {"id": "minecraft:red_dye", "count": 1}})
    W(f"{D}/recipe/sekseuil_sticks.json", {"type": "minecraft:crafting_shapeless", "category": "misc",
                                           "ingredients": [e("sekseuil")], "result": {"id": "minecraft:stick", "count": 6}})


def art():
    for n, img in ART.textures().items():
        save(img, "block/" + n)
    for n, img in ART.icons().items():
        save(img, "item/" + n)
        G.simple_item(n)
    save(v7_items.egg("#c8a868", "#7a5a30", 9), "item/qanbaq_spawn_egg")
    G.simple_item("qanbaq_spawn_egg")


def sounds():
    import json
    path = f"{G.A}/sounds.json"
    data = json.load(open(path, encoding="utf-8"))

    def entry(sub, *names, **kw):
        d = {"sounds": [dict({"name": f"{NS}:{n}"}, **kw) for n in names]}
        if sub:
            d["subtitle"] = "subtitles.ezhelgi." + sub
        return d
    data.update({
        "item.sybyzgy.play": entry("sybyzgy", "instruments/sybyzgy1", "instruments/sybyzgy2"),
        "item.zhetigen.play": entry("zhetigen", "instruments/zhetigen1", "instruments/zhetigen2"),
        "item.shanqobyz.play": entry("shanqobyz", "instruments/shanqobyz1", "instruments/shanqobyz2"),
        "item.dauylpaz.play": entry("dauylpaz", "instruments/dauylpaz1", "instruments/dauylpaz2"),
        "block.kazan.bubble": entry("kazan", "kitchen/bubble1", "kitchen/bubble2", "kitchen/bubble3"),
        "block.samauyr.boil": entry("samauyr", "kitchen/boil1", "kitchen/boil2"),
    })
    W(path, data)


def run(g):
    global G
    G = g
    art()
    kitchen_models()
    furniture_models()
    plant_models()
    loot()
    tags()
    recipes()
    sounds()
    import v8_lang_home
    G.NAMES.update(v8_lang_home.names())
