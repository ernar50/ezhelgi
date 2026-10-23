package kz.ezhelgi.entity.animal;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jspecify.annotations.Nullable;

/**
 * Лошадь Пржевальского — дикая степная лошадь. Живёт в загонах аулов.
 * Выносливее и сильнее обычной лошади: больше здоровья, высокий прыжок, быстрый бег.
 */
public class Przewalski extends Horse {
    private static final String STRONG = "ezhelgi_strong";

    public Przewalski(EntityType<? extends Horse> type, Level level) {
        super(type, level);
    }

    /** Сильные характеристики: здоровье 30–38, скорость 0.31–0.36, прыжок 0.95–1.15. */
    private void makeStrong() {
        RandomSource r = this.getRandom();
        set(Attributes.MAX_HEALTH, 30.0 + r.nextInt(9));
        set(Attributes.MOVEMENT_SPEED, 0.31 + r.nextDouble() * 0.05);
        set(Attributes.JUMP_STRENGTH, 0.95 + r.nextDouble() * 0.2);
        set(Attributes.ARMOR, 2.0);
        this.setHealth(this.getMaxHealth());
        this.getPersistentData().putBoolean(STRONG, true);
    }

    private void set(net.minecraft.core.Holder<net.minecraft.world.entity.ai.attributes.Attribute> attr, double value) {
        AttributeInstance inst = this.getAttribute(attr);
        if (inst != null) {
            inst.setBaseValue(value);
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                                  EntitySpawnReason reason, @Nullable SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        makeStrong();
        return result;
    }

    @Override
    public void tick() {
        super.tick();
        // лошади из старых миров тоже становятся сильными
        if (!this.level().isClientSide() && !this.getPersistentData().getBooleanOr(STRONG, false)) {
            makeStrong();
        }
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        if (partner instanceof Przewalski) {
            Przewalski foal = (Przewalski) this.getType().create(level, EntitySpawnReason.BREEDING);
            if (foal != null) {
                foal.makeStrong();
            }
            return foal;
        }
        return super.getBreedOffspring(level, partner);
    }
}
