package kz.ezhelgi.entity;

import kz.ezhelgi.aul.AulEvents;
import kz.ezhelgi.aul.Speech;
import kz.ezhelgi.quest.PlayerData;
import kz.ezhelgi.quest.Quests;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;

/**
 * Ақсақал — старейшина аула. Сначала рассказывает новости, даёт советы и говорит о жизни,
 * потом просит о помощи. Раз в день благословляет (бата) на час. Иногда объявляет праздник:
 * той, айтыс, көкпар или бәйге.
 */
public class Aqsaqal extends SteppeVillager {
    public static final int NAMES = 8;
    private static final int LINE_DELAY = 45;

    private final Speech speech = new Speech();
    private int nameIndex = -1;
    private long lastEventDay = -1;
    private long lastRaidDay = -100;
    private long lastWolfDay = -1;

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

    /** Объявление для всех вокруг (праздники). */
    public void announce(List<Component> text) {
        this.speech.announce(text, 60);
    }

    private record Blessing(Holder<MobEffect> effect, int amp, String key) {}

    private static List<Blessing> blessings() {
        return List.of(new Blessing(MobEffects.LUCK, 0, "luck"), new Blessing(MobEffects.REGENERATION, 0, "regen"),
                new Blessing(MobEffects.HEALTH_BOOST, 1, "health"), new Blessing(MobEffects.SPEED, 0, "speed"),
                new Blessing(MobEffects.HASTE, 0, "haste"), new Blessing(MobEffects.RESISTANCE, 0, "resist"));
    }

    /** Бата — благословение раз в игровой день, на 1 час реального времени. */
    private List<Component> bata(ServerPlayer player, ServerLevel level) {
        List<Component> out = new ArrayList<>();
        CompoundTag data = PlayerData.get(player);
        long day = level.getOverworldClockTime() / 24000L;
        if (data.getLongOr("bata_day", -1L) == day) {
            return out;
        }
        data.putLong("bata_day", day);
        Blessing b = blessings().get(this.getRandom().nextInt(blessings().size()));
        player.addEffect(new MobEffectInstance(b.effect(), 20 * 60 * 60, b.amp(), false, true, true));
        out.add(Component.translatable("aqsaqal.ezhelgi.bata_give." + b.key()).withStyle(ChatFormatting.GREEN));
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.5, player.getZ(),
                12, 0.4, 0.4, 0.4, 0.0);
        return out;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.getItemInHand(hand).getItem() instanceof SpawnEggItem || !this.isAlive()) {
            return super.mobInteract(player, hand);
        }
        if (!(player instanceof ServerPlayer sp) || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.SUCCESS;
        }
        if (this.speech.busy()) {
            return InteractionResult.SUCCESS;
        }
        ServerLevel level = sp.level();
        this.getLookControl().setLookAt(player);
        this.getNavigation().stop();
        List<Component> lines = new ArrayList<>();
        Quests.Quest quest = Quests.current(sp);
        if (quest == null) {
            lines.addAll(Quests.start(sp, this));
        } else if (player.isShiftKeyDown()) {
            Quests.abandon(sp);
            lines.add(Component.translatable("aqsaqal.ezhelgi.abandon"));
        } else {
            List<Component> done = Quests.tryComplete(sp, this);
            if (!done.isEmpty()) {
                lines.addAll(done);
            } else {
                lines.add(Component.translatable("aqsaqal.ezhelgi.remind." + this.getRandom().nextInt(4)));
                lines.add(Quests.taskLine(sp, quest).copy().withStyle(ChatFormatting.YELLOW));
                lines.add(Component.translatable("aqsaqal.ezhelgi.abandon_hint").withStyle(ChatFormatting.DARK_GRAY));
            }
        }
        lines.addAll(bata(sp, level));
        this.speech.sayTo(sp, lines, LINE_DELAY);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        this.speech.tick(level, this, speaker());
        if (this.tickCount % 200 == 37 && !this.isBaby()) {
            long day = level.getOverworldClockTime() / 24000L;
            if (day != this.lastEventDay && level.isBrightOutside() && level.getNearestPlayer(this, 40.0) != null) {
                this.lastEventDay = day;
                // набег джунгар — редко: не раньше 5-го дня и не чаще раза в 8 дней
                if (day >= 4 && day - this.lastRaidDay >= 8 && this.getRandom().nextFloat() < 0.25f) {
                    this.lastRaidDay = day;
                    AulEvents.start(this, level, AulEvents.Type.RAID);
                } else if (this.getRandom().nextFloat() < 0.6f) {
                    // праздник бывает не каждый день
                    AulEvents.maybeStart(this, level, day);
                }
            } else if (day != this.lastWolfDay && level.isDarkOutside() && level.getNearestPlayer(this, 48.0) != null) {
                // голодная волчья стая приходит к загонам — не каждую ночь
                this.lastWolfDay = day;
                if (this.getRandom().nextFloat() < 0.3f) {
                    AulEvents.start(this, level, AulEvents.Type.WOLVES);
                }
            }
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
        output.putLong("LastEventDay", this.lastEventDay);
        output.putLong("LastRaidDay", this.lastRaidDay);
        output.putLong("LastWolfDay", this.lastWolfDay);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.nameIndex = input.getIntOr("NameIndex", -1);
        this.lastEventDay = input.getLongOr("LastEventDay", -1L);
        this.lastRaidDay = input.getLongOr("LastRaidDay", -100L);
        this.lastWolfDay = input.getLongOr("LastWolfDay", -1L);
    }
}
