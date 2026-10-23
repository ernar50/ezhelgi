package kz.ezhelgi.entity;

import kz.ezhelgi.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.Filterable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.InteractGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Ауылдас — мирный житель степного аула. 20 обликов, пять ремёсел:
 * пастух, мастер войлока, повар, шорник и торговец. Торгуют за сакское золото.
 * В аулах иногда встречаются великие люди: Абай, Шоқан Уәлиханов и Әл-Фараби.
 */
public class SteppeVillager extends AbstractVillager {
    public enum Kind { COMMON, ABAI, SHOQAN, FARABI, AQSAQAL, KUISHI, KERUENSHI, AQYN, KELIN, ZHYRSHY, DARWISH }

    public static final int VARIANTS = 20;
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(SteppeVillager.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> PLAYING =
            SynchedEntityData.defineId(SteppeVillager.class, EntityDataSerializers.BOOLEAN);
    /** Кюйши играет кюй (8,5 с), потом отдыхает. */
    private int playTicks;
    private int restTicks = 200;
    private int kuiPart;
    /** Цель путешествия каравана. */
    private net.minecraft.core.BlockPos travelTarget;

    private boolean maybeNotable;
    private boolean prepared;

    public SteppeVillager(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    public Kind kind() {
        EntityType<?> t = this.getType();
        if (t == ModEntities.ABAI.get()) return Kind.ABAI;
        if (t == ModEntities.SHOQAN.get()) return Kind.SHOQAN;
        if (t == ModEntities.AL_FARABI.get()) return Kind.FARABI;
        if (t == ModEntities.AQSAQAL.get()) return Kind.AQSAQAL;
        if (t == ModEntities.KUISHI.get()) return Kind.KUISHI;
        if (t == ModEntities.KERUENSHI.get()) return Kind.KERUENSHI;
        if (t == ModEntities.AQYN.get()) return Kind.AQYN;
        if (t == ModEntities.KELIN.get()) return Kind.KELIN;
        if (t == ModEntities.ZHYRSHY.get()) return Kind.ZHYRSHY;
        if (t == ModEntities.DARWISH.get()) return Kind.DARWISH;
        return Kind.COMMON;
    }

    public int variant() {
        return this.entityData.get(VARIANT);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(PLAYING, false);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                                  EntitySpawnReason reason, @Nullable SpawnGroupData data) {
        this.entityData.set(VARIANT, this.getRandom().nextInt(VARIANTS));
        return super.finalizeSpawn(level, difficulty, reason, data);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Monster.class, 8.0f, 0.5, 0.6));
        this.goalSelector.addGoal(1, new PanicGoal(this, 0.6));
        this.goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        this.goalSelector.addGoal(3, new CaravanTravelGoal(this));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.4));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.35));
        this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
    }

    /** Купец ярмарки (жәрмеңке): торгует редкими товарами и уезжает на следующий день. */
    private boolean fair;
    private long fairDay = -1;

    public void makeFair(net.minecraft.core.BlockPos home, long day) {
        this.fair = true;
        this.fairDay = day;
        this.prepared = true;
        this.setHomeTo(home, 8);
    }

    public boolean isFair() {
        return this.fair;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (this.fair && this.tickCount % 100 == 0 && level.getOverworldClockTime() / 24000L != this.fairDay && !this.isTrading()) {
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD, this.getX(), this.getY() + 1, this.getZ(), 8, 0.3, 0.5, 0.3, 0.02);
            this.discard();
            return;
        }
        if (this.isBaby() && this.getAge() > -2000) {
            this.setAge(-24000); // дети аула не взрослеют
        }
        if (this.kind() == Kind.KUISHI) {
            tickMusic(level);
        }
        if (!this.prepared) {
            this.prepared = true;
            if (!this.hasHome() && this.kind() != Kind.KERUENSHI) {
                Kind k = this.kind();
                this.setHomeTo(this.blockPosition(), k == Kind.KUISHI || k == Kind.AQSAQAL || k == Kind.ZHYRSHY || k == Kind.AQYN ? 10 : (k == Kind.DARWISH ? 5 : 22));
            }
            if (this.kind() == Kind.KUISHI) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.DOMBYRA.get()));
                this.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
            }
            if (this.kind() == Kind.KERUENSHI) {
                spawnCaravan(level);
            }
            if (this.kind() == Kind.COMMON && this.variant() % 4 == 1 && !this.isBaby()) {
                // у некоторых в руках қамшы
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.QAMSHY.get()));
                this.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
            }
            if (this.maybeNotable) {
                this.maybeNotable = false;
                if (this.getRandom().nextFloat() < 0.6f) {
                    this.becomeNotable(level);
                }
            }
        }
    }

    /** Вместо обычного жителя появляется Абай, Шоқан или Әл-Фараби. */
    private void becomeNotable(ServerLevel level) {
        int r = this.getRandom().nextInt(3);
        EntityType<SteppeVillager> type = r == 0 ? ModEntities.ABAI.get() : r == 1 ? ModEntities.SHOQAN.get() : ModEntities.AL_FARABI.get();
        SteppeVillager notable = type.create(level, EntitySpawnReason.EVENT);
        if (notable == null) return;
        notable.prepared = false;
        notable.setPos(this.getX(), this.getY(), this.getZ());
        notable.setYRot(this.getYRot());
        notable.setPersistenceRequired();
        level.addFreshEntity(notable);
        this.discard();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (this.isBaby() && this.isAlive() && hand == InteractionHand.MAIN_HAND && stack.is(ModItems.ASYQ.get())) {
            if (this.level() instanceof ServerLevel server) {
                tradeAsyq(server, player, stack);
            }
            return InteractionResult.SUCCESS;
        }
        if (stack.getItem() instanceof net.minecraft.world.item.SpawnEggItem || !this.isAlive() || this.isTrading() || this.isBaby()) {
            return super.mobInteract(player, hand);
        }
        if (hand == InteractionHand.MAIN_HAND) {
            player.awardStat(Stats.TALKED_TO_VILLAGER);
        }
        if (this.level() instanceof ServerLevel server && hand == InteractionHand.MAIN_HAND
                && kz.ezhelgi.aul.AulEvents.toiTreat(server, this, player)) {
            return InteractionResult.SUCCESS;
        }
        if (!this.level().isClientSide()) {
            if (this.getOffers().isEmpty()) {
                return InteractionResult.CONSUME;
            }
            this.setTradingPlayer(player);
            this.openTradingScreen(player, this.getDisplayName(), 1);
        }
        return InteractionResult.SUCCESS;
    }

    // ------------------------------------------------------------------ кюйши
    public boolean isPlaying() {
        return this.entityData.get(PLAYING);
    }

    /** Кюйши играет у огня: днём изредка, вечером и ночью — почти без перерыва, если рядом люди. */
    private void tickMusic(ServerLevel level) {
        if (this.playTicks > 0) {
            this.playTicks--;
            if (this.playTicks % 20 == 0) {
                level.sendParticles(net.minecraft.core.particles.ParticleTypes.NOTE, this.getX(), this.getY() + 2.2, this.getZ(),
                        1, 0.3, 0.1, 0.3, this.getRandom().nextDouble());
            }
            if (this.playTicks == 0) {
                this.entityData.set(PLAYING, false);
                long time = level.getOverworldClockTime() % 24000L;
                boolean evening = level.isDarkOutside() || time > 11000L;
                this.restTicks = evening ? 20 + this.getRandom().nextInt(60) : 400 + this.getRandom().nextInt(800);
            }
            return;
        }
        if (--this.restTicks > 0) return;
        Player near = level.getNearestPlayer(this, 24.0);
        if (near == null || this.isTrading() || this.isBaby()) {
            this.restTicks = 100;
            return;
        }
        this.getNavigation().stop();
        this.getLookControl().setLookAt(near);
        List<net.minecraftforge.registries.RegistryObject<SoundEvent>> parts = kz.ezhelgi.sound.ModSounds.KURMANGAZY_KUI;
        SoundEvent sound = parts.get(this.kuiPart % parts.size()).get();
        this.kuiPart = (this.kuiPart + 1) % parts.size();
        level.playSound(null, this.getX(), this.getY(), this.getZ(), sound, net.minecraft.sounds.SoundSource.NEUTRAL, 1.2f, 1.0f);
        this.playTicks = 170;
        this.entityData.set(PLAYING, true);
    }

    // ------------------------------------------------------------------ керуенші (караван)
    private void spawnCaravan(ServerLevel level) {
        int n = 2 + this.getRandom().nextInt(2);
        net.minecraft.world.entity.Entity leader = this;
        for (int i = 0; i < n; i++) {
            net.minecraft.world.entity.animal.camel.Camel camel = net.minecraft.world.entity.EntityTypes.CAMEL.create(level, EntitySpawnReason.EVENT);
            if (camel == null) return;
            camel.snapTo(this.getX() - this.getLookAngle().x * (2.5 * (i + 1)), this.getY(), this.getZ() - this.getLookAngle().z * (2.5 * (i + 1)));
            camel.setPersistenceRequired();
            if (i == 0) {
                camel.setItemSlot(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
            }
            level.addFreshEntity(camel);
            // верблюды идут цепочкой: каждый привязан к предыдущему
            camel.setLeashedTo(leader, true);
            leader = camel;
        }
    }

    /** Караван медленно идёт по Шёлковому пути от одной далёкой точки к другой. */
    static class CaravanTravelGoal extends net.minecraft.world.entity.ai.goal.Goal {
        private final SteppeVillager mob;
        private int cooldown;

        CaravanTravelGoal(SteppeVillager mob) {
            this.mob = mob;
            this.setFlags(java.util.EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.mob.kind() != Kind.KERUENSHI || this.mob.fair || this.mob.isTrading() || --this.cooldown > 0) return false;
            this.cooldown = 60;
            return true;
        }

        @Override
        public void start() {
            net.minecraft.core.BlockPos pos = this.mob.blockPosition();
            if (this.mob.travelTarget == null || this.mob.travelTarget.closerThan(pos, 12)) {
                double a = this.mob.getRandom().nextDouble() * Math.PI * 2;
                this.mob.travelTarget = pos.offset((int) (Math.cos(a) * 300), 0, (int) (Math.sin(a) * 300));
            }
            net.minecraft.world.phys.Vec3 dir = net.minecraft.world.phys.Vec3.atCenterOf(this.mob.travelTarget)
                    .subtract(this.mob.position()).multiply(1, 0, 1).normalize().scale(18);
            this.mob.getNavigation().moveTo(this.mob.getX() + dir.x, this.mob.getY(), this.mob.getZ() + dir.z, 0.42);
        }

        @Override
        public boolean canContinueToUse() {
            return !this.mob.getNavigation().isDone() && !this.mob.isTrading();
        }
    }

    /** Дети меняют асыки на угощение, сакское золото и шаблоны отделки. */
    private void tradeAsyq(ServerLevel level, Player player, ItemStack stack) {
        int n = stack.getCount();
        ItemStack reward;
        int cost;
        if (n >= 16) {
            cost = 16;
            reward = kz.ezhelgi.quest.Quests.randomTemplate(this.getRandom());
        } else if (n >= 8) {
            cost = 8;
            reward = new ItemStack(ModItems.SAKA_GOLD.get(), 2);
        } else if (n >= 4) {
            cost = 4;
            reward = new ItemStack(ModItems.BAURSAK.get(), 4);
        } else {
            player.sendOverlayMessage(Component.translatable("message.ezhelgi.asyq.child_need"));
            return;
        }
        if (!player.getAbilities().instabuild) {
            stack.shrink(cost);
        }
        if (!player.getInventory().add(reward)) {
            player.spawnAtLocation(level, reward);
        }
        player.sendSystemMessage(Component.translatable("message.ezhelgi.asyq.child_trade", cost, reward.getHoverName()));
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, this.getX(), this.getY() + 1, this.getZ(), 5, 0.3, 0.3, 0.3, 0);
        this.playSound(SoundEvents.VILLAGER_YES, 1.0f, 1.6f);
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(),
                    2 + this.random.nextInt(3)));
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return false;
    }

    // ------------------------------------------------------------------ торговля
    private static Item gold() {
        return ModItems.SAKA_GOLD.get();
    }

    /** Житель продаёт: price сакского золота → count штук. */
    private static MerchantOffer sell(int price, ItemStack result, int maxUses) {
        return new MerchantOffer(new ItemCost(gold(), price), result, maxUses, 3, 0.05f);
    }

    private static MerchantOffer sell(int price, Supplier<? extends Item> item, int count) {
        return sell(price, new ItemStack(item.get(), count), 12);
    }

    private static MerchantOffer sell(int price, Item item, int count) {
        return sell(price, new ItemStack(item, count), 12);
    }

    /** Житель покупает: count штук → price сакского золота. */
    private static MerchantOffer buy(Item item, int count, int price) {
        return new MerchantOffer(new ItemCost(item, count), new ItemStack(gold(), price), 16, 2, 0.05f);
    }

    @Override
    protected void updateTrades(ServerLevel level) {
        MerchantOffers offers = this.getOffers();
        RandomSource random = this.getRandom();
        List<MerchantOffer> pool = new ArrayList<>();
        List<MerchantOffer> fixed = new ArrayList<>();
        switch (this.kind()) {
            case ABAI -> {
                for (int i = 1; i <= 3; i++) {
                    fixed.add(sell(2, qaraSozder(i), 6));
                }
                for (int i = 0; i < 4; i++) {
                    ItemStack book = randomEnchantedBook(level, random);
                    if (!book.isEmpty()) {
                        fixed.add(sell(3 + random.nextInt(5), book, 3));
                    }
                }
                fixed.add(sell(1, Items.WRITABLE_BOOK, 1));
                fixed.add(sell(1, Items.BOOK, 3));
                fixed.add(buy(Items.PAPER, 24, 1));
            }
            case SHOQAN -> {
                fixed.add(sell(1, Items.MAP, 2));
                fixed.add(sell(1, Items.COMPASS, 1));
                fixed.add(sell(2, Items.RECOVERY_COMPASS, 1));
                fixed.add(sell(1, Items.BRUSH, 1));
                fixed.add(sell(1, Items.WRITABLE_BOOK, 1));
                fixed.add(sell(1, Items.PAPER, 16));
                fixed.add(sell(1, Items.LEAD, 2));
                fixed.add(buy(ModItems.OTYRAR_TENGESI.get(), 3, 1));
                fixed.add(buy(ModItems.QOLJAZBA.get(), 1, 2));
                fixed.add(buy(ModItems.QUMYRA.get(), 1, 1));
            }
            case FARABI -> {
                fixed.add(sell(2, Items.CLOCK, 1));
                fixed.add(sell(2, Items.SPYGLASS, 1));
                fixed.add(sell(1, Items.AMETHYST_SHARD, 6));
                for (String name : new String[]{"juldyzdy_tun", "temir_qazyq", "jeti_qaraqshy", "ay_dala", "astrolabe", "urker"}) {
                    ItemStack painting = painting(level, name);
                    if (!painting.isEmpty()) {
                        fixed.add(sell(2, painting, 4));
                    }
                }
                fixed.add(buy(ModItems.QOLJAZBA.get(), 1, 3));
            }
            case AQSAQAL, AQYN, KELIN, ZHYRSHY, DARWISH -> {
            }
            case KUISHI -> {
                fixed.add(sell(3, ModItems.DOMBYRA, 1));
                fixed.add(sell(4, ModItems.QOBYZ, 1));
                fixed.add(sell(6, ModItems.MUSIC_DISC_ADAI, 1));
                fixed.add(sell(2, ModItems.TOGYZ_QUMALAQ, 1));
                fixed.add(sell(3, ModItems.KIELI_QYL, 1));
                fixed.add(buy(Items.STRING, 12, 1));
                fixed.add(buy(Items.NOTE_BLOCK, 2, 1));
            }
            case KERUENSHI -> {
                if (this.fair) {
                    fairTrades(level, random, pool);
                    break;
                }
                pool.add(sell(1, Items.SUGAR, 12));
                pool.add(sell(1, Items.COCOA_BEANS, 8));
                pool.add(sell(2, Items.AMETHYST_SHARD, 8));
                pool.add(sell(1, Items.GLASS, 12));
                pool.add(sell(1, Items.PAPER, 16));
                pool.add(sell(2, Items.LAPIS_LAZULI, 12));
                pool.add(sell(1, ModItems.SYRMAK, 4));
                pool.add(sell(1, ModItems.KOK_SYRMAK, 4));
                pool.add(sell(1, ModItems.ORNEK, 4));
                pool.add(sell(3, Items.SPYGLASS, 1));
                pool.add(sell(4, ModItems.OIU_BANNER_PATTERN, 1));
                pool.add(sell(2, ModItems.TU_QAZAQSTAN, 1));
                pool.add(sell(2, ModItems.TU_ALASH, 1));
                pool.add(sell(2, ModItems.TU_HANDYQ, 1));
                pool.add(sell(8, ModItems.QOSHQAR_MUIZ_TEMPLATE, 1));
                pool.add(sell(8, ModItems.QUS_QANATY_TEMPLATE, 1));
                pool.add(sell(5, ModItems.QALQAN, 1));
                pool.add(buy(Items.EMERALD, 2, 1));
                pool.add(buy(ModItems.OTYRAR_TENGESI.get(), 2, 1));
                pool.add(buy(Items.GOLD_INGOT, 4, 1));
            }
            default -> {
                switch (this.variant() % 5) {
                    case 0 -> { // пастух
                        pool.add(sell(1, Items.COOKED_MUTTON, 6));
                        pool.add(sell(1, Items.WOOL.white(), 6));
                        pool.add(sell(1, Items.LEATHER, 4));
                        pool.add(sell(1, ModItems.KURT, 8));
                        pool.add(sell(1, ModItems.KUMYS, 3));
                        pool.add(sell(1, ModItems.PISKEN_JYLQY_ETI, 4));
                        pool.add(buy(Items.WHEAT, 16, 1));
                        pool.add(buy(Items.MUTTON, 10, 1));
                    }
                    case 1 -> { // мастер войлока и юрт
                        pool.add(sell(1, ModItems.KIIZ, 8));
                        pool.add(sell(1, ModItems.SYRMAK, 4));
                        pool.add(sell(1, ModItems.ALASHA, 4));
                        pool.add(sell(1, ModItems.TEKEMET, 4));
                        pool.add(sell(1, ModItems.QONYR_OIU, 6));
                        pool.add(sell(1, ModItems.KEREGE, 8));
                        pool.add(sell(2, ModItems.SHANYRAK, 1));
                        pool.add(sell(4, ModItems.KIIZ_UY, 1));
                        pool.add(sell(6, ModItems.QONYR_UY, 1));
                        pool.add(buy(Items.WOOL.white(), 16, 1));
                    }
                    case 2 -> { // повар
                        pool.add(sell(1, ModItems.BAURSAK, 8));
                        pool.add(sell(1, ModItems.KURT, 8));
                        pool.add(sell(1, ModItems.KAZY, 3));
                        pool.add(sell(1, ModItems.BESHBARMAK, 2));
                        pool.add(sell(1, ModItems.KUMYS, 3));
                        pool.add(sell(1, ModItems.SHUBAT, 3));
                        pool.add(sell(1, ModItems.PISKEN_AQBOKEN_ETI, 4));
                        pool.add(buy(Items.BEEF, 10, 1));
                        pool.add(buy(ModItems.JYLQY_ETI.get(), 8, 1));
                    }
                    case 3 -> { // шорник
                        pool.add(sell(2, Items.SADDLE, 1));
                        pool.add(sell(2, Items.LEATHER_HORSE_ARMOR, 1));
                        pool.add(sell(1, Items.LEAD, 2));
                        pool.add(sell(2, ModItems.TORSYK, 1));
                        pool.add(sell(3, ModItems.ARBA, 1));
                        pool.add(sell(1, Items.LEATHER, 5));
                        pool.add(buy(Items.LEATHER, 10, 1));
                        pool.add(buy(Items.STRING, 16, 1));
                    }
                    default -> { // торговец
                        pool.add(sell(1, Items.WHEAT, 16));
                        pool.add(sell(1, Items.BREAD, 6));
                        pool.add(sell(1, Items.HAY_BLOCK, 3));
                        pool.add(sell(1, Items.APPLE, 6));
                        pool.add(sell(1, Items.COPPER_INGOT, 8));
                        pool.add(sell(1, Items.HONEY_BOTTLE, 3));
                        pool.add(buy(Items.GOLD_INGOT, 5, 1));
                        pool.add(buy(Items.EMERALD, 3, 1));
                    }
                }
            }
        }
        offers.addAll(fixed);
        java.util.Collections.shuffle(pool, new java.util.Random(random.nextLong()));
        int n = Math.min(pool.size(), 5 + random.nextInt(2));
        for (int i = 0; i < n; i++) {
            offers.add(pool.get(i));
        }
    }

    /** Редкие товары ярмарки: книги, инструменты, наряды, самауыр, саженцы. */
    private static void fairTrades(ServerLevel level, RandomSource random, List<MerchantOffer> pool) {
        ItemStack book = randomEnchantedBook(level, random);
        if (!book.isEmpty()) pool.add(sell(6, book, 2));
        pool.add(sell(5, ModItems.SYBYZGY, 1));
        pool.add(sell(7, ModItems.ZHETIGEN, 1));
        pool.add(sell(4, ModItems.SHANQOBYZ, 1));
        pool.add(sell(10, ModItems.DAUYLPAZ, 1));
        pool.add(sell(6, ModItems.SAMAUYR, 1));
        pool.add(sell(4, ModItems.KAZAN, 1));
        pool.add(sell(5, ModItems.KEBEZHE, 1));
        pool.add(sell(12, ModItems.SAUKELE, 1));
        pool.add(sell(8, ModItems.UKI_BORIK, 1));
        pool.add(sell(6, ModItems.SHAPAN, 1));
        pool.add(sell(9, ModItems.ALTYN_BELBEU, 1));
        pool.add(sell(3, ModItems.ALMA_KOSHETI, 2));
        pool.add(sell(2, ModItems.SEKSEUIL, 8));
        pool.add(sell(14, Items.DIAMOND, 1));
        pool.add(sell(3, Items.NAME_TAG, 1));
        pool.add(sell(10, ModItems.SYNAR_MUIZ_TEMPLATE, 1));
        pool.add(sell(10, ModItems.TUMARSHA_TEMPLATE, 1));
        pool.add(buy(Items.DIAMOND, 1, 6));
        pool.add(buy(Items.EMERALD, 1, 1));
    }

    private static ItemStack randomEnchantedBook(ServerLevel level, RandomSource random) {
        Optional<HolderSet.Named<Enchantment>> tag = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(EnchantmentTags.TRADEABLE);
        if (tag.isEmpty()) return ItemStack.EMPTY;
        Optional<Holder<Enchantment>> ench = tag.get().getRandomElement(random);
        if (ench.isEmpty()) return ItemStack.EMPTY;
        int max = ench.get().value().getMaxLevel();
        return EnchantmentHelper.createBook(new EnchantmentInstance(ench.get(), 1 + random.nextInt(max)));
    }

    private static ItemStack painting(ServerLevel level, String name) {
        ResourceKey<PaintingVariant> key = ResourceKey.create(Registries.PAINTING_VARIANT,
                Identifier.fromNamespaceAndPath("ezhelgi", name));
        Optional<Holder.Reference<PaintingVariant>> holder = level.registryAccess().lookupOrThrow(Registries.PAINTING_VARIANT).get(key);
        if (holder.isEmpty()) return ItemStack.EMPTY;
        ItemStack stack = new ItemStack(Items.PAINTING);
        stack.set(DataComponents.PAINTING_VARIANT, holder.get());
        return stack;
    }

    /** Книга «Қара сөздер» (пересказ, том 1–3). */
    private static ItemStack qaraSozder(int volume) {
        ItemStack book = new ItemStack(Items.WRITTEN_BOOK);
        List<Filterable<Component>> pages = new ArrayList<>();
        for (int p = 1; p <= 4; p++) {
            pages.add(Filterable.passThrough(Component.translatable("book.ezhelgi.qara_soz." + volume + "." + p)));
        }
        book.set(DataComponents.WRITTEN_BOOK_CONTENT, new WrittenBookContent(
                Filterable.passThrough("Қара сөздер " + volume), "Абай Құнанбайұлы", 0, pages, true));
        return book;
    }

    // ------------------------------------------------------------------ звуки и сохранение
    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return this.isTrading() ? SoundEvents.VILLAGER_TRADE : SoundEvents.VILLAGER_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.VILLAGER_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.VILLAGER_DEATH;
    }

    @Override
    public float getVoicePitch() {
        if (this.isBaby()) return 1.45f + (this.variant() % 3) * 0.1f;
        return this.kind() == Kind.COMMON ? 0.8f + (this.variant() % 4) * 0.08f : 0.75f;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Variant", this.variant());
        output.putBoolean("MaybeNotable", this.maybeNotable);
        output.putBoolean("Prepared", this.prepared);
        output.putInt("KuiPart", this.kuiPart);
        output.putBoolean("Fair", this.fair);
        output.putLong("FairDay", this.fairDay);
        if (this.travelTarget != null) {
            output.putLong("TravelTarget", this.travelTarget.asLong());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        int v = input.getIntOr("Variant", -1);
        this.entityData.set(VARIANT, v < 0 ? this.getRandom().nextInt(VARIANTS) : v % VARIANTS);
        this.maybeNotable = input.getBooleanOr("MaybeNotable", false);
        this.prepared = input.getBooleanOr("Prepared", false);
        this.kuiPart = input.getIntOr("KuiPart", 0);
        this.fair = input.getBooleanOr("Fair", false);
        this.fairDay = input.getLongOr("FairDay", -1L);
        long target = input.getLongOr("TravelTarget", Long.MIN_VALUE);
        this.travelTarget = target == Long.MIN_VALUE ? null : net.minecraft.core.BlockPos.of(target);
    }
}
