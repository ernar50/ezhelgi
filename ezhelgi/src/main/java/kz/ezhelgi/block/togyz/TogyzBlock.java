package kz.ezhelgi.block.togyz;

import kz.ezhelgi.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * Тоғыз құмалақ — низкая доска на два блока. Ближний ряд лунок — твой, дальний — аксакала.
 * ПКМ по своей лунке — ход. ПКМ по середине доски — счёт.
 * Shift+ПКМ — новая партия, Shift+ПКМ по середине — играть вдвоём / с аксакалом.
 */
public class TogyzBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    /** FOOT — главная половина с партией, HEAD — правая половина. */
    public static final EnumProperty<BedPart> PART = BlockStateProperties.BED_PART;
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 4, 16);

    public TogyzBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, BedPart.FOOT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, PART);
    }

    /** Направление от главной половины к правой. */
    public static Direction along(BlockState state) {
        return state.getValue(FACING).getClockWise();
    }

    public static BlockPos mainPos(BlockState state, BlockPos pos) {
        return state.getValue(PART) == BedPart.FOOT ? pos : pos.relative(along(state).getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        BlockPos other = context.getClickedPos().relative(facing.getClockWise());
        Level level = context.getLevel();
        if (!level.getBlockState(other).canBeReplaced(context) || !level.getWorldBorder().isWithinBounds(other)) {
            return null;
        }
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack stack) {
        if (!level.isClientSide()) {
            level.setBlock(pos.relative(along(state)), state.setValue(PART, BedPart.HEAD), 3);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos,
                                     Direction direction, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        Direction toPartner = state.getValue(PART) == BedPart.FOOT ? along(state) : along(state).getOpposite();
        if (direction == toPartner) {
            boolean ok = neighbourState.is(this) && neighbourState.getValue(PART) != state.getValue(PART);
            return ok ? state : Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, level, ticks, pos, direction, neighbourPos, neighbourState, random);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && state.getValue(PART) == BedPart.HEAD) {
            BlockPos main = mainPos(state, pos);
            BlockState mainState = level.getBlockState(main);
            if (mainState.is(this)) {
                if (!player.preventsBlockDrops()) {
                    dropResources(mainState, level, main, level.getBlockEntity(main), player, player.getMainHandItem());
                }
                level.setBlock(main, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, main, Block.getId(mainState));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level instanceof ServerLevel server)) {
            return InteractionResult.SUCCESS;
        }
        BlockPos main = mainPos(state, pos);
        if (!(level.getBlockEntity(main) instanceof TogyzBlockEntity be)) {
            return InteractionResult.PASS;
        }
        Direction facing = state.getValue(FACING);
        Direction l = along(state);
        Vec3 rel = hit.getLocation().subtract(Vec3.atCenterOf(main));
        double u = rel.x * l.getStepX() + rel.z * l.getStepZ() + 0.5;       // 0..2 вдоль доски
        double v = rel.x * facing.getStepX() + rel.z * facing.getStepZ() + 0.5; // 0..1 от игрока
        boolean middle = v > 0.40 && v < 0.60;
        if (player.isShiftKeyDown()) {
            be.newGame(middle);
            player.sendOverlayMessage(net.minecraft.network.chat.Component.translatable(
                    be.vsAi ? "message.ezhelgi.togyz.new_ai" : "message.ezhelgi.togyz.new_pvp"));
            return InteractionResult.SUCCESS;
        }
        if (middle) {
            player.sendOverlayMessage(be.score());
            return InteractionResult.SUCCESS;
        }
        int side = v < 0.5 ? 0 : 1;
        int column = Math.max(0, Math.min(8, (int) (u / 2.0 * 9.0)));
        // игрок 0 считает лунки слева направо, игрок 1 — со своей стороны
        int index = side == 0 ? column : 8 - column;
        be.playerMove(server, player, side, index);
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(PART) == BedPart.FOOT ? new TogyzBlockEntity(pos, state) : null;
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.TOGYZ.get(), TogyzBlockEntity::serverTick);
    }
}
