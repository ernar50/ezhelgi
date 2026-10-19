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
 * Модель барыса (снежного барса) с длинным пушистым хвостом.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class BarysModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart tail2;
    private final ModelPart leg_fl;
    private final ModelPart leg_fr;
    private final ModelPart leg_bl;
    private final ModelPart leg_br;

    public BarysModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.tail = root.getChild("tail");
        this.tail2 = tail.getChild("tail2");
        this.leg_fl = root.getChild("leg_fl");
        this.leg_fr = root.getChild("leg_fr");
        this.leg_bl = root.getChild("leg_bl");
        this.leg_br = root.getChild("leg_br");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -3F, -8F, 5F, 5.5F, 15F), PartPose.offset(0F, 15F, 0F));
        PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(40, 0).addBox(-2.5F, -3F, -4F, 5F, 4.5F, 5F).texOffs(60, 0).addBox(-1.5F, -0.8F, -5.3F, 3F, 2F, 1.5F).texOffs(70, 0).addBox(-2.6F, -4.2F, -1.5F, 1.4F, 1.4F, 1F).texOffs(76, 0).addBox(1.2F, -4.2F, -1.5F, 1.4F, 1.4F, 1F), PartPose.offset(0F, 13F, -8F));
        PartDefinition p_tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(82, 0).addBox(-1.4F, -1.4F, 0F, 2.8F, 2.8F, 9F), PartPose.offsetAndRotation(0F, 13F, 7F, -0.25F, 0F, 0F));
        PartDefinition p_tail2 = p_tail.addOrReplaceChild("tail2", CubeListBuilder.create().texOffs(106, 0).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 8F), PartPose.offsetAndRotation(0F, 0F, 9F, 0.7F, 0F, 0F));
        PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create().texOffs(0, 21).addBox(-1.1F, 0F, -1.1F, 2.2F, 7F, 2.2F), PartPose.offset(-1.6F, 17F, -6F));
        PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create().texOffs(12, 21).addBox(-1.1F, 0F, -1.1F, 2.2F, 7F, 2.2F), PartPose.offset(1.6F, 17F, -6F));
        PartDefinition p_leg_bl = root.addOrReplaceChild("leg_bl", CubeListBuilder.create().texOffs(24, 21).addBox(-1.1F, 0F, -1.1F, 2.2F, 7F, 2.2F), PartPose.offset(-1.6F, 17F, 5.5F));
        PartDefinition p_leg_br = root.addOrReplaceChild("leg_br", CubeListBuilder.create().texOffs(36, 21).addBox(-1.1F, 0F, -1.1F, 2.2F, 7F, 2.2F), PartPose.offset(1.6F, 17F, 5.5F));
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
        float swing = Mth.cos(limbPos * 0.6662F) * 1.2F * limbSpeed;
        this.leg_fl.xRot = swing;
        this.leg_br.xRot = swing;
        this.leg_fr.xRot = -swing;
        this.leg_bl.xRot = -swing;
        this.tail.yRot = Mth.sin(age * 0.08F) * 0.3F;
        this.tail2.xRot += Mth.sin(age * 0.11F) * 0.15F;
        if (state.flying) {
            this.leg_fl.xRot = -0.9F;
            this.leg_fr.xRot = -0.9F;
            this.leg_bl.xRot = 0.9F;
            this.leg_br.xRot = 0.9F;
            this.tail.xRot = 0.2F;
        }
    }
}
