package kz.ezhelgi.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kz.ezhelgi.kitchen.KeptirgishBlock;
import kz.ezhelgi.kitchen.KeptirgishBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** Рисует мясо и курт, развешанные на кептіргіше. */
public class KeptirgishRenderer implements BlockEntityRenderer<KeptirgishBlockEntity, KeptirgishRenderer.State> {
    private final ItemModelResolver items;

    public KeptirgishRenderer(BlockEntityRendererProvider.Context context) {
        this.items = context.itemModelResolver();
    }

    public static class State extends BlockEntityRenderState {
        public final ItemStackRenderState[] slots = new ItemStackRenderState[KeptirgishBlockEntity.SLOTS];
        public final boolean[] filled = new boolean[KeptirgishBlockEntity.SLOTS];
        public float yaw;

        public State() {
            for (int i = 0; i < this.slots.length; i++) this.slots[i] = new ItemStackRenderState();
        }
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(KeptirgishBlockEntity be, State state, float partialTicks, Vec3 camera,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTicks, camera, breakProgress);
        state.yaw = -be.getBlockState().getValue(KeptirgishBlock.FACING).toYRot();
        for (int i = 0; i < KeptirgishBlockEntity.SLOTS; i++) {
            ItemStack s = be.items().get(i);
            state.filled[i] = !s.isEmpty();
            if (state.filled[i]) {
                this.items.updateForTopItem(state.slots[i], s, ItemDisplayContext.FIXED, be.getLevel(), null, i);
            }
        }
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        for (int i = 0; i < KeptirgishBlockEntity.SLOTS; i++) {
            if (!state.filled[i]) continue;
            poseStack.pushPose();
            poseStack.translate(0.5, 0.0, 0.5);
            poseStack.rotate(Axis.YP.rotationDegrees(state.yaw));
            // две верхние перекладины по два места
            double x = (i % 2 == 0 ? -0.22 : 0.22);
            double y = i < 2 ? 0.72 : 0.38;
            poseStack.translate(x, y, 0.0);
            poseStack.rotate(Axis.ZP.rotationDegrees(i % 2 == 0 ? 8 : -8));
            poseStack.scale(0.42f, 0.42f, 0.42f);
            state.slots[i].submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }
}
