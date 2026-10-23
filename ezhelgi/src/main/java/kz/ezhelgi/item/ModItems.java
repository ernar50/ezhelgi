package kz.ezhelgi.item;

import kz.ezhelgi.EzhelgiMod;
import kz.ezhelgi.accessory.AccessoryItem;
import kz.ezhelgi.block.ModBlocks;
import kz.ezhelgi.entity.ModEntities;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EzhelgiMod.MODID);
    /** Все золотые украшения (заполняется ниже). */
    public static final List<RegistryObject<Item>> ACCESSORIES = new ArrayList<>();

    // ---------- Материалы ----------
    public static final RegistryObject<Item> SAKA_GOLD = item("saka_gold",
            p -> new Item(p.rarity(Rarity.UNCOMMON)));

    // ---------- Оружие батыра ----------
    public static final RegistryObject<Item> QYLYSH = item("qylysh",
            p -> new Item(p.sword(ToolMaterial.IRON, 3.5f, -2.2f)));
    public static final RegistryObject<Item> ALTYN_QYLYSH = item("altyn_qylysh",
            p -> new Item(p.sword(ModMaterials.SAKA_GOLD, 4.0f, -2.2f).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> NAIZA = item("naiza",
            p -> new Item(p.spear(ToolMaterial.IRON, 0.95f, 0.95f, 0.6f,
                    2.5f, 8.0f, 6.75f, 5.1f, 11.25f, 4.6f)));
    public static final RegistryObject<Item> ALTYN_QANJAR = item("altyn_qanjar",
            p -> new Item(p.sword(ModMaterials.SAKA_GOLD, 2.0f, -1.6f).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> SADAQ = item("sadaq",
            p -> new BowItem(p.durability(640)));

    // ---------- Доспех Золотого человека ----------
    public static final RegistryObject<Item> ALTYN_ADAM_HELMET = item("altyn_adam_helmet",
            p -> new Item(p.humanoidArmor(ModMaterials.ALTYN_ADAM, ArmorType.HELMET).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ALTYN_ADAM_CHESTPLATE = item("altyn_adam_chestplate",
            p -> new Item(p.humanoidArmor(ModMaterials.ALTYN_ADAM, ArmorType.CHESTPLATE).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ALTYN_ADAM_LEGGINGS = item("altyn_adam_leggings",
            p -> new Item(p.humanoidArmor(ModMaterials.ALTYN_ADAM, ArmorType.LEGGINGS).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ALTYN_ADAM_BOOTS = item("altyn_adam_boots",
            p -> new Item(p.humanoidArmor(ModMaterials.ALTYN_ADAM, ArmorType.BOOTS).rarity(Rarity.RARE)));

    // ---------- Еда и напитки ----------
    public static final RegistryObject<Item> BESHBARMAK = item("beshbarmak",
            p -> new Item(p.food(ModFoods.BESHBARMAK, ModFoods.BESHBARMAK_EAT).usingConvertsTo(Items.BOWL).stacksTo(16)));
    public static final RegistryObject<Item> BAURSAK = item("baursak",
            p -> new Item(p.food(ModFoods.BAURSAK, ModFoods.BAURSAK_EAT)));
    public static final RegistryObject<Item> KURT = item("kurt",
            p -> new Item(p.food(ModFoods.KURT, ModFoods.KURT_EAT)));
    public static final RegistryObject<Item> KAZY = item("kazy",
            p -> new Item(p.food(ModFoods.KAZY, ModFoods.KAZY_EAT)));
    public static final RegistryObject<Item> KUMYS = item("kumys",
            p -> new Item(p.food(ModFoods.KUMYS, ModFoods.KUMYS_DRINK).usingConvertsTo(Items.GLASS_BOTTLE).stacksTo(16)));
    public static final RegistryObject<Item> SHUBAT = item("shubat",
            p -> new Item(p.food(ModFoods.SHUBAT, ModFoods.SHUBAT_DRINK).usingConvertsTo(Items.GLASS_BOTTLE).stacksTo(16)));

    // ---------- Быт ----------
    public static final RegistryObject<Item> DOMBYRA = item("dombyra",
            p -> new DombyraItem(p.durability(900)));
    public static final RegistryObject<Item> KIIZ_UY = item("kiiz_uy",
            p -> new KiizUyItem(p.stacksTo(16).rarity(Rarity.UNCOMMON), "yurt_small", 5));
    public static final RegistryObject<Item> ULKEN_KIIZ_UY = item("ulken_kiiz_uy",
            p -> new KiizUyItem(p.stacksTo(16).rarity(Rarity.RARE), "yurt_big", 7));
    public static final RegistryObject<Item> QONYR_UY = item("qonyr_uy",
            p -> new KiizUyItem(p.stacksTo(16).rarity(Rarity.RARE), "yurt_qonyr", 7));
    public static final RegistryObject<Item> OTAU = item("otau",
            p -> new KiizUyItem(p.stacksTo(16).rarity(Rarity.UNCOMMON), "yurt_otau", 5));
    public static final RegistryObject<Item> HAN_ORDASY = item("han_ordasy",
            p -> new KiizUyItem(p.stacksTo(16).rarity(Rarity.EPIC), "yurt_han", 10));

    public static final RegistryObject<Item> KIIZ = blockItem("kiiz", ModBlocks.KIIZ);
    public static final RegistryObject<Item> KIIZ_STAIRS = blockItem("kiiz_stairs", ModBlocks.KIIZ_STAIRS);
    public static final RegistryObject<Item> KIIZ_SLAB = blockItem("kiiz_slab", ModBlocks.KIIZ_SLAB);
    public static final RegistryObject<Item> BASKUR = blockItem("baskur", ModBlocks.BASKUR);
    public static final RegistryObject<Item> QONYR_KIIZ = blockItem("qonyr_kiiz", ModBlocks.QONYR_KIIZ);
    public static final RegistryObject<Item> QONYR_KIIZ_STAIRS = blockItem("qonyr_kiiz_stairs", ModBlocks.QONYR_KIIZ_STAIRS);
    public static final RegistryObject<Item> QONYR_KIIZ_SLAB = blockItem("qonyr_kiiz_slab", ModBlocks.QONYR_KIIZ_SLAB);
    public static final RegistryObject<Item> QONYR_OIU = blockItem("qonyr_oiu", ModBlocks.QONYR_OIU);
    public static final RegistryObject<Item> AQ_OIU = blockItem("aq_oiu", ModBlocks.AQ_OIU);
    public static final RegistryObject<Item> KEREGE = blockItem("kerege", ModBlocks.KEREGE);
    public static final RegistryObject<Item> SHANYRAK = blockItem("shanyrak", ModBlocks.SHANYRAK);
    public static final RegistryObject<Item> SYRMAK = blockItem("syrmak", ModBlocks.SYRMAK);
    public static final RegistryObject<Item> KOK_SYRMAK = blockItem("kok_syrmak", ModBlocks.KOK_SYRMAK);
    public static final RegistryObject<Item> TEKEMET = blockItem("tekemet", ModBlocks.TEKEMET);
    public static final RegistryObject<Item> ALASHA = blockItem("alasha", ModBlocks.ALASHA);
    public static final RegistryObject<Item> ORNEK = blockItem("ornek", ModBlocks.ORNEK);

    // ---------- Яйца призыва ----------
    public static final RegistryObject<Item> KOKBORI_SPAWN_EGG = item("kokbori_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.KOKBORI.get())));
    public static final RegistryObject<Item> TULPAR_SPAWN_EGG = item("tulpar_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.TULPAR.get())));
    public static final RegistryObject<Item> BERKUT_SPAWN_EGG = item("berkut_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.BERKUT.get())));

    public static final RegistryObject<Item> ALTYN_JAUYNGER_SPAWN_EGG = item("altyn_jauynger_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.ALTYN_JAUYNGER.get())));
    public static final RegistryObject<Item> JUNGAR_SPAWN_EGG = item("jungar_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.JUNGAR.get())));
    public static final RegistryObject<Item> ALTYN_ADAM_SPAWN_EGG = item("altyn_adam_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.ALTYN_ADAM.get()).rarity(Rarity.EPIC)));

    // ---------- Золотая маска и арба ----------
    /** Алтын бетперде — золотая маска с высоким красным головным убором (надевается на голову). */
    public static final RegistryObject<Item> ALTYN_BETPERDE = item("altyn_betperde",
            p -> new Item(p.stacksTo(1).rarity(Rarity.EPIC).equippable(EquipmentSlot.HEAD)));
    public static final RegistryObject<Item> ARBA = item("arba",
            p -> new ArbaItem(p.stacksTo(1)));

    // ---------- v5: сказочные существа, кобыз, қамшы, тұмар ----------
    public static final RegistryObject<Item> QOBYZ = item("qobyz",
            p -> new KobyzItem(p.durability(900).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> QAMSHY = item("qamshy",
            p -> new QamshyItem(p.sword(ToolMaterial.IRON, 0.0f, -1.6f)));
    public static final RegistryObject<Item> SYNYQ_QYLYSH = item("synyq_qylysh",
            p -> new Item(p.stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ARUAQ_QYLYSH = item("aruaq_qylysh",
            p -> new SpiritBladeItem(p.sword(ModMaterials.SAKA_GOLD, 5.5f, -2.3f).rarity(Rarity.EPIC)
                    .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)));
    /** Призрачный меч в руке духа (игроку не достаётся). */
    public static final RegistryObject<Item> ARUAQ_SPECTRAL = item("aruaq_spectral",
            p -> new Item(p.stacksTo(1)));
    public static final RegistryObject<Item> ALBASTY_SHASHY = item("albasty_shashy",
            p -> new Item(p.rarity(Rarity.RARE)));
    public static final RegistryObject<Item> ZHEZ_TYRNAQ = item("zhez_tyrnaq",
            p -> new Item(p.rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TYRNAQ_QANJAR = item("tyrnaq_qanjar",
            p -> new Item(p.sword(ToolMaterial.COPPER, 3.5f, -1.4f).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> SAMURYK_JUMYRTQASY = item("samuryk_jumyrtqasy",
            p -> new SamurykEggItem(p.stacksTo(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SAMURYK_QAUYRSYN = item("samuryk_qauyrsyn",
            p -> new FeatherItem(p.stacksTo(16).rarity(Rarity.RARE)));
    /** Тұмар — оберег: не подпускает фантомов, мелкую нечисть и албасты. */
    public static final RegistryObject<Item> TUMAR = item("tumar",
            p -> new AccessoryItem(p.stacksTo(1).rarity(Rarity.RARE), "tumar", null, 0.0,
                    AttributeModifier.Operation.ADD_VALUE, false));
    public static final RegistryObject<Item> RUNA_TASY = blockItem("runa_tasy", ModBlocks.RUNA_TASY);

    public static final RegistryObject<Item> ZHEZTYRNAK_SPAWN_EGG = item("zheztyrnak_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.ZHEZTYRNAK.get())));
    public static final RegistryObject<Item> BATYR_ARUAGY_SPAWN_EGG = item("batyr_aruagy_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.BATYR_ARUAGY.get())));
    public static final RegistryObject<Item> ALBASTY_SPAWN_EGG = item("albasty_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.ALBASTY.get())));
    public static final RegistryObject<Item> ZHALMAUYZ_KEMPIR_SPAWN_EGG = item("zhalmauyz_kempir_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.ZHALMAUYZ_KEMPIR.get()).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SAMURYK_SPAWN_EGG = item("samuryk_spawn_egg",
            p -> new SpawnEggItem(p.spawnEgg(ModEntities.SAMURYK.get()).rarity(Rarity.RARE)));

    // ---------- v6: еда, торсық, находки, инструменты из древнего камня ----------
    public static final RegistryObject<Item> JYLQY_ETI = item("jylqy_eti",
            p -> new Item(p.food(ModFoods.JYLQY_ETI, ModFoods.MEAT_EAT)));
    public static final RegistryObject<Item> PISKEN_JYLQY_ETI = item("pisken_jylqy_eti",
            p -> new Item(p.food(ModFoods.PISKEN_JYLQY_ETI, ModFoods.MEAT_EAT)));
    public static final RegistryObject<Item> AQBOKEN_ETI = item("aqboken_eti",
            p -> new Item(p.food(ModFoods.AQBOKEN_ETI, ModFoods.MEAT_EAT)));
    public static final RegistryObject<Item> PISKEN_AQBOKEN_ETI = item("pisken_aqboken_eti",
            p -> new Item(p.food(ModFoods.PISKEN_AQBOKEN_ETI, ModFoods.MEAT_EAT)));
    public static final RegistryObject<Item> TORSYK = item("torsyk",
            p -> new TorsykItem(p.stacksTo(1), null));
    public static final RegistryObject<Item> TORSYK_QYMYZ = item("torsyk_qymyz",
            p -> new TorsykItem(p.durability(TorsykItem.CAPACITY), TorsykItem.Drink.QYMYZ));
    public static final RegistryObject<Item> TORSYK_SHUBAT = item("torsyk_shubat",
            p -> new TorsykItem(p.durability(TorsykItem.CAPACITY), TorsykItem.Drink.SHUBAT));
    /** Киелі қыл — мистическая струна бақсы. */
    public static final RegistryObject<Item> KIELI_QYL = item("kieli_qyl",
            p -> new Item(p.rarity(Rarity.RARE)));
    /** Ежелгі тас — древний камень балбала. */
    public static final RegistryObject<Item> EZHELGI_TAS = item("ezhelgi_tas",
            p -> new Item(p.rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TAS_KAILA = item("tas_kaila",
            p -> new Item(p.pickaxe(ModMaterials.EZHELGI_TAS, 1.0f, -2.8f).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TAS_BALTA = item("tas_balta",
            p -> new Item(p.axe(ModMaterials.EZHELGI_TAS, 6.0f, -3.1f).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> TAS_KUREK = item("tas_kurek",
            p -> new Item(p.shovel(ModMaterials.EZHELGI_TAS, 1.5f, -3.0f).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> DAU_SHOQPARY = item("dau_shoqpary",
            p -> new Item(p.sword(ToolMaterial.IRON, 8.0f, -3.4f).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> OTYRAR_TENGESI = item("otyrar_tengesi",
            p -> new Item(p.rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> QOLJAZBA = item("qoljazba",
            p -> new Item(p.stacksTo(16).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> QUMYRA = item("qumyra",
            p -> new Item(p.stacksTo(16).rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> BALBAL_SPAWN_EGG = egg("balbal", () -> ModEntities.BALBAL.get());
    public static final RegistryObject<Item> BAQSY_SPAWN_EGG = egg("baqsy_kolenkesi", () -> ModEntities.BAQSY_KOLENKESI.get());
    public static final RegistryObject<Item> DAU_SPAWN_EGG = egg("dau", () -> ModEntities.DAU.get());
    public static final RegistryObject<Item> SAIGA_SPAWN_EGG = egg("aqboken", () -> ModEntities.SAIGA.get());
    public static final RegistryObject<Item> BARYS_SPAWN_EGG = egg("barys", () -> ModEntities.BARYS.get());
    public static final RegistryObject<Item> KORKYT_SPAWN_EGG = egg("qorqyt_ata", () -> ModEntities.KORKYT_ATA.get());
    public static final RegistryObject<Item> QARAQSHY_SPAWN_EGG = egg("qaraqshy", () -> ModEntities.QARAQSHY.get());
    public static final RegistryObject<Item> AUYLDAS_SPAWN_EGG = egg("auyldas", () -> ModEntities.AUYLDAS.get());
    public static final RegistryObject<Item> ABAI_SPAWN_EGG = egg("abai", () -> ModEntities.ABAI.get());
    public static final RegistryObject<Item> SHOQAN_SPAWN_EGG = egg("shoqan", () -> ModEntities.SHOQAN.get());
    public static final RegistryObject<Item> AL_FARABI_SPAWN_EGG = egg("al_farabi", () -> ModEntities.AL_FARABI.get());

    // ---------- v7: доспехи батыров ----------
    public static final RegistryObject<Item> DULYGA = armor("dulyga", ModMaterials.BATYR, ArmorType.HELMET, Rarity.UNCOMMON);
    public static final RegistryObject<Item> KIREUKE = armor("kireuke", ModMaterials.KIREUKE, ArmorType.CHESTPLATE, Rarity.UNCOMMON);
    public static final RegistryObject<Item> KOBE_SAUYT = armor("kobe_sauyt", ModMaterials.KOBE, ArmorType.CHESTPLATE, Rarity.UNCOMMON);
    public static final RegistryObject<Item> BEREN_SAUYT = armor("beren_sauyt", ModMaterials.BEREN, ArmorType.CHESTPLATE, Rarity.RARE);
    public static final RegistryObject<Item> SHARAINA = armor("sharaina", ModMaterials.SHARAINA, ArmorType.CHESTPLATE, Rarity.RARE);
    public static final RegistryObject<Item> BATYR_SHALBAR = armor("batyr_shalbar", ModMaterials.BATYR, ArmorType.LEGGINGS, Rarity.UNCOMMON);
    public static final RegistryObject<Item> BATYR_ETIK = armor("batyr_etik", ModMaterials.BATYR, ArmorType.BOOTS, Rarity.UNCOMMON);
    public static final RegistryObject<Item> JUNGAR_DULYGA = armor("jungar_dulyga", ModMaterials.JUNGAR, ArmorType.HELMET, Rarity.UNCOMMON);
    public static final RegistryObject<Item> JUNGAR_SAUYT = armor("jungar_sauyt", ModMaterials.JUNGAR, ArmorType.CHESTPLATE, Rarity.UNCOMMON);
    public static final RegistryObject<Item> JUNGAR_SHALBAR = armor("jungar_shalbar", ModMaterials.JUNGAR, ArmorType.LEGGINGS, Rarity.UNCOMMON);
    public static final RegistryObject<Item> JUNGAR_ETIK = armor("jungar_etik", ModMaterials.JUNGAR, ArmorType.BOOTS, Rarity.UNCOMMON);
    public static final RegistryObject<Item> TYMAQ = armor("tymaq", ModMaterials.TYMAQ, ArmorType.HELMET, Rarity.COMMON);

    public static final RegistryObject<Item> QOBYLANDY_DULYGA = epic("qobylandy", "dulyga", ModMaterials.QOBYLANDY, ArmorType.HELMET);
    public static final RegistryObject<Item> QOBYLANDY_SAUYT = epic("qobylandy", "sauyt", ModMaterials.QOBYLANDY, ArmorType.CHESTPLATE);
    public static final RegistryObject<Item> QOBYLANDY_SHALBAR = epic("qobylandy", "shalbar", ModMaterials.QOBYLANDY, ArmorType.LEGGINGS);
    public static final RegistryObject<Item> QOBYLANDY_ETIK = epic("qobylandy", "etik", ModMaterials.QOBYLANDY, ArmorType.BOOTS);
    public static final RegistryObject<Item> ALPAMYS_DULYGA = epic("alpamys", "dulyga", ModMaterials.ALPAMYS, ArmorType.HELMET);
    public static final RegistryObject<Item> ALPAMYS_SAUYT = epic("alpamys", "sauyt", ModMaterials.ALPAMYS, ArmorType.CHESTPLATE);
    public static final RegistryObject<Item> ALPAMYS_SHALBAR = epic("alpamys", "shalbar", ModMaterials.ALPAMYS, ArmorType.LEGGINGS);
    public static final RegistryObject<Item> ALPAMYS_ETIK = epic("alpamys", "etik", ModMaterials.ALPAMYS, ArmorType.BOOTS);
    public static final RegistryObject<Item> ER_TARGYN_DULYGA = epic("er_targyn", "dulyga", ModMaterials.ER_TARGYN, ArmorType.HELMET);
    public static final RegistryObject<Item> ER_TARGYN_SAUYT = epic("er_targyn", "sauyt", ModMaterials.ER_TARGYN, ArmorType.CHESTPLATE);
    public static final RegistryObject<Item> ER_TARGYN_SHALBAR = epic("er_targyn", "shalbar", ModMaterials.ER_TARGYN, ArmorType.LEGGINGS);
    public static final RegistryObject<Item> ER_TARGYN_ETIK = epic("er_targyn", "etik", ModMaterials.ER_TARGYN, ArmorType.BOOTS);

    /** Қалқан — круглый щит батыра. */
    public static final RegistryObject<Item> QALQAN = shield("qalqan", 420, ModMaterials.BATYR_REPAIRABLE, Rarity.UNCOMMON, 0.25f);
    /** Щит из чешуи айдахара. */
    public static final RegistryObject<Item> AIDAHAR_QALQANY = shield("aidahar_qalqany", 1200, ModMaterials.EPIC_REPAIRABLE, Rarity.EPIC, 0.15f);

    // ---------- v7: отделка доспехов (шаблоны кузнечного стола) ----------
    public static final String[] TRIMS = {"qoshqar_muiz", "tuie_taban", "qus_qanaty", "synar_muiz", "tumarsha"};
    public static final RegistryObject<Item> QOSHQAR_MUIZ_TEMPLATE = template("qoshqar_muiz", Rarity.UNCOMMON);
    public static final RegistryObject<Item> TUIE_TABAN_TEMPLATE = template("tuie_taban", Rarity.UNCOMMON);
    public static final RegistryObject<Item> QUS_QANATY_TEMPLATE = template("qus_qanaty", Rarity.RARE);
    public static final RegistryObject<Item> SYNAR_MUIZ_TEMPLATE = template("synar_muiz", Rarity.UNCOMMON);
    public static final RegistryObject<Item> TUMARSHA_TEMPLATE = template("tumarsha", Rarity.RARE);

    // ---------- v7: музыка, добыча, тоғыз құмалақ ----------
    public static final RegistryObject<Item> MUSIC_DISC_ADAI = item("music_disc_adai",
            p -> new Item(p.stacksTo(1).rarity(Rarity.RARE).jukeboxPlayable(net.minecraft.resources.ResourceKey.create(
                    net.minecraft.core.registries.Registries.JUKEBOX_SONG,
                    net.minecraft.resources.Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "adai")))));
    public static final RegistryObject<Item> AIDAHAR_QABYRSHAGY = item("aidahar_qabyrshagy",
            p -> new Item(p.rarity(Rarity.EPIC).fireResistant()));
    public static final RegistryObject<Item> ARQAR_MUIIZI = item("arqar_muiizi",
            p -> new Item(p.rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> MUIIZ_SADAQ = item("muiiz_sadaq",
            p -> new BowItem(p.durability(900).rarity(Rarity.RARE)));
    public static final RegistryObject<Item> TULKI_TERISI = item("tulki_terisi",
            p -> new Item(p.rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> AN_ETI = item("an_eti",
            p -> new Item(p.food(ModFoods.AN_ETI, ModFoods.MEAT_EAT)));
    public static final RegistryObject<Item> PISKEN_AN_ETI = item("pisken_an_eti",
            p -> new Item(p.food(ModFoods.PISKEN_AN_ETI, ModFoods.MEAT_EAT)));
    public static final RegistryObject<Item> QUMALAQ = item("qumalaq", p -> new Item(p));
    public static final RegistryObject<Item> QUMALAQ_TUZDYK = item("qumalaq_tuzdyk", p -> new Item(p));
    /** Ою-өрнек — трафарет для ткацкого станка: флаги и тамги трёх жузов на знамёнах. */
    public static final RegistryObject<Item> OIU_BANNER_PATTERN = item("oiu_banner_pattern",
            p -> new Item(p.stacksTo(1).rarity(Rarity.UNCOMMON).delayedComponent(DataComponents.PROVIDES_BANNER_PATTERNS,
                    ctx -> ctx.getOrThrow(net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BANNER_PATTERN,
                            net.minecraft.resources.Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "pattern_item/oiu"))))));

    public static final RegistryObject<Item> TOGYZ_QUMALAQ = blockItem("togyz_qumalaq", ModBlocks.TOGYZ_QUMALAQ);
    public static final RegistryObject<Item> TU_QAZAQSTAN = blockItem("tu_qazaqstan", ModBlocks.TU_QAZAQSTAN);
    public static final RegistryObject<Item> TU_ALASH = blockItem("tu_alash", ModBlocks.TU_ALASH);
    public static final RegistryObject<Item> TU_HANDYQ = blockItem("tu_handyq", ModBlocks.TU_HANDYQ);
    public static final RegistryObject<Item> TU_ULY_JUZ = blockItem("tu_uly_juz", ModBlocks.TU_ULY_JUZ);
    public static final RegistryObject<Item> TU_ORTA_JUZ = blockItem("tu_orta_juz", ModBlocks.TU_ORTA_JUZ);
    public static final RegistryObject<Item> TU_KISHI_JUZ = blockItem("tu_kishi_juz", ModBlocks.TU_KISHI_JUZ);
    public static final RegistryObject<Item> TANBA_TAS = blockItem("tanba_tas", ModBlocks.TANBA_TAS);
    public static final RegistryObject<Item> AN_QUM = blockItem("an_qum", ModBlocks.AN_QUM);

    public static final RegistryObject<Item> ARQAR_SPAWN_EGG = egg("arqar", () -> ModEntities.ARQAR.get());
    public static final RegistryObject<Item> QARAQUIRYQ_SPAWN_EGG = egg("qaraquiryq", () -> ModEntities.QARAQUIRYQ.get());
    public static final RegistryObject<Item> TAU_TEKE_SPAWN_EGG = egg("tau_teke", () -> ModEntities.TAU_TEKE.get());
    public static final RegistryObject<Item> AYU_SPAWN_EGG = egg("ayu", () -> ModEntities.AYU.get());
    public static final RegistryObject<Item> MANUL_SPAWN_EGG = egg("manul", () -> ModEntities.MANUL.get());
    public static final RegistryObject<Item> QARSAQ_SPAWN_EGG = egg("qarsaq", () -> ModEntities.QARSAQ.get());
    public static final RegistryObject<Item> ITBALYQ_SPAWN_EGG = egg("itbalyq", () -> ModEntities.ITBALYQ.get());
    public static final RegistryObject<Item> QOSAYAQ_SPAWN_EGG = egg("qosayaq", () -> ModEntities.QOSAYAQ.get());
    public static final RegistryObject<Item> PRZEWALSKI_SPAWN_EGG = egg("przewalski", () -> ModEntities.PRZEWALSKI.get());
    public static final RegistryObject<Item> QARAQUS_SPAWN_EGG = egg("qaraqus", () -> ModEntities.QARAQUS.get());
    public static final RegistryObject<Item> AIDAHAR_SPAWN_EGG = egg("aidahar", () -> ModEntities.AIDAHAR.get());
    public static final RegistryObject<Item> MYSTAN_KEMPIR_SPAWN_EGG = egg("mystan_kempir", () -> ModEntities.MYSTAN_KEMPIR.get());
    public static final RegistryObject<Item> AQSAQAL_SPAWN_EGG = egg("aqsaqal", () -> ModEntities.AQSAQAL.get());
    public static final RegistryObject<Item> KUISHI_SPAWN_EGG = egg("kuishi", () -> ModEntities.KUISHI.get());
    public static final RegistryObject<Item> KERUENSHI_SPAWN_EGG = egg("keruenshi", () -> ModEntities.KERUENSHI.get());

    // ---------- v8: асық ату, одежда, пояса ----------
    public static final RegistryObject<Item> ASYQ = item("asyq", p -> new Item(p.stacksTo(64)));
    public static final RegistryObject<Item> SAQA = item("saqa", p -> new kz.ezhelgi.aul.SaqaItem(p.stacksTo(16)));
    public static final RegistryObject<Item> ASYQ_QATAR = blockItem("asyq_qatar", ModBlocks.ASYQ_QATAR);

    public static final RegistryObject<Item> SHAPAN = clothes("shapan", ArmorType.CHESTPLATE, Rarity.UNCOMMON);
    public static final RegistryObject<Item> KAMZOL = clothes("kamzol", ArmorType.CHESTPLATE, Rarity.COMMON);
    public static final RegistryObject<Item> TAQIYA = clothes("taqiya", ArmorType.HELMET, Rarity.COMMON);
    public static final RegistryObject<Item> KIMESHEK = clothes("kimeshek", ArmorType.HELMET, Rarity.COMMON);
    public static final RegistryObject<Item> SAUKELE = clothes("saukele", ArmorType.HELMET, Rarity.EPIC);
    public static final RegistryObject<Item> UKI_BORIK = clothes("uki_borik", ArmorType.HELMET, Rarity.RARE);
    public static final RegistryObject<Item> KELIN_KOILEK = clothes("kelin_koilek", ArmorType.CHESTPLATE, Rarity.RARE);
    public static final RegistryObject<Item> KUMIS_BELBEU = accessory("kumis_belbeu",
            () -> Attributes.ARMOR, 2.0, AttributeModifier.Operation.ADD_VALUE, false);
    public static final RegistryObject<Item> ALTYN_BELBEU = accessory("altyn_belbeu",
            () -> Attributes.KNOCKBACK_RESISTANCE, 0.3, AttributeModifier.Operation.ADD_VALUE, false);

    public static final RegistryObject<Item> KELIN_SPAWN_EGG = egg("kelin", () -> ModEntities.KELIN.get());
    public static final RegistryObject<Item> ZHYRSHY_SPAWN_EGG = egg("zhyrshy", () -> ModEntities.ZHYRSHY.get());

    // ---------- v8: пещеры ----------
    public static final RegistryObject<Item> ZHYLAN_TUMARY = accessory("zhylan_tumary",
            () -> Attributes.MAX_HEALTH, 4.0, AttributeModifier.Operation.ADD_VALUE, true);
    public static final RegistryObject<Item> SHOIYN_QULAQ = accessory("shoiyn_qulaq",
            () -> Attributes.ARMOR_TOUGHNESS, 3.0, AttributeModifier.Operation.ADD_VALUE, false);
    public static final RegistryObject<Item> QOLA = item("qola", p -> new Item(p.rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> QOLA_QANJAR = item("qola_qanjar",
            p -> new Item(p.sword(ModMaterials.QOLA, 3.0f, -2.0f).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> QOLA_BALTA = item("qola_balta",
            p -> new Item(p.axe(ModMaterials.QOLA, 6.0f, -3.0f).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ZHYLAN_SPAWN_EGG = egg("zhylan", () -> ModEntities.ZHYLAN.get());
    public static final RegistryObject<Item> ZHYN_SPAWN_EGG = egg("zhyn", () -> ModEntities.ZHYN.get());
    public static final RegistryObject<Item> SHAHMARAN_SPAWN_EGG = egg("shahmaran", () -> ModEntities.SHAHMARAN.get());
    public static final RegistryObject<Item> SHOIYNQULAQ_SPAWN_EGG = egg("shoiynqulaq", () -> ModEntities.SHOIYNQULAQ.get());
    public static final RegistryObject<Item> DARWISH_SPAWN_EGG = egg("darwish", () -> ModEntities.DARWISH.get());

    // ---------- v8: кухня, мебель, природа, инструменты ----------
    public static final RegistryObject<Item> KAZAN = blockItem("kazan", ModBlocks.KAZAN);
    public static final RegistryObject<Item> TANDYR = blockItem("tandyr", ModBlocks.TANDYR);
    public static final RegistryObject<Item> KEPTIRGISH = blockItem("keptirgish", ModBlocks.KEPTIRGISH);
    public static final RegistryObject<Item> SAMAUYR = blockItem("samauyr", ModBlocks.SAMAUYR);
    public static final RegistryObject<Item> DASTARKHAN = blockItem("dastarkhan", ModBlocks.DASTARKHAN);
    public static final RegistryObject<Item> KORPE = blockItem("korpe", ModBlocks.KORPE);
    public static final RegistryObject<Item> KEBEZHE = blockItem("kebezhe", ModBlocks.KEBEZHE);
    public static final RegistryObject<Item> BESIK = blockItem("besik", ModBlocks.BESIK);
    public static final RegistryObject<Item> QYZGALDAQ_SHRENK = blockItem("qyzgaldaq_shrenk", ModBlocks.QYZGALDAQ_SHRENK);
    public static final RegistryObject<Item> QYZGALDAQ_GREIG = blockItem("qyzgaldaq_greig", ModBlocks.QYZGALDAQ_GREIG);
    public static final RegistryObject<Item> SELEU = blockItem("seleu", ModBlocks.SELEU);
    public static final RegistryObject<Item> SIVERS_JAPYRAGY = blockItem("sivers_japyragy", ModBlocks.SIVERS_JAPYRAGY);
    public static final RegistryObject<Item> ALMA_KOSHETI = blockItem("alma_kosheti", ModBlocks.ALMA_KOSHETI);
    public static final RegistryObject<Item> SEKSEUIL = ITEMS.register("sekseuil", () -> new kz.ezhelgi.nature.FuelBlockItem(
            ModBlocks.SEKSEUIL.get(), new Item.Properties().setId(ITEMS.key("sekseuil")).useBlockDescriptionPrefix(), 3200));
    public static final RegistryObject<Item> SEKSEUIL_JAPYRAGY = blockItem("sekseuil_japyragy", ModBlocks.SEKSEUIL_JAPYRAGY);
    public static final RegistryObject<Item> SEKSEUIL_KOSHETI = blockItem("sekseuil_kosheti", ModBlocks.SEKSEUIL_KOSHETI);

    public static final RegistryObject<Item> NAN = item("nan", p -> new Item(p.food(ModFoods.NAN, ModFoods.NAN_EAT)));
    public static final RegistryObject<Item> SAMSA = item("samsa", p -> new Item(p.food(ModFoods.SAMSA, ModFoods.SAMSA_EAT)));
    public static final RegistryObject<Item> QAQ_ET = item("qaq_et", p -> new Item(p.food(ModFoods.QAQ_ET, ModFoods.MEAT_EAT)));
    public static final RegistryObject<Item> SORPA = item("sorpa",
            p -> new Item(p.food(ModFoods.SORPA, ModFoods.SORPA_EAT).usingConvertsTo(Items.BOWL).stacksTo(16)));
    public static final RegistryObject<Item> QUYRDAQ = item("quyrdaq",
            p -> new Item(p.food(ModFoods.QUYRDAQ, ModFoods.QUYRDAQ_EAT).usingConvertsTo(Items.BOWL).stacksTo(16)));
    public static final RegistryObject<Item> PALAU = item("palau",
            p -> new Item(p.food(ModFoods.PALAU, ModFoods.PALAU_EAT).usingConvertsTo(Items.BOWL).stacksTo(16)));
    public static final RegistryObject<Item> NAURYZ_KOZHE = item("nauryz_kozhe",
            p -> new Item(p.food(ModFoods.NAURYZ_KOZHE, ModFoods.NAURYZ_KOZHE_EAT).usingConvertsTo(Items.BOWL).stacksTo(16).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> QARA_SHAI = item("qara_shai",
            p -> new Item(p.food(ModFoods.QARA_SHAI, ModFoods.QARA_SHAI_DRINK).usingConvertsTo(Items.GLASS_BOTTLE).stacksTo(16)));
    public static final RegistryObject<Item> SUTTI_SHAI = item("sutti_shai",
            p -> new Item(p.food(ModFoods.SUTTI_SHAI, ModFoods.SUTTI_SHAI_DRINK).usingConvertsTo(Items.GLASS_BOTTLE).stacksTo(16)));

    public static final RegistryObject<Item> SYBYZGY = instrument("sybyzgy", InstrumentItem.Kind.SYBYZGY);
    public static final RegistryObject<Item> ZHETIGEN = instrument("zhetigen", InstrumentItem.Kind.ZHETIGEN);
    public static final RegistryObject<Item> SHANQOBYZ = instrument("shanqobyz", InstrumentItem.Kind.SHANQOBYZ);
    public static final RegistryObject<Item> DAUYLPAZ = instrument("dauylpaz", InstrumentItem.Kind.DAUYLPAZ);
    public static final RegistryObject<Item> QANBAQ_SPAWN_EGG = egg("qanbaq", () -> ModEntities.QANBAQ.get());

    private static RegistryObject<Item> instrument(String name, InstrumentItem.Kind kind) {
        return item(name, p -> new InstrumentItem(p.durability(250).rarity(Rarity.UNCOMMON), kind));
    }

    private static RegistryObject<Item> clothes(String name, ArmorType type, Rarity rarity) {
        return item(name, p -> new ClothesItem(p.humanoidArmor(ModMaterials.cloth(name), type).rarity(rarity), name));
    }

    private static RegistryObject<Item> armor(String name, net.minecraft.world.item.equipment.ArmorMaterial material,
                                              ArmorType type, Rarity rarity) {
        return item(name, p -> new Item(p.humanoidArmor(material, type).rarity(rarity)));
    }

    private static RegistryObject<Item> epic(String set, String part, net.minecraft.world.item.equipment.ArmorMaterial material,
                                             ArmorType type) {
        return item(set + "_" + part, p -> new EpicArmorItem(p.humanoidArmor(material, type).rarity(Rarity.EPIC).fireResistant(), set));
    }

    private static RegistryObject<Item> shield(String name, int durability, net.minecraft.tags.TagKey<Item> repair,
                                               Rarity rarity, float blockDelay) {
        return item(name, p -> new Item(p.durability(durability).repairable(repair).rarity(rarity)
                .equippableUnswappable(EquipmentSlot.OFFHAND)
                .delayedComponent(DataComponents.BLOCKS_ATTACKS, ctx -> new net.minecraft.world.item.component.BlocksAttacks(
                        blockDelay, 1.0f,
                        List.of(new net.minecraft.world.item.component.BlocksAttacks.DamageReduction(90.0f, java.util.Optional.empty(), 0.0f, 1.0f)),
                        new net.minecraft.world.item.component.BlocksAttacks.ItemDamageFunction(3.0f, 1.0f, 1.0f),
                        java.util.Optional.of(ctx.getOrThrow(net.minecraft.tags.DamageTypeTags.BYPASSES_SHIELD)),
                        java.util.Optional.of(net.minecraft.sounds.SoundEvents.SHIELD_BLOCK),
                        java.util.Optional.of(net.minecraft.sounds.SoundEvents.SHIELD_BREAK)))
                .component(DataComponents.BREAK_SOUND, net.minecraft.sounds.SoundEvents.SHIELD_BREAK)));
    }

    private static RegistryObject<Item> template(String pattern, Rarity rarity) {
        return item(pattern + "_armor_trim_smithing_template",
                p -> net.minecraft.world.item.SmithingTemplateItem.createArmorTrimTemplate(p.rarity(rarity)));
    }

    private static RegistryObject<Item> egg(String name, Supplier<? extends net.minecraft.world.entity.EntityType<?>> type) {
        return item(name + "_spawn_egg", p -> new SpawnEggItem(p.spawnEgg(type.get())));
    }

    // ---------- Золотые украшения в зверином стиле (аксессуары) ----------
    public static final RegistryObject<Item> BARYS_ALQASY = accessory("barys_alqasy",
            () -> Attributes.MAX_HEALTH, 4.0, AttributeModifier.Operation.ADD_VALUE, false);
    public static final RegistryObject<Item> BUGY_TUMARY = accessory("bugy_tumary",
            () -> Attributes.MOVEMENT_SPEED, 0.12, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, false);
    public static final RegistryObject<Item> QASQYR_BILEZIGI = accessory("qasqyr_bilezigi",
            () -> Attributes.ATTACK_DAMAGE, 2.0, AttributeModifier.Operation.ADD_VALUE, false);
    public static final RegistryObject<Item> ARQAR_SYRGASY = accessory("arqar_syrgasy",
            null, 0.0, AttributeModifier.Operation.ADD_VALUE, true);
    public static final RegistryObject<Item> TULPAR_JUZIGI = accessory("tulpar_juzigi",
            () -> Attributes.SAFE_FALL_DISTANCE, 8.0, AttributeModifier.Operation.ADD_VALUE, false);
    public static final RegistryObject<Item> BURKIT_BELBEUI = accessory("burkit_belbeui",
            () -> Attributes.ARMOR, 3.0, AttributeModifier.Operation.ADD_VALUE, false);

    private static RegistryObject<Item> accessory(String name, Supplier<Holder<Attribute>> attribute, double amount,
                                                  AttributeModifier.Operation operation, boolean nightVision) {
        RegistryObject<Item> obj = item(name, p -> new AccessoryItem(p.stacksTo(1).rarity(Rarity.RARE),
                name, attribute, amount, operation, nightVision));
        ACCESSORIES.add(obj);
        return obj;
    }

    private static RegistryObject<Item> item(String name, Function<Item.Properties, Item> factory) {
        return ITEMS.register(name, () -> factory.apply(new Item.Properties().setId(ITEMS.key(name))));
    }

    private static RegistryObject<Item> blockItem(String name, RegistryObject<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().setId(ITEMS.key(name)).useBlockDescriptionPrefix()));
    }
}
