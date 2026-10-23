package kz.ezhelgi.nature;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

/** Блок-предмет, который горит в печи дольше обычного (саксаул — лучшие дрова пустыни). */
public class FuelBlockItem extends BlockItem {
    private final int burnTime;

    public FuelBlockItem(Block block, Properties properties, int burnTime) {
        super(block, properties);
        this.burnTime = burnTime;
    }

    /** Forge: время горения в печи (см. также событие в NatureEvents). */
    public int getBurnTime(ItemStack stack, @Nullable RecipeType<?> recipeType) {
        return this.burnTime;
    }
}
