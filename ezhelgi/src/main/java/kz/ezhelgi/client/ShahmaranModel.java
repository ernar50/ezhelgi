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
 * Модель Шахмарана — царя змей: человек по пояс, ниже — длинный змеиный хвост.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class ShahmaranModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart crown;
    private final ModelPart arm_l;
    private final ModelPart arm_r;
    private final ModelPart t0;
    private final ModelPart t1;
    private final ModelPart t2;
    private final ModelPart t3;
    private final ModelPart t4;
    private final ModelPart t5;
    private final ModelPart t6;

    public ShahmaranModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = body.getChild("head");
        this.crown = head.getChild("crown");
        this.arm_l = body.getChild("arm_l");
        this.arm_r = body.getChild("arm_r");
        this.t0 = body.getChild("t0");
        this.t1 = t0.getChild("t1");
        this.t2 = t1.getChild("t2");
        this.t3 = t2.getChild("t3");
        this.t4 = t3.getChild("t4");
        this.t5 = t4.getChild("t5");
        this.t6 = t5.getChild("t6");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, 0F, -2F, 8F, 11F, 4F).texOffs(24, 0).addBox(-4.5F, 0F, -2.5F, 9F, 3F, 5F), PartPose.offset(0F, 2F, 0F));
        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(52, 0).addBox(-4F, -8F, -4F, 8F, 8F, 8F), PartPose.offset(0F, 0F, 0F));
        PartDefinition p_crown = p_head.addOrReplaceChild("crown", CubeListBuilder.create().texOffs(84, 0).addBox(-4.5F, -3F, -4.5F, 9F, 3F, 9F), PartPose.offset(0F, -8F, 0F));
        PartDefinition p_arm_l = p_body.addOrReplaceChild("arm_l", CubeListBuilder.create().texOffs(0, 16).addBox(-1.5F, -1F, -1.5F, 3F, 11F, 3F), PartPose.offset(5F, 1F, 0F));
        PartDefinition p_arm_r = p_body.addOrReplaceChild("arm_r", CubeListBuilder.create().texOffs(12, 16).addBox(-1.5F, -1F, -1.5F, 3F, 11F, 3F), PartPose.offset(-5F, 1F, 0F));
        PartDefinition p_t0 = p_body.addOrReplaceChild("t0", CubeListBuilder.create().texOffs(24, 16).addBox(-3.5F, 0F, -3.5F, 7F, 9F, 7F), PartPose.offset(0F, 11F, 0F));
        PartDefinition p_t1 = p_t0.addOrReplaceChild("t1", CubeListBuilder.create().texOffs(52, 16).addBox(-3.25F, 0F, -3.25F, 6.5F, 7F, 6.5F), PartPose.offsetAndRotation(0F, 9F, 0F, 1.35F, 0F, 0F));
        PartDefinition p_t2 = p_t1.addOrReplaceChild("t2", CubeListBuilder.create().texOffs(80, 16).addBox(-3F, 0F, -3F, 6F, 7F, 6F), PartPose.offsetAndRotation(0F, 7F, 0F, 0.15F, 0F, 0F));
        PartDefinition p_t3 = p_t2.addOrReplaceChild("t3", CubeListBuilder.create().texOffs(104, 16).addBox(-2.5F, 0F, -2.5F, 5F, 7F, 5F), PartPose.offsetAndRotation(0F, 7F, 0F, 0.05F, 0F, 0F));
        PartDefinition p_t4 = p_t3.addOrReplaceChild("t4", CubeListBuilder.create().texOffs(0, 32).addBox(-2F, 0F, -2F, 4F, 6F, 4F), PartPose.offsetAndRotation(0F, 7F, 0F, 0.05F, 0F, 0F));
        PartDefinition p_t5 = p_t4.addOrReplaceChild("t5", CubeListBuilder.create().texOffs(16, 32).addBox(-1.5F, 0F, -1.5F, 3F, 6F, 3F), PartPose.offset(0F, 6F, 0F));
        PartDefinition p_t6 = p_t5.addOrReplaceChild("t6", CubeListBuilder.create().texOffs(28, 32).addBox(-1F, 0F, -1F, 2F, 5F, 2F), PartPose.offset(0F, 6F, 0F));
        return LayerDefinition.create(mesh, 128, 64);
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
        this.body.y += Mth.sin(age * 0.07F) * 0.5F;
        this.arm_l.xRot = state.attacking ? -1.4F : Mth.sin(age * 0.05F) * 0.1F;
        this.arm_r.xRot = state.attacking ? -1.4F : -Mth.sin(age * 0.05F) * 0.1F;
        this.arm_l.zRot = -0.1F;
        this.arm_r.zRot = 0.1F;
        this.t2.yRot = Mth.sin(age * 0.12F - 2 * 0.8F) * 0.18F;
        this.t3.yRot = Mth.sin(age * 0.12F - 3 * 0.8F) * 0.18F;
        this.t4.yRot = Mth.sin(age * 0.12F - 4 * 0.8F) * 0.18F;
        this.t5.yRot = Mth.sin(age * 0.12F - 5 * 0.8F) * 0.18F;
        this.t6.yRot = Mth.sin(age * 0.12F - 6 * 0.8F) * 0.18F;
    }
}
