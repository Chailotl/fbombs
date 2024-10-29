package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class RecipeProvider extends FabricRecipeProvider {

    public RecipeProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        //createTntRecipe(exporter, FBombsBlocks.INSTANT_TNT, Blocks.SPONGE);
        //createTntRecipe(exporter, FBombsBlocks.SHORT_FUSE_TNT, Blocks.SPONGE);
        offerTntRecipe(exporter, FBombsBlocks.SHORT_FUSE_TNT, FBombsBlocks.INSTANT_TNT, Items.STRING);
        offerTntRecipe(exporter, Blocks.TNT, FBombsBlocks.SHORT_FUSE_TNT, Items.STRING);
        offerTntRecipe(exporter, FBombsBlocks.LONG_FUSE_TNT, Items.STRING);
        //offerTntRecipe(exporter, FBombsBlocks.HIGH_POWER_TNT, Blocks.TNT);
        offerTntRecipe(exporter, FBombsBlocks.FIRE_TNT, Items.FIRE_CHARGE);
        offerTntRecipe(exporter, FBombsBlocks.WIND_CHARGED_TNT, Items.WIND_CHARGE);
        offerTntRecipe(exporter, FBombsBlocks.UNDERWATER_TNT, Items.PRISMARINE_SHARD);
        offerTntRecipe(exporter, FBombsBlocks.SPONGE_BOMB, Blocks.SPONGE);
        offerTntRecipe(exporter, FBombsBlocks.LEVITATING_TNT, Items.PHANTOM_MEMBRANE);
        offerTntRecipe(exporter, FBombsBlocks.SHAPED_CHARGE, Items.BOWL);
        offerTntRecipe(exporter, FBombsBlocks.MINING_CHARGE, FBombsBlocks.SHAPED_CHARGE, Items.QUARTZ);

        offerAcmeBedRecipe(exporter, FBombsBlocks.WHITE_ACME_BED, Blocks.WHITE_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.LIGHT_GRAY_ACME_BED, Blocks.LIGHT_GRAY_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.GRAY_ACME_BED, Blocks.GRAY_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.BLACK_ACME_BED, Blocks.BLACK_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.BROWN_ACME_BED, Blocks.BROWN_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.RED_ACME_BED, Blocks.RED_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.ORANGE_ACME_BED, Blocks.ORANGE_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.YELLOW_ACME_BED, Blocks.YELLOW_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.LIME_ACME_BED, Blocks.LIME_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.GREEN_ACME_BED, Blocks.GREEN_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.CYAN_ACME_BED, Blocks.CYAN_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.LIGHT_BLUE_ACME_BED, Blocks.LIGHT_BLUE_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.BLUE_ACME_BED, Blocks.BLUE_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.PURPLE_ACME_BED, Blocks.PURPLE_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.MAGENTA_ACME_BED, Blocks.MAGENTA_BED);
        offerAcmeBedRecipe(exporter, FBombsBlocks.PINK_ACME_BED, Blocks.PINK_BED);

        offerDynamiteRecipe(exporter, FBombsItems.BOUNCY_DYNAMITE, Items.SLIME_BALL);
        offerDynamiteRecipe(exporter, FBombsItems.STICKY_DYNAMITE, Items.HONEY_BOTTLE);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, FBombsBlocks.FRAGMENTATION_TNT)
            .pattern("nnn")
            .pattern("ntn")
            .pattern("nnn")
            .input('t', Blocks.TNT)
            .input('n', Items.IRON_NUGGET)
            .criterion(FabricRecipeProvider.hasItem(Blocks.TNT), FabricRecipeProvider.conditionsFromItem(Blocks.TNT))
            .offerTo(exporter);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, FBombsBlocks.HIGH_POWER_TNT)
            .input(Blocks.BARREL)
            .input(Items.GUNPOWDER, 8)
            .criterion(FabricRecipeProvider.hasItem(Items.GUNPOWDER), FabricRecipeProvider.conditionsFromItem(Items.GUNPOWDER))
            .criterion(FabricRecipeProvider.hasItem(Blocks.BARREL), FabricRecipeProvider.conditionsFromItem(Blocks.BARREL))
            .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, FBombsBlocks.LOW_POWER_TNT)
            .pattern("gs")
            .pattern("sg")
            .input('g', Items.GUNPOWDER)
            .input('s', ItemTags.SAND)
            .criterion(FabricRecipeProvider.hasItem(Items.GUNPOWDER), FabricRecipeProvider.conditionsFromItem(Items.GUNPOWDER))
            .offerTo(exporter);
    }

    private void offerTntRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
        offerTntRecipe(exporter, output, Blocks.TNT, input);
    }

    private void offerTntRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible tnt, ItemConvertible input) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, output)
            .input(tnt)
            .input(input)
            .criterion(FabricRecipeProvider.hasItem(tnt), FabricRecipeProvider.conditionsFromItem(tnt))
            .offerTo(exporter);
    }

    private void offerAcmeBedRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, output)
            .input(Blocks.TNT)
            .input(input)
            .criterion(FabricRecipeProvider.hasItem(input), FabricRecipeProvider.conditionsFromItem(input))
            .offerTo(exporter);
    }

    private void offerDynamiteRecipe(RecipeExporter exporter, ItemConvertible output, ItemConvertible input) {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, output, 4)
            .input(FBombsItems.DYNAMITE, 4)
            .input(input)
            .criterion(FabricRecipeProvider.hasItem(FBombsItems.DYNAMITE), FabricRecipeProvider.conditionsFromItem(FBombsItems.DYNAMITE))
            .offerTo(exporter);
    }
}
