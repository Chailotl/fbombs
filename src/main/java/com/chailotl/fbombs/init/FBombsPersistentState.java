package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.data.BlockAndEntityGroup;
import com.chailotl.fbombs.data.RadiationData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FBombsPersistentState extends PersistentState {
    private final List<RadiationData> radiationSource;
    private final List<BlockAndEntityGroup> explosions;

    //region Constructor, Accessor & Mutator
    public FBombsPersistentState() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public FBombsPersistentState(List<RadiationData> radiationData, List<BlockAndEntityGroup> explosions) {
        this.radiationSource = radiationData;
        this.explosions = explosions;
    }

    /**
     * You can safely access this data. But keep in mind that modifying it may cause issues with saving the PersistentState properly.
     * To modify data, don't access it directly, but make sure to use
     * {@link FBombs#modifyCachedPersistentState(ServerWorld, Consumer) modifyCachedPersistentState}
     *
     * @return stored RadiationData from the specific world's PersistentState instance
     */
    @Deprecated()
    public List<RadiationData> getRadiationSources() {
        return radiationSource;
    }

    /**
     * You can safely access this data. But keep in mind that modifying it may cause issues with saving the PersistentState properly.
     * To modify data, don't access it directly, but make sure to use
     * {@link FBombs#modifyCachedPersistentState(ServerWorld, Consumer) modifyCachedPersistentState}
     *
     * @return stored ExplosionData from the specific world's PersistentState instance
     */
    @Deprecated
    public List<BlockAndEntityGroup> getExplosions() {
        return explosions;
    }
    //endregion

    //region (De-)Serialisation
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        RadiationData.toNbt(this.getRadiationSources(), nbt);
        BlockAndEntityGroup.toNbt(explosions, nbt);
        return nbt;
    }

    public static FBombsPersistentState fromNbt(NbtCompound nbt) {
        List<RadiationData> radiationSourcesData = RadiationData.fromNbt(nbt);
        List<BlockAndEntityGroup> explosionData = BlockAndEntityGroup.fromNbt(nbt);
        return new FBombsPersistentState(radiationSourcesData, explosionData);
    }

    @Nullable
    public static FBombsPersistentState fromServer(ServerWorld world) {
        ServerWorld serverWorld = world.getServer().getWorld(world.getRegistryKey());
        if (serverWorld == null) return null;
        PersistentStateManager manager = serverWorld.getPersistentStateManager();
        FBombsPersistentState state = manager.getOrCreate(type, FBombs.MOD_ID);
        state.markDirty();
        return state;
    }

    private static final Type<FBombsPersistentState> type = new Type<>(
            FBombsPersistentState::new, // If there's no 'StateSaverAndLoader' yet create one
            (nbtCompound, wrapperLookup) ->  FBombsPersistentState.fromNbt(nbtCompound), // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );
}
