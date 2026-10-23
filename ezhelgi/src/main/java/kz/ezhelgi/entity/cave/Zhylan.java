package kz.ezhelgi.entity.cave;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import org.jspecify.annotations.Nullable;

/**
 * Жылан — пещерная змея. Ползает по стенам и потолку, кусает ядом.
 * Живёт глубоко под землёй, в темноте.
 */
public class Zhylan extends Spider {
    public Zhylan(EntityType<? extends Spider> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Spider.createAttributes()
                .add(Attributes.MAX_HEALTH, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.32)
                .add(Attributes.ATTACK_DAMAGE, 3.0);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hit = super.doHurtTarget(level, target);
        if (hit && target instanceof LivingEntity living) {
            int seconds = level.getDifficulty() == Difficulty.HARD ? 9 : (level.getDifficulty() == Difficulty.NORMAL ? 6 : 3);
            living.addEffect(new MobEffectInstance(MobEffects.POISON, seconds * 20, 0), this);
        }
        return hit;
    }

    /** Змеи живут только глубоко: ниже 40-го блока и в темноте. */
    public static boolean checkCaveSpawnRules(EntityType<? extends Monster> type, ServerLevelAccessor level,
                                              EntitySpawnReason reason, BlockPos pos, RandomSource random) {
        return pos.getY() < 40 && Monster.checkMonsterSpawnRules(type, level, reason, pos, random);
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return this.getRandom().nextInt(3) == 0 ? kz.ezhelgi.sound.ModSounds.ZHYLAN_HISS.get() : null;
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SILVERFISH_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.SILVERFISH_DEATH;
    }
}
