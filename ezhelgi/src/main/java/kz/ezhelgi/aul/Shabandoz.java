package kz.ezhelgi.aul;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * Шабандоз — всадник для көкпара (команды красных и синих) и бәйге (юные наездники).
 * Сам не думает: куда скакать, решает праздник аула (AulEvents).
 */
public class Shabandoz extends PathfinderMob {
    public static final int RED = 0, BLUE = 1, JOCKEY = 2;
    private static final EntityDataAccessor<Integer> TEAM =
            SynchedEntityData.defineId(Shabandoz.class, EntityDataSerializers.INT);

    private @Nullable UUID eventId;
    private boolean prepared;
    private int orphanTicks;
    /** Для бәйге: номер следующей вехи круга. */
    public int waypoint;

    public Shabandoz(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 64.0);
    }

    public void setup(int team, UUID eventId) {
        this.entityData.set(TEAM, team);
        this.eventId = eventId;
        this.setPersistenceRequired();
    }

    public int team() {
        return this.entityData.get(TEAM);
    }

    public @Nullable UUID eventId() {
        return this.eventId;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TEAM, 0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (!this.prepared) {
            this.prepared = true;
            if (!this.isPassenger()) {
                Horse horse = EntityTypes.HORSE.create(level, EntitySpawnReason.EVENT);
                if (horse != null) {
                    horse.snapTo(this.getX(), this.getY(), this.getZ());
                    horse.setYRot(this.getYRot());
                    horse.setTamed(true);
                    horse.setItemSlot(EquipmentSlot.SADDLE, new ItemStack(Items.SADDLE));
                    var speed = horse.getAttribute(Attributes.MOVEMENT_SPEED);
                    if (speed != null) {
                        speed.setBaseValue(this.team() == JOCKEY ? 0.30 + this.getRandom().nextDouble() * 0.04 : 0.32);
                    }
                    horse.getPersistentData().putBoolean("ezhelgi_event_horse", true);
                    horse.setPersistenceRequired();
                    level.addFreshEntity(horse);
                    this.startRiding(horse);
                }
            }
        }
        // праздник закончился — всадник уезжает вместе с конём
        if (this.tickCount % 20 == 0) {
            if (this.eventId == null || !AulEvents.isActive(this.eventId)) {
                if (++this.orphanTicks > 3) {
                    vanish(level);
                }
            } else {
                this.orphanTicks = 0;
            }
        }
    }

    public void vanish(ServerLevel level) {
        Entity v = this.getVehicle();
        level.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY() + 1, this.getZ(), 12, 0.5, 0.5, 0.5, 0.02);
        if (v != null) {
            v.discard();
        }
        this.discard();
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return false;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Team", this.team());
        output.putBoolean("Prepared", this.prepared);
        if (this.eventId != null) {
            output.putString("Event", this.eventId.toString());
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(TEAM, input.getIntOr("Team", 0));
        this.prepared = input.getBooleanOr("Prepared", false);
        String ev = input.getStringOr("Event", "");
        this.eventId = ev.isEmpty() ? null : UUID.fromString(ev);
    }
}
