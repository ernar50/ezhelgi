package kz.ezhelgi.entity;

import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Дәу — великан из казахских сказок. Живёт у пещер в своём лагере.
 * Бьёт огромной дубиной так, что жертва взлетает в воздух,
 * и бьёт дубиной о землю — ударная волна валит всех вокруг.
 */
public class Dau extends Monster {
    private static final BlockParticleOption ROCK = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.COBBLESTONE.defaultBlockState());
    private boolean prepared;
    private int slamCooldown = 100;
    private int slamWindup = 0;

    public Dau(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 50;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 180.0)
                .add(Attributes.ATTACK_DAMAGE, 13.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.STEP_HEIGHT, 2.0)
                .add(Attributes.ENTITY_INTERACTION_RANGE, 4.5);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.6));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (!this.prepared) {
            this.prepared = true;
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.DAU_SHOQPARY.get()));
            this.setDropChance(EquipmentSlot.MAINHAND, 0.2f);
        }
        if (this.walkAnimation.speed() > 0.3f && this.tickCount % 9 == 0) {
            this.playSound(SoundEvents.RAVAGER_STEP, 1.0f, 0.6f);
        }
        LivingEntity target = this.getTarget();
        if (this.slamCooldown > 0) this.slamCooldown--;
        if (this.slamWindup > 0) {
            this.getNavigation().stop();
            if (--this.slamWindup == 0) {
                this.slam(level);
            }
            return;
        }
        if (target != null && this.slamCooldown <= 0 && this.onGround() && this.distanceTo(target) < 8.0f) {
            this.slamWindup = 18;
            this.slamCooldown = 140 + this.getRandom().nextInt(60);
            this.playSound(ModSounds.DAU_ROAR.get(), 2.5f, 0.8f);
            this.swingForAttack(net.minecraft.world.InteractionHand.MAIN_HAND);
        }
    }

    /** Удар дубиной о землю. */
    private void slam(ServerLevel level) {
        this.playSound(ModSounds.DAU_SLAM.get(), 3.0f, 0.7f);
        for (int i = 0; i < 40; i++) {
            double a = i * Math.PI * 2.0 / 40.0;
            for (double r = 1.5; r <= 7.0; r += 1.8) {
                level.sendParticles(ROCK, this.getX() + Math.cos(a) * r, this.getY() + 0.2, this.getZ() + Math.sin(a) * r,
                        2, 0.1, 0.2, 0.1, 0.1);
            }
        }
        level.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.5, this.getZ(), 3, 1.0, 0.2, 1.0, 0.0);
        for (LivingEntity victim : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(7.0, 2.5, 7.0),
                e -> e != this && e.isAlive() && !(e instanceof Dau) && e.onGround())) {
            double dx = victim.getX() - this.getX();
            double dz = victim.getZ() - this.getZ();
            double dist = Math.max(0.5, Math.sqrt(dx * dx + dz * dz));
            float damage = (float) Mth.clamp(14.0 - dist * 1.4, 4.0, 12.0);
            DamageSource src = this.damageSources().mobAttack(this);
            victim.hurtServer(level, src, damage);
            victim.setDeltaMovement(dx / dist * 0.9, 0.75, dz / dist * 0.9);
            victim.needsSync = true;
            victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 2));
        }
    }

    /** Удар дубиной подбрасывает жертву. */
    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hit = super.doHurtTarget(level, target);
        if (hit && target instanceof LivingEntity living) {
            Vec3 dir = target.position().subtract(this.position());
            Vec3 flat = new Vec3(dir.x, 0, dir.z).normalize();
            living.setDeltaMovement(flat.x * 0.8, 1.15, flat.z * 0.8);
            living.needsSync = true;
            this.playSound(ModSounds.DAU_HIT.get(), 1.6f, 0.8f);
            level.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + 1.0, target.getZ(), 15, 0.3, 0.4, 0.3, 0.2);
        }
        return hit;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.DAU_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.DAU_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.DAU_DEATH.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.RAVAGER_STEP, 0.8f, 0.6f);
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
}
