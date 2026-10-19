package kz.ezhelgi.entity;

import kz.ezhelgi.item.KobyzItem;
import kz.ezhelgi.item.ModItems;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;

/**
 * Бақсы көлеңкесі — тень шамана-бақсы у древних камней.
 * Играет на кобызе: звуковые волны ослабляют и замедляют игрока,
 * а пока звучит музыка, духи и нежить рядом становятся сильнее.
 * Оставляет киелі қыл — мистическую струну для домбыры и кобыза.
 */
public class BaqsyKolenkesi extends Monster implements Interruptible {
    private static final EntityDataAccessor<Integer> PLAYING =
            SynchedEntityData.defineId(BaqsyKolenkesi.class, EntityDataSerializers.INT);
    public static final DustParticleOptions WAVE = new DustParticleOptions(0x8A6AE0, 1.4f);

    private boolean prepared;
    private int waveCooldown = 40;

    public BaqsyKolenkesi(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 18;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 36.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.FOLLOW_RANGE, 28.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PLAYING, 0);
    }

    /** Тики, пока звучит музыка (для анимации). */
    public int playingTicks() {
        return this.entityData.get(PLAYING);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new KeepAwayGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 14.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void interrupt() {
        this.waveCooldown = 80;
        this.entityData.set(PLAYING, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (!this.prepared) {
            this.prepared = true;
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.QOBYZ.get()));
            this.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
        }
        if (this.tickCount % 3 == 0) {
            level.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 0.2 + this.getRandom().nextDouble() * 1.6,
                    this.getZ(), 1, 0.25, 0.1, 0.25, 0.0);
        }
        int playing = this.entityData.get(PLAYING);
        if (playing > 0) {
            this.entityData.set(PLAYING, playing - 1);
            if (playing % 4 == 0) {
                level.sendParticles(ParticleTypes.NOTE, this.getX(), this.getY() + 2.2, this.getZ(), 1, 0.4, 0.2, 0.4, 1.0);
            }
            // музыка придаёт сил духам рядом
            if (playing % 20 == 0) {
                for (LivingEntity spirit : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(16.0),
                        e -> e != this && e.isAlive() && (KobyzItem.isEvil(e) || e instanceof Balbal || e instanceof BaqsyKolenkesi))) {
                    spirit.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 120, 0));
                    spirit.addEffect(new MobEffectInstance(MobEffects.SPEED, 120, 0));
                    spirit.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 120, 0));
                    level.sendParticles(WAVE, spirit.getX(), spirit.getY() + spirit.getBbHeight() * 0.7, spirit.getZ(),
                            4, 0.3, 0.3, 0.3, 0.0);
                }
            }
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        if (--this.waveCooldown <= 0 && this.distanceTo(target) < 14.0f) {
            this.waveCooldown = 70 + this.getRandom().nextInt(30);
            this.soundWave(level);
        }
    }

    /** Звуковая волна кобыза. */
    private void soundWave(ServerLevel level) {
        this.entityData.set(PLAYING, 160);
        this.swing(InteractionHand.MAIN_HAND, net.minecraft.world.item.component.SwingAnimation.DEFAULT);
        this.playSound(ModSounds.BAQSY_WAVE.get(), 2.0f, 0.8f + this.getRandom().nextFloat() * 0.2f);
        for (int ring = 1; ring <= 4; ring++) {
            double r = ring * 2.5;
            int n = 10 + ring * 8;
            for (int i = 0; i < n; i++) {
                double a = i * Math.PI * 2.0 / n;
                level.sendParticles(WAVE, this.getX() + Math.cos(a) * r, this.getY() + 1.2, this.getZ() + Math.sin(a) * r,
                        1, 0.0, 0.1, 0.0, 0.0);
            }
        }
        for (Player player : level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(10.0),
                p -> !p.isCreative() && !p.isSpectator())) {
            player.hurtServer(level, this.damageSources().indirectMagic(this, this), 3.0f);
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 0));
            player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 1));
            player.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 60, 0));
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.BAQSY_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.ARUAQ_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.ARUAQ_DEATH.get();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Prepared", this.prepared);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.prepared = input.getBooleanOr("Prepared", false);
    }

    /** Держится в 6–11 блоках от игрока. */
    static class KeepAwayGoal extends Goal {
        private final BaqsyKolenkesi mob;
        private int delay;

        KeepAwayGoal(BaqsyKolenkesi mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() != null && this.mob.getTarget().isAlive();
        }

        @Override
        public void tick() {
            LivingEntity target = this.mob.getTarget();
            if (target == null || --this.delay > 0) return;
            this.delay = 10;
            this.mob.getLookControl().setLookAt(target, 30.0f, 30.0f);
            double d = this.mob.distanceTo(target);
            if (d > 11.0) {
                this.mob.getNavigation().moveTo(target, 1.0);
            } else if (d < 6.0) {
                Vec3 away = this.mob.position().subtract(target.position()).normalize().scale(6.0);
                this.mob.getNavigation().moveTo(this.mob.getX() + away.x, this.mob.getY(), this.mob.getZ() + away.z, 1.2);
            } else {
                this.mob.getNavigation().stop();
            }
        }
    }
}
