package kz.ezhelgi.entity;

import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * Алтын жауынгер — золотой воин, страж сакских курганов.
 * Иногда верхом на коне в золотой попоне и с копьём-найзой,
 * иногда пеший с золотым кинжалом. Враждует с джунгарами.
 */
public class AltynJauynger extends SteppeWarrior {
    public AltynJauynger(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 8.0)
                .add(Attributes.MOVEMENT_SPEED, 0.27)
                .add(Attributes.FOLLOW_RANGE, 32.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
    }

    @Override
    protected float mountedChance() {
        return 0.5f;
    }

    @Override
    protected @Nullable Class<? extends Monster> enemyClass() {
        return Jungar.class;
    }

    @Override
    protected void equip(boolean mounted) {
        this.setItem(EquipmentSlot.HEAD, new ItemStack(ModItems.ALTYN_ADAM_HELMET.get()));
        this.setItem(EquipmentSlot.CHEST, new ItemStack(ModItems.ALTYN_ADAM_CHESTPLATE.get()));
        this.setItem(EquipmentSlot.LEGS, new ItemStack(ModItems.ALTYN_ADAM_LEGGINGS.get()));
        this.setItem(EquipmentSlot.FEET, new ItemStack(ModItems.ALTYN_ADAM_BOOTS.get()));
        this.setItem(EquipmentSlot.MAINHAND, new ItemStack(mounted ? ModItems.NAIZA.get() : ModItems.ALTYN_QANJAR.get()));
    }

    @Override
    protected void dressHorse(Horse horse) {
        horse.setItemSlot(EquipmentSlot.BODY, new ItemStack(Items.GOLDEN_HORSE_ARMOR));
    }

    @Override
    protected @Nullable SoundEvent battleCry() {
        return ModSounds.WARRIOR_CRY.get();
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.WARRIOR_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.WARRIOR_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.WARRIOR_DEATH.get();
    }
}
