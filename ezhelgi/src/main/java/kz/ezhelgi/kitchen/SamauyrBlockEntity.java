package kz.ezhelgi.kitchen;

import kz.ezhelgi.block.ModBlockEntities;
import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/** Самауыр: вода, угли в трубе — и через 10 секунд кипяток на 8 пиал чая. С молоком — сүтті шай. */
public class SamauyrBlockEntity extends SyncedBlockEntity {
    public static final int CUPS = 8;
    public static final int BOIL = 200;
    private int water;
    private int milk;
    private int fuel;
    private int heat;

    public SamauyrBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SAMAUYR.get(), pos, state);
    }

    boolean fillWater() {
        if (this.water >= CUPS) return false;
        this.water = CUPS;
        changed();
        return true;
    }

    boolean addMilk() {
        if (this.milk >= CUPS) return false;
        this.milk = CUPS;
        changed();
        return true;
    }

    boolean addFuel(ItemStack stack) {
        int f = Cook.fuel(stack);
        if (f <= 0 || this.fuel > 3200) return false;
        this.fuel += f;
        changed();
        return true;
    }

    /** Налить пиалу (стеклянный пузырёк). null — ещё не вскипел или пусто. */
    @Nullable ItemStack pour() {
        if (this.water <= 0 || this.heat < BOIL) return null;
        this.water--;
        ItemStack out;
        if (this.milk > 0) {
            this.milk--;
            out = new ItemStack(ModItems.SUTTI_SHAI.get());
        } else {
            out = new ItemStack(ModItems.QARA_SHAI.get());
        }
        changed();
        return out;
    }

    Component status() {
        if (this.water <= 0) return Component.translatable("message.ezhelgi.samauyr.need_water");
        if (this.fuel <= 0 && this.heat < BOIL) return Component.translatable("message.ezhelgi.samauyr.need_fuel");
        if (this.heat < BOIL) return Component.translatable("message.ezhelgi.samauyr.heating", this.heat * 100 / BOIL);
        return Component.translatable("message.ezhelgi.samauyr.ready", this.water, this.milk);
    }

    private void setLit(boolean lit) {
        BlockState st = this.getBlockState();
        if (this.level != null && st.getValue(BlockStateProperties.LIT) != lit) {
            this.level.setBlock(this.getBlockPos(), st.setValue(BlockStateProperties.LIT, lit), 3);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SamauyrBlockEntity be) {
        boolean burning = be.fuel > 0 && be.water > 0;
        if (burning) {
            be.fuel--;
            if (be.heat < BOIL && ++be.heat == BOIL) {
                level.playSound(null, pos, ModSounds.SAMAUYR_BOIL.get(), SoundSource.BLOCKS, 0.8f, 1.0f);
            }
        } else if (be.heat > 0 && level.getGameTime() % 4 == 0) {
            be.heat--;
        }
        be.setLit(burning);
        if (level instanceof ServerLevel server && be.heat >= BOIL && level.getGameTime() % 10 == 0) {
            server.sendParticles(ParticleTypes.CLOUD, pos.getX() + 0.5, pos.getY() + 1.15, pos.getZ() + 0.5, 1, 0.03, 0.0, 0.03, 0.01);
            if (level.getGameTime() % 160 == 0) {
                server.playSound(null, pos, ModSounds.SAMAUYR_BOIL.get(), SoundSource.BLOCKS, 0.35f, 1.1f);
            }
        }
        if (level.getGameTime() % 40 == 0) be.setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("Water", this.water);
        output.putInt("Milk", this.milk);
        output.putInt("Fuel", this.fuel);
        output.putInt("Heat", this.heat);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.water = input.getIntOr("Water", 0);
        this.milk = input.getIntOr("Milk", 0);
        this.fuel = input.getIntOr("Fuel", 0);
        this.heat = input.getIntOr("Heat", 0);
    }
}
