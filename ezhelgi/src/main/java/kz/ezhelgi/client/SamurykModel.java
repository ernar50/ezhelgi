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
 * Модель Самұрыка — огромной золотой птицы.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class SamurykModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart wing_l;
    private final ModelPart wing_r;
    private final ModelPart tail;
    private final ModelPart plume_c;
    private final ModelPart plume_l;
    private final ModelPart plume_r;
    private final ModelPart plume_l2;
    private final ModelPart plume_r2;
    private final ModelPart leg_l;
    private final ModelPart leg_r;

    public SamurykModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.neck = root.getChild("neck");
        this.head = neck.getChild("head");
        this.wing_l = root.getChild("wing_l");
        this.wing_r = root.getChild("wing_r");
        this.tail = root.getChild("tail");
        this.plume_c = tail.getChild("plume_c");
        this.plume_l = tail.getChild("plume_l");
        this.plume_r = tail.getChild("plume_r");
        this.plume_l2 = tail.getChild("plume_l2");
        this.plume_r2 = tail.getChild("plume_r2");
        this.leg_l = root.getChild("leg_l");
        this.leg_r = root.getChild("leg_r");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -4F, -6F, 7F, 7F, 12F).texOffs(38, 0).addBox(-2.5F, -3F, -7.5F, 5F, 5F, 2F), PartPose.offsetAndRotation(0F, 15F, 0F, -0.15F, 0F, 0F));
        PartDefinition p_neck = root.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(52, 0).addBox(-2F, -5F, -2F, 4F, 6F, 4F), PartPose.offsetAndRotation(0F, 12.5F, -6F, 0.5F, 0F, 0F));
        PartDefinition p_head = p_neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(68, 0).addBox(-2.5F, -4F, -3.5F, 5F, 4F, 6F).texOffs(90, 0).addBox(-1F, -2.6F, -6.5F, 2F, 2F, 3F).texOffs(100, 0).addBox(-0.5F, -7F, -1F, 1F, 3F, 1F).texOffs(104, 0).addBox(-0.5F, -6F, 0.5F, 1F, 2F, 1F).texOffs(108, 0).addBox(-0.5F, -5.5F, 1.8F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, -4.5F, 0F, -0.5F, 0F, 0F));
        PartDefinition p_wing_l = root.addOrReplaceChild("wing_l", CubeListBuilder.create().texOffs(0, 19).addBox(0F, 0F, -10F, 1F, 15F, 10F), PartPose.offsetAndRotation(3.5F, 11F, -5F, 1.42F, 0F, -0.12F));
        PartDefinition p_wing_r = root.addOrReplaceChild("wing_r", CubeListBuilder.create().texOffs(22, 19).addBox(-1F, 0F, -10F, 1F, 15F, 10F), PartPose.offsetAndRotation(-3.5F, 11F, -5F, 1.42F, 0F, 0.12F));
        PartDefinition p_tail = root.addOrReplaceChild("tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, 13.5F, 5.5F, -0.2F, 0F, 0F));
        PartDefinition p_plume_c = p_tail.addOrReplaceChild("plume_c", CubeListBuilder.create().texOffs(44, 19).addBox(-1F, 0F, 0F, 2F, 1F, 18F), PartPose.offset(0F, 0F, 0F));
        PartDefinition p_plume_l = p_tail.addOrReplaceChild("plume_l", CubeListBuilder.create().texOffs(84, 19).addBox(-1F, 0F, 0F, 2F, 1F, 15F), PartPose.offsetAndRotation(1F, 0F, 0F, 0F, 0.32F, 0F));
        PartDefinition p_plume_r = p_tail.addOrReplaceChild("plume_r", CubeListBuilder.create().texOffs(0, 44).addBox(-1F, 0F, 0F, 2F, 1F, 15F), PartPose.offsetAndRotation(-1F, 0F, 0F, 0F, -0.32F, 0F));
        PartDefinition p_plume_l2 = p_tail.addOrReplaceChild("plume_l2", CubeListBuilder.create().texOffs(34, 44).addBox(-1F, 0F, 0F, 2F, 1F, 11F), PartPose.offsetAndRotation(1.5F, 0.5F, 0F, 0.1F, 0.62F, 0F));
        PartDefinition p_plume_r2 = p_tail.addOrReplaceChild("plume_r2", CubeListBuilder.create().texOffs(60, 44).addBox(-1F, 0F, 0F, 2F, 1F, 11F), PartPose.offsetAndRotation(-1.5F, 0.5F, 0F, 0.1F, -0.62F, 0F));
        PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create().texOffs(86, 44).addBox(-0.5F, 0F, -0.5F, 1F, 5.5F, 1F).texOffs(90, 44).addBox(-1F, 5F, -1.5F, 2F, 0.5F, 2F), PartPose.offset(1.6F, 18.5F, 1F));
        PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create().texOffs(98, 44).addBox(-0.5F, 0F, -0.5F, 1F, 5.5F, 1F).texOffs(102, 44).addBox(-1F, 5F, -1.5F, 2F, 0.5F, 2F), PartPose.offset(-1.6F, 18.5F, 1F));
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
        this.head.xRot += headPitch;
        this.leg_l.visible = state.flying || !state.sitting;
        this.leg_r.visible = state.flying || !state.sitting;
        float breathe = Mth.sin(age * 0.1F) * 0.03F;
        this.tail.xRot += Mth.sin(age * 0.07F) * 0.05F;
        if (state.flying) {
            float flap = Mth.sin(age * 0.35F) * 0.6F;
            this.wing_l.zRot = -1.35F + flap;
            this.wing_r.zRot = 1.35F - flap;
            this.wing_l.xRot = 0.0F;
            this.wing_r.xRot = 0.0F;
            this.wing_l.z += 10.0F;
            this.wing_r.z += 10.0F;
            this.leg_l.xRot = 1.1F;
            this.leg_r.xRot = 1.1F;
            this.tail.xRot -= 0.15F;
            this.neck.xRot += 0.3F;
            this.head.xRot -= 0.3F;
        } else {
            float step = Mth.cos(limbPos * 0.6662F) * 1.0F * limbSpeed;
            this.leg_l.xRot = step;
            this.leg_r.xRot = -step;
            this.wing_l.xRot = 1.42F + breathe;
            this.wing_r.xRot = 1.42F + breathe;
            this.wing_l.zRot = -0.12F;
            this.wing_r.zRot = 0.12F;
            if (state.sitting) {
                this.body.y += 3.0F;
                this.neck.y += 3.0F;
                this.wing_l.y += 3.0F;
                this.wing_r.y += 3.0F;
                this.tail.y += 3.0F;
            }
        }
    }
}
