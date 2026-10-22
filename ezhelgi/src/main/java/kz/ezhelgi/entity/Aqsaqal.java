package kz.ezhelgi.entity;

import kz.ezhelgi.quest.Quests;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.UUID;

/**
 * Ақсақал — старейшина аула. Сначала рассказывает новости, даёт советы и говорит о жизни,
 * потом просит о помощи. Заданий много: принести вещи, прогнать врагов, найти коня,
 * отыскать петроглифы, обыграть его в тоғыз құмалақ.
 */
public class Aqsaqal extends SteppeVillager {
    public static final int NAMES = 8;
    private static final int LINE_DELAY = 45;

    private final Deque<Component> lines = new ArrayDeque<>();
    private UUID listener;
    private int lineTimer;
    private int nameIndex = -1;

    public Aqsaqal(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return SteppeVillager.createAttributes().add(Attributes.MOVEMENT_SPEED, 0.38).add(Attributes.MAX_HEALTH, 24.0);
    }

    private Component speaker() {
        if (this.nameIndex < 0) {
            this.nameIndex = this.getRandom().nextInt(NAMES);
        }
        return Component.translatable("aqsaqal.ezhelgi.name." + this.nameIndex).withStyle(ChatFormatting.GOLD);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.getItemInHand(hand).getItem() instanceof SpawnEggItem || !this.isAlive()) {
            return super.mobInteract(player, hand);
        }
        if (!(player instanceof ServerPlayer sp) || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.SUCCESS;
        }
        if (!this.lines.isEmpty()) {
            return InteractionResult.SUCCESS; // аксакал ещё говорит
        }
        this.getLookControl().setLookAt(player);
        this.getNavigation().stop();
        Quests.Quest quest = Quests.current(sp);
        if (quest == null) {
            say(sp, Quests.start(sp, this));
            return InteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown()) {
            Quests.abandon(sp);
            say(sp, List.of(Component.translatable("aqsaqal.ezhelgi.abandon")));
            return InteractionResult.SUCCESS;
        }
        List<Component> done = Quests.tryComplete(sp, this);
        if (!done.isEmpty()) {
            say(sp, done);
        } else {
            say(sp, List.of(Component.translatable("aqsaqal.ezhelgi.remind." + this.getRandom().nextInt(4)),
                    Quests.taskLine(sp, quest).copy().withStyle(ChatFormatting.YELLOW),
                    Component.translatable("aqsaqal.ezhelgi.abandon_hint").withStyle(ChatFormatting.DARK_GRAY)));
        }
        return InteractionResult.SUCCESS;
    }

    private void say(ServerPlayer player, List<Component> text) {
        this.lines.clear();
        this.lines.addAll(text);
        this.listener = player.getUUID();
        this.lineTimer = 1;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level) || this.lines.isEmpty()) {
            return;
        }
        Player player = this.listener == null ? null : level.getPlayerByUUID(this.listener);
        if (player == null || player.distanceToSqr(this) > 24 * 24) {
            this.lines.clear();
            return;
        }
        this.getLookControl().setLookAt(player);
        this.getNavigation().stop();
        if (--this.lineTimer <= 0) {
            Component line = this.lines.poll();
            player.sendSystemMessage(Component.translatable("aqsaqal.ezhelgi.says", speaker(), line));
            level.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.AKSAKAL_TALK.get(), SoundSource.NEUTRAL,
                    0.8f, 0.85f + this.getRandom().nextFloat() * 0.1f);
            this.lineTimer = LINE_DELAY;
        }
    }

    @Override
    protected void updateTrades(ServerLevel level) {
        // аксакал не торгует — он даёт задания
    }

    @Override
    public float getVoicePitch() {
        return 0.7f;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("NameIndex", this.nameIndex);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.nameIndex = input.getIntOr("NameIndex", -1);
    }
}
