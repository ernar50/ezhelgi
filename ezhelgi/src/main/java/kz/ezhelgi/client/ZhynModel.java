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
 * Модель жына — летающего демона без ног, с дымным хвостом и рогами.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class ZhynModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart horn_l;
    private final ModelPart arm_l;
    private final ModelPart horn_r;
    private final ModelPart arm_r;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;

    public ZhynModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = body.getChild("head");
        this.horn_l = head.getChild("horn_l");
        this.arm_l = body.getChild("arm_l");
        this.horn_r = head.getChild("horn_r");
        this.arm_r = body.getChild("arm_r");
        this.tail1 = body.getChild("tail1");
        this.tail2 = tail1.getChild("tail2");
        this.tail3 = tail2.getChild("tail3");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, 0F, -2.5F, 8F, 9F, 5F), PartPose.offset(0F, 6F, 0F));
        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(26, 0).addBox(-3.5F, -7F, -3.5F, 7F, 7F, 7F), PartPose.offset(0F, 0F, 0F));
        PartDefinition p_horn_l = p_head.addOrReplaceChild("horn_l", CubeListBuilder.create().texOffs(54, 0).addBox(-0.75F, -4F, -0.75F, 1.5F, 4F, 1.5F), PartPose.offsetAndRotation(2.5F, -6.5F, -1F, -0.4F, 0F, 0.5F));
        PartDefinition p_arm_l = p_body.addOrReplaceChild("arm_l", CubeListBuilder.create().texOffs(0, 14).addBox(-1.5F, -1F, -1.5F, 3F, 10F, 3F).texOffs(12, 14).addBox(-1.5F, 9F, -1.5F, 3F, 1.5F, 3F), PartPose.offset(5F, 1F, 0F));
        PartDefinition p_horn_r = p_head.addOrReplaceChild("horn_r", CubeListBuilder.create().texOffs(24, 14).addBox(-0.75F, -4F, -0.75F, 1.5F, 4F, 1.5F), PartPose.offsetAndRotation(-2.5F, -6.5F, -1F, -0.4F, 0F, -0.5F));
        PartDefinition p_arm_r = p_body.addOrReplaceChild("arm_r", CubeListBuilder.create().texOffs(32, 14).addBox(-1.5F, -1F, -1.5F, 3F, 10F, 3F).texOffs(44, 14).addBox(-1.5F, 9F, -1.5F, 3F, 1.5F, 3F), PartPose.offset(-5F, 1F, 0F));
        PartDefinition p_tail1 = p_body.addOrReplaceChild("tail1", CubeListBuilder.create().texOffs(0, 27).addBox(-3F, 0F, -2F, 6F, 5F, 4F), PartPose.offsetAndRotation(0F, 9F, 0F, 0.2F, 0F, 0F));
        PartDefinition p_tail2 = p_tail1.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(20, 27).addBox(-2F, 0F, -1.5F, 4F, 5F, 3F), PartPose.offsetAndRotation(0F, 5F, 0F, 0.25F, 0F, 0F));
        PartDefinition p_tail3 = p_tail2.addOrReplaceChild("tail3", CubeListBuilder.create().texOffs(34, 27).addBox(-1F, 0F, -1F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, 5F, 0F, 0.3F, 0F, 0F));
        return LayerDefinition.create(mesh, 64, 64);
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
        this.body.y += Mth.sin(age * 0.1F) * 0.8F;
        this.tail1.zRot = Mth.sin(age * 0.15F) * 0.2F;
        this.tail2.zRot = Mth.sin(age * 0.15F - 0.8F) * 0.3F;
        this.tail3.zRot = Mth.sin(age * 0.15F - 1.6F) * 0.4F;
        float reach = state.attacking ? -1.6F : -0.2F + Mth.sin(age * 0.08F) * 0.1F;
        this.arm_l.xRot = reach;
        this.arm_r.xRot = reach;
        this.arm_l.zRot = -0.15F;
        this.arm_r.zRot = 0.15F;
    }
}
