package kz.ezhelgi.item;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Домбыра: каждое нажатие ПКМ играет следующую ноту мелодии
 * в пентатонике, похожей на мотив казахского кюя.
 */
public class DombyraItem extends Item {
    /** Полутоны относительно ноты фа-диез (0..24, как у нотного блока). */
    private static final int[] KUI = {
            6, 9, 11, 13, 11, 9, 6, 4, 6, 9,
            13, 16, 18, 16, 13, 11, 9, 11, 13, 9,
            6, 4, 1, 4, 6, 6
    };

    public DombyraItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        // Номер ноты по игровому времени, чтобы клиент и сервер играли одно и то же
        int index = (int) ((level.getGameTime() / 3L) % KUI.length);
        int note = KUI[index];
        float pitch = (float) Math.pow(2.0, (note - 12) / 12.0);

        // Бас и звонкая струна вместе звучат как две струны домбыры
        level.playSound(player, player.getX(), player.getY(), player.getZ(),
                SoundEvents.NOTE_BLOCK_GUITAR.value(), SoundSource.PLAYERS, 1.0f, pitch);
        level.playSound(player, player.getX(), player.getY(), player.getZ(),
                SoundEvents.NOTE_BLOCK_BANJO.value(), SoundSource.PLAYERS, 0.35f, pitch);

        if (level.isClientSide()) {
            level.addParticle(ParticleTypes.NOTE,
                    player.getX(), player.getEyeY() + 0.6, player.getZ(),
                    note / 24.0, 0.0, 0.0);
        }
        return InteractionResult.SUCCESS;
    }
}
