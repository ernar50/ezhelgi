package kz.ezhelgi.nature;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/**
 * Қызғалдақ — дикий тюльпан (Шренка или Грейга). Казахстан — родина тюльпанов.
 * Весной степь покрывается ими; дикие тюльпаны после весны отцветают и пропадают,
 * а посаженные игроком растут всегда.
 */
public class QyzgaldaqBlock extends FlowerBlock {
    public static final BooleanProperty WILD = BooleanProperty.create("wild");

    public QyzgaldaqBlock(Properties properties) {
        super(MobEffects.LUCK, 8.0f, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WILD, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WILD);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(WILD);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!NatureEvents.isSpring(level) && random.nextInt(6) == 0) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }
}
