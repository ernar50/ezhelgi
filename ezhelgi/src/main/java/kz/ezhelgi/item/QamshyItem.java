package kz.ezhelgi.item;

import kz.ezhelgi.entity.Interruptible;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Қамшы — плеть. Удар отбрасывает небольших мобов далеко,
 * сбивает натянутый лук, щит, заклинание или прыжок,
 * а криперу не даёт взорваться. ПКМ — щелчок плетью перед собой.
 */
public class QamshyItem extends Item {
    public QamshyItem(Properties properties) {
        super(properties);
    }

    /** Небольшой моб — не больше человека и не босс. */
    public static boolean isSmall(LivingEntity e) {
        return e.getBbWidth() * e.getBbHeight() <= 1.4f && e.getMaxHealth() <= 40.0f
                && !(e instanceof WitherBoss) && !(e instanceof EnderDragon);
    }

    /** Сбить атаку. */
    public static void interrupt(LivingEntity target) {
        if (target.isUsingItem()) {
            target.stopUsingItem();
        }
        if (target instanceof Creeper creeper) {
            creeper.setSwellDir(-1);
        }
        if (target instanceof Interruptible interruptible) {
            interruptible.interrupt();
        }
        if (target instanceof Mob mob) {
            mob.getNavigation().stop();
        }
    }

    public static void lash(ServerLevel level, LivingEntity attacker, LivingEntity target, double power) {
        interrupt(target);
        Vec3 dir = target.position().subtract(attacker.position());
        dir = new Vec3(dir.x, 0.0, dir.z).normalize();
        double strength = isSmall(target) ? power : power * 0.35;
        target.knockback(strength, -dir.x, -dir.z, attacker.damageSources().generic(), 1.0f);
        level.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + target.getBbHeight() * 0.6, target.getZ(),
                8, 0.2, 0.2, 0.2, 0.2);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hurtEnemy(stack, target, attacker);
        if (attacker.level() instanceof ServerLevel level) {
            lash(level, attacker, target, 1.6);
            level.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.QAMSHY_CRACK.get(),
                    SoundSource.PLAYERS, 1.0f, 0.9f + level.getRandom().nextFloat() * 0.2f);
        }
    }

    /** Щелчок плетью: задевает всех перед собой на 4 блока. */
    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel server) {
            server.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.QAMSHY_CRACK.get(),
                    SoundSource.PLAYERS, 1.3f, 1.0f);
            Vec3 look = player.getLookAngle();
            Vec3 tip = player.getEyePosition().add(look.scale(3.5));
            server.sendParticles(ParticleTypes.CRIT, tip.x, tip.y, tip.z, 10, 0.15, 0.15, 0.15, 0.3);
            server.sendParticles(ParticleTypes.SWEEP_ATTACK, tip.x, tip.y, tip.z, 1, 0.0, 0.0, 0.0, 0.0);
            for (LivingEntity e : server.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(4.5),
                    e -> e != player && e.isAlive())) {
                Vec3 to = e.position().subtract(player.position());
                if (to.length() > 4.5 || to.normalize().dot(new Vec3(look.x, 0.0, look.z).normalize()) < 0.5) {
                    continue;
                }
                lash(server, player, e, 1.1);
                e.hurtServer(server, player.damageSources().playerAttack(player), 2.0f);
            }
            stack.hurtAndBreak(1, player, hand);
        }
        player.getCooldowns().addCooldown(stack, 20);
        return InteractionResult.SUCCESS;
    }
}
