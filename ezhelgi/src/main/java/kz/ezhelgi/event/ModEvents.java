package kz.ezhelgi.event;

import kz.ezhelgi.accessory.Accessories;
import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.item.TorsykItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Игровые события мода:
 * <ul>
 *   <li>с лошадей падает конина;</li>
 *   <li>кобылу доят в бутылку или торсык — получается қымыз, верблюдицу — шұбат;</li>
 *   <li>украшения выпадают при смерти (если не включён keepInventory).</li>
 * </ul>
 */
public final class ModEvents {
    private ModEvents() {}

    private static final String MILKED = "ezhelgi_milked";
    /** Сколько ждать между дойками одного животного (1 минута). */
    private static final long MILK_COOLDOWN = 1200L;

    public static void init() {
        LivingDropsEvent.BUS.addListener(ModEvents::onDrops);
        LivingDeathEvent.BUS.addListener(ModEvents::onDeath);
        PlayerInteractEvent.EntityInteractSpecific.BUS.addListener(ModEvents::onInteract);
        net.minecraftforge.event.TickEvent.PlayerTickEvent.Post.BUS.addListener(ModEvents::onPlayerTick);
        net.minecraftforge.event.TickEvent.LevelTickEvent.Post.BUS.addListener(ModEvents::onLevelTick);
        net.minecraftforge.event.entity.EntityJoinLevelEvent.BUS.addListener(ModEvents::onJoin);
    }

    // ------------------------------------------------------------------ мүшел жыл
    private static long announcedMushel = -1;

    /** Мүшел: 13-й день, а потом каждый 12-й — нечисть сильнее и в броне. */
    public static boolean isMushel(ServerLevel level) {
        long day = level.getOverworldClockTime() / 24000L;
        return day >= 12 && day % 12 == 0;
    }

    private static void onLevelTick(net.minecraftforge.event.TickEvent.LevelTickEvent.Post event) {
        if (!(event.level() instanceof ServerLevel level)) {
            return;
        }
        kz.ezhelgi.aul.AulEvents.tick(level);
        if (level.dimension() != net.minecraft.world.level.Level.OVERWORLD || level.getGameTime() % 100 != 0) {
            return;
        }
        long day = level.getOverworldClockTime() / 24000L;
        if (isMushel(level) && announcedMushel != day) {
            announcedMushel = day;
            for (ServerPlayer p : level.players()) {
                p.sendSystemMessage(Component.translatable("message.ezhelgi.mushel.start", day + 1)
                        .withStyle(net.minecraft.ChatFormatting.DARK_RED));
                p.sendSystemMessage(Component.translatable("message.ezhelgi.mushel.desc").withStyle(net.minecraft.ChatFormatting.GRAY));
                level.playSound(null, p.blockPosition(), SoundEvents.WITHER_AMBIENT, SoundSource.AMBIENT, 0.6f, 0.6f);
            }
        }
    }

    private static final net.minecraft.resources.Identifier MUSHEL_ID =
            net.minecraft.resources.Identifier.fromNamespaceAndPath("ezhelgi", "mushel");

    private static void onJoin(net.minecraftforge.event.entity.EntityJoinLevelEvent event) {
        if (event.loadedFromDisk() || !(event.getLevel() instanceof ServerLevel level)
                || !(event.getEntity() instanceof net.minecraft.world.entity.monster.Monster mob)
                || level.dimension() != net.minecraft.world.level.Level.OVERWORLD || !isMushel(level)) {
            return;
        }
        addMod(mob, net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 0.5,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        addMod(mob, net.minecraft.world.entity.ai.attributes.Attributes.ARMOR, 6.0,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE);
        addMod(mob, net.minecraft.world.entity.ai.attributes.Attributes.ARMOR_TOUGHNESS, 2.0,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE);
        addMod(mob, net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 0.3,
                net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        mob.setHealth(mob.getMaxHealth());
        // больше брони: шлем и нагрудник, если их нет
        var r = level.getRandom();
        if (mob.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).isEmpty() && r.nextFloat() < 0.6f) {
            mob.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, new ItemStack(r.nextBoolean() ? Items.IRON_HELMET : Items.CHAINMAIL_HELMET));
            mob.setDropChance(net.minecraft.world.entity.EquipmentSlot.HEAD, 0.05f);
        }
        if (mob.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST).isEmpty() && r.nextFloat() < 0.5f) {
            mob.setItemSlot(net.minecraft.world.entity.EquipmentSlot.CHEST, new ItemStack(r.nextBoolean() ? Items.IRON_CHESTPLATE : Items.CHAINMAIL_CHESTPLATE));
            mob.setDropChance(net.minecraft.world.entity.EquipmentSlot.CHEST, 0.05f);
        }
    }

    private static void addMod(net.minecraft.world.entity.LivingEntity mob,
                               net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr, double value,
                               net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation op) {
        var inst = mob.getAttribute(attr);
        if (inst != null && !inst.hasModifier(MUSHEL_ID)) {
            inst.addPermanentModifier(new net.minecraft.world.entity.ai.attributes.AttributeModifier(MUSHEL_ID, value, op));
        }
    }

    /** Бонусы полных эпических доспехов: проверяем раз в секунду. */
    private static void onPlayerTick(net.minecraftforge.event.TickEvent.PlayerTickEvent.Post event) {
        Player player = event.player();
        if (!(player instanceof ServerPlayer sp) || sp.tickCount % 20 != 0 || !sp.isAlive()) {
            return;
        }
        clothes(sp);
        String set = fullSet(sp);
        if (set == null) {
            return;
        }
        switch (set) {
            case "qobylandy" -> {
                // Қобыланды — сила батыра; его конь Тайбурыл скачет быстрее ветра
                effect(sp, net.minecraft.world.effect.MobEffects.STRENGTH, 0);
                effect(sp, net.minecraft.world.effect.MobEffects.RESISTANCE, 0);
                if (sp.getVehicle() instanceof net.minecraft.world.entity.LivingEntity mount) {
                    mount.addEffect(new net.minecraft.world.effect.MobEffectInstance(net.minecraft.world.effect.MobEffects.SPEED, 60, 1, true, false));
                }
            }
            case "alpamys" -> {
                // Алпамыс — ни огонь, ни стрелы не брали его тело
                effect(sp, net.minecraft.world.effect.MobEffects.FIRE_RESISTANCE, 0);
                effect(sp, net.minecraft.world.effect.MobEffects.RESISTANCE, 1);
            }
            case "er_targyn" -> {
                // Ер Тарғын — быстрый и ловкий батыр
                effect(sp, net.minecraft.world.effect.MobEffects.SPEED, 0);
                effect(sp, net.minecraft.world.effect.MobEffects.JUMP_BOOST, 1);
                effect(sp, net.minecraft.world.effect.MobEffects.HASTE, 0);
            }
            default -> { }
        }
    }

    /** Бонусы национальной одежды. */
    private static void clothes(ServerPlayer sp) {
        for (net.minecraft.world.entity.EquipmentSlot slot : new net.minecraft.world.entity.EquipmentSlot[]{
                net.minecraft.world.entity.EquipmentSlot.HEAD, net.minecraft.world.entity.EquipmentSlot.CHEST}) {
            if (!(sp.getItemBySlot(slot).getItem() instanceof kz.ezhelgi.item.ClothesItem c)) {
                continue;
            }
            switch (c.kind()) {
                case "shapan" -> {
                    // шапан греет: в холодных краях не мёрзнешь и легче держишь удар
                    if (sp.level().getBiome(sp.blockPosition()).value().coldEnoughToSnow(sp.blockPosition(), sp.level().getSeaLevel())) {
                        effect(sp, net.minecraft.world.effect.MobEffects.RESISTANCE, 0);
                        sp.setTicksFrozen(0);
                    }
                }
                case "kamzol" -> effect(sp, net.minecraft.world.effect.MobEffects.SPEED, 0);
                case "taqiya" -> effect(sp, net.minecraft.world.effect.MobEffects.LUCK, 0);
                case "kimeshek" -> effect(sp, net.minecraft.world.effect.MobEffects.HEALTH_BOOST, 0);
                case "saukele" -> {
                    effect(sp, net.minecraft.world.effect.MobEffects.LUCK, 1);
                    effect(sp, net.minecraft.world.effect.MobEffects.HERO_OF_THE_VILLAGE, 0);
                }
                case "uki_borik" -> {
                    // перья филина берегут от сглаза и порчи
                    sp.removeEffect(net.minecraft.world.effect.MobEffects.POISON);
                    sp.removeEffect(net.minecraft.world.effect.MobEffects.WEAKNESS);
                    sp.removeEffect(net.minecraft.world.effect.MobEffects.NAUSEA);
                    effect(sp, net.minecraft.world.effect.MobEffects.NIGHT_VISION, 0);
                }
                case "kelin_koilek" -> effect(sp, net.minecraft.world.effect.MobEffects.REGENERATION, 0);
                default -> { }
            }
        }
    }

    private static void effect(Player player, net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> effect, int amp) {
        player.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect, 50, amp, true, false, true));
    }

    private static String fullSet(Player player) {
        String set = null;
        for (net.minecraft.world.entity.EquipmentSlot slot : new net.minecraft.world.entity.EquipmentSlot[]{
                net.minecraft.world.entity.EquipmentSlot.HEAD, net.minecraft.world.entity.EquipmentSlot.CHEST,
                net.minecraft.world.entity.EquipmentSlot.LEGS, net.minecraft.world.entity.EquipmentSlot.FEET}) {
            if (!(player.getItemBySlot(slot).getItem() instanceof kz.ezhelgi.item.EpicArmorItem epic)) {
                return null;
            }
            if (set == null) {
                set = epic.set();
            } else if (!set.equals(epic.set())) {
                return null;
            }
        }
        return set;
    }

    private static void onDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level) || entity.isBaby()) {
            return;
        }
        if (entity instanceof net.minecraft.world.entity.animal.sheep.Sheep && level.getRandom().nextFloat() < 0.35f) {
            event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY() + 0.5, entity.getZ(),
                    new ItemStack(ModItems.ASYQ.get(), 1 + level.getRandom().nextInt(2))));
        }
        if (entity instanceof Horse) {
            int n = 1 + level.getRandom().nextInt(3);
            ItemStack meat = new ItemStack(entity.isOnFire() ? ModItems.PISKEN_JYLQY_ETI.get() : ModItems.JYLQY_ETI.get(), n);
            event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY() + 0.5, entity.getZ(), meat));
        }
    }

    private static void onDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer killer && !(event.getEntity() instanceof Player)) {
            kz.ezhelgi.quest.Quests.onKill(killer, event.getEntity());
        }
        if (event.getEntity() instanceof ServerPlayer player && !player.isSpectator()) {
            ServerLevel level = player.level();
            if (level.getGameRules().get(GameRules.KEEP_INVENTORY)) {
                return;
            }
            SimpleContainer container = Accessories.load(player);
            boolean any = false;
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.removeItemNoUpdate(i);
                if (!stack.isEmpty()) {
                    player.spawnAtLocation(level, stack);
                    any = true;
                }
            }
            if (any) {
                Accessories.save(player, container);
                Accessories.refresh(player);
            }
        }
    }

    /** Доение кобылы и верблюдицы. Возвращает true, чтобы не открылось седло/посадка. */
    private static boolean onInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        Entity target = event.getTarget();
        boolean horse = target instanceof Horse h && !h.isBaby() && !(target instanceof kz.ezhelgi.entity.Tulpar);
        boolean camel = target instanceof Camel c && !c.isBaby();
        if (!horse && !camel) {
            return false;
        }
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());
        boolean bottle = stack.is(Items.GLASS_BOTTLE);
        boolean torsyk = stack.getItem() instanceof TorsykItem t && t.canTake(stack, camel ? TorsykItem.Drink.SHUBAT : TorsykItem.Drink.QYMYZ);
        if (!bottle && !torsyk) {
            return false;
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (!(player.level() instanceof ServerLevel level)) {
            return true;
        }
        CompoundTag data = target.getPersistentData();
        long now = level.getGameTime();
        long last = data.getLongOr(MILKED, -MILK_COOLDOWN);
        if (now - last < MILK_COOLDOWN) {
            player.sendOverlayMessage(Component.translatable(camel ? "message.ezhelgi.milk.camel_empty" : "message.ezhelgi.milk.horse_empty"));
            return true;
        }
        data.putLong(MILKED, now);
        level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.COW_MILK, SoundSource.PLAYERS, 1.0f, 1.0f);
        level.sendParticles(ParticleTypes.HEART, target.getX(), target.getY() + target.getBbHeight(), target.getZ(), 2, 0.3, 0.2, 0.3, 0.0);
        if (bottle) {
            ItemStack drink = new ItemStack(camel ? ModItems.SHUBAT.get() : ModItems.KUMYS.get());
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            if (!player.getInventory().add(drink)) {
                player.spawnAtLocation(level, drink);
            }
        } else {
            TorsykItem.addPortion(player, event.getHand(), stack, camel ? TorsykItem.Drink.SHUBAT : TorsykItem.Drink.QYMYZ);
        }
        return true;
    }
}
