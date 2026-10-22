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
 * Модель Айдаһара — змееподобного дракона с гривой и рогами.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class AidaharModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart horn_l;
    private final ModelPart whisker_l;
    private final ModelPart horn_r;
    private final ModelPart whisker_r;
    private final ModelPart mane;
    private final ModelPart seg0;
    private final ModelPart seg1;
    private final ModelPart leg1l;
    private final ModelPart leg1r;
    private final ModelPart seg2;
    private final ModelPart seg3;
    private final ModelPart seg4;
    private final ModelPart seg5;
    private final ModelPart seg6;
    private final ModelPart leg6l;
    private final ModelPart leg6r;
    private final ModelPart seg7;
    private final ModelPart seg8;
    private final ModelPart seg9;
    private final ModelPart seg10;
    private final ModelPart tailfin;

    public AidaharModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
        this.jaw = head.getChild("jaw");
        this.horn_l = head.getChild("horn_l");
        this.whisker_l = head.getChild("whisker_l");
        this.horn_r = head.getChild("horn_r");
        this.whisker_r = head.getChild("whisker_r");
        this.mane = head.getChild("mane");
        this.seg0 = root.getChild("seg0");
        this.seg1 = seg0.getChild("seg1");
        this.leg1l = seg1.getChild("leg1l");
        this.leg1r = seg1.getChild("leg1r");
        this.seg2 = seg1.getChild("seg2");
        this.seg3 = seg2.getChild("seg3");
        this.seg4 = seg3.getChild("seg4");
        this.seg5 = seg4.getChild("seg5");
        this.seg6 = seg5.getChild("seg6");
        this.leg6l = seg6.getChild("leg6l");
        this.leg6r = seg6.getChild("leg6r");
        this.seg7 = seg6.getChild("seg7");
        this.seg8 = seg7.getChild("seg8");
        this.seg9 = seg8.getChild("seg9");
        this.seg10 = seg9.getChild("seg10");
        this.tailfin = seg10.getChild("tailfin");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5F, -5F, -10F, 10F, 7F, 10F).texOffs(40, 0).addBox(-4F, -2F, -17F, 8F, 3F, 7F).texOffs(70, 0).addBox(-1.5F, -3F, -17F, 1F, 1F, 1F).texOffs(74, 0).addBox(0.5F, -3F, -17F, 1F, 1F, 1F), PartPose.offset(0F, 8F, -6F));
        PartDefinition p_jaw = p_head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(78, 0).addBox(-3.5F, 0F, -8F, 7F, 2F, 8F), PartPose.offset(0F, 2F, -9F));
        PartDefinition p_horn_l = p_head.addOrReplaceChild("horn_l", CubeListBuilder.create().texOffs(108, 0).addBox(-0.75F, -0.75F, 0F, 1.5F, 1.5F, 9F), PartPose.offsetAndRotation(3.5F, -5F, -2F, 0.8F, 0.3F, 0F));
        PartDefinition p_whisker_l = p_head.addOrReplaceChild("whisker_l", CubeListBuilder.create().texOffs(130, 0).addBox(-0.3F, -0.3F, 0F, 0.6F, 0.6F, 10F), PartPose.offsetAndRotation(3F, 0F, -15F, 0.3F, 0.5F, 0F));
        PartDefinition p_horn_r = p_head.addOrReplaceChild("horn_r", CubeListBuilder.create().texOffs(152, 0).addBox(-0.75F, -0.75F, 0F, 1.5F, 1.5F, 9F), PartPose.offsetAndRotation(-3.5F, -5F, -2F, 0.8F, -0.3F, 0F));
        PartDefinition p_whisker_r = p_head.addOrReplaceChild("whisker_r", CubeListBuilder.create().texOffs(174, 0).addBox(-0.3F, -0.3F, 0F, 0.6F, 0.6F, 10F), PartPose.offsetAndRotation(-3F, 0F, -15F, 0.3F, -0.5F, 0F));
        PartDefinition p_mane = p_head.addOrReplaceChild("mane", CubeListBuilder.create().texOffs(196, 0).addBox(-5.5F, -3F, -2F, 11F, 4F, 7F), PartPose.offsetAndRotation(0F, -5F, 0F, 0.3F, 0F, 0F));
        PartDefinition p_seg0 = root.addOrReplaceChild("seg0", CubeListBuilder.create().texOffs(0, 17).addBox(-4F, -4F, 0F, 8F, 8F, 8F).texOffs(32, 17).addBox(-0.5F, -6.5F, 2F, 1F, 2.5F, 3F), PartPose.offset(0F, 8F, 0F));
        PartDefinition p_seg1 = p_seg0.addOrReplaceChild("seg1", CubeListBuilder.create().texOffs(40, 17).addBox(-4F, -4F, 0F, 8F, 8F, 8F).texOffs(72, 17).addBox(-0.5F, -6.5F, 2F, 1F, 2.5F, 3F), PartPose.offset(0F, 0F, 8F));
        PartDefinition p_leg1l = p_seg1.addOrReplaceChild("leg1l", CubeListBuilder.create().texOffs(80, 17).addBox(-1.25F, 0F, -1.25F, 2.5F, 6F, 2.5F).texOffs(92, 17).addBox(-1.5F, 5.5F, -2.5F, 3F, 1F, 3F), PartPose.offsetAndRotation(4F, 3F, 4F, 0.3F, 0F, -0.5F));
        PartDefinition p_leg1r = p_seg1.addOrReplaceChild("leg1r", CubeListBuilder.create().texOffs(104, 17).addBox(-1.25F, 0F, -1.25F, 2.5F, 6F, 2.5F).texOffs(116, 17).addBox(-1.5F, 5.5F, -2.5F, 3F, 1F, 3F), PartPose.offsetAndRotation(-4F, 3F, 4F, 0.3F, 0F, 0.5F));
        PartDefinition p_seg2 = p_seg1.addOrReplaceChild("seg2", CubeListBuilder.create().texOffs(128, 17).addBox(-4F, -4F, 0F, 8F, 8F, 8F).texOffs(160, 17).addBox(-0.5F, -6.5F, 2F, 1F, 2.5F, 3F), PartPose.offset(0F, 0F, 8F));
        PartDefinition p_seg3 = p_seg2.addOrReplaceChild("seg3", CubeListBuilder.create().texOffs(168, 17).addBox(-3.75F, -3.75F, 0F, 7.5F, 7.5F, 8F).texOffs(200, 17).addBox(-0.5F, -6.25F, 2F, 1F, 2.5F, 3F), PartPose.offset(0F, 0F, 8F));
        PartDefinition p_seg4 = p_seg3.addOrReplaceChild("seg4", CubeListBuilder.create().texOffs(208, 17).addBox(-3.5F, -3.5F, 0F, 7F, 7F, 8F).texOffs(238, 17).addBox(-0.5F, -6F, 2F, 1F, 2.5F, 3F), PartPose.offset(0F, 0F, 8F));
        PartDefinition p_seg5 = p_seg4.addOrReplaceChild("seg5", CubeListBuilder.create().texOffs(0, 33).addBox(-3.25F, -3.25F, 0F, 6.5F, 6.5F, 8F).texOffs(30, 33).addBox(-0.5F, -5.75F, 2F, 1F, 2.5F, 3F), PartPose.offset(0F, 0F, 8F));
        PartDefinition p_seg6 = p_seg5.addOrReplaceChild("seg6", CubeListBuilder.create().texOffs(38, 33).addBox(-3F, -3F, 0F, 6F, 6F, 8F).texOffs(66, 33).addBox(-0.5F, -5.5F, 2F, 1F, 2.5F, 3F), PartPose.offset(0F, 0F, 8F));
        PartDefinition p_leg6l = p_seg6.addOrReplaceChild("leg6l", CubeListBuilder.create().texOffs(74, 33).addBox(-1.25F, 0F, -1.25F, 2.5F, 6F, 2.5F).texOffs(86, 33).addBox(-1.5F, 5.5F, -2.5F, 3F, 1F, 3F), PartPose.offsetAndRotation(3F, 2F, 4F, 0.3F, 0F, -0.5F));
        PartDefinition p_leg6r = p_seg6.addOrReplaceChild("leg6r", CubeListBuilder.create().texOffs(98, 33).addBox(-1.25F, 0F, -1.25F, 2.5F, 6F, 2.5F).texOffs(110, 33).addBox(-1.5F, 5.5F, -2.5F, 3F, 1F, 3F), PartPose.offsetAndRotation(-3F, 2F, 4F, 0.3F, 0F, 0.5F));
        PartDefinition p_seg7 = p_seg6.addOrReplaceChild("seg7", CubeListBuilder.create().texOffs(122, 33).addBox(-2.5F, -2.5F, 0F, 5F, 5F, 8F).texOffs(148, 33).addBox(-0.5F, -5F, 2F, 1F, 2.5F, 3F), PartPose.offset(0F, 0F, 8F));
        PartDefinition p_seg8 = p_seg7.addOrReplaceChild("seg8", CubeListBuilder.create().texOffs(156, 33).addBox(-2F, -2F, 0F, 4F, 4F, 8F).texOffs(180, 33).addBox(-0.5F, -4.5F, 2F, 1F, 2.5F, 3F), PartPose.offset(0F, 0F, 8F));
        PartDefinition p_seg9 = p_seg8.addOrReplaceChild("seg9", CubeListBuilder.create().texOffs(188, 33).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 8F).texOffs(210, 33).addBox(-0.5F, -4F, 2F, 1F, 2.5F, 3F), PartPose.offset(0F, 0F, 8F));
        PartDefinition p_seg10 = p_seg9.addOrReplaceChild("seg10", CubeListBuilder.create().texOffs(218, 33).addBox(-1F, -1F, 0F, 2F, 2F, 8F).texOffs(238, 33).addBox(-0.5F, -3.5F, 2F, 1F, 2.5F, 3F), PartPose.offset(0F, 0F, 8F));
        PartDefinition p_tailfin = p_seg10.addOrReplaceChild("tailfin", CubeListBuilder.create().texOffs(0, 48).addBox(-0.5F, -4F, 0F, 1F, 8F, 6F), PartPose.offset(0F, 0F, 8F));
        return LayerDefinition.create(mesh, 256, 256);
    }

    @Override
    public void setupAnim(EzhelgiRenderState state) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float limbPos = state.walkAnimationPos;
        float limbSpeed = Math.min(state.walkAnimationSpeed, 1.0F);
        float age = state.ageInTicks;
        float headYaw = Mth.clamp(state.yRot, -40.0F, 40.0F) * ((float) Math.PI / 180.0F);
        float headPitch = Mth.clamp(state.xRot, -30.0F, 40.0F) * ((float) Math.PI / 180.0F);

        this.head.yRot = headYaw * 0.5F;
        this.head.xRot = headPitch * 0.5F;
        this.jaw.xRot = 0.15F + Mth.sin(age * 0.1F) * 0.12F + (state.attacking ? 0.6F : 0.0F);
        this.seg0.yRot = Mth.sin(age * 0.18F - 0 * 0.7F) * 0.22F;
        this.seg0.xRot = Mth.cos(age * 0.14F - 0 * 0.6F) * 0.12F;
        this.seg1.yRot = Mth.sin(age * 0.18F - 1 * 0.7F) * 0.22F;
        this.seg1.xRot = Mth.cos(age * 0.14F - 1 * 0.6F) * 0.12F;
        this.seg2.yRot = Mth.sin(age * 0.18F - 2 * 0.7F) * 0.22F;
        this.seg2.xRot = Mth.cos(age * 0.14F - 2 * 0.6F) * 0.12F;
        this.seg3.yRot = Mth.sin(age * 0.18F - 3 * 0.7F) * 0.22F;
        this.seg3.xRot = Mth.cos(age * 0.14F - 3 * 0.6F) * 0.12F;
        this.seg4.yRot = Mth.sin(age * 0.18F - 4 * 0.7F) * 0.22F;
        this.seg4.xRot = Mth.cos(age * 0.14F - 4 * 0.6F) * 0.12F;
        this.seg5.yRot = Mth.sin(age * 0.18F - 5 * 0.7F) * 0.22F;
        this.seg5.xRot = Mth.cos(age * 0.14F - 5 * 0.6F) * 0.12F;
        this.seg6.yRot = Mth.sin(age * 0.18F - 6 * 0.7F) * 0.22F;
        this.seg6.xRot = Mth.cos(age * 0.14F - 6 * 0.6F) * 0.12F;
        this.seg7.yRot = Mth.sin(age * 0.18F - 7 * 0.7F) * 0.22F;
        this.seg7.xRot = Mth.cos(age * 0.14F - 7 * 0.6F) * 0.12F;
        this.seg8.yRot = Mth.sin(age * 0.18F - 8 * 0.7F) * 0.22F;
        this.seg8.xRot = Mth.cos(age * 0.14F - 8 * 0.6F) * 0.12F;
        this.seg9.yRot = Mth.sin(age * 0.18F - 9 * 0.7F) * 0.22F;
        this.seg9.xRot = Mth.cos(age * 0.14F - 9 * 0.6F) * 0.12F;
        this.seg10.yRot = Mth.sin(age * 0.18F - 10 * 0.7F) * 0.22F;
        this.seg10.xRot = Mth.cos(age * 0.14F - 10 * 0.6F) * 0.12F;
        this.leg1l.xRot = 0.3F + Mth.sin(age * 0.2F) * 0.3F;
        this.leg1r.xRot = 0.3F - Mth.sin(age * 0.2F) * 0.3F;
        this.leg6l.xRot = 0.3F - Mth.sin(age * 0.2F) * 0.3F;
        this.leg6r.xRot = 0.3F + Mth.sin(age * 0.2F) * 0.3F;
    }
}
