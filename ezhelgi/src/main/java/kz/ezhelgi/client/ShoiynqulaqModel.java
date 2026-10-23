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
 * Модель Шойынқұлақ — великана с огромными чугунными ушами.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class ShoiynqulaqModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart ear_l;
    private final ModelPart arm_l;
    private final ModelPart leg_l;
    private final ModelPart ear_r;
    private final ModelPart arm_r;
    private final ModelPart leg_r;

    public ShoiynqulaqModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = body.getChild("head");
        this.ear_l = head.getChild("ear_l");
        this.arm_l = body.getChild("arm_l");
        this.leg_l = root.getChild("leg_l");
        this.ear_r = head.getChild("ear_r");
        this.arm_r = body.getChild("arm_r");
        this.leg_r = root.getChild("leg_r");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-7F, 0F, -4F, 14F, 16F, 8F), PartPose.offset(0F, -6F, 0F));
        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(44, 0).addBox(-5F, -10F, -5F, 10F, 10F, 10F).texOffs(84, 0).addBox(-5F, 0F, -5.5F, 10F, 4F, 1F), PartPose.offset(0F, 0F, 0F));
        PartDefinition p_ear_l = p_head.addOrReplaceChild("ear_l", CubeListBuilder.create().texOffs(0, 24).addBox(-1F, -7F, -5F, 2F, 14F, 10F), PartPose.offsetAndRotation(5F, -6F, 0F, 0F, 0.2F, 0F));
        PartDefinition p_arm_l = p_body.addOrReplaceChild("arm_l", CubeListBuilder.create().texOffs(24, 24).addBox(-3F, -2F, -3F, 6F, 18F, 6F), PartPose.offset(9F, 2F, 0F));
        PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create().texOffs(48, 24).addBox(-3F, 0F, -3F, 6F, 14F, 6F), PartPose.offset(3.5F, 10F, 0F));
        PartDefinition p_ear_r = p_head.addOrReplaceChild("ear_r", CubeListBuilder.create().texOffs(72, 24).addBox(-1F, -7F, -5F, 2F, 14F, 10F), PartPose.offsetAndRotation(-5F, -6F, 0F, 0F, -0.2F, 0F));
        PartDefinition p_arm_r = p_body.addOrReplaceChild("arm_r", CubeListBuilder.create().texOffs(96, 24).addBox(-3F, -2F, -3F, 6F, 18F, 6F), PartPose.offset(-9F, 2F, 0F));
        PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create().texOffs(0, 48).addBox(-3F, 0F, -3F, 6F, 14F, 6F), PartPose.offset(-3.5F, 10F, 0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(EzhelgiRenderState state) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float limbPos = state.walkAnimationPos;
        float limbSpeed = Math.min(state.walkAnimationSpeed, 1.0F);
        float age = state.ageInTicks;
        float headYaw = Mth.clamp(state.yRot, -40.0F, 40.0F) * ((float) Math.PI / 180.0F);
        float headPitch = Mth.clamp(state.xRot, -30.0F, 40.0F) * ((float) Math.PI / 180.0F);

        this.head.yRot = headYaw;
        this.head.xRot = headPitch;
        float swing = Mth.cos(limbPos * 0.5F) * 1.0F * limbSpeed;
        this.leg_l.xRot = swing;
        this.leg_r.xRot = -swing;
        this.arm_l.xRot = -swing * 0.8F;
        this.arm_r.xRot = swing * 0.8F;
        // уши раскрываются перед хлопком
        float flap = state.attacking ? 0.9F + Mth.sin(age * 0.8F) * 0.4F : 0.15F + Mth.sin(age * 0.05F) * 0.05F;
        this.ear_l.yRot = flap;
        this.ear_r.yRot = -flap;
        if (state.attacking) {
            this.arm_l.xRot = -1.8F;
            this.arm_r.xRot = -1.8F;
        }
    }
}
