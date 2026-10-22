package kz.ezhelgi.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Лиса, в которую обернулась мыстан кемпір. Убегает и через 12 секунд снова становится ведьмой. */
public class MystanTulki extends Fox {
    private int timer = 240;
    private float witchHealth = 40.0f;

    public MystanTulki(EntityType<? extends Fox> type, Level level) {
        super(type, level);
    }

    public void setWitchHealth(float health) {
        this.witchHealth = health;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, 16.0f, 1.6, 2.0));
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (this.tickCount % 4 == 0) {
            level.sendParticles(ParticleTypes.WITCH, this.getX(), this.getY() + 0.5, this.getZ(), 1, 0.2, 0.2, 0.2, 0.0);
        }
        if (--this.timer <= 0) {
            MystanKempir witch = ModEntities.MYSTAN_KEMPIR.get().create(level, EntitySpawnReason.CONVERSION);
            if (witch != null) {
                witch.setPos(this.getX(), this.getY(), this.getZ());
                witch.setHealth(Math.min(witch.getMaxHealth(), this.witchHealth + 8.0f));
                level.addFreshEntity(witch);
                level.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 1.0, this.getZ(), 30, 0.4, 0.8, 0.4, 0.03);
            }
            this.discard();
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("ShiftTimer", this.timer);
        output.putFloat("WitchHealth", this.witchHealth);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.timer = input.getIntOr("ShiftTimer", 240);
        this.witchHealth = input.getFloatOr("WitchHealth", 40.0f);
    }
}
