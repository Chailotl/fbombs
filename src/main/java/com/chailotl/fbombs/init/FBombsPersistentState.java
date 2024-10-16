package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.data.RadiationData;
import com.chailotl.fbombs.util.NbtKeys;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FBombsPersistentState extends PersistentState {
    private final List<RadiationData> radiation;

    //region Constructor
    public FBombsPersistentState() {
        this.radiation = new ArrayList<>();
    }

    public FBombsPersistentState(List<RadiationData> radiationData) {
        this.radiation = radiationData;
    }
    //endregion

    //region (De-)Serialisation
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        RadiationData.toNbt(radiation, nbt);
        return nbt;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static FBombsPersistentState fromNbt(NbtCompound nbt) {
        NbtCompound radiationNbt = nbt.getCompound(NbtKeys.RADIATION_DATA);
        List<RadiationData> radiationData = RadiationData.fromNbt(radiationNbt);

        FBombsPersistentState state = new FBombsPersistentState(radiationData);
        return state;
    }


    public static Optional<FBombsPersistentState> fromServer(MinecraftServer server, RegistryKey<World> world) {
        ServerWorld serverWorld = server.getWorld(world);
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
