package com.chailotl.fbombs.explosion;

import com.chailotl.fbombs.FBombs;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class ExplosionManager {
    private final MinecraftServer server;
    private final List<Schedule> explosionScheduler = new LinkedList<>();

    public ExplosionManager(MinecraftServer server) {
        this.server = server;

        for (ServerWorld world : server.getWorlds()) {
            if (world == null) continue;
            var key = world.getRegistryKey();
            FBombs.modifyCachedPersistentState(world, state -> {
                ExplosionScheduler scheduler = new ExplosionScheduler(state.getExplosions());
                this.explosionScheduler.add(new Schedule(world, scheduler));
            });
        }
    }

    public void onServerTick() {
        for (ServerWorld world : this.server.getWorlds()) {
            if (world == null) continue;
            double latestTickTime = this.server.getTickTimes()[0] / 1_000_000.0;

            Schedule worldScheduler = Schedule.getFromWorld(world, this.explosionScheduler);
            if (worldScheduler == null || worldScheduler.getScheduledExplosions() == null) continue;
            ExplosionScheduler explosionsInWorld = worldScheduler.getScheduledExplosions();

            explosionsInWorld.processNextTick(this.server, latestTickTime);
            if (explosionsInWorld.isComplete()) this.explosionScheduler.remove(worldScheduler);
        }
    }

    public static class Schedule {
        private ServerWorld world;
        private ExplosionScheduler scheduledExplosions;

        public Schedule(ServerWorld world, ExplosionScheduler scheduledExplosions) {
            this.world = world;
            this.scheduledExplosions = scheduledExplosions;
        }

        public ServerWorld getWorld() {
            return world;
        }

        public void setWorld(ServerWorld world) {
            this.world = world;
        }

        public ExplosionScheduler getScheduledExplosions() {
            return scheduledExplosions;
        }

        public void setScheduledExplosions(ExplosionScheduler scheduledExplosions) {
            this.scheduledExplosions = scheduledExplosions;
        }

        @Nullable
        public static Schedule getFromWorld(ServerWorld world, List<Schedule> schedules) {
            for (Schedule entry : schedules) {
                if (entry.getWorld().equals(world)) return entry;
            }
            return null;
        }
    }
}
