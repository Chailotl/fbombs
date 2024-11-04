package com.chailotl.fbombs.data;

import com.chailotl.fbombs.util.NbtKeys;
import com.google.common.primitives.Doubles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public record LocatableEntity<T extends Entity>(UUID uuid,
                                                double sqDistanceToOrigin) implements Comparable<LocatableEntity<?>> {
    public static final Codec<LocatableEntity<?>> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Uuids.CODEC.fieldOf(NbtKeys.UUID).forGetter(LocatableEntity::uuid),
            Codec.DOUBLE.fieldOf(NbtKeys.DISTANCE_TO_ORIGIN).forGetter(LocatableEntity::sqDistanceToOrigin)
    ).apply(instance, LocatableEntity::new));

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

    @Override
    public int compareTo(@NotNull LocatableEntity<?> external) {
        return Doubles.compare(this.sqDistanceToOrigin, external.sqDistanceToOrigin);
    }
}
