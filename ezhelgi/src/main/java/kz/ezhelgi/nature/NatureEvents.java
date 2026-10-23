package kz.ezhelgi.nature;

import kz.ezhelgi.block.ModBlocks;
import kz.ezhelgi.entity.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Природа степи: новые чанки украшаются ковылём, тюльпанами, яблонями Сиверса и саксаулом;
 * весной (первые 6 дней из каждых 24) степь цветёт тюльпанами; ветер катит перекати-поле.
 */
public final class NatureEvents {
    private NatureEvents() {}

    public static final TagKey<Biome> STEPPE = tag("steppe");
    public static final TagKey<Biome> DESERT = tag("sekseuil_habitat");
    public static final TagKey<Biome> APPLE = tag("alma_habitat");
    public static final TagKey<Biome> TUMBLE = tag("qanbaq_habitat");
    private static final ConcurrentLinkedQueue<ChunkPos> QUEUE = new ConcurrentLinkedQueue<>();
    private static long announcedSpring = -1;

    private static TagKey<Biome> tag(String name) {
        return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath("ezhelgi", name));
    }

    public static void init() {
        ChunkEvent.Load.BUS.addListener(NatureEvents::onChunkLoad);
        TickEvent.LevelTickEvent.Post.BUS.addListener(NatureEvents::onLevelTick);
        TickEvent.PlayerTickEvent.Post.BUS.addListener(NatureEvents::onPlayerTick);
        // саксаул — лучшие дрова пустыни: горит вдвое дольше угля
        net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent.BUS.addListener(e -> {
            if (e.getItemStack().is(kz.ezhelgi.item.ModItems.SEKSEUIL.get())) {
                e.setBurnTime(3200);
            }
        });
    }

    public static long day(Level level) {
        return level.getOverworldClockTime() / 24000L;
    }

    /** Весна — первые шесть дней каждого 24-дневного года (Наурыз). */
    public static boolean isSpring(Level level) {
        return day(level) % 24 < 6;
    }

    /** Направление и сила ветра: меняется каждый день и чуть «гуляет» в течение дня. */
    public static Vec3 wind(Level level) {
        long d = day(level);
        RandomSource r = RandomSource.create(d * 341873128712L + 132897987541L);
        double angle = r.nextDouble() * Math.PI * 2 + Math.sin(level.getGameTime() / 900.0) * 0.4;
        double strength = 0.6 + r.nextDouble() * 0.6;
        return new Vec3(Math.cos(angle) * strength, 0, Math.sin(angle) * strength);
    }

    private static void onChunkLoad(ChunkEvent.Load event) {
        if (event.isNewChunk() && event.getChunk() instanceof LevelChunk chunk && chunk.getLevel() instanceof ServerLevel level
                && level.dimension() == Level.OVERWORLD) {
            QUEUE.add(chunk.getPos());
        }
    }

    private static void onLevelTick(TickEvent.LevelTickEvent.Post event) {
        if (!(event.level() instanceof ServerLevel level) || level.dimension() != Level.OVERWORLD) {
            return;
        }
        for (int i = 0; i < 4 && !QUEUE.isEmpty(); i++) {
            ChunkPos cp = QUEUE.poll();
            if (cp != null && level.hasChunk(cp.x(), cp.z())) {
                decorate(level, cp);
            }
        }
        if (level.getGameTime() % 200 == 0 && isSpring(level) && announcedSpring != day(level) / 24) {
            announcedSpring = day(level) / 24;
            for (ServerPlayer p : level.players()) {
                p.sendSystemMessage(Component.translatable("message.ezhelgi.spring").withStyle(ChatFormatting.LIGHT_PURPLE));
            }
        }
    }

    private static BlockPos surface(ServerLevel level, int x, int z) {
        return new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);
    }

    private static boolean grassy(BlockState s) {
        return s.is(BlockTags.DIRT) && !s.is(net.minecraft.world.level.block.Blocks.MUD);
    }

    private static boolean place(ServerLevel level, BlockPos pos, BlockState plant) {
        if (!level.isLoaded(pos) || !level.getBlockState(pos).isAir() || !plant.canSurvive(level, pos)) return false;
        level.setBlock(pos, plant, 2);
        return true;
    }

    private static void patch(ServerLevel level, BlockPos center, RandomSource r, int count, int spread, BlockState... plants) {
        for (int i = 0; i < count; i++) {
            int x = center.getX() + r.nextInt(spread * 2 + 1) - spread;
            int z = center.getZ() + r.nextInt(spread * 2 + 1) - spread;
            if (!level.isLoaded(new BlockPos(x, 0, z))) continue;
            BlockPos p = surface(level, x, z);
            if (grassy(level.getBlockState(p.below()))) {
                place(level, p, plants[r.nextInt(plants.length)]);
            }
        }
    }

    private static BlockState tulip(RandomSource r, boolean wild) {
        Block b = r.nextBoolean() ? ModBlocks.QYZGALDAQ_SHRENK.get() : ModBlocks.QYZGALDAQ_GREIG.get();
        return b.defaultBlockState().setValue(QyzgaldaqBlock.WILD, wild);
    }

    private static void decorate(ServerLevel level, ChunkPos cp) {
        RandomSource r = RandomSource.create(level.getSeed() ^ (cp.x() * 341873128712L + cp.z() * 132897987541L) ^ 0x5eedL);
        int x = cp.getMinBlockX() + 8, z = cp.getMinBlockZ() + 8;
        BlockPos mid = surface(level, x, z);
        var biome = level.getBiome(mid);
        if (biome.is(STEPPE)) {
            if (r.nextInt(10) < 7) {
                BlockState seleu = ModBlocks.SELEU.get().defaultBlockState();
                for (int i = 0, n = 1 + r.nextInt(3); i < n; i++) {
                    patch(level, mid.offset(r.nextInt(12) - 6, 0, r.nextInt(12) - 6), r, 10 + r.nextInt(10), 4, seleu);
                }
            }
            if (r.nextInt(4) == 0) {
                patch(level, mid, r, 8 + r.nextInt(8), 5, tulip(r, false), tulip(r, false));
            }
        }
        if (biome.is(APPLE) && r.nextInt(9) == 0) {
            BlockPos p = surface(level, cp.getMinBlockX() + 3 + r.nextInt(10), cp.getMinBlockZ() + 3 + r.nextInt(10));
            if (grassy(level.getBlockState(p.below()))) {
                Trees.apple(level, p, r);
            }
        }
        if (biome.is(DESERT) && r.nextInt(5) == 0) {
            for (int i = 0, n = 1 + r.nextInt(2); i < n; i++) {
                BlockPos p = surface(level, cp.getMinBlockX() + 3 + r.nextInt(10), cp.getMinBlockZ() + 3 + r.nextInt(10));
                BlockState below = level.getBlockState(p.below());
                if (below.is(BlockTags.SAND) || grassy(below) || below.is(BlockTags.TERRACOTTA)) {
                    Trees.saxaul(level, p, r);
                }
            }
        }
    }

    private static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
        if (!(event.player() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)
                || level.dimension() != Level.OVERWORLD || player.tickCount % 100 != 0) {
            return;
        }
        RandomSource r = level.getRandom();
        BlockPos at = player.blockPosition();
        var biome = level.getBiome(at);
        // весна: степь понемногу зацветает вокруг игрока
        if (isSpring(level) && biome.is(STEPPE) && player.tickCount % 400 == 0) {
            int x = at.getX() + r.nextInt(48) - 24, z = at.getZ() + r.nextInt(48) - 24;
            BlockPos p = surface(level, x, z);
            if (level.isLoaded(p) && grassy(level.getBlockState(p.below()))
                    && level.getBlockStates(new AABB(p).inflate(3)).filter(s -> s.getBlock() instanceof QyzgaldaqBlock).count() < 4) {
                patch(level, p, r, 4 + r.nextInt(5), 2, tulip(r, true), tulip(r, true));
            }
        }
        // перекати-поле днём в сухой степи и пустыне
        if (level.isBrightOutside() && !level.isRaining() && biome.is(TUMBLE) && r.nextInt(6) == 0
                && level.getEntitiesOfClass(Qanbaq.class, player.getBoundingBox().inflate(64)).size() < 3) {
            Vec3 w = wind(level);
            double len = Math.max(0.1, w.length());
            // появляется с подветренной стороны и катится к игроку
            double px = player.getX() - w.x / len * 36 + r.nextGaussian() * 10;
            double pz = player.getZ() - w.z / len * 36 + r.nextGaussian() * 10;
            BlockPos p = surface(level, (int) Math.floor(px), (int) Math.floor(pz));
            if (level.isLoaded(p) && level.getBlockState(p).isAir() && level.getBlockState(p.below()).getFluidState().isEmpty()) {
                Qanbaq q = ModEntities.QANBAQ.get().create(level, EntitySpawnReason.NATURAL);
                if (q != null) {
                    q.snapTo(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, r.nextFloat() * 360, 0);
                    level.addFreshEntity(q);
                }
            }
        }
    }
}
