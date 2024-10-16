package com.chailotl.fbombs.data;

import com.chailotl.fbombs.util.Locatable;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public record LocatableEntity<T extends Entity>(T entity) implements Locatable {
    @Override
    public Vec3d getPos() {
        return this.entity().getPos();
    }

    // for hashmap key equality checks
    @Override
    public int hashCode() {
        return this.entity().hashCode();
    }

    // for hashmap key equality checks
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LocatableEntity<?> locatableEntity)) return false;
        return locatableEntity.entity().equals(this.entity());
    }
}
