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
 * Модель ақбөкена (сайгака) с горбатым носом и рогами у самцов.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class SaigaModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart horns;
    private final ModelPart tail;
    private final ModelPart leg_fl;
    private final ModelPart leg_fr;
    private final ModelPart leg_bl;
    private final ModelPart leg_br;

    public SaigaModel(ModelPart root) {
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
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3F, -3F, -7F, 6F, 6F, 13F), PartPose.offset(0F, 13F, 0F));
        PartDefinition p_neck = root.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(38, 0).addBox(-1.5F, -4.5F, -2F, 3F, 5.5F, 3.5F), PartPose.offsetAndRotation(0F, 11.5F, -6F, 1F, 0F, 0F));
        PartDefinition p_head = p_neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 19).addBox(-2F, -3F, -5F, 4F, 4F, 6F).texOffs(20, 19).addBox(-1.75F, -2.8F, -7.2F, 3.5F, 3.3F, 2.5F).texOffs(34, 19).addBox(-1.3F, 0.2F, -7.4F, 2.6F, 1.6F, 1.6F).texOffs(44, 19).addBox(-3F, -4F, 0F, 1F, 2F, 1F).texOffs(48, 19).addBox(2F, -4F, 0F, 1F, 2F, 1F), PartPose.offsetAndRotation(0F, -4F, 0F, -1F, 0F, 0F));
        PartDefinition p_horns = p_head.addOrReplaceChild("horns", CubeListBuilder.create().texOffs(52, 19).addBox(-1.8F, -4.5F, -0.4F, 0.8F, 4.5F, 0.8F).texOffs(56, 19).addBox(1F, -4.5F, -0.4F, 0.8F, 4.5F, 0.8F), PartPose.offset(0F, -3F, -2F));
        PartDefinition p_tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(60, 19).addBox(-0.5F, 0F, 0F, 1F, 3F, 1F), PartPose.offsetAndRotation(0F, 11F, 6F, 0.6F, 0F, 0F));
        PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create().texOffs(0, 29).addBox(-0.8F, 0F, -0.8F, 1.6F, 7F, 1.6F).texOffs(8, 29).addBox(-0.8F, 7F, -0.8F, 1.6F, 1F, 1.6F), PartPose.offset(-2F, 16F, -5F));
        PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create().texOffs(16, 29).addBox(-0.8F, 0F, -0.8F, 1.6F, 7F, 1.6F).texOffs(24, 29).addBox(-0.8F, 7F, -0.8F, 1.6F, 1F, 1.6F), PartPose.offset(2F, 16F, -5F));
        PartDefinition p_leg_bl = root.addOrReplaceChild("leg_bl", CubeListBuilder.create().texOffs(32, 29).addBox(-0.8F, 0F, -0.8F, 1.6F, 7F, 1.6F).texOffs(40, 29).addBox(-0.8F, 7F, -0.8F, 1.6F, 1F, 1.6F), PartPose.offset(-2F, 16F, 4.5F));
        PartDefinition p_leg_br = root.addOrReplaceChild("leg_br", CubeListBuilder.create().texOffs(48, 29).addBox(-0.8F, 0F, -0.8F, 1.6F, 7F, 1.6F).texOffs(56, 29).addBox(-0.8F, 7F, -0.8F, 1.6F, 1F, 1.6F), PartPose.offset(2F, 16F, 4.5F));
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

        this.horns.visible = state.male;
        this.neck.yRot = headYaw * 0.7F;
        this.head.xRot += headPitch * 0.5F;
        float swing = Mth.cos(limbPos * 0.6662F) * 1.3F * limbSpeed;
        this.leg_fl.xRot = swing;
        this.leg_br.xRot = swing;
        this.leg_fr.xRot = -swing;
        this.leg_bl.xRot = -swing;
        this.tail.xRot += Mth.sin(age * 0.3F) * 0.2F;
    }
}
