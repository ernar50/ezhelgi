package kz.ezhelgi.entity;

import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
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
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Батыр аруағы — призрак древнего воина. Приходит ночью, вооружён призрачным
 * мечом и умеет отбивать часть ударов. На рассвете растворяется.
 * После гибели оставляет сломанный меч, из которого куётся «Аруақ қылышы».
 */
public class BatyrAruagy extends Monster implements Interruptible {
    private boolean prepared = false;
    private int parryCooldown = 0;

    public BatyrAruagy(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 20;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 44.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5);
    }

    /** Появляется только ночью, в темноте. */
    public static boolean checkSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor level, EntitySpawnReason reason,
                                          BlockPos pos, RandomSource random) {
        return level.getLevel().isDarkOutside() && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 12.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Jungar.class, true));
    }

    @Override
    public void interrupt() {
        this.parryCooldown = 60;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (!this.prepared) {
            this.prepared = true;
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.ARUAQ_SPECTRAL.get()));
            this.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
        }
        if (this.parryCooldown > 0) {
            this.parryCooldown--;
        }
        if (this.tickCount % 2 == 0) {
            level.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 0.3 + this.getRandom().nextDouble() * 1.4, this.getZ(),
                    1, 0.25, 0.1, 0.25, 0.01);
        }
        if (this.tickCount % 5 == 0) {
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 0.1, this.getZ(), 1, 0.3, 0.0, 0.3, 0.0);
        }
        // на рассвете дух уходит
        if (level.isBrightOutside() && this.tickCount % 20 == 0 && level.canSeeSky(this.blockPosition())
                && !this.isPersistenceRequired() && this.getRandom().nextInt(6) == 0) {
            level.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 1.0, this.getZ(), 30, 0.4, 0.8, 0.4, 0.05);
            this.playSound(ModSounds.ARUAQ_VANISH.get(), 1.0f, 1.0f);
            this.discard();
        }
    }

    /** Парирование: треть ударов спереди отбивается призрачным мечом. */
    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        Entity attacker = source.getDirectEntity();
        if (attacker != null && this.parryCooldown <= 0 && !source.is(DamageTypeTags.BYPASSES_ARMOR)
                && this.getRandom().nextFloat() < 0.35f) {
            Vec3 look = this.getViewVector(1.0f);
            Vec3 to = attacker.position().subtract(this.position()).normalize();
            if (look.x * to.x + look.z * to.z > 0.2) {
                this.parryCooldown = 25;
                this.playSound(ModSounds.ARUAQ_PARRY.get(), 1.2f, 0.9f + this.getRandom().nextFloat() * 0.3f);
                level.sendParticles(ParticleTypes.ELECTRIC_SPARK, this.getX() + to.x * 0.7, this.getY() + 1.3, this.getZ() + to.z * 0.7,
                        14, 0.2, 0.2, 0.2, 0.3);
                level.sendParticles(ParticleTypes.SOUL, this.getX() + to.x * 0.7, this.getY() + 1.3, this.getZ() + to.z * 0.7,
                        6, 0.2, 0.2, 0.2, 0.05);
                this.swingForAttack(net.minecraft.world.InteractionHand.MAIN_HAND);
                // отбитый удар частично возвращается
                if (attacker instanceof LivingEntity living && this.distanceTo(living) < 4.0f) {
                    living.knockback(0.6, -to.x, -to.z, source, amount);
                }
                return false;
            }
        }
        return super.hurtServer(level, source, amount * 0.85f);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hit = super.doHurtTarget(level, target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 40, 0));
            level.sendParticles(ParticleTypes.SOUL, target.getX(), target.getY() + 1.0, target.getZ(), 8, 0.3, 0.4, 0.3, 0.02);
        }
        return hit;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean killedByPlayer) {
        super.dropCustomDeathLoot(level, source, killedByPlayer);
        level.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 1.0, this.getZ(), 40, 0.5, 0.9, 0.5, 0.06);
    }

    @Override
    public boolean isSensitiveToWater() {
        return false;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.ARUAQ_AMBIENT.get();
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
    protected void playStepSound(BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(SoundEvents.SOUL_SAND_STEP, 0.3f, 1.2f);
    }

    @Override
    protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Prepared", this.prepared);
    }

    @Override
    protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        this.prepared = input.getBooleanOr("Prepared", false);
    }
}
