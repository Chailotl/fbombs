package com.chailotl.fbombs.data;

import com.chailotl.fbombs.util.NbtKeys;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class BlockAndEntityData {
    private final List<LocatableBlock> affectedBlocks;
    private final List<LocatableBlock> scorchedBlocks;
    private final List<BlockPos> unaffectedBlocks;

    private final List<LocatableEntity<?>> affectedEntities;
    private final List<UUID> unaffectedEntities;


    public static final Codec<BlockAndEntityData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LocatableBlock.CODEC.listOf().fieldOf(NbtKeys.AFFECTED_BLOCKS).forGetter(BlockAndEntityData::getAffectedBlocks),
            LocatableBlock.CODEC.listOf().fieldOf(NbtKeys.SCORCHED_BLOCKS).forGetter(BlockAndEntityData::getScorchedBlocks),
            BlockPos.CODEC.listOf().fieldOf(NbtKeys.UNAFFECTED_BLOCKS).forGetter(BlockAndEntityData::getUnaffectedBlocks),
            LocatableEntity.CODEC.listOf().fieldOf(NbtKeys.AFFECTED_ENTITIES).forGetter(BlockAndEntityData::getAffectedEntities),
            Uuids.CODEC.listOf().fieldOf(NbtKeys.UNAFFECTED_ENTITIES).forGetter(BlockAndEntityData::getUnaffectedEntities)
    ).apply(instance, BlockAndEntityData::new));


    public BlockAndEntityData(List<LocatableBlock> affectedBlocks, List<LocatableBlock> scorchedBlocks, List<BlockPos> unaffectedBlocks,
                              List<LocatableEntity<?>> affectedEntities, List<UUID> unaffectedEntities) {
        this.affectedBlocks = Collections.synchronizedList(affectedBlocks);
        this.scorchedBlocks = Collections.synchronizedList(scorchedBlocks);
        this.unaffectedBlocks = Collections.synchronizedList(unaffectedBlocks);

        this.affectedEntities = Collections.synchronizedList(affectedEntities);
        this.unaffectedEntities = Collections.synchronizedList(unaffectedEntities);
    }

    public BlockAndEntityData() {
        this(Collections.synchronizedList(new ArrayList<>()), Collections.synchronizedList(new ArrayList<>()),
                Collections.synchronizedList(new ArrayList<>()), Collections.synchronizedList(new ArrayList<>()),
                Collections.synchronizedList(new ArrayList<>()));
    }

    //region getter & setter
    public List<LocatableBlock> getAffectedBlocks() {
        return affectedBlocks;
    }

    public List<BlockPos> getUnaffectedBlocks() {
        return unaffectedBlocks;
    }

    public List<LocatableBlock> getScorchedBlocks() {
        return scorchedBlocks;
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
                .filter(locatableEntity -> !this.unaffectedEntities.contains(locatableEntity.uuid()))
                .iterator();
    }
    //endregion

    public void toNbt(NbtCompound nbt) {
        NbtList nbtList;
        if (nbt.contains(NbtKeys.EXPLOSION_DATA)) {
            nbtList = nbt.getList(NbtKeys.EXPLOSION_DATA, NbtElement.LIST_TYPE);
        } else {
            nbtList = new NbtList();
        }
        nbtList.add(CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow());
        nbt.put(NbtKeys.EXPLOSION_DATA, nbtList);
    }

    public static void toNbt(List<BlockAndEntityData> explosionData, NbtCompound nbt) {
        for (BlockAndEntityData entry : explosionData) {
            entry.toNbt(nbt);
        }
    }

    public static List<BlockAndEntityData> fromNbt(NbtCompound nbt) {
        if (!nbt.contains(NbtKeys.EXPLOSION_DATA)) return List.of();
        List<BlockAndEntityData> explosionDataList = Collections.synchronizedList(new ArrayList<>());
        for (NbtElement entry : nbt.getList(NbtKeys.EXPLOSION_DATA, NbtElement.LIST_TYPE)) {
            explosionDataList.add(CODEC.parse(NbtOps.INSTANCE, entry).getPartialOrThrow());
        }
        return explosionDataList;
    }
}
