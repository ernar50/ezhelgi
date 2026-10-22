package kz.ezhelgi.block.togyz;

import kz.ezhelgi.block.ModBlockEntities;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.Random;

/** Доска тоғыз құмалақ: хранит партию, ходит за аксакала. */
public class TogyzBlockEntity extends BlockEntity {
    public final TogyzGame game = new TogyzGame();
    /** Играть против аксакала (ИИ за дальний ряд) или вдвоём. */
    public boolean vsAi = true;
    private int aiDelay;
    /** Счётчик для анимации последнего хода на клиенте. */
    public long moveTime;

    public TogyzBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TOGYZ.get(), pos, state);
    }

    public void playerMove(ServerLevel level, Player player, int side, int index) {
        if (this.game.winner >= 0) {
            player.sendOverlayMessage(Component.translatable("message.ezhelgi.togyz.over"));
            return;
        }
        if (this.vsAi && side == 1) {
            player.sendOverlayMessage(Component.translatable("message.ezhelgi.togyz.aksakal_row"));
            return;
        }
        if (side != this.game.turn) {
            player.sendOverlayMessage(Component.translatable("message.ezhelgi.togyz.wait"));
            return;
        }
        if (!this.game.canMove(side, index)) {
            player.sendOverlayMessage(Component.translatable("message.ezhelgi.togyz.empty"));
            return;
        }
        doMove(level, player, index);
        if (this.vsAi && this.game.winner < 0 && this.game.turn == 1) {
            this.aiDelay = 25;
        }
    }

    private void doMove(ServerLevel level, @Nullable Player player, int index) {
        int side = this.game.turn;
        TogyzGame.Result r = this.game.move(index);
        BlockPos pos = this.getBlockPos();
        level.playSound(null, pos, ModSounds.TOGYZ_MOVE.get(), SoundSource.BLOCKS, 0.8f, 0.9f + level.getRandom().nextFloat() * 0.2f);
        if (r.captured() > 0) {
            level.playSound(null, pos, SoundEvents.AMETHYST_CLUSTER_STEP, SoundSource.BLOCKS, 1.0f, 1.3f);
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, 6, 0.4, 0.1, 0.4, 0.0);
        }
        Component who = Component.translatable(side == 0 ? "message.ezhelgi.togyz.side0" : (this.vsAi ? "message.ezhelgi.togyz.aksakal" : "message.ezhelgi.togyz.side1"));
        if (r.newTuzdyk()) {
            broadcast(level, Component.translatable("message.ezhelgi.togyz.tuzdyk", who).withStyle(ChatFormatting.GOLD));
        }
        if (this.game.winner >= 0) {
            Component text = this.game.winner == 2 ? Component.translatable("message.ezhelgi.togyz.draw")
                    : Component.translatable("message.ezhelgi.togyz.win", Component.translatable(this.game.winner == 0
                    ? "message.ezhelgi.togyz.side0" : (this.vsAi ? "message.ezhelgi.togyz.aksakal" : "message.ezhelgi.togyz.side1")));
            broadcast(level, Component.translatable("message.ezhelgi.togyz.final", text, this.game.kazan[0], this.game.kazan[1])
                    .withStyle(ChatFormatting.AQUA));
            if (this.game.winner == 0 && this.vsAi) {
                Player champ = level.getNearestPlayer(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 8.0, false);
                if (champ instanceof net.minecraft.server.level.ServerPlayer sp) {
                    kz.ezhelgi.quest.Quests.onTogyzWin(sp);
                }
            }
            level.playSound(null, pos, this.game.winner == 0 || !this.vsAi ? SoundEvents.PLAYER_LEVELUP : SoundEvents.VILLAGER_YES,
                    SoundSource.BLOCKS, 1.0f, 1.0f);
        } else if (player != null) {
            player.sendOverlayMessage(score());
        }
        this.moveTime = level.getGameTime();
        changed();
    }

    public Component score() {
        return Component.translatable("message.ezhelgi.togyz.score",
                this.game.kazan[0], this.game.kazan[1],
                Component.translatable(this.game.turn == 0 ? "message.ezhelgi.togyz.side0"
                        : (this.vsAi ? "message.ezhelgi.togyz.aksakal" : "message.ezhelgi.togyz.side1")));
    }

    private void broadcast(ServerLevel level, Component text) {
        for (Player p : level.players()) {
            if (p.distanceToSqr(net.minecraft.world.phys.Vec3.atCenterOf(this.getBlockPos())) < 16 * 16) {
                p.sendSystemMessage(text);
            }
        }
    }

    public void newGame(boolean switchMode) {
        if (switchMode) {
            this.vsAi = !this.vsAi;
        }
        this.game.reset();
        this.aiDelay = 0;
        changed();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TogyzBlockEntity be) {
        if (be.aiDelay > 0 && --be.aiDelay == 0 && level instanceof ServerLevel server
                && be.vsAi && be.game.winner < 0 && be.game.turn == 1) {
            int m = be.game.bestMove(new Random(server.getRandom().nextLong()), 3);
            if (m >= 0) {
                be.doMove(server, null, m);
                if (be.game.winner < 0 && be.game.turn == 1) {
                    be.aiDelay = 25; // у игрока нет ходов — аксакал ходит снова
                }
            }
        }
    }

    private void changed() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putIntArray("Game", this.game.save());
        output.putBoolean("VsAi", this.vsAi);
        output.putInt("AiDelay", this.aiDelay);
        output.putLong("MoveTime", this.moveTime);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.game.load(input.getIntArray("Game").orElse(null));
        this.vsAi = input.getBooleanOr("VsAi", true);
        this.aiDelay = input.getIntOr("AiDelay", 0);
        this.moveTime = input.getLongOr("MoveTime", 0L);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }
}
