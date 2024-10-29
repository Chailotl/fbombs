package com.chailotl.fbombs.data;

import com.chailotl.fbombs.explosion.ExplosionHandler;
import com.chailotl.fbombs.util.NbtKeys;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BlockAndEntityGroup {
    private final RegistryKey<World> dimension;
    private final BlockPos origin;
    private BlockPos center;

    private final List<LocatableBlock> affectedBlocks;
    private final List<LocatableBlock> scorchedBlocks;
    private final List<BlockPos> unaffectedBlocks;

    private final List<LocatableEntity<?>> affectedEntities;
    private final List<UUID> unaffectedEntities;

    public static final Codec<BlockAndEntityGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryKey.createCodec(RegistryKeys.WORLD).fieldOf(NbtKeys.DIMENSION).forGetter(BlockAndEntityGroup::getDimension),
            BlockPos.CODEC.fieldOf(NbtKeys.BLOCK_POS + "_origin").forGetter(BlockAndEntityGroup::getOrigin),
            BlockPos.CODEC.fieldOf(NbtKeys.BLOCK_POS + "_center").forGetter(BlockAndEntityGroup::getCenter),
            LocatableBlock.CODEC.listOf().fieldOf(NbtKeys.AFFECTED_BLOCKS).forGetter(BlockAndEntityGroup::getAffectedBlocks),
            LocatableBlock.CODEC.listOf().fieldOf(NbtKeys.SCORCHED_BLOCKS).forGetter(BlockAndEntityGroup::getScorchedBlocks),
            BlockPos.CODEC.listOf().fieldOf(NbtKeys.UNAFFECTED_BLOCKS).forGetter(BlockAndEntityGroup::getUnaffectedBlocks),
            LocatableEntity.CODEC.listOf().fieldOf(NbtKeys.AFFECTED_ENTITIES).forGetter(BlockAndEntityGroup::getAffectedEntities),
            Uuids.CODEC.listOf().fieldOf(NbtKeys.UNAFFECTED_ENTITIES).forGetter(BlockAndEntityGroup::getUnaffectedEntities)
    ).apply(instance, BlockAndEntityGroup::new));


    public BlockAndEntityGroup(RegistryKey<World> dimension, BlockPos origin, BlockPos center, List<LocatableBlock> affectedBlocks,
                               List<LocatableBlock> scorchedBlocks, List<BlockPos> unaffectedBlocks,
                               List<LocatableEntity<?>> affectedEntities, List<UUID> unaffectedEntities) {
        this.dimension = dimension;
        this.origin = origin;
        this.center = center;

        this.affectedBlocks = Collections.synchronizedList(affectedBlocks);
        this.scorchedBlocks = Collections.synchronizedList(scorchedBlocks);
        this.unaffectedBlocks = Collections.synchronizedList(unaffectedBlocks);

        this.affectedEntities = Collections.synchronizedList(affectedEntities);
        this.unaffectedEntities = Collections.synchronizedList(unaffectedEntities);
    }

    public BlockAndEntityGroup(RegistryKey<World> dimension, BlockPos origin) {
        this(dimension, origin, BlockPos.ORIGIN, Collections.synchronizedList(new ArrayList<>()), Collections.synchronizedList(new ArrayList<>()),
                Collections.synchronizedList(new ArrayList<>()), Collections.synchronizedList(new ArrayList<>()),
                Collections.synchronizedList(new ArrayList<>()));
    }

    //region getter & setter
    public RegistryKey<World> getDimension() {
        return dimension;
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public BlockPos getCenter() {
        return center;
    }

    public void setCenter(BlockPos center) {
        this.center = center;
    }

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

    public double getDistanceToOrigin() {
        return this.center.getManhattanDistance(this.origin);
    }


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

    public static void toNbt(List<BlockAndEntityGroup> explosionData, NbtCompound nbt) {
        for (BlockAndEntityGroup entry : explosionData) {
            entry.toNbt(nbt);
        }
    }

    public static List<BlockAndEntityGroup> fromNbt(NbtCompound nbt) {
        if (!nbt.contains(NbtKeys.EXPLOSION_DATA)) return List.of();
        List<BlockAndEntityGroup> explosionDataList = Collections.synchronizedList(new ArrayList<>());
        for (NbtElement entry : nbt.getList(NbtKeys.EXPLOSION_DATA, NbtElement.LIST_TYPE)) {
            explosionDataList.add(CODEC.parse(NbtOps.INSTANCE, entry).getPartialOrThrow());
        }
        return explosionDataList;
    }

    public int applyChanges(MinecraftServer server) {
        return ExplosionHandler.handleExplosion(server.getWorld(this.dimension), this);
    }
}
