package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.data.BlockAndEntityGroup;
import com.chailotl.fbombs.util.NbtKeys;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class FBombsPersistentState extends PersistentState {
    private final HashMap<BlockPos, Float> radiation;
    private final List<BlockAndEntityGroup> explosions;

    //region Constructor, Getter & Setter
    public FBombsPersistentState() {
        this(new HashMap<>(), new ArrayList<>());
    }

    public FBombsPersistentState(HashMap<BlockPos, Float> radiationData, List<BlockAndEntityGroup> explosions) {
        this.radiation = radiationData;
        this.explosions = explosions;
    }

    public HashMap<BlockPos, Float> getRadiation() {
        return radiation;
    }

    public List<BlockAndEntityGroup> getExplosions() {
        return explosions;
    }
    //endregion

    //region (De-)Serialisation
    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList radiationNbt = new NbtList();
        for (var entry : radiation.entrySet()) {
            NbtCompound entryNbt = new NbtCompound();
            entryNbt.putLong(NbtKeys.BLOCK_POS, entry.getKey().asLong());
            entryNbt.putFloat(NbtKeys.CPS, entry.getValue());
            radiationNbt.add(entryNbt);
        }
        nbt.put(NbtKeys.RADIATION_DATA, radiationNbt);

        BlockAndEntityGroup.toNbt(explosions, nbt);
        return nbt;
    }

    public static FBombsPersistentState fromNbt(NbtCompound nbt) {
        HashMap<BlockPos, Float> radiationMap = new HashMap<>();
        NbtList radiationNbt = nbt.getList(NbtKeys.RADIATION_DATA, NbtElement.LIST_TYPE);
        for (int i = 0; i < radiationNbt.size(); i++) {
            var entry = radiationNbt.getCompound(i);
            radiationMap.put(BlockPos.fromLong(entry.getLong(NbtKeys.BLOCK_POS)), entry.getFloat(NbtKeys.CPS));
        }

        List<BlockAndEntityGroup> explosionData = BlockAndEntityGroup.fromNbt(nbt);
        return new FBombsPersistentState(radiationMap, explosionData);
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
