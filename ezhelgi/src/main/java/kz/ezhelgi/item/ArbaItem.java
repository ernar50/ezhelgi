package kz.ezhelgi.item;

import kz.ezhelgi.entity.Arba;
import kz.ezhelgi.entity.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

/** Арба: ПКМ по земле ставит деревянную телегу. */
public class ArbaItem extends Item {
    public ArbaItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }
        BlockPos pos = context.getClickedPos().above();
        Arba arba = ModEntities.ARBA.get().create(level, EntitySpawnReason.SPAWN_ITEM_USE);
        if (arba == null) {
            return InteractionResult.FAIL;
        }
        Player player = context.getPlayer();
        float yaw = player != null ? player.getYRot() : 0.0f;
        arba.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        arba.setYRot(yaw);
        arba.yBodyRot = yaw;
        arba.yHeadRot = yaw;
        level.addFreshEntity(arba);
        level.playSound(null, pos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0f, 0.9f);
        if (player == null || !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
}
