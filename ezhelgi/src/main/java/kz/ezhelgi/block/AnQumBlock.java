package kz.ezhelgi.block;

import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Ән құм — поющий песок бархана Алтын-Эмель. Когда по нему идёшь, дюна гудит,
 * будто где-то далеко играет орган.
 */
public class AnQumBlock extends Block {
    public AnQumBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (level instanceof ServerLevel server && entity instanceof LivingEntity living && !entity.isSteppingCarefully()
                && entity.getDeltaMovement().horizontalDistanceSqr() > 0.0004) {
            long key = server.getGameTime() / 30;
            // не чаще раза в полторы секунды на одного ходока
            if (living.getPersistentData().getLongOr("ezhelgi_anqum", -1L) != key) {
                living.getPersistentData().putLong("ezhelgi_anqum", key);
                float pitch = 0.7f + (pos.getY() % 7) * 0.05f + server.getRandom().nextFloat() * 0.1f;
                server.playSound(null, pos, ModSounds.SINGING_SAND.get(), SoundSource.BLOCKS, 0.9f, pitch);
                server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), entity.getX(), pos.getY() + 1.05,
                        entity.getZ(), 6, 0.3, 0.05, 0.3, 0.02);
            }
        }
        super.stepOn(level, pos, state, entity);
    }
}
