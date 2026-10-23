package kz.ezhelgi.block;

import kz.ezhelgi.EzhelgiMod;
import kz.ezhelgi.block.togyz.TogyzBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EzhelgiMod.MODID);

    public static final RegistryObject<BlockEntityType<TogyzBlockEntity>> TOGYZ = BLOCK_ENTITIES.register("togyz_qumalaq",
            () -> new BlockEntityType<>(TogyzBlockEntity::new, Set.of(ModBlocks.TOGYZ_QUMALAQ.get())));

    public static final RegistryObject<BlockEntityType<kz.ezhelgi.kitchen.KazanBlockEntity>> KAZAN = BLOCK_ENTITIES.register("kazan",
            () -> new BlockEntityType<>(kz.ezhelgi.kitchen.KazanBlockEntity::new, Set.of(ModBlocks.KAZAN.get())));
    public static final RegistryObject<BlockEntityType<kz.ezhelgi.kitchen.TandyrBlockEntity>> TANDYR = BLOCK_ENTITIES.register("tandyr",
            () -> new BlockEntityType<>(kz.ezhelgi.kitchen.TandyrBlockEntity::new, Set.of(ModBlocks.TANDYR.get())));
    public static final RegistryObject<BlockEntityType<kz.ezhelgi.kitchen.KeptirgishBlockEntity>> KEPTIRGISH = BLOCK_ENTITIES.register("keptirgish",
            () -> new BlockEntityType<>(kz.ezhelgi.kitchen.KeptirgishBlockEntity::new, Set.of(ModBlocks.KEPTIRGISH.get())));
    public static final RegistryObject<BlockEntityType<kz.ezhelgi.kitchen.SamauyrBlockEntity>> SAMAUYR = BLOCK_ENTITIES.register("samauyr",
            () -> new BlockEntityType<>(kz.ezhelgi.kitchen.SamauyrBlockEntity::new, Set.of(ModBlocks.SAMAUYR.get())));
    public static final RegistryObject<BlockEntityType<kz.ezhelgi.furniture.KebezheBlockEntity>> KEBEZHE = BLOCK_ENTITIES.register("kebezhe",
            () -> new BlockEntityType<>(kz.ezhelgi.furniture.KebezheBlockEntity::new, Set.of(ModBlocks.KEBEZHE.get())));
}
