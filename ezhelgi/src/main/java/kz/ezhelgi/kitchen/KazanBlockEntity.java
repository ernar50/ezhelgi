package kz.ezhelgi.kitchen;

import kz.ezhelgi.block.ModBlockEntities;
import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.Supplier;

/** Қазан: вода, продукты, огонь снизу — и через 20 секунд готово блюдо на несколько мисок. */
public class KazanBlockEntity extends SyncedBlockEntity {
    public static final int COOK_TIME = 400;
    public static final int SLOTS = 6;

    public enum Part {
        MEAT, CARROT, POTATO, WHEAT, MILK, KURT, BEETROOT;

        static @Nullable Part of(ItemStack s) {
            if (Cook.isMeat(s)) return MEAT;
            if (s.is(Items.CARROT)) return CARROT;
            if (s.is(Items.POTATO) || s.is(Items.BAKED_POTATO)) return POTATO;
            if (s.is(Items.WHEAT)) return WHEAT;
            if (s.is(Items.MILK_BUCKET)) return MILK;
            if (s.is(ModItems.KURT.get())) return KURT;
            if (s.is(Items.BEETROOT)) return BEETROOT;
            return null;
        }
    }

    public enum Dish {
        SORPA(() -> ModItems.SORPA.get(), 4, EnumSet.of(Part.MEAT)),
        QUYRDAQ(() -> ModItems.QUYRDAQ.get(), 4, EnumSet.of(Part.MEAT, Part.POTATO)),
        PALAU(() -> ModItems.PALAU.get(), 4, EnumSet.of(Part.MEAT, Part.CARROT, Part.WHEAT)),
        NAURYZ_KOZHE(() -> ModItems.NAURYZ_KOZHE.get(), 7,
                EnumSet.of(Part.MEAT, Part.WHEAT, Part.MILK, Part.KURT, Part.CARROT, Part.BEETROOT));

        final Supplier<Item> item;
        final int servings;
        final Set<Part> parts;

        Dish(Supplier<Item> item, int servings, Set<Part> parts) {
            this.item = item;
            this.servings = servings;
            this.parts = parts;
        }

        static @Nullable Dish match(Set<Part> parts) {
            for (Dish d : values()) {
                if (d.parts.equals(parts)) return d;
            }
            return null;
        }
    }

    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
    private int progress;
    private int dish = -1;
    private int servings;

    public KazanBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.KAZAN.get(), pos, state);
    }

    public int stage() {
        return this.getBlockState().getValue(KazanBlock.STAGE);
    }

    Set<Part> parts() {
        EnumSet<Part> set = EnumSet.noneOf(Part.class);
        for (ItemStack s : this.items) {
            Part p = Part.of(s);
            if (p != null) set.add(p);
        }
        return set;
    }

    /** Положить продукт; сообщает игроку результат. true — продукт в казане. */
    boolean add(ItemStack held, net.minecraft.world.entity.player.Player player) {
        Part p = Part.of(held);
        if (p == null) {
            player.sendOverlayMessage(Component.translatable("message.ezhelgi.kazan.not_food"));
            return false;
        }
        if (parts().contains(p)) {
            player.sendOverlayMessage(Component.translatable("message.ezhelgi.kazan.already", held.getHoverName()));
            return false;
        }
        for (int i = 0; i < SLOTS; i++) {
            if (this.items.get(i).isEmpty()) {
                // молоко уходит в казан, ведро возвращается игроку (см. KazanBlock)
                this.items.set(i, p == Part.MILK ? new ItemStack(Items.MILK_BUCKET) : held.copyWithCount(1));
                changed();
                player.sendOverlayMessage(Component.translatable("message.ezhelgi.kazan.added", held.getHoverName()));
                return true;
            }
        }
        player.sendOverlayMessage(Component.translatable("message.ezhelgi.kazan.full"));
        return false;
    }

    /** Подать одну миску. */
    @Nullable ItemStack serve() {
        if (this.dish < 0 || this.servings <= 0) return null;
        ItemStack out = new ItemStack(Dish.values()[this.dish].item.get());
        if (--this.servings <= 0) {
            this.dish = -1;
            setStage(0);
        }
        changed();
        return out;
    }

    void fillWater() {
        setStage(1);
        changed();
    }

    /** Вылить содержимое: продукты возвращаются. */
    void dump(Level level, BlockPos pos) {
        for (int i = 0; i < SLOTS; i++) {
            ItemStack s = this.items.get(i);
            // молоко уже вылито в казан (ведро вернули сразу) — его не вернуть
            if (!s.isEmpty() && !s.is(Items.MILK_BUCKET)) {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, s);
            }
            this.items.set(i, ItemStack.EMPTY);
        }
        this.progress = 0;
        this.dish = -1;
        this.servings = 0;
        setStage(0);
        changed();
    }

    Component status() {
        int st = stage();
        if (st == 0) return Component.translatable("message.ezhelgi.kazan.need_water");
        if (st == 3 && this.dish >= 0) {
            return Component.translatable("message.ezhelgi.kazan.ready", Cook.name(Dish.values()[this.dish].item.get()), this.servings);
        }
        Dish d = Dish.match(parts());
        if (st == 2 && d != null) {
            return Component.translatable("message.ezhelgi.kazan.cooking", Cook.name(d.item.get()), this.progress * 100 / COOK_TIME);
        }
        if (parts().isEmpty()) return Component.translatable("message.ezhelgi.kazan.hint");
        if (d == null) return Component.translatable("message.ezhelgi.kazan.unknown");
        return Component.translatable("message.ezhelgi.kazan.no_fire", Cook.name(d.item.get()));
    }

    private void setStage(int st) {
        if (this.level != null && stage() != st) {
            this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(KazanBlock.STAGE, st), 3);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, KazanBlockEntity be) {
        int st = state.getValue(KazanBlock.STAGE);
        if (st != 1 && st != 2) return;
        boolean hot = Cook.heated(level, pos);
        Dish d = Dish.match(be.parts());
        if (st == 1) {
            if (hot && d != null && level.getGameTime() % 10 == 0) {
                be.progress = 0;
                be.setStage(2);
                be.changed();
            }
            return;
        }
        if (d == null) {
            be.setStage(1);
            return;
        }
        if (!hot) return;
        be.progress++;
        if (level instanceof ServerLevel server) {
            if (be.progress % 8 == 0) {
                server.sendParticles(ParticleTypes.BUBBLE_POP, pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, 2, 0.2, 0.0, 0.2, 0.0);
            }
            if (be.progress % 15 == 0) {
                server.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5, 1, 0.1, 0.0, 0.1, 0.01);
            }
            if (be.progress % 50 == 0) {
                server.playSound(null, pos, ModSounds.KAZAN_BUBBLE.get(), SoundSource.BLOCKS, 0.7f, 0.9f + server.getRandom().nextFloat() * 0.2f);
            }
        }
        if (be.progress >= COOK_TIME) {
            for (int i = 0; i < SLOTS; i++) be.items.set(i, ItemStack.EMPTY);
            be.dish = d.ordinal();
            be.servings = d.servings;
            be.progress = 0;
            be.setStage(3);
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.8f, 0.8f);
            be.changed();
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (this.level != null) {
            for (ItemStack s : this.items) {
                if (!s.isEmpty() && !s.is(Items.MILK_BUCKET)) {
                    Containers.dropItemStack(this.level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, s.copy());
                }
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt("Progress", this.progress);
        output.putInt("Dish", this.dish);
        output.putInt("Servings", this.servings);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        for (int i = 0; i < SLOTS; i++) this.items.set(i, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, this.items);
        this.progress = input.getIntOr("Progress", 0);
        this.dish = input.getIntOr("Dish", -1);
        this.servings = input.getIntOr("Servings", 0);
    }
}
