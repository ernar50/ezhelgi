package kz.ezhelgi.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

/**
 * Арба — деревянная телега. Едет за лошадью, к которой прицеплена.
 * <ul>
 *   <li>ПКМ, сидя на лошади — прицепить/отцепить арбу к этой лошади;</li>
 *   <li>ПКМ — сесть в арбу;</li>
 *   <li>Shift+ПКМ — открыть сундук арбы (27 слотов).</li>
 * </ul>
 * Сломать — несколько ударов; вещи и сама арба выпадут.
 */
public class Arba extends Mob {
    private static final EntityDataAccessor<Integer> HORSE_ID =
            SynchedEntityData.defineId(Arba.class, EntityDataSerializers.INT);
    private static final double DISTANCE = 3.0;

    private final SimpleContainer cargo = new SimpleContainer(27);
    private long horseMost = 0L;
    private long horseLeast = 0L;

    public float wheelRot;
    public float wheelRotO;

    public Arba(EntityType<? extends Mob> type, Level level) {
        super(type, level);
        this.setNoAi(true);
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 12.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0);
    }

    public SimpleContainer cargo() {
        return this.cargo;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HORSE_ID, -1);
    }

    /** Прицепить к лошади (или null — отцепить). */
    public void hitch(@Nullable AbstractHorse horse) {
        if (horse == null) {
            this.entityData.set(HORSE_ID, -1);
            this.horseMost = 0L;
            this.horseLeast = 0L;
        } else {
            this.entityData.set(HORSE_ID, horse.getId());
            this.horseMost = horse.getUUID().getMostSignificantBits();
            this.horseLeast = horse.getUUID().getLeastSignificantBits();
        }
    }

    private @Nullable AbstractHorse horse() {
        int id = this.entityData.get(HORSE_ID);
        if (id >= 0 && this.level().getEntity(id) instanceof AbstractHorse horse && horse.isAlive()) {
            return horse;
        }
        // после загрузки мира id другой — ищем лошадь по UUID
        if (this.level() instanceof ServerLevel server && (this.horseMost != 0L || this.horseLeast != 0L)) {
            Entity found = server.getEntity(new java.util.UUID(this.horseMost, this.horseLeast));
            if (found instanceof AbstractHorse horse && horse.isAlive()) {
                this.entityData.set(HORSE_ID, horse.getId());
                return horse;
            }
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();
        double oldX = this.getX();
        double oldZ = this.getZ();

        AbstractHorse horse = this.horse();
        if (horse != null) {
            float yaw = horse.getYRot();
            double fx = -Mth.sin(yaw * ((float) Math.PI / 180.0F));
            double fz = Mth.cos(yaw * ((float) Math.PI / 180.0F));
            double tx = horse.getX() - fx * DISTANCE;
            double tz = horse.getZ() - fz * DISTANCE;
            // мягко подтягиваемся, чтобы не дёргалось
            double nx = this.getX() + (tx - this.getX()) * 0.6;
            double nz = this.getZ() + (tz - this.getZ()) * 0.6;
            double ny = this.getY() + (horse.getY() - this.getY()) * 0.5;
            this.setPos(nx, ny, nz);
            this.setYRot(yaw);
            this.yBodyRot = yaw;
            this.yHeadRot = yaw;
            if (!this.level().isClientSide() && horse.distanceTo(this) > 10.0f) {
                this.hitch(null);
            }
        }

        this.wheelRotO = this.wheelRot;
        double moved = Math.sqrt((this.getX() - oldX) * (this.getX() - oldX) + (this.getZ() - oldZ) * (this.getZ() - oldZ));
        this.wheelRot += (float) (moved / 0.45);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS;
        }
        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player.getVehicle() instanceof AbstractHorse horse) {
            if (this.horse() == horse) {
                this.hitch(null);
                player.sendSystemMessage(Component.translatable("message.ezhelgi.arba.unhitched"));
            } else {
                this.hitch(horse);
                player.sendSystemMessage(Component.translatable("message.ezhelgi.arba.hitched"));
            }
            this.playSound(SoundEvents.WOOD_PLACE, 1.0f, 1.0f);
            return InteractionResult.SUCCESS;
        }
        if (player.isSecondaryUseActive()) {
            player.openMenu(new SimpleMenuProvider(
                    (id, inventory, p) -> ChestMenu.threeRows(id, inventory, this.cargo),
                    Component.translatable("entity.ezhelgi.arba")));
            return InteractionResult.SUCCESS;
        }
        if (this.getPassengers().isEmpty()) {
            player.startRiding(this);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void dropEquipment(ServerLevel level) {
        super.dropEquipment(level);
        Containers.dropContents(level, this.blockPosition().above(), this.cargo);
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WOOD_HIT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return SoundEvents.WOOD_BREAK;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putLong("HorseMost", this.horseMost);
        output.putLong("HorseLeast", this.horseLeast);
        ValueOutput.TypedOutputList<ItemStackWithSlot> items = output.list("Cargo", ItemStackWithSlot.CODEC);
        for (int i = 0; i < this.cargo.getContainerSize(); i++) {
            ItemStack stack = this.cargo.getItem(i);
            if (!stack.isEmpty()) {
                items.add(new ItemStackWithSlot(i, stack));
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.horseMost = input.getLongOr("HorseMost", 0L);
        this.horseLeast = input.getLongOr("HorseLeast", 0L);
        for (ItemStackWithSlot item : input.listOrEmpty("Cargo", ItemStackWithSlot.CODEC)) {
            if (item.isValidInContainer(this.cargo.getContainerSize())) {
                this.cargo.setItem(item.slot(), item.stack());
            }
        }
    }
}
