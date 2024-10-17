package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.GenericTntBlock;
import com.chailotl.fbombs.init.FBombsTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class TagProvider {

    public static class BlockTagProvider extends FabricTagProvider.BlockTagProvider {
        public BlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

            var tntVariants = getOrCreateTagBuilder(FBombsTags.Blocks.TNT_VARIANTS)
                    .add(Blocks.TNT);

            FBombs.streamEntries(Registries.BLOCK, block -> block instanceof GenericTntBlock).forEach(tntVariants::add);

            getOrCreateTagBuilder(FBombsTags.Blocks.VOLUMETRIC_EXPLOSION_IMMUNE)
                    .add(Blocks.BEDROCK, Blocks.BARRIER, Blocks.BEDROCK, Blocks.END_PORTAL,
                            Blocks.END_PORTAL_FRAME, Blocks.END_GATEWAY, Blocks.COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK,
                            Blocks.CHAIN_COMMAND_BLOCK, Blocks.STRUCTURE_BLOCK, Blocks.JIGSAW, Blocks.MOVING_PISTON, Blocks.LIGHT,
                            Blocks.REINFORCED_DEEPSLATE);

        }
    }

    public static class ItemTagProvider extends FabricTagProvider.ItemTagProvider {
        public ItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getOrCreateTagBuilder(FBombsTags.Items.SPLITS_TNT)
                    .add(Items.SHEARS);
            getOrCreateTagBuilder(FBombsTags.Items.IGNITES_TNT)
                    .add(Items.FLINT_AND_STEEL, Items.FIRE_CHARGE);
        }
    }

    public static void registerAll(FabricDataGenerator.Pack pack) {
        pack.addProvider(BlockTagProvider::new);
        pack.addProvider(ItemTagProvider::new);
    }
}
