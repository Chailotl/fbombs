package com.chailotl.fbombs.init;

import com.chailotl.fbombs.explosion.ExplosionScheduler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class FBombsCommonEvents {
    static {
        ServerTickEvents.END_SERVER_TICK.register(ExplosionScheduler::tick);
    }


    public static void initialize() {
        // static initialisation
    }
}
