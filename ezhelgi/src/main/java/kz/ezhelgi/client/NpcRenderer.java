package kz.ezhelgi.client;

import com.mojang.blaze3d.vertex.PoseStack;
import kz.ezhelgi.EzhelgiMod;
import kz.ezhelgi.entity.Balbal;
import kz.ezhelgi.entity.BaqsyKolenkesi;
import kz.ezhelgi.entity.KorkytAta;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Mob;

import java.util.function.Function;

/** Рендерер людей и духов мода (жители, великие люди, балбал, бақсы, дәу, разбойники). */
public class NpcRenderer<T extends Mob> extends HumanoidMobRenderer<T, NpcRenderState, NpcModel> {
    private final Function<T, String> texture;
    private final float scale;
    private final boolean ghost;

    public NpcRenderer(EntityRendererProvider.Context context, Function<T, String> texture, float scale, boolean ghost) {
        super(context, new NpcModel(context.bakeLayer(EzhelgiClient.HUMANOID_LAYER),
                ghost ? RenderTypes::entityTranslucent : RenderTypes::entityCutout), 0.5f * scale);
        this.texture = texture;
        this.scale = scale;
        this.ghost = ghost;
    }

    public static <T extends Mob> NpcRenderer<T> fixed(EntityRendererProvider.Context context, String name, float scale, boolean ghost) {
        return new NpcRenderer<>(context, e -> name, scale, ghost);
    }

    @Override
    public NpcRenderState createRenderState() {
        return new NpcRenderState();
    }

    @Override
    public void extractRenderState(T entity, NpcRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.texture = Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "textures/entity/" + this.texture.apply(entity) + ".png");
        state.playing = (entity instanceof BaqsyKolenkesi b && b.playingTicks() > 0)
                || (entity instanceof KorkytAta k && k.playingTicks() > 0)
                || (entity instanceof kz.ezhelgi.entity.SteppeVillager v && v.isPlaying());
        state.statue = entity instanceof Balbal balbal && !balbal.isAwake();
        if (state.statue) {
            state.walkAnimationSpeed = 0.0f;
            state.walkAnimationPos = 0.0f;
        }
    }

    @Override
    public Identifier getTextureLocation(NpcRenderState state) {
        return state.texture;
    }

    @Override
    public void submit(NpcRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        if (this.ghost) {
            poseStack.translate(0.0, 0.1 + Math.sin(state.ageInTicks * 0.08) * 0.06, 0.0);
        }
        float s = state.isBaby ? this.scale * 0.55f : this.scale;
        if (s != 1.0f) {
            poseStack.scale(s, s, s);
        }
        super.submit(state, poseStack, collector, camera);
        poseStack.popPose();
    }
}
