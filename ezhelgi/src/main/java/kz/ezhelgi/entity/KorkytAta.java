package kz.ezhelgi.entity;

import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

/**
 * Қорқыт ата — легендарный мудрец и первый кобызист, искавший бессмертие.
 * Встречается редко и всегда один, чаще в горах. Если подойти, сыграет
 * на кобызе свой кюй, подарит кобыз и растворится в воздухе.
 */
public class KorkytAta extends PathfinderMob {
    private static final EntityDataAccessor<Integer> PLAYING =
            SynchedEntityData.defineId(KorkytAta.class, EntityDataSerializers.INT);
    public static final int SONG_TICKS = 220;
    private boolean prepared;
    private int lifeTicks = 20 * 60 * 8;

    public KorkytAta(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.MOVEMENT_SPEED, 0.22);
    }

    /**
     * Очень редко. Здесь нельзя искать других мобов: проверка вызывается и при
     * генерации чанков, а там соседние чанки ещё не загружены (это вешало создание мира).
     */
    public static boolean checkSpawnRules(EntityType<? extends PathfinderMob> type, ServerLevelAccessor level, EntitySpawnReason reason,
                                          BlockPos pos, RandomSource random) {
        if (EntitySpawnReason.ignoresLightRequirements(reason)) return true;
        return reason != EntitySpawnReason.CHUNK_GENERATION && random.nextInt(30) == 0 && level.getBlockState(pos.below()).isSolid();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PLAYING, 0);
    }

    public int playingTicks() {
        return this.entityData.get(PLAYING);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 16.0f));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        // мудреца не ранить: он просто исчезает
        if (source.getEntity() instanceof Player && this.playingTicks() == 0) {
            this.vanish(level);
        }
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        if (!this.prepared) {
            this.prepared = true;
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(ModItems.QOBYZ.get()));
            this.setDropChance(EquipmentSlot.MAINHAND, 0.0f);
        }
        if (this.tickCount % 5 == 0) {
            level.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 0.5 + this.getRandom().nextDouble() * 1.5,
                    this.getZ(), 1, 0.3, 0.2, 0.3, 0.0);
        }
        int playing = this.entityData.get(PLAYING);
        if (playing > 0) {
            this.getNavigation().stop();
            Player listener = level.getNearestPlayer(this, 24.0);
            if (listener != null) {
                this.getLookControl().setLookAt(listener, 20.0f, 20.0f);
            }
            if (playing % 6 == 0) {
                level.sendParticles(ParticleTypes.NOTE, this.getX(), this.getY() + 2.2, this.getZ(), 1, 0.5, 0.3, 0.5, 1.0);
            }
            if (playing % 40 == 0) {
                for (Player p : level.getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(12.0))) {
                    p.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 0, true, true));
                }
            }
            this.entityData.set(PLAYING, playing - 1);
            if (playing - 1 == 0) {
                this.giveGift(level, listener);
            }
            return;
        }
        Player near = level.getNearestPlayer(this, 7.0);
        if (near != null && !near.isSpectator()) {
            this.entityData.set(PLAYING, SONG_TICKS);
            this.getLookControl().setLookAt(near, 30.0f, 30.0f);
            level.playSound(null, this.getX(), this.getY(), this.getZ(), ModSounds.KORKYT_KUI.get(), SoundSource.RECORDS, 2.0f, 1.0f);
            near.sendSystemMessage(Component.translatable("message.ezhelgi.korkyt.greet"));
            return;
        }
        if (--this.lifeTicks <= 0) {
            this.vanish(level);
        }
    }

    private void giveGift(ServerLevel level, Player player) {
        ItemStack gift = new ItemStack(ModItems.QOBYZ.get());
        if (player != null) {
            player.sendSystemMessage(Component.translatable("message.ezhelgi.korkyt.gift"));
            Vec3 to = player.position().subtract(this.position()).normalize().scale(0.3);
            ItemEntity item = new ItemEntity(level, this.getX(), this.getY() + 1.4, this.getZ(), gift, to.x, 0.25, to.z);
            level.addFreshEntity(item);
        } else {
            this.spawnAtLocation(level, gift);
        }
        this.vanish(level);
    }

    private void vanish(ServerLevel level) {
        level.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 1.0, this.getZ(), 50, 0.4, 0.9, 0.4, 0.08);
        level.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY() + 1.0, this.getZ(), 20, 0.4, 0.9, 0.4, 0.02);
        this.playSound(ModSounds.KORKYT_VANISH.get(), 1.5f, 1.0f);
        this.discard();
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return false;
    }
}
