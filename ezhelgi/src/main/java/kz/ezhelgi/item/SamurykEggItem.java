package kz.ezhelgi.item;

import kz.ezhelgi.entity.ModEntities;
import kz.ezhelgi.entity.Samuryk;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

/**
 * Жұмыртқа Самұрыка: ПКМ по земле — из золотого яйца вылупляется птенец,
 * который считает тебя своим. Через 20 минут он вырастет.
 */
public class SamurykEggItem extends Item {
    public SamurykEggItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        Samuryk chick = ModEntities.SAMURYK.get().create(level, EntitySpawnReason.SPAWN_ITEM_USE);
        if (chick == null) {
            return InteractionResult.FAIL;
        }
        chick.setAge(-24000);
        chick.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        if (player != null) {
            chick.tame(player);
            chick.setYRot(player.getYRot() + 180.0f);
            player.sendSystemMessage(Component.translatable("message.ezhelgi.samuryk.hatched"));
        }
        chick.setPersistenceRequired();
        level.addFreshEntity(chick);
        level.playSound(null, pos, SoundEvents.SNIFFER_EGG_HATCH, SoundSource.NEUTRAL, 1.0f, 1.2f);
        level.sendParticles(ParticleTypes.WAX_ON, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 30, 0.4, 0.4, 0.4, 0.1);
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 12, 0.4, 0.4, 0.4, 0.0);
        if (player == null || !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
