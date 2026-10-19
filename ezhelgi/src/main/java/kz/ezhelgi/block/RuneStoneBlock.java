package kz.ezhelgi.block;

import kz.ezhelgi.entity.BatyrAruagy;
import kz.ezhelgi.entity.ModEntities;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Руна тас — камень с древнетюркскими (орхоно-енисейскими) рунами.
 * Пока руны светятся, камень можно пробудить (ПКМ): на минуту даётся
 * случайная сила — или из-под земли поднимается нежить. После этого руны гаснут.
 * Погасший камень снова загорается, если долго простоит ночью под открытым небом.
 */
public class RuneStoneBlock extends Block {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    /** Руны-благословения: эффект и имя руны для сообщения. */
    private record Blessing(Holder<MobEffect> effect, int amplifier, String rune) {}

    private static List<Blessing> blessings() {
        return List.of(
                new Blessing(MobEffects.STRENGTH, 1, "kuch"),
                new Blessing(MobEffects.SPEED, 1, "jel"),
                new Blessing(MobEffects.REGENERATION, 1, "sau"),
                new Blessing(MobEffects.RESISTANCE, 1, "qalqan"),
                new Blessing(MobEffects.NIGHT_VISION, 0, "kun"),
                new Blessing(MobEffects.JUMP_BOOST, 2, "qanat"),
                new Blessing(MobEffects.FIRE_RESISTANCE, 0, "ot"),
                new Blessing(MobEffects.HASTE, 1, "temir"),
                new Blessing(MobEffects.ABSORPTION, 2, "tengri"),
                new Blessing(MobEffects.WATER_BREATHING, 0, "su"),
                new Blessing(MobEffects.LUCK, 1, "baq"));
    }

    public RuneStoneBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

    /** Поставленный игроком камень сначала тёмный — он заряжается ночью под открытым небом. */
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(LIT, false);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return !state.getValue(LIT);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.getValue(LIT) && level.isDarkOutside() && level.canSeeSky(pos.above()) && random.nextInt(30) == 0) {
            level.setBlock(pos, state.setValue(LIT, true), Block.UPDATE_ALL);
            level.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.5, 12, 0.3, 0.3, 0.3, 0.02);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!state.getValue(LIT)) {
            return InteractionResult.PASS;
        }
        if (!(level instanceof ServerLevel server)) {
            return InteractionResult.SUCCESS;
        }
        // руны гаснут
        server.setBlock(pos, state.setValue(LIT, false), Block.UPDATE_ALL);
        // соседние камни той же стелы тоже гаснут
        for (BlockPos p : new BlockPos[]{pos.above(), pos.below()}) {
            BlockState other = server.getBlockState(p);
            if (other.getBlock() instanceof RuneStoneBlock && other.getValue(LIT)) {
                server.setBlock(p, other.setValue(LIT, false), Block.UPDATE_ALL);
            }
        }
        RandomSource random = server.getRandom();
        server.playSound(null, pos, ModSounds.RUNE_ACTIVATE.get(), SoundSource.BLOCKS, 1.4f, 0.9f + random.nextFloat() * 0.2f);
        server.sendParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5, pos.getY() + 1.2, pos.getZ() + 0.5, 60, 0.6, 0.8, 0.6, 0.6);
        server.sendParticles(ParticleTypes.END_ROD, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 20, 0.3, 0.6, 0.3, 0.05);

        if (random.nextFloat() < 0.7f) {
            List<Blessing> all = blessings();
            Blessing b = all.get(random.nextInt(all.size()));
            player.addEffect(new MobEffectInstance(b.effect(), 20 * 60, b.amplifier()));
            if (random.nextInt(4) == 0) {
                Blessing b2 = all.get(random.nextInt(all.size()));
                player.addEffect(new MobEffectInstance(b2.effect(), 20 * 60, b2.amplifier()));
            }
            player.sendOverlayMessage(Component.translatable("message.ezhelgi.rune." + b.rune()));
            server.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1.0, player.getZ(), 30, 0.4, 0.8, 0.4, 0.3);
        } else {
            // проклятая руна: поднимается нежить
            server.playSound(null, pos, ModSounds.RUNE_CURSE.get(), SoundSource.HOSTILE, 1.6f, 0.8f);
            player.sendOverlayMessage(Component.translatable("message.ezhelgi.rune.curse"));
            int count = 2 + random.nextInt(3);
            for (int i = 0; i < count; i++) {
                EntityType<? extends Mob> type;
                float r = random.nextFloat();
                if (r < 0.12f && i == 0) {
                    type = ModEntities.BATYR_ARUAGY.get();
                } else if (r < 0.5f) {
                    type = EntityTypes.ZOMBIE;
                } else if (r < 0.8f) {
                    type = EntityTypes.SKELETON;
                } else {
                    type = EntityTypes.HUSK;
                }
                Mob mob = type.create(server, EntitySpawnReason.EVENT);
                if (mob == null) continue;
                double a = random.nextDouble() * Math.PI * 2.0;
                double x = pos.getX() + 0.5 + Math.cos(a) * (2.0 + random.nextDouble() * 2.0);
                double z = pos.getZ() + 0.5 + Math.sin(a) * (2.0 + random.nextDouble() * 2.0);
                BlockPos at = BlockPos.containing(x, pos.getY() + 2, z);
                for (int k = 0; k < 6 && !server.getBlockState(at.below()).isSolid(); k++) {
                    at = at.below();
                }
                mob.setPos(x, at.getY(), z);
                mob.setTarget(player);
                if (mob instanceof BatyrAruagy) {
                    mob.setPersistenceRequired();
                }
                server.addFreshEntity(mob);
                server.sendParticles(ParticleTypes.SOUL, x, at.getY() + 0.5, z, 20, 0.3, 0.6, 0.3, 0.05);
                server.sendParticles(ParticleTypes.LARGE_SMOKE, x, at.getY() + 0.3, z, 10, 0.3, 0.2, 0.3, 0.02);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT) && random.nextInt(3) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + random.nextDouble();
            double z = pos.getZ() + random.nextDouble();
            level.addParticle(ParticleTypes.ENCHANT, x, y + 0.5, z, (random.nextDouble() - 0.5) * 0.4, 0.3, (random.nextDouble() - 0.5) * 0.4);
            if (random.nextInt(4) == 0) {
                level.addParticle(ParticleTypes.GLOW, x, y, z, 0, 0.02, 0);
            }
        }
    }
}
