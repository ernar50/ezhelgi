package kz.ezhelgi.item;

import kz.ezhelgi.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Киіз үй (набор юрты): ПКМ по земле ставит готовую юрту.
 * Кереге (стены), киіз (войлочная крыша), шаңырақ (купол),
 * сырмақ (ковры) и очаг в центре. Вход смотрит туда, откуда подошёл игрок.
 */
public class KiizUyItem extends Item {
    private static final double WALL_RADIUS = 4.5;

    public KiizUyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos center = context.getClickedPos().above();

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!hasRoom(level, center)) {
            if (player != null) {
                player.sendSystemMessage(Component.translatable("message.ezhelgi.kiiz_uy.no_room"));
            }
            return InteractionResult.FAIL;
        }

        // Дверь на стороне, где стоит игрок
        Direction door = context.getHorizontalDirection().getOpposite();
        build(level, center, door);

        level.playSound(null, center, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0f, 0.8f);
        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    private static boolean hasRoom(Level level, BlockPos center) {
        int blocked = 0;
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (dx * dx + dz * dz > WALL_RADIUS * WALL_RADIUS) continue;
                for (int dy = 0; dy <= 5; dy++) {
                    if (!level.getBlockState(center.offset(dx, dy, dz)).canBeReplaced()) {
                        blocked++;
                    }
                }
            }
        }
        // Немного травы, цветов или снега не мешают, а вот стены и деревья — мешают
        return blocked <= 6;
    }

    private static void build(Level level, BlockPos center, Direction door) {
        BlockState kerege = ModBlocks.KEREGE.get().defaultBlockState();
        BlockState kiiz = ModBlocks.KIIZ.get().defaultBlockState();
        BlockState shanyrak = ModBlocks.SHANYRAK.get().defaultBlockState();
        BlockState syrmak = ModBlocks.SYRMAK.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                double r = Math.sqrt(dx * dx + dz * dz);
                if (r > WALL_RADIUS) continue;

                boolean isDoor = isDoorColumn(dx, dz, door);

                // Пол: сырмақ внутри
                if (r <= 3.5) {
                    put(level, center.offset(dx, 0, dz), (dx == 0 && dz == 0) ? Blocks.CAMPFIRE.defaultBlockState() : syrmak);
                    for (int dy = 1; dy <= 3; dy++) put(level, center.offset(dx, dy, dz), air);
                }

                // Стены: кереге в три яруса, проём для двери
                if (r > 3.5) {
                    for (int dy = 0; dy <= 2; dy++) {
                        boolean opening = isDoor && dy <= 1;
                        put(level, center.offset(dx, dy, dz), opening ? air : kerege);
                    }
                }

                // Крыша: войлочный купол
                if (r > 2.5) {
                    put(level, center.offset(dx, 3, dz), kiiz);
                } else if (r > 1.2) {
                    put(level, center.offset(dx, 4, dz), kiiz);
                    put(level, center.offset(dx, 3, dz), air);
                } else {
                    put(level, center.offset(dx, 3, dz), air);
                    put(level, center.offset(dx, 4, dz), air);
                    put(level, center.offset(dx, 5, dz), kiiz);
                }
            }
        }
        // Шаңырақ на вершине, как символ дома
        put(level, center.offset(0, 5, 0), shanyrak);
    }

    private static boolean isDoorColumn(int dx, int dz, Direction door) {
        int along = dx * door.getStepX() + dz * door.getStepZ();
        int side = Math.abs(dx * door.getStepZ() - dz * door.getStepX());
        return along >= 3 && side == 0;
    }

    private static void put(Level level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, 3);
    }
}
