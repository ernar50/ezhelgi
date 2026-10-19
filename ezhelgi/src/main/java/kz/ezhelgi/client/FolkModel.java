package kz.ezhelgi.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/**
 * Человекоподобная модель для сказочных мобов: длинные волосы до пояса (или до колен),
 * медный нос и медные когти, которые выдвигаются из пальцев.
 * Развёртка текстуры — как у скина игрока 64×64; волосы, нос и когти
 * лежат в нижней половине текстуры.
 */
public class FolkModel extends HumanoidModel<FolkRenderState> {
    private final ModelPart hair;
    private final ModelPart nose;
    private final ModelPart rightClaws;
    private final ModelPart leftClaws;
    private final boolean hunched;

    public FolkModel(ModelPart root, boolean hunched) {
        super(root);
        this.hair = this.head.getChild("hair");
        this.nose = this.head.getChild("nose");
        this.rightClaws = this.rightArm.getChild("claws");
        this.leftClaws = this.leftArm.getChild("claws");
        this.hunched = hunched;
    }

    /**
     * @param hairLength длина волос по спине (в пикселях модели)
     * @param noseLength длина носа (0 — обычный нос на текстуре)
     */
    public static LayerDefinition createLayer(float hairLength, float noseLength) {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f);
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.getChild("head");
        // волосы: свисают со затылка за спину
        head.addOrReplaceChild("hair", CubeListBuilder.create().texOffs(0, 32)
                .addBox(-4.5f, -8.5f, 3.6f, 9.0f, hairLength, 1.0f), PartPose.ZERO);
        // нос (у жезтырнақ — медный)
        head.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 32)
                .addBox(-0.5f, -4.0f, -4.0f - Math.max(0.01f, noseLength), 1.0f, 2.0f, Math.max(0.01f, noseLength)), PartPose.ZERO);
        // когти: три тонких медных лезвия из кисти каждой руки
        CubeListBuilder claws = CubeListBuilder.create().texOffs(32, 32)
                .addBox(-1.6f, 0.0f, -1.6f, 0.6f, 7.0f, 0.6f)
                .texOffs(32, 32).addBox(-0.3f, 0.0f, -1.8f, 0.6f, 8.0f, 0.6f)
                .texOffs(32, 32).addBox(1.0f, 0.0f, -1.6f, 0.6f, 7.0f, 0.6f);
        root.getChild("right_arm").addOrReplaceChild("claws", claws, PartPose.offsetAndRotation(-1.0f, 9.5f, 0.0f, -0.25f, 0.0f, 0.0f));
        root.getChild("left_arm").addOrReplaceChild("claws", claws, PartPose.offsetAndRotation(1.0f, 9.5f, 0.0f, -0.25f, 0.0f, 0.0f));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(FolkRenderState state) {
        super.setupAnim(state);
        // волосы не крутятся вместе с наклоном головы вниз
        this.hair.xRot = -this.head.xRot * 0.7f + Mth.sin(state.ageInTicks * 0.08f) * 0.04f + state.walkAnimationSpeed * 0.25f;

        float claws = state.claws;
        boolean show = claws > 0.03f;
        this.rightClaws.visible = show;
        this.leftClaws.visible = show;
        if (show) {
            float len = claws * (1.0f + Math.min(1.0f, state.strike / 4.0f) * 0.8f);
            this.rightClaws.yScale = len;
            this.leftClaws.yScale = len;
            this.rightClaws.xScale = 1.0f;
            this.leftClaws.xScale = 1.0f;
        }
        if (state.revealed) {
            // хищная стойка: руки с когтями вперёд
            float sway = Mth.sin(state.ageInTicks * 0.2f) * 0.08f;
            float lunge = Math.min(1.0f, state.strike / 4.0f);
            this.rightArm.xRot = -1.2f + sway - lunge * 0.5f;
            this.leftArm.xRot = -1.2f - sway - lunge * 0.5f;
            this.rightArm.zRot = 0.15f;
            this.leftArm.zRot = -0.15f;
            this.body.xRot = 0.25f + lunge * 0.2f;
            this.head.z = -1.0f;
            this.rightArm.z = -1.0f;
            this.leftArm.z = -1.0f;
        }
        if (state.casting > 0) {
            // колдует: руки воздеты к небу
            float s = Mth.cos(state.ageInTicks * 0.6662f) * 0.25f;
            this.rightArm.xRot = -2.6f + s;
            this.leftArm.xRot = -2.6f - s;
            this.rightArm.zRot = 0.4f;
            this.leftArm.zRot = -0.4f;
            this.rightArm.yRot = 0.0f;
            this.leftArm.yRot = 0.0f;
        }
        if (this.hunched) {
            this.body.xRot += 0.35f;
            this.head.z -= 2.5f;
            this.head.y += 1.5f;
            this.rightArm.z -= 2.0f;
            this.leftArm.z -= 2.0f;
            this.rightArm.y += 1.0f;
            this.leftArm.y += 1.0f;
        }
    }
}
