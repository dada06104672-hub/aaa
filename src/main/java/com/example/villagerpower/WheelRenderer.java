package com.example.villagerpower;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** 回し車 + 中で走っている(縮小された)村人 */
public class WheelRenderer extends RotorRenderer<HamsterWheelBE> {

    public WheelRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx, ClientSetup.WHEEL_ROTOR);
    }

    @Override
    protected void renderExtra(HamsterWheelBE be, float pt, PoseStack ps, MultiBufferSource buffer, int light,
                               Direction.Axis axis) {
        Entity e = be.getRenderEntity();
        if (e == null || be.getLevel() == null) return;

        float yaw = (axis == Direction.Axis.X ? Direction.SOUTH : Direction.EAST).toYRot();
        if (be.getSpeed() < 0) yaw += 180f;
        e.setYRot(yaw);
        e.yRotO = yaw;
        if (e instanceof LivingEntity le) {
            le.yBodyRot = yaw;
            le.yBodyRotO = yaw;
            le.yHeadRot = yaw;
            le.yHeadRotO = yaw;
        }

        float t = be.getLevel().getGameTime() + pt;
        float bob = Math.abs(Mth.sin(t * Math.abs(be.getSpeed()) * 0.1f)) * 0.02f; // 走っている感じの上下動

        ps.pushPose();
        ps.translate(0.5, 0.16 + bob, 0.5);
        ps.scale(0.32f, 0.32f, 0.32f);
        EntityRenderDispatcher d = Minecraft.getInstance().getEntityRenderDispatcher();
        d.setRenderShadow(false);
        d.render(e, 0, 0, 0, 0f, pt, ps, buffer, light);
        d.setRenderShadow(true);
        ps.popPose();
    }
}
