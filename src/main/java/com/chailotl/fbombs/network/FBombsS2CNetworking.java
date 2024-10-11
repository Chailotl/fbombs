package com.chailotl.fbombs.network;


import com.chailotl.fbombs.network.packet.ExampleS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FBombsS2CNetworking {
    static {
        ClientPlayNetworking.registerGlobalReceiver(ExampleS2CPacket.PACKET_ID, ExampleS2CPacket::handlePacket);
    }

    public static void initialize() {
        // static initialisation
    }
}
