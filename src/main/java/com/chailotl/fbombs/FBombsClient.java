package com.chailotl.fbombs;

import com.chailotl.fbombs.block.entity.renderer.AcmeBedBlockEntityRenderer;
import com.chailotl.fbombs.block.entity.renderer.SirenBlockEntityRenderer;
import com.chailotl.fbombs.entity.renderer.DynamiteEntityRenderer;
import com.chailotl.fbombs.entity.renderer.GenericTntEntityRenderer;
import com.chailotl.fbombs.init.*;
import com.chailotl.fbombs.network.FBombsS2CNetworking;
import com.chailotl.fbombs.particles.MushroomCloudEmitterParticle;
import com.chailotl.fbombs.particles.MushroomCloudSmokeParticle;
import com.chailotl.fbombs.particles.GroundSmokeParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;

public class FBombsClient implements ClientModInitializer {
    public static final EntityModelLayer SIREN_HEAD_BLOCK_ENTITY_LAYER =
            new EntityModelLayer(FBombs.getId("siren_head_block_entity_layer"), "main");

    @Override
    public void onInitializeClient() {
        FBombsS2CNetworking.initialize();
        FBombsClientEvents.initialize();

        ParticleFactoryRegistry.getInstance().register(FBombsParticleTypes.MUSHROOM_CLOUD_EMITTER, MushroomCloudEmitterParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(FBombsParticleTypes.MUSHROOM_CLOUD_SMOKE, MushroomCloudSmokeParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(FBombsParticleTypes.GROUND_SMOKE, GroundSmokeParticle.Factory::new);

        FBombsEntityTypes.streamTntEntityTypes().forEach(entityType -> EntityRendererRegistry.register(entityType, GenericTntEntityRenderer::new));

        EntityRendererRegistry.register(FBombsEntityTypes.DYNAMITE, DynamiteEntityRenderer::new);
        EntityRendererRegistry.register(FBombsEntityTypes.BOUNCY_DYNAMITE, DynamiteEntityRenderer::new);
        EntityRendererRegistry.register(FBombsEntityTypes.STICKY_DYNAMITE, DynamiteEntityRenderer::new);
        EntityRendererRegistry.register(FBombsEntityTypes.DYNAMITE_BUNDLE, DynamiteEntityRenderer::new);

        BlockEntityRendererFactories.register(FBombsBlockEntities.ACME_BED, AcmeBedBlockEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(SIREN_HEAD_BLOCK_ENTITY_LAYER, SirenBlockEntityRenderer::getTexturedModelData);
        BlockEntityRendererFactories.register(FBombsBlockEntities.SIREN, ctx -> new SirenBlockEntityRenderer(ctx.getLayerModelPart(SIREN_HEAD_BLOCK_ENTITY_LAYER)));

        BlockRenderLayerMap.INSTANCE.putBlock(FBombsBlocks.EXPOSED_CHAINLINK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(FBombsBlocks.GUNPOWDER_TRAIL, RenderLayer.getCutout());
        ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> 0x494949, FBombsBlocks.GUNPOWDER_TRAIL);
    }
}
