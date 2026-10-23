package kz.ezhelgi.entity.cave;

import kz.ezhelgi.entity.HasVariant;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Жын — летающий пещерный демон. Бывает трёх видов:
 * <ul>
 *   <li>Қара жын (чёрный) — тень: ослепляет и подкрадывается за спину;</li>
 *   <li>Қызыл жын (красный) — огонь: поджигает и мечет огненные шары;</li>
 *   <li>Көк жын (синий) — стужа: замораживает и замедляет.</li>
 * </ul>
 */
public class Zhyn extends Monster implements HasVariant {
    public static final int BLACK = 0, RED = 1, BLUE = 2;
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(Zhyn.class, EntityDataSerializers.INT);
    private int skillCooldown = 60;

    public Zhyn(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl<>(this, 20, true);
        this.setNoGravity(true);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.FLYING_SPEED, 0.55)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation nav = new FlyingPathNavigation(this, level);
        nav.setCanOpenDoors(false);
        nav.setCanFloat(true);
        return nav;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.3, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomFlyingGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
    }

    @Override
    public int variant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int v) {
        this.entityData.set(VARIANT, Math.floorMod(v, 3));
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                                  EntitySpawnReason reason, @Nullable SpawnGroupData data) {
        this.setVariant(this.getRandom().nextInt(3));
        return super.finalizeSpawn(level, difficulty, reason, data);
    }

    @Override
    public boolean fireImmune() {
        return this.variant() == RED || super.fireImmune();
    }

    @Override
    public boolean causeFallDamage(double distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        // лёгкое парение вверх-вниз
        if (this.getTarget() == null) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, Math.sin(this.tickCount * 0.1) * 0.004, 0));
        }
        if (this.tickCount % 4 == 0) {
            ParticleOptions p = switch (this.variant()) {
                case RED -> ParticleTypes.FLAME;
                case BLUE -> ParticleTypes.SNOWFLAKE;
                default -> ParticleTypes.SQUID_INK;
            };
            level.sendParticles(p, this.getX(), this.getY() + 0.4, this.getZ(), 1, 0.2, 0.3, 0.2, 0.01);
        }
        LivingEntity target = this.getTarget();
        if (target == null || --this.skillCooldown > 0) {
            return;
        }
        this.skillCooldown = 80 + this.getRandom().nextInt(60);
        double dist = this.distanceTo(target);
        switch (this.variant()) {
            case RED -> {
                if (dist > 4 && dist < 20 && this.hasLineOfSight(target)) {
                    for (int i = 0; i < 3; i++) {
                        Vec3 dir = target.getEyePosition().subtract(this.getEyePosition()).normalize()
                                .add(this.getRandom().nextGaussian() * 0.08, 0, this.getRandom().nextGaussian() * 0.08);
                        SmallFireball ball = new SmallFireball(level, this, dir);
                        ball.setPos(this.getX(), this.getEyeY(), this.getZ());
                        level.addFreshEntity(ball);
                    }
                    this.playSound(ModSounds.ZHYN_AMBIENT.get(), 1.5f, 1.4f);
                }
            }
            case BLACK -> {
                // тень переносится за спину жертвы
                if (dist < 16) {
                    Vec3 behind = target.position().subtract(target.getLookAngle().multiply(1, 0, 1).normalize().scale(2.0)).add(0, 1.0, 0);
                    BlockPos bp = BlockPos.containing(behind);
                    if (level.getBlockState(bp).isAir() && level.getBlockState(bp.above()).isAir()) {
                        level.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 15, 0.3, 0.5, 0.3, 0.02);
                        this.teleportTo(behind.x, behind.y, behind.z);
                        level.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.5, this.getZ(), 15, 0.3, 0.5, 0.3, 0.02);
                        this.playSound(ModSounds.ZHYN_AMBIENT.get(), 1.5f, 0.6f);
                    }
                }
            }
            case BLUE -> {
                // ледяное дыхание: всё рядом замерзает
                if (dist < 8) {
                    for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(6.0),
                            e -> e != this && !(e instanceof Zhyn))) {
                        e.setTicksFrozen(Math.min(e.getTicksRequiredToFreeze() + 100, e.getTicksFrozen() + 120));
                        e.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 1), this);
                    }
                    level.sendParticles(ParticleTypes.SNOWFLAKE, this.getX(), this.getY() + 0.5, this.getZ(), 60, 3, 1, 3, 0.05);
                    this.playSound(ModSounds.ZHYN_AMBIENT.get(), 1.5f, 1.8f);
                }
            }
            default -> { }
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hit = super.doHurtTarget(level, target);
        if (hit && target instanceof LivingEntity living) {
            switch (this.variant()) {
                case RED -> living.igniteForSeconds(4);
                case BLUE -> {
                    living.setTicksFrozen(Math.min(living.getTicksRequiredToFreeze() + 100, living.getTicksFrozen() + 80));
                    living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 2), this);
                }
                default -> living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0), this);
            }
        }
        return hit;
    }

    public static boolean checkCaveSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor level,
                                              EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return pos.getY() < 30 && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.ZHYN_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ZHYN_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.ZHYN_DEATH.get();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Variant", this.variant());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setVariant(input.getIntOr("Variant", this.getRandom().nextInt(3)));
    }
}
