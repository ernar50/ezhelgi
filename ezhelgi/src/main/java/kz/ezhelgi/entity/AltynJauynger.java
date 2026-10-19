package kz.ezhelgi.entity;

import kz.ezhelgi.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Алтын жауынгер — золотой воин, страж сакских курганов.
 * Иногда он верхом на коне в золотой броне и с копьём-найзой,
 * иногда пеший с золотым кинжалом.
 * Mode: 0 — случайно, 1 — пеший, 2 — конный (задаётся в шаблоне кургана).
 */
public class AltynJauynger extends Monster {
    private int mode = 0;
    private boolean prepared = false;

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
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.prepared && this.level() instanceof ServerLevel server) {
            this.prepared = true;
            this.prepare(server);
        }
    }

    /** Снаряжение воина: золотой доспех и оружие, у конного — конь в золотой попоне. */
    private void prepare(ServerLevel server) {
        boolean mounted = this.mode == 2 || (this.mode == 0 && this.getRandom().nextBoolean());

        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.ALTYN_ADAM_HELMET.get()));
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.ALTYN_ADAM_CHESTPLATE.get()));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.ALTYN_ADAM_LEGGINGS.get()));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.ALTYN_ADAM_BOOTS.get()));
        this.setItemSlot(EquipmentSlot.MAINHAND,
                new ItemStack(mounted ? ModItems.NAIZA.get() : ModItems.ALTYN_QANJAR.get()));

        if (mounted && !this.isPassenger()) {
            Horse horse = EntityTypes.HORSE.create(server, EntitySpawnReason.EVENT);
            if (horse != null) {
                horse.setPos(this.getX(), this.getY(), this.getZ());
                horse.setItemSlot(EquipmentSlot.BODY, new ItemStack(Items.GOLDEN_HORSE_ARMOR));
                horse.setPersistenceRequired();
                server.addFreshEntity(horse);
                this.startRiding(horse);
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Mode", this.mode);
        output.putBoolean("Prepared", this.prepared);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.mode = input.getIntOr("Mode", 0);
        this.prepared = input.getBooleanOr("Prepared", false);
    }
}
