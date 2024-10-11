package com.chailotl.fbombs;

import com.chailotl.fbombs.entity.renderer.InstantTntEntityRenderer;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class FBombsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(FBombsEntityTypes.INSTANT_TNT, InstantTntEntityRenderer::new);
    }
}
