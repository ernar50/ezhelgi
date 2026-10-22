package kz.ezhelgi.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kz.ezhelgi.block.togyz.TogyzBlock;
import kz.ezhelgi.block.togyz.TogyzBlockEntity;
import kz.ezhelgi.block.togyz.TogyzGame;
import kz.ezhelgi.item.ModItems;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

/** Рисует құмалақ в лунках и қазанах доски. */
public class TogyzRenderer implements BlockEntityRenderer<TogyzBlockEntity, TogyzRenderer.State> {
    private static final float STONE = 0.24f;
    private final ItemModelResolver items;

    public TogyzRenderer(BlockEntityRendererProvider.Context context) {
        this.items = context.itemModelResolver();
    }

    public static class State extends BlockEntityRenderState {
        public final ItemStackRenderState stone = new ItemStackRenderState();
        public final ItemStackRenderState marker = new ItemStackRenderState();
        /** x, y, z, поворот, тип (0 — құмалақ, 1 — метка тұздық) для каждого камня. */
        public float[] data = new float[0];
        public int count;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(TogyzBlockEntity be, State state, float partialTicks, Vec3 camera,
                                   ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(be, state, partialTicks, camera, breakProgress);
        this.items.updateForTopItem(state.stone, new ItemStack(ModItems.QUMALAQ.get()), ItemDisplayContext.FIXED, be.getLevel(), null, 0);
        this.items.updateForTopItem(state.marker, new ItemStack(ModItems.QUMALAQ_TUZDYK.get()), ItemDisplayContext.FIXED, be.getLevel(), null, 1);
        Direction facing = be.getBlockState().getValue(TogyzBlock.FACING);
        Direction along = facing.getClockWise();
        TogyzGame g = be.game;
        int total = 2;
        for (int p : g.pits) total += p;
        total += g.kazan[0] + g.kazan[1];
        if (state.data.length < total * 5) {
            state.data = new float[total * 5 + 40];
        }
        state.count = 0;
        for (int side = 0; side < 2; side++) {
            for (int i = 0; i < TogyzGame.PITS; i++) {
                int pit = side * TogyzGame.PITS + i;
                int column = side == 0 ? i : 8 - i;
                double cu = (column + 0.5) * 2.0 / 9.0;
                double cv = side == 0 ? 0.24 : 0.76;
                if (g.tuzdyk[0] == pit || g.tuzdyk[1] == pit) {
                    put(state, facing, along, cu, cv, 0.19, 0.0f, 1);
                    continue;
                }
                int n = g.pits[pit];
                for (int k = 0; k < n; k++) {
                    int col = k % 2, row = (k / 2) % 5, layer = k / 10;
                    double du = (col - 0.5) * 0.075 + (layer % 2) * 0.03;
                    double dv = (row - 2) * 0.062 + (layer % 2) * 0.02;
                    put(state, facing, along, cu + du, cv + dv, 0.157 + layer * 0.05, (k * 47) % 360, 0);
                }
            }
            // қазан: длинная канавка посередине, у каждого своя половина
            int n = g.kazan[side];
            double startU = side == 0 ? 0.12 : 1.88;
            double dir = side == 0 ? 1 : -1;
            for (int k = 0; k < n; k++) {
                int col = k % 12, row = (k / 12) % 2, layer = k / 24;
                double u = startU + dir * (col * 0.068 + (layer % 2) * 0.03);
                double v = 0.5 + (row - 0.5) * 0.06;
                put(state, facing, along, u, v, 0.157 + layer * 0.05, (k * 71) % 360, 0);
            }
        }
    }

    /** Переводит координаты доски (u — вдоль, v — от игрока) в координаты блока. */
    private static void put(State s, Direction facing, Direction along, double u, double v, double y, float rot, int type) {
        if (s.count * 5 + 5 > s.data.length) return;
        double x = 0.5 + (u - 0.5) * along.getStepX() + (v - 0.5) * facing.getStepX();
        double z = 0.5 + (u - 0.5) * along.getStepZ() + (v - 0.5) * facing.getStepZ();
        int o = s.count * 5;
        s.data[o] = (float) x;
        s.data[o + 1] = (float) y;
        s.data[o + 2] = (float) z;
        s.data[o + 3] = rot;
        s.data[o + 4] = type;
        s.count++;
    }

    @Override
    public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        for (int i = 0; i < state.count; i++) {
            int o = i * 5;
            poseStack.pushPose();
            poseStack.translate(state.data[o], state.data[o + 1], state.data[o + 2]);
            poseStack.rotate(Axis.YP.rotationDegrees(state.data[o + 3]));
            boolean marker = state.data[o + 4] > 0.5f;
            float scale = marker ? STONE * 2.2f : STONE;
            poseStack.scale(scale, scale, scale);
            (marker ? state.marker : state.stone).submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 32;
    }
}
