package kz.ezhelgi.client;

import kz.ezhelgi.EzhelgiMod;
import kz.ezhelgi.accessory.AccessoryMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

/** Окно аксессуаров: 4 золотых слота над инвентарём. */
public class AccessoryScreen extends AbstractContainerScreen<AccessoryMenu> {
    private static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "textures/gui/accessories.png");

    public AccessoryScreen(AccessoryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
