package kz.ezhelgi.entity;

import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

/**
 * Жезтырнақ — злой дух в облике красивой девушки с медным носом и медными когтями.
 * Пока никого не заметила — бродит спокойно, когти спрятаны. Подпустив игрока близко,
 * сбрасывает облик: когти удлиняются, она визжит, далеко прыгает, бьёт и отскакивает,
 * а потом нападает снова.
 */
public class Zheztyrnak extends Monster implements Interruptible {
    private static final EntityDataAccessor<Boolean> REVEALED =
            SynchedEntityData.defineId(Zheztyrnak.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> STRIKE =
            SynchedEntityData.defineId(Zheztyrnak.class, EntityDataSerializers.INT);
    /** Цвет меди для частиц. */
    public static final DustParticleOptions COPPER = new DustParticleOptions(0xD0743C, 1.2f);
    public static final DustParticleOptions COPPER_SMALL = new DustParticleOptions(0xE79A5A, 0.7f);

    /** Насколько выдвинуты когти (только на клиенте, для плавной анимации). */
    public float claws;
    public float clawsO;
    private int calmTicks;
    private int stunTicks;

    public Zheztyrnak(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 15;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 36.0)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.ARMOR, 2.0)
                .add(Attributes.MOVEMENT_SPEED, 0.33)
                .add(Attributes.FOLLOW_RANGE, 28.0)
                .add(Attributes.SAFE_FALL_DISTANCE, 12.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(REVEALED, false);
        builder.define(STRIKE, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new HitAndRunGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // притворяется безобидной: замечает игрока только вблизи
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                (target, level) -> target.distanceToSqr(this) < 7.0 * 7.0));
    }

    public boolean isRevealed() {
        return this.entityData.get(REVEALED);
    }

    /** Тики «удара когтями» (для анимации длинного выпада). */
    public int strikeTicks() {
        return this.entityData.get(STRIKE);
    }

    @Override
    public void interrupt() {
        this.stunTicks = 30;
        this.getNavigation().stop();
        this.setDeltaMovement(this.getDeltaMovement().multiply(0.2, 1.0, 0.2));
    }

    @Override
    public void tick() {
        super.tick();
        this.clawsO = this.claws;
        float want = this.isRevealed() ? 1.0f : 0.0f;
        this.claws += (want - this.claws) * 0.15f;

        if (this.level() instanceof ServerLevel level) {
            int strike = this.entityData.get(STRIKE);
            if (strike > 0) {
                this.entityData.set(STRIKE, strike - 1);
            }
            if (this.stunTicks > 0) {
                this.stunTicks--;
            }
            LivingEntity target = this.getTarget();
            if (target != null && target.isAlive()) {
                this.calmTicks = 0;
                if (!this.isRevealed()) {
                    this.reveal(level);
                }
            } else if (this.isRevealed() && ++this.calmTicks > 200) {
                // снова притворяется девушкой
                this.entityData.set(REVEALED, false);
                level.sendParticles(COPPER_SMALL, this.getX(), this.getY() + 1.2, this.getZ(), 12, 0.3, 0.6, 0.3, 0.0);
            }
            if (this.isRevealed() && this.tickCount % 3 == 0) {
                // медные искры с когтей
                Vec3 side = Vec3.directionFromRotation(0.0f, this.yBodyRot + 90.0f).scale(0.45);
                level.sendParticles(COPPER_SMALL, this.getX() + side.x, this.getY() + 0.6, this.getZ() + side.z, 1, 0.05, 0.05, 0.05, 0.0);
                level.sendParticles(COPPER_SMALL, this.getX() - side.x, this.getY() + 0.6, this.getZ() - side.z, 1, 0.05, 0.05, 0.05, 0.0);
            }
            if (!this.onGround() && this.isRevealed() && this.getDeltaMovement().horizontalDistanceSqr() > 0.2) {
                level.sendParticles(COPPER, this.getX(), this.getY() + 0.9, this.getZ(), 2, 0.2, 0.3, 0.2, 0.0);
            }
        }
    }

    private void reveal(ServerLevel level) {
        this.entityData.set(REVEALED, true);
        this.playSound(ModSounds.ZHEZTYRNAK_SHRIEK.get(), 1.8f, 1.0f);
        level.sendParticles(COPPER, this.getX(), this.getY() + 1.0, this.getZ(), 40, 0.4, 0.8, 0.4, 0.05);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 15, 0.3, 0.6, 0.3, 0.02);
        LivingEntity target = this.getTarget();
        if (target != null) {
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 30, 1));
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hit = super.doHurtTarget(level, target);
        this.entityData.set(STRIKE, 8);
        this.playSound(ModSounds.ZHEZTYRNAK_CLAW.get(), 1.2f, 0.9f + this.getRandom().nextFloat() * 0.3f);
        Vec3 look = this.getViewVector(1.0f);
        level.sendParticles(ParticleTypes.SWEEP_ATTACK, this.getX() + look.x * 1.2, this.getY() + 1.1, this.getZ() + look.z * 1.2,
                1, 0.0, 0.0, 0.0, 0.0);
        level.sendParticles(COPPER, target.getX(), target.getY() + 1.0, target.getZ(), 14, 0.3, 0.4, 0.3, 0.1);
        if (hit && target instanceof LivingEntity living) {
            // глубокие раны медными когтями
            living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
        }
        return hit;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return this.isRevealed() ? ModSounds.ZHEZTYRNAK_ANGRY.get() : ModSounds.ZHEZTYRNAK_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ZHEZTYRNAK_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.ZHEZTYRNAK_DEATH.get();
    }

    /** Удар — отскок — прыжок — снова удар. */
    static class HitAndRunGoal extends Goal {
        private final Zheztyrnak mob;
        private int retreatTicks;
        private int leapCooldown = 20;
        private int attackCooldown;
        private int hits;
        private int pathDelay;

        HitAndRunGoal(Zheztyrnak mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.mob.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void stop() {
            this.mob.getNavigation().stop();
            this.retreatTicks = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target == null || !(this.mob.level() instanceof ServerLevel level)) {
                return;
            }
            if (this.leapCooldown > 0) this.leapCooldown--;
            if (this.attackCooldown > 0) this.attackCooldown--;
            if (this.mob.stunTicks > 0) {
                return;
            }
            double dist = this.mob.distanceTo(target);

            if (this.retreatTicks > 0) {
                // отступает, не сводя глаз с жертвы
                this.retreatTicks--;
                this.mob.getLookControl().setLookAt(target, 30.0f, 30.0f);
                Vec3 away = this.mob.position().subtract(target.position()).normalize();
                if (--this.pathDelay <= 0) {
                    this.pathDelay = 8;
                    Vec3 dest = this.mob.position().add(away.scale(7.0));
                    this.mob.getNavigation().moveTo(dest.x, dest.y, dest.z, 1.55);
                }
                if (this.retreatTicks == 0) {
                    this.leapCooldown = Math.min(this.leapCooldown, 10);
                }
                return;
            }

            this.mob.getLookControl().setLookAt(target, 30.0f, 30.0f);
            if (this.mob.onGround() && this.leapCooldown <= 0 && dist > 4.0 && dist < 15.0
                    && this.mob.hasLineOfSight(target)) {
                // длинный прыжок к жертве
                Vec3 dir = target.position().subtract(this.mob.position());
                Vec3 flat = new Vec3(dir.x, 0.0, dir.z).normalize();
                double power = Math.min(1.9, 0.45 + dist * 0.11);
                this.mob.setDeltaMovement(flat.x * power, 0.42 + Math.min(0.35, dist * 0.025) + Math.max(0.0, dir.y * 0.08), flat.z * power);
                this.mob.needsSync = true;
                this.leapCooldown = 50 + this.mob.getRandom().nextInt(40);
                this.mob.playSound(ModSounds.ZHEZTYRNAK_LEAP.get(), 1.4f, 1.0f);
                level.sendParticles(ParticleTypes.CLOUD, this.mob.getX(), this.mob.getY() + 0.1, this.mob.getZ(), 10, 0.4, 0.05, 0.4, 0.05);
                level.sendParticles(COPPER, this.mob.getX(), this.mob.getY() + 0.5, this.mob.getZ(), 12, 0.3, 0.3, 0.3, 0.05);
                return;
            }
            if (--this.pathDelay <= 0) {
                this.pathDelay = 5;
                this.mob.getNavigation().moveTo(target, 1.45);
            }
            double reach = this.mob.getBbWidth() * 2.0 + target.getBbWidth() + 0.9;
            if (dist <= reach && this.attackCooldown <= 0) {
                this.mob.swingForAttack(InteractionHand.MAIN_HAND);
                this.mob.doHurtTarget(level, target);
                this.attackCooldown = 14;
                this.hits++;
                if (this.hits >= 2 || this.mob.getRandom().nextInt(3) == 0) {
                    this.hits = 0;
                    this.retreatTicks = 25 + this.mob.getRandom().nextInt(25);
                    // отскок назад
                    Vec3 away = this.mob.position().subtract(target.position()).normalize();
                    this.mob.setDeltaMovement(away.x * 0.9, 0.35, away.z * 0.9);
                    this.mob.needsSync = true;
                }
            }
        }
    }
}
