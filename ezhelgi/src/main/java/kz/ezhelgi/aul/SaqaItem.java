package kz.ezhelgi.aul;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Сақа — биток для асық ату. ПКМ — бросить. */
public class SaqaItem extends Item {
    public SaqaItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.6f, 0.7f);
        if (level instanceof ServerLevel server) {
            Projectile.spawnProjectileFromRotation(Saqa::new, server, stack, player, 0.0f, 1.3f, 0.6f);
        }
        player.getCooldowns().addCooldown(stack, 15);
        stack.consume(1, player);
        return InteractionResult.SUCCESS;
    }
}
