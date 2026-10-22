package kz.ezhelgi.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/** Часть эпического доспеха героя: в подсказке — легенда и бонус полного набора. */
public class EpicArmorItem extends Item {
    private final String set;

    public EpicArmorItem(Properties properties, String set) {
        super(properties);
        this.set = set;
    }

    public String set() {
        return this.set;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("tooltip.ezhelgi.set." + this.set).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
        builder.accept(Component.translatable("tooltip.ezhelgi.set.full").withStyle(ChatFormatting.GOLD));
        builder.accept(Component.translatable("tooltip.ezhelgi.set." + this.set + ".bonus").withStyle(ChatFormatting.YELLOW));
        super.appendHoverText(stack, context, display, builder, flag);
    }
}
