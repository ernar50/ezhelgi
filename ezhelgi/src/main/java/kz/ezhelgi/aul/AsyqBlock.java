package kz.ezhelgi.aul;

import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * Асық қатар — ряд асыков на земле. Сбивай их сақой (бросок ПКМ).
 * Выбитые асыки достаются тебе. Ряд понемногу восстанавливается сам.
 */
public class AsyqBlock extends HorizontalDirectionalBlock {
    public static final IntegerProperty COUNT = IntegerProperty.create("count", 0, 5);
    private static final VoxelShape SHAPE = Block.box(0, 0, 5, 16, 3, 11);

    public AsyqBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(COUNT, 5));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COUNT);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(COUNT) < 5;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // дети снова выставляют асыки в ряд
        if (random.nextInt(4) == 0) {
            level.setBlock(pos, state.setValue(COUNT, state.getValue(COUNT) + 1), 3);
        }
    }

    public static void knock(ServerLevel level, BlockPos pos, BlockState state, @Nullable Entity thrower) {
        int count = state.getValue(COUNT);
        if (count <= 0) {
            if (thrower instanceof Player p) {
                p.sendOverlayMessage(Component.translatable("message.ezhelgi.asyq.empty"));
            }
            return;
        }
        RandomSource r = level.getRandom();
        // попасть точно — не всегда: иногда сақа лишь задевает ряд
        int out = r.nextFloat() < 0.25f ? 0 : Math.min(count, 1 + (r.nextFloat() < 0.35f ? 1 : 0));
        level.playSound(null, pos, ModSounds.TOGYZ_MOVE.get(), SoundSource.BLOCKS, 1.0f, 1.3f);
        if (out == 0) {
            if (thrower instanceof Player p) {
                p.sendOverlayMessage(Component.translatable("message.ezhelgi.asyq.miss"));
            }
            return;
        }
        level.setBlock(pos, state.setValue(COUNT, count - out), 3);
        for (int i = 0; i < out; i++) {
            ItemEntity item = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5,
                    new ItemStack(ModItems.ASYQ.get()));
            item.setDeltaMovement(r.nextGaussian() * 0.15, 0.25, r.nextGaussian() * 0.15);
            level.addFreshEntity(item);
        }
        level.sendParticles(ParticleTypes.CRIT, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, 8, 0.3, 0.1, 0.3, 0.1);
        if (thrower instanceof Player p) {
            p.sendOverlayMessage(Component.translatable("message.ezhelgi.asyq.hit", out));
        }
    }
}
