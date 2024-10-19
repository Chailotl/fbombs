package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.data.BlockAndEntityData;
import com.chailotl.fbombs.data.RadiationData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FBombsPersistentState extends PersistentState {
    private final List<RadiationData> radiation;
    private final List<BlockAndEntityData> explosions;

    //region Constructor, Getter & Setter
    public FBombsPersistentState() {
        this(new ArrayList<>(), new ArrayList<>());
    }

    public FBombsPersistentState(List<RadiationData> radiationData, List<BlockAndEntityData> explosions) {
        this.radiation = radiationData;
        this.explosions = explosions;
    }

    public List<RadiationData> getRadiation() {
        return radiation;
    }

    public List<BlockAndEntityData> getExplosions() {
        return explosions;
    }
    //endregion

    //region (De-)Serialisation
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        RadiationData.toNbt(radiation, nbt);
        BlockAndEntityData.toNbt(explosions, nbt);
        return nbt;
    }

    public static FBombsPersistentState fromNbt(NbtCompound nbt) {
        List<RadiationData> radiationData = RadiationData.fromNbt(nbt);
        List<BlockAndEntityData> explosionData = BlockAndEntityData.fromNbt(nbt);
        return new FBombsPersistentState(radiationData, explosionData);
    }

    public static Optional<FBombsPersistentState> fromServer(ServerWorld world) {
        ServerWorld serverWorld = world.getServer().getWorld(world.getRegistryKey());
        if (serverWorld == null) return Optional.empty();
        PersistentStateManager manager = serverWorld.getPersistentStateManager();
        FBombsPersistentState state = manager.getOrCreate(getType(), FBombs.MOD_ID);
        state.markDirty();
        return Optional.of(state);
    }

    public static PersistentState.Type<FBombsPersistentState> getType() {
        return new PersistentState.Type<>(FBombsPersistentState::new, (nbt, registryLookup) -> fromNbt(nbt), null);
    }
}
