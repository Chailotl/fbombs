package com.chailotl.fbombs.init;

import com.chailotl.fbombs.explosion.ExplosionManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class FBombsCommonEvents {
    private static ExplosionManager explosionManager;

    static {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (explosionManager == null) explosionManager = new ExplosionManager(server);
            explosionManager.onServerTick();
        });
    }


    public static void initialize() {
        // static initialisation
    }
}
