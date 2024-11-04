package com.chailotl.fbombs;

import com.chailotl.fbombs.data.ScorchedBlockDataLoader;
import com.chailotl.fbombs.init.*;
import com.chailotl.fbombs.network.FBombsC2SNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class FBombs implements ModInitializer {
    public static final String MOD_ID = "fbombs";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final HashMap<RegistryKey<World>, FBombsPersistentState> CACHED_PERSISTENT_STATE = new HashMap<>();

    @Override
    public void onInitialize() {
        FBombsBlocks.initialize();
        FBombsItems.initialize();
        FBombsItemGroups.initialize();
        FBombsBlockEntities.initialize();
        FBombsEntityTypes.initialize();
        FBombsC2SNetworking.initialize();
        FBombsNetworkPayloads.initialize();
        FBombsGamerules.initialize();
        FBombsItemComponents.initialize();
        FBombsCriteria.initialize();
        FBombsCommonEvents.initialize();
        FBombsSoundEvents.initialize();

        ScorchedBlockDataLoader.initialize();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            for (ServerWorld world : server.getWorlds()) {
                FBombsPersistentState state = FBombsPersistentState.fromServer(world);
                if (state != null) {
                    CACHED_PERSISTENT_STATE.put(world.getRegistryKey(), state);
                }
            }
        });

        LOGGER.info("May contain traces of nuclear explosions");
    }

    /**
     * Do not modify the PersistentState instance with this method. This method can be used for a lookup on
     * PersistentState values from the cache. If you want to modify it, always make sure to mark the
     * PersistentState instance as dirty afterward, or just use the
     * {@link #modifyCachedPersistentState(ServerWorld, Consumer) modifyCachedPersistentState} method which
     * already does that on every modification call.
     *
     * @param world PersistentState's world, since they are stored separately for each Dimension
     * @return Cached instance of FBombs' PersistentState
     */
    @Nullable
    public static FBombsPersistentState getCachedPersistentState(ServerWorld world) {
        FBombsPersistentState state = CACHED_PERSISTENT_STATE.get(world.getRegistryKey());
        if (state != null) return state;
        else {
            FBombsPersistentState newState = FBombsPersistentState.fromServer(world);
            if (newState != null) {
                return CACHED_PERSISTENT_STATE.put(world.getRegistryKey(), newState);
            }
        }
        return null;
    }

    /**
     * Delivers an instance of the specified PersistentState with a markDirty call. So if you need to modify
     * the cached PersistentState, use this method. If you just need access to the data for reading instead of
     * modification, use {@link #getCachedPersistentState(ServerWorld) getCachedPersistentState} instead, which doesn't
     * involve the markDirty call.
     *
     * @param world       PersistentState's world, since they are stored separately for each Dimension
     * @param cachedState a consumer to modify and / or add new data to the PersistentState. If the world doesn't exist
     *                    yet on the server this will not be applied and also doesn't include a markDirty call
     */
    public static void modifyCachedPersistentState(ServerWorld world, Consumer<FBombsPersistentState> cachedState) {
        Optional.ofNullable(FBombsPersistentState.fromServer(world)).ifPresent(
                cachedState
        );

        /*FBombsPersistentState state = CACHED_PERSISTENT_STATE.get(world.getRegistryKey());
        if (state != null) {
            cachedState.accept(state);
            state.markDirty();
        } else {
            FBombsPersistentState newState = FBombsPersistentState.fromServer(world);
            if (newState != null) {
                cachedState.accept(newState);
                newState.markDirty();
                CACHED_PERSISTENT_STATE.put(world.getRegistryKey(), newState);
            }
        }*/
    }

    public static Identifier getId(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public static Identifier getCommonId(String path) {
        return Identifier.of("c", path);
    }
}