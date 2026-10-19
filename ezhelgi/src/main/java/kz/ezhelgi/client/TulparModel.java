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
 * Модель Тұлпара (крылатый конь).
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class TulparModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart leg_fl;
    private final ModelPart leg_fr;
    private final ModelPart leg_bl;
    private final ModelPart leg_br;
    private final ModelPart wing_l;
    private final ModelPart wing_r;

    public TulparModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.neck = root.getChild("neck");
        this.head = neck.getChild("head");
        this.tail = root.getChild("tail");
        this.leg_fl = root.getChild("leg_fl");
        this.leg_fr = root.getChild("leg_fr");
        this.leg_bl = root.getChild("leg_bl");
        this.leg_br = root.getChild("leg_br");
        this.wing_l = root.getChild("wing_l");
        this.wing_r = root.getChild("wing_r");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5F, -5F, -11F, 10F, 10F, 22F), PartPose.offset(0F, 8F, 0F));
        PartDefinition p_neck = root.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(64, 0).addBox(-2F, -11F, -3F, 4F, 12F, 6F).texOffs(84, 0).addBox(-1F, -12F, 3F, 2F, 13F, 2F), PartPose.offsetAndRotation(0F, 5F, -9F, 0.55F, 0F, 0F));
        PartDefinition p_head = p_neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(92, 0).addBox(-3F, -4F, -7F, 6F, 5F, 8F).texOffs(0, 32).addBox(-2.5F, -3F, -11F, 5F, 4F, 4F).texOffs(18, 32).addBox(-2.5F, -6F, -1F, 1F, 2F, 1F).texOffs(22, 32).addBox(1.5F, -6F, -1F, 1F, 2F, 1F), PartPose.offset(0F, -10F, 0F));
        PartDefinition p_tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(26, 32).addBox(-1.5F, 0F, -1.5F, 3F, 13F, 3F), PartPose.offsetAndRotation(0F, 4F, 11F, 0.5F, 0F, 0F));
        PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create().texOffs(38, 32).addBox(-2F, 0F, -2F, 4F, 9F, 4F).texOffs(54, 32).addBox(-2.5F, 9F, -2.5F, 5F, 2F, 5F), PartPose.offset(-3F, 13F, -8F));
        PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create().texOffs(74, 32).addBox(-2F, 0F, -2F, 4F, 9F, 4F).texOffs(90, 32).addBox(-2.5F, 9F, -2.5F, 5F, 2F, 5F), PartPose.offset(3F, 13F, -8F));
        PartDefinition p_leg_bl = root.addOrReplaceChild("leg_bl", CubeListBuilder.create().texOffs(110, 32).addBox(-2F, 0F, -2F, 4F, 9F, 4F).texOffs(0, 48).addBox(-2.5F, 9F, -2.5F, 5F, 2F, 5F), PartPose.offset(-3F, 13F, 8F));
        PartDefinition p_leg_br = root.addOrReplaceChild("leg_br", CubeListBuilder.create().texOffs(20, 48).addBox(-2F, 0F, -2F, 4F, 9F, 4F).texOffs(36, 48).addBox(-2.5F, 9F, -2.5F, 5F, 2F, 5F), PartPose.offset(3F, 13F, 8F));
        PartDefinition p_wing_l = root.addOrReplaceChild("wing_l", CubeListBuilder.create().texOffs(56, 48).addBox(0F, -1F, 0F, 16F, 1F, 12F), PartPose.offset(5F, 4F, -6F));
        PartDefinition p_wing_r = root.addOrReplaceChild("wing_r", CubeListBuilder.create().texOffs(0, 61).addBox(-16F, -1F, 0F, 16F, 1F, 12F), PartPose.offset(-5F, 4F, -6F));
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

        this.neck.yRot = headYaw * 0.6F;
        this.neck.xRot += headPitch * 0.4F;
        float swing = Mth.cos(limbPos * 0.6662F) * 1.1F * limbSpeed;
        this.leg_fl.xRot = swing;
        this.leg_br.xRot = swing;
        this.leg_fr.xRot = -swing;
        this.leg_bl.xRot = -swing;
        this.tail.xRot += Mth.cos(age * 0.12F) * 0.08F + limbSpeed * 0.5F;
        float wing;
        if (state.flying) {
            wing = -0.25F + Mth.sin(age * 0.6F) * 0.65F;
        } else {
            wing = -1.25F + Mth.sin(age * 0.08F) * 0.05F - limbSpeed * 0.25F;
        }
        this.wing_l.zRot = wing;
        this.wing_r.zRot = -wing;
    }
}
