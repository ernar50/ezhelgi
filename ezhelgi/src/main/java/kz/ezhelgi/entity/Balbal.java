package kz.ezhelgi.entity;

import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Балбал — древнее каменное изваяние. Днём стоит неподвижно, как статуя.
 * Ночью, если подойти близко, оживает: медленный, но тяжёлый,
 * стрелы его почти не берут. Оставляет древний камень.
 */
public class Balbal extends Monster {
    private static final EntityDataAccessor<Boolean> AWAKE =
            SynchedEntityData.defineId(Balbal.class, EntityDataSerializers.BOOLEAN);
    private float statueYaw;
    private boolean yawSet = false;
    private int calm = 0;

    public Balbal(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.ATTACK_DAMAGE, 9.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.17)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 20.0);
    }

    public static boolean checkSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor level, EntitySpawnReason reason,
                                          BlockPos pos, RandomSource random) {
        return random.nextInt(3) == 0 && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(AWAKE, false);
    }

    public boolean isAwake() {
        return this.entityData.get(AWAKE);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    private static final BlockParticleOption DUST = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.STONE.defaultBlockState());

    private void wake(ServerLevel level) {
        if (this.isAwake()) return;
        this.entityData.set(AWAKE, true);
        this.setNoAi(false);
        this.playSound(ModSounds.BALBAL_AWAKE.get(), 1.6f, 1.0f);
        level.sendParticles(DUST, this.getX(), this.getY() + 1.0, this.getZ(), 40, 0.4, 0.8, 0.4, 0.1);
    }

    private void sleep(ServerLevel level) {
        this.entityData.set(AWAKE, false);
        this.setTarget(null);
        this.getNavigation().stop();
        this.statueYaw = this.getYRot();
        this.setNoAi(true);
        level.sendParticles(DUST, this.getX(), this.getY() + 1.0, this.getZ(), 15, 0.3, 0.6, 0.3, 0.05);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (!this.yawSet) {
            this.yawSet = true;
            this.statueYaw = this.getYRot();
            if (!this.isAwake()) {
                this.setNoAi(true);
            }
        }
        if (!this.isAwake()) {
            // неподвижная статуя
            this.setYRot(this.statueYaw);
            this.yBodyRot = this.statueYaw;
            this.yHeadRot = this.statueYaw;
            if (this.tickCount % 10 == 0 && level.isDarkOutside()) {
                Player p = level.getNearestPlayer(this, 6.0);
                if (p != null && !p.isCreative() && !p.isSpectator()) {
                    this.wake(level);
                }
            }
            return;
        }
        if (this.getTarget() == null) {
            if (++this.calm > 200 && level.isBrightOutside()) {
                this.calm = 0;
                this.sleep(level);
            }
        } else {
            this.calm = 0;
        }
        if (this.walkAnimation.speed() > 0.1f && this.tickCount % 6 == 0) {
            this.playSound(SoundEvents.STONE_STEP, 0.9f, 0.6f);
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (!this.isAwake() && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.wake(level);
            amount *= 0.5f;
        }
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            // стрелы отскакивают от камня
            amount *= 0.15f;
            this.playSound(SoundEvents.STONE_HIT, 1.0f, 1.3f);
        }
        return super.hurtServer(level, source, amount);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return this.isAwake() ? SoundEvents.GRINDSTONE_USE : null;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.STONE_HIT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.STONE_BREAK;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.STONE_STEP, 1.0f, 0.5f);
    }

    @Override
    protected float getSoundVolume() {
        return 0.6f;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Awake", this.isAwake());
        output.putFloat("StatueYaw", this.statueYaw);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(AWAKE, input.getBooleanOr("Awake", false));
        this.statueYaw = input.getFloatOr("StatueYaw", this.getYRot());
        this.yawSet = true;
    }
}
