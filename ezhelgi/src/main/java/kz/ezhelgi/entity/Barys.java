package kz.ezhelgi.entity;

import kz.ezhelgi.sound.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Барыс — снежный барс. Живёт в горах, охотится на кроликов, овец, коз
 * и сайгаков. Высоко прыгает. Если подойти слишком близко — нападает.
 */
public class Barys extends Animal {
    private int leapCooldown;

    public Barys(EntityType<? extends Animal> type, Level level) {
        super(type, level);
        this.xpReward = 8;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.34)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.FOLLOW_RANGE, 24.0)
                .add(Attributes.JUMP_STRENGTH, 0.75)
                .add(Attributes.SAFE_FALL_DISTANCE, 16.0)
                .add(Attributes.STEP_HEIGHT, 1.5);
    }

    /** В горах и на снегу: блок под ногами — снег, камень или трава, свет не важен. */
    public static boolean checkBarysSpawnRules(EntityType<? extends Animal> type, net.minecraft.world.level.LevelAccessor level,
                                               net.minecraft.world.entity.EntitySpawnReason reason, net.minecraft.core.BlockPos pos,
                                               net.minecraft.util.RandomSource random) {
        net.minecraft.world.level.block.state.BlockState below = level.getBlockState(pos.below());
        return below.isSolid() && random.nextInt(2) == 0;
    }

    private static boolean isPrey(LivingEntity e) {
        return e instanceof Rabbit || e instanceof Sheep || e instanceof Chicken || e instanceof Goat || e instanceof Saiga;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.55f));
        this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.35, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // подошёл слишком близко — нападает
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                (target, level) -> target.distanceToSqr(this) < 4.5 * 4.5));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 60, true, false,
                (target, level) -> isPrey(target) && !target.isBaby()));
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel)) return;
        if (this.leapCooldown > 0) this.leapCooldown--;
        LivingEntity target = this.getTarget();
        // большой прыжок: на уступ или к добыче
        if (target != null && this.onGround() && this.leapCooldown <= 0) {
            double dist = this.distanceTo(target);
            double dy = target.getY() - this.getY();
            if ((dy > 1.5 && dist < 8.0) || (dist > 3.5 && dist < 9.0 && this.getRandom().nextInt(20) == 0)) {
                Vec3 dir = target.position().subtract(this.position());
                Vec3 flat = new Vec3(dir.x, 0, dir.z).normalize();
                this.setDeltaMovement(flat.x * 0.75, 0.75 + Math.min(0.5, Math.max(0.0, dy) * 0.12), flat.z * 0.75);
                this.needsSync = true;
                this.leapCooldown = 50;
                this.playSound(ModSounds.BARYS_ANGRY.get(), 1.0f, 1.1f);
            }
        }
        if (target == null && this.getHealth() < this.getMaxHealth() && this.tickCount % 80 == 0) {
            this.heal(1.0f);
        }
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
    protected @Nullable SoundEvent getAmbientSound() {
        return this.getTarget() != null ? ModSounds.BARYS_ANGRY.get() : ModSounds.BARYS_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.BARYS_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.BARYS_DEATH.get();
    }
}
