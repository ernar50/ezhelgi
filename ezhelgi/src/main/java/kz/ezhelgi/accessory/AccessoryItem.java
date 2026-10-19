package kz.ezhelgi.accessory;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Золотое украшение в зверином стиле. Носится в слотах аксессуаров
 * (кнопка рядом со слотами брони) и даёт постоянный бонус.
 */
public class AccessoryItem extends Item {
    private final String id;
    private final @Nullable Supplier<Holder<Attribute>> attribute;
    private final double amount;
    private final AttributeModifier.Operation operation;
    private final boolean nightVision;

    public AccessoryItem(Properties properties, String id, @Nullable Supplier<Holder<Attribute>> attribute,
                         double amount, AttributeModifier.Operation operation, boolean nightVision) {
        super(properties);
        this.id = id;
        this.attribute = attribute;
        this.amount = amount;
        this.operation = operation;
        this.nightVision = nightVision;
    }

    public String accessoryId() {
        return this.id;
    }

    public @Nullable Holder<Attribute> attribute() {
        return this.attribute == null ? null : this.attribute.get();
    }

    public double amount() {
        return this.amount;
    }

    public AttributeModifier.Operation operation() {
        return this.operation;
    }

    public boolean nightVision() {
        return this.nightVision;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("tooltip.ezhelgi.accessory").withStyle(ChatFormatting.GOLD));
        builder.accept(Component.translatable("tooltip.ezhelgi." + this.id).withStyle(ChatFormatting.BLUE));
        super.appendHoverText(stack, context, display, builder, flag);
    }
}
