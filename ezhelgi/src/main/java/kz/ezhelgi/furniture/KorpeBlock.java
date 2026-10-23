package kz.ezhelgi.furniture;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.state.BlockState;

/** Көрпе — стёганая подстилка-подушка. ПКМ — сесть, как гость за дастарханом. */
public class KorpeBlock extends FacingBlock {
    public KorpeBlock(Properties properties) {
        super(properties, Block.box(0, 0, 0, 16, 4, 16), Block.box(0, 0, 0, 16, 4, 16));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && !player.isShiftKeyDown()) {
            Seat.sit(level, pos, 0.25, player);
        }
        return InteractionResult.SUCCESS;
    }
}
