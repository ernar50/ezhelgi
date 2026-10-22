package kz.ezhelgi.item;

import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;

/**
 * Домбыра: зажми ПКМ — звучит кюй Курмангазы. Запись разбита на 20 частей
 * по 8,5 секунды: если отпустить и снова зажать, музыка продолжится дальше.
 * Музыка понемногу лечит игроков рядом, а домбыра медленно изнашивается.
 */
public class DombyraItem extends Item {
    /** 8,5 секунды — длина одной части записи. */
    private static final int PART_TICKS = 170;
    private static final String KEY = "ezhelgi_kui_part";

    public DombyraItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        if (level instanceof ServerLevel server) {
            playPart(server, player);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int ticksRemaining) {
        int used = this.getUseDuration(stack, entity) - ticksRemaining;
        if (!(level instanceof ServerLevel server)) {
            return;
        }
        if (used > 0 && used % PART_TICKS == 0) {
            advance(entity);
            playPart(server, entity);
        }
        if (used % 40 == 20) {
            server.sendParticles(ParticleTypes.NOTE, entity.getX(), entity.getEyeY() + 0.5, entity.getZ(), 2, 0.6, 0.3, 0.6, 1.0);
            // музыка лечит игроков рядом
            for (Player near : server.getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(8.0))) {
                near.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 80, 0, true, true));
            }
        }
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remainingTime) {
        if (level instanceof ServerLevel server) {
            stopMusic(server, entity);
            advance(entity);
        }
        return true;
    }

    private static int part(LivingEntity entity) {
        return Math.floorMod(entity.getPersistentData().getIntOr(KEY, 0), ModSounds.KURMANGAZY_KUI.size());
    }

    private static void advance(LivingEntity entity) {
        CompoundTag data = entity.getPersistentData();
        data.putInt(KEY, (part(entity) + 1) % ModSounds.KURMANGAZY_KUI.size());
    }

    private static SoundEvent current(LivingEntity entity) {
        return ModSounds.KURMANGAZY_KUI.get(part(entity)).get();
    }

    private static void playPart(ServerLevel level, LivingEntity player) {
        level.playSound(null, player.getX(), player.getY(), player.getZ(), current(player), SoundSource.RECORDS, 1.0f, 1.0f);
        level.sendParticles(ParticleTypes.NOTE, player.getX(), player.getEyeY() + 0.5, player.getZ(), 4, 0.6, 0.3, 0.6, 1.0);
        // инструмент понемногу изнашивается
        if (!(player instanceof Player p && p.getAbilities().instabuild) && player.isUsingItem()) {
            player.getItemInHand(player.getUsedItemHand()).hurtAndBreak(1, player, player.getUsedItemHand());
        }
    }

    /** Остановить звучащую часть у всех, кто слышит. */
    private static void stopMusic(ServerLevel level, LivingEntity player) {
        ClientboundStopSoundPacket packet = new ClientboundStopSoundPacket(current(player).location(), SoundSource.RECORDS);
        for (ServerPlayer listener : level.players()) {
            if (listener.distanceToSqr(player) < 64.0 * 64.0) {
                listener.connection.send(packet);
            }
        }
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
