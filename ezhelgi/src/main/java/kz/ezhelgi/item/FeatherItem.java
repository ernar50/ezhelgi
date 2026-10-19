package kz.ezhelgi.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Перо Самұрыка: ПКМ — 2 минуты плавного падения и прыгучести. */
public class FeatherItem extends Item {
    public FeatherItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level instanceof ServerLevel server) {
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * 120, 0));
            player.addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 20 * 120, 1));
            server.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PHANTOM_FLAP, SoundSource.PLAYERS, 0.8f, 1.4f);
            server.sendParticles(ParticleTypes.WAX_ON, player.getX(), player.getY() + 1.0, player.getZ(), 20, 0.4, 0.6, 0.4, 0.05);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
