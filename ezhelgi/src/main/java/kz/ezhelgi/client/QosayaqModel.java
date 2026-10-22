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
 * Модель тушканчика (қосаяқ) с длинным хвостом-кисточкой.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class QosayaqModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leg_l;
    private final ModelPart leg_r;
    private final ModelPart tail;

    public QosayaqModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.leg_l = root.getChild("leg_l");
        this.leg_r = root.getChild("leg_r");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -2F, -2F, 3F, 3.5F, 4F), PartPose.offsetAndRotation(0F, 20F, 0F, -0.5F, 0F, 0F));
        PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(14, 0).addBox(-1.5F, -2F, -2.5F, 3F, 2.5F, 3F).texOffs(26, 0).addBox(-1.3F, -5F, -0.5F, 1F, 3F, 0.4F).texOffs(30, 0).addBox(0.3F, -5F, -0.5F, 1F, 3F, 0.4F), PartPose.offset(0F, 18F, -2F));
        PartDefinition p_leg_l = root.addOrReplaceChild("leg_l", CubeListBuilder.create().texOffs(34, 0).addBox(-0.5F, 0F, -0.5F, 1F, 3F, 1F).texOffs(38, 0).addBox(-0.5F, 2.5F, -2.5F, 1F, 0.5F, 3F), PartPose.offset(1F, 21F, 1F));
        PartDefinition p_leg_r = root.addOrReplaceChild("leg_r", CubeListBuilder.create().texOffs(46, 0).addBox(-0.5F, 0F, -0.5F, 1F, 3F, 1F).texOffs(50, 0).addBox(-0.5F, 2.5F, -2.5F, 1F, 0.5F, 3F), PartPose.offset(-1F, 21F, 1F));
        PartDefinition p_tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 8).addBox(-0.3F, -0.3F, 0F, 0.6F, 0.6F, 8F).texOffs(18, 8).addBox(-0.8F, -0.8F, 7F, 1.6F, 1.6F, 2.5F), PartPose.offsetAndRotation(0F, 21F, 2F, -0.3F, 0F, 0F));
        return LayerDefinition.create(mesh, 64, 32);
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
        float hop = Mth.sin(limbPos * 0.9F) * limbSpeed;
        this.leg_l.xRot = hop;
        this.leg_r.xRot = hop;
        this.tail.xRot = -0.3F + hop * 0.3F;
    }
}
