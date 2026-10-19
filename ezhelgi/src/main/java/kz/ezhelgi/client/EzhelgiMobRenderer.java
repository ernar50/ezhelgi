package kz.ezhelgi.client;

import com.mojang.blaze3d.vertex.PoseStack;
import kz.ezhelgi.entity.Arba;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;

/** Простой рендерер для животных мода с собственными моделями. */
public class EzhelgiMobRenderer<T extends Mob> extends MobRenderer<T, EzhelgiRenderState, EntityModel<EzhelgiRenderState>> {
    private final Identifier texture;
    private final float scale;

    public EzhelgiMobRenderer(EntityRendererProvider.Context context, EntityModel<EzhelgiRenderState> model,
                              float shadowRadius, Identifier texture, float scale) {
        super(context, model, shadowRadius);
        this.texture = texture;
        this.scale = scale;
    }

    @Override
    public EzhelgiRenderState createRenderState() {
        return new EzhelgiRenderState();
    }

    @Override
    public void extractRenderState(T entity, EzhelgiRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.sitting = entity instanceof TamableAnimal tamable && tamable.isInSittingPose();
        state.flying = !entity.onGround() && !entity.isPassenger();
        state.saddled = !entity.getItemBySlot(EquipmentSlot.SADDLE).isEmpty();
        if (entity instanceof Arba arba) {
            state.wheel = arba.wheelRotO + (arba.wheelRot - arba.wheelRotO) * partialTicks;
            state.flying = false;
        }
    }

    @Override
    public Identifier getTextureLocation(EzhelgiRenderState state) {
        return this.texture;
    }

    @Override
    public void submit(EzhelgiRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        float s = state.isBaby ? this.scale * 0.5f : this.scale;
        poseStack.scale(s, s, s);
        super.submit(state, poseStack, collector, camera);
    }
}
