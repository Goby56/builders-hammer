package com.goby56.buildershammer.render;

import com.goby56.buildershammer.ModUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class PresetOutlineRenderer implements WorldRenderEvents.BeforeBlockOutline {
    private static final int outlineLifetime = 40;
    private static final ArrayList<RenderedOutline> outlines = new ArrayList<>();

    @Override
    public boolean beforeBlockOutline(WorldRenderContext context, @Nullable HitResult hitResult) {
        Camera camera = context.camera();
        Vec3d cameraPos = camera.getPos();

        boolean lookingAtOutline = false;
        BlockPos targetedBlock = null;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            targetedBlock = ((BlockHitResult) hitResult).getBlockPos();
        }
        synchronized (outlines) {
            for (RenderedOutline outline : outlines) {
                BlockPos blockPos = outline.blockPos;
                OutlineColors outlineColor = outline.color;
                WorldRenderer.drawCuboidShapeOutline(
                        context.matrixStack(),
                        context.consumers().getBuffer(RenderLayer.getLines()),
                        outline.blockState.getOutlineShape(context.world(), blockPos, ShapeContext.of(camera.getFocusedEntity())),
                        blockPos.getX() - cameraPos.x,
                        blockPos.getY() - cameraPos.y,
                        blockPos.getZ() - cameraPos.z,
                        outlineColor.r, outlineColor.g, outlineColor.b, outlineColor.a);
                if (ModUtils.blockPosEquals(targetedBlock, blockPos)) {
                    lookingAtOutline = true;
                }
            }
        }
        return !lookingAtOutline;
    }

    public static void addBlockOutline(BlockState blockState, BlockPos blockPos, OutlineColors outlineColor) {
        for (RenderedOutline outline : outlines) {
            if (outline.blockPos == blockPos || outline.blockState.getBlock() == blockState.getBlock()) {
                outline.blockPos = blockPos;
                outline.blockState = blockState;
                outline.color = outlineColor;
                outline.lifetime = 0;
                return;
            }
        }

        PresetOutlineRenderer.outlines.add(new RenderedOutline(blockState, blockPos, outlineColor));
    }

    public static void tick() {
        Stack<Integer> indicesToDelete = new Stack<>();
        int i = 0;
        for (RenderedOutline outline : outlines) {
            if (outline.lifetime > outlineLifetime) {
                indicesToDelete.add(i);
            } else {
                outline.lifetime++;
            }
            i++;
        }
        for (i = 0; i < indicesToDelete.size(); i++) {
            outlines.remove((int) indicesToDelete.pop());
        }
    }

    public static class RenderedOutline {
        private int lifetime = 0;
        private BlockPos blockPos;
        private BlockState blockState;
        private OutlineColors color;
    //
    //
        public RenderedOutline(BlockState blockState, BlockPos blockPos, OutlineColors outlineColor) {
            this.blockState = blockState;
            this.blockPos = blockPos;
            this.color = outlineColor;
        }
    }

    public enum OutlineColors {
        SAVED_PRESET(0, 1f, 0, .8f),
        REMOVED_PRESET(1f, 0, 0, .8f);

        private final float r;
        private final float g;
        private final float b;
        private final float a;

        OutlineColors(float r, float g, float b, float a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
    }
}
