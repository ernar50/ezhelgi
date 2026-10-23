package kz.ezhelgi.aul;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Серке — туша козла для көкпара. Её хватают (ПКМ) и везут к своему флагу.
 * Сама не двигается и неуязвима; после игры исчезает.
 */
public class Serke extends Goat {
    private int orphan;

    public Serke(EntityType<? extends Goat> type, Level level) {
        super(type, level);
        this.setNoAi(true);
        this.setSilent(true);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        if (this.level() instanceof ServerLevel level) {
            AulEvents.grabSerke(level, this, player);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel && this.tickCount % 40 == 0) {
            if (!AulEvents.isSerkeInGame(this.getUUID())) {
                if (++this.orphan > 2) {
                    this.discard();
                }
            } else {
                this.orphan = 0;
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return false;
    }
}
