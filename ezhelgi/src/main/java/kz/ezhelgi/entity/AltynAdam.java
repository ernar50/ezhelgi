package kz.ezhelgi.entity;

import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * Алтын Адам — босс, повелитель сакских курганов в золотой маске.
 * Выше человека, с полоской здоровья как у визера. Умеет:
 * <ul>
 *   <li>серии из трёх быстрых ударов кылышом;</li>
 *   <li>закрываться щитом (удары спереди не проходят);</li>
 *   <li>прыгать и бить о землю — золотая ударная волна отбрасывает и замедляет;</li>
 *   <li>во второй фазе (меньше половины здоровья) — молнии и призыв золотых стражей.</li>
 * </ul>
 */
public class AltynAdam extends Monster {
    private final ServerBossEvent bossBar = new ServerBossEvent(UUID.randomUUID(),
            net.minecraft.network.chat.Component.translatable("entity.ezhelgi.altyn_adam"),
            BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_10);

    private boolean prepared = false;
    private boolean phaseTwo = false;
    private int shieldTicks = 0;
    private int shieldCooldown = 60;
    private int comboHits = 0;
    private int comboTimer = 0;
    private int comboCooldown = 40;
    private int slamCooldown = 140;
    private boolean slamming = false;
    private int lightningCooldown = 120;

    public AltynAdam(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 120;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 320.0)
                .add(Attributes.ATTACK_DAMAGE, 11.0)
                .add(Attributes.ARMOR, 14.0)
                .add(Attributes.ARMOR_TOUGHNESS, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    // ---------------------------------------------------------------- полоска здоровья
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

    // ---------------------------------------------------------------- бой
    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (!this.prepared) {
            this.prepared = true;
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.ALTYN_BETPERDE.get()));
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.ALTYN_QYLYSH.get()));
            this.setItemSlot(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
        }
        this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        if (this.isDeadOrDying()) {
            return;
        }

        // золотое сияние
        if (this.tickCount % 4 == 0) {
            level.sendParticles(ParticleTypes.WAX_ON, this.getX(), this.getY() + 1.6, this.getZ(), 3, 0.5, 0.9, 0.5, 0.0);
        }

        LivingEntity target = this.getTarget();
        this.shieldCooldown--;
        this.comboCooldown--;
        this.slamCooldown--;
        this.lightningCooldown--;

        // щит
        if (this.shieldTicks > 0) {
            this.shieldTicks--;
            if (this.shieldTicks == 0) {
                this.stopUsingItem();
            }
        } else if (target != null && this.shieldCooldown <= 0 && this.comboHits == 0 && this.distanceTo(target) < 9.0f
                && this.getRandom().nextInt(20) == 0) {
            this.startUsingItem(InteractionHand.OFF_HAND);
            this.shieldTicks = 35;
            this.shieldCooldown = 110;
        }

        // серия быстрых ударов
        if (this.comboHits > 0) {
            if (--this.comboTimer <= 0 && target != null) {
                this.comboTimer = 5;
                this.comboHits--;
                this.swing(InteractionHand.MAIN_HAND);
                if (this.distanceTo(target) < 4.0f) {
                    target.hurtServer(level, this.damageSources().mobAttack(this), 6.0f);
                }
                this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0f, 0.8f + this.getRandom().nextFloat() * 0.3f);
                level.sendParticles(ParticleTypes.SWEEP_ATTACK, target.getX(), target.getY() + 1.0, target.getZ(), 1, 0, 0, 0, 0);
            }
        } else if (target != null && this.shieldTicks == 0 && this.comboCooldown <= 0 && this.distanceTo(target) < 3.6f) {
            this.comboHits = 3;
            this.comboTimer = 0;
            this.comboCooldown = this.phaseTwo ? 45 : 70;
        }

        // прыжок и удар о землю
        if (this.slamming && this.onGround() && this.getDeltaMovement().y <= 0.0) {
            this.slamming = false;
            this.slam(level);
        } else if (!this.slamming && target != null && this.slamCooldown <= 0 && this.onGround()
                && this.distanceTo(target) < 11.0f && this.shieldTicks == 0) {
            this.slamming = true;
            this.slamCooldown = this.phaseTwo ? 120 : 180;
            Vec3 dir = target.position().subtract(this.position()).normalize().scale(0.55);
            this.setDeltaMovement(dir.x, 1.0, dir.z);
            this.playSound(ModSounds.BOSS_ROAR.get(), 2.0f, 1.0f);
        }

        // вторая фаза
        if (!this.phaseTwo && this.getHealth() < this.getMaxHealth() * 0.5f) {
            this.phaseTwo = true;
            this.bossBar.setColor(BossEvent.BossBarColor.RED);
            this.playSound(ModSounds.BOSS_ROAR.get(), 3.0f, 0.7f);
            this.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 20 * 600, 0));
            this.addEffect(new MobEffectInstance(MobEffects.SPEED, 20 * 600, 0));
            for (int i = 0; i < 2; i++) {
                AltynJauynger guard = ModEntities.ALTYN_JAUYNGER.get().create(level, EntitySpawnReason.EVENT);
                if (guard != null) {
                    double a = i * Math.PI + this.getRandom().nextDouble();
                    guard.setPos(this.getX() + Math.cos(a) * 3.0, this.getY(), this.getZ() + Math.sin(a) * 3.0);
                    level.addFreshEntity(guard);
                    level.sendParticles(ParticleTypes.EXPLOSION, guard.getX(), guard.getY() + 1.0, guard.getZ(), 2, 0.3, 0.5, 0.3, 0.0);
                }
            }
        }
        if (this.phaseTwo && target != null && this.lightningCooldown <= 0) {
            this.lightningCooldown = 140;
            LightningBolt bolt = new LightningBolt(EntityTypes.LIGHTNING_BOLT, level);
            bolt.setPos(target.getX() + (this.getRandom().nextDouble() - 0.5) * 2.0, target.getY(),
                    target.getZ() + (this.getRandom().nextDouble() - 0.5) * 2.0);
            level.addFreshEntity(bolt);
        }
    }

    /** Золотая ударная волна. */
    private void slam(ServerLevel level) {
        this.playSound(ModSounds.BOSS_SLAM.get(), 2.5f, 0.8f);
        for (int i = 0; i < 36; i++) {
            double a = i * Math.PI * 2.0 / 36.0;
            for (double r = 1.5; r <= 6.0; r += 1.5) {
                level.sendParticles(ParticleTypes.FLAME, this.getX() + Math.cos(a) * r, this.getY() + 0.1,
                        this.getZ() + Math.sin(a) * r, 1, 0.0, 0.05, 0.0, 0.01);
            }
            level.sendParticles(ParticleTypes.WAX_ON, this.getX() + Math.cos(a) * 3.0, this.getY() + 0.5,
                    this.getZ() + Math.sin(a) * 3.0, 2, 0.2, 0.3, 0.2, 0.0);
        }
        level.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY(), this.getZ(), 1, 0, 0, 0, 0);
        List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(7.0, 3.0, 7.0),
                e -> e != this && !(e instanceof AltynJauynger) && e.isAlive());
        for (LivingEntity victim : victims) {
            double dx = victim.getX() - this.getX();
            double dz = victim.getZ() - this.getZ();
            double dist = Math.max(0.5, Math.sqrt(dx * dx + dz * dz));
            victim.hurtServer(level, this.damageSources().mobAttack(this), (float) Mth.clamp(12.0 - dist, 4.0, 10.0));
            victim.setDeltaMovement(victim.getDeltaMovement().add(dx / dist * 1.4, 0.6, dz / dist * 1.4));
            victim.hurtMarked = true;
            victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 2));
        }
    }

    /** Щит: пока поднят, удары спереди не проходят. */
    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (this.shieldTicks > 0 && !source.is(DamageTypeTags.BYPASSES_SHIELD)) {
            Entity attacker = source.getDirectEntity();
            if (attacker != null) {
                Vec3 look = this.getViewVector(1.0f);
                Vec3 toAttacker = attacker.position().subtract(this.position()).normalize();
                if (look.x * toAttacker.x + look.z * toAttacker.z > 0.0) {
                    this.playSound(SoundEvents.ANVIL_LAND, 0.8f, 1.6f);
                    level.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + 1.4, this.getZ(), 10, 0.4, 0.4, 0.4, 0.1);
                    return false;
                }
            }
        }
        return super.hurtServer(level, source, amount);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.BOSS_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BOSS_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.BOSS_DEATH.get();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Prepared", this.prepared);
        output.putBoolean("PhaseTwo", this.phaseTwo);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.prepared = input.getBooleanOr("Prepared", false);
        this.phaseTwo = input.getBooleanOr("PhaseTwo", false);
        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }
    }
}
