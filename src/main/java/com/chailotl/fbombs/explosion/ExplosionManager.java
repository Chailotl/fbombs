package com.chailotl.fbombs.explosion;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.data.BlockAndEntityGroup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExplosionManager {
    private static ExplosionManager instance;

    private final MinecraftServer server;
    private final HashMap<RegistryKey<World>, Schedule> explosionSchedulers = new HashMap<>();

    private ExplosionManager(MinecraftServer server) {
        this.server = server;

        for (ServerWorld world : server.getWorlds()) {
            if (world == null) continue;
            RegistryKey<World> key = world.getRegistryKey();
            FBombs.modifyCachedPersistentState(world, state -> {
                ExplosionScheduler scheduler = new ExplosionScheduler(state.getExplosions());
                this.explosionSchedulers.put(world.getRegistryKey(), new Schedule(world, scheduler));
            });
        }
    }

    public static ExplosionManager getInstance(MinecraftServer server) {
        if (instance == null) instance = new ExplosionManager(server);
        return instance;
    }

    public void onServerTick() {
        for (ServerWorld world : this.server.getWorlds()) {
            if (world == null) continue;
            double latestTickTime = this.server.getTickTimes()[0] / 1_000_000.0;

            Schedule worldScheduler = this.explosionSchedulers.get(world.getRegistryKey());
            if (worldScheduler == null || worldScheduler.getScheduledExplosions() == null) continue;
            ExplosionScheduler explosionsInWorld = worldScheduler.getScheduledExplosions();

            explosionsInWorld.processNextTick(this.server, latestTickTime);
            // if (explosionsInWorld.isComplete()) this.explosionSchedulers.remove(worldScheduler);
        }
    }

    public HashMap<RegistryKey<World>, Schedule> getExplosionSchedulers() {
        return this.explosionSchedulers;
    }

    public void addExplosion(ServerWorld world, BlockAndEntityGroup explosion) {
        Schedule schedule = explosionSchedulers.get(world.getRegistryKey());
        if (schedule == null) {
            List<BlockAndEntityGroup> newScheduleExplosionList = new ArrayList<>(List.of(explosion));
            explosionSchedulers.put(world.getRegistryKey(), new Schedule(world, new ExplosionScheduler(newScheduleExplosionList)));
        }
        if (schedule == null) return;
        schedule.getScheduledExplosions().addExplosion(explosion);
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
    }
}
