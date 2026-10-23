package kz.ezhelgi.nature;

import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Қаңбақ — перекати-поле. Ветер катит сухой куст по степи и пустыне, он подпрыгивает
 * на кочках и рассыпается от удара (палки и семена). Через пару минут его уносит прочь.
 */
public class Qanbaq extends Mob {
    private int life;

    public Qanbaq(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 2.0).add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.STEP_HEIGHT, 1.0);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide()) {
            return;
        }
        Vec3 wind = NatureEvents.wind(this.level());
        Vec3 v = this.getDeltaMovement();
        double vx = v.x * 0.92 + wind.x * 0.035;
        double vz = v.z * 0.92 + wind.z * 0.035;
        double vy = v.y;
        if (this.onGround()) {
            if (this.horizontalCollision) {
                vy = 0.42;
            } else if (this.random.nextInt(10) == 0) {
                vy = 0.18 + this.random.nextDouble() * 0.25;
            }
        }
        this.setDeltaMovement(vx, vy, vz);
        if (vx * vx + vz * vz > 1.0E-4) {
            float yaw = (float) (Mth.atan2(vz, vx) * (180.0 / Math.PI)) - 90.0f;
            this.setYRot(yaw);
            this.yBodyRot = yaw;
            this.yHeadRot = yaw;
        }
        if (++this.life > 2400 || this.isInWater() || this.isInLava()) {
            poof((ServerLevel) this.level(), false);
        }
    }

    private void poof(ServerLevel level, boolean drops) {
        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.DEAD_BUSH.defaultBlockState()),
                this.getX(), this.getY() + 0.4, this.getZ(), 20, 0.3, 0.3, 0.3, 0.05);
        if (drops) {
            this.spawnAtLocation(level, new ItemStack(Items.STICK, 1 + this.random.nextInt(3)));
            if (this.random.nextInt(2) == 0) {
                this.spawnAtLocation(level, new ItemStack(Items.WHEAT_SEEDS, 1 + this.random.nextInt(2)));
            }
            if (this.random.nextInt(6) == 0) {
                this.spawnAtLocation(level, new ItemStack(Items.DEAD_BUSH));
            }
        }
        this.playSound(SoundEvents.AZALEA_LEAVES_BREAK, 1.0f, 0.8f);
        this.discard();
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        if (this.isInvulnerableTo(level, source)) {
            return false;
        }
        poof(level, source.getEntity() != null);
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return true;
    }
}
