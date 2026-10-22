package kz.ezhelgi.entity.animal;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Общая основа диких животных Казахстана: пугливость, размножение,
 * самцы и самки (у самцов рога).
 */
public abstract class WildAnimal extends Animal {
    private static final EntityDataAccessor<Boolean> MALE = SynchedEntityData.defineId(WildAnimal.class, EntityDataSerializers.BOOLEAN);

    protected WildAnimal(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    /** Чем приманивать и разводить. */
    protected abstract Ingredient food();

    /** Дистанция, на которой животное убегает от игрока (0 — не боится). */
    protected float shyness() {
        return 10.0f;
    }

    protected double fleeSpeed() {
        return 1.8;
    }

    /** Подходит любая твёрдая земля под ногами — песок, камень, трава. */
    public static boolean checkWildSpawnRules(EntityType<? extends Animal> type, LevelAccessor level, EntitySpawnReason reason,
                                              BlockPos pos, RandomSource random) {
        return level.getBlockState(pos.below()).isSolid() && level.getRawBrightness(pos, 0) > 7;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(MALE, false);
    }

    public boolean isMale() {
        return this.entityData.get(MALE);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                                  EntitySpawnReason reason, @Nullable SpawnGroupData data) {
        this.entityData.set(MALE, this.getRandom().nextInt(3) == 0);
        return super.finalizeSpawn(level, difficulty, reason, data);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new PanicGoal(this, 2.0));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0));
        this.goalSelector.addGoal(3, new TemptGoal(this, 1.1, this.food(), false));
        if (this.shyness() > 0) {
            this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Player.class, this.shyness(), 1.4, this.fleeSpeed(),
                    p -> !p.isCrouching() && this.getTarget() == null && !((Player) p).isCreative()));
        }
        this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.2));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.9));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return this.food().test(stack);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        AgeableMob baby = ((EntityType<? extends AgeableMob>) this.getType()).create(level, EntitySpawnReason.BREEDING);
        if (baby instanceof WildAnimal wild) {
            wild.entityData.set(MALE, this.getRandom().nextBoolean());
        }
        return baby;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Male", this.isMale());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(MALE, input.getBooleanOr("Male", false));
    }
}
