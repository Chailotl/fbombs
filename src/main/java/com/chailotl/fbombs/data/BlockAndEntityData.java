package com.chailotl.fbombs.data;

import com.chailotl.fbombs.util.Locatable;
import com.google.common.collect.Iterators;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BlockAndEntityData {
    private final List<LocatableBlock> affectedBlocks;
    private final List<LocatableBlock> scorchedBlocks;
    private final List<BlockPos> unaffectedBlocks;

    private final List<LocatableEntity<?>> affectedEntities;
    private final List<UUID> unaffectedEntities;

    private final ServerWorld world;

    public BlockAndEntityData(ServerWorld world) {
        this.affectedBlocks = new ArrayList<>();
        this.scorchedBlocks = new ArrayList<>();
        this.unaffectedBlocks = new ArrayList<>();

        this.affectedEntities = new ArrayList<>();
        this.unaffectedEntities = new ArrayList<>();

        this.world = world;
    }

    //region getter & setter
    public List<LocatableBlock> getAffectedBlocks() {
        return affectedBlocks;
    }

    public List<BlockPos> getUnaffectedBlocks() {
        return unaffectedBlocks;
    }

    public List<LocatableEntity<?>> getAffectedEntities() {
        return affectedEntities;
    }

    public List<UUID> getUnaffectedEntities() {
        return unaffectedEntities;
    }
    //endregion

    //region adder
    public void addToAffectedBlocks(LocatableBlock... locatableBlocks) {
        this.affectedBlocks.addAll(List.of(locatableBlocks));
    }

    public void addToScorchedBlocks(LocatableBlock... locatableBlocks) {
        this.scorchedBlocks.addAll(List.of(locatableBlocks));
    }

    public void addToUnaffectedBlocks(BlockPos... pos) {
        this.unaffectedBlocks.addAll(List.of(pos));
    }

    public void addToAffectedEntities(LocatableEntity<?>... locatableEntities) {
        this.affectedEntities.addAll(List.of(locatableEntities));
    }

    public void addToUnaffectedEntities(UUID... uuid) {
        this.unaffectedEntities.addAll(List.of(uuid));
    }
    //endregion

    //region iterator
    public Iterator<LocatableBlock> iterateBlocks() {
        return this.affectedBlocks.stream()
                .filter(locatableBlock -> !this.unaffectedBlocks.contains(locatableBlock.pos()))
                .iterator();
    }

    public Iterator<LocatableEntity<?>> iterateEntities() {
        return this.affectedEntities.stream()
                .filter(locatableEntity -> !this.unaffectedEntities.contains(locatableEntity.entity().getUuid()))
                .iterator();
    }

    public Iterator<Locatable> iterateAffectedTargets() {
        return Iterators.concat(this.iterateBlocks(), this.iterateEntities());
    }
    //endregion

    public void exclude(BlockPos pos, BlockPos origin) {
        Vec3d point1 = Vec3d.ofCenter(pos);
        Vec3d point1ToOrigin = Vec3d.ofCenter(origin).subtract(point1);

        if (point1ToOrigin.lengthSquared() == 0) return;
        
    }
}
