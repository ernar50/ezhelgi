package kz.ezhelgi.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

/**
 * Көкбөрі: крупный серо-голубой степной волк.
 * Приручается костью, как обычный волк, но сильнее и быстрее.
 */
public class Kokbori extends Wolf {
    public Kokbori(EntityType<? extends Wolf> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Wolf.createAttributes()
                .add(Attributes.MAX_HEALTH, 14.0)
                .add(Attributes.MOVEMENT_SPEED, 0.34)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    public @Nullable Kokbori getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return ModEntities.KOKBORI.get().create(level, EntitySpawnReason.BREEDING);
    }
}
