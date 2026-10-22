package kz.ezhelgi.entity;

import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * Айдаһар — змееподобный дракон гор Алатау, босс с полоской здоровья.
 * Кружит над жертвой, пикирует и кусает, дышит огнём и мечет огненные шары.
 * Когда здоровья меньше половины — свирепеет: летает быстрее и чаще жжёт.
 */
public class Aidahar extends Monster {
    private enum Phase { CIRCLE, SWOOP, BREATH, VOLLEY }

    private final ServerBossEvent bossBar = new ServerBossEvent(UUID.randomUUID(),
            Component.translatable("entity.ezhelgi.aidahar"), BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_12);

    private Phase phase = Phase.CIRCLE;
    private int phaseTicks = 0;
    private int actionCooldown = 80;
    private double circleAngle;
    private @Nullable BlockPos home;
    private boolean enraged;

    public Aidahar(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.xpReward = 300;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 400.0)
                .add(Attributes.ATTACK_DAMAGE, 14.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.FOLLOW_RANGE, 64.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FLYING_SPEED, 0.8);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public boolean causeFallDamage(double distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (this.home == null) {
            this.home = this.blockPosition();
        }
        this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        if (!this.enraged && this.getHealth() < this.getMaxHealth() * 0.5f) {
            this.enraged = true;
            this.bossBar.setColor(BossEvent.BossBarColor.RED);
            this.playSound(ModSounds.AIDAHAR_ROAR.get(), 5.0f, 0.7f);
        }
        if (this.tickCount % 3 == 0) {
            level.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 2, 0.6, 0.4, 0.6, 0.0);
        }
        LivingEntity target = this.getTarget();
        if (target != null && (!target.isAlive() || this.distanceTo(target) > 80.0f
                || (target instanceof Player p && (p.isCreative() || p.isSpectator())))) {
            this.setTarget(null);
            target = null;
        }
        double speed = this.enraged ? 0.95 : 0.75;
        Vec3 goal;
        this.phaseTicks++;
        this.setAggressive(this.phase == Phase.BREATH || this.phase == Phase.SWOOP);
        if (target == null) {
            this.phase = Phase.CIRCLE;
            this.circleAngle += 0.02;
            goal = Vec3.atCenterOf(this.home).add(Math.cos(this.circleAngle) * 22.0, 14.0 + Math.sin(this.circleAngle * 2) * 3.0,
                    Math.sin(this.circleAngle) * 22.0);
            speed = 0.5;
        } else {
            switch (this.phase) {
                case SWOOP -> {
                    goal = target.position().add(0, target.getBbHeight() * 0.5, 0);
                    speed *= 1.6;
                    if (this.distanceTo(target) < 4.0f) {
                        this.doHurtTarget(level, target);
                        target.setDeltaMovement(target.getDeltaMovement().add(0, 0.8, 0));
                        target.needsSync = true;
                        this.playSound(ModSounds.AIDAHAR_BITE.get(), 3.0f, 0.8f);
                        this.setPhase(Phase.CIRCLE);
                    } else if (this.phaseTicks > 80) {
                        this.setPhase(Phase.CIRCLE);
                    }
                }
                case BREATH -> {
                    Vec3 away = this.position().subtract(target.position());
                    Vec3 flat = new Vec3(away.x, 0, away.z).normalize().scale(10.0);
                    goal = target.position().add(flat).add(0, 6.0, 0);
                    speed *= 0.5;
                    if (this.phaseTicks > 15 && this.phaseTicks % 2 == 0) {
                        this.breathe(level, target);
                    }
                    if (this.phaseTicks > 70) {
                        this.setPhase(Phase.CIRCLE);
                    }
                }
                case VOLLEY -> {
                    Vec3 away = this.position().subtract(target.position());
                    Vec3 flat = new Vec3(away.x, 0, away.z).normalize().scale(16.0);
                    goal = target.position().add(flat).add(0, 10.0, 0);
                    if (this.phaseTicks % 12 == 0 && this.phaseTicks <= 48) {
                        Vec3 dir = target.position().add(0, 1.0, 0).subtract(this.getEyePosition()).normalize();
                        SmallFireball ball = new SmallFireball(level, this, dir.scale(1.2));
                        ball.setPos(this.getX() + dir.x * 2.5, this.getEyeY(), this.getZ() + dir.z * 2.5);
                        level.addFreshEntity(ball);
                        this.playSound(ModSounds.AIDAHAR_FIRE.get(), 3.0f, 1.0f);
                    }
                    if (this.phaseTicks > 60) {
                        this.setPhase(Phase.CIRCLE);
                    }
                }
                default -> {
                    this.circleAngle += this.enraged ? 0.05 : 0.035;
                    goal = target.position().add(Math.cos(this.circleAngle) * 14.0, 10.0 + Math.sin(this.circleAngle * 3) * 2.0,
                            Math.sin(this.circleAngle) * 14.0);
                    if (--this.actionCooldown <= 0) {
                        this.actionCooldown = (this.enraged ? 50 : 80) + this.getRandom().nextInt(40);
                        int r = this.getRandom().nextInt(3);
                        this.setPhase(r == 0 ? Phase.SWOOP : r == 1 ? Phase.BREATH : Phase.VOLLEY);
                        this.playSound(ModSounds.AIDAHAR_ROAR.get(), 4.0f, 0.9f + this.getRandom().nextFloat() * 0.2f);
                    }
                }
            }
        }
        // плавный полёт к цели
        Vec3 to = goal.subtract(this.position());
        double dist = to.length();
        Vec3 vel = this.getDeltaMovement();
        if (dist > 0.5) {
            Vec3 wanted = to.scale(1.0 / dist).scale(Math.min(speed, dist * 0.12));
            vel = vel.add(wanted.subtract(vel).scale(0.12));
        } else {
            vel = vel.scale(0.8);
        }
        this.setDeltaMovement(vel);
        this.needsSync = true;
        if (vel.horizontalDistanceSqr() > 0.002) {
            float yaw = (float) (Mth.atan2(vel.z, vel.x) * (180.0 / Math.PI)) - 90.0f;
            this.setYRot(Mth.approachDegrees(this.getYRot(), yaw, 8.0f));
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
        }
        if (target != null && (this.phase == Phase.BREATH || this.phase == Phase.VOLLEY)) {
            this.getLookControl().setLookAt(target, 30.0f, 30.0f);
            Vec3 d = target.position().subtract(this.position());
            this.setYRot((float) (Mth.atan2(d.z, d.x) * (180.0 / Math.PI)) - 90.0f);
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();
        }
    }

    private void setPhase(Phase phase) {
        this.phase = phase;
        this.phaseTicks = 0;
    }

    /** Огненное дыхание: конус огня перед пастью. */
    private void breathe(ServerLevel level, LivingEntity target) {
        Vec3 mouth = this.getEyePosition();
        Vec3 dir = target.position().add(0, 1.0, 0).subtract(mouth).normalize();
        for (int i = 1; i <= 14; i++) {
            Vec3 p = mouth.add(dir.scale(i * 0.9));
            double spread = i * 0.08;
            level.sendParticles(ParticleTypes.FLAME, p.x, p.y, p.z, 4, spread, spread, spread, 0.02);
            if (i % 3 == 0) {
                level.sendParticles(ParticleTypes.LARGE_SMOKE, p.x, p.y, p.z, 1, spread, spread, spread, 0.01);
            }
        }
        if (this.phaseTicks % 6 == 0) {
            this.playSound(ModSounds.AIDAHAR_FIRE.get(), 3.0f, 0.8f);
        }
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(14.0),
                e -> e != this && e.isAlive())) {
            Vec3 to = victim.position().add(0, victim.getBbHeight() * 0.5, 0).subtract(mouth);
            double d = to.length();
            if (d < 13.0 && to.normalize().dot(dir) > 0.88) {
                victim.hurtServer(level, this.damageSources().mobAttack(this), this.enraged ? 4.0f : 3.0f);
                victim.igniteForSeconds(4.0f);
            }
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.AIDAHAR_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.AIDAHAR_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.AIDAHAR_DEATH.get();
    }

    @Override
    protected float getSoundVolume() {
        return 4.0f;
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        if (this.home != null) {
            output.putInt("HomeX", this.home.getX());
            output.putInt("HomeY", this.home.getY());
            output.putInt("HomeZ", this.home.getZ());
        }
        output.putBoolean("Enraged", this.enraged);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        if (input.getIntOr("HomeY", Integer.MIN_VALUE) != Integer.MIN_VALUE) {
            this.home = new BlockPos(input.getIntOr("HomeX", 0), input.getIntOr("HomeY", 0), input.getIntOr("HomeZ", 0));
        }
        this.enraged = input.getBooleanOr("Enraged", false);
    }
}
