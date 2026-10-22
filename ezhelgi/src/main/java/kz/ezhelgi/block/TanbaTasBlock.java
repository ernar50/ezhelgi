package kz.ezhelgi.block;

import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.quest.PlayerData;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

/**
 * Таңба тас — скала с петроглифами Тамгалы. Восемь рисунков: солнцеголовый,
 * хоровод, бык, конь, олень, верблюд, лучник и колесница.
 * ПКМ — рассмотреть рисунок. Нашёл все восемь — получаешь награду.
 */
public class TanbaTasBlock extends HorizontalDirectionalBlock {
    public static final int GLYPHS = 8;
    public static final IntegerProperty GLYPH = IntegerProperty.create("glyph", 0, GLYPHS - 1);
    public static final String[] NAMES = {"kunbasty", "biy", "ogiz", "at", "bugy", "tuie", "mergen", "arba"};
    private static final String KEY = "tanba";

    public TanbaTasBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(GLYPH, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, GLYPH);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        // поставленный камень получает случайный рисунок
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(GLYPH, context.getLevel().getRandom().nextInt(GLYPHS));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!(level instanceof ServerLevel server)) {
            return InteractionResult.SUCCESS;
        }
        int glyph = state.getValue(GLYPH);
        CompoundTag data = PlayerData.get(player);
        int mask = data.getIntOr(KEY, 0);
        Component name = Component.translatable("glyph.ezhelgi." + NAMES[glyph]).withStyle(ChatFormatting.GOLD);
        if ((mask & (1 << glyph)) != 0) {
            player.sendOverlayMessage(Component.translatable("message.ezhelgi.tanba.known", name));
            return InteractionResult.SUCCESS;
        }
        mask |= 1 << glyph;
        data.putInt(KEY, mask);
        int found = Integer.bitCount(mask);
        player.sendSystemMessage(Component.translatable("message.ezhelgi.tanba.found", name, found, GLYPHS));
        player.sendSystemMessage(Component.translatable("glyph.ezhelgi." + NAMES[glyph] + ".desc").withStyle(ChatFormatting.GRAY));
        server.playSound(null, pos, ModSounds.PETROGLYPH.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        server.sendParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 30, 0.5, 0.5, 0.5, 0.6);
        if (found == GLYPHS && !data.getBooleanOr("tanba_reward", false)) {
            data.putBoolean("tanba_reward", true);
            player.sendSystemMessage(Component.translatable("message.ezhelgi.tanba.all").withStyle(ChatFormatting.AQUA));
            give(player, new ItemStack(ModItems.TUMARSHA_TEMPLATE.get()));
            give(player, new ItemStack(ModItems.SAKA_GOLD.get(), 12));
            give(player, new ItemStack(ModItems.TANBA_TAS.get(), 8));
            give(player, new ItemStack(Items.EXPERIENCE_BOTTLE, 16));
            server.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.PLAYERS, 1.0f, 1.0f);
            server.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1.0, player.getZ(), 60, 0.6, 1.0, 0.6, 0.4);
        }
        return InteractionResult.SUCCESS;
    }

    private static void give(Player player, ItemStack stack) {
        if (!player.getInventory().add(stack) && player.level() instanceof ServerLevel level) {
            player.spawnAtLocation(level, stack);
        }
    }
}
