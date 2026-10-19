package kz.ezhelgi;

import com.mojang.logging.LogUtils;
import kz.ezhelgi.block.ModBlocks;
import kz.ezhelgi.client.EzhelgiClient;
import kz.ezhelgi.accessory.Accessories;
import kz.ezhelgi.accessory.ModMenus;
import kz.ezhelgi.entity.AltynJauynger;
import kz.ezhelgi.entity.Berkut;
import kz.ezhelgi.entity.Kokbori;
import kz.ezhelgi.entity.ModEntities;
import kz.ezhelgi.entity.Tulpar;
import kz.ezhelgi.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

/**
 * Ежелгі Қазақстан — мод о древнем Казахстане.
 */
@Mod(EzhelgiMod.MODID)
public final class EzhelgiMod {
    public static final String MODID = "ezhelgi";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_TABS.register("ezhelgi", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ezhelgi"))
            .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> ModItems.ALTYN_QYLYSH.get().getDefaultInstance())
            .displayItems((params, output) -> ModItems.ITEMS.getEntries().forEach(item -> output.accept(item.get())))
            .build());

    public EzhelgiMod(FMLJavaModLoadingContext context) {
        var modBusGroup = context.getModBusGroup();

        ModBlocks.BLOCKS.register(modBusGroup);
        ModItems.ITEMS.register(modBusGroup);
        ModEntities.ENTITY_TYPES.register(modBusGroup);
        CREATIVE_TABS.register(modBusGroup);
        ModMenus.MENUS.register(modBusGroup);
        Accessories.init();

        EntityAttributeCreationEvent.BUS.addListener(EzhelgiMod::registerAttributes);
        SpawnPlacementRegisterEvent.BUS.addListener(EzhelgiMod::registerSpawnPlacements);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            EzhelgiClient.init(modBusGroup);
        }

        LOGGER.info("Ежелгі Қазақстан: сәлем, батыр!");
    }

    private static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.KOKBORI.get(), Kokbori.createAttributes().build());
        event.put(ModEntities.TULPAR.get(), Tulpar.createAttributes().build());
        event.put(ModEntities.BERKUT.get(), Berkut.createAttributes().build());
        event.put(ModEntities.ALTYN_JAUYNGER.get(), AltynJauynger.createAttributes().build());
    }

    private static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.KOKBORI.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.TULPAR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.BERKUT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
        event.register(ModEntities.ALTYN_JAUYNGER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}
