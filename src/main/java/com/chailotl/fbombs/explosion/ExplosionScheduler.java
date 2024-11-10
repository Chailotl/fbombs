package com.chailotl.fbombs.explosion;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.data.BlockAndEntityGroup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ExplosionScheduler {
    private final Queue<BlockAndEntityGroup> explosions;
    private final ArrayDeque<Double> recentTickTimes;

    private int blocksPerTick = 90;

    public ExplosionScheduler(List<BlockAndEntityGroup> blockGroups) {
        this.explosions = new LinkedList<>(blockGroups);
        this.recentTickTimes = new ArrayDeque<>();
    }

    public Queue<BlockAndEntityGroup> getExplosions() {
        return explosions;
    }

    public void processNextTick(MinecraftServer server, double currentTime) {
        adjustBlocksPerTick(currentTime);

        int blocksProcessed = 0;
        while (!this.explosions.isEmpty() && blocksProcessed < this.blocksPerTick) {
            BlockAndEntityGroup explosionPartition = this.explosions.peek();


            blocksProcessed += explosionPartition.applyChanges(server, blocksPerTick);
            ServerWorld groupWorld = server.getWorld(explosionPartition.getDimension());
            if (explosionPartition.isComplete() && groupWorld != null) {
                FBombs.modifyCachedPersistentState(groupWorld, state -> state.getExplosions().remove(explosionPartition));
                this.explosions.poll();
            }
        }
    }

    private void adjustBlocksPerTick(double currentTime) {
        this.recentTickTimes.add(currentTime);
        if (this.recentTickTimes.size() > 20) {
            this.recentTickTimes.poll();
        }

        int minBlocksPerTick = 2;
        int maxBlocksPerTick = 200;
        int adjustmentFactor = 5;
        double tickTimeThreshold = 50.0;        // Threshold in milliseconds
        double averageTickTime = recentTickTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0);

        if (averageTickTime > tickTimeThreshold && blocksPerTick > minBlocksPerTick) {
            this.blocksPerTick = Math.max(minBlocksPerTick, blocksPerTick - adjustmentFactor);
        } else if (averageTickTime < tickTimeThreshold && blocksPerTick < maxBlocksPerTick) {
            this.blocksPerTick = Math.min(maxBlocksPerTick, blocksPerTick + adjustmentFactor);
        }
    }

    public void addExplosion(BlockAndEntityGroup explosion) {
        this.explosions.add(explosion);
    }

    public boolean isComplete() {
        return this.explosions.isEmpty();
    }
}
