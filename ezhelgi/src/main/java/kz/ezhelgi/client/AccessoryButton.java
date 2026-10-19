package kz.ezhelgi.client;

import kz.ezhelgi.accessory.Accessories;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ScreenEvent;

/** Кнопка «Аксессуары» рядом со слотами брони в инвентаре. */
public final class AccessoryButton {
    private AccessoryButton() {}

    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof InventoryScreen screen) {
            int x = screen.getGuiLeft() - 18;
            int y = screen.getGuiTop() + 8;
            Button button = Button.builder(Component.literal("◆"), b -> Accessories.requestOpen())
                    .bounds(x, y, 16, 16)
                    .tooltip(Tooltip.create(Component.translatable("container.ezhelgi.accessories")))
                    .build();
            event.addListener(button);
        }
    }
}
