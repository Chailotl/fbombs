package com.chailotl.fbombs;

import com.chailotl.fbombs.entity.renderer.DynamiteStickEntityRenderer;
import com.chailotl.fbombs.entity.renderer.AbstractTntEntityRenderer;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import com.chailotl.fbombs.network.FBombsS2CNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class FBombsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FBombsS2CNetworking.initialize();

        EntityRendererRegistry.register(FBombsEntityTypes.INSTANT_TNT, AbstractTntEntityRenderer::new);
        EntityRendererRegistry.register(FBombsEntityTypes.SHORT_FUSE_TNT, AbstractTntEntityRenderer::new);
        EntityRendererRegistry.register(FBombsEntityTypes.DYNAMITE_STICK, DynamiteStickEntityRenderer::new);
    }
}
