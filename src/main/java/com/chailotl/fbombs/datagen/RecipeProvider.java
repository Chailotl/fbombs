package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.init.FBombsBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends FabricRecipeProvider {

    public RecipeProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, FBombsBlocks.SPONGE_BOMB)
                .pattern("sss")
                .pattern("sts")
                .pattern("sss")
                .input('s', Items.SPONGE)
                .input('t', Blocks.TNT)
                .criterion(FabricRecipeProvider.hasItem(Blocks.SPONGE), FabricRecipeProvider.conditionsFromItem(Blocks.SPONGE))
                .criterion(FabricRecipeProvider.hasItem(Blocks.TNT), FabricRecipeProvider.conditionsFromItem(Blocks.TNT))
                .offerTo(exporter);
    }
}
