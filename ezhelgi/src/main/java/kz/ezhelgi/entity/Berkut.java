package kz.ezhelgi.entity;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * Бүркіт: беркут. Приручается сырым мясом (кролик, курица, баранина, говядина),
 * летает за хозяином, охотится на кроликов и бросается на врагов хозяина.
 * ПКМ пустой рукой: сидеть / лететь следом.
 */
public class Berkut extends Parrot {
    public Berkut(EntityType<? extends Parrot> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Parrot.createAttributes()
                .add(Attributes.MAX_HEALTH, 16.0)
                .add(Attributes.FLYING_SPEED, 0.7)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 4.0)
                .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.4, true));
        this.goalSelector.addGoal(3, new FollowOwnerGoal(this, 1.2, 8.0f, 2.0f));
        this.goalSelector.addGoal(4, new WaterAvoidingRandomFlyingGoal(this, 1.0));
        this.goalSelector.addGoal(5, new LookAtPlayerGoal(this, Player.class, 8.0f));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Rabbit.class, false));
    }

    private static boolean isMeat(ItemStack stack) {
        return stack.is(Items.RABBIT) || stack.is(Items.CHICKEN) || stack.is(Items.MUTTON) || stack.is(Items.BEEF);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (isMeat(stack)) {
            if (!this.level().isClientSide()) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.playSound(SoundEvents.PARROT_EAT, 1.0f, 0.7f);
                if (!this.isTame()) {
                    if (this.getRandom().nextInt(3) == 0) {
                        this.tame(player);
                        this.setOrderedToSit(true);
                        this.level().broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                } else {
                    this.heal(4.0f);
                }
            }
            return InteractionResult.SUCCESS;
        }

        if (this.isTame() && this.isOwnedBy(player) && hand == InteractionHand.MAIN_HAND && stack.isEmpty()) {
            if (!this.level().isClientSide()) {
                this.setOrderedToSit(!this.isOrderedToSit());
            }
            return InteractionResult.SUCCESS;
        }

        // Печенье и семена попугаев беркуту не нужны
        return InteractionResult.PASS;
    }
}
