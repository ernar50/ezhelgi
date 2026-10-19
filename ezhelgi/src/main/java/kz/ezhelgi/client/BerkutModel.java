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
 * Модель Бүркіта (беркут): золотистый затылок, белое основание хвоста.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class BerkutModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart wing_l;
    private final ModelPart wing_r;
    private final ModelPart tail;
    private final ModelPart leg_l;
    private final ModelPart leg_r;

    public BerkutModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.wing_l = root.getChild("wing_l");
        this.wing_r = root.getChild("wing_r");
        this.tail = root.getChild("tail");
        this.leg_l = root.getChild("leg_l");
        this.leg_r = root.getChild("leg_r");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3F, -3.5F, -3.5F, 6F, 7F, 8F).texOffs(28, 0).addBox(-2.5F, -2.5F, -4.5F, 5F, 5F, 1.5F), PartPose.offsetAndRotation(0F, 18F, 0F, -0.25F, 0F, 0F));
        PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(42, 0).addBox(-2F, -4F, -3F, 4F, 4F, 4F).texOffs(0, 15).addBox(-1F, -3F, -4.5F, 2F, 1.5F, 1.5F).texOffs(8, 15).addBox(-0.75F, -2.5F, -5.5F, 1.5F, 2.5F, 1.2F), PartPose.offset(0F, 14.5F, -3F));
        PartDefinition p_wing_l = root.addOrReplaceChild("wing_l", CubeListBuilder.create().texOffs(16, 15).addBox(0F, 0F, 0F, 1F, 7F, 9F).texOffs(36, 15).addBox(0F, 6F, 3F, 1F, 3F, 6F), PartPose.offset(3F, 15F, -2.5F));
        PartDefinition p_wing_r = root.addOrReplaceChild("wing_r", CubeListBuilder.create().texOffs(0, 31).addBox(-1F, 0F, 0F, 1F, 7F, 9F).texOffs(20, 31).addBox(-1F, 6F, 3F, 1F, 3F, 6F), PartPose.offset(-3F, 15F, -2.5F));
        PartDefinition p_tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(34, 31).addBox(-2.5F, 0F, 0F, 5F, 1F, 6F), PartPose.offsetAndRotation(0F, 20.5F, 4F, -0.35F, 0F, 0F));
        PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create().texOffs(56, 31).addBox(-1F, -0.5F, -1F, 2F, 2F, 2F).texOffs(0, 47).addBox(-0.5F, 1.5F, -0.5F, 1F, 1.5F, 1F).texOffs(4, 47).addBox(-1F, 2.5F, -1.5F, 2F, 0.5F, 2F), PartPose.offset(1.5F, 21F, 0.5F));
        PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create().texOffs(12, 47).addBox(-1F, -0.5F, -1F, 2F, 2F, 2F).texOffs(20, 47).addBox(-0.5F, 1.5F, -0.5F, 1F, 1.5F, 1F).texOffs(24, 47).addBox(-1F, 2.5F, -1.5F, 2F, 0.5F, 2F), PartPose.offset(-1.5F, 21F, 0.5F));
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
        if (state.flying) {
            float flap = Mth.sin(age * 0.6F) * 0.55F;
            this.wing_l.zRot = -1.5F + flap;
            this.wing_r.zRot = 1.5F - flap;
            this.wing_l.xRot = 0.1F;
            this.wing_r.xRot = 0.1F;
            this.leg_l.xRot = 1.0F;
            this.leg_r.xRot = 1.0F;
            this.tail.xRot = -0.05F;
            this.body.xRot = 0.1F;
        } else {
            float step = Mth.cos(limbPos * 0.6662F) * 1.2F * limbSpeed;
            this.leg_l.xRot = step;
            this.leg_r.xRot = -step;
            this.wing_l.zRot = -0.06F;
            this.wing_r.zRot = 0.06F;
            this.wing_l.xRot = 0.12F;
            this.wing_r.xRot = 0.12F;
        }
    }
}
