package kz.ezhelgi.client;

import com.mojang.blaze3d.vertex.PoseStack;
import kz.ezhelgi.EzhelgiMod;
import kz.ezhelgi.entity.BatyrAruagy;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

/** Полупрозрачный призрак батыра, слегка парящий над землёй. */
public class GhostRenderer extends HumanoidMobRenderer<BatyrAruagy, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "textures/entity/batyr_aruagy.png");

    public GhostRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(EzhelgiClient.HUMANOID_LAYER), RenderTypes::entityTranslucent), 0.0f);
    }

    @Override
    public HumanoidRenderState createRenderState() {
        return new HumanoidRenderState();
    }

    @Override
    public Identifier getTextureLocation(HumanoidRenderState state) {
        return TEXTURE;
    }

    @Override
    public void submit(HumanoidRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.0, 0.12 + Mth.sin(state.ageInTicks * 0.08f) * 0.06, 0.0);
        poseStack.scale(1.05f, 1.05f, 1.05f);
        super.submit(state, poseStack, collector, camera);
        poseStack.popPose();
    }
}
