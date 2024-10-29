package com.chailotl.fbombs.explosion;

import com.chailotl.fbombs.init.FBombsPersistentState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class ExplosionManager {
    private final MinecraftServer server;
    private final HashMap<ServerWorld, Queue<ExplosionScheduler>> scheduler = new HashMap<>();

    public ExplosionManager(MinecraftServer server) {
        this.server = server;
        for (ServerWorld world : server.getWorlds()) {
            if (world == null) continue;
            if (!this.scheduler.containsKey(world)) this.scheduler.put(world, new LinkedList<>());
            FBombsPersistentState.fromServer(world).map(FBombsPersistentState::getExplosions).ifPresent(explosions -> {
                if (explosions.isEmpty()) return;
                Queue<ExplosionScheduler> worldScheduler = this.scheduler.get(world);
                worldScheduler.add(new ExplosionScheduler(explosions));
                this.scheduler.put(world, worldScheduler);
            });
        }
    }

    public void onServerTick() {
        for (ServerWorld world : this.server.getWorlds()) {
            if (world == null) continue;
            double latestTickTime = this.server.getTickTimes()[0] / 1_000_000.0;
            var worldScheduler = this.scheduler.get(world);
            if (worldScheduler == null) continue;
            ExplosionScheduler currentExplosion = worldScheduler.peek();
            if (currentExplosion == null) continue;

            currentExplosion.processNextTick(this.server, latestTickTime);
            if (currentExplosion.isComplete()) worldScheduler.poll();
        }
    }
}
