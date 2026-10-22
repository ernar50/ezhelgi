package kz.ezhelgi.entity.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/** Лошадь Пржевальского — дикая степная лошадь. Живёт в загонах аулов, приручается как обычная. */
public class Przewalski extends Horse {
    public Przewalski(EntityType<? extends Horse> type, Level level) {
        super(type, level);
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        if (partner instanceof Przewalski) {
            return (AgeableMob) this.getType().create(level, EntitySpawnReason.BREEDING);
        }
        return super.getBreedOffspring(level, partner);
    }
}
