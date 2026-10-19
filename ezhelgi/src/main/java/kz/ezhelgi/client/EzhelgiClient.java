package kz.ezhelgi.client;

import kz.ezhelgi.EzhelgiMod;
import kz.ezhelgi.entity.ModEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import kz.ezhelgi.accessory.ModMenus;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;

/** Клиентская часть: модели и рендереры животных. Загружается только на клиенте. */
public final class EzhelgiClient {
    private EzhelgiClient() {}

    public static final ModelLayerLocation KOKBORI_LAYER = layer("kokbori");
    public static final ModelLayerLocation TULPAR_LAYER = layer("tulpar");
    public static final ModelLayerLocation BERKUT_LAYER = layer("berkut");
    public static final ModelLayerLocation HUMANOID_LAYER = layer("humanoid");
    public static final ModelLayerLocation ARBA_LAYER = layer("arba");
    public static final ModelLayerLocation ZHEZTYRNAK_LAYER = layer("zheztyrnak");
    public static final ModelLayerLocation ALBASTY_LAYER = layer("albasty");
    public static final ModelLayerLocation KEMPIR_LAYER = layer("zhalmauyz_kempir");
    public static final ModelLayerLocation SAMURYK_LAYER = layer("samuryk");
    public static final ModelLayerLocation SAIGA_LAYER = layer("aqboken");
    public static final ModelLayerLocation BARYS_LAYER = layer("barys");

    public static void init(BusGroup modBusGroup) {
        EntityRenderersEvent.RegisterLayerDefinitions.BUS.addListener(EzhelgiClient::registerLayers);
        EntityRenderersEvent.RegisterRenderers.BUS.addListener(EzhelgiClient::registerRenderers);
        FMLClientSetupEvent.getBus(modBusGroup).addListener(EzhelgiClient::clientSetup);
        ScreenEvent.Init.Post.BUS.addListener(AccessoryButton::onScreenInit);
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.register(ModMenus.ACCESSORIES.get(), AccessoryScreen::new));
    }

    private static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(KOKBORI_LAYER, KokboriModel::createBodyLayer);
        event.registerLayerDefinition(TULPAR_LAYER, TulparModel::createBodyLayer);
        event.registerLayerDefinition(BERKUT_LAYER, BerkutModel::createBodyLayer);
        event.registerLayerDefinition(HUMANOID_LAYER, HumanoidMonsterRenderer::createBodyLayer);
        event.registerLayerDefinition(ARBA_LAYER, ArbaModel::createBodyLayer);
        event.registerLayerDefinition(ZHEZTYRNAK_LAYER, () -> FolkModel.createLayer(17.0f, 1.5f));
        event.registerLayerDefinition(ALBASTY_LAYER, () -> FolkModel.createLayer(22.0f, 0.0f));
        event.registerLayerDefinition(KEMPIR_LAYER, () -> FolkModel.createLayer(14.0f, 2.5f));
        event.registerLayerDefinition(SAMURYK_LAYER, SamurykModel::createBodyLayer);
        event.registerLayerDefinition(SAIGA_LAYER, SaigaModel::createBodyLayer);
        event.registerLayerDefinition(BARYS_LAYER, BarysModel::createBodyLayer);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.KOKBORI.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new KokboriModel(ctx.bakeLayer(KOKBORI_LAYER)), 0.5f, texture("kokbori"), 1.0f));
        event.registerEntityRenderer(ModEntities.TULPAR.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new TulparModel(ctx.bakeLayer(TULPAR_LAYER)), 0.75f, texture("tulpar"), 1.0f));
        event.registerEntityRenderer(ModEntities.BERKUT.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new BerkutModel(ctx.bakeLayer(BERKUT_LAYER)), 0.4f, texture("berkut"), 1.4f));
        event.registerEntityRenderer(ModEntities.ALTYN_JAUYNGER.get(), ctx -> new HumanoidMonsterRenderer<>(ctx, "altyn_jauynger", 1.0f));
        event.registerEntityRenderer(ModEntities.JUNGAR.get(), ctx -> new HumanoidMonsterRenderer<>(ctx, "jungar", 1.0f));
        event.registerEntityRenderer(ModEntities.ALTYN_ADAM.get(), ctx -> new HumanoidMonsterRenderer<>(ctx, "altyn_adam", 1.25f));
        event.registerEntityRenderer(ModEntities.ARBA.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new ArbaModel(ctx.bakeLayer(ARBA_LAYER)), 0.8f, texture("arba"), 1.0f));
        event.registerEntityRenderer(ModEntities.ZHEZTYRNAK.get(), ctx -> new FolkRenderer<>(ctx, ZHEZTYRNAK_LAYER, false,
                "zheztyrnak", "zheztyrnak_angry", 0.97f));
        event.registerEntityRenderer(ModEntities.ALBASTY.get(), ctx -> new FolkRenderer<>(ctx, ALBASTY_LAYER, false,
                "albasty", "albasty_angry", 1.0f));
        event.registerEntityRenderer(ModEntities.ZHALMAUYZ_KEMPIR.get(), ctx -> new FolkRenderer<>(ctx, KEMPIR_LAYER, true,
                "zhalmauyz_kempir", "zhalmauyz_kempir_cast", 1.0f));
        event.registerEntityRenderer(ModEntities.BATYR_ARUAGY.get(), GhostRenderer::new);
        event.registerEntityRenderer(ModEntities.SAMURYK.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new SamurykModel(ctx.bakeLayer(SAMURYK_LAYER)), 1.2f, texture("samuryk"), 2.0f));
        // v6
        event.registerEntityRenderer(ModEntities.BALBAL.get(), ctx -> new NpcRenderer<>(ctx,
                (kz.ezhelgi.entity.Balbal e) -> e.isAwake() ? "balbal_awake" : "balbal", 1.1f, false));
        event.registerEntityRenderer(ModEntities.BAQSY_KOLENKESI.get(), ctx -> NpcRenderer.fixed(ctx, "baqsy_kolenkesi", 1.0f, true));
        event.registerEntityRenderer(ModEntities.DAU.get(), ctx -> NpcRenderer.fixed(ctx, "dau", 2.25f, false));
        event.registerEntityRenderer(ModEntities.KORKYT_ATA.get(), ctx -> NpcRenderer.fixed(ctx, "qorqyt_ata", 1.0f, false));
        event.registerEntityRenderer(ModEntities.QARAQSHY.get(), ctx -> NpcRenderer.fixed(ctx, "qaraqshy", 1.0f, false));
        event.registerEntityRenderer(ModEntities.AUYLDAS.get(), ctx -> new NpcRenderer<>(ctx,
                (kz.ezhelgi.entity.SteppeVillager e) -> "auyldas/auyldas_" + e.variant(), 0.95f, false));
        event.registerEntityRenderer(ModEntities.ABAI.get(), ctx -> NpcRenderer.fixed(ctx, "abai", 0.97f, false));
        event.registerEntityRenderer(ModEntities.SHOQAN.get(), ctx -> NpcRenderer.fixed(ctx, "shoqan", 0.97f, false));
        event.registerEntityRenderer(ModEntities.AL_FARABI.get(), ctx -> NpcRenderer.fixed(ctx, "al_farabi", 0.97f, false));
        event.registerEntityRenderer(ModEntities.SAIGA.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new SaigaModel(ctx.bakeLayer(SAIGA_LAYER)), 0.5f, texture("aqboken"), 1.0f));
        event.registerEntityRenderer(ModEntities.BARYS.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new BarysModel(ctx.bakeLayer(BARYS_LAYER)), 0.55f, texture("barys"), 1.0f));
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, name), "main");
    }

    private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "textures/entity/" + name + ".png");
    }
}
