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

    // ---------- Руны ----------
    /** Руна тас — камень с древнетюркскими рунами, светится, пока не пробуждён. */
    public static final RegistryObject<Block> RUNA_TASY = register("runa_tasy", RuneStoneBlock::new,
            p -> p.mapColor(MapColor.STONE).strength(3.0f, 9.0f).requiresCorrectToolForDrops()
                    .lightLevel(state -> state.getValue(RuneStoneBlock.LIT) ? 9 : 0));

    // ---------- v7: тоғыз құмалақ, флаги, петроглифы, поющий песок ----------
    public static final RegistryObject<Block> TOGYZ_QUMALAQ = register("togyz_qumalaq", kz.ezhelgi.block.togyz.TogyzBlock::new,
            p -> p.mapColor(MapColor.WOOD).strength(1.5f).sound(SoundType.WOOD).noOcclusion().ignitedByLava());

    public static final String[] FLAGS = {"tu_qazaqstan", "tu_alash", "tu_handyq", "tu_uly_juz", "tu_orta_juz", "tu_kishi_juz"};
    public static final RegistryObject<Block> TU_QAZAQSTAN = flag("tu_qazaqstan");
    public static final RegistryObject<Block> TU_ALASH = flag("tu_alash");
    public static final RegistryObject<Block> TU_HANDYQ = flag("tu_handyq");
    public static final RegistryObject<Block> TU_ULY_JUZ = flag("tu_uly_juz");
    public static final RegistryObject<Block> TU_ORTA_JUZ = flag("tu_orta_juz");
    public static final RegistryObject<Block> TU_KISHI_JUZ = flag("tu_kishi_juz");

    /** Таңба тас — скала с петроглифами Тамгалы. */
    public static final RegistryObject<Block> TANBA_TAS = register("tanba_tas", TanbaTasBlock::new,
            p -> p.mapColor(MapColor.TERRACOTTA_BROWN).strength(2.5f, 8.0f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    /** Ән құм — поющий песок. */
    public static final RegistryObject<Block> AN_QUM = register("an_qum", AnQumBlock::new,
            p -> p.mapColor(MapColor.SAND).strength(0.5f).sound(SoundType.SAND));

    /** Асық қатар — ряд асыков для игры асық ату. */
    public static final RegistryObject<Block> ASYQ_QATAR = register("asyq_qatar", kz.ezhelgi.aul.AsyqBlock::new,
            p -> p.mapColor(MapColor.SNOW).strength(0.3f).sound(SoundType.BONE_BLOCK).noOcclusion().noCollision());

    // ---------- v8: кухня аула ----------
    public static final RegistryObject<Block> KAZAN = register("kazan", kz.ezhelgi.kitchen.KazanBlock::new,
            p -> p.mapColor(MapColor.COLOR_BLACK).strength(3.0f, 6.0f).sound(SoundType.METAL).noOcclusion());
    public static final RegistryObject<Block> TANDYR = register("tandyr", kz.ezhelgi.kitchen.TandyrBlock::new,
            p -> p.mapColor(MapColor.TERRACOTTA_ORANGE).strength(2.0f, 6.0f).sound(SoundType.MUD_BRICKS).noOcclusion()
                    .lightLevel(s -> s.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) ? 12 : 0));
    public static final RegistryObject<Block> KEPTIRGISH = register("keptirgish", kz.ezhelgi.kitchen.KeptirgishBlock::new,
            p -> p.mapColor(MapColor.WOOD).strength(1.0f).sound(SoundType.WOOD).noOcclusion().ignitedByLava());
    public static final RegistryObject<Block> SAMAUYR = register("samauyr", kz.ezhelgi.kitchen.SamauyrBlock::new,
            p -> p.mapColor(MapColor.COLOR_ORANGE).strength(2.0f, 6.0f).sound(SoundType.COPPER).noOcclusion()
                    .lightLevel(s -> s.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) ? 7 : 0));

    // ---------- v8: мебель ----------
    public static final RegistryObject<Block> DASTARKHAN = register("dastarkhan",
            p -> new kz.ezhelgi.furniture.FacingBlock(p, Block.box(0, 0, 0, 16, 7, 16), Block.box(0, 0, 0, 16, 7, 16)),
            p -> p.mapColor(MapColor.COLOR_RED).strength(1.0f).sound(SoundType.WOOD).noOcclusion().ignitedByLava());
    public static final RegistryObject<Block> KORPE = register("korpe", kz.ezhelgi.furniture.KorpeBlock::new,
            p -> p.mapColor(MapColor.COLOR_RED).strength(0.5f).sound(SoundType.WOOL).noOcclusion().ignitedByLava());
    public static final RegistryObject<Block> KEBEZHE = register("kebezhe", kz.ezhelgi.furniture.KebezheBlock::new,
            p -> p.mapColor(MapColor.WOOD).strength(2.5f).sound(SoundType.WOOD).noOcclusion().ignitedByLava());
    public static final RegistryObject<Block> BESIK = register("besik", kz.ezhelgi.furniture.BesikBlock::new,
            p -> p.mapColor(MapColor.WOOD).strength(1.0f).sound(SoundType.WOOD).noOcclusion().ignitedByLava());

    // ---------- v8: природа ----------
    public static final RegistryObject<Block> QYZGALDAQ_SHRENK = plant("qyzgaldaq_shrenk", kz.ezhelgi.nature.QyzgaldaqBlock::new, MapColor.COLOR_PINK);
    public static final RegistryObject<Block> QYZGALDAQ_GREIG = plant("qyzgaldaq_greig", kz.ezhelgi.nature.QyzgaldaqBlock::new, MapColor.COLOR_RED);
    public static final RegistryObject<Block> SELEU = plant("seleu",
            p -> new net.minecraft.world.level.block.FlowerBlock(net.minecraft.world.effect.MobEffects.SPEED, 4.0f, p), MapColor.QUARTZ);
    public static final RegistryObject<Block> SIVERS_JAPYRAGY = register("sivers_japyragy", kz.ezhelgi.nature.SiversLeavesBlock::new,
            p -> leaves(p, MapColor.PLANT));
    public static final RegistryObject<Block> ALMA_KOSHETI = register("alma_kosheti", p -> new kz.ezhelgi.nature.KochetBlock(p, false),
            p -> p.mapColor(MapColor.PLANT).noCollision().randomTicks().instabreak().sound(SoundType.GRASS)
                    .pushReaction(net.minecraft.world.level.material.PushReaction.POPPED));
    public static final RegistryObject<Block> SEKSEUIL = register("sekseuil", net.minecraft.world.level.block.RotatedPillarBlock::new,
            p -> p.mapColor(MapColor.TERRACOTTA_GRAY).strength(2.5f).sound(SoundType.WOOD).ignitedByLava());
    public static final RegistryObject<Block> SEKSEUIL_JAPYRAGY = register("sekseuil_japyragy",
            p -> new net.minecraft.world.level.block.TintedParticleLeavesBlock(0.01f, p), p -> leaves(p, MapColor.TERRACOTTA_LIGHT_GREEN));
    public static final RegistryObject<Block> SEKSEUIL_KOSHETI = register("sekseuil_kosheti", p -> new kz.ezhelgi.nature.KochetBlock(p, true),
            p -> p.mapColor(MapColor.PLANT).noCollision().randomTicks().instabreak().sound(SoundType.GRASS)
                    .pushReaction(net.minecraft.world.level.material.PushReaction.POPPED));

    private static RegistryObject<Block> plant(String name, Function<BlockBehaviour.Properties, Block> factory, MapColor color) {
        return register(name, factory, p -> p.mapColor(color).noCollision().instabreak().sound(SoundType.GRASS)
                .offsetType(BlockBehaviour.OffsetType.XZ).pushReaction(net.minecraft.world.level.material.PushReaction.POPPED));
    }

    private static BlockBehaviour.Properties leaves(BlockBehaviour.Properties p, MapColor color) {
        return p.mapColor(color).strength(0.2f).randomTicks().sound(SoundType.GRASS).noOcclusion()
                .isSuffocating((s, l, pos) -> false).ignitedByLava()
                .pushReaction(net.minecraft.world.level.material.PushReaction.POPPED).isRedstoneConductor((s, l, pos) -> false);
    }

    private static RegistryObject<Block> flag(String name) {
        return register(name, TuBlock::new,
                p -> p.mapColor(MapColor.WOOD).strength(1.0f).sound(SoundType.WOOD).noOcclusion().ignitedByLava());
    }

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
