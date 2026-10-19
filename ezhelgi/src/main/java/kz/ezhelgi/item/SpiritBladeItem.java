package kz.ezhelgi.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Аруақ қылышы — клинок, перекованный из меча духа батыра.
 * Удар замедляет врага, а нежити наносит дополнительный урон.
 */
public class SpiritBladeItem extends Item {
    public SpiritBladeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hurtEnemy(stack, target, attacker);
        if (attacker.level() instanceof ServerLevel level) {
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 50, 1));
            if (target.is(EntityTypeTags.UNDEAD) || KobyzItem.isEvil(target)) {
                target.hurtServer(level, attacker.damageSources().magic(), 4.0f);
            }
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, target.getX(), target.getY() + target.getBbHeight() * 0.6,
                    target.getZ(), 10, 0.25, 0.3, 0.25, 0.03);
            level.sendParticles(ParticleTypes.SOUL, target.getX(), target.getY() + target.getBbHeight() * 0.6,
                    target.getZ(), 4, 0.25, 0.3, 0.25, 0.03);
        }
    }
}
