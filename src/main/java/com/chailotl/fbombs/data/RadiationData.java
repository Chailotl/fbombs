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
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public record RadiationData(BlockPos pos, float cps, float radius) {

    public static final Codec<RadiationData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf(NbtKeys.BLOCK_POS).forGetter(RadiationData::pos),
            Codec.FLOAT.fieldOf(NbtKeys.CPS).forGetter(RadiationData::cps),
            Codec.FLOAT.fieldOf(NbtKeys.RADIUS).forGetter(RadiationData::radius)
    ).apply(instance, RadiationData::new));

    public static final PacketCodec<ByteBuf, RadiationData> PACKET_CODEC = PacketCodecs.codec(CODEC);

    public void toNbt(NbtCompound nbt) {
        NbtList nbtList;
        if (nbt.contains(NbtKeys.RADIATION_DATA, NbtElement.LIST_TYPE)) {
            nbtList = nbt.getList(NbtKeys.RADIATION_DATA, NbtElement.COMPOUND_TYPE);
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

    public static ArrayList<RadiationData> fromNbt(NbtCompound nbt) {
        ArrayList<RadiationData> radiationDataList = new ArrayList<>();
        if (!nbt.contains(NbtKeys.RADIATION_DATA)) return radiationDataList;

        for (NbtElement entry : nbt.getList(NbtKeys.RADIATION_DATA, NbtElement.COMPOUND_TYPE)) {
            radiationDataList.add(CODEC.parse(NbtOps.INSTANCE, entry).getPartialOrThrow());
        }
        return radiationDataList;
    }

    public double getRadiationLevel(BlockPos pos) {
        double sqDistance = this.pos.getSquaredDistance(pos);
        if (sqDistance > this.radius() * this.radius()) return RadiationCategory.SAFE.getMinCps();
        double normalizedDistance = sqDistance / (this.radius * this.radius);
        return MathHelper.lerp(normalizedDistance, 0f, this.cps);
    }

    public static float getRadiationLevel(List<RadiationData> data, BlockPos pos) {
        float level = 0;
        for (RadiationData entry : data) {
            level = (float) Math.max(level, entry.getRadiationLevel(pos));
        }
        return level;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RadiationData radiationData)) return false;
        return this.pos().equals(radiationData.pos());
    }

    @Override
    public int hashCode() {
        return this.pos().hashCode();
    }
}
