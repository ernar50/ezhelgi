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
 * Модель тянь-шаньского бурого медведя (светлого, с длинными когтями).
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class AyuModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart leg_fl;
    private final ModelPart leg_fr;
    private final ModelPart leg_bl;
    private final ModelPart leg_br;

    public AyuModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.leg_fl = root.getChild("leg_fl");
        this.leg_fr = root.getChild("leg_fr");
        this.leg_bl = root.getChild("leg_bl");
        this.leg_br = root.getChild("leg_br");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-6F, -5F, -10F, 12F, 11F, 20F).texOffs(64, 0).addBox(-5F, -7F, -9F, 10F, 3F, 7F), PartPose.offset(0F, 10F, 0F));
        PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(98, 0).addBox(-4F, -4F, -6F, 8F, 8F, 7F).texOffs(0, 31).addBox(-2.5F, -0.5F, -9F, 5F, 4F, 3.5F).texOffs(18, 31).addBox(-4.5F, -5.5F, -2.5F, 2.5F, 2.5F, 1.5F).texOffs(28, 31).addBox(2F, -5.5F, -2.5F, 2.5F, 2.5F, 1.5F), PartPose.offset(0F, 7F, -10F));
        PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create().texOffs(38, 31).addBox(-2.25F, 0F, -2.25F, 4.5F, 9F, 4.5F).texOffs(58, 31).addBox(-2.25F, 8.5F, -3F, 4.5F, 0.5F, 1F), PartPose.offset(-3.5F, 15F, -7F));
        PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create().texOffs(70, 31).addBox(-2.25F, 0F, -2.25F, 4.5F, 9F, 4.5F).texOffs(90, 31).addBox(-2.25F, 8.5F, -3F, 4.5F, 0.5F, 1F), PartPose.offset(3.5F, 15F, -7F));
        PartDefinition p_leg_bl = root.addOrReplaceChild("leg_bl", CubeListBuilder.create().texOffs(102, 31).addBox(-2.25F, 0F, -2.25F, 4.5F, 9F, 4.5F).texOffs(0, 45).addBox(-2.25F, 8.5F, -3F, 4.5F, 0.5F, 1F), PartPose.offset(-3.5F, 15F, 7F));
        PartDefinition p_leg_br = root.addOrReplaceChild("leg_br", CubeListBuilder.create().texOffs(12, 45).addBox(-2.25F, 0F, -2.25F, 4.5F, 9F, 4.5F).texOffs(32, 45).addBox(-2.25F, 8.5F, -3F, 4.5F, 0.5F, 1F), PartPose.offset(3.5F, 15F, 7F));
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
        this.head.xRot += headPitch;
        float swing = Mth.cos(limbPos * 0.6662F) * 1.2F * limbSpeed;
        this.leg_fl.xRot = swing;
        this.leg_br.xRot = swing;
        this.leg_fr.xRot = -swing;
        this.leg_bl.xRot = -swing;
        if (state.sitting) {
            this.body.xRot = -0.9F;
            this.body.y += 4.0F;
            this.head.y -= 6.0F;
            this.head.z += 4.0F;
            this.leg_fl.xRot = -1.2F;
            this.leg_fr.xRot = -1.2F;
            this.leg_fl.y -= 7.0F;
            this.leg_fr.y -= 7.0F;
            this.leg_fl.z += 4.0F;
            this.leg_fr.z += 4.0F;
        }
    }
}
