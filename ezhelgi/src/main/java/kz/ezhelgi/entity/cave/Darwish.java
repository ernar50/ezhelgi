package kz.ezhelgi.entity.cave;

import kz.ezhelgi.entity.SteppeVillager;
import kz.ezhelgi.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * Дәруіш — старец-отшельник в пещерной келье. Делится мудростью и меняет
 * редкие зачарованные книги (на высшем уровне) на сакское золото и рукописи.
 */
public class Darwish extends SteppeVillager {
    public static final int ADVICE = 12;

    public Darwish(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && !this.level().isClientSide()) {
            player.sendSystemMessage(Component.translatable("aqsaqal.ezhelgi.says",
                    Component.translatable("entity.ezhelgi.darwish").withStyle(ChatFormatting.DARK_AQUA),
                    Component.translatable("darwish.ezhelgi.advice." + this.getRandom().nextInt(ADVICE)).withStyle(ChatFormatting.ITALIC)));
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected void updateTrades(ServerLevel level) {
        MerchantOffers offers = this.getOffers();
        RandomSource r = this.getRandom();
        for (int i = 0; i < 5; i++) {
            ItemStack book = maxBook(level, r);
            if (!book.isEmpty()) {
                offers.add(new MerchantOffer(new ItemCost(ModItems.SAKA_GOLD.get(), 6 + r.nextInt(8)), book, 2, 10, 0.05f));
            }
        }
        offers.add(new MerchantOffer(new ItemCost(ModItems.QOLJAZBA.get(), 2), maxBook(level, r), 3, 10, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(ModItems.SAKA_GOLD.get(), 3), new ItemStack(Items.EXPERIENCE_BOTTLE, 8), 6, 5, 0.05f));
        offers.add(new MerchantOffer(new ItemCost(Items.BOOK, 4), new ItemStack(ModItems.SAKA_GOLD.get(), 1), 12, 2, 0.05f));
        offers.removeIf(o -> o.getResult().isEmpty());
    }

    /** Зачарованная книга самого высокого уровня. */
    private static ItemStack maxBook(ServerLevel level, RandomSource random) {
        Optional<HolderSet.Named<Enchantment>> tag = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .get(EnchantmentTags.TRADEABLE);
        if (tag.isEmpty()) return ItemStack.EMPTY;
        Optional<Holder<Enchantment>> ench = tag.get().getRandomElement(random);
        if (ench.isEmpty()) return ItemStack.EMPTY;
        return EnchantmentHelper.createBook(new EnchantmentInstance(ench.get(), ench.get().value().getMaxLevel()));
    }
}
