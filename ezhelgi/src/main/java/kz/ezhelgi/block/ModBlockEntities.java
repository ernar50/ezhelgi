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
}
