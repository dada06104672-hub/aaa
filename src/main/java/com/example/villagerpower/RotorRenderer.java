package com.example.villagerpower;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

/** 回転する部品(ローター)を描画する。モデルはX軸方向で作ってあり、ここで軸に合わせて向きを変える。 */
public class RotorRenderer<T extends KineticBlockEntity> implements BlockEntityRenderer<T> {
    private final ResourceLocation rotorModel;

    public RotorRenderer(BlockEntityRendererProvider.Context ctx, ResourceLocation rotorModel) {
        this.rotorModel = rotorModel;
    }

    @Override
    public void render(T be, float partialTick, PoseStack ps, MultiBufferSource buffer, int light, int overlay) {
        if (be.getLevel() == null) return;
        Direction.Axis axis = be.getBlockState().getValue(RotatedPillarKineticBlock.AXIS);
        // RPM -> 度/tick : rpm * 360 / 60 / 20 = rpm * 0.3
        float angle = ((be.getLevel().getGameTime() + partialTick) * be.getSpeed() * 0.3f) % 360f;

        ps.pushPose();
        ps.translate(0.5, 0.5, 0.5);
        if (axis == Direction.Axis.Y) ps.mulPose(Axis.ZP.rotationDegrees(90));
        else if (axis == Direction.Axis.Z) ps.mulPose(Axis.YP.rotationDegrees(90));
        ps.mulPose(Axis.XP.rotationDegrees(angle));
        ps.translate(-0.5, -0.5, -0.5);

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(rotorModel);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                ps.last(), buffer.getBuffer(RenderType.cutout()), null, model, 1f, 1f, 1f, light, overlay);
        ps.popPose();

        renderExtra(be, partialTick, ps, buffer, light, axis);
    }

    protected void renderExtra(T be, float partialTick, PoseStack ps, MultiBufferSource buffer, int light,
                               Direction.Axis axis) {
    }
}
