package kz.ezhelgi.item;

import kz.ezhelgi.EzhelgiMod;
import kz.ezhelgi.block.ModBlocks;
import kz.ezhelgi.entity.ModEntities;
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

import java.util.function.Function;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EzhelgiMod.MODID);

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

    public static final RegistryObject<Item> KIIZ = blockItem("kiiz", ModBlocks.KIIZ);
    public static final RegistryObject<Item> KIIZ_STAIRS = blockItem("kiiz_stairs", ModBlocks.KIIZ_STAIRS);
    public static final RegistryObject<Item> KIIZ_SLAB = blockItem("kiiz_slab", ModBlocks.KIIZ_SLAB);
    public static final RegistryObject<Item> BASKUR = blockItem("baskur", ModBlocks.BASKUR);
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

    private static RegistryObject<Item> item(String name, Function<Item.Properties, Item> factory) {
        return ITEMS.register(name, () -> factory.apply(new Item.Properties().setId(ITEMS.key(name))));
    }

    private static RegistryObject<Item> blockItem(String name, RegistryObject<? extends Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().setId(ITEMS.key(name)).useBlockDescriptionPrefix()));
    }
}
