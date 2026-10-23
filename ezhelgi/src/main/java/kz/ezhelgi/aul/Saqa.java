package kz.ezhelgi.aul;

import kz.ezhelgi.block.ModBlocks;
import kz.ezhelgi.entity.ModEntities;
import kz.ezhelgi.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/**
 * Сақа — тяжёлый биток для игры в асық ату. Если попасть в ряд асыков (асық қатар),
 * из ряда вылетают 1–2 асыка. Сақа падает на землю, её можно подобрать снова.
 */
public class Saqa extends ThrowableItemProjectile {
    public Saqa(EntityType<? extends Saqa> type, Level level) {
        super(type, level);
    }

    public Saqa(Level level, LivingEntity thrower, ItemStack stack) {
        super(ModEntities.SAQA.get(), thrower, level, stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SAQA.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult hit) {
        super.onHitBlock(hit);
        if (this.level() instanceof ServerLevel level) {
            BlockPos pos = hit.getBlockPos();
            BlockState state = level.getBlockState(pos);
            if (state.is(ModBlocks.ASYQ_QATAR.get())) {
                AsyqBlock.knock(level, pos, state, this.getOwner());
            }
        }
    }

    @Override
    protected void onHit(HitResult hit) {
        super.onHit(hit);
        if (this.level() instanceof ServerLevel level) {
            level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(this.getItem())),
                    this.getX(), this.getY(), this.getZ(), 6, 0.1, 0.1, 0.1, 0.05);
            level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.BONE_BLOCK_HIT, SoundSource.PLAYERS, 1.0f, 1.4f);
            // сақа не пропадает — падает рядом
            ItemEntity drop = new ItemEntity(level, this.getX(), this.getY() + 0.1, this.getZ(), new ItemStack(ModItems.SAQA.get()));
            drop.setDefaultPickUpDelay();
            level.addFreshEntity(drop);
            this.discard();
        }
    }
}
