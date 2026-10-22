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
 * Модель корсака — маленькой степной лисы.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class QarsaqModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart tail;
    private final ModelPart leg_fl;
    private final ModelPart leg_fr;
    private final ModelPart leg_bl;
    private final ModelPart leg_br;

    public QarsaqModel(ModelPart root) {
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
        PartDefinition p_body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-2F, -2F, -5F, 4F, 4F, 9F), PartPose.offset(0F, 18F, 0F));
        PartDefinition p_head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(26, 0).addBox(-2.5F, -2F, -3F, 5F, 4F, 3F).texOffs(42, 0).addBox(-1F, 0F, -5F, 2F, 2F, 2F).texOffs(50, 0).addBox(-2.5F, -4.5F, -1.5F, 1.5F, 2.5F, 1F).texOffs(56, 0).addBox(1F, -4.5F, -1.5F, 1.5F, 2.5F, 1F), PartPose.offset(0F, 16.5F, -5F));
        PartDefinition p_tail = root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 13).addBox(-1.5F, -1.5F, 0F, 3F, 3F, 8F), PartPose.offsetAndRotation(0F, 17F, 4F, -0.9F, 0F, 0F));
        PartDefinition p_leg_fl = root.addOrReplaceChild("leg_fl", CubeListBuilder.create().texOffs(22, 13).addBox(-0.6F, 0F, -0.6F, 1.2F, 4F, 1.2F), PartPose.offset(-1.3F, 20F, -3.5F));
        PartDefinition p_leg_fr = root.addOrReplaceChild("leg_fr", CubeListBuilder.create().texOffs(30, 13).addBox(-0.6F, 0F, -0.6F, 1.2F, 4F, 1.2F), PartPose.offset(1.3F, 20F, -3.5F));
        PartDefinition p_leg_bl = root.addOrReplaceChild("leg_bl", CubeListBuilder.create().texOffs(38, 13).addBox(-0.6F, 0F, -0.6F, 1.2F, 4F, 1.2F), PartPose.offset(-1.3F, 20F, 3.5F));
        PartDefinition p_leg_br = root.addOrReplaceChild("leg_br", CubeListBuilder.create().texOffs(46, 13).addBox(-0.6F, 0F, -0.6F, 1.2F, 4F, 1.2F), PartPose.offset(1.3F, 20F, 3.5F));
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
        this.head.xRot += headPitch;
        float swing = Mth.cos(limbPos * 0.6662F) * 1.2F * limbSpeed;
        this.leg_fl.xRot = swing;
        this.leg_br.xRot = swing;
        this.leg_fr.xRot = -swing;
        this.leg_bl.xRot = -swing;
        this.tail.yRot = Mth.sin(age * 0.15F) * 0.2F;
    }
}
