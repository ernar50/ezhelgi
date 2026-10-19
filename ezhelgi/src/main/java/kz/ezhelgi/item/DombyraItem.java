package kz.ezhelgi.item;

import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

/**
 * Домбыра: зажми ПКМ — батыр держит домбыру двумя руками и играет кюй.
 * Каждые 3 секунды звучит новая музыкальная фраза (записи синтезированы
 * как щипок двух струн, строй ре–соль). Только музыка, без эффектов.
 */
public class DombyraItem extends Item {
    private static final int PHRASE_TICKS = 62;

    public DombyraItem(Properties properties) {
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
        if (used > 0 && used % PHRASE_TICKS == 0 && level instanceof ServerLevel server) {
            playPhrase(server, entity);
        }
    }

    private static void playPhrase(ServerLevel level, LivingEntity player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.DOMBYRA_KUI.get(), SoundSource.RECORDS, 1.0f, 1.0f);
        level.sendParticles(ParticleTypes.NOTE, player.getX(), player.getEyeY() + 0.5, player.getZ(),
                4, 0.6, 0.3, 0.6, 1.0);
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
