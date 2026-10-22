package kz.ezhelgi.client;

import com.mojang.blaze3d.vertex.PoseStack;
import kz.ezhelgi.EzhelgiMod;
import kz.ezhelgi.entity.Albasty;
import kz.ezhelgi.entity.Zheztyrnak;
import kz.ezhelgi.entity.ZhalmauyzKempir;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.Monster;

/** Рендерер жезтырнақ, албасты и жалмауыз кемпір. */
public class FolkRenderer<T extends Monster> extends HumanoidMobRenderer<T, FolkRenderState, FolkModel> {
    private final Identifier texture;
    private final Identifier revealedTexture;
    private final float scale;

    public FolkRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer, boolean hunched,
                        String texture, String revealedTexture, float scale) {
        super(context, new FolkModel(context.bakeLayer(layer), hunched), 0.5f * scale);
        this.texture = tex(texture);
        this.revealedTexture = tex(revealedTexture);
        this.scale = scale;
    }

    private static Identifier tex(String name) {
        return Identifier.fromNamespaceAndPath(EzhelgiMod.MODID, "textures/entity/" + name + ".png");
    }

    @Override
    public FolkRenderState createRenderState() {
        return new FolkRenderState();
    }

    @Override
    public void extractRenderState(T entity, FolkRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        state.claws = 0.0f;
        state.strike = 0.0f;
        state.revealed = false;
        state.casting = 0;
        if (entity instanceof Zheztyrnak z) {
            state.claws = z.clawsO + (z.claws - z.clawsO) * partialTicks;
            state.strike = z.strikeTicks();
            state.revealed = z.isRevealed();
        } else if (entity instanceof Albasty a) {
            state.revealed = a.getTarget() != null || a.isAggressive();
        } else if (entity instanceof kz.ezhelgi.entity.MystanKempir m) {
            state.revealed = m.isAggressive();
        } else if (entity instanceof ZhalmauyzKempir k) {
            state.casting = k.castingSpell();
        }
    }

    @Override
    public Identifier getTextureLocation(FolkRenderState state) {
        return state.revealed || state.casting > 0 ? this.revealedTexture : this.texture;
    }

    @Override
    public void submit(FolkRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        if (this.scale != 1.0f) {
            poseStack.scale(this.scale, this.scale, this.scale);
        }
        super.submit(state, poseStack, collector, camera);
    }
}
