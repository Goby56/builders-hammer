package com.goby56.buildershammer;

import net.minecraft.util.math.BlockPos;

public class ModUtils {
    public static boolean blockPosEquals(BlockPos bp1, BlockPos bp2) {
        if (bp1 == null || bp2 == null) return false; // Evaluation to true is not interesting if both are null
        return bp1.getX() == bp2.getX() && bp1.getY() == bp2.getY() && bp1.getZ() == bp2.getZ();
    }
}
