package kz.ezhelgi.aul;

import kz.ezhelgi.sound.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

/**
 * Речь персонажа: реплики по очереди с паузой. Слушатель — один игрок
 * (диалог) или все игроки рядом (айтыс, жыр, объявления).
 */
public class Speech {
    private final Deque<Component> lines = new ArrayDeque<>();
    private @Nullable UUID listener;
    private boolean broadcast;
    private int timer;
    private int delay = 45;
    private @Nullable SoundEvent sound;

    public boolean busy() {
        return !this.lines.isEmpty();
    }

    public void sayTo(Player player, List<Component> text, int delay) {
        this.lines.clear();
        this.lines.addAll(text);
        this.listener = player.getUUID();
        this.broadcast = false;
        this.delay = delay;
        this.timer = 1;
    }

    public void announce(List<Component> text, int delay) {
        this.lines.clear();
        this.lines.addAll(text);
        this.listener = null;
        this.broadcast = true;
        this.delay = delay;
        this.timer = 1;
    }

    public void sound(@Nullable SoundEvent sound) {
        this.sound = sound;
    }

    public void clear() {
        this.lines.clear();
    }

    /** Возвращает true, когда прозвучала очередная реплика. */
    public boolean tick(ServerLevel level, Mob speaker, Component name) {
        if (this.lines.isEmpty()) {
            return false;
        }
        Player target = this.listener == null ? null : level.getPlayerByUUID(this.listener);
        if (!this.broadcast && (target == null || target.distanceToSqr(speaker) > 24 * 24)) {
            this.lines.clear();
            return false;
        }
        if (target != null) {
            speaker.getLookControl().setLookAt(target);
            speaker.getNavigation().stop();
        }
        if (--this.timer > 0) {
            return false;
        }
        Component line = Component.translatable("aqsaqal.ezhelgi.says", name, this.lines.poll());
        if (this.broadcast) {
            for (Player p : level.players()) {
                if (p.distanceToSqr(speaker) < 32 * 32) {
                    p.sendSystemMessage(line);
                }
            }
        } else if (target != null) {
            target.sendSystemMessage(line);
        }
        SoundEvent s = this.sound != null ? this.sound : ModSounds.AKSAKAL_TALK.get();
        level.playSound(null, speaker.getX(), speaker.getY(), speaker.getZ(), s, SoundSource.NEUTRAL, 0.8f,
                0.85f + speaker.getRandom().nextFloat() * 0.15f);
        this.timer = this.delay;
        return true;
    }
}
