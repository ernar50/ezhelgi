package kz.ezhelgi.nature;

import kz.ezhelgi.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/** Деревья, выращиваемые кодом: дикая яблоня Сиверса и саксаул. */
public final class Trees {
    private Trees() {}

    private static boolean free(LevelAccessor level, BlockPos pos) {
        if (!level.hasChunkAt(pos)) return false;
        BlockState s = level.getBlockState(pos);
        return s.isAir() || s.canBeReplaced() || s.is(net.minecraft.tags.BlockTags.LEAVES) || s.getBlock() instanceof KochetBlock;
    }

    private static void log(LevelAccessor level, BlockPos pos, BlockState log, Direction.Axis axis, List<BlockPos> logs) {
        if (free(level, pos)) {
            level.setBlock(pos, log.setValue(RotatedPillarBlock.AXIS, axis), 2);
            logs.add(pos.immutable());
        }
    }

    private static void leaves(LevelAccessor level, BlockPos pos, BlockState leaves, List<BlockPos> logs) {
        if (!level.hasChunkAt(pos)) return;
        BlockState cur = level.getBlockState(pos);
        if (!(cur.isAir() || cur.canBeReplaced())) return;
        int d = 7;
        for (BlockPos l : logs) {
            d = Math.min(d, Math.abs(l.getX() - pos.getX()) + Math.abs(l.getY() - pos.getY()) + Math.abs(l.getZ() - pos.getZ()));
        }
        if (d >= 7) return;
        level.setBlock(pos, leaves.setValue(LeavesBlock.DISTANCE, Math.max(1, d)), 2);
    }

    /** Дикая яблоня: невысокий кривой ствол и широкая округлая крона, иногда с яблоками. */
    public static boolean apple(LevelAccessor level, BlockPos base, RandomSource r) {
        int h = 4 + r.nextInt(3);
        for (int y = 1; y <= h + 2; y++) {
            if (!free(level, base.above(y))) return false;
        }
        List<BlockPos> logs = new ArrayList<>();
        BlockState logState = Blocks.OAK_LOG.defaultBlockState();
        BlockPos.MutableBlockPos p = base.mutable();
        int dx = r.nextInt(3) - 1, dz = r.nextInt(3) - 1;
        level.setBlock(base, Blocks.AIR.defaultBlockState(), 2);
        for (int y = 0; y < h; y++) {
            log(level, p, logState, Direction.Axis.Y, logs);
            if (y == h / 2 && (dx != 0 || dz != 0)) {
                p.move(dx, 0, dz);
                log(level, p, logState, Direction.Axis.Y, logs);
            }
            p.move(Direction.UP);
        }
        // пара боковых ветвей
        BlockPos top = p.below();
        for (Direction d : Direction.Plane.HORIZONTAL) {
            if (r.nextInt(2) == 0) {
                BlockPos b = top.below(r.nextInt(2)).relative(d);
                log(level, b, logState, d.getAxis(), logs);
            }
        }
        BlockState leafState = ModBlocks.SIVERS_JAPYRAGY.get().defaultBlockState();
        for (int y = -2; y <= 2; y++) {
            int rad = y >= 2 ? 1 : (y <= -2 ? 2 : 3);
            for (int x = -rad; x <= rad; x++) {
                for (int z = -rad; z <= rad; z++) {
                    if (x * x + z * z + y * y * 1.4 > rad * rad + 1.5 || (Math.abs(x) == rad && Math.abs(z) == rad && r.nextInt(2) == 0)) continue;
                    BlockState ls = r.nextInt(5) == 0 ? leafState.setValue(SiversLeavesBlock.FRUIT, true) : leafState;
                    leaves(level, top.offset(x, y, z), ls, logs);
                }
            }
        }
        return true;
    }

    /** Саксаул: низкое корявое дерево пустыни с редкой серо-зелёной кроной. */
    public static boolean saxaul(LevelAccessor level, BlockPos base, RandomSource r) {
        int h = 2 + r.nextInt(2);
        for (int y = 1; y <= h + 2; y++) {
            if (!free(level, base.above(y))) return false;
        }
        List<BlockPos> logs = new ArrayList<>();
        BlockState logState = ModBlocks.SEKSEUIL.get().defaultBlockState();
        level.setBlock(base, Blocks.AIR.defaultBlockState(), 2);
        BlockPos.MutableBlockPos p = base.mutable();
        for (int y = 0; y < h; y++) {
            log(level, p, logState, Direction.Axis.Y, logs);
            p.move(Direction.UP);
        }
        BlockPos top = p.below();
        List<BlockPos> tips = new ArrayList<>();
        for (Direction d : Direction.Plane.HORIZONTAL) {
            if (r.nextInt(3) == 0) continue;
            BlockPos b = top.relative(d);
            log(level, b, logState, d.getAxis(), logs);
            BlockPos up = b.relative(d).above();
            log(level, up, logState, Direction.Axis.Y, logs);
            tips.add(up);
        }
        tips.add(top.above());
        log(level, top.above(), logState, Direction.Axis.Y, logs);
        BlockState leafState = ModBlocks.SEKSEUIL_JAPYRAGY.get().defaultBlockState();
        for (BlockPos t : tips) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    for (int y = 0; y <= 1; y++) {
                        if (r.nextInt(3) == 0) continue;
                        leaves(level, t.offset(x, y, z), leafState, logs);
                    }
                }
            }
        }
        return true;
    }
}
