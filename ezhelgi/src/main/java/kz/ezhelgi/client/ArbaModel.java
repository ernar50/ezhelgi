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
 * Модель арбы — деревянной телеги с большими колёсами.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class ArbaModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart bed;
    private final ModelPart axle;
    private final ModelPart wheel_l;
    private final ModelPart wheel_r;
    private final ModelPart shafts;

    public ArbaModel(ModelPart root) {
        super(root);
        this.bed = root.getChild("bed");
        this.axle = root.getChild("axle");
        this.wheel_l = root.getChild("wheel_l");
        this.wheel_r = root.getChild("wheel_r");
        this.shafts = root.getChild("shafts");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_bed = root.addOrReplaceChild("bed", CubeListBuilder.create().texOffs(0, 0).addBox(-8F, -2F, -11F, 16F, 2F, 22F).texOffs(76, 0).addBox(-8F, -6F, -11F, 1F, 4F, 22F).texOffs(0, 26).addBox(7F, -6F, -11F, 1F, 4F, 22F).texOffs(46, 26).addBox(-7F, -6F, 10F, 14F, 4F, 1F).texOffs(76, 26).addBox(-7F, -6F, -11F, 14F, 4F, 1F).texOffs(0, 52).addBox(-6F, -3F, -9F, 12F, 1F, 6F).texOffs(36, 52).addBox(-6F, -5F, -10F, 12F, 2F, 2F), PartPose.offset(0F, 14F, 0F));
        PartDefinition p_axle = root.addOrReplaceChild("axle", CubeListBuilder.create().texOffs(64, 52).addBox(-9F, -1F, -1F, 18F, 2F, 2F), PartPose.offset(0F, 17F, 1F));
        PartDefinition p_wheel_l = root.addOrReplaceChild("wheel_l", CubeListBuilder.create().texOffs(0, 59).addBox(-0.5F, -7F, -7F, 1F, 14F, 14F), PartPose.offset(9.5F, 17F, 1F));
        PartDefinition p_wheel_r = root.addOrReplaceChild("wheel_r", CubeListBuilder.create().texOffs(30, 59).addBox(-0.5F, -7F, -7F, 1F, 14F, 14F), PartPose.offset(-9.5F, 17F, 1F));
        PartDefinition p_shafts = root.addOrReplaceChild("shafts", CubeListBuilder.create().texOffs(60, 59).addBox(-6F, -1F, -22F, 1F, 1F, 22F).texOffs(0, 87).addBox(5F, -1F, -22F, 1F, 1F, 22F).texOffs(46, 87).addBox(-6F, -1F, -20F, 12F, 1F, 1F), PartPose.offset(0F, 14F, -11F));
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

        this.wheel_l.xRot = state.wheel;
        this.wheel_r.xRot = state.wheel;
        this.shafts.xRot = 0.12F;
    }
}
