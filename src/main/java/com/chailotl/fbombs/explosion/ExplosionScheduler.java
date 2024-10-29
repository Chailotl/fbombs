package com.chailotl.fbombs.explosion;

import com.chailotl.fbombs.data.BlockAndEntityGroup;
import com.chailotl.fbombs.init.FBombsPersistentState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.*;

public class ExplosionScheduler {
    private final Queue<BlockAndEntityGroup> blockGroupQueue;
    private final ArrayDeque<Double> recentTickTimes;

    private int blocksPerTick = 100;

    public ExplosionScheduler(List<BlockAndEntityGroup> blockGroups) {
        this.blockGroupQueue = new PriorityQueue<>(blockGroups.size(), Comparator.comparingDouble(BlockAndEntityGroup::getDistanceToOrigin));
        this.blockGroupQueue.addAll(blockGroups);
        this.recentTickTimes = new ArrayDeque<>();
    }

    public void processNextTick(MinecraftServer server, double currentTime) {
        adjustBlocksPerTick(currentTime);

        int groupsProcessed = 0;
        while (!this.blockGroupQueue.isEmpty() && groupsProcessed < this.blocksPerTick) {
            BlockAndEntityGroup group = this.blockGroupQueue.poll();
            groupsProcessed += group.applyChanges(server);
            ServerWorld groupWorld = server.getWorld(group.getDimension());
            if (isComplete() && groupWorld != null) {
                FBombsPersistentState.fromServer(groupWorld).ifPresent(state -> state.getExplosions().remove(group));
            }
        }
    }

    private void adjustBlocksPerTick(double currentTime) {
        this.recentTickTimes.add(currentTime);
        if (this.recentTickTimes.size() > 20) {
            this.recentTickTimes.poll();
        }

        int minBlocksPerTick = 10;
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

    public boolean isComplete() {
        return this.blockGroupQueue.isEmpty();
    }
}
