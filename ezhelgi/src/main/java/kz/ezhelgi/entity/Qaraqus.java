package kz.ezhelgi.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Қарақұс — гигантский стервятник. Кружит над степью, хватает когтями
 * кроликов, кур, тушканчиков и детёнышей, уносит ввысь и роняет.
 * Если его ранить — бросается на обидчика.
 */
public class Qaraqus extends Parrot {
    private int carryTicks;
    private int huntCooldown = 200;
    private @Nullable LivingEntity prey;

    public Qaraqus(EntityType<? extends Parrot> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Parrot.createAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.FLYING_SPEED, 0.8)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.4, true));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    private static boolean isPrey(LivingEntity e) {
        return e instanceof Rabbit || e instanceof Chicken || (e instanceof AgeableMob a && a.isBaby() && !(e instanceof Player)
                && e.getBbWidth() < 1.0f);
    }

    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (this.carryTicks > 0) {
            // уносит добычу ввысь
            this.carryTicks--;
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 0.0, 0.8).add(0.0, 0.18, 0.0));
            this.needsSync = true;
            if (this.carryTicks == 0 || this.getPassengers().isEmpty()) {
                this.ejectPassengers();
                this.carryTicks = 0;
                this.playSound(SoundEvents.PHANTOM_SWOOP, 1.0f, 0.7f);
            }
            return;
        }
        if (this.getTarget() != null) {
            return;
        }
        if (--this.huntCooldown <= 0) {
            this.huntCooldown = 300 + this.getRandom().nextInt(300);
            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(20.0, 12.0, 20.0),
                    e -> e.isAlive() && isPrey(e) && !e.isPassenger());
            this.prey = list.isEmpty() ? null : list.get(this.getRandom().nextInt(list.size()));
        }
        if (this.prey != null) {
            if (!this.prey.isAlive() || this.distanceTo(this.prey) > 40.0f) {
                this.prey = null;
                return;
            }
            this.getNavigation().moveTo(this.prey, 1.5);
            if (this.distanceTo(this.prey) < 1.8f && this.getPassengers().isEmpty()) {
                if (this.prey.startRiding(this)) {
                    this.carryTicks = 90;
                    this.playSound(SoundEvents.PHANTOM_BITE, 1.0f, 0.6f);
                }
                this.prey = null;
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    public @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.PHANTOM_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PHANTOM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PHANTOM_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return 0.5f;
    }
}
