package kz.ezhelgi.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/**
 * Тұлпар: крылатый конь. Приручается и седлается как лошадь,
 * быстрее любой лошади, прыгает очень высоко, не получает урона от падения
 * и плавно планирует на крыльях, если с наездником падает вниз.
 */
public class Tulpar extends Horse {
    private static final double SPEED = 0.38;
    private static final double JUMP = 1.25;

    public Tulpar(EntityType<? extends Horse> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return AbstractHorse.createBaseHorseAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, SPEED)
                .add(Attributes.JUMP_STRENGTH, JUMP)
                .add(Attributes.SAFE_FALL_DISTANCE, 256.0)
                .add(Attributes.FALL_DAMAGE_MULTIPLIER, 0.0);
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                                  EntitySpawnReason spawnReason, @Nullable SpawnGroupData groupData) {
        SpawnGroupData data = super.finalizeSpawn(level, difficulty, spawnReason, groupData);
        // Обычная лошадь получает случайные характеристики, тұлпар всегда лучший
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(SPEED);
        this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(JUMP);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(40.0);
        this.setHealth(this.getMaxHealth());
        return data;
    }

    @Override
    public void tick() {
        super.tick();
        // Планирование на крыльях
        if (this.isVehicle() && !this.onGround() && !this.isInWater()) {
            Vec3 motion = this.getDeltaMovement();
            if (motion.y < -0.12) {
                this.setDeltaMovement(motion.x * 1.02, -0.12, motion.z * 1.02);
            }
        }
    }

    @Override
    public @Nullable Tulpar getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ModEntities.TULPAR.get().create(level, EntitySpawnReason.BREEDING);
    }
}
