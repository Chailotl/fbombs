package com.chailotl.fbombs.network.packet;

import com.chailotl.fbombs.FBombs;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;


@SuppressWarnings("unused")
public record ExampleS2CPacket(int seed, BlockPos pos) implements CustomPayload {
    public static final CustomPayload.Id<ExampleS2CPacket> PACKET_ID =
            new CustomPayload.Id<>(FBombs.getId("example_s2c"));

    public static final PacketCodec<RegistryByteBuf, ExampleS2CPacket> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ExampleS2CPacket::seed,
            BlockPos.PACKET_CODEC, ExampleS2CPacket::pos,
            ExampleS2CPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }

    public void send(ServerPlayerEntity target) {
        ServerPlayNetworking.send(target, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        if (context.player() == null) return;
        FBombs.LOGGER.info(String.valueOf(this.seed));
        FBombs.LOGGER.info(this.pos.toShortString());

        // ...

    }
}
