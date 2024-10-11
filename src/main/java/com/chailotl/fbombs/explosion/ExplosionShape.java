package com.chailotl.fbombs.explosion;

import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public enum ExplosionShape {
    SPHERE, CYLINDER, CUBE;

    boolean isInsideVolume(int radius, @Nullable Double extrusion, Vec3d pos) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        return switch (this) {
            case SPHERE -> x * x + y * y + z * z < radius * radius;
            case CYLINDER -> {
                if (extrusion == null) yield false;
                if (y < (-extrusion) || y > extrusion) yield false;
                yield x * x + z * z < radius * radius;
            }
            case CUBE -> true;
        };
    }
}