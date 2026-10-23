package kz.ezhelgi.client;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Модель пещерной змеи.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class ZhylanModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart head;
    private final ModelPart s0;
    private final ModelPart s1;
    private final ModelPart s2;
    private final ModelPart s3;
    private final ModelPart s4;
    private final ModelPart s5;
    private final ModelPart s6;

    public ZhylanModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.s0 = root.getChild("s0");
        this.s1 = s0.getChild("s1");
        this.s2 = s1.getChild("s2");
        this.s3 = s2.getChild("s3");
        this.s4 = s3.getChild("s4");
        this.s5 = s4.getChild("s5");
        this.s6 = s5.getChild("s6");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2F, -1.5F, -4F, 4F, 3F, 4F).texOffs(16, 0).addBox(-0.5F, 0.5F, -5.5F, 1F, 0.1F, 1.5F), PartPose.offset(0F, 21.5F, -6F));
        PartDefinition p_s0 = root.addOrReplaceChild("s0", CubeListBuilder.create().texOffs(22, 0).addBox(-1.6F, -1.4F, 0F, 3.2F, 2.56F, 4F), PartPose.offset(0F, 21.5F, -6F));
        PartDefinition p_s1 = p_s0.addOrReplaceChild("s1", CubeListBuilder.create().texOffs(38, 0).addBox(-1.5F, -1.3F, 0F, 3F, 2.4F, 4F), PartPose.offset(0F, 0F, 4F));
        PartDefinition p_s2 = p_s1.addOrReplaceChild("s2", CubeListBuilder.create().texOffs(0, 7).addBox(-1.4F, -1.2F, 0F, 2.8F, 2.24F, 4F), PartPose.offset(0F, 0F, 4F));
        PartDefinition p_s3 = p_s2.addOrReplaceChild("s3", CubeListBuilder.create().texOffs(14, 7).addBox(-1.25F, -1.05F, 0F, 2.5F, 2F, 4F), PartPose.offset(0F, 0F, 4F));
        PartDefinition p_s4 = p_s3.addOrReplaceChild("s4", CubeListBuilder.create().texOffs(28, 7).addBox(-1.1F, -0.9F, 0F, 2.2F, 1.76F, 4F), PartPose.offset(0F, 0F, 4F));
        PartDefinition p_s5 = p_s4.addOrReplaceChild("s5", CubeListBuilder.create().texOffs(42, 7).addBox(-0.9F, -0.7F, 0F, 1.8F, 1.44F, 4F), PartPose.offset(0F, 0F, 4F));
        PartDefinition p_s6 = p_s5.addOrReplaceChild("s6", CubeListBuilder.create().texOffs(0, 14).addBox(-0.7F, -0.5F, 0F, 1.4F, 1.12F, 4F), PartPose.offset(0F, 0F, 4F));
        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(EzhelgiRenderState state) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float limbPos = state.walkAnimationPos;
        float limbSpeed = Math.min(state.walkAnimationSpeed, 1.0F);
        float age = state.ageInTicks;
        float headYaw = Mth.clamp(state.yRot, -40.0F, 40.0F) * ((float) Math.PI / 180.0F);
        float headPitch = Mth.clamp(state.xRot, -30.0F, 40.0F) * ((float) Math.PI / 180.0F);

        this.head.yRot = headYaw * 0.5F + Mth.sin(age * 0.35F + limbPos * 0.8F + 0.9F) * 0.2F;
        this.head.xRot = headPitch * 0.3F;
        this.s0.yRot = Mth.sin(age * 0.35F + limbPos * 0.8F - 0 * 0.9F) * (0.35F + limbSpeed * 0.3F);
        this.s1.yRot = Mth.sin(age * 0.35F + limbPos * 0.8F - 1 * 0.9F) * (0.35F + limbSpeed * 0.3F);
        this.s2.yRot = Mth.sin(age * 0.35F + limbPos * 0.8F - 2 * 0.9F) * (0.35F + limbSpeed * 0.3F);
        this.s3.yRot = Mth.sin(age * 0.35F + limbPos * 0.8F - 3 * 0.9F) * (0.35F + limbSpeed * 0.3F);
        this.s4.yRot = Mth.sin(age * 0.35F + limbPos * 0.8F - 4 * 0.9F) * (0.35F + limbSpeed * 0.3F);
        this.s5.yRot = Mth.sin(age * 0.35F + limbPos * 0.8F - 5 * 0.9F) * (0.35F + limbSpeed * 0.3F);
        this.s6.yRot = Mth.sin(age * 0.35F + limbPos * 0.8F - 6 * 0.9F) * (0.35F + limbSpeed * 0.3F);
    }
}
