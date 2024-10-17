package com.chailotl.fbombs.data;

import com.chailotl.fbombs.util.NbtKeys;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public record RadiationData(BlockPos pos, float cps) {
    public static final float SAFE_CPS_LEVEL = 0.5f;

    public static final Codec<RadiationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf(NbtKeys.BLOCK_POS).forGetter(RadiationData::pos),
            Codec.FLOAT.fieldOf(NbtKeys.CPS).forGetter(RadiationData::cps)
    ).apply(instance, RadiationData::new));

    public static final PacketCodec<ByteBuf, RadiationData> PACKET_CODEC = PacketCodecs.codec(CODEC);


    public void toNbt(NbtCompound nbt) {
        NbtList nbtList;
        if (nbt.contains(NbtKeys.RADIATION_DATA)) {
            nbtList = nbt.getList(NbtKeys.RADIATION_DATA, NbtElement.LIST_TYPE);
        } else {
            nbtList = new NbtList();
        }
        nbtList.add(CODEC.encodeStart(NbtOps.INSTANCE, this).getOrThrow());
        nbt.put(NbtKeys.RADIATION_DATA, nbtList);
    }

    public static void toNbt(List<RadiationData> radiationDataList, NbtCompound nbt) {
        for (RadiationData entry : radiationDataList) {
            entry.toNbt(nbt);
        }
    }

    public static List<RadiationData> fromNbt(NbtCompound nbt) {
        if (!nbt.contains(NbtKeys.RADIATION_DATA)) return List.of();
        List<RadiationData> radiationDataList = new ArrayList<>();
        for (NbtElement entry : nbt.getList(NbtKeys.RADIATION_DATA, NbtElement.LIST_TYPE)) {
            radiationDataList.add(CODEC.parse(NbtOps.INSTANCE, entry).getPartialOrThrow());
        }
        return radiationDataList;
    }
}
