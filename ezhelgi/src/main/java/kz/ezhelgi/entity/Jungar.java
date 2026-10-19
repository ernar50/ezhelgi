package kz.ezhelgi.entity;

import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
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
 * Жоңғар — джунгарский воин. Бродит по степи с кылышом, часто верхом,
 * а иногда везёт за конём арбу с добычей. Враждует с золотыми воинами.
 */
public class Jungar extends SteppeWarrior {
    public Jungar(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 26.0)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR, 4.0)
                .add(Attributes.MOVEMENT_SPEED, 0.28)
                .add(Attributes.FOLLOW_RANGE, 28.0);
    }

    @Override
    protected float mountedChance() {
        return 0.45f;
    }

    @Override
    protected @Nullable Class<? extends Monster> enemyClass() {
        return AltynJauynger.class;
    }

    @Override
    protected void equip(boolean mounted) {
        this.setItem(EquipmentSlot.MAINHAND, new ItemStack(ModItems.QYLYSH.get()));
        if (this.getRandom().nextInt(3) == 0) {
            this.setItem(EquipmentSlot.OFFHAND, new ItemStack(Items.SHIELD));
        }
    }

    @Override
    protected void dressHorse(Horse horse) {
        if (this.getRandom().nextInt(3) == 0) {
            horse.setItemSlot(EquipmentSlot.BODY, new ItemStack(Items.LEATHER_HORSE_ARMOR));
        }
    }

    /** Иногда за конём едет арба с припасами и добычей. */
    @Override
    protected void afterMounted(ServerLevel level, Horse horse) {
        if (this.getRandom().nextInt(10) >= 3) {
            return;
        }
        Arba arba = ModEntities.ARBA.get().create(level, EntitySpawnReason.EVENT);
        if (arba == null) {
            return;
        }
        arba.setPos(this.getX(), this.getY(), this.getZ() - 3.0);
        arba.hitch(horse);
        arba.cargo().setItem(0, new ItemStack(ModItems.KURT.get(), 2 + this.getRandom().nextInt(5)));
        arba.cargo().setItem(1, new ItemStack(ModItems.BAURSAK.get(), 2 + this.getRandom().nextInt(6)));
        arba.cargo().setItem(2, new ItemStack(Items.ARROW, 4 + this.getRandom().nextInt(12)));
        if (this.getRandom().nextInt(3) == 0) {
            arba.cargo().setItem(3, new ItemStack(ModItems.KUMYS.get(), 1 + this.getRandom().nextInt(2)));
        }
        if (this.getRandom().nextInt(4) == 0) {
            arba.cargo().setItem(4, new ItemStack(ModItems.SAKA_GOLD.get(), 1 + this.getRandom().nextInt(2)));
        }
        if (this.getRandom().nextInt(5) == 0) {
            arba.cargo().setItem(5, new ItemStack(Items.GOLD_INGOT, 2 + this.getRandom().nextInt(4)));
        }
        level.addFreshEntity(arba);
    }

    @Override
    protected @Nullable SoundEvent battleCry() {
        return ModSounds.JUNGAR_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return ModSounds.JUNGAR_AMBIENT.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return ModSounds.JUNGAR_HURT.get();
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return ModSounds.JUNGAR_DEATH.get();
    }
}
