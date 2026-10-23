package kz.ezhelgi.kitchen;

import kz.ezhelgi.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

/**
 * Қазан — чугунный котёл. Поставь на костёр, налей воды, положи продукты:
 * мясо — сорпа; мясо и картофель — қуырдақ; мясо, морковь и пшеница — палау;
 * шесть продуктов (мясо, пшеница, молоко, құрт, морковь, свёкла) — наурыз-көже.
 * Готовое блюдо разливай в миски.
 */
public class KazanBlock extends BaseEntityBlock {
    /** 0 — пустой, 1 — вода, 2 — варится, 3 — готово. */
    public static final IntegerProperty STAGE = IntegerProperty.create("stage", 0, 3);
    private static final VoxelShape SHAPE = Shapes.or(Block.box(2, 0, 2, 14, 9, 14), Block.box(1, 7, 1, 15, 10, 15));

    public KazanBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(STAGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                          InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof KazanBlockEntity be)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        int st = state.getValue(STAGE);
        if (stack.is(Items.WATER_BUCKET) && st == 0) {
            if (!level.isClientSide()) {
                be.fillWater();
                Cook.exchange(player, hand, new ItemStack(Items.BUCKET));
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.is(Items.BOWL) && st == 3) {
            if (!level.isClientSide()) {
                ItemStack dish = be.serve();
                if (dish != null) {
                    Cook.exchange(player, hand, dish);
                    level.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 0.8f);
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (st == 1 && KazanBlockEntity.Part.of(stack) != null) {
            if (!level.isClientSide()) {
                boolean milk = stack.is(Items.MILK_BUCKET);
                if (be.add(stack, player)) {
                    if (milk) {
                        Cook.exchange(player, hand, new ItemStack(Items.BUCKET));
                    } else {
                        Cook.consume(player, stack, 1);
                    }
                    level.playSound(null, pos, SoundEvents.GENERIC_SPLASH, SoundSource.BLOCKS, 0.4f, 1.4f);
                }
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof KazanBlockEntity be) {
            int st = state.getValue(STAGE);
            if (player.isShiftKeyDown() && (st == 1 || st == 2)) {
                be.dump(level, pos);
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 0.8f);
                player.sendOverlayMessage(Component.translatable("message.ezhelgi.kazan.emptied"));
            } else {
                player.sendOverlayMessage(be.status());
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new KazanBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(type, ModBlockEntities.KAZAN.get(), KazanBlockEntity::serverTick);
    }
}
