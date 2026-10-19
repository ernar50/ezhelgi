package kz.ezhelgi.block;

import kz.ezhelgi.EzhelgiMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Блоки для юрты (киіз үй). */
public final class ModBlocks {
    private ModBlocks() {}

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EzhelgiMod.MODID);

    /** Киіз — войлок, покрытие юрты. */
    public static final RegistryObject<Block> KIIZ = BLOCKS.register("kiiz", () -> new Block(BlockBehaviour.Properties.of()
            .setId(BLOCKS.key("kiiz"))
            .mapColor(MapColor.WOOL)
            .strength(0.8f)
            .sound(SoundType.WOOL)
            .ignitedByLava()));

    /** Кереге — решётчатая деревянная стена юрты. */
    public static final RegistryObject<Block> KEREGE = BLOCKS.register("kerege", () -> new Block(BlockBehaviour.Properties.of()
            .setId(BLOCKS.key("kerege"))
            .mapColor(MapColor.WOOD)
            .strength(1.5f)
            .sound(SoundType.WOOD)
            .ignitedByLava()));

    /** Шаңырақ — купол-круг на вершине юрты, символ дома. */
    public static final RegistryObject<Block> SHANYRAK = BLOCKS.register("shanyrak", () -> new Block(BlockBehaviour.Properties.of()
            .setId(BLOCKS.key("shanyrak"))
            .mapColor(MapColor.WOOD)
            .strength(2.0f)
            .sound(SoundType.WOOD)
            .ignitedByLava()));

    /** Сырмақ — войлочный ковёр с орнаментом «қошқар мүйіз». */
    public static final RegistryObject<Block> SYRMAK = BLOCKS.register("syrmak", () -> new CarpetBlock(BlockBehaviour.Properties.of()
            .setId(BLOCKS.key("syrmak"))
            .mapColor(MapColor.COLOR_RED)
            .strength(0.1f)
            .sound(SoundType.WOOL)
            .ignitedByLava()));
}
