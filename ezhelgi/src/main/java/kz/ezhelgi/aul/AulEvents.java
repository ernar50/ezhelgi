package kz.ezhelgi.aul;

import kz.ezhelgi.block.ModBlocks;
import kz.ezhelgi.block.TuBlock;
import kz.ezhelgi.entity.Aqsaqal;
import kz.ezhelgi.entity.ModEntities;
import kz.ezhelgi.entity.SteppeVillager;
import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Праздники аула. Раз в день аксакал может объявить одно из событий:
 * <ul>
 *   <li>Той и беташар — свадьба: невеста, гости, шашу, угощение каждому.</li>
 *   <li>Айтыс — два акына состязаются в стихах, игрок выбирает победителя.</li>
 *   <li>Көкпар — две команды всадников борются за серке; игрок играет за синих.</li>
 *   <li>Бәйге — скачки вокруг аула, приз — породистый конь.</li>
 * </ul>
 */
public final class AulEvents {
    private AulEvents() {}

    public enum Type { TOI, AITYS, KOKPAR, BAIGE, RAID, WOLVES }

    private static final class Event {
        final UUID id;
        final Type type;
        final ServerLevel level;
        final UUID aksakal;
        final BlockPos center;
        long tick;
        long end;
        final List<UUID> spawned = new ArrayList<>();
        final List<BlockPos> placed = new ArrayList<>();
        final Set<UUID> players = new HashSet<>();
        // көкпар
        BlockPos field, goalRed, goalBlue;
        int scoreRed, scoreBlue;
        @Nullable UUID serke, holder;
        // бәйге
        final List<BlockPos> waypoints = new ArrayList<>();
        final Map<UUID, Integer> progress = new HashMap<>();
        boolean started;
        // айтыс
        @Nullable UUID aqyn1, aqyn2;
        int verse;
        final List<Integer> verses = new ArrayList<>();
        boolean voting, voted;
        // той
        final Set<UUID> gifted = new HashSet<>();
        // набег и волки
        final List<UUID> raiders = new ArrayList<>();
        int wave;
        long waveAt;

        Event(Type type, ServerLevel level, Aqsaqal aksakal, BlockPos center) {
            this.id = UUID.randomUUID();
            this.type = type;
            this.level = level;
            this.aksakal = aksakal.getUUID();
            this.center = center;
        }
    }

    private static final Map<UUID, Event> ACTIVE = new HashMap<>();
    public static final int VERSES = 14;
    public static final int AQYN_NAMES = 6;

    public static boolean isActive(UUID eventId) {
        return ACTIVE.containsKey(eventId);
    }

    public static boolean isSerkeInGame(UUID serke) {
        for (Event e : ACTIVE.values()) {
            if (serke.equals(e.serke)) return true;
        }
        return false;
    }

    public static boolean isAqynInGame(UUID aqyn) {
        for (Event e : ACTIVE.values()) {
            if (aqyn.equals(e.aqyn1) || aqyn.equals(e.aqyn2)) return true;
        }
        return false;
    }

    private static @Nullable Event byAksakal(UUID aksakal) {
        for (Event e : ACTIVE.values()) {
            if (e.aksakal.equals(aksakal)) return e;
        }
        return null;
    }

    // ------------------------------------------------------------------ запуск
    /** Вызывается аксакалом раз в 10 секунд. Возвращает true, если праздник начался. */
    public static boolean maybeStart(Aqsaqal aksakal, ServerLevel level, long lastDay) {
        if (byAksakal(aksakal.getUUID()) != null || !level.isBrightOutside()) {
            return false;
        }
        Player near = level.getNearestPlayer(aksakal, 40.0);
        if (near == null) {
            return false;
        }
        RandomSource r = aksakal.getRandom();
        Type[] types = {Type.TOI, Type.AITYS, Type.AITYS, Type.KOKPAR, Type.KOKPAR, Type.BAIGE, Type.BAIGE};
        start(aksakal, level, types[r.nextInt(types.length)]);
        return true;
    }

    public static void start(Aqsaqal aksakal, ServerLevel level, Type type) {
        if (byAksakal(aksakal.getUUID()) != null) return;
        BlockPos center = aksakal.hasHome() ? aksakal.getHomePosition() : aksakal.blockPosition();
        Event e = new Event(type, level, aksakal, center);
        e.tick = 0;
        ACTIVE.put(e.id, e);
        switch (type) {
            case TOI -> startToi(e, aksakal);
            case AITYS -> startAitys(e, aksakal);
            case KOKPAR -> startKokpar(e, aksakal);
            case BAIGE -> startBaige(e, aksakal);
            case RAID -> startRaid(e, aksakal);
            case WOLVES -> startWolves(e, aksakal);
        }
    }

    private static List<Component> lines(String prefix, int n, Object... args) {
        List<Component> out = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            out.add(Component.translatable(prefix + "." + i, args));
        }
        return out;
    }

    private static BlockPos surface(ServerLevel level, double x, double z) {
        int ix = (int) Math.floor(x), iz = (int) Math.floor(z);
        level.getChunk(ix >> 4, iz >> 4);
        return new BlockPos(ix, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ix, iz), iz);
    }

    private static <T extends Mob> @Nullable T spawn(Event e, EntityType<T> type, BlockPos pos) {
        T mob = type.create(e.level, EntitySpawnReason.EVENT);
        if (mob == null) return null;
        mob.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, e.level.getRandom().nextFloat() * 360f, 0f);
        mob.finalizeSpawn(e.level, e.level.getCurrentDifficultyAt(pos), EntitySpawnReason.EVENT, null);
        e.level.addFreshEntity(mob);
        e.spawned.add(mob.getUUID());
        return mob;
    }

    /** Флаг-веха на поверхности; запоминаем, чтобы убрать после праздника. */
    private static void flag(Event e, BlockPos pos, Block block) {
        if (e.level.getBlockState(pos).canBeReplaced() && e.level.getBlockState(pos.above()).canBeReplaced()) {
            BlockState lower = block.defaultBlockState().setValue(TuBlock.FACING, Direction.SOUTH);
            e.level.setBlock(pos, lower, 3);
            e.level.setBlock(pos.above(), lower.setValue(TuBlock.HALF, DoubleBlockHalf.UPPER), 3);
            e.placed.add(pos.immutable());
        }
    }

    /** Ровное место для поля в 20–30 блоках от аула. */
    private static BlockPos fieldNear(Event e, int dist) {
        RandomSource r = e.level.getRandom();
        BlockPos best = surface(e.level, e.center.getX() + dist, e.center.getZ());
        int bestScore = Integer.MAX_VALUE;
        for (int k = 0; k < 8; k++) {
            double a = k * Math.PI / 4 + r.nextDouble() * 0.3;
            BlockPos p = surface(e.level, e.center.getX() + Math.cos(a) * dist, e.center.getZ() + Math.sin(a) * dist);
            int score = 0;
            for (int i = -12; i <= 12; i += 6) {
                BlockPos q = surface(e.level, p.getX() + i, p.getZ() + i / 2);
                score += Math.abs(q.getY() - p.getY());
                if (!e.level.getFluidState(q.below()).isEmpty()) score += 20;
            }
            score += Math.abs(p.getY() - e.center.getY()) * 2;
            if (score < bestScore) {
                bestScore = score;
                best = p;
            }
        }
        return best;
    }

    private static void broadcast(Event e, Component text) {
        for (Player p : e.level.players()) {
            if (p.distanceToSqr(Vec3.atCenterOf(e.center)) < 96 * 96) {
                p.sendSystemMessage(text);
            }
        }
    }

    private static @Nullable Aqsaqal aksakal(Event e) {
        return e.level.getEntity(e.aksakal) instanceof Aqsaqal a ? a : null;
    }

    private static void give(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack) && player.level() instanceof ServerLevel level) {
            player.spawnAtLocation(level, stack);
        }
    }

    // ------------------------------------------------------------------ той
    private static void startToi(Event e, Aqsaqal a) {
        e.end = 20 * 60 * 5;
        a.announce(lines("aul.ezhelgi.toi", 5));
        RandomSource r = e.level.getRandom();
        BlockPos c = e.center;
        SteppeVillager kelin = spawn(e, ModEntities.KELIN.get(), surface(e.level, c.getX() + 2, c.getZ() + 1));
        if (kelin != null) {
            kelin.setPersistenceRequired();
            e.spawned.remove(kelin.getUUID()); // невеста остаётся жить в ауле
        }
        for (int i = 0; i < 3; i++) {
            double ang = r.nextDouble() * Math.PI * 2;
            spawn(e, ModEntities.AUYLDAS.get(), surface(e.level, c.getX() + Math.cos(ang) * 5, c.getZ() + Math.sin(ang) * 5));
        }
        spawn(e, ModEntities.KUISHI.get(), surface(e.level, c.getX() - 3, c.getZ() - 2));
        // шашу — бросают сладости над невестой
        for (int i = 0; i < 10; i++) {
            ItemStack sweet = new ItemStack(r.nextBoolean() ? Items.COOKIE : (r.nextBoolean() ? Items.SUGAR : ModItems.BAURSAK.get()));
            ItemEntity it = new ItemEntity(e.level, c.getX() + 2.5, c.getY() + 2.5, c.getZ() + 1.5, sweet);
            it.setDeltaMovement(r.nextGaussian() * 0.2, 0.3, r.nextGaussian() * 0.2);
            e.level.addFreshEntity(it);
        }
    }

    /** Во время тоя каждый житель угощает игрока (один раз за праздник). */
    public static boolean toiTreat(ServerLevel level, SteppeVillager villager, Player player) {
        for (Event e : ACTIVE.values()) {
            if (e.type == Type.TOI && e.level == level && villager.distanceToSqr(Vec3.atCenterOf(e.center)) < 40 * 40) {
                if (!e.gifted.add(player.getUUID())) {
                    return false;
                }
                give(player, new ItemStack(ModItems.BAURSAK.get(), 6));
                give(player, new ItemStack(ModItems.KAZY.get(), 2));
                give(player, new ItemStack(ModItems.KUMYS.get()));
                player.sendSystemMessage(Component.translatable("aul.ezhelgi.toi.treat").withStyle(ChatFormatting.GOLD));
                level.sendParticles(ParticleTypes.HEART, villager.getX(), villager.getY() + 2, villager.getZ(), 4, 0.3, 0.2, 0.3, 0);
                return true;
            }
        }
        return false;
    }

    private static void tickToi(Event e) {
        if (e.tick % 60 == 0) {
            for (UUID id : e.spawned) {
                Entity g = e.level.getEntity(id);
                if (g != null) {
                    e.level.sendParticles(ParticleTypes.NOTE, g.getX(), g.getY() + 2.2, g.getZ(), 1, 0.2, 0.1, 0.2, e.level.getRandom().nextDouble());
                }
            }
        }
    }

    // ------------------------------------------------------------------ айтыс
    private static void startAitys(Event e, Aqsaqal a) {
        e.end = 20 * 60 * 4;
        a.announce(lines("aul.ezhelgi.aitys", 3));
        RandomSource r = e.level.getRandom();
        BlockPos c = e.center;
        Aqyn first = spawn(e, ModEntities.AQYN.get(), surface(e.level, c.getX() - 2, c.getZ() + 2));
        Aqyn second = spawn(e, ModEntities.AQYN.get(), surface(e.level, c.getX() + 3, c.getZ() + 2));
        if (first == null || second == null) {
            finish(e, null);
            return;
        }
        e.aqyn1 = first.getUUID();
        e.aqyn2 = second.getUUID();
        int n1 = r.nextInt(AQYN_NAMES);
        int n2 = (n1 + 1 + r.nextInt(AQYN_NAMES - 1)) % AQYN_NAMES;
        first.setCustomName(Component.translatable("aitys.ezhelgi.name." + n1));
        second.setCustomName(Component.translatable("aitys.ezhelgi.name." + n2));
        for (Aqyn q : new Aqyn[]{first, second}) {
            q.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.DOMBYRA.get()));
            q.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
            q.setPersistenceRequired();
        }
        List<Integer> pool = new ArrayList<>();
        for (int i = 0; i < VERSES; i++) pool.add(i);
        java.util.Collections.shuffle(pool, new java.util.Random(r.nextLong()));
        e.verses.addAll(pool.subList(0, 6));
        e.verse = -1;
    }

    private static void tickAitys(Event e) {
        Entity q1 = e.aqyn1 == null ? null : e.level.getEntity(e.aqyn1);
        Entity q2 = e.aqyn2 == null ? null : e.level.getEntity(e.aqyn2);
        if (!(q1 instanceof Aqyn a1) || !(q2 instanceof Aqyn a2)) {
            if (e.tick > 100) finish(e, null);
            return;
        }
        a1.getLookControl().setLookAt(a2);
        a2.getLookControl().setLookAt(a1);
        a1.getNavigation().stop();
        a2.getNavigation().stop();
        if (!e.voting && e.tick > 160 && e.tick % 140 == 0) {
            e.verse++;
            if (e.verse < e.verses.size()) {
                Aqyn speaker = e.verse % 2 == 0 ? a1 : a2;
                Component name = speaker.getCustomName() == null ? speaker.getDisplayName() : speaker.getCustomName();
                broadcast(e, Component.translatable("aqsaqal.ezhelgi.says", name.copy().withStyle(ChatFormatting.AQUA),
                        Component.translatable("aitys.ezhelgi.verse." + e.verses.get(e.verse)).withStyle(ChatFormatting.ITALIC)));
                speaker.swingForAttack(InteractionHand.MAIN_HAND);
                List<net.minecraftforge.registries.RegistryObject<net.minecraft.sounds.SoundEvent>> kui = ModSounds.KURMANGAZY_KUI;
                e.level.playSound(null, speaker.getX(), speaker.getY(), speaker.getZ(), kui.get(e.level.getRandom().nextInt(kui.size())).get(),
                        SoundSource.NEUTRAL, 0.6f, 1.0f);
                e.level.sendParticles(ParticleTypes.NOTE, speaker.getX(), speaker.getY() + 2.3, speaker.getZ(), 3, 0.3, 0.1, 0.3, 0.5);
            } else {
                e.voting = true;
                Aqsaqal a = aksakal(e);
                if (a != null) {
                    a.announce(lines("aul.ezhelgi.aitys.vote", 2));
                }
                e.end = e.tick + 20 * 60;
            }
        }
    }

    /** Игрок выбирает победителя айтыса. */
    public static void vote(ServerLevel level, Aqyn aqyn, Player player) {
        for (Event e : ACTIVE.values()) {
            if (e.type != Type.AITYS || !(aqyn.getUUID().equals(e.aqyn1) || aqyn.getUUID().equals(e.aqyn2))) continue;
            if (!e.voting) {
                player.sendOverlayMessage(Component.translatable("aul.ezhelgi.aitys.wait"));
                return;
            }
            if (e.voted) return;
            e.voted = true;
            Component name = aqyn.getCustomName() == null ? aqyn.getDisplayName() : aqyn.getCustomName();
            broadcast(e, Component.translatable("aul.ezhelgi.aitys.winner", name).withStyle(ChatFormatting.GOLD));
            // победитель получает подарок от аула — шапан и коня
            aqyn.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(ModItems.SHAPAN.get()));
            aqyn.setDropChance(EquipmentSlot.OFFHAND, 0.0f);
            Horse horse = net.minecraft.world.entity.EntityTypes.HORSE.create(level, EntitySpawnReason.EVENT);
            if (horse != null) {
                horse.snapTo(aqyn.getX() + 1.5, aqyn.getY(), aqyn.getZ());
                horse.setTamed(true);
                horse.setItemSlot(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
                level.addFreshEntity(horse);
            }
            level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, aqyn.getX(), aqyn.getY() + 1, aqyn.getZ(), 30, 0.4, 0.8, 0.4, 0.3);
            level.playSound(null, aqyn.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 1.0f, 1.0f);
            give(player, new ItemStack(ModItems.SAKA_GOLD.get(), 2));
            player.sendSystemMessage(Component.translatable("aul.ezhelgi.aitys.judge").withStyle(ChatFormatting.GRAY));
            e.end = e.tick + 20 * 15;
            return;
        }
    }

    // ------------------------------------------------------------------ көкпар
    private static void startKokpar(Event e, Aqsaqal a) {
        e.end = 20 * 60 * 6;
        e.field = fieldNear(e, 28);
        RandomSource r = e.level.getRandom();
        double ang = r.nextDouble() * Math.PI;
        double dx = Math.cos(ang) * 16, dz = Math.sin(ang) * 16;
        e.goalRed = surface(e.level, e.field.getX() - dx, e.field.getZ() - dz);
        e.goalBlue = surface(e.level, e.field.getX() + dx, e.field.getZ() + dz);
        flag(e, e.goalRed, ModBlocks.TU_KISHI_JUZ.get());
        flag(e, e.goalBlue, ModBlocks.TU_ULY_JUZ.get());
        Serke serke = spawn(e, ModEntities.SERKE.get(), e.field);
        if (serke == null) {
            finish(e, null);
            return;
        }
        e.serke = serke.getUUID();
        for (int i = 0; i < 5; i++) {
            int team = i < 3 ? Shabandoz.RED : Shabandoz.BLUE;
            BlockPos base = team == Shabandoz.RED ? e.goalRed : e.goalBlue;
            Shabandoz s = spawn(e, ModEntities.SHABANDOZ.get(), surface(e.level, base.getX() + r.nextInt(5) - 2, base.getZ() + r.nextInt(5) - 2));
            if (s != null) s.setup(team, e.id);
        }
        a.announce(lines("aul.ezhelgi.kokpar", 4, direction(e.center, e.field)));
    }

    private static Component direction(BlockPos from, BlockPos to) {
        double a = Math.toDegrees(Math.atan2(to.getX() - from.getX(), -(to.getZ() - from.getZ())));
        int oct = (int) Math.floorMod(Math.round(a / 45.0), 8);
        String[] names = {"north", "northeast", "east", "southeast", "south", "southwest", "west", "northwest"};
        return Component.translatable("direction.ezhelgi." + names[oct]);
    }

    /** ПКМ по серке: схватить или вырвать у соперника. */
    public static void grabSerke(ServerLevel level, Serke serke, Player player) {
        for (Event e : ACTIVE.values()) {
            if (e.type != Type.KOKPAR || !serke.getUUID().equals(e.serke)) continue;
            if (!(player.getVehicle() instanceof net.minecraft.world.entity.animal.equine.AbstractHorse)) {
                player.sendOverlayMessage(Component.translatable("aul.ezhelgi.kokpar.need_horse"));
                return;
            }
            if (player.getUUID().equals(e.holder)) return;
            Entity holder = e.holder == null ? null : level.getEntity(e.holder);
            if (holder != null && !(holder instanceof Shabandoz sh && sh.team() == Shabandoz.BLUE)
                    && level.getRandom().nextFloat() > 0.6f) {
                player.sendOverlayMessage(Component.translatable("aul.ezhelgi.kokpar.miss"));
                return;
            }
            e.holder = player.getUUID();
            e.players.add(player.getUUID());
            player.sendOverlayMessage(Component.translatable("aul.ezhelgi.kokpar.grab"));
            level.playSound(null, serke.blockPosition(), SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(0).value(), SoundSource.PLAYERS, 0.4f, 1.2f);
            return;
        }
    }

    private static int teamOf(Event e, Entity entity) {
        if (entity instanceof Shabandoz s) return s.team();
        return Shabandoz.BLUE; // игрок играет за синих
    }

    private static void tickKokpar(Event e) {
        if (!(e.serke != null && e.level.getEntity(e.serke) instanceof Serke serke)) {
            finish(e, null);
            return;
        }
        Entity holder = e.holder == null ? null : e.level.getEntity(e.holder);
        if (holder != null && (!holder.isAlive() || holder.distanceToSqr(Vec3.atCenterOf(e.field)) > 70 * 70)) {
            holder = null;
            e.holder = null;
        }
        if (holder != null) {
            Vec3 side = holder.getLookAngle().yRot((float) Math.PI / 2).multiply(1, 0, 1).normalize().scale(0.9);
            serke.snapTo(holder.getX() + side.x, holder.getY() + 0.7, holder.getZ() + side.z, holder.getYRot(), 0f);
            serke.setDeltaMovement(Vec3.ZERO);
            serke.setNoGravity(true);
            BlockPos goal = teamOf(e, holder) == Shabandoz.RED ? e.goalRed : e.goalBlue;
            if (holder.distanceToSqr(Vec3.atCenterOf(goal)) < 4.0 * 4.0) {
                score(e, serke, teamOf(e, holder));
                return;
            }
        } else {
            serke.setNoGravity(false);
        }
        if (e.tick % 5 != 0) return;
        RandomSource r = e.level.getRandom();
        for (UUID id : e.spawned) {
            if (!(e.level.getEntity(id) instanceof Shabandoz s) || !s.isPassenger()) continue;
            int team = s.team();
            if (s == holder) {
                BlockPos goal = team == Shabandoz.RED ? e.goalRed : e.goalBlue;
                s.getNavigation().moveTo(goal.getX() + 0.5, goal.getY(), goal.getZ() + 0.5, 1.5);
                continue;
            }
            if (holder != null && teamOf(e, holder) == team) {
                // прикрывает своего — скачет рядом с ним
                s.getNavigation().moveTo(holder.getX() + (r.nextDouble() - 0.5) * 6, holder.getY(), holder.getZ() + (r.nextDouble() - 0.5) * 6, 1.4);
                continue;
            }
            s.getNavigation().moveTo(serke.getX(), serke.getY(), serke.getZ(), 1.7);
            if (s.distanceToSqr(serke) < 2.6 * 2.6) {
                float chance = holder == null ? 1.0f : (holder instanceof Player ? 0.18f : 0.25f);
                if (r.nextFloat() < chance) {
                    e.holder = s.getUUID();
                    holder = s;
                    s.swingForAttack(InteractionHand.MAIN_HAND);
                    e.level.playSound(null, s.blockPosition(), SoundEvents.HORSE_ANGRY, SoundSource.NEUTRAL, 0.8f, 1.0f);
                }
            }
        }
    }

    private static void score(Event e, Serke serke, int team) {
        if (team == Shabandoz.RED) e.scoreRed++; else e.scoreBlue++;
        broadcast(e, Component.translatable(team == Shabandoz.RED ? "aul.ezhelgi.kokpar.goal_red" : "aul.ezhelgi.kokpar.goal_blue",
                e.scoreRed, e.scoreBlue).withStyle(team == Shabandoz.RED ? ChatFormatting.RED : ChatFormatting.BLUE));
        e.level.playSound(null, serke.blockPosition(), SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(2).value(), SoundSource.NEUTRAL, 1.5f, 1.0f);
        e.level.sendParticles(ParticleTypes.FIREWORK, serke.getX(), serke.getY() + 1, serke.getZ(), 40, 0.5, 0.8, 0.5, 0.15);
        e.holder = null;
        serke.setNoGravity(false);
        serke.snapTo(e.field.getX() + 0.5, e.field.getY() + 1, e.field.getZ() + 0.5);
        if (e.scoreRed >= 3 || e.scoreBlue >= 3) {
            finish(e, e.scoreBlue > e.scoreRed ? "blue" : "red");
        }
    }

    // ------------------------------------------------------------------ бәйге
    private static void startBaige(Event e, Aqsaqal a) {
        e.end = 20 * 60 * 6;
        RandomSource r = e.level.getRandom();
        double start = r.nextDouble() * Math.PI * 2;
        for (int k = 0; k < 4; k++) {
            double ang = start + k * Math.PI / 2;
            e.waypoints.add(surface(e.level, e.center.getX() + Math.cos(ang) * 36, e.center.getZ() + Math.sin(ang) * 36));
        }
        for (BlockPos w : e.waypoints) {
            flag(e, w.offset(2, 0, 0), ModBlocks.TU_HANDYQ.get());
        }
        BlockPos s0 = e.waypoints.get(0);
        for (int i = 0; i < 3; i++) {
            Shabandoz s = spawn(e, ModEntities.SHABANDOZ.get(), surface(e.level, s0.getX() - 2 + i * 2, s0.getZ() + 2));
            if (s != null) {
                s.setup(Shabandoz.JOCKEY, e.id);
                s.waypoint = 1;
            }
        }
        a.announce(lines("aul.ezhelgi.baige", 3, direction(e.center, s0)));
    }

    private static void tickBaige(Event e) {
        BlockPos s0 = e.waypoints.get(0);
        if (!e.started) {
            if (e.tick == 20 * 25) {
                broadcast(e, Component.translatable("aul.ezhelgi.baige.ready").withStyle(ChatFormatting.YELLOW));
            }
            if (e.tick >= 20 * 30) {
                e.started = true;
                for (Player p : e.level.players()) {
                    if (p.distanceToSqr(Vec3.atCenterOf(s0)) < 12 * 12 && p.getVehicle() instanceof net.minecraft.world.entity.animal.equine.AbstractHorse) {
                        e.progress.put(p.getUUID(), 1);
                        e.players.add(p.getUUID());
                    }
                }
                broadcast(e, Component.translatable("aul.ezhelgi.baige.go").withStyle(ChatFormatting.GOLD));
                e.level.playSound(null, s0, SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(1).value(), SoundSource.NEUTRAL, 2.0f, 1.0f);
            }
            return;
        }
        // игроки проходят вехи по порядку
        for (UUID id : new ArrayList<>(e.progress.keySet())) {
            Player p = e.level.getPlayerByUUID(id);
            if (p == null) continue;
            int next = e.progress.get(id);
            BlockPos w = e.waypoints.get(next % 4);
            if (p.distanceToSqr(Vec3.atCenterOf(w)) < 7 * 7) {
                if (next == 4) {
                    finishBaige(e, p);
                    return;
                }
                e.progress.put(id, next + 1);
                p.sendOverlayMessage(Component.translatable("aul.ezhelgi.baige.mark", next, 4));
            }
        }
        if (e.tick % 5 != 0) return;
        for (UUID id : e.spawned) {
            if (!(e.level.getEntity(id) instanceof Shabandoz s) || !s.isPassenger()) continue;
            BlockPos w = e.waypoints.get(s.waypoint % 4);
            double speed = 1.45 + (id.hashCode() & 7) * 0.04;
            s.getNavigation().moveTo(w.getX() + 0.5, w.getY(), w.getZ() + 0.5, speed);
            if (s.distanceToSqr(Vec3.atCenterOf(w)) < 5 * 5) {
                if (s.waypoint == 4) {
                    finishBaige(e, s);
                    return;
                }
                s.waypoint++;
            }
        }
    }

    private static void finishBaige(Event e, Entity winner) {
        if (winner instanceof Player p) {
            broadcast(e, Component.translatable("aul.ezhelgi.baige.win_player", p.getDisplayName()).withStyle(ChatFormatting.GOLD));
            // приз — породистая лошадь Пржевальского под седлом
            var prize = ModEntities.PRZEWALSKI.get().create(e.level, EntitySpawnReason.EVENT);
            if (prize != null) {
                prize.snapTo(p.getX() + 1.5, p.getY(), p.getZ());
                prize.finalizeSpawn(e.level, e.level.getCurrentDifficultyAt(p.blockPosition()), EntitySpawnReason.EVENT, null);
                prize.setTamed(true);
                prize.setOwner(p);
                prize.setItemSlot(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
                prize.setPersistenceRequired();
                prize.setCustomName(Component.translatable("aul.ezhelgi.baige.prize_name"));
                e.level.addFreshEntity(prize);
            }
            give(p, new ItemStack(ModItems.SAKA_GOLD.get(), 5));
            e.level.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, p.getX(), p.getY() + 1, p.getZ(), 50, 0.6, 1, 0.6, 0.4);
            finish(e, null);
        } else {
            broadcast(e, Component.translatable("aul.ezhelgi.baige.win_jockey").withStyle(ChatFormatting.YELLOW));
            for (UUID id : e.players) {
                Player p = e.level.getPlayerByUUID(id);
                if (p != null) give(p, new ItemStack(ModItems.SAKA_GOLD.get(), 2));
            }
            finish(e, null);
        }
    }

    // ------------------------------------------------------------------ общий цикл
    public static void tick(ServerLevel level) {
        if (ACTIVE.isEmpty()) return;
        for (Iterator<Event> it = new ArrayList<>(ACTIVE.values()).iterator(); it.hasNext(); ) {
            Event e = it.next();
            if (e.level != level || !ACTIVE.containsKey(e.id)) continue;
            e.tick++;
            if (e.tick > e.end) {
                if (e.type == Type.KOKPAR) {
                    finish(e, e.scoreBlue > e.scoreRed ? "blue" : (e.scoreRed > e.scoreBlue ? "red" : "draw"));
                } else if (e.type == Type.RAID || e.type == Type.WOLVES) {
                    finish(e, "lost");
                } else {
                    finish(e, null);
                }
                continue;
            }
            switch (e.type) {
                case TOI -> tickToi(e);
                case AITYS -> tickAitys(e);
                case KOKPAR -> tickKokpar(e);
                case BAIGE -> tickBaige(e);
                case RAID -> tickRaid(e);
                case WOLVES -> tickWolves(e);
            }
        }
    }

    private static void finish(Event e, @Nullable String result) {
        if (!ACTIVE.containsKey(e.id)) return;
        ACTIVE.remove(e.id);
        if (e.type == Type.KOKPAR && result != null) {
            String key = switch (result) {
                case "blue" -> "aul.ezhelgi.kokpar.win_blue";
                case "red" -> "aul.ezhelgi.kokpar.win_red";
                default -> "aul.ezhelgi.kokpar.draw";
            };
            broadcast(e, Component.translatable(key, e.scoreRed, e.scoreBlue).withStyle(ChatFormatting.GOLD));
            if ("blue".equals(result)) {
                for (UUID id : e.players) {
                    Player p = e.level.getPlayerByUUID(id);
                    if (p != null) {
                        give(p, new ItemStack(ModItems.SAKA_GOLD.get(), 6));
                        give(p, new ItemStack(ModItems.KUMYS.get(), 2));
                        if (e.level.getRandom().nextInt(3) == 0) give(p, new ItemStack(ModItems.QALQAN.get()));
                    }
                }
            }
        } else if (e.type == Type.TOI) {
            broadcast(e, Component.translatable("aul.ezhelgi.toi.end").withStyle(ChatFormatting.GRAY));
        } else if (e.type == Type.RAID || e.type == Type.WOLVES) {
            String k = e.type == Type.RAID ? "raid" : "wolves";
            broadcast(e, Component.translatable("aul.ezhelgi." + k + ("won".equals(result) ? ".won" : ".lost"))
                    .withStyle("won".equals(result) ? ChatFormatting.GOLD : ChatFormatting.GRAY));
            if ("won".equals(result)) {
                rewardDefenders(e);
            }
        }
        // гости и всадники разъезжаются, флаги убираются
        for (UUID id : e.spawned) {
            Entity ent = e.level.getEntity(id);
            if (ent instanceof Shabandoz s) {
                s.vanish(e.level);
            } else if (ent instanceof Mob m && !(ent instanceof Aqyn) && (!m.isDeadOrDying())) {
                e.level.sendParticles(ParticleTypes.CLOUD, m.getX(), m.getY() + 1, m.getZ(), 8, 0.3, 0.5, 0.3, 0.02);
                if ((e.type == Type.RAID || e.type == Type.WOLVES) && m.getVehicle() instanceof Mob mount) {
                    mount.discard(); // всадники уезжают вместе с конями
                }
                m.discard();
            }
        }
        for (BlockPos p : e.placed) {
            BlockState st = e.level.getBlockState(p);
            if (st.getBlock() instanceof TuBlock) {
                e.level.setBlock(p.above(), net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
                e.level.setBlock(p, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    // ------------------------------------------------------------------ набег джунгар
    private static void startRaid(Event e, Aqsaqal a) {
        e.end = 20 * 60 * 7;
        e.wave = 0;
        e.waveAt = 20 * 12;
        a.announce(lines("aul.ezhelgi.raid", 3));
        e.level.playSound(null, e.center, SoundEvents.RAID_HORN.value(), SoundSource.HOSTILE, 4.0f, 0.9f);
        // на защиту аула выходят батыры
        RandomSource r = e.level.getRandom();
        for (int i = 0; i < 3; i++) {
            double ang = r.nextDouble() * Math.PI * 2;
            kz.ezhelgi.entity.AltynJauynger d = spawn(e, ModEntities.ALTYN_JAUYNGER.get(),
                    surface(e.level, e.center.getX() + Math.cos(ang) * 6, e.center.getZ() + Math.sin(ang) * 6));
            if (d != null) {
                d.makeDefender();
                d.setPersistenceRequired();
            }
        }
    }

    private static void raidWave(Event e, int count) {
        RandomSource r = e.level.getRandom();
        double side = r.nextDouble() * Math.PI * 2;
        for (int i = 0; i < count; i++) {
            double ang = side + (r.nextDouble() - 0.5) * 0.8;
            double dist = 34 + r.nextDouble() * 8;
            kz.ezhelgi.entity.Jungar j = spawn(e, ModEntities.JUNGAR.get(),
                    surface(e.level, e.center.getX() + Math.cos(ang) * dist, e.center.getZ() + Math.sin(ang) * dist));
            if (j != null) {
                j.setPersistenceRequired();
                e.raiders.add(j.getUUID());
                Player near = e.level.getNearestPlayer(j, 80.0);
                if (near != null) j.setTarget(near);
            }
        }
        e.level.playSound(null, e.center, SoundEvents.RAID_HORN.value(), SoundSource.HOSTILE, 4.0f, 1.1f);
    }

    private static boolean raidersAlive(Event e) {
        for (UUID id : e.raiders) {
            Entity ent = e.level.getEntity(id);
            if (ent instanceof LivingEntity l && l.isAlive()) return true;
        }
        return false;
    }

    private static void tickRaid(Event e) {
        if (e.tick % 20 == 0) {
            for (Player p : e.level.players()) {
                if (p.distanceToSqr(Vec3.atCenterOf(e.center)) < 64 * 64) e.players.add(p.getUUID());
            }
        }
        if (e.wave < 2 && e.tick >= e.waveAt && !raidersAlive(e)) {
            e.wave++;
            broadcast(e, Component.translatable("aul.ezhelgi.raid.wave" + e.wave).withStyle(ChatFormatting.RED));
            raidWave(e, e.wave == 1 ? 4 : 6);
            e.waveAt = e.tick + 20 * 10;
            return;
        }
        if (e.wave == 2 && e.tick >= e.waveAt && !raidersAlive(e)) {
            finish(e, "won");
            return;
        }
        // джунгары идут на аул, если потеряли цель
        if (e.tick % 60 == 0) {
            for (UUID id : e.raiders) {
                if (e.level.getEntity(id) instanceof Mob m && m.isAlive() && m.getTarget() == null) {
                    m.getNavigation().moveTo(e.center.getX() + 0.5, e.center.getY(), e.center.getZ() + 0.5, 1.1);
                }
            }
        }
    }

    // ------------------------------------------------------------------ волчья стая
    private static void startWolves(Event e, Aqsaqal a) {
        e.end = 20 * 60 * 4;
        a.announce(lines("aul.ezhelgi.wolves", 2));
        RandomSource r = e.level.getRandom();
        double side = r.nextDouble() * Math.PI * 2;
        int n = 3 + r.nextInt(3);
        for (int i = 0; i < n; i++) {
            double ang = side + (r.nextDouble() - 0.5) * 0.6;
            net.minecraft.world.entity.animal.wolf.Wolf w = spawn(e, net.minecraft.world.entity.EntityTypes.WOLF,
                    surface(e.level, e.center.getX() + Math.cos(ang) * 30, e.center.getZ() + Math.sin(ang) * 30));
            if (w != null) {
                w.setPersistenceRequired();
                e.raiders.add(w.getUUID());
                var speed = w.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
                if (speed != null) speed.setBaseValue(0.36);
                var dmg = w.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);
                if (dmg != null) dmg.setBaseValue(5.0);
            }
        }
        e.level.playSound(null, e.center, SoundEvents.WOLF_SOUNDS.values().iterator().next().adultSounds().growlSound().value(), SoundSource.HOSTILE, 3.0f, 0.9f);
    }

    private static void tickWolves(Event e) {
        if (e.tick % 20 == 0) {
            for (Player p : e.level.players()) {
                if (p.distanceToSqr(Vec3.atCenterOf(e.center)) < 64 * 64) e.players.add(p.getUUID());
            }
        }
        if (e.tick > 100 && !raidersAlive(e)) {
            finish(e, "won");
            return;
        }
        if (e.level.isBrightOutside() && e.tick > 200) {
            finish(e, "lost"); // рассвет — стая уходит
            return;
        }
        if (e.tick % 40 != 0) return;
        // голодные волки ищут добычу в загонах, а если её нет — нападают на людей
        for (UUID id : e.raiders) {
            if (!(e.level.getEntity(id) instanceof net.minecraft.world.entity.animal.wolf.Wolf w) || !w.isAlive()) continue;
            if (w.getTarget() != null && w.getTarget().isAlive()) continue;
            LivingEntity prey = null;
            double best = Double.MAX_VALUE;
            for (net.minecraft.world.entity.animal.Animal an : e.level.getEntitiesOfClass(net.minecraft.world.entity.animal.Animal.class,
                    new net.minecraft.world.phys.AABB(e.center).inflate(40, 12, 40),
                    an -> !(an instanceof net.minecraft.world.entity.animal.wolf.Wolf) && an.isAlive())) {
                double d = an.distanceToSqr(w);
                if (d < best) {
                    best = d;
                    prey = an;
                }
            }
            if (prey == null) prey = e.level.getNearestPlayer(w, 32.0);
            if (prey != null) {
                w.setTarget(prey);
            } else {
                w.getNavigation().moveTo(e.center.getX() + 0.5, e.center.getY(), e.center.getZ() + 0.5, 1.2);
            }
        }
    }

    /** Награда тем, кто защищал аул: уважение, золото, а за набег — доспехи джунгар. */
    private static void rewardDefenders(Event e) {
        RandomSource r = e.level.getRandom();
        Aqsaqal a = aksakal(e);
        for (UUID id : e.players) {
            Player p = e.level.getPlayerByUUID(id);
            if (p == null) continue;
            var data = kz.ezhelgi.quest.PlayerData.get((ServerPlayer) p);
            int respect = data.getIntOr("aul_respect", 0) + (e.type == Type.RAID ? 3 : 1);
            data.putInt("aul_respect", respect);
            if (e.type == Type.RAID) {
                ItemStack[] armor = {new ItemStack(ModItems.JUNGAR_DULYGA.get()), new ItemStack(ModItems.JUNGAR_SAUYT.get()),
                        new ItemStack(ModItems.JUNGAR_SHALBAR.get()), new ItemStack(ModItems.JUNGAR_ETIK.get()),
                        new ItemStack(ModItems.KOBE_SAUYT.get()), new ItemStack(ModItems.DULYGA.get())};
                give(p, armor[r.nextInt(armor.length)]);
                give(p, new ItemStack(ModItems.SAKA_GOLD.get(), 6));
                p.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.HERO_OF_THE_VILLAGE, 20 * 60 * 20, 0));
            } else {
                give(p, new ItemStack(ModItems.SAKA_GOLD.get(), 2));
                give(p, new ItemStack(ModItems.KAZY.get(), 2));
            }
            p.sendSystemMessage(Component.translatable("aul.ezhelgi.respect", respect).withStyle(ChatFormatting.AQUA));
        }
        if (a != null) {
            a.announce(List.of(Component.translatable(e.type == Type.RAID ? "aul.ezhelgi.raid.thanks" : "aul.ezhelgi.wolves.thanks")));
        }
    }

    /** Когда игрок бьёт всадника или серке — ничего не ломаем. */
    public static boolean protects(LivingEntity entity) {
        return entity instanceof Shabandoz || entity instanceof Serke;
    }
}
