package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.GenericTntBlock;
import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsDamageTypes;
import com.chailotl.fbombs.init.FBombsTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Items;
import net.minecraft.registry.*;
import net.minecraft.registry.tag.DamageTypeTags;

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

            getOrCreateTagBuilder(FBombsTags.Blocks.TRANSMITS_REDSTONE_POWER)
                    .add(FBombsBlocks.SIREN_BASE, FBombsBlocks.SIREN_POLE, FBombsBlocks.SIREN_HEAD, Blocks.IRON_BARS);

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

    public static class DamageTypeTagProvider extends FabricTagProvider<DamageType> {
        public DamageTypeTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getOrCreateTagBuilder(DamageTypeTags.IS_EXPLOSION).add(FBombsDamageTypes.NUCLEAR_EXPLOSION);
            getOrCreateTagBuilder(DamageTypeTags.BYPASSES_ARMOR).add(FBombsDamageTypes.NUCLEAR_EXPLOSION);
            getOrCreateTagBuilder(DamageTypeTags.NO_KNOCKBACK).add(FBombsDamageTypes.NUCLEAR_EXPLOSION);
            getOrCreateTagBuilder(DamageTypeTags.PANIC_CAUSES).add(FBombsDamageTypes.NUCLEAR_EXPLOSION);
        }
    }

    public static void registerAll(FabricDataGenerator.Pack pack) {
        pack.addProvider(BlockTagProvider::new);
        pack.addProvider(ItemTagProvider::new);
        pack.addProvider(DamageTypeTagProvider::new);
    }
}
