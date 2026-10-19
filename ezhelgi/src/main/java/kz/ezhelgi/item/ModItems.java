package kz.ezhelgi.item;

import kz.ezhelgi.EzhelgiMod;
import kz.ezhelgi.accessory.AccessoryItem;
import kz.ezhelgi.block.ModBlocks;
import kz.ezhelgi.entity.ModEntities;
import net.minecraft.core.Holder;
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
            p -> new DombyraItem(p.stacksTo(1)));
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
