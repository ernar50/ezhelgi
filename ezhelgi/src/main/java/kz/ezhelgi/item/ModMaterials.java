package kz.ezhelgi.item;

import kz.ezhelgi.EzhelgiMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.EnumMap;
import java.util.Map;

/** Материалы: сакское золото для оружия и брони Золотого человека. */
public final class ModMaterials {
    private ModMaterials() {}

    public static final TagKey<Item> SAKA_GOLD_REPAIRABLE = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "saka_gold_repairable"));

    /** Сақ алтыны: прочнее железа, легко зачаровывается. */
    public static final ToolMaterial SAKA_GOLD = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL,
            1100, 8.0f, 3.0f, 22, SAKA_GOLD_REPAIRABLE);

    public static final TagKey<Item> EZHELGI_TAS_REPAIRABLE = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "ezhelgi_tas_repairable"));

    /** Ежелгі тас — камень балбала: прочнее железа, копает как алмаз не умеет, но быстро. */
    public static final ToolMaterial EZHELGI_TAS = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL,
            900, 7.5f, 2.5f, 16, EZHELGI_TAS_REPAIRABLE);

    public static final ResourceKey<? extends Registry<EquipmentAsset>> EQUIPMENT_ROOT =
            ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("equipment_asset"));

    public static final ResourceKey<EquipmentAsset> ALTYN_ADAM_ASSET = ResourceKey.create(EQUIPMENT_ROOT,
            Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "altyn_adam"));

    /** Доспех Золотого человека (Есікский курган). */
    public static final ArmorMaterial ALTYN_ADAM = new ArmorMaterial(30,
            defense(3, 6, 8, 3, 10), 25, SoundEvents.ARMOR_EQUIP_GOLD,
            2.0f, 0.05f, SAKA_GOLD_REPAIRABLE, ALTYN_ADAM_ASSET);

    // ------------------------------------------------------------------ v7: доспехи батыров
    public static final TagKey<Item> BATYR_REPAIRABLE = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "batyr_repairable"));
    public static final TagKey<Item> EPIC_REPAIRABLE = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "epic_repairable"));
    public static final TagKey<Item> FUR_REPAIRABLE = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "fur_repairable"));

    public static ResourceKey<EquipmentAsset> asset(String name) {
        return ResourceKey.create(EQUIPMENT_ROOT, Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, name));
    }

    /** Дулыға, батыр шалбары и етігі. */
    public static final ArmorMaterial BATYR = new ArmorMaterial(18, defense(2, 5, 6, 2, 5), 12,
            SoundEvents.ARMOR_EQUIP_CHAIN, 0.5f, 0.0f, BATYR_REPAIRABLE, asset("batyr"));
    /** Кіреуке — лёгкая кольчужная рубаха. */
    public static final ArmorMaterial KIREUKE = new ArmorMaterial(16, defense(2, 5, 5, 2, 5), 14,
            SoundEvents.ARMOR_EQUIP_CHAIN, 0.0f, 0.0f, BATYR_REPAIRABLE, asset("kireuke"));
    /** Көбе сауыт — тяжёлая длинная кольчуга. */
    public static final ArmorMaterial KOBE = new ArmorMaterial(22, defense(2, 5, 6, 2, 5), 12,
            SoundEvents.ARMOR_EQUIP_CHAIN, 1.0f, 0.0f, BATYR_REPAIRABLE, asset("kobe_sauyt"));
    /** Берен сауыт — доспех из лучшей булатной стали. */
    public static final ArmorMaterial BEREN = new ArmorMaterial(28, defense(2, 5, 7, 2, 6), 15,
            SoundEvents.ARMOR_EQUIP_IRON, 1.5f, 0.0f, BATYR_REPAIRABLE, asset("beren_sauyt"));
    /** Шарайна — зерцальный доспех: стальные пластины поверх кольчуги. */
    public static final ArmorMaterial SHARAINA = new ArmorMaterial(32, defense(2, 5, 8, 2, 6), 14,
            SoundEvents.ARMOR_EQUIP_IRON, 2.0f, 0.05f, BATYR_REPAIRABLE, asset("sharaina"));
    /** Джунгарская ламеллярная броня. */
    public static final ArmorMaterial JUNGAR = new ArmorMaterial(20, defense(2, 5, 6, 2, 5), 10,
            SoundEvents.ARMOR_EQUIP_IRON, 1.0f, 0.0f, BATYR_REPAIRABLE, asset("jungar_sauyt"));
    /** Эпические доспехи героев эпоса. */
    public static final ArmorMaterial QOBYLANDY = new ArmorMaterial(40, defense(3, 6, 8, 3, 11), 20,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0f, 0.1f, EPIC_REPAIRABLE, asset("qobylandy"));
    public static final ArmorMaterial ALPAMYS = new ArmorMaterial(40, defense(3, 6, 8, 3, 11), 20,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0f, 0.1f, EPIC_REPAIRABLE, asset("alpamys"));
    public static final ArmorMaterial ER_TARGYN = new ArmorMaterial(38, defense(3, 6, 8, 3, 11), 22,
            SoundEvents.ARMOR_EQUIP_NETHERITE, 2.5f, 0.05f, EPIC_REPAIRABLE, asset("er_targyn"));
    /** Тымақ — меховая шапка из лисьего меха. */
    public static final ArmorMaterial TYMAQ = new ArmorMaterial(9, defense(1, 2, 3, 2, 3), 15,
            SoundEvents.ARMOR_EQUIP_LEATHER, 0.0f, 0.0f, FUR_REPAIRABLE, asset("tymaq"));

    public static final TagKey<Item> QOLA_REPAIRABLE = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "qola_repairable"));
    /** Қола — бронза древних рудокопов: чуть слабее железа, но хорошо зачаровывается. */
    public static final ToolMaterial QOLA = new ToolMaterial(BlockTags.INCORRECT_FOR_IRON_TOOL,
            420, 6.5f, 2.0f, 18, QOLA_REPAIRABLE);

    // ------------------------------------------------------------------ v8: национальная одежда
    public static final TagKey<Item> CLOTH_REPAIRABLE = TagKey.create(Registries.ITEM,
            Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "cloth_repairable"));

    /** Одежда почти не защищает, но даёт бонусы (см. ModEvents). */
    public static ArmorMaterial cloth(String asset) {
        return new ArmorMaterial(8, defense(1, 1, 2, 1, 1), 18, SoundEvents.ARMOR_EQUIP_LEATHER,
                0.0f, 0.0f, CLOTH_REPAIRABLE, asset(asset));
    }

    private static Map<ArmorType, Integer> defense(int boots, int legs, int chest, int helmet, int body) {
        Map<ArmorType, Integer> map = new EnumMap<>(ArmorType.class);
        map.put(ArmorType.BOOTS, boots);
        map.put(ArmorType.LEGGINGS, legs);
        map.put(ArmorType.CHESTPLATE, chest);
        map.put(ArmorType.HELMET, helmet);
        map.put(ArmorType.BODY, body);
        return map;
    }
}
