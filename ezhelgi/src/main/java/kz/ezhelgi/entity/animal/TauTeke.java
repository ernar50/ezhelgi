package kz.ezhelgi.entity.animal;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/** Тау-теке — горный козёл. Запрыгивает на уступы, будто по лестнице. */
public class TauTeke extends WildAnimal {
    public TauTeke(EntityType<? extends Animal> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.JUMP_STRENGTH, 0.8)
                .add(Attributes.STEP_HEIGHT, 1.5)
                .add(Attributes.SAFE_FALL_DISTANCE, 14.0);
    }

    @Override
    public void tick() {
        super.tick();
        // прыжок на уступ, если упёрся в скалу
        if (!this.level().isClientSide() && this.onGround() && this.horizontalCollision
                && this.getDeltaMovement().horizontalDistanceSqr() > 0.0001) {
            this.setDeltaMovement(this.getDeltaMovement().x, 0.78, this.getDeltaMovement().z);
            this.needsSync = true;
        }
    }

    @Override
    protected Ingredient food() {
        return Ingredient.of(Items.WHEAT);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.GOAT_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.GOAT_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.GOAT_DEATH;
    }
}
