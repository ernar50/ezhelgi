package kz.ezhelgi.accessory;

import kz.ezhelgi.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

/** Список всех украшений мода (для снятия бонусов, когда украшение сняли). */
public final class AccessoryRegistry {
    private AccessoryRegistry() {}

    public static List<AccessoryItem> all() {
        List<AccessoryItem> list = new ArrayList<>();
        for (RegistryObject<Item> item : ModItems.ACCESSORIES) {
            if (item.get() instanceof AccessoryItem accessory) {
                list.add(accessory);
            }
        }
        return list;
    }
}
