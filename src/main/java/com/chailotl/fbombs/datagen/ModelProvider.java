package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TexturedModel;

public class ModelProvider extends FabricModelProvider {

    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // TODO: [Chai] unsure if we should assume all TNT blocks will have the same textured model
        //FBombsBlocks.streamTntBlocks().forEach(block -> blockStateModelGenerator.registerSingleton(block, TexturedModel.CUBE_BOTTOM_TOP));
        blockStateModelGenerator.registerSingleton(FBombsBlocks.INSTANT_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.SHORT_FUSE_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.LONG_FUSE_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.HIGH_POWER_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.LOW_POWER_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.FIRE_TNT, TexturedModel.CUBE_BOTTOM_TOP);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(FBombsItems.DYNAMITE_STICK, Models.GENERATED);
    }
}
