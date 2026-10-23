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
 * Модель перекати-поля — клубок сухих веток.
 * Сгенерировано tools/gen_models.py вместе с текстурой.
 */
@SuppressWarnings("unused")
public class QanbaqModel extends EntityModel<EzhelgiRenderState> {
    private final ModelPart ball;
    private final ModelPart t0;
    private final ModelPart t1;
    private final ModelPart t2;
    private final ModelPart t3;
    private final ModelPart t4;
    private final ModelPart t5;
    private final ModelPart t6;
    private final ModelPart t7;
    private final ModelPart t8;
    private final ModelPart c0;

    public QanbaqModel(ModelPart root) {
        super(root);
        this.ball = root.getChild("ball");
        this.t0 = ball.getChild("t0");
        this.t1 = ball.getChild("t1");
        this.t2 = ball.getChild("t2");
        this.t3 = ball.getChild("t3");
        this.t4 = ball.getChild("t4");
        this.t5 = ball.getChild("t5");
        this.t6 = ball.getChild("t6");
        this.t7 = ball.getChild("t7");
        this.t8 = ball.getChild("t8");
        this.c0 = ball.getChild("c0");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_ball = root.addOrReplaceChild("ball", CubeListBuilder.create(), PartPose.offset(0F, 17F, 0F));
        PartDefinition p_t0 = p_ball.addOrReplaceChild("t0", CubeListBuilder.create().texOffs(0, 0).addBox(-6F, -0.5F, -0.5F, 12F, 1F, 1F), PartPose.offset(0F, 0F, 0F));
        PartDefinition p_t1 = p_ball.addOrReplaceChild("t1", CubeListBuilder.create().texOffs(26, 0).addBox(-5F, -0.5F, -0.5F, 10F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0.7F, 1.3F, 0.45F));
        PartDefinition p_t2 = p_ball.addOrReplaceChild("t2", CubeListBuilder.create().texOffs(0, 2).addBox(-4F, -0.5F, -0.5F, 8F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 1.4F, 2.6F, 0.9F));
        PartDefinition p_t3 = p_ball.addOrReplaceChild("t3", CubeListBuilder.create().texOffs(18, 2).addBox(-6F, -0.5F, -0.5F, 12F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 2.1F, 0.8F, 1.35F));
        PartDefinition p_t4 = p_ball.addOrReplaceChild("t4", CubeListBuilder.create().texOffs(0, 4).addBox(-5F, -0.5F, -0.5F, 10F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 2.8F, 2.1F, 1.8F));
        PartDefinition p_t5 = p_ball.addOrReplaceChild("t5", CubeListBuilder.create().texOffs(22, 4).addBox(-4F, -0.5F, -0.5F, 8F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 0.4F, 0.3F, 2.25F));
        PartDefinition p_t6 = p_ball.addOrReplaceChild("t6", CubeListBuilder.create().texOffs(0, 6).addBox(-6F, -0.5F, -0.5F, 12F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 1.1F, 1.6F, 2.7F));
        PartDefinition p_t7 = p_ball.addOrReplaceChild("t7", CubeListBuilder.create().texOffs(26, 6).addBox(-5F, -0.5F, -0.5F, 10F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 1.8F, 2.9F, 0.05F));
        PartDefinition p_t8 = p_ball.addOrReplaceChild("t8", CubeListBuilder.create().texOffs(0, 8).addBox(-4F, -0.5F, -0.5F, 8F, 1F, 1F), PartPose.offsetAndRotation(0F, 0F, 0F, 2.5F, 1.1F, 0.5F));
        PartDefinition p_c0 = p_ball.addOrReplaceChild("c0", CubeListBuilder.create().texOffs(18, 8).addBox(-3F, -3F, -3F, 6F, 6F, 6F), PartPose.offsetAndRotation(0F, 0F, 0F, 0F, 0F, 0.3F));
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

        this.ball.xRot = limbPos * 0.9F;
        this.ball.zRot = Mth.sin(limbPos * 0.3F) * 0.2F;
    }
}
