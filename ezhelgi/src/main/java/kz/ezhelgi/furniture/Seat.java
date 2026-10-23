package kz.ezhelgi.furniture;

import kz.ezhelgi.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Невидимое «сиденье» для көрпе: игрок садится на него, встаёт — сиденье исчезает. */
public class Seat extends Entity {
    public Seat(EntityType<? extends Seat> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public static void sit(Level level, BlockPos pos, double height, Player player) {
        if (player.isPassenger()) return;
        if (!level.getEntitiesOfClass(Seat.class, new net.minecraft.world.phys.AABB(pos)).isEmpty()) return;
        Seat seat = new Seat(ModEntities.SEAT.get(), level);
        seat.setPos(pos.getX() + 0.5, pos.getY() + height, pos.getZ() + 0.5);
        level.addFreshEntity(seat);
        player.startRiding(seat);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && (this.getPassengers().isEmpty() || this.level().getBlockState(this.blockPosition()).isAir())) {
            this.ejectPassengers();
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
    }
}
