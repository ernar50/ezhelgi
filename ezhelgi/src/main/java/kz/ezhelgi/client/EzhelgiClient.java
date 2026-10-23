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
    public static final ModelLayerLocation ARQAR_LAYER = layer("arqar");
    public static final ModelLayerLocation QARAQUIRYQ_LAYER = layer("qaraquiryq");
    public static final ModelLayerLocation TAU_TEKE_LAYER = layer("tau_teke");
    public static final ModelLayerLocation AYU_LAYER = layer("ayu");
    public static final ModelLayerLocation MANUL_LAYER = layer("manul");
    public static final ModelLayerLocation QARSAQ_LAYER = layer("qarsaq");
    public static final ModelLayerLocation ITBALYQ_LAYER = layer("itbalyq");
    public static final ModelLayerLocation QOSAYAQ_LAYER = layer("qosayaq");
    public static final ModelLayerLocation PRZEWALSKI_LAYER = layer("przewalski");
    public static final ModelLayerLocation QARAQUS_LAYER = layer("qaraqus");
    public static final ModelLayerLocation AIDAHAR_LAYER = layer("aidahar");
    public static final ModelLayerLocation MYSTAN_LAYER = layer("mystan_kempir");
    public static final ModelLayerLocation ZHYLAN_LAYER = layer("zhylan");
    public static final ModelLayerLocation ZHYN_LAYER = layer("zhyn");
    public static final ModelLayerLocation SHAHMARAN_LAYER = layer("shahmaran");
    public static final ModelLayerLocation SHOIYNQULAQ_LAYER = layer("shoiynqulaq");
    public static final ModelLayerLocation QANBAQ_LAYER = layer("qanbaq");

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
        event.registerLayerDefinition(ARQAR_LAYER, ArqarModel::createBodyLayer);
        event.registerLayerDefinition(QARAQUIRYQ_LAYER, QaraquiryqModel::createBodyLayer);
        event.registerLayerDefinition(TAU_TEKE_LAYER, TauTekeModel::createBodyLayer);
        event.registerLayerDefinition(AYU_LAYER, AyuModel::createBodyLayer);
        event.registerLayerDefinition(MANUL_LAYER, ManulModel::createBodyLayer);
        event.registerLayerDefinition(QARSAQ_LAYER, QarsaqModel::createBodyLayer);
        event.registerLayerDefinition(ITBALYQ_LAYER, ItbalyqModel::createBodyLayer);
        event.registerLayerDefinition(QOSAYAQ_LAYER, QosayaqModel::createBodyLayer);
        event.registerLayerDefinition(PRZEWALSKI_LAYER, PrzewalskiModel::createBodyLayer);
        event.registerLayerDefinition(QARAQUS_LAYER, QaraqusModel::createBodyLayer);
        event.registerLayerDefinition(AIDAHAR_LAYER, AidaharModel::createBodyLayer);
        event.registerLayerDefinition(MYSTAN_LAYER, () -> FolkModel.createLayer(18.0f, 1.0f));
        event.registerLayerDefinition(ZHYLAN_LAYER, ZhylanModel::createBodyLayer);
        event.registerLayerDefinition(ZHYN_LAYER, ZhynModel::createBodyLayer);
        event.registerLayerDefinition(SHAHMARAN_LAYER, ShahmaranModel::createBodyLayer);
        event.registerLayerDefinition(SHOIYNQULAQ_LAYER, ShoiynqulaqModel::createBodyLayer);
        event.registerLayerDefinition(QANBAQ_LAYER, QanbaqModel::createBodyLayer);
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
        event.registerEntityRenderer(ModEntities.AQSAQAL.get(), ctx -> NpcRenderer.fixed(ctx, "aqsaqal", 0.95f, false));
        event.registerEntityRenderer(ModEntities.KUISHI.get(), ctx -> new NpcRenderer<>(ctx,
                (kz.ezhelgi.entity.SteppeVillager e) -> "kuishi_" + (e.variant() % 3), 0.95f, false));
        event.registerEntityRenderer(ModEntities.KERUENSHI.get(), ctx -> new NpcRenderer<>(ctx,
                (kz.ezhelgi.entity.SteppeVillager e) -> "keruenshi_" + (e.variant() % 2), 0.97f, false));
        event.registerBlockEntityRenderer(kz.ezhelgi.block.ModBlockEntities.TOGYZ.get(), TogyzRenderer::new);
        event.registerEntityRenderer(ModEntities.SHABANDOZ.get(), ctx -> new NpcRenderer<>(ctx,
                (kz.ezhelgi.aul.Shabandoz e) -> "shabandoz_" + (e.team() == 0 ? "red" : e.team() == 1 ? "blue" : "jockey"), 0.95f, false));
        event.registerEntityRenderer(ModEntities.SERKE.get(), net.minecraft.client.renderer.entity.GoatRenderer::new);
        event.registerEntityRenderer(ModEntities.AQYN.get(), ctx -> new NpcRenderer<>(ctx,
                (kz.ezhelgi.aul.Aqyn e) -> "aqyn_" + (e.variant() % 2), 0.97f, false));
        event.registerEntityRenderer(ModEntities.KELIN.get(), ctx -> NpcRenderer.fixed(ctx, "kelin", 0.93f, false));
        event.registerEntityRenderer(ModEntities.ZHYRSHY.get(), ctx -> NpcRenderer.fixed(ctx, "zhyrshy", 0.95f, false));
        event.registerEntityRenderer(ModEntities.SAQA.get(), net.minecraft.client.renderer.entity.ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.ZHYLAN.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new ZhylanModel(ctx.bakeLayer(ZHYLAN_LAYER)), 0.3f, texture("zhylan"), 1.0f));
        event.registerEntityRenderer(ModEntities.ZHYN.get(), ctx -> new EzhelgiMobRenderer<kz.ezhelgi.entity.cave.Zhyn>(ctx,
                new ZhynModel(ctx.bakeLayer(ZHYN_LAYER)), 0.4f,
                e -> texture(e.variant() == 1 ? "zhyn_qyzyl" : e.variant() == 2 ? "zhyn_kok" : "zhyn_qara"), 1.0f));
        event.registerEntityRenderer(ModEntities.SHAHMARAN.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new ShahmaranModel(ctx.bakeLayer(SHAHMARAN_LAYER)), 0.7f, texture("shahmaran"), 1.05f));
        event.registerEntityRenderer(ModEntities.SHOIYNQULAQ.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new ShoiynqulaqModel(ctx.bakeLayer(SHOIYNQULAQ_LAYER)), 2.0f, texture("shoiynqulaq"), 2.1f));
        event.registerEntityRenderer(ModEntities.DARWISH.get(), ctx -> NpcRenderer.fixed(ctx, "darwish", 0.95f, false));
        event.registerEntityRenderer(ModEntities.QANBAQ.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new QanbaqModel(ctx.bakeLayer(QANBAQ_LAYER)), 0.35f, texture("qanbaq"), 1.0f));
        event.registerEntityRenderer(ModEntities.SEAT.get(), net.minecraft.client.renderer.entity.NoopRenderer::new);
        event.registerBlockEntityRenderer(kz.ezhelgi.block.ModBlockEntities.KEPTIRGISH.get(), KeptirgishRenderer::new);
        event.registerEntityRenderer(ModEntities.SHOQAN.get(), ctx -> NpcRenderer.fixed(ctx, "shoqan", 0.97f, false));
        event.registerEntityRenderer(ModEntities.AL_FARABI.get(), ctx -> NpcRenderer.fixed(ctx, "al_farabi", 0.97f, false));
        event.registerEntityRenderer(ModEntities.SAIGA.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new SaigaModel(ctx.bakeLayer(SAIGA_LAYER)), 0.5f, texture("aqboken"), 1.0f));
        event.registerEntityRenderer(ModEntities.BARYS.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new BarysModel(ctx.bakeLayer(BARYS_LAYER)), 0.55f, texture("barys"), 1.0f));
        // v7
        event.registerEntityRenderer(ModEntities.ARQAR.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new ArqarModel(ctx.bakeLayer(ARQAR_LAYER)), 0.7f, texture("arqar"), 1.0f));
        event.registerEntityRenderer(ModEntities.QARAQUIRYQ.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new QaraquiryqModel(ctx.bakeLayer(QARAQUIRYQ_LAYER)), 0.45f, texture("qaraquiryq"), 1.0f));
        event.registerEntityRenderer(ModEntities.TAU_TEKE.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new TauTekeModel(ctx.bakeLayer(TAU_TEKE_LAYER)), 0.6f, texture("tau_teke"), 1.0f));
        event.registerEntityRenderer(ModEntities.AYU.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new AyuModel(ctx.bakeLayer(AYU_LAYER)), 1.0f, texture("ayu"), 1.0f));
        event.registerEntityRenderer(ModEntities.MANUL.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new ManulModel(ctx.bakeLayer(MANUL_LAYER)), 0.35f, texture("manul"), 1.0f));
        event.registerEntityRenderer(ModEntities.QARSAQ.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new QarsaqModel(ctx.bakeLayer(QARSAQ_LAYER)), 0.3f, texture("qarsaq"), 1.0f));
        event.registerEntityRenderer(ModEntities.ITBALYQ.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new ItbalyqModel(ctx.bakeLayer(ITBALYQ_LAYER)), 0.5f, texture("itbalyq"), 1.0f));
        event.registerEntityRenderer(ModEntities.QOSAYAQ.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new QosayaqModel(ctx.bakeLayer(QOSAYAQ_LAYER)), 0.2f, texture("qosayaq"), 1.0f));
        event.registerEntityRenderer(ModEntities.PRZEWALSKI.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new PrzewalskiModel(ctx.bakeLayer(PRZEWALSKI_LAYER)), 0.75f, texture("przewalski"), 1.0f));
        event.registerEntityRenderer(ModEntities.QARAQUS.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new QaraqusModel(ctx.bakeLayer(QARAQUS_LAYER)), 0.5f, texture("qaraqus"), 1.5f));
        event.registerEntityRenderer(ModEntities.AIDAHAR.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new AidaharModel(ctx.bakeLayer(AIDAHAR_LAYER)), 2.0f, texture("aidahar"), 2.6f));
        event.registerEntityRenderer(ModEntities.MYSTAN_KEMPIR.get(), ctx -> new FolkRenderer<>(ctx, MYSTAN_LAYER, false,
                "mystan_kempir", "mystan_kempir_angry", 1.0f));
        event.registerEntityRenderer(ModEntities.MYSTAN_TULKI.get(), net.minecraft.client.renderer.entity.FoxRenderer::new);
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, name), "main");
    }

    private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "textures/entity/" + name + ".png");
    }
}
