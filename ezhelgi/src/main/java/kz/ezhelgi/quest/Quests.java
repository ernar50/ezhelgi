package kz.ezhelgi.quest;

import kz.ezhelgi.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Задания аксакала. Аксакал сначала рассказывает новость или даёт совет, потом просит о помощи.
 * Задание хранится в данных игрока, его можно сдать любому аксакалу.
 */
public final class Quests {
    private Quests() {}

    public enum Type { BRING, KILL, HORSE, GLYPHS, TOGYZ }

    public record Quest(String id, Type type, String target, int count, int tier) {}

    public static final List<Quest> ALL = List.of(
            new Quest("otau_kiiz", Type.BRING, "minecraft:white_wool", 16, 1),
            new Quest("toi_baursak", Type.BRING, "ezhelgi:baursak", 12, 1),
            new Quest("qymyz", Type.BRING, "ezhelgi:kumys", 4, 1),
            new Quest("beshbarmak", Type.BRING, "minecraft:mutton", 10, 1),
            new Quest("otyn", Type.BRING, "#minecraft:logs", 32, 1),
            new Quest("shop", Type.BRING, "minecraft:hay_block", 8, 1),
            new Quest("teri", Type.BRING, "minecraft:leather", 10, 1),
            new Quest("bal", Type.BRING, "minecraft:honey_bottle", 3, 1),
            new Quest("kitap", Type.BRING, "minecraft:book", 5, 1),
            new Quest("qauyrsyn", Type.BRING, "minecraft:feather", 16, 1),
            new Quest("an_eti", Type.BRING, "ezhelgi:an_eti", 6, 1),
            new Quest("temir", Type.BRING, "minecraft:iron_ingot", 12, 2),
            new Quest("tenge", Type.BRING, "ezhelgi:otyrar_tengesi", 5, 2),
            new Quest("qumyra", Type.BRING, "ezhelgi:qumyra", 2, 2),
            new Quest("muiiz", Type.BRING, "ezhelgi:arqar_muiizi", 2, 2),
            new Quest("tymaq", Type.BRING, "ezhelgi:tulki_terisi", 2, 2),
            new Quest("kieli_qyl", Type.BRING, "ezhelgi:kieli_qyl", 1, 2),
            new Quest("saukele", Type.BRING, "minecraft:gold_ingot", 6, 2),
            new Quest("qasqyr", Type.KILL, "minecraft:wolf", 3, 1),
            new Quest("tun", Type.KILL, "#monster", 15, 1),
            new Quest("orymshek", Type.KILL, "minecraft:spider", 6, 1),
            new Quest("jungar", Type.KILL, "ezhelgi:jungar", 5, 2),
            new Quest("qaraqshy", Type.KILL, "ezhelgi:qaraqshy", 4, 2),
            new Quest("albasty", Type.KILL, "ezhelgi:albasty", 1, 2),
            new Quest("zheztyrnaq", Type.KILL, "ezhelgi:zheztyrnak", 2, 2),
            new Quest("mystan", Type.KILL, "ezhelgi:mystan_kempir", 1, 2),
            new Quest("baqsy", Type.KILL, "ezhelgi:baqsy_kolenkesi", 3, 2),
            new Quest("jalmauyz", Type.KILL, "ezhelgi:zhalmauyz_kempir", 1, 3),
            new Quest("dau", Type.KILL, "ezhelgi:dau", 1, 3),
            new Quest("aidahar", Type.KILL, "ezhelgi:aidahar", 1, 4),
            new Quest("jogalgan_at", Type.HORSE, "", 1, 2),
            new Quest("tanba", Type.GLYPHS, "", 3, 2),
            new Quest("togyz", Type.TOGYZ, "", 1, 2));

    public static final int WISDOM = 30;
    public static final int GREETINGS = 6;

    private static final String QUEST = "q_id";
    private static final String PROGRESS = "q_prog";
    private static final String HORSE = "q_horse";
    private static final String GLYPH_BASE = "q_glyphs";
    private static final String DONE = "q_done";
    private static final String LAST = "q_last";

    public static @Nullable Quest byId(String id) {
        for (Quest q : ALL) if (q.id().equals(id)) return q;
        return null;
    }

    public static @Nullable Quest current(Player player) {
        String id = PlayerData.get(player).getStringOr(QUEST, "");
        return id.isEmpty() ? null : byId(id);
    }

    public static int progress(Player player) {
        return PlayerData.get(player).getIntOr(PROGRESS, 0);
    }

    // ------------------------------------------------------------------ выдача
    /** Выбирает новое задание (не повторяя прошлое) и возвращает реплики аксакала. */
    public static List<Component> start(ServerPlayer player, LivingEntity aksakal) {
        RandomSource random = player.getRandom();
        CompoundTag data = PlayerData.get(player);
        int done = data.getIntOr(DONE, 0);
        String last = data.getStringOr(LAST, "");
        List<Quest> options = new ArrayList<>();
        for (Quest q : ALL) {
            if (q.id().equals(last)) continue;
            // сильные задания — когда уже помог аулу
            if (q.tier() >= 3 && done < 4) continue;
            if (q.tier() >= 4 && done < 8) continue;
            options.add(q);
        }
        Quest q = options.get(random.nextInt(options.size()));
        data.putString(QUEST, q.id());
        data.putInt(PROGRESS, 0);
        data.remove(HORSE);
        if (q.type() == Type.GLYPHS) {
            data.putInt(GLYPH_BASE, Integer.bitCount(data.getIntOr("tanba", 0)));
        }
        List<Component> lines = new ArrayList<>();
        lines.add(Component.translatable("aqsaqal.ezhelgi.greet." + random.nextInt(GREETINGS)));
        lines.add(Component.translatable("aqsaqal.ezhelgi.wisdom." + random.nextInt(WISDOM)).withStyle(ChatFormatting.ITALIC));
        lines.add(Component.translatable("quest.ezhelgi." + q.id() + ".story"));
        if (q.type() == Type.HORSE) {
            Direction8 dir = spawnLostHorse(player, aksakal);
            lines.add(Component.translatable("quest.ezhelgi.jogalgan_at.task",
                    Component.translatable("direction.ezhelgi." + (dir == null ? "far" : dir.name().toLowerCase()))));
        } else {
            lines.add(Component.translatable("quest.ezhelgi." + q.id() + ".task", q.count()));
        }
        lines.add(taskLine(player, q).copy().withStyle(ChatFormatting.YELLOW));
        return lines;
    }

    /** Строка «Задание: …» с прогрессом. */
    public static Component taskLine(Player player, Quest q) {
        int p = progress(player);
        return switch (q.type()) {
            case BRING -> Component.translatable("quest.ezhelgi.line.bring", itemName(q), q.count());
            case KILL -> Component.translatable("quest.ezhelgi.line.kill", targetName(q), p, q.count());
            case HORSE -> Component.translatable("quest.ezhelgi.line.horse");
            case GLYPHS -> Component.translatable("quest.ezhelgi.line.glyphs", glyphProgress(player), q.count());
            case TOGYZ -> Component.translatable("quest.ezhelgi.line.togyz");
        };
    }

    private static Component itemName(Quest q) {
        if (q.target().startsWith("#")) {
            return Component.translatable("quest.ezhelgi.any_logs");
        }
        Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(q.target()));
        return new ItemStack(item).getHoverName();
    }

    private static Component targetName(Quest q) {
        if (q.target().equals("#monster")) {
            return Component.translatable("quest.ezhelgi.any_monster");
        }
        return BuiltInRegistries.ENTITY_TYPE.getValue(Identifier.parse(q.target())).getDescription();
    }

    private static int glyphProgress(Player player) {
        CompoundTag data = PlayerData.get(player);
        return Math.max(0, Integer.bitCount(data.getIntOr("tanba", 0)) - data.getIntOr(GLYPH_BASE, 0));
    }

    // ------------------------------------------------------------------ сдача
    /** Проверяет задание. Возвращает реплики (пусто — задание ещё не выполнено). */
    public static List<Component> tryComplete(ServerPlayer player, LivingEntity aksakal) {
        Quest q = current(player);
        List<Component> lines = new ArrayList<>();
        if (q == null) return lines;
        boolean ok = switch (q.type()) {
            case BRING -> takeItems(player, q);
            case KILL, TOGYZ -> progress(player) >= q.count();
            case GLYPHS -> glyphProgress(player) >= q.count();
            case HORSE -> horseReturned(player, aksakal);
        };
        if (!ok) return lines;
        CompoundTag data = PlayerData.get(player);
        data.remove(QUEST);
        data.remove(PROGRESS);
        data.putString(LAST, q.id());
        int done = data.getIntOr(DONE, 0) + 1;
        data.putInt(DONE, done);
        lines.add(Component.translatable("quest.ezhelgi." + q.id() + ".thanks"));
        lines.add(Component.translatable("aqsaqal.ezhelgi.bata." + player.getRandom().nextInt(5)).withStyle(ChatFormatting.ITALIC));
        reward(player, q.tier(), done);
        if (done % 10 == 0) {
            lines.add(Component.translatable("aqsaqal.ezhelgi.batyr", done).withStyle(ChatFormatting.GOLD));
        }
        return lines;
    }

    public static void abandon(Player player) {
        CompoundTag data = PlayerData.get(player);
        data.remove(QUEST);
        data.remove(PROGRESS);
        data.remove(HORSE);
    }

    private static boolean matches(ItemStack stack, Quest q) {
        if (q.target().startsWith("#")) {
            TagKey<Item> tag = TagKey.create(Registries.ITEM, Identifier.parse(q.target().substring(1)));
            return stack.is(tag);
        }
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString().equals(q.target());
    }

    private static boolean takeItems(Player player, Quest q) {
        Inventory inv = player.getInventory();
        int have = 0;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (!s.isEmpty() && matches(s, q)) have += s.getCount();
        }
        if (have < q.count()) return false;
        int left = q.count();
        for (int i = 0; i < inv.getContainerSize() && left > 0; i++) {
            ItemStack s = inv.getItem(i);
            if (!s.isEmpty() && matches(s, q)) {
                int n = Math.min(left, s.getCount());
                s.shrink(n);
                left -= n;
            }
        }
        inv.setChanged();
        return true;
    }

    // ------------------------------------------------------------------ потерянный конь
    private enum Direction8 { NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST }

    private static @Nullable Direction8 spawnLostHorse(ServerPlayer player, LivingEntity aksakal) {
        ServerLevel level = player.level();
        RandomSource random = player.getRandom();
        int octant = random.nextInt(8);
        double angle = octant * Math.PI / 4.0;
        double dist = 70 + random.nextInt(50);
        int x = (int) (aksakal.getX() + Math.sin(angle) * dist);
        int z = (int) (aksakal.getZ() - Math.cos(angle) * dist);
        level.getChunk(x >> 4, z >> 4);
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        Horse horse = net.minecraft.world.entity.EntityTypes.HORSE.create(level, EntitySpawnReason.EVENT);
        if (horse == null) return null;
        horse.snapTo(x + 0.5, y, z + 0.5, random.nextFloat() * 360f, 0f);
        horse.setCustomName(Component.translatable("quest.ezhelgi.horse_name"));
        horse.setPersistenceRequired();
        horse.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20 * 60 * 20, 0, false, false));
        level.addFreshEntity(horse);
        PlayerData.get(player).putString(HORSE, horse.getUUID().toString());
        return Direction8.values()[octant];
    }

    private static boolean horseReturned(ServerPlayer player, LivingEntity aksakal) {
        String id = PlayerData.get(player).getStringOr(HORSE, "");
        if (id.isEmpty()) return false;
        Entity horse = player.level().getEntity(UUID.fromString(id));
        if (horse == null || !horse.isAlive() || horse.distanceToSqr(aksakal) > 12 * 12) return false;
        if (horse instanceof Horse h) {
            h.removeEffect(MobEffects.GLOWING);
            h.setTamed(true);
            h.setOwner(player);
        }
        return true;
    }

    // ------------------------------------------------------------------ события
    public static void onKill(ServerPlayer player, LivingEntity victim) {
        Quest q = current(player);
        if (q == null || q.type() != Type.KILL) return;
        boolean match = q.target().equals("#monster") ? victim instanceof Enemy
                : BuiltInRegistries.ENTITY_TYPE.getKey(victim.getType()).toString().equals(q.target());
        if (!match) return;
        CompoundTag data = PlayerData.get(player);
        int p = data.getIntOr(PROGRESS, 0) + 1;
        data.putInt(PROGRESS, p);
        player.sendOverlayMessage(Component.translatable(p >= q.count() ? "quest.ezhelgi.ready" : "quest.ezhelgi.progress",
                targetName(q), Math.min(p, q.count()), q.count()).withStyle(ChatFormatting.YELLOW));
    }

    public static void onTogyzWin(ServerPlayer player) {
        Quest q = current(player);
        if (q == null || q.type() != Type.TOGYZ) return;
        PlayerData.get(player).putInt(PROGRESS, 1);
        player.sendSystemMessage(Component.translatable("quest.ezhelgi.togyz_won").withStyle(ChatFormatting.YELLOW));
    }

    // ------------------------------------------------------------------ награды
    private static void reward(ServerPlayer player, int tier, int done) {
        RandomSource r = player.getRandom();
        int gold = switch (tier) { case 1 -> 3 + r.nextInt(3); case 2 -> 6 + r.nextInt(5); case 3 -> 12 + r.nextInt(7); default -> 25; };
        give(player, new ItemStack(ModItems.SAKA_GOLD.get(), gold));
        ServerLevel level = player.level();
        level.addFreshEntity(new ExperienceOrb(level, player.getX(), player.getY() + 0.5, player.getZ(), 10 * tier * tier + 5));
        List<Supplier<ItemStack>> small = List.of(
                () -> new ItemStack(ModItems.KUMYS.get(), 2), () -> new ItemStack(ModItems.BAURSAK.get(), 8),
                () -> new ItemStack(ModItems.KURT.get(), 8), () -> new ItemStack(ModItems.KAZY.get(), 3),
                () -> new ItemStack(Items.EMERALD, 3), () -> new ItemStack(ModItems.SYRMAK.get(), 2));
        List<Supplier<ItemStack>> batyr = List.of(
                () -> new ItemStack(ModItems.DULYGA.get()), () -> new ItemStack(ModItems.KIREUKE.get()),
                () -> new ItemStack(ModItems.KOBE_SAUYT.get()), () -> new ItemStack(ModItems.BATYR_SHALBAR.get()),
                () -> new ItemStack(ModItems.BATYR_ETIK.get()), () -> new ItemStack(ModItems.QALQAN.get()),
                () -> new ItemStack(ModItems.OIU_BANNER_PATTERN.get()), () -> new ItemStack(ModItems.MUSIC_DISC_ADAI.get()),
                () -> new ItemStack(ModItems.TU_QAZAQSTAN.get()), () -> new ItemStack(ModItems.TOGYZ_QUMALAQ.get()));
        List<Supplier<ItemStack>> heavy = List.of(
                () -> new ItemStack(ModItems.BEREN_SAUYT.get()), () -> new ItemStack(ModItems.SHARAINA.get()),
                () -> new ItemStack(ModItems.MUIIZ_SADAQ.get()));
        switch (tier) {
            case 1 -> {
                give(player, small.get(r.nextInt(small.size())).get());
                if (r.nextFloat() < 0.25f) give(player, randomTemplate(r));
                if (r.nextFloat() < 0.10f) give(player, batyr.get(r.nextInt(batyr.size())).get());
            }
            case 2 -> {
                give(player, r.nextBoolean() ? batyr.get(r.nextInt(batyr.size())).get() : randomTemplate(r));
                if (r.nextFloat() < 0.04f) give(player, randomEpic(r));
            }
            case 3 -> {
                give(player, heavy.get(r.nextInt(heavy.size())).get());
                give(player, randomTemplate(r));
                if (r.nextFloat() < 0.25f) give(player, randomEpic(r));
            }
            default -> {
                give(player, randomEpic(r));
                give(player, randomEpic(r));
                give(player, randomTemplate(r));
            }
        }
        if (done % 10 == 0) {
            give(player, randomEpic(r));
        }
    }

    public static ItemStack randomTemplate(RandomSource r) {
        Item[] t = {ModItems.QOSHQAR_MUIZ_TEMPLATE.get(), ModItems.TUIE_TABAN_TEMPLATE.get(), ModItems.QUS_QANATY_TEMPLATE.get(),
                ModItems.SYNAR_MUIZ_TEMPLATE.get(), ModItems.TUMARSHA_TEMPLATE.get()};
        return new ItemStack(t[r.nextInt(t.length)]);
    }

    public static ItemStack randomEpic(RandomSource r) {
        Item[] t = {ModItems.QOBYLANDY_DULYGA.get(), ModItems.QOBYLANDY_SAUYT.get(), ModItems.QOBYLANDY_SHALBAR.get(),
                ModItems.QOBYLANDY_ETIK.get(), ModItems.ALPAMYS_DULYGA.get(), ModItems.ALPAMYS_SAUYT.get(),
                ModItems.ALPAMYS_SHALBAR.get(), ModItems.ALPAMYS_ETIK.get(), ModItems.ER_TARGYN_DULYGA.get(),
                ModItems.ER_TARGYN_SAUYT.get(), ModItems.ER_TARGYN_SHALBAR.get(), ModItems.ER_TARGYN_ETIK.get()};
        return new ItemStack(t[r.nextInt(t.length)]);
    }

    private static void give(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.spawnAtLocation(player.level(), stack);
        }
    }
}
