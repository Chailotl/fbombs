package com.chailotl.fbombs.init;

import com.chailotl.fbombs.command.ContaminationCommands;
import com.chailotl.fbombs.explosion.ExplosionScheduler;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class FBombsCommonEvents {
    static {
        CommandRegistrationCallback.EVENT.register(ContaminationCommands::register);
        ServerTickEvents.END_SERVER_TICK.register(ExplosionScheduler::tick);
        // ServerTickEvents.END_SERVER_TICK.register(FBombsPersistentState::tickContamination);    <- for contamination decrease
    }


    public static void initialize() {
        // static initialisation
    }
}
