package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsItems;
import com.chailotl.fbombs.init.FBombsTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
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
        offerTntRecipe(exporter, FBombsBlocks.SHORT_FUSE_TNT, FBombsBlocks.FUSELESS_TNT, Items.STRING);
        offerTntRecipe(exporter, Blocks.TNT, FBombsBlocks.SHORT_FUSE_TNT, Items.STRING);
        offerTntRecipe(exporter, FBombsBlocks.LONG_FUSE_TNT, Items.STRING);
        offerTntRecipe(exporter, FBombsBlocks.HIGH_POWER_TNT, Blocks.TNT);
        offerTntRecipe(exporter, FBombsBlocks.FIRE_CHARGED_TNT, Items.FIRE_CHARGE);
        offerTntRecipe(exporter, FBombsBlocks.WIND_CHARGED_TNT, Items.WIND_CHARGE);
        offerTntRecipe(exporter, FBombsBlocks.UNDERWATER_TNT, Items.PRISMARINE_SHARD);
        offerTntRecipe(exporter, FBombsBlocks.SPONGE_BOMB, Blocks.SPONGE);
        offerTntRecipe(exporter, FBombsBlocks.LEVITATING_TNT, Items.PHANTOM_MEMBRANE);
        offerTntRecipe(exporter, FBombsBlocks.SHAPED_CHARGE, Items.BOWL);
        offerTntRecipe(exporter, FBombsBlocks.MINING_CHARGE, FBombsBlocks.SHAPED_CHARGE, Items.QUARTZ);
        offerTntRecipe(exporter, FBombsBlocks.FIREWORK_TNT, Items.FIREWORK_ROCKET);
        offerTntRecipe(exporter, FBombsBlocks.CLUSTER_TNT, Items.SHEARS);

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
        /*ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, FBombsBlocks.HIGH_POWER_TNT)
            .input(Blocks.BARREL)
            .input(Items.GUNPOWDER, 8)
            .criterion(FabricRecipeProvider.hasItem(Items.GUNPOWDER), FabricRecipeProvider.conditionsFromItem(Items.GUNPOWDER))
            .criterion(FabricRecipeProvider.hasItem(Blocks.BARREL), FabricRecipeProvider.conditionsFromItem(Blocks.BARREL))
            .offerTo(exporter);*/
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, FBombsBlocks.LOW_POWER_TNT)
            .pattern("gs")
            .pattern("sg")
            .input('g', Items.GUNPOWDER)
            .input('s', ItemTags.SAND)
            .criterion(FabricRecipeProvider.hasItem(Items.GUNPOWDER), FabricRecipeProvider.conditionsFromItem(Items.GUNPOWDER))
            .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, FBombsItems.DYNAMITE_BUNDLE)
            .pattern("dd")
            .pattern("dd")
            .input('d', FBombsItems.DYNAMITE)
            .criterion(FabricRecipeProvider.hasItem(FBombsItems.DYNAMITE), FabricRecipeProvider.conditionsFromItem(FBombsItems.DYNAMITE))
            .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, FBombsItems.NUCLEAR_LAUNCH_KEY)
            .input(FBombsItems.COLA_BOTTLE_CAP)
            .input(FBombsItems.ROOT_BEER_BOTTLE_CAP)
            .input(FBombsItems.CREAM_SODA_BOTTLE_CAP)
            .input(FBombsItems.GINGER_ALE_BOTTLE_CAP)
            .input(FBombsItems.LEMON_LIME_SODA_BOTTLE_CAP)
            .input(FBombsItems.BLUEBERRY_SODA_BOTTLE_CAP)
            .input(FBombsItems.CHERRY_SODA_BOTTLE_CAP)
            .input(FBombsItems.ORANGE_SODA_BOTTLE_CAP)
            .input(FBombsItems.SARSPARILLA_BOTTLE_CAP)
            .criterion("has_the_recipe2", RecipeUnlockedCriterion.create(Registries.ITEM.getId(FBombsItems.NUCLEAR_LAUNCH_KEY)))
            .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, FBombsBlocks.SIREN_POLE, 4)
                .pattern(" i ")
                .pattern("rir")
                .pattern(" i ")
                .input('i', Items.IRON_INGOT)
                .input('r', Items.REDSTONE)
                .criterion(FabricRecipeProvider.hasItem(Items.REDSTONE), FabricRecipeProvider.conditionsFromItem(Items.REDSTONE))
                .criterion(FabricRecipeProvider.hasItem(Items.IRON_INGOT), FabricRecipeProvider.conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, FBombsBlocks.SIREN_BASE, 1)
                .pattern("s")
                .pattern("s")
                .pattern("r")
                .input('s', Items.SMOOTH_STONE)
                .input('r', Items.REDSTONE)
                .criterion(FabricRecipeProvider.hasItem(Items.REDSTONE), FabricRecipeProvider.conditionsFromItem(Items.REDSTONE))
                .criterion(FabricRecipeProvider.hasItem(Items.SMOOTH_STONE), FabricRecipeProvider.conditionsFromItem(Items.SMOOTH_STONE))
                .offerTo(exporter);
        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, FBombsBlocks.SIREN_HEAD, 2)
                .pattern(" p ")
                .pattern("npn")
                .pattern(" p ")
                .input('n', Blocks.NOTE_BLOCK)
                .input('p', FBombsBlocks.SIREN_POLE)
                .criterion(FabricRecipeProvider.hasItem(Blocks.NOTE_BLOCK), FabricRecipeProvider.conditionsFromItem(Blocks.NOTE_BLOCK))
                .criterion(FabricRecipeProvider.hasItem(FBombsBlocks.SIREN_POLE), FabricRecipeProvider.conditionsFromItem(FBombsBlocks.SIREN_POLE))
                .offerTo(exporter);

        ShapedRecipeJsonBuilder.create(RecipeCategory.REDSTONE, FBombsBlocks.DETONATOR, 2)
                .pattern("nnn")
                .pattern(" f ")
                .pattern("pgp")
                .input('n', Items.IRON_NUGGET)
                .input('f', Items.FLINT)
                .input('g', Items.GUNPOWDER)
                .input('p', ItemTags.PLANKS)
                .criterion(FabricRecipeProvider.hasItem(Items.IRON_NUGGET), FabricRecipeProvider.conditionsFromItem(Items.IRON_NUGGET))
                .criterion(FabricRecipeProvider.hasItem(Items.FLINT), FabricRecipeProvider.conditionsFromItem(Items.FLINT))
                .criterion(FabricRecipeProvider.hasItem(Items.GUNPOWDER), FabricRecipeProvider.conditionsFromItem(Items.GUNPOWDER))
                .criterion("has_planks", FabricRecipeProvider.conditionsFromTag(ItemTags.PLANKS))
                .offerTo(exporter);

        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, FBombsItems.JUICE_THAT_MAKES_YOU_EXPLODE_BUCKET)
            .input(Items.BUCKET)
            .input(Items.FIRE_CHARGE)
            .input(FBombsTags.Items.FIRE_CORAL)
            .input(FBombsItems.DYNAMITE_BUNDLE)
            .criterion(FabricRecipeProvider.hasItem(Items.FIRE_CHARGE), FabricRecipeProvider.conditionsFromItem(Items.FIRE_CHARGE))
            .criterion(FabricRecipeProvider.hasItem(Items.FIRE_CORAL), FabricRecipeProvider.conditionsFromTag(FBombsTags.Items.FIRE_CORAL))
            .criterion(FabricRecipeProvider.hasItem(FBombsItems.DYNAMITE_BUNDLE), FabricRecipeProvider.conditionsFromItem(FBombsItems.DYNAMITE_BUNDLE))
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
