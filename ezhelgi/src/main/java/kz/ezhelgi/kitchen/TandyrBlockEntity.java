package kz.ezhelgi.kitchen;

import kz.ezhelgi.block.ModBlockEntities;
import kz.ezhelgi.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** Тандыр: на стенках печи пекутся четыре лепёшки или самсы, пока горит огонь. */
public class TandyrBlockEntity extends SyncedBlockEntity {
    public static final int SLOTS = 4;
    public static final int BAKE_TIME = 300;
    public static final int MAX_FUEL = 6400;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private final int[] progress = new int[SLOTS];
    private int fuel;

    public TandyrBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TANDYR.get(), pos, state);
    }

    private int freeSlot() {
        for (int i = 0; i < SLOTS; i++) {
            if (this.items.get(i).isEmpty()) return i;
        }
        return -1;
    }

    boolean addFuel(ItemStack stack) {
        int f = Cook.fuel(stack);
        if (f <= 0 || this.fuel + f > MAX_FUEL + 1600) return false;
        this.fuel += f;
        setLit(true);
        changed();
        return true;
    }

    /** Положить тесто (2 пшеницы → нан) или мясо (мясо + 1 пшеница из сумки → самса). */
    boolean addDough(Player player, ItemStack held) {
        int slot = freeSlot();
        if (slot < 0) {
            player.sendOverlayMessage(Component.translatable("message.ezhelgi.tandyr.full"));
            return false;
        }
        if (held.is(Items.WHEAT)) {
            if (held.getCount() < 2 && !player.getAbilities().instabuild) {
                player.sendOverlayMessage(Component.translatable("message.ezhelgi.tandyr.need_wheat"));
                return false;
            }
            this.items.set(slot, new ItemStack(Items.WHEAT, 2));
            Cook.consume(player, held, 2);
        } else if (Cook.isRawMeat(held)) {
            int w = player.getInventory().findSlotMatchingItem(new ItemStack(Items.WHEAT));
            if (w < 0 && !player.getAbilities().instabuild) {
                player.sendOverlayMessage(Component.translatable("message.ezhelgi.tandyr.need_wheat"));
                return false;
            }
            if (w >= 0 && !player.getAbilities().instabuild) {
                player.getInventory().getItem(w).shrink(1);
            }
            this.items.set(slot, held.copyWithCount(1));
            Cook.consume(player, held, 1);
        } else {
            return false;
        }
        this.progress[slot] = 0;
        if (this.fuel <= 0) {
            player.sendOverlayMessage(Component.translatable("message.ezhelgi.tandyr.need_fuel"));
        }
        changed();
        return true;
    }

    Component status() {
        int n = 0;
        for (ItemStack s : this.items) if (!s.isEmpty()) n++;
        return Component.translatable("message.ezhelgi.tandyr.status", n, SLOTS, this.fuel / 20);
    }

    private void setLit(boolean lit) {
        BlockState st = this.getBlockState();
        if (this.level != null && st.getValue(BlockStateProperties.LIT) != lit) {
            this.level.setBlock(this.getBlockPos(), st.setValue(BlockStateProperties.LIT, lit), 3);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TandyrBlockEntity be) {
        if (be.fuel <= 0) {
            if (state.getValue(BlockStateProperties.LIT)) be.setLit(false);
            return;
        }
        be.fuel--;
        if (!state.getValue(BlockStateProperties.LIT)) be.setLit(true);
        for (int i = 0; i < SLOTS; i++) {
            ItemStack in = be.items.get(i);
            if (in.isEmpty()) continue;
            if (++be.progress[i] >= BAKE_TIME) {
                ItemStack out = new ItemStack(in.is(Items.WHEAT) ? ModItems.NAN.get() : ModItems.SAMSA.get());
                be.items.set(i, ItemStack.EMPTY);
                be.progress[i] = 0;
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, out);
                level.playSound(null, pos, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0f, 1.2f);
                be.changed();
            }
        }
        if (level instanceof ServerLevel server && level.getGameTime() % 12 == 0) {
            server.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.05, pos.getZ() + 0.5, 1, 0.08, 0.0, 0.08, 0.01);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this.level != null) Containers.dropContents(this.level, pos, this.items);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putIntArray("Progress", this.progress.clone());
        output.putInt("Fuel", this.fuel);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        for (int i = 0; i < SLOTS; i++) this.items.set(i, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        int[] p = input.getIntArray("Progress").orElse(new int[SLOTS]);
        System.arraycopy(p, 0, this.progress, 0, Math.min(p.length, SLOTS));
        this.fuel = input.getIntOr("Fuel", 0);
    }
}
