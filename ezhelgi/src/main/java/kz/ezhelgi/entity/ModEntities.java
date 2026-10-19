package kz.ezhelgi.entity;

import kz.ezhelgi.EzhelgiMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
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
}
