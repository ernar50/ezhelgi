package kz.ezhelgi.entity.cave;

import kz.ezhelgi.aul.Speech;
import kz.ezhelgi.entity.ModEntities;
import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.quest.PlayerData;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * Шахмаран (Жылан патша) — мудрый царь змей: наполовину человек, наполовину змей.
 * С ним можно поговорить: он делится знанием, а однажды дарит змеиный тұмар.
 * Если напасть — защищается ядовитыми укусами и зовёт змей.
 */
public class Shahmaran extends PathfinderMob {
    public static final int WISDOM = 10;
    private final Speech speech = new Speech();
    private boolean calledSnakes;

    public Shahmaran(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPersistenceRequired();
        this.xpReward = 40;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0)
                .add(Attributes.ATTACK_DAMAGE, 7.0)
                .add(Attributes.ARMOR, 6.0)
                .add(Attributes.MOVEMENT_SPEED, 0.24)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1, true));
        this.goalSelector.addGoal(4, new MoveTowardsRestrictionGoal(this, 0.6));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.4));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || !(player instanceof ServerPlayer sp)) {
            return InteractionResult.SUCCESS;
        }
        if (this.getTarget() == player) {
            sp.sendOverlayMessage(Component.translatable("message.ezhelgi.shahmaran.angry"));
            return InteractionResult.SUCCESS;
        }
        if (this.speech.busy()) {
            return InteractionResult.SUCCESS;
        }
        CompoundTag data = PlayerData.get(sp);
        List<Component> lines = new ArrayList<>();
        int talks = data.getIntOr("shahmaran_talks", 0);
        if (talks == 0) {
            lines.add(Component.translatable("shahmaran.ezhelgi.greet"));
        }
        lines.add(Component.translatable("shahmaran.ezhelgi.wisdom." + (talks % WISDOM)).withStyle(ChatFormatting.ITALIC));
        data.putInt("shahmaran_talks", talks + 1);
        if (!data.getBooleanOr("shahmaran_gift", false)) {
            data.putBoolean("shahmaran_gift", true);
            lines.add(Component.translatable("shahmaran.ezhelgi.gift").withStyle(ChatFormatting.GREEN));
            ItemStack gift = new ItemStack(ModItems.ZHYLAN_TUMARY.get());
            if (!sp.getInventory().add(gift)) {
                sp.spawnAtLocation(sp.level(), gift);
            }
        } else if (talks % 3 == 2) {
            // знание: на время видишь во тьме и не боишься яда
            sp.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 60 * 5, 0));
            sp.removeEffect(MobEffects.POISON);
            lines.add(Component.translatable("shahmaran.ezhelgi.knowledge").withStyle(ChatFormatting.GREEN));
        }
        this.speech.sound(ModSounds.SHAHMARAN_SPEAK.get());
        this.speech.sayTo(sp, lines, 60);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel level) {
            this.speech.tick(level, this, Component.translatable("entity.ezhelgi.shahmaran").withStyle(ChatFormatting.DARK_GREEN));
            if (this.tickCount % 20 == 0 && !this.hasHome()) {
                this.setHomeTo(this.blockPosition(), 8);
            }
            if (this.getTarget() == null && this.tickCount % 40 == 0) {
                level.sendParticles(ParticleTypes.GLOW, this.getX(), this.getY() + 2.2, this.getZ(), 2, 0.3, 0.2, 0.3, 0.0);
            }
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        boolean hurt = super.hurtServer(level, source, amount);
        if (hurt && !this.calledSnakes && this.getHealth() < this.getMaxHealth() * 0.6f) {
            this.calledSnakes = true;
            for (int i = 0; i < 3; i++) {
                Zhylan snake = ModEntities.ZHYLAN.get().create(level, EntitySpawnReason.MOB_SUMMONED);
                if (snake != null) {
                    snake.snapTo(this.getX() + this.getRandom().nextGaussian() * 2, this.getY(), this.getZ() + this.getRandom().nextGaussian() * 2);
                    if (source.getEntity() instanceof LivingEntity attacker) {
                        snake.setTarget(attacker);
                    }
                    level.addFreshEntity(snake);
                }
            }
            this.playSound(ModSounds.ZHYLAN_HISS.get(), 2.0f, 0.6f);
        }
        return hurt;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hit = super.doHurtTarget(level, target);
        if (hit && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 1), this);
        }
        return hit;
    }

    @Override
    public boolean removeWhenFarAway(double distSqr) {
        return false;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ILLUSIONER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ILLUSIONER_DEATH;
    }
}
