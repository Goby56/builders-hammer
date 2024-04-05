package com.goby56.buildershammer;

import com.goby56.buildershammer.item.CopperHammerItem;
import com.goby56.buildershammer.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

public class ModUtils {
    public static boolean blockPosEquals(BlockPos bp1, BlockPos bp2) {
        if (bp1 == null || bp2 == null) return false; // Evaluation to true is not interesting if both are null
        return bp1.getX() == bp2.getX() && bp1.getY() == bp2.getY() && bp1.getZ() == bp2.getZ();
    }

    public static boolean lookingAtChangeableBlock(MinecraftClient minecraftClient) {
        if (minecraftClient.crosshairTarget.getType() == HitResult.Type.BLOCK) {
            BlockState state = MinecraftClient.getInstance().world.getBlockState(((BlockHitResult) minecraftClient.crosshairTarget).getBlockPos());
            return CopperHammerItem.shouldCancelOrdinaryInteraction(state);
        }
        return false;
    }
}
