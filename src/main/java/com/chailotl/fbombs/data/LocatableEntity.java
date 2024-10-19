package com.chailotl.fbombs.data;

import com.mojang.serialization.Codec;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public record LocatableEntity<T extends Entity>(UUID uuid) {
    public static final Codec<LocatableEntity<?>> CODEC = Uuids.CODEC.xmap(LocatableEntity::new, LocatableEntity::uuid);

    public Vec3d getPos(ServerWorld world) {
        return get(world).getPos();
    }

    @SuppressWarnings("unchecked")
    public T get(ServerWorld world) {
        return (T) world.getEntity(uuid());
    }

    // for hashmap key equality checks
    @Override
    public int hashCode() {
        return this.uuid().hashCode();
    }

    // for hashmap key equality checks
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LocatableEntity<?> locatableEntity)) return false;
        return locatableEntity.uuid().equals(this.uuid());
    }
}
