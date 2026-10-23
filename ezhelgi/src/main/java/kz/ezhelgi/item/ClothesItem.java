package kz.ezhelgi.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

/** Национальная одежда: почти не защищает, зато даёт свой бонус. */
public class ClothesItem extends Item {
    private final String kind;

    public ClothesItem(Properties properties, String kind) {
        super(properties);
        this.kind = kind;
    }

    public String kind() {
        return this.kind;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("tooltip.ezhelgi.clothes." + this.kind).withStyle(ChatFormatting.GOLD));
        super.appendHoverText(stack, context, display, builder, flag);
    }
}
