package kz.ezhelgi.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Торсық — кожаная фляга. Вмещает до 6 порций қымыза или шұбата.
 * <ul>
 *   <li>ПКМ — отпить одну порцию;</li>
 *   <li>Shift+ПКМ — перелить бутылку қымыза/шұбата из инвентаря во флягу;</li>
 *   <li>ПКМ по кобыле или верблюдице — надоить прямо во флягу.</li>
 * </ul>
 * Количество порций показано полоской прочности.
 */
public class TorsykItem extends Item {
    public static final int CAPACITY = 6;

    public enum Drink { QYMYZ, SHUBAT }

    private final @Nullable Drink drink;

    public TorsykItem(Properties properties, @Nullable Drink drink) {
        super(properties);
        this.drink = drink;
    }

    public @Nullable Drink drink() {
        return this.drink;
    }

    public static int portions(ItemStack stack) {
        if (!(stack.getItem() instanceof TorsykItem t) || t.drink == null) {
            return 0;
        }
        return CAPACITY - stack.getDamageValue();
    }

    /** Можно ли налить сюда этот напиток. */
    public boolean canTake(ItemStack stack, Drink what) {
        return this.drink == null || (this.drink == what && portions(stack) < CAPACITY);
    }

    private static Item filled(Drink d) {
        return d == Drink.QYMYZ ? ModItems.TORSYK_QYMYZ.get() : ModItems.TORSYK_SHUBAT.get();
    }

    /** Добавить порцию: пустой торсык превращается в полный. */
    public static void addPortion(Player player, InteractionHand hand, ItemStack stack, Drink what) {
        int now = portions(stack);
        ItemStack result = new ItemStack(filled(what));
        result.setDamageValue(CAPACITY - Math.min(CAPACITY, now + 1));
        player.setItemInHand(hand, result);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isSecondaryUseActive()) {
            return this.pour(level, player, hand, stack);
        }
        if (portions(stack) > 0) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    /** Перелить бутылку из инвентаря во флягу. */
    private InteractionResult pour(Level level, Player player, InteractionHand hand, ItemStack stack) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack bottle = player.getInventory().getItem(i);
            Drink what = bottle.is(ModItems.KUMYS.get()) ? Drink.QYMYZ : bottle.is(ModItems.SHUBAT.get()) ? Drink.SHUBAT : null;
            if (what == null || !this.canTake(stack, what)) {
                continue;
            }
            if (!level.isClientSide()) {
                bottle.shrink(1);
                addPortion(player, hand, stack, what);
                if (!player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE)) && level instanceof ServerLevel server) {
                    player.spawnAtLocation(server, new ItemStack(Items.GLASS_BOTTLE));
                }
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 1.0f, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (this.drink == null || portions(stack) <= 0) {
            return stack;
        }
        if (level instanceof ServerLevel) {
            if (this.drink == Drink.QYMYZ) {
                entity.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 1200, 0));
                entity.addEffect(new MobEffectInstance(MobEffects.SPEED, 1200, 0));
                if (entity.getRandom().nextFloat() < 0.15f) {
                    entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 160, 0));
                }
            } else {
                entity.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 1200, 0));
                entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0));
            }
            if (entity instanceof Player player) {
                player.getFoodData().eat(3, 0.4f);
            }
        }
        int left = portions(stack) - 1;
        if (left <= 0) {
            return new ItemStack(ModItems.TORSYK.get());
        }
        stack.setDamageValue(CAPACITY - left);
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> builder, TooltipFlag flag) {
        if (this.drink != null) {
            builder.accept(Component.translatable("tooltip.ezhelgi.torsyk.portions", portions(stack), CAPACITY)
                    .withStyle(ChatFormatting.GOLD));
        }
        builder.accept(Component.translatable("tooltip.ezhelgi.torsyk").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, display, builder, flag);
    }
}
