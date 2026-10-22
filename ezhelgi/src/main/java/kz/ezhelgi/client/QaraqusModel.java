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
 * Модель стервятника қарақұс.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class QaraqusModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart ruff;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart wing_l;
    private final ModelPart wing_r;
    private final ModelPart tail;
    private final ModelPart leg_l;
    private final ModelPart leg_r;

    public QaraqusModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.ruff = root.getChild("ruff");
        this.neck = root.getChild("neck");
        this.head = neck.getChild("head");
        this.wing_l = root.getChild("wing_l");
        this.wing_r = root.getChild("wing_r");
        this.tail = root.getChild("tail");
        this.leg_l = root.getChild("leg_l");
        this.leg_r = root.getChild("leg_r");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.5F, -4F, 7F, 7F, 9F), PartPose.offsetAndRotation(0F, 17F, 0F, -0.3F, 0F, 0F));
        PartDefinition p_ruff = root.addOrReplaceChild("ruff", CubeListBuilder.create().texOffs(32, 0).addBox(-3F, -1.5F, -2F, 6F, 3F, 3F), PartPose.offset(0F, 13.5F, -3F));
        PartDefinition p_neck = root.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(50, 0).addBox(-1F, -4F, -1F, 2F, 4F, 2F), PartPose.offsetAndRotation(0F, 13F, -4F, 0.6F, 0F, 0F));
        PartDefinition p_head = p_neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 16).addBox(-1.5F, -2.5F, -2.5F, 3F, 3F, 3F).texOffs(12, 16).addBox(-0.75F, -1.5F, -4.5F, 1.5F, 2F, 2F), PartPose.offsetAndRotation(0F, -4F, 0F, -0.6F, 0F, 0F));
        PartDefinition p_wing_l = root.addOrReplaceChild("wing_l", CubeListBuilder.create().texOffs(20, 16).addBox(0F, 0F, 0F, 1F, 9F, 11F), PartPose.offset(3.5F, 14F, -3F));
        PartDefinition p_wing_r = root.addOrReplaceChild("wing_r", CubeListBuilder.create().texOffs(0, 36).addBox(-1F, 0F, 0F, 1F, 9F, 11F), PartPose.offset(-3.5F, 14F, -3F));
        PartDefinition p_tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(24, 36).addBox(-2.5F, 0F, 0F, 5F, 1F, 5F), PartPose.offsetAndRotation(0F, 20F, 4.5F, -0.4F, 0F, 0F));
        PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create().texOffs(44, 36).addBox(-0.5F, 0F, -0.5F, 1F, 3F, 1F), PartPose.offset(1.5F, 21F, 0F));
        PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create().texOffs(48, 36).addBox(-0.5F, 0F, -0.5F, 1F, 3F, 1F), PartPose.offset(-1.5F, 21F, 0F));
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
        if (state.flying) {
            float flap = Mth.sin(age * 0.35F) * 0.4F;
            this.wing_l.zRot = -1.55F + flap;
            this.wing_r.zRot = 1.55F - flap;
            this.leg_l.xRot = 1.1F;
            this.leg_r.xRot = 1.1F;
        } else {
            float step = Mth.cos(limbPos * 0.6662F) * 1.2F * limbSpeed;
            this.leg_l.xRot = step;
            this.leg_r.xRot = -step;
            this.wing_l.zRot = -0.05F;
            this.wing_r.zRot = 0.05F;
        }
    }
}
