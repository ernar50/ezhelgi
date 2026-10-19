package kz.ezhelgi.block;

import kz.ezhelgi.EzhelgiMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

/** Блоки для юрты (киіз үй) и ковры. */
public final class ModBlocks {
    private ModBlocks() {}

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EzhelgiMod.MODID);

    /** Киіз — белый войлок, покрытие юрты. */
    public static final RegistryObject<Block> KIIZ = register("kiiz", Block::new,
            p -> p.mapColor(MapColor.SNOW).strength(0.8f).sound(SoundType.WOOL).ignitedByLava());

    /** Киіз ступени и плита — для круглой крыши юрты. */
    public static final RegistryObject<Block> KIIZ_STAIRS = register("kiiz_stairs",
            p -> new StairBlock(KIIZ.get().defaultBlockState(), p),
            p -> p.mapColor(MapColor.SNOW).strength(0.8f).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> KIIZ_SLAB = register("kiiz_slab", SlabBlock::new,
            p -> p.mapColor(MapColor.SNOW).strength(0.8f).sound(SoundType.WOOL).ignitedByLava());

    /** Басқұр — войлок с орнаментальной лентой, опоясывает юрту. */
    public static final RegistryObject<Block> BASKUR = register("baskur", Block::new,
            p -> p.mapColor(MapColor.SNOW).strength(0.8f).sound(SoundType.WOOL).ignitedByLava());

    /** Қоңыр киіз — коричневый войлок, ступени и плита. */
    public static final RegistryObject<Block> QONYR_KIIZ = register("qonyr_kiiz", Block::new,
            p -> p.mapColor(MapColor.COLOR_BROWN).strength(0.8f).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> QONYR_KIIZ_STAIRS = register("qonyr_kiiz_stairs",
            p -> new StairBlock(QONYR_KIIZ.get().defaultBlockState(), p),
            p -> p.mapColor(MapColor.COLOR_BROWN).strength(0.8f).sound(SoundType.WOOL).ignitedByLava());
    public static final RegistryObject<Block> QONYR_KIIZ_SLAB = register("qonyr_kiiz_slab", SlabBlock::new,
            p -> p.mapColor(MapColor.COLOR_BROWN).strength(0.8f).sound(SoundType.WOOL).ignitedByLava());
    /** Қоңыр ою — коричневый войлок с белым орнаментом «қошқар мүйіз». */
    public static final RegistryObject<Block> QONYR_OIU = register("qonyr_oiu", Block::new,
            p -> p.mapColor(MapColor.COLOR_BROWN).strength(0.8f).sound(SoundType.WOOL).ignitedByLava());
    /** Ақ ою — белый войлок с коричневым орнаментом. */
    public static final RegistryObject<Block> AQ_OIU = register("aq_oiu", Block::new,
            p -> p.mapColor(MapColor.SNOW).strength(0.8f).sound(SoundType.WOOL).ignitedByLava());

    /** Кереге — решётчатая деревянная стена юрты. */
    public static final RegistryObject<Block> KEREGE = register("kerege", Block::new,
            p -> p.mapColor(MapColor.WOOD).strength(1.5f).sound(SoundType.WOOD).ignitedByLava());

    /** Шаңырақ — купол-круг на вершине юрты, символ дома. */
    public static final RegistryObject<Block> SHANYRAK = register("shanyrak", Block::new,
            p -> p.mapColor(MapColor.WOOD).strength(2.0f).sound(SoundType.WOOD).ignitedByLava());

    // ---------- Ковры ----------
    /** Сырмақ — красный ковёр с орнаментом «қошқар мүйіз». */
    public static final RegistryObject<Block> SYRMAK = carpet("syrmak", MapColor.COLOR_RED);
    /** Көк сырмақ — синий сырмак. */
    public static final RegistryObject<Block> KOK_SYRMAK = carpet("kok_syrmak", MapColor.COLOR_BLUE);
    /** Текемет — валяный войлочный ковёр. */
    public static final RegistryObject<Block> TEKEMET = carpet("tekemet", MapColor.COLOR_BROWN);
    /** Алаша — полосатый тканый ковёр. */
    public static final RegistryObject<Block> ALASHA = carpet("alasha", MapColor.COLOR_ORANGE);
    /** Өрнек — чёрно-золотой узорный ковёр. */
    public static final RegistryObject<Block> ORNEK = carpet("ornek", MapColor.COLOR_BLACK);

    private static RegistryObject<Block> carpet(String name, MapColor color) {
        return register(name, CarpetBlock::new,
                p -> p.mapColor(color).strength(0.1f).sound(SoundType.WOOL).ignitedByLava());
    }

    private static RegistryObject<Block> register(String name,
                                                  Function<BlockBehaviour.Properties, Block> factory,
                                                  Function<BlockBehaviour.Properties, BlockBehaviour.Properties> props) {
        return BLOCKS.register(name, () -> factory.apply(props.apply(BlockBehaviour.Properties.of().setId(BLOCKS.key(name)))));
    }
}
