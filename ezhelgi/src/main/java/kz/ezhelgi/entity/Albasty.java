package kz.ezhelgi.entity;

import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Албасты — ночной дух, живущий у воды и в тумане. Длинные спутанные волосы,
 * бледное лицо. Внезапно исчезает и появляется у жертвы за спиной.
 * Её удар наводит тьму и оцепенение. Изредка оставляет прядь волос —
 * из неё делают тұмар.
 */
public class Albasty extends Monster implements Interruptible {
    private int blinkCooldown = 60;
    private int hiddenTicks = 0;

    public Albasty(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    /** Ночью, в темноте и обязательно рядом с водой. */
    public static boolean checkSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor level, EntitySpawnReason reason,
                                          BlockPos pos, RandomSource random) {
        if (!Monster.checkMonsterSpawnRules(type, level, reason, pos, random)) {
            return false;
        }
        if (EntitySpawnReason.ignoresLightRequirements(reason)) {
            return true;
        }
        if (!level.getLevel().isDarkOutside()) {
            return false;
        }
        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-6, -3, -6), pos.offset(6, 1, 6))) {
            if (level.getFluidState(p).is(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
        this.goalSelector.addGoal(5, new RandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    @Override
    public void interrupt() {
        this.blinkCooldown = Math.max(this.blinkCooldown, 80);
        if (this.hiddenTicks > 0) {
            this.hiddenTicks = 1;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        // туман вокруг
        if (this.tickCount % 3 == 0 && !this.isInvisible()) {
            level.sendParticles(ParticleTypes.WHITE_ASH, this.getX(), this.getY() + 0.8, this.getZ(), 3, 0.6, 0.6, 0.6, 0.0);
        }
        if (this.tickCount % 7 == 0) {
            level.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY() + 0.2, this.getZ(), 1, 0.8, 0.05, 0.8, 0.0);
        }
        LivingEntity target = this.getTarget();
        if (this.blinkCooldown > 0) {
            this.blinkCooldown--;
        }
        if (this.hiddenTicks > 0) {
            // невидима: появится за спиной
            if (--this.hiddenTicks == 0) {
                this.setInvisible(false);
                if (target != null) {
                    this.appearBehind(level, target);
                }
            }
            return;
        }
        if (target != null && this.blinkCooldown <= 0 && this.distanceTo(target) < 20.0f) {
            float roll = this.getRandom().nextFloat();
            if (roll < 0.05f) {
                // исчезает в тумане
                this.blinkCooldown = 120 + this.getRandom().nextInt(80);
                this.hiddenTicks = 30 + this.getRandom().nextInt(25);
                this.setInvisible(true);
                this.getNavigation().stop();
                this.playSound(ModSounds.ALBASTY_VANISH.get(), 1.2f, 1.0f);
                level.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 25, 0.4, 0.8, 0.4, 0.02);
                level.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY() + 1.0, this.getZ(), 20, 0.5, 0.8, 0.5, 0.02);
            } else if (roll < 0.08f && this.distanceTo(target) > 5.0f) {
                this.blinkCooldown = 100 + this.getRandom().nextInt(60);
                this.appearBehind(level, target);
            }
        }
    }

    /** Телепорт за спину жертвы. */
    private void appearBehind(ServerLevel level, LivingEntity target) {
        Vec3 look = Vec3.directionFromRotation(0.0f, target.getYRot());
        double x = target.getX() - look.x * 2.2;
        double z = target.getZ() - look.z * 2.2;
        level.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 15, 0.3, 0.7, 0.3, 0.02);
        for (int dy = 2; dy >= -3; dy--) {
            BlockPos p = BlockPos.containing(x, target.getY() + dy, z);
            if (level.getBlockState(p.below()).isSolid() && level.getBlockState(p).isAir() && level.getBlockState(p.above()).isAir()) {
                this.teleportTo(x, p.getY(), z);
                this.lookAt(target, 360.0f, 360.0f);
                this.playSound(ModSounds.ALBASTY_APPEAR.get(), 1.4f, 1.0f);
                level.sendParticles(ParticleTypes.CLOUD, x, p.getY() + 1.0, z, 25, 0.4, 0.8, 0.4, 0.03);
                level.sendParticles(ParticleTypes.SCULK_SOUL, x, p.getY() + 1.4, z, 6, 0.3, 0.4, 0.3, 0.02);
                return;
            }
        }
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hit = super.doHurtTarget(level, target);
        if (hit && target instanceof LivingEntity living) {
            // «басып қалу» — оцепенение и тьма
            living.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 100, 0));
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 2));
            living.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 100, 0));
        }
        return hit;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (this.hiddenTicks > 0) {
            this.hiddenTicks = 1;
        }
        return super.hurtServer(level, source, amount);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.ALBASTY_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ALBASTY_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.ALBASTY_DEATH.get();
    }
}
