package kz.ezhelgi.accessory;

import kz.ezhelgi.EzhelgiMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModMenus {
    private ModMenus() {}

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, EzhelgiMod.MODID);

    public static final RegistryObject<MenuType<AccessoryMenu>> ACCESSORIES = MENUS.register("accessories",
            () -> IForgeMenuType.create((id, inventory, data) -> new AccessoryMenu(id, inventory)));
}
