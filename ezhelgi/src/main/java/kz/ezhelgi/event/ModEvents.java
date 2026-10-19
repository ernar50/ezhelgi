package kz.ezhelgi.event;

import kz.ezhelgi.accessory.Accessories;
import kz.ezhelgi.item.ModItems;
import kz.ezhelgi.item.TorsykItem;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

/**
 * Игровые события мода:
 * <ul>
 *   <li>с лошадей падает конина;</li>
 *   <li>кобылу доят в бутылку или торсык — получается қымыз, верблюдицу — шұбат;</li>
 *   <li>украшения выпадают при смерти (если не включён keepInventory).</li>
 * </ul>
 */
public final class ModEvents {
    private ModEvents() {}

    private static final String MILKED = "ezhelgi_milked";
    /** Сколько ждать между дойками одного животного (1 минута). */
    private static final long MILK_COOLDOWN = 1200L;

    public static void init() {
        LivingDropsEvent.BUS.addListener(ModEvents::onDrops);
        LivingDeathEvent.BUS.addListener(ModEvents::onDeath);
        PlayerInteractEvent.EntityInteractSpecific.BUS.addListener(ModEvents::onInteract);
    }

    private static void onDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel level) || entity.isBaby()) {
            return;
        }
        if (entity instanceof Horse) {
            int n = 1 + level.getRandom().nextInt(3);
            ItemStack meat = new ItemStack(entity.isOnFire() ? ModItems.PISKEN_JYLQY_ETI.get() : ModItems.JYLQY_ETI.get(), n);
            event.getDrops().add(new ItemEntity(level, entity.getX(), entity.getY() + 0.5, entity.getZ(), meat));
        }
    }

    private static void onDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player && !player.isSpectator()) {
            ServerLevel level = player.level();
            if (level.getGameRules().get(GameRules.KEEP_INVENTORY)) {
                return;
            }
            SimpleContainer container = Accessories.load(player);
            boolean any = false;
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack stack = container.removeItemNoUpdate(i);
                if (!stack.isEmpty()) {
                    player.spawnAtLocation(level, stack);
                    any = true;
                }
            }
            if (any) {
                Accessories.save(player, container);
                Accessories.refresh(player);
            }
        }
    }

    /** Доение кобылы и верблюдицы. Возвращает true, чтобы не открылось седло/посадка. */
    private static boolean onInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        Entity target = event.getTarget();
        boolean horse = target instanceof Horse h && !h.isBaby() && !(target instanceof kz.ezhelgi.entity.Tulpar);
        boolean camel = target instanceof Camel c && !c.isBaby();
        if (!horse && !camel) {
            return false;
        }
        Player player = event.getEntity();
        ItemStack stack = player.getItemInHand(event.getHand());
        boolean bottle = stack.is(Items.GLASS_BOTTLE);
        boolean torsyk = stack.getItem() instanceof TorsykItem t && t.canTake(stack, camel ? TorsykItem.Drink.SHUBAT : TorsykItem.Drink.QYMYZ);
        if (!bottle && !torsyk) {
            return false;
        }
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (!(player.level() instanceof ServerLevel level)) {
            return true;
        }
        CompoundTag data = target.getPersistentData();
        long now = level.getGameTime();
        long last = data.getLongOr(MILKED, -MILK_COOLDOWN);
        if (now - last < MILK_COOLDOWN) {
            player.sendOverlayMessage(Component.translatable(camel ? "message.ezhelgi.milk.camel_empty" : "message.ezhelgi.milk.horse_empty"));
            return true;
        }
        data.putLong(MILKED, now);
        level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.COW_MILK, SoundSource.PLAYERS, 1.0f, 1.0f);
        level.sendParticles(ParticleTypes.HEART, target.getX(), target.getY() + target.getBbHeight(), target.getZ(), 2, 0.3, 0.2, 0.3, 0.0);
        if (bottle) {
            ItemStack drink = new ItemStack(camel ? ModItems.SHUBAT.get() : ModItems.KUMYS.get());
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            if (!player.getInventory().add(drink)) {
                player.spawnAtLocation(level, drink);
            }
        } else {
            TorsykItem.addPortion(player, event.getHand(), stack, camel ? TorsykItem.Drink.SHUBAT : TorsykItem.Drink.QYMYZ);
        }
        return true;
    }
}
