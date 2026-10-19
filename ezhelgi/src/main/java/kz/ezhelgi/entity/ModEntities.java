package kz.ezhelgi.entity;

import kz.ezhelgi.EzhelgiMod;
import net.minecraft.world.entity.EntityAttachment;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    private ModEntities() {}

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, EzhelgiMod.MODID);

    /** Көкбөрі — степной серый волк, тотем тюрков. */
    public static final RegistryObject<EntityType<Kokbori>> KOKBORI = ENTITY_TYPES.register("kokbori",
            () -> EntityType.Builder.of(Kokbori::new, MobCategory.CREATURE)
                    .sized(0.7f, 0.9f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("kokbori")));

    /** Тұлпар — крылатый конь батыров. */
    public static final RegistryObject<EntityType<Tulpar>> TULPAR = ENTITY_TYPES.register("tulpar",
            () -> EntityType.Builder.of(Tulpar::new, MobCategory.CREATURE)
                    .sized(1.4f, 1.6f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("tulpar")));

    /** Бүркіт — беркут, ловчая птица. */
    public static final RegistryObject<EntityType<Berkut>> BERKUT = ENTITY_TYPES.register("berkut",
            () -> EntityType.Builder.of(Berkut::new, MobCategory.CREATURE)
                    .sized(0.6f, 0.9f)
                    .clientTrackingRange(8)
                    .build(ENTITY_TYPES.key("berkut")));

    /** Как у игрока: всадник садится в седло, а не над конём. */
    private static final Vec3 RIDER_SEAT = new Vec3(0.0, 0.6, 0.0);

    /** Алтын жауынгер — золотой воин, страж курганов. */
    public static final RegistryObject<EntityType<AltynJauynger>> ALTYN_JAUYNGER = ENTITY_TYPES.register("altyn_jauynger",
            () -> EntityType.Builder.of(AltynJauynger::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .attach(EntityAttachment.VEHICLE, RIDER_SEAT)
                    .clientTrackingRange(8)
                    .build(ENTITY_TYPES.key("altyn_jauynger")));

    /** Жоңғар — джунгарский воин. */
    public static final RegistryObject<EntityType<Jungar>> JUNGAR = ENTITY_TYPES.register("jungar",
            () -> EntityType.Builder.of(Jungar::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .attach(EntityAttachment.VEHICLE, RIDER_SEAT)
                    .clientTrackingRange(8)
                    .build(ENTITY_TYPES.key("jungar")));

    /** Алтын Адам — босс в золотой маске, выше человека. */
    public static final RegistryObject<EntityType<AltynAdam>> ALTYN_ADAM = ENTITY_TYPES.register("altyn_adam",
            () -> EntityType.Builder.of(AltynAdam::new, MobCategory.MONSTER)
                    .sized(0.8f, 2.45f)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("altyn_adam")));

    /** Арба — деревянная телега. */
    public static final RegistryObject<EntityType<Arba>> ARBA = ENTITY_TYPES.register("arba",
            () -> EntityType.Builder.of(Arba::new, MobCategory.MISC)
                    .sized(1.5f, 1.0f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("arba")));
}
