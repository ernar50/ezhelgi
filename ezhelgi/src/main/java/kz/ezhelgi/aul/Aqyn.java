package kz.ezhelgi.aul;

import kz.ezhelgi.entity.SteppeVillager;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** Ақын — поэт-импровизатор, участник айтыса. ПКМ во время голосования — выбрать победителя. */
public class Aqyn extends SteppeVillager {
    private int orphan;

    public Aqyn(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        if (this.level() instanceof ServerLevel level) {
            AulEvents.vote(level, this, player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void updateTrades(ServerLevel level) {
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel level && this.tickCount % 40 == 0) {
            if (!AulEvents.isAqynInGame(this.getUUID())) {
                if (++this.orphan > 2) {
                    level.sendParticles(net.minecraft.core.particles.ParticleTypes.CLOUD, this.getX(), this.getY() + 1, this.getZ(),
                            10, 0.3, 0.6, 0.3, 0.02);
                    this.discard();
                }
            } else {
                this.orphan = 0;
            }
        }
    }
}
