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
    public enum Kind { COMMON, ABAI, SHOQAN, FARABI }

    public static final int VARIANTS = 20;
    private static final EntityDataAccessor<Integer> VARIANT =
            SynchedEntityData.defineId(SteppeVillager.class, EntityDataSerializers.INT);

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
        return Kind.COMMON;
    }

    public int variant() {
        return this.entityData.get(VARIANT);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
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
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.4));
        this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 0.35));
        this.goalSelector.addGoal(9, new InteractGoal(this, Player.class, 3.0f, 1.0f));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0f));
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (!this.prepared) {
            this.prepared = true;
            if (!this.hasHome()) {
                this.setHomeTo(this.blockPosition(), 22);
            }
            if (this.kind() == Kind.COMMON && this.variant() % 4 == 1) {
                // у некоторых в руках қамшы
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.QAMSHY.get()));
                this.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
            }
            if (this.maybeNotable) {
                this.maybeNotable = false;
                if (this.getRandom().nextFloat() < 0.35f) {
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
        if (stack.getItem() instanceof net.minecraft.world.item.SpawnEggItem || !this.isAlive() || this.isTrading() || this.isBaby()) {
            return super.mobInteract(player, hand);
        }
        if (hand == InteractionHand.MAIN_HAND) {
            player.awardStat(Stats.TALKED_TO_VILLAGER);
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
        return this.kind() == Kind.COMMON ? 0.8f + (this.variant() % 4) * 0.08f : 0.75f;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Variant", this.variant());
        output.putBoolean("MaybeNotable", this.maybeNotable);
        output.putBoolean("Prepared", this.prepared);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        int v = input.getIntOr("Variant", -1);
        this.entityData.set(VARIANT, v < 0 ? this.getRandom().nextInt(VARIANTS) : v % VARIANTS);
        this.maybeNotable = input.getBooleanOr("MaybeNotable", false);
        this.prepared = input.getBooleanOr("Prepared", false);
    }
}
