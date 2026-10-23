package kz.ezhelgi.entity.cave;

import kz.ezhelgi.entity.ModEntities;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
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
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * Шойынқұлақ — великан с чугунными ушами, хозяин подземного царства из сказки об Ер Төстіке.
 * Хлопает ушами так, что земля дрожит и всех оглушает, швыряет валуны,
 * бьёт кулаком о землю, а когда ранен — зовёт пещерных змей.
 */
public class Shoiynqulaq extends Monster {
    private final ServerBossEvent bossBar = new ServerBossEvent(UUID.randomUUID(),
            Component.translatable("entity.ezhelgi.shoiynqulaq"), BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.NOTCHED_10);
    private int clapCooldown = 100;
    private int throwCooldown = 80;
    private int clapWindup;
    private boolean summoned;

    public Shoiynqulaq(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 400;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 500.0)
                .add(Attributes.ATTACK_DAMAGE, 16.0)
                .add(Attributes.ARMOR, 14.0)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.26)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.STEP_HEIGHT, 2.0)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 5.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 20.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
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

    /** Поднятые для хлопка уши (для анимации). */
    public boolean isClapping() {
        return this.clapWindup > 0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        if (this.walkAnimation.speed() > 0.3f && this.tickCount % 10 == 0) {
            this.playSound(net.minecraft.sounds.SoundEvents.RAVAGER_STEP, 1.5f, 0.5f);
        }
        LivingEntity target = this.getTarget();
        if (this.clapWindup > 0) {
            this.getNavigation().stop();
            if (--this.clapWindup == 0) {
                clap(level);
            }
            return;
        }
        if (target == null) {
            return;
        }
        double dist = this.distanceTo(target);
        if (--this.clapCooldown <= 0 && dist < 10) {
            this.clapCooldown = 160 + this.getRandom().nextInt(80);
            this.clapWindup = 20;
            this.playSound(ModSounds.SHOIYN_ROAR.get(), 3.0f, 0.6f);
        } else if (--this.throwCooldown <= 0 && dist > 7 && dist < 28 && this.hasLineOfSight(target)) {
            this.throwCooldown = 70 + this.getRandom().nextInt(50);
            throwBoulder(level, target);
        }
    }

    /** Хлопок чугунными ушами: оглушает и отбрасывает всех рядом. */
    private void clap(ServerLevel level) {
        this.playSound(ModSounds.SHOIYN_CLAP.get(), 4.0f, 0.8f);
        level.sendParticles(ParticleTypes.SONIC_BOOM, this.getX(), this.getY() + 3.5, this.getZ(), 1, 0, 0, 0, 0);
        for (int i = 0; i < 48; i++) {
            double a = i * Math.PI * 2 / 48;
            level.sendParticles(ParticleTypes.CLOUD, this.getX() + Math.cos(a) * 4, this.getY() + 0.5, this.getZ() + Math.sin(a) * 4,
                    1, 0, 0, 0, 0.05);
        }
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(9.0, 3.0, 9.0),
                e -> e != this && e.isAlive() && !(e instanceof Zhylan))) {
            Vec3 d = victim.position().subtract(this.position()).multiply(1, 0, 1);
            double len = Math.max(0.5, d.length());
            victim.hurtServer(level, this.damageSources().mobAttack(this), (float) Math.max(4.0, 12.0 - len));
            victim.setDeltaMovement(d.x / len * 1.2, 0.6, d.z / len * 1.2);
            victim.needsSync = true;
            victim.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 140, 0), this);
            victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 1), this);
        }
    }

    /** Швыряет валун (падающий блок булыжника). */
    private void throwBoulder(ServerLevel level, LivingEntity target) {
        this.swingForAttack(net.minecraft.world.InteractionHand.MAIN_HAND);
        BlockPos start = BlockPos.containing(this.getX(), this.getY() + 4.5, this.getZ());
        if (!level.getBlockState(start).isAir()) {
            return;
        }
        FallingBlockEntity rock = FallingBlockEntity.fall(level, start, Blocks.COBBLESTONE.defaultBlockState());
        Vec3 d = target.position().subtract(this.position());
        double flat = Math.sqrt(d.x * d.x + d.z * d.z);
        double speed = 0.9 + flat * 0.035;
        rock.setDeltaMovement(d.x / flat * speed, 0.45 + flat * 0.02, d.z / flat * speed);
        rock.setHurtsEntities(2.0f, 14);
        rock.disableDrop();
        this.playSound(net.minecraft.sounds.SoundEvents.RAVAGER_ROAR, 2.0f, 0.6f);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        boolean hurt = super.hurtServer(level, source, amount);
        if (hurt && !this.summoned && this.getHealth() < this.getMaxHealth() * 0.5f) {
            this.summoned = true;
            for (int i = 0; i < 4; i++) {
                Zhylan snake = ModEntities.ZHYLAN.get().create(level, EntitySpawnReason.MOB_SUMMONED);
                if (snake != null) {
                    snake.snapTo(this.getX() + this.getRandom().nextGaussian() * 3, this.getY(), this.getZ() + this.getRandom().nextGaussian() * 3);
                    level.addFreshEntity(snake);
                }
            }
            this.bossBar.setColor(BossEvent.BossBarColor.RED);
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.IRON_BLOCK.defaultBlockState()),
                    this.getX(), this.getY() + 3, this.getZ(), 40, 1, 1, 1, 0.1);
        }
        return hurt;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hit = super.doHurtTarget(level, target);
        if (hit) {
            Vec3 d = target.position().subtract(this.position()).multiply(1, 0, 1).normalize();
            target.setDeltaMovement(d.x * 1.0, 0.9, d.z * 1.0);
            target.needsSync = true;
        }
        return hit;
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return false;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.SHOIYN_ROAR.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SHOIYN_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.SHOIYN_DEATH.get();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Summoned", this.summoned);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.summoned = input.getBooleanOr("Summoned", false);
    }
}
