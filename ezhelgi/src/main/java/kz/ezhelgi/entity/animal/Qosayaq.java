package kz.ezhelgi.entity.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/** Қосаяқ — тушканчик. Прыгает на длинных задних лапах, живёт в пустыне. */
public class Qosayaq extends Rabbit {
    public Qosayaq(EntityType<? extends Rabbit> type, Level level) {
        super(type, level);
    }

    @Override
    public @Nullable Rabbit getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return (Rabbit) this.getType().create(level, EntitySpawnReason.BREEDING);
    }
}
