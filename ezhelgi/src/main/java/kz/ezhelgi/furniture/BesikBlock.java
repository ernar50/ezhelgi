package kz.ezhelgi.furniture;

import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * Бесік — деревянная колыбель. ПКМ — покачать и спеть бесік жыры (колыбельную).
 * Колыбельная успокаивает: ненадолго даёт регенерацию всем рядом.
 */
public class BesikBlock extends FacingBlock {
    public BesikBlock(Properties properties) {
        super(properties, Block.box(3, 0, 1, 13, 11, 15), Block.box(1, 0, 3, 15, 11, 13));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level instanceof ServerLevel server) {
            if (player.getCooldowns().isOnCooldown(new net.minecraft.world.item.ItemStack(this.asItem()))) {
                return InteractionResult.SUCCESS;
            }
            player.getCooldowns().addCooldown(new net.minecraft.world.item.ItemStack(this.asItem()), 200);
            server.playSound(null, pos, ModSounds.ZHETIGEN.get(), SoundSource.BLOCKS, 0.6f, 1.25f);
            server.sendParticles(ParticleTypes.NOTE, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 4, 0.3, 0.2, 0.3, 0.5);
            for (Player p : server.getEntitiesOfClass(Player.class, new net.minecraft.world.phys.AABB(pos).inflate(6))) {
                p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0, true, true));
            }
            player.sendOverlayMessage(Component.translatable("message.ezhelgi.besik.lullaby"));
        }
        return InteractionResult.SUCCESS;
    }
}
