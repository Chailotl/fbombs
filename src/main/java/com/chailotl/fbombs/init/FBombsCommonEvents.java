package com.chailotl.fbombs.init;

import com.chailotl.fbombs.command.ContaminationCommands;
import com.chailotl.fbombs.explosion.ExplosionManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class FBombsCommonEvents {
    private static ExplosionManager explosionManager;

    static {
        CommandRegistrationCallback.EVENT.register(ContaminationCommands::register);
        // ServerTickEvents.END_SERVER_TICK.register(FBombsPersistentState::tickContamination);    <- for contamination decrease
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (explosionManager == null) explosionManager = new ExplosionManager(server);
            explosionManager.onServerTick();
        });
    }


    public static void initialize() {
        // static initialisation
    }
}
