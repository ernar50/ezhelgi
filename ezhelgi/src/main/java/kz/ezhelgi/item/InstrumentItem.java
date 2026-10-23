package kz.ezhelgi.item;

import kz.ezhelgi.entity.SteppeVillager;
import kz.ezhelgi.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Народные инструменты. ПКМ — сыграть короткий наигрыш:
 * сыбызғы (флейта пастуха) созывает домашних животных к тебе;
 * жетіген (семиструнная арфа) лечит всех рядом;
 * шаңқобыз (варган) даёт ночное зрение и подсвечивает нечисть вокруг;
 * дауылпаз (боевой барабан) даёт союзникам силу и скорость.
 */
public class InstrumentItem extends Item {
    public enum Kind {
        SYBYZGY(() -> ModSounds.SYBYZGY.get(), 100),
        ZHETIGEN(() -> ModSounds.ZHETIGEN.get(), 160),
        SHANQOBYZ(() -> ModSounds.SHANQOBYZ.get(), 200),
        DAUYLPAZ(() -> ModSounds.DAUYLPAZ.get(), 600);

        final Supplier<SoundEvent> sound;
        final int cooldown;

        Kind(Supplier<SoundEvent> sound, int cooldown) {
            this.sound = sound;
            this.cooldown = cooldown;
        }
    }

    private final Kind kind;

    public InstrumentItem(Properties properties, Kind kind) {
        super(properties);
        this.kind = kind;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel server)) {
            return InteractionResult.SUCCESS;
        }
        player.getCooldowns().addCooldown(stack, this.kind.cooldown);
        server.playSound(null, player.getX(), player.getY(), player.getZ(), this.kind.sound.get(), SoundSource.RECORDS, 1.0f, 1.0f);
        server.sendParticles(ParticleTypes.NOTE, player.getX(), player.getEyeY() + 0.5, player.getZ(), 6, 0.6, 0.3, 0.6, 1.0);
        AABB area = player.getBoundingBox().inflate(this.kind == Kind.SYBYZGY ? 24 : 12);
        switch (this.kind) {
            case SYBYZGY -> {
                int n = 0;
                for (Animal a : server.getEntitiesOfClass(Animal.class, area, a -> !(a instanceof TamableAnimal t && t.isOrderedToSit()))) {
                    a.getNavigation().moveTo(player, 1.25);
                    n++;
                }
                player.sendOverlayMessage(Component.translatable("message.ezhelgi.sybyzgy", n));
            }
            case ZHETIGEN -> {
                for (LivingEntity e : server.getEntitiesOfClass(LivingEntity.class, area, e -> !(e instanceof Enemy))) {
                    e.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0), player);
                }
                server.sendParticles(ParticleTypes.HEART, player.getX(), player.getEyeY(), player.getZ(), 5, 2, 0.5, 2, 0.0);
            }
            case SHANQOBYZ -> {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 20 * 40, 0));
                for (LivingEntity e : server.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(24), e -> e instanceof Enemy)) {
                    e.addEffect(new MobEffectInstance(MobEffects.GLOWING, 20 * 15, 0), player);
                }
            }
            case DAUYLPAZ -> {
                for (LivingEntity e : server.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(16), e -> e == player
                        || e instanceof Player || e instanceof IronGolem || e instanceof SteppeVillager
                        || (e instanceof TamableAnimal t && t.isTame()))) {
                    e.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 20 * 30, 0), player);
                    e.addEffect(new MobEffectInstance(MobEffects.SPEED, 20 * 30, 0), player);
                }
                server.sendParticles(ParticleTypes.ANGRY_VILLAGER, player.getX(), player.getEyeY(), player.getZ(), 4, 1.5, 0.5, 1.5, 0.0);
            }
        }
        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(1, player, hand);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag flag) {
        builder.accept(Component.translatable("tooltip.ezhelgi.instrument." + this.kind.name().toLowerCase()).withStyle(ChatFormatting.GRAY));
    }
}
