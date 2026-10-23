package kz.ezhelgi.kitchen;

import kz.ezhelgi.block.ModBlockEntities;
import kz.ezhelgi.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Кептіргіш — решётка для сушки. Сырое мясо превращается в қақ ет (вяленое мясо),
 * ведро молока — в четыре құрта. На солнце сохнет быстро, в тени и ночью — медленно.
 */
public class KeptirgishBlockEntity extends SyncedBlockEntity {
    public static final int SLOTS = 4;
    public static final int DRY_TIME = 3600;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    /** -1 — уже высохло. */
    private final int[] progress = new int[SLOTS];

    public KeptirgishBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KEPTIRGISH.get(), pos, state);
    }

    public NonNullList<ItemStack> items() {
        return this.items;
    }

    public static boolean canDry(ItemStack s) {
        return Cook.isRawMeat(s) || s.is(Items.MILK_BUCKET);
    }

    boolean add(Player player, ItemStack held) {
        for (int i = 0; i < SLOTS; i++) {
            if (this.items.get(i).isEmpty()) {
                this.items.set(i, held.copyWithCount(1));
                this.progress[i] = 0;
                if (!player.getAbilities().instabuild) {
                    held.shrink(1);
                }
                changed();
                return true;
            }
        }
        player.sendOverlayMessage(Component.translatable("message.ezhelgi.keptirgish.full"));
        return false;
    }

    /** Снять всё, что высохло. */
    int takeDry(Player player) {
        int n = 0;
        for (int i = 0; i < SLOTS; i++) {
            if (!this.items.get(i).isEmpty() && this.progress[i] < 0) {
                Cook.give(player, this.items.get(i).copy());
                this.items.set(i, ItemStack.EMPTY);
                this.progress[i] = 0;
                n++;
            }
        }
        if (n > 0) changed();
        return n;
    }

    Component status() {
        int wet = 0, dry = 0, pct = 0;
        for (int i = 0; i < SLOTS; i++) {
            if (this.items.get(i).isEmpty()) continue;
            if (this.progress[i] < 0) dry++;
            else {
                wet++;
                pct = Math.max(pct, this.progress[i] * 100 / DRY_TIME);
            }
        }
        if (wet == 0 && dry == 0) return Component.translatable("message.ezhelgi.keptirgish.hint");
        return Component.translatable("message.ezhelgi.keptirgish.status", wet, pct);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, KeptirgishBlockEntity be) {
        if (level.getGameTime() % 4 != 0) return;
        boolean sunny = level.isBrightOutside() && level.canSeeSky(pos.above()) && !level.isRainingAt(pos.above());
        int step = sunny ? 4 : 1;
        for (int i = 0; i < SLOTS; i++) {
            ItemStack s = be.items.get(i);
            if (s.isEmpty() || be.progress[i] < 0) continue;
            be.progress[i] += step;
            if (be.progress[i] >= DRY_TIME) {
                if (s.is(Items.MILK_BUCKET)) {
                    be.items.set(i, new ItemStack(ModItems.KURT.get(), 4));
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.3, pos.getZ() + 0.5, new ItemStack(Items.BUCKET));
                } else {
                    be.items.set(i, new ItemStack(ModItems.QAQ_ET.get()));
                }
                be.progress[i] = -1;
                be.changed();
            }
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this.level != null) Containers.dropContents(this.level, pos, this.items);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items, true);
        output.putIntArray("Progress", this.progress.clone());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        for (int i = 0; i < SLOTS; i++) this.items.set(i, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        int[] p = input.getIntArray("Progress").orElse(new int[SLOTS]);
        System.arraycopy(p, 0, this.progress, 0, Math.min(p.length, SLOTS));
    }
}
