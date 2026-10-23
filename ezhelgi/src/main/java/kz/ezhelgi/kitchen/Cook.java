package kz.ezhelgi.kitchen;

import kz.ezhelgi.block.ModBlocks;
import kz.ezhelgi.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/** Общие правила кухни аула: что считается мясом, топливом и огнём. */
public final class Cook {
    private Cook() {}

    public static boolean isRawMeat(ItemStack s) {
        return s.is(Items.BEEF) || s.is(Items.MUTTON) || s.is(Items.CHICKEN) || s.is(Items.RABBIT)
                || s.is(ModItems.JYLQY_ETI.get()) || s.is(ModItems.AQBOKEN_ETI.get()) || s.is(ModItems.AN_ETI.get());
    }

    public static boolean isMeat(ItemStack s) {
        return isRawMeat(s) || s.is(Items.COOKED_BEEF) || s.is(Items.COOKED_MUTTON) || s.is(Items.COOKED_CHICKEN)
                || s.is(Items.COOKED_RABBIT) || s.is(ModItems.PISKEN_JYLQY_ETI.get()) || s.is(ModItems.PISKEN_AQBOKEN_ETI.get())
                || s.is(ModItems.PISKEN_AN_ETI.get()) || s.is(ModItems.KAZY.get());
    }

    /** Сколько тиков горит предмет в тандыре или самоваре (0 — не топливо). */
    public static int fuel(ItemStack s) {
        if (s.is(ModItems.SEKSEUIL.get())) {
            return 3200;
        }
        if (s.is(Items.COAL) || s.is(Items.CHARCOAL)) {
            return 1600;
        }
        if (s.is(ItemTags.LOGS_THAT_BURN)) {
            return 400;
        }
        if (s.is(ItemTags.PLANKS)) {
            return 200;
        }
        if (s.is(Items.STICK) || s.is(Items.DRIED_KELP) || s.is(ModItems.QUMALAQ.get())) {
            return 100;
        }
        return 0;
    }

    /** Есть ли огонь под блоком: костёр, пламя, магма, лава или горящий тандыр. */
    public static boolean heated(Level level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        if (below.getBlock() instanceof CampfireBlock) {
            return below.getValue(CampfireBlock.LIT);
        }
        if (below.is(ModBlocks.TANDYR.get())) {
            return below.getValue(BlockStateProperties.LIT);
        }
        return below.is(Blocks.FIRE) || below.is(Blocks.SOUL_FIRE) || below.is(Blocks.MAGMA_BLOCK) || below.is(Blocks.LAVA);
    }

    /** Забрать один предмет из руки и выдать результат (ведро, миска, блюдо). */
    public static void exchange(Player player, InteractionHand hand, ItemStack result) {
        ItemStack held = player.getItemInHand(hand);
        player.setItemInHand(hand, ItemUtils.createFilledResult(held, player, result, false));
    }

    /** Израсходовать один предмет (кроме творческого режима). */
    public static void consume(Player player, ItemStack stack, int n) {
        if (!player.getAbilities().instabuild) {
            stack.shrink(n);
        }
    }

    public static Component name(Item item) {
        return new ItemStack(item).getHoverName();
    }

    public static void give(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.spawnAtLocation((net.minecraft.server.level.ServerLevel) player.level(), stack);
        }
    }
}
