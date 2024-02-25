package com.goby56.buildershammer;

import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;

import java.util.Arrays;

public enum ChangeableProperties {
    NORTH(Properties.NORTH, PropertyController.FACING_NORTH),
    EAST(Properties.EAST, PropertyController.FACING_EAST),
    WEST(Properties.WEST, PropertyController.FACING_WEST),
    SOUTH(Properties.SOUTH, PropertyController.FACING_SOUTH),
    IN_WALL(Properties.IN_WALL, PropertyController.SNEAKING),
    UP(Properties.UP, PropertyController.SNEAKING),
    NORTH_WALL_SHAPE(Properties.NORTH_WALL_SHAPE, PropertyController.FACING_NORTH),
    EAST_WALL_SHAPE(Properties.EAST_WALL_SHAPE, PropertyController.FACING_EAST),
    WEST_WALL_SHAPE(Properties.WEST_WALL_SHAPE, PropertyController.FACING_WEST),
    SOUTH_WALL_SHAPE(Properties.SOUTH_WALL_SHAPE, PropertyController.FACING_SOUTH),
    EXTENDED(Properties.EXTENDED, PropertyController.SNEAKING),
    ROTATION(Properties.ROTATION, PropertyController.SNEAKING);
    // STAIR_SHAPE(Properties.STAIR_SHAPE);

    public final Property<?> property;
    public final PropertyController controller;

    ChangeableProperties(Property<?> property, PropertyController controller) {
        this.property = property;
        this.controller = controller;
    }

    public static ChangeableProperties fromProperty(Property<?> property) {
        for (ChangeableProperties p : ChangeableProperties.values()) {
            if (p.property == property) {
                return p;
            }
        }
        return null;
    }
}
