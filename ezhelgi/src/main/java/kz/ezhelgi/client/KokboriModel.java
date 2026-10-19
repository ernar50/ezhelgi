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
 * Модель Көкбөрі (степной волк).
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class KokboriModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart leg_fl;
    private final ModelPart leg_fr;
    private final ModelPart leg_bl;
    private final ModelPart leg_br;

    public KokboriModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.tail = root.getChild("tail");
        this.leg_fl = root.getChild("leg_fl");
        this.leg_fr = root.getChild("leg_fr");
        this.leg_bl = root.getChild("leg_bl");
        this.leg_br = root.getChild("leg_br");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -3.5F, -7F, 8F, 7F, 14F).texOffs(0, 21).addBox(-4.5F, -4.5F, -7.5F, 9F, 8F, 6F), PartPose.offset(0F, 13F, 0F));
        PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(30, 21).addBox(-3.5F, -3.5F, -6F, 7F, 7F, 6F).texOffs(0, 35).addBox(-2F, 0F, -9F, 4F, 3F, 3F).texOffs(14, 35).addBox(-3F, -6F, -3F, 2F, 3F, 1F).texOffs(20, 35).addBox(1F, -6F, -3F, 2F, 3F, 1F), PartPose.offset(0F, 11.5F, -7.5F));
        PartDefinition p_tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(26, 35).addBox(-1F, 0F, -1F, 2F, 9F, 2F), PartPose.offsetAndRotation(0F, 10.5F, 7F, 0.9F, 0F, 0F));
        PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create().texOffs(34, 35).addBox(-1.5F, 0F, -1.5F, 3F, 8F, 3F), PartPose.offset(-2.5F, 16F, -5F));
        PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create().texOffs(46, 35).addBox(-1.5F, 0F, -1.5F, 3F, 8F, 3F), PartPose.offset(2.5F, 16F, -5F));
        PartDefinition p_leg_bl = root.addOrReplaceChild("leg_bl", CubeListBuilder.create().texOffs(0, 46).addBox(-1.5F, 0F, -1.5F, 3F, 8F, 3F), PartPose.offset(-2.5F, 16F, 5F));
        PartDefinition p_leg_br = root.addOrReplaceChild("leg_br", CubeListBuilder.create().texOffs(12, 46).addBox(-1.5F, 0F, -1.5F, 3F, 8F, 3F), PartPose.offset(2.5F, 16F, 5F));
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
        float swing = Mth.cos(limbPos * 0.6662F) * 1.3F * limbSpeed;
        this.leg_fl.xRot = swing;
        this.leg_br.xRot = swing;
        this.leg_fr.xRot = -swing;
        this.leg_bl.xRot = -swing;
        this.tail.yRot = Mth.cos(age * 0.2F) * 0.15F + Mth.cos(limbPos * 0.6662F) * 0.4F * limbSpeed;
        if (state.sitting) {
            this.body.xRot = -0.45F;
            this.body.y += 2.0F;
            this.head.y += 0.5F;
            this.tail.y += 5.0F;
            this.tail.z -= 1.0F;
            this.tail.xRot = 1.5F;
            this.leg_bl.xRot = -1.5708F;
            this.leg_br.xRot = -1.5708F;
            this.leg_bl.y = 22.5F;
            this.leg_br.y = 22.5F;
            this.leg_bl.z -= 2.0F;
            this.leg_br.z -= 2.0F;
        }
    }
}
