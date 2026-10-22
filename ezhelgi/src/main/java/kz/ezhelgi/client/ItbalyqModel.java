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
 * Модель каспийского тюленя (итбалық).
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class ItbalyqModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart flipper_l;
    private final ModelPart flipper_r;
    private final ModelPart tail;

    public ItbalyqModel(ModelPart root) {
        super(root);
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.flipper_l = root.getChild("flipper_l");
        this.flipper_r = root.getChild("flipper_r");
        this.tail = root.getChild("tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -3F, -6F, 7F, 6F, 12F).texOffs(38, 0).addBox(-2.5F, -2.5F, 6F, 5F, 5F, 4F), PartPose.offset(0F, 21F, 0F));
        PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 18).addBox(-3F, -3F, -4F, 6F, 5F, 4.5F).texOffs(22, 18).addBox(-1.5F, -1F, -5.5F, 3F, 2.5F, 1.5F), PartPose.offset(0F, 20F, -6F));
        PartDefinition p_flipper_l = root.addOrReplaceChild("flipper_l", CubeListBuilder.create().texOffs(32, 18).addBox(0F, -0.5F, -1F, 3F, 1F, 3F), PartPose.offset(3.5F, 23F, -3F));
        PartDefinition p_flipper_r = root.addOrReplaceChild("flipper_r", CubeListBuilder.create().texOffs(44, 18).addBox(-3F, -0.5F, -1F, 3F, 1F, 3F), PartPose.offset(-3.5F, 23F, -3F));
        PartDefinition p_tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 28).addBox(-3F, -0.5F, 0F, 6F, 1F, 3F), PartPose.offset(0F, 22F, 10F));
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
        float wave = Mth.sin(limbPos * 0.6F) * limbSpeed;
        this.flipper_l.zRot = -0.3F + wave * 0.6F;
        this.flipper_r.zRot = 0.3F - wave * 0.6F;
        this.tail.xRot = Mth.sin(age * 0.2F) * 0.15F + wave * 0.4F;
    }
}
