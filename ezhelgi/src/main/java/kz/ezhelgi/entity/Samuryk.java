package kz.ezhelgi.entity;

import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Самұрық — огромная золотая птица, живущая у великого дерева Байтерек.
 * Не враждебна. Дикие птицы иногда роняют золотые перья.
 * Из яйца Самұрыка вылупляется птенец, преданный тому, кто его высидел:
 * вырастая, он становится летающим спутником — на нём можно летать.
 */
public class Samuryk extends Parrot {
    private int featherTime;

    public Samuryk(EntityType<? extends Parrot> type, Level level) {
        super(type, level);
        this.featherTime = 6000 + this.getRandom().nextInt(6000);
        this.xpReward = 10;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Parrot.createAttributes()
                .add(Attributes.MAX_HEALTH, 70.0)
                .add(Attributes.FLYING_SPEED, 0.8)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 8.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.3, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.2, 10.0f, 3.0f));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 12.0f));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    @Override
    protected boolean canBeABaby() {
        return true;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    private static boolean isTreat(ItemStack stack) {
        return stack.is(Items.COD) || stack.is(Items.SALMON) || stack.is(Items.COOKED_COD) || stack.is(Items.COOKED_SALMON)
                || stack.is(Items.GOLDEN_APPLE) || stack.is(Items.MUTTON) || stack.is(Items.BEEF);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel level) {
            // дикие птицы держатся своего дерева
            if (!this.isTame() && !this.hasHome()) {
                this.setHomeTo(this.blockPosition(), 28);
            }
            if (this.isTame() && this.hasHome()) {
                this.clearHome();
            }
            if (!this.isBaby() && --this.featherTime <= 0) {
                this.featherTime = 6000 + this.getRandom().nextInt(6000);
                this.spawnAtLocation(level, new ItemStack(ModItems.SAMURYK_QAUYRSYN.get()));
                this.playSound(SoundEvents.CHICKEN_EGG, 1.0f, 0.6f);
            }
            if (this.tickCount % 6 == 0) {
                level.sendParticles(ParticleTypes.WAX_ON, this.getX(), this.getY() + this.getBbHeight() * 0.6, this.getZ(),
                        1, this.getBbWidth() * 0.6, 0.3, this.getBbWidth() * 0.6, 0.0);
            }
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (isTreat(stack)) {
            if (!this.level().isClientSide()) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.heal(stack.is(Items.GOLDEN_APPLE) ? 30.0f : 8.0f);
                if (this.isBaby()) {
                    this.ageUp(60, true);
                }
                this.playSound(SoundEvents.PARROT_EAT, 1.0f, 0.6f);
                this.level().broadcastEntityEvent(this, (byte) 7);
            }
            return InteractionResult.SUCCESS;
        }
        if (!this.isTame() || !this.isOwnedBy(player) || hand != InteractionHand.MAIN_HAND || !stack.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player.isSecondaryUseActive() || this.isBaby()) {
            this.setOrderedToSit(!this.isOrderedToSit());
            player.sendSystemMessage(Component.translatable(this.isOrderedToSit()
                    ? "message.ezhelgi.samuryk.sit" : "message.ezhelgi.samuryk.follow"));
            return InteractionResult.SUCCESS;
        }
        this.setOrderedToSit(false);
        this.setInSittingPose(false);
        player.startRiding(this);
        return InteractionResult.SUCCESS;
    }

    // ---------------------------------------------------------------- полёт верхом
    @Override
    public @Nullable LivingEntity getControllingPassenger() {
        if (this.isTame() && this.getFirstPassenger() instanceof Player player && this.isOwnedBy(player)) {
            return player;
        }
        return super.getControllingPassenger();
    }

    @Override
    protected void tickRidden(Player controller, Vec3 riddenInput) {
        super.tickRidden(controller, riddenInput);
        this.setRot(controller.getYRot(), controller.getXRot() * 0.5f);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
    }

    @Override
    protected Vec3 getRiddenInput(Player controller, Vec3 selfInput) {
        return new Vec3(controller.xxa * 0.6f, 0.0, controller.zza);
    }

    @Override
    protected float getRiddenSpeed(Player controller) {
        return (float) this.getAttributeValue(Attributes.FLYING_SPEED) * 0.25f;
    }

    @Override
    public void travel(Vec3 input) {
        if (this.getControllingPassenger() instanceof Player rider && this.isAlive()) {
            Vec3 look = rider.getLookAngle();
            Vec3 motion = this.getDeltaMovement().scale(0.91);
            double speed = this.getAttributeValue(Attributes.FLYING_SPEED) * 0.09;
            if (input.z > 0.0) {
                motion = motion.add(look.scale(speed * input.z));
            } else if (input.z < 0.0) {
                motion = motion.scale(0.85);
            }
            if (input.x != 0.0) {
                Vec3 side = new Vec3(look.z, 0.0, -look.x).normalize();
                motion = motion.add(side.scale(speed * 0.6 * input.x));
            }
            if (input.z <= 0.0 && !this.onGround()) {
                motion = motion.add(0.0, -0.012, 0.0); // мягкое парение вниз
            }
            this.setDeltaMovement(motion);
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.resetFallDistance();
            return;
        }
        super.travel(input);
    }

    @Override
    protected void positionRider(net.minecraft.world.entity.Entity passenger, net.minecraft.world.entity.Entity.MoveFunction moveFunction) {
        super.positionRider(passenger, moveFunction);
        if (passenger instanceof LivingEntity living) {
            living.yBodyRot = this.yBodyRot;
        }
    }

    @Override
    public boolean causeFallDamage(double distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    public @Nullable SoundEvent getAmbientSound() {
        return ModSounds.SAMURYK_AMBIENT.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.SAMURYK_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.SAMURYK_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return this.isBaby() ? 1.5f : 0.9f + this.getRandom().nextFloat() * 0.1f;
    }

    @Override
    protected void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("FeatherTime", this.featherTime);
    }

    @Override
    protected void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        this.featherTime = input.getIntOr("FeatherTime", 6000);
    }
}
