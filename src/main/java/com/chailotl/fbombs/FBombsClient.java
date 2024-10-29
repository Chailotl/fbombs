package com.chailotl.fbombs;

import com.chailotl.fbombs.entity.renderer.AcmeBedBlockEntityRenderer;
import com.chailotl.fbombs.entity.renderer.DynamiteEntityRenderer;
import com.chailotl.fbombs.entity.renderer.GenericTntEntityRenderer;
import com.chailotl.fbombs.init.*;
import com.chailotl.fbombs.network.FBombsS2CNetworking;
import com.chailotl.fbombs.particles.FastFlameParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class FBombsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        FBombsS2CNetworking.initialize();
        FBombsClientEvents.initialize();

        ParticleFactoryRegistry.getInstance().register(FBombsParticleTypes.FAST_FLAME, FastFlameParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(FBombsParticleTypes.FAST_SMALL_FLAME, FastFlameParticle.SmallFactory::new);

        FBombsEntityTypes.streamTntEntityTypes().forEach(entityType -> EntityRendererRegistry.register(entityType, GenericTntEntityRenderer::new));
        EntityRendererRegistry.register(FBombsEntityTypes.DYNAMITE, DynamiteEntityRenderer::new);
        BlockEntityRendererFactories.register(FBombsBlockEntities.ACME_BED, AcmeBedBlockEntityRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlock(FBombsBlocks.GUNPOWDER_TRAIL, RenderLayer.getCutout());
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> 0x494949, FBombsBlocks.GUNPOWDER_TRAIL);
    }
}
