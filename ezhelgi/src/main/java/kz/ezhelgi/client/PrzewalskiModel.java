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
 * Модель лошади Пржевальского — мышастой, со стоячей гривой.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class PrzewalskiModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart leg_fl;
    private final ModelPart leg_fr;
    private final ModelPart leg_bl;
    private final ModelPart leg_br;
    private final ModelPart saddle;

    public PrzewalskiModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.neck = root.getChild("neck");
        this.head = neck.getChild("head");
        this.tail = root.getChild("tail");
        this.leg_fl = root.getChild("leg_fl");
        this.leg_fr = root.getChild("leg_fr");
        this.leg_bl = root.getChild("leg_bl");
        this.leg_br = root.getChild("leg_br");
        this.saddle = root.getChild("saddle");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-5F, -5F, -11F, 10F, 10F, 21F), PartPose.offset(0F, 9F, 0F));
        PartDefinition p_neck = root.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(62, 0).addBox(-2F, -10F, -3F, 4F, 11F, 6F).texOffs(82, 0).addBox(-1F, -12F, 2F, 2F, 12F, 2F), PartPose.offsetAndRotation(0F, 6F, -9F, 0.55F, 0F, 0F));
        PartDefinition p_head = p_neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(90, 0).addBox(-3F, -4F, -7F, 6F, 5F, 8F).texOffs(0, 31).addBox(-2.5F, -3F, -11F, 5F, 4F, 4F).texOffs(18, 31).addBox(-2.5F, -6F, -1F, 1F, 2F, 1F).texOffs(22, 31).addBox(1.5F, -6F, -1F, 1F, 2F, 1F), PartPose.offset(0F, -9F, 0F));
        PartDefinition p_tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(26, 31).addBox(-1.5F, 0F, -1.5F, 3F, 12F, 3F), PartPose.offsetAndRotation(0F, 5F, 10F, 0.5F, 0F, 0F));
        PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create().texOffs(38, 31).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(54, 31).addBox(-2F, 10F, -2F, 4F, 1F, 4F), PartPose.offset(-3F, 13F, -8F));
        PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create().texOffs(70, 31).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(86, 31).addBox(-2F, 10F, -2F, 4F, 1F, 4F), PartPose.offset(3F, 13F, -8F));
        PartDefinition p_leg_bl = root.addOrReplaceChild("leg_bl", CubeListBuilder.create().texOffs(102, 31).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(0, 46).addBox(-2F, 10F, -2F, 4F, 1F, 4F), PartPose.offset(-3F, 13F, 7F));
        PartDefinition p_leg_br = root.addOrReplaceChild("leg_br", CubeListBuilder.create().texOffs(16, 46).addBox(-2F, 0F, -2F, 4F, 10F, 4F).texOffs(32, 46).addBox(-2F, 10F, -2F, 4F, 1F, 4F), PartPose.offset(3F, 13F, 7F));
        PartDefinition p_saddle = root.addOrReplaceChild("saddle", CubeListBuilder.create().texOffs(48, 46).addBox(-5.5F, -6F, -5F, 11F, 1F, 10F).texOffs(90, 46).addBox(-3F, -7F, -3F, 6F, 1F, 6F).texOffs(0, 60).addBox(5F, -5F, -4F, 1F, 5F, 8F).texOffs(18, 60).addBox(-6F, -5F, -4F, 1F, 5F, 8F).texOffs(36, 60).addBox(-1F, -8F, -3F, 2F, 1F, 1F), PartPose.offset(0F, 9F, 0F));
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

        this.saddle.visible = state.saddled;
        this.neck.yRot = headYaw * 0.6F;
        this.neck.xRot += headPitch * 0.4F;
        float swing = Mth.cos(limbPos * 0.6662F) * 1.1F * limbSpeed;
        this.leg_fl.xRot = swing;
        this.leg_br.xRot = swing;
        this.leg_fr.xRot = -swing;
        this.leg_bl.xRot = -swing;
        this.tail.xRot += Mth.cos(age * 0.12F) * 0.08F + limbSpeed * 0.5F;
    }
}
