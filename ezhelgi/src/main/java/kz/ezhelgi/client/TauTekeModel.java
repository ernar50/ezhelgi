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
 * Модель тау-теке (горного козла) с саблевидными рогами.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class TauTekeModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart horn_l;
    private final ModelPart horn_l2;
    private final ModelPart horn_r;
    private final ModelPart horn_r2;
    private final ModelPart tail;
    private final ModelPart leg_fl;
    private final ModelPart leg_fr;
    private final ModelPart leg_bl;
    private final ModelPart leg_br;

    public TauTekeModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.horn_l = head.getChild("horn_l");
        this.horn_l2 = horn_l.getChild("horn_l2");
        this.horn_r = head.getChild("horn_r");
        this.horn_r2 = horn_r.getChild("horn_r2");
        this.tail = root.getChild("tail");
        this.leg_fl = root.getChild("leg_fl");
        this.leg_fr = root.getChild("leg_fr");
        this.leg_bl = root.getChild("leg_bl");
        this.leg_br = root.getChild("leg_br");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3.5F, -7F, 7F, 7F, 14F), PartPose.offset(0F, 13F, 0F));
        PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(42, 0).addBox(-2F, -3.5F, -5F, 4F, 4F, 5F).texOffs(60, 0).addBox(-1.5F, -2F, -6.8F, 3F, 2.5F, 2F).texOffs(70, 0).addBox(-1F, 0.5F, -5F, 2F, 4F, 1.5F), PartPose.offsetAndRotation(0F, 9F, -7F, 0.35F, 0F, 0F));
        PartDefinition p_horn_l = p_head.addOrReplaceChild("horn_l", CubeListBuilder.create().texOffs(78, 0).addBox(-0.9F, -0.9F, 0F, 1.8F, 1.8F, 6F), PartPose.offsetAndRotation(1.2F, -3.5F, -2.5F, -0.9F, 0.15F, 0.1F));
        PartDefinition p_horn_l2 = p_horn_l.addOrReplaceChild("horn_l2", CubeListBuilder.create().texOffs(94, 0).addBox(-0.75F, -0.75F, 0F, 1.5F, 1.5F, 6F), PartPose.offsetAndRotation(0F, 0F, 6F, 0.55F, 0F, 0F));
        PartDefinition p_horn_r = p_head.addOrReplaceChild("horn_r", CubeListBuilder.create().texOffs(110, 0).addBox(-0.9F, -0.9F, 0F, 1.8F, 1.8F, 6F), PartPose.offsetAndRotation(-1.2F, -3.5F, -2.5F, -0.9F, -0.15F, -0.1F));
        PartDefinition p_horn_r2 = p_horn_r.addOrReplaceChild("horn_r2", CubeListBuilder.create().texOffs(0, 21).addBox(-0.75F, -0.75F, 0F, 1.5F, 1.5F, 6F), PartPose.offsetAndRotation(0F, 0F, 6F, 0.55F, 0F, 0F));
        PartDefinition p_tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(16, 21).addBox(-0.8F, 0F, 0F, 1.6F, 2.5F, 1F), PartPose.offsetAndRotation(0F, 10F, 7F, 0.7F, 0F, 0F));
        PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create().texOffs(22, 21).addBox(-1.1F, 0F, -1.1F, 2.2F, 6.5F, 2.2F).texOffs(34, 21).addBox(-1.1F, 6.5F, -1.1F, 2.2F, 1F, 2.2F), PartPose.offset(-2.2F, 16.5F, -5F));
        PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create().texOffs(46, 21).addBox(-1.1F, 0F, -1.1F, 2.2F, 6.5F, 2.2F).texOffs(58, 21).addBox(-1.1F, 6.5F, -1.1F, 2.2F, 1F, 2.2F), PartPose.offset(2.2F, 16.5F, -5F));
        PartDefinition p_leg_bl = root.addOrReplaceChild("leg_bl", CubeListBuilder.create().texOffs(70, 21).addBox(-1.1F, 0F, -1.1F, 2.2F, 6.5F, 2.2F).texOffs(82, 21).addBox(-1.1F, 6.5F, -1.1F, 2.2F, 1F, 2.2F), PartPose.offset(-2.2F, 16.5F, 5F));
        PartDefinition p_leg_br = root.addOrReplaceChild("leg_br", CubeListBuilder.create().texOffs(94, 21).addBox(-1.1F, 0F, -1.1F, 2.2F, 6.5F, 2.2F).texOffs(106, 21).addBox(-1.1F, 6.5F, -1.1F, 2.2F, 1F, 2.2F), PartPose.offset(2.2F, 16.5F, 5F));
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
    }
}
