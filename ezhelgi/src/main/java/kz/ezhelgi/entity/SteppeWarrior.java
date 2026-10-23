package kz.ezhelgi.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Общая основа степных воинов: снаряжение при первом появлении,
 * иногда — конь (и даже арба за конём).
 * Mode: 0 — случайно, 1 — пеший, 2 — конный.
 */
public abstract class SteppeWarrior extends Monster {
    protected int mode = 0;
    private boolean prepared = false;
    /** Защитник аула во время набега: не трогает игроков. */
    protected boolean defender = false;

    public void makeDefender() {
        this.defender = true;
        this.setTarget(null);
    }

    public boolean isDefender() {
        return this.defender;
    }

    protected SteppeWarrior(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    /** Шанс оказаться верхом при Mode 0. */
    protected abstract float mountedChance();

    /** Снаряжение (оружие и броня). */
    protected abstract void equip(boolean mounted);

    /** Враги этого воина кроме игроков. */
    protected abstract @Nullable Class<? extends Monster> enemyClass();

    /** Боевой клич, когда воин замечает врага. */
    protected @Nullable SoundEvent battleCry() {
        return null;
    }

    private boolean hadTarget = false;

    /** Дополнительные действия после посадки на коня (например, прицепить арбу). */
    protected void afterMounted(ServerLevel level, Horse horse) {}

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.15, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true, (target, lvl) -> !this.defender));
        Class<? extends Monster> enemy = this.enemyClass();
        if (enemy != null) {
            this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, enemy, true));
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide()) {
            boolean hasTarget = this.getTarget() != null;
            SoundEvent cry = this.battleCry();
            if (hasTarget && !this.hadTarget && cry != null) {
                this.playSound(cry, 1.5f, 0.9f + this.getRandom().nextFloat() * 0.2f);
            }
            this.hadTarget = hasTarget;
        }
        if (!this.prepared && this.level() instanceof ServerLevel server) {
            this.prepared = true;
            boolean mounted = this.mode == 2 || (this.mode == 0 && this.getRandom().nextFloat() < this.mountedChance());
            this.equip(mounted);
            if (mounted && !this.isPassenger()) {
                Horse horse = EntityTypes.HORSE.create(server, EntitySpawnReason.EVENT);
                if (horse != null) {
                    horse.setPos(this.getX(), this.getY(), this.getZ());
                    horse.setYRot(this.getYRot());
                    this.dressHorse(horse);
                    horse.setPersistenceRequired();
                    server.addFreshEntity(horse);
                    this.startRiding(horse);
                    this.afterMounted(server, horse);
                }
            }
        }
    }

    protected void dressHorse(Horse horse) {}

    protected void setItem(EquipmentSlot slot, ItemStack stack) {
        this.setItemSlot(slot, stack);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Mode", this.mode);
        output.putBoolean("Prepared", this.prepared);
        output.putBoolean("Defender", this.defender);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.mode = input.getIntOr("Mode", 0);
        this.prepared = input.getBooleanOr("Prepared", false);
        this.defender = input.getBooleanOr("Defender", false);
    }
}
