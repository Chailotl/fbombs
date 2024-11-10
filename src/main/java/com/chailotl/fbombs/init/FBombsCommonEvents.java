package com.chailotl.fbombs.init;

import com.chailotl.fbombs.command.ContaminationCommands;
import com.chailotl.fbombs.command.VolumetricExplosionCommands;
import com.chailotl.fbombs.explosion.ExplosionManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class FBombsCommonEvents {
    static {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            ContaminationCommands.register(dispatcher, registryAccess, environment);
            VolumetricExplosionCommands.register(dispatcher, registryAccess, environment);
        });
        // ServerTickEvents.END_SERVER_TICK.register(FBombsPersistentState::tickContamination);    <- for contamination decrease
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            ExplosionManager.getInstance(server).onServerTick();
        });
    }


    public static void initialize() {
        // static initialisation
    }
}
