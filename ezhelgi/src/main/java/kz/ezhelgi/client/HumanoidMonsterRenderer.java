package kz.ezhelgi.client;

import com.mojang.blaze3d.vertex.PoseStack;
import kz.ezhelgi.EzhelgiMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;

/**
 * Человекоподобные мобы мода (золотой воин, джунгар, Алтын Адам).
 * Костюм нарисован прямо на текстуре; предметы в руках и маска на голове
 * рисуются стандартными слоями.
 */
public class HumanoidMonsterRenderer<T extends Mob> extends HumanoidMobRenderer<T, HumanoidRenderState, HumanoidModel<HumanoidRenderState>> {
    private final Identifier texture;
    private final float scale;

    public HumanoidMonsterRenderer(EntityRendererProvider.Context context, String texture, float scale) {
        super(context, new HumanoidModel<>(context.bakeLayer(EzhelgiClient.HUMANOID_LAYER)), 0.5f * scale);
        this.texture = Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "textures/entity/" + texture + ".png");
        this.scale = scale;
    }

    public static LayerDefinition createBodyLayer() {
        return LayerDefinition.create(HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f), 64, 64);
    }

    @Override
    public HumanoidRenderState createRenderState() {
        return new HumanoidRenderState();
    }

    @Override
    public Identifier getTextureLocation(HumanoidRenderState state) {
        return this.texture;
    }

    @Override
    public void submit(HumanoidRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (this.scale != 1.0f) {
            poseStack.scale(this.scale, this.scale, this.scale);
        }
        super.submit(state, poseStack, collector, camera);
    }
}
