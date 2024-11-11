package com.chailotl.fbombs.data;

import com.chailotl.fbombs.explosion.ExplosionHandler;
import com.chailotl.fbombs.util.NbtKeys;
import com.google.common.collect.Queues;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

public class BlockAndEntityGroup {
    private final RegistryKey<World> dimension;
    private final BlockPos origin;

    private final PriorityBlockingQueue<LocatableBlock> affectedBlocks;
    private final PriorityBlockingQueue<LocatableBlock> scorchedBlocks;
    private final PriorityBlockingQueue<LocatableBlock> unaffectedBlocks;

    private final PriorityBlockingQueue<LocatableEntity<?>> affectedEntities;
    private final PriorityBlockingQueue<LocatableEntity<?>> unaffectedEntities;

    public static final Codec<BlockAndEntityGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryKey.createCodec(RegistryKeys.WORLD).fieldOf(NbtKeys.DIMENSION).forGetter(BlockAndEntityGroup::getDimension),
            BlockPos.CODEC.fieldOf(NbtKeys.BLOCK_POS).forGetter(BlockAndEntityGroup::getOrigin),

            LocatableBlock.CODEC.listOf().fieldOf(NbtKeys.AFFECTED_BLOCKS).forGetter(group -> new LinkedList<>(group.affectedBlocks)),
            LocatableBlock.CODEC.listOf().fieldOf(NbtKeys.SCORCHED_BLOCKS).forGetter(group -> new LinkedList<>(group.scorchedBlocks)),
            LocatableBlock.CODEC.listOf().fieldOf(NbtKeys.UNAFFECTED_BLOCKS).forGetter(group -> new LinkedList<>(group.unaffectedBlocks)),

            LocatableEntity.CODEC.listOf().fieldOf(NbtKeys.AFFECTED_ENTITIES).forGetter(group -> new LinkedList<>(group.affectedEntities)),
            LocatableEntity.CODEC.listOf().fieldOf(NbtKeys.UNAFFECTED_ENTITIES).forGetter(group -> new LinkedList<>(group.unaffectedEntities))
    ).apply(instance, BlockAndEntityGroup::fromCodec));

    public BlockAndEntityGroup(RegistryKey<World> dimension, BlockPos origin,
                               PriorityBlockingQueue<LocatableBlock> affectedBlocks,
                               PriorityBlockingQueue<LocatableBlock> scorchedBlocks,
                               PriorityBlockingQueue<LocatableBlock> unaffectedBlocks,
                               PriorityBlockingQueue<LocatableEntity<?>> affectedEntities,
                               PriorityBlockingQueue<LocatableEntity<?>> unaffectedEntities) {
        this.dimension = dimension;
        this.origin = origin;

        this.affectedBlocks = affectedBlocks;
        this.scorchedBlocks = scorchedBlocks;
        this.unaffectedBlocks = unaffectedBlocks;

        this.affectedEntities = affectedEntities;
        this.unaffectedEntities = unaffectedEntities;
    }

    public BlockAndEntityGroup(RegistryKey<World> dimension, BlockPos origin) {
        this(dimension, origin, Queues.newPriorityBlockingQueue(), Queues.newPriorityBlockingQueue(),
                Queues.newPriorityBlockingQueue(), Queues.newPriorityBlockingQueue(),
                Queues.newPriorityBlockingQueue());
    }

    private static BlockAndEntityGroup fromCodec(RegistryKey<World> worldRegistryKey, BlockPos pos,
                                                 List<LocatableBlock> locatableBlocks,
                                                 List<LocatableBlock> locatableBlocks1,
                                                 List<LocatableBlock> locatableBlocks2,
                                                 List<LocatableEntity<?>> locatableEntities,
                                                 List<LocatableEntity<?>> locatableEntities1) {

        return new BlockAndEntityGroup(worldRegistryKey, pos,
                locatableBlocks.isEmpty() ? Queues.newPriorityBlockingQueue()
                        : new PriorityBlockingQueue<>(locatableBlocks.size(), Comparator.comparingDouble(LocatableBlock::sqDistanceToOrigin)),
                locatableBlocks1.isEmpty() ? Queues.newPriorityBlockingQueue()
                        : new PriorityBlockingQueue<>(locatableBlocks1.size(), Comparator.comparingDouble(LocatableBlock::sqDistanceToOrigin)),
                locatableBlocks2.isEmpty() ? Queues.newPriorityBlockingQueue()
                        : new PriorityBlockingQueue<>(locatableBlocks2.size(), Comparator.comparingDouble(LocatableBlock::sqDistanceToOrigin)),
                locatableEntities.isEmpty() ? Queues.newPriorityBlockingQueue()
                        : new PriorityBlockingQueue<>(locatableEntities.size(), Comparator.comparingDouble(LocatableEntity::sqDistanceToOrigin)),
                locatableEntities1.isEmpty() ? Queues.newPriorityBlockingQueue()
                        : new PriorityBlockingQueue<>(locatableEntities1.size(), Comparator.comparingDouble(LocatableEntity::sqDistanceToOrigin))
        );
    }

    //region getter & setter
    public RegistryKey<World> getDimension() {
        return dimension;
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public PriorityBlockingQueue<LocatableBlock> getAffectedBlocks() {
        return affectedBlocks;
    }

    public PriorityBlockingQueue<LocatableBlock> getScorchedBlocks() {
        return scorchedBlocks;
    }

    public PriorityBlockingQueue<LocatableBlock> getUnaffectedBlocks() {
        return unaffectedBlocks;
    }

    public PriorityBlockingQueue<LocatableEntity<?>> getAffectedEntities() {
        return affectedEntities;
    }

    public PriorityBlockingQueue<LocatableEntity<?>> getUnaffectedEntities() {
        return unaffectedEntities;
    }

    public int getCollectedBlocksSize() {
        return this.affectedBlocks.size() + this.scorchedBlocks.size() + this.unaffectedBlocks.size();
    }

    public int getCollectedEntitiesSize() {
        return this.affectedEntities.size() + this.unaffectedEntities.size();
    }
    //endregion


    public void toNbt(NbtCompound nbt, int index) {
        NbtCompound listEntryNbt = nbt.contains(NbtKeys.EXPLOSION_DATA) ? nbt.getCompound(NbtKeys.EXPLOSION_DATA) : new NbtCompound();
        NbtElement data = CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow();
        listEntryNbt.put(String.valueOf(index), data);
        nbt.put(NbtKeys.EXPLOSION_DATA, listEntryNbt);
    }

    public static void toNbt(List<BlockAndEntityGroup> explosions, NbtCompound nbt) {
        for (int i = 0; i < explosions.size(); i++) {
            explosions.get(i).toNbt(nbt, i);
        }
    }

    public static List<BlockAndEntityGroup> fromNbt(NbtCompound nbt) {
        if (!nbt.contains(NbtKeys.EXPLOSION_DATA)) return new ArrayList<>();
        List<BlockAndEntityGroup> explosions = new ArrayList<>();
        for (String index : nbt.getCompound(NbtKeys.EXPLOSION_DATA).getKeys()) {
            NbtCompound explosionNbt = nbt.getCompound(NbtKeys.EXPLOSION_DATA).getCompound(index);
            explosions.add(CODEC.parse(NbtOps.INSTANCE, explosionNbt).getPartialOrThrow());
        }
        return explosions;
    }

    public int applyChanges(MinecraftServer server, int blocksPerTick) {
        return ExplosionHandler.handleExplosion(server.getWorld(this.dimension), this, blocksPerTick);
    }

    public boolean isComplete() {
        return getAffectedBlocks().isEmpty() && getScorchedBlocks().isEmpty() && getUnaffectedBlocks().isEmpty()
                && getAffectedEntities().isEmpty() && getUnaffectedEntities().isEmpty();
    }
}
