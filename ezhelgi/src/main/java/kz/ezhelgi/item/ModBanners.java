package kz.ezhelgi.item;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

/** Готовые знамёна: Казахстан, Алаш, ханство и три жуза — во вкладке мода. */
public final class ModBanners {
    private ModBanners() {}

    private static ResourceKey<BannerPattern> key(String ns, String path) {
        return ResourceKey.create(Registries.BANNER_PATTERN, Identifier.fromNamespaceAndPath(ns, path));
    }

    private static ResourceKey<BannerPattern> ez(String path) {
        return key("ezhelgi", path);
    }

    private static ItemStack banner(HolderGetter<BannerPattern> patterns, DyeColor base, String name, Object... layers) {
        ItemStack stack = new ItemStack(Items.BANNER.pick(base));
        BannerPatternLayers.Builder real = new BannerPatternLayers.Builder();
        for (int i = 0; i < layers.length; i += 2) {
            @SuppressWarnings("unchecked")
            ResourceKey<BannerPattern> k = (ResourceKey<BannerPattern>) layers[i];
            DyeColor color = (DyeColor) layers[i + 1];
            patterns.get(k).ifPresent(h -> real.add(h, color));
        }
        stack.set(DataComponents.BANNER_PATTERNS, real.build());
        stack.set(DataComponents.ITEM_NAME, Component.translatable("banner.ezhelgi." + name));
        stack.set(DataComponents.RARITY, Rarity.UNCOMMON);
        return stack;
    }

    public static void addTo(CreativeModeTab.ItemDisplayParameters params, java.util.function.Consumer<ItemStack> output) {
        HolderLookup.Provider holders = params.holders();
        HolderGetter<BannerPattern> p = holders.lookupOrThrow(Registries.BANNER_PATTERN);
        output.accept(banner(p, DyeColor.LIGHT_BLUE, "qazaqstan",
                ez("kz_oiu"), DyeColor.YELLOW, ez("kz_kun"), DyeColor.YELLOW, ez("kz_qyran"), DyeColor.YELLOW));
        output.accept(banner(p, DyeColor.YELLOW, "alash",
                key("minecraft", "stripe_top"), DyeColor.GREEN, key("minecraft", "stripe_bottom"), DyeColor.RED,
                ez("alash_ai"), DyeColor.WHITE, ez("alash_juldyz"), DyeColor.YELLOW));
        output.accept(banner(p, DyeColor.BLUE, "handyq",
                ez("qoshqar_muiz"), DyeColor.YELLOW, ez("shanyraq"), DyeColor.YELLOW));
        output.accept(banner(p, DyeColor.CYAN, "uly_juz",
                ez("qoshqar_muiz"), DyeColor.YELLOW, ez("tamga_uly"), DyeColor.YELLOW));
        output.accept(banner(p, DyeColor.WHITE, "orta_juz",
                ez("qoshqar_muiz"), DyeColor.BLUE, ez("tamga_orta"), DyeColor.BLUE));
        output.accept(banner(p, DyeColor.RED, "kishi_juz",
                ez("qoshqar_muiz"), DyeColor.YELLOW, ez("tamga_kishi"), DyeColor.YELLOW));
    }
}
