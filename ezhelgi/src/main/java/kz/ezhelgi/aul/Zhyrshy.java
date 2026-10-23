package kz.ezhelgi.aul;

import kz.ezhelgi.entity.SteppeVillager;
import kz.ezhelgi.quest.PlayerData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.util.ArrayList;
import java.util.List;

/**
 * Жыршы — сказитель эпоса. Каждый раз рассказывает следующую часть жыра:
 * «Қобыланды батыр», «Алпамыс батыр», «Ер Тарғын». Дослушал эпос до конца —
 * жыршы подскажет, где искать доспехи героя, и даст карту.
 */
public class Zhyrshy extends SteppeVillager {
    public static final String[] EPICS = {"qobylandy", "alpamys", "er_targyn"};
    public static final int PARTS = 4;
    public static final int LINES = 3;

    private final Speech speech = new Speech();

    public Zhyrshy(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || !(player instanceof ServerPlayer sp) || this.isBaby()) {
            return InteractionResult.SUCCESS;
        }
        if (this.speech.busy()) {
            return InteractionResult.SUCCESS;
        }
        CompoundTag data = PlayerData.get(sp);
        List<Component> lines = new ArrayList<>();
        String epic = null;
        int part = 0;
        for (String e : EPICS) {
            int heard = data.getIntOr("zhyr_" + e, 0);
            if (heard < PARTS) {
                epic = e;
                part = heard;
                break;
            }
        }
        if (epic == null) {
            lines.add(Component.translatable("zhyr.ezhelgi.all_heard"));
            lines.add(Component.translatable("aqsaqal.ezhelgi.wisdom." + this.getRandom().nextInt(30)).withStyle(ChatFormatting.ITALIC));
            this.speech.sayTo(sp, lines, 50);
            return InteractionResult.SUCCESS;
        }
        if (part == 0) {
            lines.add(Component.translatable("zhyr.ezhelgi." + epic + ".title").withStyle(ChatFormatting.GOLD));
        }
        for (int i = 0; i < LINES; i++) {
            lines.add(Component.translatable("zhyr.ezhelgi." + epic + "." + part + "." + i).withStyle(ChatFormatting.ITALIC));
        }
        data.putInt("zhyr_" + epic, part + 1);
        if (part + 1 < PARTS) {
            lines.add(Component.translatable("zhyr.ezhelgi.next", part + 1, PARTS).withStyle(ChatFormatting.GRAY));
        } else {
            lines.add(Component.translatable("zhyr.ezhelgi." + epic + ".hint").withStyle(ChatFormatting.YELLOW));
            giveMap(sp, epic);
        }
        this.speech.sayTo(sp, lines, 70);
        return InteractionResult.SUCCESS;
    }

    /** Карта к месту, где можно найти доспехи героя. */
    private void giveMap(ServerPlayer player, String epic) {
        ServerLevel level = player.level();
        TagKey<Structure> tag = TagKey.create(Registries.STRUCTURE, Identifier.fromNamespaceAndPath("ezhelgi", "zhyr_" + epic));
        BlockPos target = level.findNearestMapStructure(tag, this.blockPosition(), 100, true);
        if (target == null) {
            player.sendSystemMessage(Component.translatable("zhyr.ezhelgi.no_map"));
            return;
        }
        ItemStack map = MapItem.create(level, target.getX(), target.getZ(), (byte) 2, true, true);
        MapItem.renderBiomePreviewMap(level, map);
        MapItemSavedData.addTargetDecoration(map, target, "+", MapDecorationTypes.RED_X);
        map.set(DataComponents.ITEM_NAME, Component.translatable("zhyr.ezhelgi." + epic + ".map"));
        if (!player.getInventory().add(map)) {
            player.spawnAtLocation(level, map);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel level && this.speech.tick(level, this,
                Component.translatable("entity.ezhelgi.zhyrshy").withStyle(ChatFormatting.GOLD))) {
            this.swingForAttack(InteractionHand.MAIN_HAND);
        }
        if (!this.level().isClientSide() && this.tickCount == 5 && this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(kz.ezhelgi.item.ModItems.DOMBYRA.get()));
            this.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
        }
    }

    @Override
    protected void updateTrades(ServerLevel level) {
    }
}
