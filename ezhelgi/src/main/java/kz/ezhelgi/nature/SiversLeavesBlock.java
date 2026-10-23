package kz.ezhelgi.nature;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TintedParticleLeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Листва дикой яблони Сиверса. В Алатау растут древние дикие яблони —
 * от них произошли все яблоки мира. Со временем на ветках зреют яблоки: ПКМ — сорвать.
 */
public class SiversLeavesBlock extends TintedParticleLeavesBlock {
    public static final BooleanProperty FRUIT = BooleanProperty.create("fruit");

    public SiversLeavesBlock(Properties properties) {
        super(0.01f, properties);
        this.registerDefaultState(this.defaultBlockState().setValue(FRUIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FRUIT);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (this.decaying(state)) {
            super.randomTick(state, level, pos, random);
            return;
        }
        if (!state.getValue(FRUIT) && random.nextInt(12) == 0 && level.getRawBrightness(pos, 0) >= 9) {
            level.setBlock(pos, state.setValue(FRUIT, true), 2);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!state.getValue(FRUIT)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide()) {
            int n = 1 + level.getRandom().nextInt(2);
            Block.popResource(level, pos.below(), new ItemStack(Items.APPLE, n));
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0f, 0.9f);
            level.setBlock(pos, state.setValue(FRUIT, false), 2);
        }
        return InteractionResult.SUCCESS;
    }
}
