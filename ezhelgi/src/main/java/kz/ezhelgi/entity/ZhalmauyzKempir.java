package kz.ezhelgi.entity;

import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

/**
 * Жалмауыз кемпір — старая ведьма-людоедка с горы Бурабай.
 * Держится на расстоянии и колдует: чёрная молния (урон и замедление),
 * тұзақ — путы на ногах жертвы, и призыв мелкой нечисти.
 * Если подойти вплотную — отпрыгивает в облаке дыма.
 */
public class ZhalmauyzKempir extends Monster implements Interruptible {
    private static final EntityDataAccessor<Integer> CASTING =
            SynchedEntityData.defineId(ZhalmauyzKempir.class, EntityDataSerializers.INT);
    public static final DustParticleOptions HEX = new DustParticleOptions(0x5E1F8A, 1.3f);
    public static final DustParticleOptions HEX_GREEN = new DustParticleOptions(0x5FAF3A, 1.0f);

    private final ServerBossEvent bossBar = new ServerBossEvent(UUID.randomUUID(),
            Component.translatable("entity.ezhelgi.zhalmauyz_kempir"),
            BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_6);

    private boolean prepared = false;
    private int castTimer = 0;
    private int spell = 0;
    private int spellCooldown = 40;
    private int summonCooldown = 100;
    private int blinkCooldown = 0;
    private @Nullable Vec3 snareAt;

    public ZhalmauyzKempir(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 60;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 140.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.FOLLOW_RANGE, 36.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CASTING, 0);
    }

    /** 0 — не колдует, 1 — чёрная молния, 2 — путы, 3 — призыв. */
    public int castingSpell() {
        return this.entityData.get(CASTING);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new KeepDistanceGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
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

    /** Удар қамшы сбивает заклинание. */
    @Override
    public void interrupt() {
        if (this.castTimer > 0) {
            this.castTimer = 0;
            this.entityData.set(CASTING, 0);
            this.spellCooldown = 50;
            if (this.level() instanceof ServerLevel level) {
                level.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY() + 1.8, this.getZ(), 20, 0.3, 0.3, 0.3, 0.05);
            }
            this.playSound(ModSounds.KEMPIR_HURT.get(), 1.2f, 1.4f);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            if (this.castingSpell() > 0) {
                // искры в руках во время колдовства
                double a = this.yBodyRot * Math.PI / 180.0;
                for (int s = -1; s <= 1; s += 2) {
                    this.level().addParticle(this.castingSpell() == 2 ? HEX_GREEN : HEX,
                            this.getX() - Math.cos(a) * 0.5 * s, this.getY() + 2.1, this.getZ() - Math.sin(a) * 0.5 * s, 0, 0.05, 0);
                }
            }
            return;
        }
        if (!this.prepared) {
            this.prepared = true;
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STICK));
            this.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
        }
        this.bossBar.setProgress(this.getHealth() / this.getMaxHealth());
        if (this.tickCount % 4 == 0) {
            level.sendParticles(ParticleTypes.WITCH, this.getX(), this.getY() + 1.2, this.getZ(), 1, 0.3, 0.5, 0.3, 0.0);
        }
        if (this.spellCooldown > 0) this.spellCooldown--;
        if (this.summonCooldown > 0) this.summonCooldown--;
        if (this.blinkCooldown > 0) this.blinkCooldown--;

        // путы срабатывают с задержкой
        if (this.snareAt != null && this.tickCount % 2 == 0) {
            for (int i = 0; i < 10; i++) {
                double a = this.getRandom().nextDouble() * Math.PI * 2.0;
                level.sendParticles(HEX_GREEN, this.snareAt.x + Math.cos(a) * 1.4, this.snareAt.y + 0.1,
                        this.snareAt.z + Math.sin(a) * 1.4, 1, 0, 0.05, 0, 0);
            }
        }

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            if (this.castTimer > 0) {
                this.castTimer = 0;
                this.entityData.set(CASTING, 0);
            }
            return;
        }
        this.getLookControl().setLookAt(target, 30.0f, 30.0f);

        // слишком близко — отпрыгнуть в дыму
        if (this.distanceTo(target) < 3.5f && this.blinkCooldown <= 0) {
            this.blinkCooldown = 80;
            this.blinkAway(level, target);
            return;
        }

        if (this.castTimer > 0) {
            this.castTimer--;
            if (this.castTimer == 0) {
                this.release(level, target);
                this.entityData.set(CASTING, 0);
            }
            return;
        }
        if (this.spellCooldown <= 0 && this.hasLineOfSight(target)) {
            int nearbySummons = level.getEntitiesOfClass(Monster.class, this.getBoundingBox().inflate(12.0),
                    m -> m.getType() == EntityTypes.SILVERFISH || m.getType() == EntityTypes.CAVE_SPIDER).size();
            if (this.summonCooldown <= 0 && nearbySummons < 5 && this.getRandom().nextInt(3) == 0) {
                this.spell = 3;
            } else {
                this.spell = this.getRandom().nextInt(3) == 0 ? 2 : 1;
            }
            if (this.spell == 2) {
                this.snareAt = target.position();
            }
            this.castTimer = this.spell == 3 ? 30 : 20;
            this.entityData.set(CASTING, this.spell);
            this.playSound(ModSounds.KEMPIR_CAST.get(), 1.5f, this.spell == 3 ? 0.7f : 1.0f);
            this.getNavigation().stop();
        }
    }

    private void release(ServerLevel level, LivingEntity target) {
        switch (this.spell) {
            case 1 -> {
                // чёрная молния
                this.spellCooldown = 35 + this.getRandom().nextInt(20);
                Vec3 from = new Vec3(this.getX(), this.getY() + 1.9, this.getZ());
                Vec3 to = new Vec3(target.getX(), target.getY() + target.getBbHeight() * 0.6, target.getZ());
                Vec3 step = to.subtract(from);
                int n = (int) (step.length() * 3.0);
                for (int i = 0; i <= n; i++) {
                    Vec3 p = from.add(step.scale(i / (double) n));
                    level.sendParticles(HEX, p.x, p.y, p.z, 1, 0.05, 0.05, 0.05, 0.0);
                    if (i % 3 == 0) {
                        level.sendParticles(ParticleTypes.WITCH, p.x, p.y, p.z, 1, 0.0, 0.0, 0.0, 0.0);
                    }
                }
                this.playSound(ModSounds.KEMPIR_BOLT.get(), 1.6f, 1.0f);
                if (this.hasLineOfSight(target)) {
                    target.hurtServer(level, this.damageSources().indirectMagic(this, this), 6.0f);
                    target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 1));
                    level.sendParticles(ParticleTypes.SQUID_INK, to.x, to.y, to.z, 10, 0.3, 0.3, 0.3, 0.02);
                }
            }
            case 2 -> {
                // путы: кто остался в круге — увяз
                this.spellCooldown = 45 + this.getRandom().nextInt(20);
                if (this.snareAt != null) {
                    this.playSound(ModSounds.KEMPIR_SNARE.get(), 1.5f, 1.0f);
                    for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class,
                            new net.minecraft.world.phys.AABB(this.snareAt, this.snareAt).inflate(1.8, 1.5, 1.8),
                            e -> e != this && e.isAlive() && !(e instanceof Monster))) {
                        victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 70, 4));
                        victim.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
                        victim.hurtServer(level, this.damageSources().indirectMagic(this, this), 3.0f);
                    }
                    level.sendParticles(HEX_GREEN, this.snareAt.x, this.snareAt.y + 0.5, this.snareAt.z, 40, 0.8, 0.5, 0.8, 0.02);
                    level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, this.snareAt.x, this.snareAt.y + 0.5, this.snareAt.z, 20, 0.8, 0.5, 0.8, 0.0);
                }
                this.snareAt = null;
            }
            default -> {
                // призыв мелкой нечисти
                this.spellCooldown = 40;
                this.summonCooldown = 220;
                this.playSound(ModSounds.KEMPIR_SUMMON.get(), 1.8f, 1.0f);
                int count = 2 + this.getRandom().nextInt(2);
                for (int i = 0; i < count; i++) {
                    Mob minion = (this.getRandom().nextBoolean() ? EntityTypes.SILVERFISH : EntityTypes.CAVE_SPIDER)
                            .create(level, EntitySpawnReason.EVENT);
                    if (minion == null) continue;
                    double a = this.getRandom().nextDouble() * Math.PI * 2.0;
                    double x = this.getX() + Math.cos(a) * 2.0;
                    double z = this.getZ() + Math.sin(a) * 2.0;
                    minion.setPos(x, this.getY() + 0.2, z);
                    minion.setTarget(target);
                    level.addFreshEntity(minion);
                    level.sendParticles(ParticleTypes.LARGE_SMOKE, x, this.getY() + 0.5, z, 8, 0.2, 0.3, 0.2, 0.02);
                    level.sendParticles(HEX, x, this.getY() + 0.5, z, 10, 0.3, 0.4, 0.3, 0.02);
                }
            }
        }
    }

    private void blinkAway(ServerLevel level, LivingEntity target) {
        Vec3 away = this.position().subtract(target.position());
        Vec3 dir = new Vec3(away.x, 0.0, away.z).normalize();
        level.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 30, 0.4, 0.8, 0.4, 0.03);
        for (int tries = 0; tries < 8; tries++) {
            double dist = 6.0 + this.getRandom().nextDouble() * 4.0;
            double ang = (this.getRandom().nextDouble() - 0.5) * 1.6;
            double x = this.getX() + (dir.x * Math.cos(ang) - dir.z * Math.sin(ang)) * dist;
            double z = this.getZ() + (dir.x * Math.sin(ang) + dir.z * Math.cos(ang)) * dist;
            for (int dy = 3; dy >= -4; dy--) {
                BlockPos p = BlockPos.containing(x, this.getY() + dy, z);
                if (level.getBlockState(p.below()).isSolid() && level.getBlockState(p).isAir() && level.getBlockState(p.above()).isAir()) {
                    this.teleportTo(x, p.getY(), z);
                    this.playSound(ModSounds.KEMPIR_LAUGH.get(), 1.6f, 1.0f);
                    level.sendParticles(ParticleTypes.LARGE_SMOKE, x, p.getY() + 1.0, z, 20, 0.4, 0.8, 0.4, 0.02);
                    level.sendParticles(HEX, x, p.getY() + 1.0, z, 20, 0.4, 0.8, 0.4, 0.02);
                    return;
                }
            }
        }
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
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Prepared", this.prepared);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.prepared = input.getBooleanOr("Prepared", false);
        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }
    }

    /** Держится в 7–13 блоках от жертвы. */
    static class KeepDistanceGoal extends Goal {
        private final ZhalmauyzKempir mob;
        private int delay;

        KeepDistanceGoal(ZhalmauyzKempir mob) {
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
            if (target == null || this.mob.castTimer > 0 || --this.delay > 0) {
                return;
            }
            this.delay = 10;
            double d = this.mob.distanceTo(target);
            if (d > 13.0 || !this.mob.hasLineOfSight(target)) {
                this.mob.getNavigation().moveTo(target, 1.0);
            } else if (d < 7.0) {
                Vec3 away = this.mob.position().subtract(target.position()).normalize().scale(6.0);
                this.mob.getNavigation().moveTo(this.mob.getX() + away.x, this.mob.getY(), this.mob.getZ() + away.z, 1.2);
            } else {
                this.mob.getNavigation().stop();
            }
        }
    }
}
