package kz.ezhelgi.client;

import kz.ezhelgi.EzhelgiMod;
import kz.ezhelgi.entity.ModEntities;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;

/** Клиентская часть: модели и рендереры животных. Загружается только на клиенте. */
public final class EzhelgiClient {
    private EzhelgiClient() {}

    public static final ModelLayerLocation KOKBORI_LAYER = layer("kokbori");
    public static final ModelLayerLocation TULPAR_LAYER = layer("tulpar");
    public static final ModelLayerLocation BERKUT_LAYER = layer("berkut");

    public static void init(BusGroup modBusGroup) {
        EntityRenderersEvent.RegisterLayerDefinitions.BUS.addListener(EzhelgiClient::registerLayers);
        EntityRenderersEvent.RegisterRenderers.BUS.addListener(EzhelgiClient::registerRenderers);
    }

    private static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(KOKBORI_LAYER, KokboriModel::createBodyLayer);
        event.registerLayerDefinition(TULPAR_LAYER, TulparModel::createBodyLayer);
        event.registerLayerDefinition(BERKUT_LAYER, BerkutModel::createBodyLayer);
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.KOKBORI.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new KokboriModel(ctx.bakeLayer(KOKBORI_LAYER)), 0.5f, texture("kokbori"), 1.0f));
        event.registerEntityRenderer(ModEntities.TULPAR.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new TulparModel(ctx.bakeLayer(TULPAR_LAYER)), 0.75f, texture("tulpar"), 1.0f));
        event.registerEntityRenderer(ModEntities.BERKUT.get(), ctx -> new EzhelgiMobRenderer<>(ctx,
                new BerkutModel(ctx.bakeLayer(BERKUT_LAYER)), 0.3f, texture("berkut"), 1.0f));
    }

    private static ModelLayerLocation layer(String name) {
        return new ModelLayerLocation(Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, name), "main");
    }

    private static Identifier texture(String name) {
        return Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "textures/entity/" + name + ".png");
    }
}
