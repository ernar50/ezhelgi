package kz.ezhelgi.aul;

import kz.ezhelgi.entity.ModEntities;
import kz.ezhelgi.entity.SteppeVillager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.event.TickEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Жәрмеңке — ярмарка. Каждый третий день в караван-сарай съезжаются купцы с редкими товарами:
 * зачарованные книги, инструменты, свадебные наряды, самауыр, саженцы яблони Сиверса.
 * На следующий день они уезжают.
 */
public final class Zharmenke {
    private Zharmenke() {}

    public static final TagKey<Structure> HAS_FAIR = TagKey.create(Registries.STRUCTURE,
            Identifier.fromNamespaceAndPath("ezhelgi", "has_fair"));
    /** Караван-сарай (по позиции начала) → день последней ярмарки. */
    private static final Map<Long, Long> HELD = new HashMap<>();

    public static void init() {
        TickEvent.PlayerTickEvent.Post.BUS.addListener(Zharmenke::onPlayerTick);
    }

    public static boolean isFairDay(Level level) {
        long day = level.getOverworldClockTime() / 24000L;
        return day % 3 == 2;
    }

    private static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
        if (!(event.player() instanceof ServerPlayer player) || !(player.level() instanceof ServerLevel level)
                || player.tickCount % 100 != 50 || level.dimension() != Level.OVERWORLD
                || !isFairDay(level) || !level.isBrightOutside()) {
            return;
        }
        StructureStart start = level.structureManager().getStructureWithPieceAt(player.blockPosition(), HAS_FAIR);
        if (!start.isValid()) {
            return;
        }
        long day = level.getOverworldClockTime() / 24000L;
        long key = start.getChunkPos().pack();
        if (HELD.getOrDefault(key, -1L) == day) {
            return;
        }
        HELD.put(key, day);
        BlockPos c = start.getBoundingBox().getCenter();
        var r = level.getRandom();
        for (int i = 0; i < 3; i++) {
            int x = c.getX() + r.nextInt(9) - 4, z = c.getZ() + r.nextInt(9) - 4;
            BlockPos p = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);
            SteppeVillager merchant = ModEntities.KERUENSHI.get().create(level, EntitySpawnReason.EVENT);
            if (merchant == null) continue;
            merchant.makeFair(p, day);
            merchant.snapTo(p.getX() + 0.5, p.getY(), p.getZ() + 0.5, r.nextFloat() * 360, 0);
            level.addFreshEntity(merchant);
        }
        level.playSound(null, c, SoundEvents.VILLAGER_CELEBRATE, SoundSource.NEUTRAL, 1.5f, 1.0f);
        for (ServerPlayer p : level.players()) {
            if (p.blockPosition().closerThan(c, 96)) {
                p.sendSystemMessage(Component.translatable("message.ezhelgi.zharmenke").withStyle(ChatFormatting.GOLD));
            }
        }
    }
}
