package kz.ezhelgi.entity;

import kz.ezhelgi.item.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/** Қарақшы — разбойник степных дорог. Грабит караваны и прячется в руинах Отрара. */
public class Qaraqshy extends SteppeWarrior {
    public Qaraqshy(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 24.0)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.ARMOR, 3.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 28.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, SteppeVillager.class, false));
    }

    @Override
    protected float mountedChance() {
        return 0.2f;
    }

    @Override
    protected @Nullable Class<? extends Monster> enemyClass() {
        return null;
    }

    @Override
    protected void equip(boolean mounted) {
        int r = this.getRandom().nextInt(3);
        this.setItem(EquipmentSlot.MAINHAND, new ItemStack(r == 0 ? ModItems.QYLYSH.get() : r == 1 ? ModItems.TYRNAQ_QANJAR.get() : Items.IRON_AXE));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.05f);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return SoundEvents.PILLAGER_AMBIENT;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PILLAGER_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.PILLAGER_DEATH;
    }

    @Override
    protected @Nullable SoundEvent battleCry() {
        return SoundEvents.PILLAGER_CELEBRATE;
    }
}
