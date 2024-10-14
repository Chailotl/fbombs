package com.chailotl.fbombs.data;

import com.chailotl.fbombs.util.NbtKeys;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public record RadiationData(BlockPos pos, float cps) {

    public void toNbt(NbtCompound nbt) {
        NbtCompound radiationNbt;
        if (nbt.contains(NbtKeys.RADIATION_DATA)) {
            radiationNbt = nbt.getCompound(NbtKeys.RADIATION_DATA);
        } else {
            radiationNbt = new NbtCompound();
        }

        DataResult<NbtElement> blockPosNbt = BlockPos.CODEC.encodeStart(NbtOps.INSTANCE, pos());
        radiationNbt.put(NbtKeys.BLOCK_POS, blockPosNbt.getOrThrow());
        radiationNbt.putFloat(NbtKeys.CPS, cps());
        nbt.put(NbtKeys.RADIATION_DATA, radiationNbt);
    }

    public static void toNbt(List<RadiationData> radiationDataList, NbtCompound nbt) {
        for (RadiationData entry : radiationDataList) {
            entry.toNbt(nbt);
        }
    }

    public static List<RadiationData> fromNbt(NbtCompound nbt) {
        if (!nbt.contains(NbtKeys.RADIATION_DATA)) return List.of();
        NbtCompound radiationNbt = (NbtCompound) nbt.get(NbtKeys.RADIATION_DATA);
        if (radiationNbt == null) return List.of();

        List<RadiationData> radiationDataList = new ArrayList<>();
        for (String nbtKey : radiationNbt.getKeys()) {
            NbtElement blockPosNbt = radiationNbt.get(nbtKey);
            BlockPos retrievedPos = BlockPos.CODEC.parse(NbtOps.INSTANCE, blockPosNbt).getOrThrow();
            float retrievedCps = radiationNbt.getFloat(NbtKeys.CPS);
            radiationDataList.add(new RadiationData(retrievedPos, retrievedCps));
        }
        return radiationDataList;
    }
}
