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
                    .sized(0.8f, 1.15f)
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

    /** Жезтырнақ — злой дух с медными когтями. */
    public static final RegistryObject<EntityType<Zheztyrnak>> ZHEZTYRNAK = ENTITY_TYPES.register("zheztyrnak",
            () -> EntityType.Builder.of(Zheztyrnak::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("zheztyrnak")));

    /** Батыр аруағы — призрак древнего воина. */
    public static final RegistryObject<EntityType<BatyrAruagy>> BATYR_ARUAGY = ENTITY_TYPES.register("batyr_aruagy",
            () -> EntityType.Builder.of(BatyrAruagy::new, MobCategory.MONSTER)
                    .sized(0.6f, 2.0f)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("batyr_aruagy")));

    /** Албасты — ночной дух у воды. */
    public static final RegistryObject<EntityType<Albasty>> ALBASTY = ENTITY_TYPES.register("albasty",
            () -> EntityType.Builder.of(Albasty::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("albasty")));

    /** Жалмауыз кемпір — ведьма с горы Бурабай. */
    public static final RegistryObject<EntityType<ZhalmauyzKempir>> ZHALMAUYZ_KEMPIR = ENTITY_TYPES.register("zhalmauyz_kempir",
            () -> EntityType.Builder.of(ZhalmauyzKempir::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.9f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("zhalmauyz_kempir")));

    /** Самұрық — огромная золотая птица у Байтерека. */
    public static final RegistryObject<EntityType<Samuryk>> SAMURYK = ENTITY_TYPES.register("samuryk",
            () -> EntityType.Builder.of(Samuryk::new, MobCategory.CREATURE)
                    .sized(1.5f, 1.7f)
                    .passengerAttachments(new Vec3(0.0, 1.55, 0.1))
                    .clientTrackingRange(12)
                    .build(ENTITY_TYPES.key("samuryk")));

    // ---------- v6 ----------
    public static final RegistryObject<EntityType<Balbal>> BALBAL = ENTITY_TYPES.register("balbal",
            () -> EntityType.Builder.<Balbal>of(Balbal::new, MobCategory.MONSTER)
                    .sized(0.7f, 2.1f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("balbal")));

    public static final RegistryObject<EntityType<BaqsyKolenkesi>> BAQSY_KOLENKESI = ENTITY_TYPES.register("baqsy_kolenkesi",
            () -> EntityType.Builder.<BaqsyKolenkesi>of(BaqsyKolenkesi::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("baqsy_kolenkesi")));

    public static final RegistryObject<EntityType<Dau>> DAU = ENTITY_TYPES.register("dau",
            () -> EntityType.Builder.<Dau>of(Dau::new, MobCategory.MONSTER)
                    .sized(1.4f, 4.4f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("dau")));

    public static final RegistryObject<EntityType<Saiga>> SAIGA = ENTITY_TYPES.register("aqboken",
            () -> EntityType.Builder.<Saiga>of(Saiga::new, MobCategory.CREATURE)
                    .sized(0.8f, 1.1f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("aqboken")));

    public static final RegistryObject<EntityType<Barys>> BARYS = ENTITY_TYPES.register("barys",
            () -> EntityType.Builder.<Barys>of(Barys::new, MobCategory.CREATURE)
                    .sized(0.8f, 0.85f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("barys")));

    public static final RegistryObject<EntityType<KorkytAta>> KORKYT_ATA = ENTITY_TYPES.register("qorqyt_ata",
            () -> EntityType.Builder.<KorkytAta>of(KorkytAta::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("qorqyt_ata")));

    public static final RegistryObject<EntityType<Qaraqshy>> QARAQSHY = ENTITY_TYPES.register("qaraqshy",
            () -> EntityType.Builder.<Qaraqshy>of(Qaraqshy::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f)
                    .attach(EntityAttachment.VEHICLE, RIDER_SEAT)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("qaraqshy")));

    public static final RegistryObject<EntityType<SteppeVillager>> AUYLDAS = ENTITY_TYPES.register("auyldas",
            () -> EntityType.Builder.<SteppeVillager>of(SteppeVillager::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("auyldas")));

    public static final RegistryObject<EntityType<SteppeVillager>> ABAI = ENTITY_TYPES.register("abai",
            () -> EntityType.Builder.<SteppeVillager>of(SteppeVillager::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("abai")));

    public static final RegistryObject<EntityType<SteppeVillager>> SHOQAN = ENTITY_TYPES.register("shoqan",
            () -> EntityType.Builder.<SteppeVillager>of(SteppeVillager::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("shoqan")));

    public static final RegistryObject<EntityType<SteppeVillager>> AL_FARABI = ENTITY_TYPES.register("al_farabi",
            () -> EntityType.Builder.<SteppeVillager>of(SteppeVillager::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.95f)
                    .clientTrackingRange(10)
                    .build(ENTITY_TYPES.key("al_farabi")));
}
