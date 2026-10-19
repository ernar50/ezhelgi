package kz.ezhelgi.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

import java.util.function.Function;

/** Человеческая модель: умеет «играть на кобызе» и стоять неподвижно, как статуя. */
public class NpcModel extends HumanoidModel<NpcRenderState> {
    public NpcModel(ModelPart root, Function<Identifier, RenderType> renderType) {
        super(root, renderType);
    }

    @Override
    public void setupAnim(NpcRenderState state) {
        super.setupAnim(state);
        if (state.statue) {
            // балбал: руки сложены на груди, в правой — чаша
            this.head.xRot = 0.0f;
            this.head.yRot = 0.0f;
            this.rightArm.xRot = -1.1f;
            this.rightArm.yRot = -0.5f;
            this.leftArm.xRot = -0.9f;
            this.leftArm.yRot = 0.6f;
            this.rightLeg.xRot = 0.0f;
            this.leftLeg.xRot = 0.0f;
            return;
        }
        if (state.playing) {
            float t = state.ageInTicks;
            // левая рука держит гриф кобыза, правая водит смычком
            this.leftArm.xRot = -0.85f;
            this.leftArm.yRot = 0.45f;
            this.rightArm.xRot = -0.55f + Mth.sin(t * 0.35f) * 0.15f;
            this.rightArm.yRot = -0.35f + Mth.sin(t * 0.35f) * 0.45f;
            this.head.xRot = 0.25f;
            this.body.xRot = 0.08f;
        }
    }
}
