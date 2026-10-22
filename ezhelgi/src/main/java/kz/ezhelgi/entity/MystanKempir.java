package kz.ezhelgi.entity;

import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Мыстан кемпір — хитрая ведьма-оборотень. Насылает порчу издалека,
 * а когда её прижмут — оборачивается рыжей лисой и убегает,
 * чтобы потом снова принять облик старухи.
 */
public class MystanKempir extends Monster implements Interruptible {
    private int curseCooldown = 40;
    private int shiftCooldown = 300;

    public MystanKempir(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 28.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void interrupt() {
        this.curseCooldown = 80;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (this.tickCount % 5 == 0) {
            level.sendParticles(ParticleTypes.WITCH, this.getX(), this.getY() + 1.4, this.getZ(), 1, 0.3, 0.4, 0.3, 0.0);
        }
        LivingEntity target = this.getTarget();
        if (target == null) {
            return;
        }
        if (--this.curseCooldown <= 0 && this.hasLineOfSight(target) && this.distanceTo(target) < 16.0f) {
            this.curseCooldown = 70 + this.getRandom().nextInt(40);
            this.curse(level, target);
        }
        if (--this.shiftCooldown <= 0 || (this.getHealth() < this.getMaxHealth() * 0.4f && this.shiftCooldown < 200)) {
            this.shiftCooldown = 600;
            this.becomeFox(level);
        }
    }

    private void curse(ServerLevel level, LivingEntity target) {
        Vec3 from = this.getEyePosition();
        Vec3 to = target.position().add(0, target.getBbHeight() * 0.6, 0);
        Vec3 step = to.subtract(from);
        int n = (int) (step.length() * 3);
        for (int i = 0; i <= n; i++) {
            Vec3 p = from.add(step.scale(i / (double) n));
            level.sendParticles(ParticleTypes.WITCH, p.x, p.y, p.z, 1, 0.03, 0.03, 0.03, 0.0);
        }
        this.playSound(ModSounds.KEMPIR_BOLT.get(), 1.4f, 1.3f);
        target.hurtServer(level, this.damageSources().indirectMagic(this, this), 4.0f);
        target.addEffect(new MobEffectInstance(this.getRandom().nextBoolean() ? MobEffects.POISON : MobEffects.WEAKNESS, 100, 0));
    }

    /** Обернуться лисой. */
    private void becomeFox(ServerLevel level) {
        MystanTulki fox = ModEntities.MYSTAN_TULKI.get().create(level, EntitySpawnReason.CONVERSION);
        if (fox == null) return;
        fox.setPos(this.getX(), this.getY(), this.getZ());
        fox.setWitchHealth(this.getHealth());
        level.addFreshEntity(fox);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 30, 0.4, 0.8, 0.4, 0.03);
        level.sendParticles(ParticleTypes.WITCH, this.getX(), this.getY() + 1.0, this.getZ(), 30, 0.4, 0.8, 0.4, 0.1);
        this.playSound(ModSounds.KEMPIR_LAUGH.get(), 1.6f, 1.3f);
        this.discard();
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.KEMPIR_LAUGH.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.KEMPIR_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.KEMPIR_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return 1.25f;
    }
}
