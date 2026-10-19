package kz.ezhelgi.accessory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Окно аксессуаров: 4 слота для золотых украшений + инвентарь игрока. */
public class AccessoryMenu extends AbstractContainerMenu {
    public static final int SLOTS = Accessories.SIZE;
    /** Координаты слотов украшений в окне (совпадают с текстурой). */
    public static final int[][] SLOT_POS = {{53, 34}, {71, 34}, {89, 34}, {107, 34}};

    private final Container accessories;
    private final Player player;

    /** Клиентская сторона: пустой контейнер, содержимое придёт с сервера. */
    public AccessoryMenu(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(SLOTS));
    }

    public AccessoryMenu(int id, Inventory inventory, Container accessories) {
        super(ModMenus.ACCESSORIES.get(), id);
        this.accessories = accessories;
        this.player = inventory.player;

        for (int i = 0; i < SLOTS; i++) {
            this.addSlot(new Slot(accessories, i, SLOT_POS[i][0], SLOT_POS[i][1]) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() instanceof AccessoryItem;
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            });
        }
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index < SLOTS) {
            if (!this.moveItemStackTo(stack, SLOTS, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!(stack.getItem() instanceof AccessoryItem) || !this.moveItemStackTo(stack, 0, SLOTS, false)) {
                return ItemStack.EMPTY;
            }
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return copy;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.isAlive();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (player instanceof ServerPlayer serverPlayer) {
            Accessories.save(serverPlayer, this.accessories);
            Accessories.refresh(serverPlayer);
        }
    }
}
