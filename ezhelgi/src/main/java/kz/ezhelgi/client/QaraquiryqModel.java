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
 * Модель джейрана (қарақұйрық) — стройной газели.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class QaraquiryqModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart horns;
    private final ModelPart tail;
    private final ModelPart leg_fl;
    private final ModelPart leg_fr;
    private final ModelPart leg_bl;
    private final ModelPart leg_br;

    public QaraquiryqModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.neck = root.getChild("neck");
        this.head = neck.getChild("head");
        this.horns = head.getChild("horns");
        this.tail = root.getChild("tail");
        this.leg_fl = root.getChild("leg_fl");
        this.leg_fr = root.getChild("leg_fr");
        this.leg_bl = root.getChild("leg_bl");
        this.leg_br = root.getChild("leg_br");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -3F, -6F, 5F, 5F, 12F), PartPose.offset(0F, 12F, 0F));
        PartDefinition p_neck = root.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(34, 0).addBox(-1.2F, -5F, -1.5F, 2.4F, 6F, 2.6F), PartPose.offsetAndRotation(0F, 10F, -5F, 0.6F, 0F, 0F));
        PartDefinition p_head = p_neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(46, 0).addBox(-1.5F, -2.5F, -4F, 3F, 3F, 4.5F).texOffs(0, 17).addBox(-1F, -1.5F, -5.4F, 2F, 2F, 1.6F).texOffs(8, 17).addBox(-1.8F, -3.5F, -0.5F, 0.8F, 1.5F, 0.6F).texOffs(12, 17).addBox(1F, -3.5F, -0.5F, 0.8F, 1.5F, 0.6F), PartPose.offsetAndRotation(0F, -5F, 0F, -0.6F, 0F, 0F));
        PartDefinition p_horns = p_head.addOrReplaceChild("horns", CubeListBuilder.create().texOffs(16, 17).addBox(-1.2F, -5F, 0F, 0.6F, 5F, 0.6F).texOffs(20, 17).addBox(0.6F, -5F, 0F, 0.6F, 5F, 0.6F), PartPose.offset(0F, -2.5F, -1.5F));
        PartDefinition p_tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(24, 17).addBox(-0.5F, 0F, 0F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, 10F, 6F, 0.5F, 0F, 0F));
        PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create().texOffs(28, 17).addBox(-0.7F, 0F, -0.7F, 1.4F, 9F, 1.4F).texOffs(36, 17).addBox(-0.7F, 9F, -0.7F, 1.4F, 1F, 1.4F), PartPose.offset(-1.5F, 14F, -4.5F));
        PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create().texOffs(44, 17).addBox(-0.7F, 0F, -0.7F, 1.4F, 9F, 1.4F).texOffs(52, 17).addBox(-0.7F, 9F, -0.7F, 1.4F, 1F, 1.4F), PartPose.offset(1.5F, 14F, -4.5F));
        PartDefinition p_leg_bl = root.addOrReplaceChild("leg_bl", CubeListBuilder.create().texOffs(0, 28).addBox(-0.7F, 0F, -0.7F, 1.4F, 9F, 1.4F).texOffs(8, 28).addBox(-0.7F, 9F, -0.7F, 1.4F, 1F, 1.4F), PartPose.offset(-1.5F, 14F, 4.5F));
        PartDefinition p_leg_br = root.addOrReplaceChild("leg_br", CubeListBuilder.create().texOffs(16, 28).addBox(-0.7F, 0F, -0.7F, 1.4F, 9F, 1.4F).texOffs(24, 28).addBox(-0.7F, 9F, -0.7F, 1.4F, 1F, 1.4F), PartPose.offset(1.5F, 14F, 4.5F));
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
        this.head.xRot += headPitch;
        float swing = Mth.cos(limbPos * 0.6662F) * 1.2F * limbSpeed;
        this.leg_fl.xRot = swing;
        this.leg_br.xRot = swing;
        this.leg_fr.xRot = -swing;
        this.leg_bl.xRot = -swing;
        this.horns.visible = state.male;
    }
}
