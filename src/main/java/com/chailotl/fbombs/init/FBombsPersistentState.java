package com.chailotl.fbombs.init;

import com.chailotl.fbombs.data.RadiationData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.PersistentState;

import java.util.ArrayList;
import java.util.List;

public class FBombsPersistentState extends PersistentState {
    private final List<RadiationData> radiation = new ArrayList<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        RadiationData.toNbt(radiation, nbt);
        return nbt;
    }
}
