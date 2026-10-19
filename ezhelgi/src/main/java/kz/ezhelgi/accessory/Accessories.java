package kz.ezhelgi.accessory;

import kz.ezhelgi.EzhelgiMod;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.Channel.VersionTest;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Слоты аксессуаров игрока: хранение (данные игрока, переживают смерть),
 * сетевой пакет «открыть окно» и применение бонусов украшений.
 */
public final class Accessories {
    private Accessories() {}

    public static final int SIZE = 4;
    private static final String KEY = "ezhelgi_accessories";
    private static final String PERSISTED = "PlayerPersisted";

    // ---------------------------------------------------------------- сеть
    /** Пустой пакет «клиент нажал кнопку аксессуаров». */
    public record OpenPacket() {
        public static final OpenPacket INSTANCE = new OpenPacket();
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenPacket> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    }

    private static final int PROTOCOL = 1;
    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "main"))
            .clientAcceptedVersions(VersionTest.exact(PROTOCOL))
            .serverAcceptedVersions(VersionTest.exact(PROTOCOL))
            .networkProtocolVersion(PROTOCOL)
            .simpleChannel()
                .play()
                    .serverbound()
                        .addMain(OpenPacket.class, OpenPacket.STREAM_CODEC, Accessories::handleOpen)
            .build();

    public static void init() {
        TickEvent.PlayerTickEvent.Post.BUS.addListener(Accessories::onPlayerTick);
    }

    /** Вызывается с клиента (кнопка в инвентаре). */
    public static void requestOpen() {
        CHANNEL.send(OpenPacket.INSTANCE, PacketDistributor.SERVER.noArg());
    }

    private static void handleOpen(OpenPacket packet, CustomPayloadEvent.Context context) {
        ServerPlayer player = context.getSender();
        context.enqueueWork(() -> {
            if (player != null) {
                open(player);
            }
        });
        context.setPacketHandled(true);
    }

    public static void open(ServerPlayer player) {
        SimpleContainer container = load(player);
        player.openMenu(new SimpleMenuProvider(
                (id, inventory, p) -> new AccessoryMenu(id, inventory, container),
                Component.translatable("container.ezhelgi.accessories")));
    }

    // ---------------------------------------------------------------- хранение
    public static SimpleContainer load(ServerPlayer player) {
        SimpleContainer container = new SimpleContainer(SIZE);
        CompoundTag persisted = player.getPersistentData().getCompoundOrEmpty(PERSISTED);
        ListTag list = persisted.getListOrEmpty(KEY);
        RegistryOps<Tag> ops = player.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        for (int i = 0; i < Math.min(SIZE, list.size()); i++) {
            int slot = i;
            ItemStack.OPTIONAL_CODEC.parse(ops, list.get(i)).result().ifPresent(stack -> container.setItem(slot, stack));
        }
        return container;
    }

    public static void save(ServerPlayer player, Container container) {
        RegistryOps<Tag> ops = player.registryAccess().createSerializationContext(NbtOps.INSTANCE);
        ListTag list = new ListTag();
        for (int i = 0; i < SIZE; i++) {
            list.add(ItemStack.OPTIONAL_CODEC.encodeStart(ops, container.getItem(i)).getOrThrow());
        }
        CompoundTag data = player.getPersistentData();
        CompoundTag persisted = data.getCompoundOrEmpty(PERSISTED);
        persisted.put(KEY, list);
        data.put(PERSISTED, persisted);
    }

    // ---------------------------------------------------------------- бонусы
    private static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
        Player player = event.player();
        if (player instanceof ServerPlayer serverPlayer && serverPlayer.tickCount % 20 == 0) {
            refresh(serverPlayer);
        }
    }

    /** Нечисть, которую отгоняет тұмар. */
    public static boolean isPest(net.minecraft.world.entity.Entity e) {
        return e instanceof net.minecraft.world.entity.monster.Phantom
                || e instanceof net.minecraft.world.entity.monster.Silverfish
                || e instanceof net.minecraft.world.entity.monster.Endermite
                || e instanceof net.minecraft.world.entity.monster.spider.CaveSpider
                || e instanceof net.minecraft.world.entity.monster.Vex
                || e instanceof net.minecraft.world.entity.ambient.Bat
                || e instanceof kz.ezhelgi.entity.Albasty;
    }

    private static void repelPests(ServerPlayer player) {
        // фантомы не прилетают: тұмар даёт спокойный сон
        player.resetStat(net.minecraft.stats.Stats.CUSTOM.get(net.minecraft.stats.Stats.TIME_SINCE_REST));
        net.minecraft.server.level.ServerLevel level = player.level();
        for (net.minecraft.world.entity.Mob mob : level.getEntitiesOfClass(net.minecraft.world.entity.Mob.class,
                player.getBoundingBox().inflate(10.0), Accessories::isPest)) {
            if (mob.getTarget() == player) {
                mob.setTarget(null);
            }
            net.minecraft.world.phys.Vec3 away = mob.position().subtract(player.position());
            double dist = Math.max(0.5, away.length());
            if (dist < 6.0) {
                net.minecraft.world.phys.Vec3 push = away.scale(0.6 / dist);
                mob.setDeltaMovement(mob.getDeltaMovement().add(push.x, mob.isNoGravity() ? push.y : 0.1, push.z));
                mob.needsSync = true;
            }
            if (mob instanceof net.minecraft.world.entity.PathfinderMob pathfinder) {
                pathfinder.getNavigation().moveTo(mob.getX() + away.x / dist * 8.0, mob.getY(), mob.getZ() + away.z / dist * 8.0, 1.3);
            }
            level.sendParticles(net.minecraft.core.particles.ParticleTypes.ENCHANTED_HIT, mob.getX(), mob.getY() + mob.getBbHeight() * 0.5,
                    mob.getZ(), 4, 0.2, 0.2, 0.2, 0.1);
        }
    }

    private static Identifier modifierId(String accessory) {
        return Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "accessory_" + accessory);
    }

    /** Пересчитывает бонусы украшений: добавляет нужные и снимает лишние. */
    public static void refresh(ServerPlayer player) {
        SimpleContainer container = load(player);
        List<AccessoryItem> worn = new ArrayList<>();
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < SIZE; i++) {
            if (container.getItem(i).getItem() instanceof AccessoryItem accessory && ids.add(accessory.accessoryId())) {
                worn.add(accessory);
            }
        }
        for (AccessoryItem accessory : AccessoryRegistry.all()) {
            Holder<Attribute> attribute = accessory.attribute();
            if (attribute == null) continue;
            AttributeInstance instance = player.getAttribute(attribute);
            if (instance == null) continue;
            Identifier id = modifierId(accessory.accessoryId());
            instance.removeModifier(id);
            if (worn.contains(accessory)) {
                instance.addTransientModifier(new AttributeModifier(id, accessory.amount(), accessory.operation()));
            }
        }
        for (AccessoryItem accessory : worn) {
            if (accessory.nightVision()) {
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, true, false));
            }
        }
        // тұмар: оберег от «досаждателей»
        boolean tumar = false;
        for (AccessoryItem accessory : worn) {
            if ("tumar".equals(accessory.accessoryId())) {
                tumar = true;
            }
        }
        if (tumar) {
            repelPests(player);
        }
        // золотая маска на голове: стойкость и ночное зрение
        if (player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD).is(kz.ezhelgi.item.ModItems.ALTYN_BETPERDE.get())) {
            player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 60, 0, true, false));
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, true, false));
        }
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }
}
