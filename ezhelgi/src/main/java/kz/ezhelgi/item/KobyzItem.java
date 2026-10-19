package kz.ezhelgi.item;

import kz.ezhelgi.entity.Albasty;
import kz.ezhelgi.entity.BatyrAruagy;
import kz.ezhelgi.entity.Zheztyrnak;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Қобыз — смычковый инструмент баксы. Зажми ПКМ: звучит низкий
 * гудящий напев, и нежить (зомби, скелеты), а также албасты, жезтырнақ
 * и духи в радиусе 14 блоков в страхе бегут прочь и забывают о тебе.
 */
public class KobyzItem extends Item {
    private static final int PHRASE_TICKS = 50;
    private static final double RADIUS = 14.0;

    public KobyzItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        if (level instanceof ServerLevel server) {
            playPhrase(server, player);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int ticksRemaining) {
        int used = this.getUseDuration(stack, entity) - ticksRemaining;
        if (level instanceof ServerLevel server) {
            if (used > 0 && used % PHRASE_TICKS == 0) {
                playPhrase(server, entity);
            }
            if (used % 10 == 0) {
                scare(server, entity);
            }
        }
    }

    public static boolean isEvil(LivingEntity e) {
        return e.is(EntityTypeTags.UNDEAD) || e instanceof Albasty || e instanceof Zheztyrnak || e instanceof BatyrAruagy;
    }

    private static void playPhrase(ServerLevel level, LivingEntity player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.KOBYZ_PLAY.get(), SoundSource.RECORDS, 1.2f, 1.0f);
        level.sendParticles(ParticleTypes.NOTE, player.getX(), player.getEyeY() + 0.5, player.getZ(), 3, 0.6, 0.3, 0.6, 1.0);
        // звуковая волна
        for (int i = 0; i < 24; i++) {
            double a = i * Math.PI * 2.0 / 24.0;
            level.sendParticles(ParticleTypes.SOUL, player.getX() + Math.cos(a) * 2.5, player.getY() + 0.3,
                    player.getZ() + Math.sin(a) * 2.5, 1, 0.0, 0.0, 0.0, 0.02);
        }
    }

    private static void scare(ServerLevel level, LivingEntity player) {
        for (Monster mob : level.getEntitiesOfClass(Monster.class, player.getBoundingBox().inflate(RADIUS), KobyzItem::isEvil)) {
            if (mob.getTarget() == player) {
                mob.setTarget(null);
            }
            Vec3 away = mob.position().subtract(player.position());
            double dist = Math.max(0.5, away.length());
            Vec3 dir = away.scale(1.0 / dist);
            mob.getNavigation().moveTo(mob.getX() + dir.x * 10.0, mob.getY(), mob.getZ() + dir.z * 10.0, 1.4);
            mob.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1));
            if (dist < 4.0) {
                mob.setDeltaMovement(mob.getDeltaMovement().add(dir.x * 0.5, 0.15, dir.z * 0.5));
                mob.needsSync = true;
            }
            if (level.getRandom().nextInt(3) == 0) {
                level.sendParticles(ParticleTypes.SMOKE, mob.getX(), mob.getY() + mob.getBbHeight(), mob.getZ(), 3, 0.2, 0.2, 0.2, 0.01);
            }
        }
    }

    /** Нужно ли этому мобу бежать от игрока с кобызом (используется и тумаром). */
    public static void flee(PathfinderMob mob, LivingEntity from, double distance) {
        Vec3 away = mob.position().subtract(from.position()).normalize().scale(distance);
        mob.getNavigation().moveTo(mob.getX() + away.x, mob.getY(), mob.getZ() + away.z, 1.3);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.CROSSBOW;
    }
}
