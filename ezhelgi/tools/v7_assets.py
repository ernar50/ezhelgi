"""v7: доспехи батыров и отделка, флаги и знамёна, тоғыз құмалақ, петроглифы Тамгалы,
поющий песок, аксакал и жизнь аула, кюй Курмангазы, новые звери и айдахар.
Вызывается из gen_assets.py после v6.
"""
import json
import os

import v7_armor
import v7_blocks
import v7_items
import v7_models3d
import v7_skins

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


ARMOR_ITEMS = {
    # имя: (слот, материал-ассет)
    "dulyga": "head", "kireuke": "chest", "kobe_sauyt": "chest", "beren_sauyt": "chest", "sharaina": "chest",
    "batyr_shalbar": "legs", "batyr_etik": "feet", "jungar_dulyga": "head", "jungar_sauyt": "chest",
    "jungar_shalbar": "legs", "jungar_etik": "feet", "tymaq": "head",
}
EPIC = ("qobylandy", "alpamys", "er_targyn")
for _s in EPIC:
    ARMOR_ITEMS.update({f"{_s}_dulyga": "head", f"{_s}_sauyt": "chest", f"{_s}_shalbar": "legs", f"{_s}_etik": "feet"})
TRIMS = ["qoshqar_muiz", "tuie_taban", "qus_qanaty", "synar_muiz", "tumarsha"]
FLAGS = ["tu_qazaqstan", "tu_alash", "tu_handyq", "tu_uly_juz", "tu_orta_juz", "tu_kishi_juz"]


# ------------------------------------------------------------------ текстуры
def textures():
    for name, img in v7_armor.armor_icons().items():
        save(img, "item/" + name)
    for asset, (hum, legs) in v7_armor.equipment().items():
        save(hum, "entity/equipment/humanoid/" + asset)
        layers = {"humanoid": [{"texture": e(asset)}]}
        if legs is not None:
            save(legs, "entity/equipment/humanoid_leggings/" + asset)
            layers["humanoid_leggings"] = [{"texture": e(asset)}]
        W(f"{G.A}/equipment/{asset}.json", {"layers": layers})
    meta = {"palette": {"base_palette": "trim_base"}}
    for p in TRIMS:
        hum, legs = v7_armor.trim(p)
        save(hum, "trims/entity/humanoid/" + p)
        save(legs, "trims/entity/humanoid_leggings/" + p)
        for sub in ("humanoid", "humanoid_leggings"):
            W(os.path.join(G.A, f"textures/trims/entity/{sub}/{p}.png.mcmeta"), meta)
    for name, img in v7_items.all_items().items():
        save(img, "item/" + name)
    for f in FLAGS:
        save(v7_blocks.flag_texture(f), "block/" + f)
        save(v7_blocks.flag_item_icon(f), "item/" + f)
    for i, g in enumerate(v7_blocks.GLYPHS):
        save(v7_blocks.glyph_texture(g, 40 + i), f"block/tanba_tas_{g}")
    save(v7_blocks.varnish(99), "block/tanba_tas_side")
    save(v7_blocks.an_qum_texture(), "block/an_qum")
    top = v7_blocks.togyz_top()
    save(top.crop((0, 0, 32, 32)), "block/togyz_left")
    save(top.crop((32, 0, 64, 32)), "block/togyz_right")
    save(v7_blocks.togyz_side(), "block/togyz_side")
    icon = top.resize((16, 8)).convert("RGBA")
    from PIL import Image
    ic = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    ic.paste(icon, (0, 5))
    for x in range(16):
        ic.putpixel((x, 13), (98, 60, 30, 255))
        ic.putpixel((x, 14), (70, 42, 20, 255))
    save(ic, "item/togyz_qumalaq")
    save(v7_blocks.qumalaq_texture(False), "item/qumalaq_stone")
    save(v7_blocks.qumalaq_texture(True), "item/qumalaq_tuzdyk_stone")
    save(v7_models3d.qalqan_front(), "item/qalqan_front")
    save(v7_models3d.shield_back(), "item/qalqan_back")
    save(v7_models3d.aidahar_front(), "item/aidahar_qalqany_front")
    save(v7_models3d.dombyra_texture(), "item/dombyra_3d")
    save(v7_models3d.qobyz_texture(), "item/qobyz_3d")
    for p in v7_blocks.PATTERNS:
        save(v7_blocks.banner_pattern_texture(p), "entity/banner/" + p)
        save(v7_blocks.shield_pattern_texture(p), "entity/shield/" + p)
    v7_skins.run(os.path.join(G.A, "textures/entity"))


# ------------------------------------------------------------------ модели
def models():
    A = G.A
    for name in ARMOR_ITEMS:
        G.simple_item(name)
    for name in ("aidahar_qabyrshagy", "arqar_muiizi", "tulki_terisi", "an_eti", "pisken_an_eti", "oiu_banner_pattern",
                 "music_disc_adai"):
        G.simple_item(name)
    for p in TRIMS:
        G.simple_item(f"{p}_armor_trim_smithing_template")
    for n in v7_items.EGGS:
        G.simple_item(f"{n}_spawn_egg")
    # роговой лук
    for n in ["muiiz_sadaq", "muiiz_sadaq_pulling_0", "muiiz_sadaq_pulling_1", "muiiz_sadaq_pulling_2"]:
        W(f"{A}/models/item/{n}.json", {"parent": "minecraft:item/bow", "textures": {"layer0": e("item/" + n)}})
    W(f"{A}/items/muiiz_sadaq.json", {"model": {
        "type": "minecraft:condition", "property": "minecraft:using_item",
        "on_false": {"type": "minecraft:model", "model": e("item/muiiz_sadaq")},
        "on_true": {"type": "minecraft:range_dispatch", "property": "minecraft:use_duration", "scale": 0.05,
                    "entries": [{"model": {"type": "minecraft:model", "model": e("item/muiiz_sadaq_pulling_1")}, "threshold": 0.65},
                                {"model": {"type": "minecraft:model", "model": e("item/muiiz_sadaq_pulling_2")}, "threshold": 0.9}],
                    "fallback": {"type": "minecraft:model", "model": e("item/muiiz_sadaq_pulling_0")}}}})
    # щиты
    for name, front, boss in (("qalqan", "qalqan_front", True), ("aidahar_qalqany", "aidahar_qalqany_front", False)):
        W(f"{A}/models/item/{name}.json", v7_models3d.shield_model(e("item/" + front), e("item/qalqan_back"), boss=boss))
        W(f"{A}/models/item/{name}_blocking.json",
          v7_models3d.shield_model(e("item/" + front), e("item/qalqan_back"), blocking=True, boss=boss))
        W(f"{A}/items/{name}.json", {"model": {
            "type": "minecraft:condition", "property": "minecraft:using_item",
            "on_false": {"type": "minecraft:model", "model": e(f"item/{name}")},
            "on_true": {"type": "minecraft:model", "model": e(f"item/{name}_blocking")}}})
    # объёмные домбыра и қобыз
    W(f"{A}/models/item/dombyra.json", v7_models3d.dombyra_model())
    W(f"{A}/models/item/qobyz.json", v7_models3d.qobyz_model())
    # құмалақ — камешек для доски
    for name, tex in (("qumalaq", "qumalaq_stone"), ("qumalaq_tuzdyk", "qumalaq_tuzdyk_stone")):
        W(f"{A}/models/item/{name}.json", {
            "textures": {"0": e("item/" + tex), "particle": e("item/" + tex)},
            "elements": [
                {"from": [6, 6.5, 6.5], "to": [10, 9.5, 9.5], "faces": {f: {"uv": [0, 0, 4, 3], "texture": "#0"}
                                                                       for f in ("north", "south", "east", "west", "up", "down")}},
                {"from": [6.5, 6, 6.5], "to": [9.5, 10, 9.5], "faces": {f: {"uv": [4, 0, 7, 4], "texture": "#0"}
                                                                       for f in ("north", "south", "east", "west", "up", "down")}},
                {"from": [6.5, 6.5, 6], "to": [9.5, 9.5, 10], "faces": {f: {"uv": [8, 0, 11, 3], "texture": "#0"}
                                                                       for f in ("north", "south", "east", "west", "up", "down")}}],
            "display": {"fixed": {"rotation": [0, 0, 0], "translation": [0, 0, 0], "scale": [1, 1, 1]},
                        "gui": {"rotation": [30, 45, 0], "translation": [0, 0, 0], "scale": [2.6, 2.6, 2.6]},
                        "ground": {"rotation": [0, 0, 0], "translation": [0, 0, 0], "scale": [1, 1, 1]},
                        "thirdperson_righthand": {"rotation": [0, 0, 0], "translation": [0, 2, 1], "scale": [1, 1, 1]},
                        "firstperson_righthand": {"rotation": [0, 0, 0], "translation": [0, 2, 0], "scale": [1.4, 1.4, 1.4]}}})
        W(f"{A}/items/{name}.json", {"model": {"type": "minecraft:model", "model": e("item/" + name)}})
    # флаги
    W(f"{A}/models/block/tu_base.json", v7_blocks.flag_models(NS))
    W(f"{A}/models/block/tu_upper.json", {"textures": {"particle": "ezhelgi:block/tu_qazaqstan"}, "elements": []})
    rot = {"south": 0, "west": 90, "north": 180, "east": 270}
    for f in FLAGS:
        W(f"{A}/models/block/{f}.json", {"parent": e("block/tu_base"), "textures": {"flag": e("block/" + f)}})
        variants = {}
        for facing, y in rot.items():
            variants[f"facing={facing},half=lower"] = {"model": e("block/" + f), "y": y}
            variants[f"facing={facing},half=upper"] = {"model": e("block/tu_upper")}
        W(f"{A}/blockstates/{f}.json", {"variants": variants})
        G.simple_item(f)
    # петроглифы
    gl = list(v7_blocks.GLYPHS)
    for g in gl:
        W(f"{A}/models/block/tanba_tas_{g}.json", {"parent": "minecraft:block/orientable", "textures": {
            "front": e("block/tanba_tas_" + g), "side": e("block/tanba_tas_side"), "top": e("block/tanba_tas_side")}})
    variants = {}
    for facing, y in {"north": 0, "east": 90, "south": 180, "west": 270}.items():
        for i, g in enumerate(gl):
            variants[f"facing={facing},glyph={i}"] = {"model": e("block/tanba_tas_" + g), "y": y}
    W(f"{A}/blockstates/tanba_tas.json", {"variants": variants})
    W(f"{A}/models/item/tanba_tas.json", {"parent": e("block/tanba_tas_kunbasty")})
    W(f"{A}/items/tanba_tas.json", {"model": {"type": "minecraft:model", "model": e("item/tanba_tas")}})
    # поющий песок
    W(f"{A}/models/block/an_qum.json", {"parent": "minecraft:block/cube_all", "textures": {"all": e("block/an_qum")}})
    W(f"{A}/blockstates/an_qum.json", {"variants": {"": {"model": e("block/an_qum")}}})
    W(f"{A}/models/item/an_qum.json", {"parent": e("block/an_qum")})
    W(f"{A}/items/an_qum.json", {"model": {"type": "minecraft:model", "model": e("item/an_qum")}})
    # тоғыз құмалақ
    side = e("block/togyz_side")
    for part, top, rim_x in (("foot", "togyz_left", (0, 0.75)), ("head", "togyz_right", (15.25, 16))):
        els = [{"from": [0, 0, 0], "to": [16, 2, 16], "faces": {
            "up": {"uv": [0, 0, 16, 16], "texture": "#top"}, "down": {"uv": [0, 0, 16, 16], "texture": "#side"},
            "north": {"uv": [0, 12, 16, 14], "texture": "#side"}, "south": {"uv": [0, 12, 16, 14], "texture": "#side"},
            "east": {"uv": [0, 12, 16, 14], "texture": "#side"}, "west": {"uv": [0, 12, 16, 14], "texture": "#side"}}}]
        rim = {f: {"uv": [0, 0, 16, 1], "texture": "#side"} for f in ("north", "south", "east", "west", "up")}
        els.append({"from": [0, 2, 0], "to": [16, 3, 0.75], "faces": rim})
        els.append({"from": [0, 2, 15.25], "to": [16, 3, 16], "faces": rim})
        els.append({"from": [rim_x[0], 2, 0.75], "to": [rim_x[1], 3, 15.25], "faces": rim})
        W(f"{A}/models/block/togyz_{part}.json", {"textures": {"top": e("block/" + top), "side": side, "particle": side},
                                                  "elements": els})
    variants = {}
    for facing, y in {"north": 0, "east": 90, "south": 180, "west": 270}.items():
        for part in ("foot", "head"):
            variants[f"facing={facing},part={part}"] = {"model": e("block/togyz_" + part), "y": y}
    W(f"{A}/blockstates/togyz_qumalaq.json", {"variants": variants})
    G.simple_item("togyz_qumalaq")


# ------------------------------------------------------------------ звуки
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
    for i in range(1, 21):
        data[f"item.dombyra.kurmangazy.{i}"] = entry("dombyra", {"name": f"{NS}:dombyra/kurmangazy/kui{i}", "stream": False})
    data["music_disc.adai"] = {"sounds": [{"name": f"{NS}:records/adai", "stream": True}]}
    data.update({
        "entity.aidahar.roar": entry("aidahar_roar", ev("minecraft:entity.ender_dragon.growl", 0.8, 3.0)),
        "entity.aidahar.bite": entry("aidahar_bite", ev("minecraft:entity.evoker_fangs.attack", 0.6, 1.5),
                                     ev("minecraft:entity.ravager.attack", 0.8, 1.5)),
        "entity.aidahar.fire": entry("aidahar_fire", ev("minecraft:entity.blaze.shoot", 0.6, 2.0),
                                     ev("minecraft:entity.ender_dragon.shoot", 0.9, 2.0)),
        "entity.aidahar.ambient": entry("aidahar", ev("minecraft:entity.ender_dragon.ambient", 0.9, 2.5)),
        "entity.aidahar.hurt": entry(None, ev("minecraft:entity.ender_dragon.hurt", 0.9, 2.0)),
        "entity.aidahar.death": entry(None, ev("minecraft:entity.ender_dragon.death", 1.0, 3.0)),
        "entity.aqsaqal.talk": entry("aqsaqal", ev("minecraft:entity.villager.ambient", 0.7), ev("minecraft:entity.villager.trade", 0.7),
                                     ev("minecraft:entity.villager.yes", 0.7)),
        "block.togyz.move": entry("togyz", *[{"name": f"{NS}:togyz/move{i}"} for i in range(1, 5)]),
        "block.an_qum.sing": entry("an_qum", *[{"name": f"{NS}:an_qum/sing{i}", "attenuation_distance": 24} for i in range(1, 4)]),
        "block.tanba.found": entry("tanba", ev("minecraft:block.amethyst_block.resonate", 0.8, 1.2),
                                   ev("minecraft:block.enchantment_table.use", 0.9, 1.0)),
    })
    W(path, data)
    W(f"{G.D}/jukebox_song/adai.json", {"comparator_output": 9, "description": {"translate": "jukebox_song.ezhelgi.adai"},
                                        "length_in_seconds": 128.0, "sound_event": e("music_disc.adai")})


# ------------------------------------------------------------------ данные: отделка, знамёна, теги
def trims_and_banners():
    D, MC = G.D, G.MC
    for p in TRIMS:
        W(f"{D}/trim_pattern/{p}.json", {"asset_id": e(p), "decal": False,
                                         "description": {"translate": f"trim_pattern.ezhelgi.{p}"}})
        tpl = e(f"{p}_armor_trim_smithing_template")
        W(f"{D}/recipe/{p}_armor_trim_smithing_template_smithing_trim.json", {
            "type": "minecraft:smithing_trim", "addition": "#minecraft:trim_materials",
            "base": "#minecraft:trimmable_armor", "pattern": e(p), "template": tpl})
        W(f"{D}/recipe/{p}_armor_trim_smithing_template.json", {
            "type": "minecraft:crafting_shaped", "category": "misc",
            "key": {"#": e("saka_gold"), "C": e("kiiz"), "S": tpl},
            "pattern": ["#S#", "#C#", "###"], "result": {"count": 2, "id": tpl}})
    for p in v7_blocks.PATTERNS:
        W(f"{D}/banner_pattern/{p}.json", {"asset_id": e(p), "translation_key": f"block.ezhelgi.banner.{p}"})
    W(f"{D}/tags/banner_pattern/pattern_item/oiu.json", {"values": [e(p) for p in v7_blocks.PATTERNS]})


def tags():
    D, MC = G.D, G.MC
    slots = {"head": [], "chest": [], "legs": [], "feet": []}
    for n, s in ARMOR_ITEMS.items():
        slots[s].append(e(n))
    W(f"{MC}/tags/item/head_armor.json", {"values": [e("altyn_adam_helmet")] + slots["head"]})
    W(f"{MC}/tags/item/chest_armor.json", {"values": [e("altyn_adam_chestplate")] + slots["chest"]})
    W(f"{MC}/tags/item/leg_armor.json", {"values": [e("altyn_adam_leggings")] + slots["legs"]})
    W(f"{MC}/tags/item/foot_armor.json", {"values": [e("altyn_adam_boots")] + slots["feet"]})
    W(f"{MC}/tags/item/enchantable/durability.json", {"values": [e("sadaq"), e("muiiz_sadaq"), e("qalqan"), e("aidahar_qalqany")]})
    W(f"{MC}/tags/item/enchantable/bow.json", {"values": [e("sadaq"), e("muiiz_sadaq")]})
    W(f"{D}/tags/item/batyr_repairable.json", {"values": ["minecraft:iron_ingot"]})
    W(f"{D}/tags/item/epic_repairable.json", {"values": [e("aidahar_qabyrshagy"), e("saka_gold")]})
    W(f"{D}/tags/item/fur_repairable.json", {"values": [e("tulki_terisi"), "minecraft:leather"]})
    W(f"{MC}/tags/item/meat.json", {"values": [e("jylqy_eti"), e("pisken_jylqy_eti"), e("aqboken_eti"), e("pisken_aqboken_eti"),
                                               e("an_eti"), e("pisken_an_eti")]})
    W(f"{MC}/tags/item/creeper_drop_music_discs.json", {"values": [e("music_disc_adai")]})
    W(f"{MC}/tags/entity_type/can_equip_saddle.json", {"values": [e("przewalski")]})
    W(f"{MC}/tags/entity_type/can_wear_horse_armor.json", {"values": [e("przewalski")]})
    W(f"{MC}/tags/block/mineable/axe.json", {"values": [e("kerege"), e("shanyrak"), e("togyz_qumalaq")] + [e(f) for f in FLAGS]})
    W(f"{MC}/tags/block/mineable/pickaxe.json", {"values": [e("runa_tasy"), e("tanba_tas")]})
    W(f"{MC}/tags/block/mineable/shovel.json", {"values": [e("an_qum")]})
    # среды обитания
    W(f"{D}/tags/worldgen/biome/arqar_habitat.json", {"values": ["#ezhelgi:mountains", "minecraft:meadow", "minecraft:stony_peaks",
                                                                 "minecraft:windswept_savanna"]})
    W(f"{D}/tags/worldgen/biome/desert_steppe.json", {"values": ["minecraft:desert", "minecraft:savanna", "minecraft:savanna_plateau",
                                                                 "minecraft:windswept_savanna", "#ezhelgi:steppe"]})
    W(f"{D}/tags/worldgen/biome/ayu_habitat.json", {"values": ["minecraft:taiga", "minecraft:old_growth_pine_taiga",
                                                               "minecraft:old_growth_spruce_taiga", "minecraft:grove",
                                                               "minecraft:windswept_forest"]})
    W(f"{D}/tags/worldgen/biome/manul_habitat.json", {"values": ["minecraft:windswept_hills", "minecraft:windswept_gravelly_hills",
                                                                 "minecraft:meadow", "minecraft:stony_peaks", "minecraft:savanna_plateau"]})
    W(f"{D}/tags/worldgen/biome/shore.json", {"values": ["minecraft:beach", "minecraft:stony_shore", "minecraft:snowy_beach"]})
    W(f"{D}/tags/worldgen/biome/forest_night.json", {"values": ["minecraft:forest", "minecraft:birch_forest", "minecraft:dark_forest",
                                                                "minecraft:taiga", "minecraft:old_growth_birch_forest"]})
    W(f"{D}/tags/worldgen/biome/peaks.json", {"values": ["minecraft:jagged_peaks", "minecraft:frozen_peaks", "minecraft:stony_peaks",
                                                         "minecraft:snowy_slopes"]})
    W(f"{D}/tags/worldgen/biome/tamgaly.json", {"values": ["#ezhelgi:steppe", "minecraft:savanna_plateau", "minecraft:windswept_savanna",
                                                           "minecraft:meadow"]})
    W(f"{D}/tags/worldgen/biome/aral.json", {"values": ["minecraft:desert", "minecraft:beach", "#minecraft:is_badlands"]})
    W(f"{D}/tags/worldgen/biome/dune.json", {"values": ["minecraft:desert"]})
    W(f"{D}/tags/worldgen/biome/yasawi.json", {"values": ["minecraft:desert", "minecraft:savanna", "#ezhelgi:steppe"]})


def spawns():
    mod = f"{G.D}/forge/biome_modifier"

    def add(name, tag, weight, lo, hi):
        W(f"{mod}/add_{name}.json", {"type": "forge:add_spawns", "biomes": tag, "spawners": [
            {"type": e(name), "count": {"type": "minecraft:uniform", "min_inclusive": lo, "max_inclusive": hi}, "weight": weight}]})
    add("arqar", "#ezhelgi:arqar_habitat", 6, 2, 4)
    add("tau_teke", "#ezhelgi:mountains", 6, 2, 4)
    add("qaraquiryq", "#ezhelgi:desert_steppe", 7, 3, 6)
    add("qosayaq", "#ezhelgi:desert_steppe", 6, 1, 3)
    add("ayu", "#ezhelgi:ayu_habitat", 4, 1, 2)
    add("manul", "#ezhelgi:manul_habitat", 4, 1, 1)
    add("qarsaq", "#ezhelgi:desert_steppe", 5, 1, 2)
    add("itbalyq", "#ezhelgi:shore", 6, 2, 4)
    add("qaraqus", "#ezhelgi:arqar_habitat", 3, 1, 1)
    add("mystan_kempir", "#ezhelgi:forest_night", 3, 1, 1)


# ------------------------------------------------------------------ добыча
def it(name, w, lo=1, hi=1, cond=None):
    x = {"type": "minecraft:item", "name": name, "weight": w}
    if hi > 1:
        x["modifier"] = [{"type": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}}]
    if cond:
        x["condition"] = cond
    return x


def meat(raw, cooked, lo, hi):
    return it(raw, 1, lo, hi)


def loot():
    D = G.D
    kp = {"type": "minecraft:killed_by_player"}

    def table(path, pools, kind=None):
        if kind is None:
            kind = "minecraft:entity" if path.startswith("entities") else "minecraft:chest"
        W(f"{D}/loot_table/{path}.json", {"type": kind, "pools": pools, "random_sequence": f"{NS}:{path}"})
    hide = lambda w=1: {"rolls": 1, "entries": [it("minecraft:leather", 1, 1, 2), {"type": "minecraft:empty", "weight": w}]}
    table("entities/arqar", [{"rolls": 1, "entries": [meat("minecraft:mutton", None, 1, 3)]},
                             {"rolls": 1, "entries": [it(e("arqar_muiizi"), 1), {"type": "minecraft:empty", "weight": 2}]}, hide()])
    table("entities/qaraquiryq", [{"rolls": 1, "entries": [meat(e("an_eti"), None, 1, 2)]}, hide(2)])
    table("entities/tau_teke", [{"rolls": 1, "entries": [meat(e("an_eti"), None, 1, 3)]},
                                {"rolls": 1, "entries": [it(e("arqar_muiizi"), 1), {"type": "minecraft:empty", "weight": 4}]}, hide()])
    table("entities/ayu", [{"rolls": 1, "entries": [it("minecraft:leather", 1, 2, 4)]},
                           {"rolls": 1, "entries": [meat("minecraft:salmon", None, 1, 2), {"type": "minecraft:empty", "weight": 1}]},
                           {"rolls": 1, "entries": [it("minecraft:honeycomb", 1), {"type": "minecraft:empty", "weight": 3}]}])
    table("entities/manul", [{"rolls": 1, "entries": [it("minecraft:string", 1, 1, 2), {"type": "minecraft:empty", "weight": 1}]}])
    table("entities/qarsaq", [{"rolls": 1, "entries": [it(e("tulki_terisi"), 1), {"type": "minecraft:empty", "weight": 2}]}])
    table("entities/itbalyq", [{"rolls": 1, "entries": [meat("minecraft:cod", None, 1, 3)]}, hide(2)])
    table("entities/qosayaq", [{"rolls": 1, "entries": [it("minecraft:rabbit_hide", 1), {"type": "minecraft:empty", "weight": 1}]}])
    table("entities/przewalski", [{"rolls": 1, "entries": [it("minecraft:leather", 1, 1, 2), {"type": "minecraft:empty", "weight": 1}]},
                                  {"rolls": 1, "entries": [meat(e("jylqy_eti"), None, 1, 3)]}])
    table("entities/qaraqus", [{"rolls": 1, "entries": [it("minecraft:feather", 1, 2, 5)]},
                               {"rolls": 1, "entries": [it("minecraft:bone", 1), {"type": "minecraft:empty", "weight": 1}]}])
    epic = [it(e(f"{s}_{p}"), 1) for s in EPIC for p in ("dulyga", "sauyt", "shalbar", "etik")]
    table("entities/aidahar", [{"rolls": 1, "entries": [it(e("aidahar_qabyrshagy"), 1, 6, 10)]},
                               {"rolls": 1, "entries": [it(e("saka_gold"), 1, 8, 16)]},
                               {"rolls": 1, "entries": epic},
                               {"rolls": 1, "entries": [it(e("qus_qanaty_armor_trim_smithing_template"), 1),
                                                        it(e("qoshqar_muiz_armor_trim_smithing_template"), 1)]},
                               {"rolls": 1, "entries": [it("minecraft:dragon_breath", 1, 1, 3), it("minecraft:experience_bottle", 1, 4, 8)]}])
    table("entities/mystan_kempir", [{"rolls": 1, "entries": [it(e("tulki_terisi"), 1, 1, 2)]},
                                     {"rolls": 1, "condition": kp, "entries": [
                                         it("minecraft:glass_bottle", 3), it("minecraft:redstone", 3, 1, 3), it("minecraft:amethyst_shard", 2),
                                         it(e("tumarsha_armor_trim_smithing_template"), 1), {"type": "minecraft:empty", "weight": 3}]}])
    table("entities/mystan_tulki", [{"rolls": 1, "entries": [it(e("tulki_terisi"), 1)]}])
    for n in ("aqsaqal", "kuishi", "keruenshi"):
        table("entities/" + n, [])
    # джунгары иногда роняют части своей брони
    path = f"{D}/loot_table/entities/jungar.json"
    data = json.load(open(path, encoding="utf-8"))
    data["pools"] = [p for p in data["pools"] if "jungar_dulyga" not in json.dumps(p)]
    data["pools"].append({"rolls": 1, "condition": {"type": "minecraft:all_of", "terms": [
                              kp, {"type": "minecraft:random_chance", "chance": 0.12}]},
                          "entries": [it(e("jungar_dulyga"), 3), it(e("jungar_sauyt"), 2), it(e("jungar_shalbar"), 2),
                                      it(e("jungar_etik"), 3)]})
    W(path, data)
    # блоки
    def block(name, cond=None):
        entry = {"type": "minecraft:item", "name": e(name)}
        pool = {"rolls": 1, "entries": [entry], "condition": {"type": "minecraft:survives_explosion"}}
        if cond:
            pool["condition"] = {"type": "minecraft:all_of", "terms": [{"type": "minecraft:survives_explosion"}, cond]}
        W(f"{D}/loot_table/blocks/{name}.json", {"type": "minecraft:block", "pools": [pool],
                                                  "random_sequence": f"{NS}:blocks/{name}"})
    for f in FLAGS:
        block(f, {"type": "minecraft:match_block", "blocks": e(f), "state": {"half": "lower"}})
    block("togyz_qumalaq", {"type": "minecraft:match_block", "blocks": e("togyz_qumalaq"), "state": {"part": "foot"}})
    block("tanba_tas")
    block("an_qum")
    # сундуки новых мест
    templates = [it(e(f"{p}_armor_trim_smithing_template"), 1) for p in TRIMS]
    table("chests/yasawi_library", [
        {"rolls": {"type": "minecraft:uniform", "min": 3, "max": 6}, "entries": [
            it("minecraft:book", 10, 1, 4), it("minecraft:paper", 8, 2, 8), it(e("qoljazba"), 6, 1, 2), it("minecraft:writable_book", 4),
            it("minecraft:lapis_lazuli", 6, 2, 6), it("minecraft:candle", 6, 1, 3), it(e("otyrar_tengesi"), 5, 1, 3)]},
        {"rolls": 1, "entries": [{"type": "minecraft:item", "name": "minecraft:book", "weight": 3,
                                  "modifier": {"type": "minecraft:enchant_randomly", "options": "#minecraft:on_random_loot"}},
                                 {"type": "minecraft:empty", "weight": 4}]}])
    table("chests/yasawi_cache", [
        {"rolls": {"type": "minecraft:uniform", "min": 4, "max": 7}, "entries": [
            it(e("saka_gold"), 10, 2, 6), it("minecraft:gold_ingot", 8, 2, 5), it("minecraft:emerald", 8, 2, 6),
            it(e("otyrar_tengesi"), 8, 2, 6), it(e("qumyra"), 4), it("minecraft:diamond", 3, 1, 2), it("minecraft:golden_apple", 2),
            it(e("oiu_banner_pattern"), 3)]},
        {"rolls": 1, "entries": templates + [{"type": "minecraft:empty", "weight": 2}]},
        {"rolls": 1, "entries": epic + [{"type": "minecraft:empty", "weight": 30}]}])
    table("chests/aral_ship", [
        {"rolls": {"type": "minecraft:uniform", "min": 3, "max": 6}, "entries": [
            it("minecraft:iron_nugget", 10, 2, 9), it("minecraft:cod", 6, 1, 4), it("minecraft:salmon", 4, 1, 3),
            it("minecraft:string", 6, 2, 6), it("minecraft:rotten_flesh", 6, 1, 4), it("minecraft:compass", 2),
            it("minecraft:map", 2), it("minecraft:copper_ingot", 6, 1, 5), it("minecraft:nautilus_shell", 1),
            it(e("saka_gold"), 3, 1, 2), it("minecraft:emerald", 4, 1, 3)]},
        {"rolls": 1, "entries": [it(e("tuie_taban_armor_trim_smithing_template"), 1), it(e("music_disc_adai"), 1),
                                 {"type": "minecraft:empty", "weight": 8}]}])
    table("chests/jibek_joly", [
        {"rolls": {"type": "minecraft:uniform", "min": 4, "max": 7}, "entries": [
            it(e("syrmak"), 6, 1, 2), it(e("kok_syrmak"), 6, 1, 2), it(e("ornek"), 4, 1, 2), it("minecraft:sugar", 8, 2, 8),
            it("minecraft:cocoa_beans", 6, 2, 6), it("minecraft:paper", 6, 2, 8), it("minecraft:glass", 4, 2, 6),
            it(e("saka_gold"), 6, 1, 4), it("minecraft:amethyst_shard", 4, 1, 4), it(e("kumys"), 4, 1, 3),
            it(e("baursak"), 6, 2, 6), it(e("oiu_banner_pattern"), 2)]},
        {"rolls": 1, "entries": templates + [it(e("qalqan"), 2), {"type": "minecraft:empty", "weight": 6}]}])
    table("chests/aidahar_hoard", [
        {"rolls": {"type": "minecraft:uniform", "min": 5, "max": 9}, "entries": [
            it(e("saka_gold"), 10, 3, 8), it("minecraft:gold_ingot", 10, 3, 8), it("minecraft:gold_block", 3),
            it("minecraft:diamond", 5, 1, 3), it("minecraft:emerald", 6, 2, 6), it("minecraft:bone", 8, 2, 6),
            it(e("aidahar_qabyrshagy"), 4, 1, 3), it("minecraft:golden_apple", 3), it("minecraft:enchanted_golden_apple", 1)]},
        {"rolls": 2, "entries": epic},
        {"rolls": 1, "entries": templates}])
    table("chests/tamgaly", [
        {"rolls": {"type": "minecraft:uniform", "min": 2, "max": 4}, "entries": [
            it("minecraft:bone", 8, 1, 4), it("minecraft:arrow", 8, 4, 12), it("minecraft:flint", 6, 2, 6),
            it(e("ezhelgi_tas"), 3), it(e("saka_gold"), 4, 1, 2), it(e("an_eti"), 4, 1, 3)]}])


# ------------------------------------------------------------------ рецепты
def recipes():
    sh, sl, D = G.shaped, G.shapeless, G.D
    I, L = "minecraft:iron_ingot", "minecraft:leather"
    sh("dulyga", ["IGI", "I I", "C C"], {"I": I, "G": "minecraft:gold_ingot", "C": "minecraft:iron_chain"}, e("dulyga"),
       category="equipment")
    sh("kireuke", ["C C", "CRC", "CCC"], {"C": "minecraft:iron_chain", "R": "minecraft:red_wool"}, e("kireuke"), category="equipment")
    sh("kobe_sauyt", ["C C", "CIC", "CCC"], {"C": "minecraft:iron_chain", "I": I}, e("kobe_sauyt"), category="equipment")
    sh("beren_sauyt", ["I I", "IGI", "III"], {"I": I, "G": e("saka_gold")}, e("beren_sauyt"), category="equipment")
    sh("sharaina", ["I I", "OKO", "ICI"], {"I": I, "O": "minecraft:iron_block", "K": e("kobe_sauyt"), "C": "minecraft:iron_chain"},
       e("sharaina"), category="equipment")
    sh("batyr_shalbar", ["LLL", "C C", "C C"], {"L": L, "C": "minecraft:iron_chain"}, e("batyr_shalbar"), category="equipment")
    sh("batyr_etik", ["L L", "I I"], {"L": L, "I": I}, e("batyr_etik"), category="equipment")
    sh("tymaq", ["FWF", "F F"], {"F": e("tulki_terisi"), "W": "minecraft:blue_wool"}, e("tymaq"), category="equipment")
    sh("qalqan", ["PIP", "PLP", " P "], {"P": "#minecraft:planks", "I": I, "L": L}, e("qalqan"), category="equipment")
    sh("aidahar_qalqany", ["SSS", "SQS", " S "], {"S": e("aidahar_qabyrshagy"), "Q": e("qalqan")}, e("aidahar_qalqany"),
       category="equipment")
    sh("muiiz_sadaq", ["HS ", "B S", "HS "], {"H": e("arqar_muiizi"), "S": "minecraft:string", "B": e("sadaq")}, e("muiiz_sadaq"),
       category="equipment")
    sh("togyz_qumalaq", ["SSS", "PPP"], {"S": "minecraft:cobblestone", "P": "#minecraft:wooden_slabs"}, e("togyz_qumalaq"))
    sh("oiu_banner_pattern", ["PG"], {"P": "minecraft:paper", "G": e("saka_gold")}, e("oiu_banner_pattern"))
    flags = {"tu_qazaqstan": ("minecraft:light_blue_wool", "minecraft:yellow_wool"),
             "tu_alash": ("minecraft:green_wool", "minecraft:red_wool"),
             "tu_handyq": ("minecraft:blue_wool", "minecraft:yellow_wool"),
             "tu_uly_juz": ("minecraft:cyan_wool", "minecraft:yellow_wool"),
             "tu_orta_juz": ("minecraft:white_wool", "minecraft:blue_wool"),
             "tu_kishi_juz": ("minecraft:red_wool", "minecraft:yellow_wool")}
    for f, (a, b) in flags.items():
        sh(f, ["SAB", "SAA", "S  "], {"S": "minecraft:stick", "A": a, "B": b}, e(f))
    for kind, time in (("smelting", 200), ("smoking", 100), ("campfire_cooking", 600)):
        G.write_json(f"{D}/recipe/pisken_an_eti_{kind}.json", {"type": f"minecraft:{kind}", "category": "food",
                                                              "ingredient": e("an_eti"), "result": {"id": e("pisken_an_eti")},
                                                              "experience": 0.35, "cookingtime": time})


def run(g):
    global G
    G = g
    textures()
    models()
    sounds()
    trims_and_banners()
    tags()
    spawns()
    loot()
    recipes()
    import v7_world
    v7_world.run(g)
    import v7_lang
    G.NAMES.update(v7_lang.names())
