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

    public static final ResourceKey<? extends Registry<EquipmentAsset>> EQUIPMENT_ROOT =
            ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("equipment_asset"));

    public static final ResourceKey<EquipmentAsset> ALTYN_ADAM_ASSET = ResourceKey.create(EQUIPMENT_ROOT,
            Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "altyn_adam"));

    /** Доспех Золотого человека (Есікский курган). */
    public static final ArmorMaterial ALTYN_ADAM = new ArmorMaterial(30,
            defense(3, 6, 8, 3, 10), 25, SoundEvents.ARMOR_EQUIP_GOLD,
            2.0f, 0.05f, SAKA_GOLD_REPAIRABLE, ALTYN_ADAM_ASSET);

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
