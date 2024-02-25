package com.goby56.buildershammer;

import net.minecraft.util.math.Direction;

public enum PropertyController {
    SNEAKING(null),
    FACING_NORTH(Direction.NORTH),
    FACING_EAST(Direction.EAST),
    FACING_WEST(Direction.WEST),
    FACING_SOUTH(Direction.SOUTH);

    public final Direction associatedDirection;

    PropertyController(Direction associatedDirection) {
       this.associatedDirection = associatedDirection;
    }

}
