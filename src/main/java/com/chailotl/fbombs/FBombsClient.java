package com.chailotl.fbombs;

import com.chailotl.fbombs.block.GunpowderTrailBlock;
import com.chailotl.fbombs.entity.renderer.DynamiteStickEntityRenderer;
import com.chailotl.fbombs.entity.renderer.GenericTntEntityRenderer;
import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsClientEvents;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import com.chailotl.fbombs.network.FBombsS2CNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

public class FBombsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FBombsS2CNetworking.initialize();
        FBombsClientEvents.initialize();

        FBombsEntityTypes.streamTntEntityTypes().forEach(entityType -> EntityRendererRegistry.register(entityType, GenericTntEntityRenderer::new));
        EntityRendererRegistry.register(FBombsEntityTypes.DYNAMITE_STICK, DynamiteStickEntityRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(FBombsBlocks.GUNPOWDER_TRAIL, RenderLayer.getCutout());
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> 0x494949, FBombsBlocks.GUNPOWDER_TRAIL);
    }
}
