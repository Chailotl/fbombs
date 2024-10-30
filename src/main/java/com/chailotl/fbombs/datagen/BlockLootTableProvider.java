package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.GenericTntBlock;
import com.chailotl.fbombs.init.FBombsBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {
    public BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        FBombs.streamEntries(Registries.BLOCK, block -> block instanceof GenericTntBlock).forEach(this::addDrop);
        addDrop(FBombsBlocks.TNT_SLAB);
        addDrop(FBombsBlocks.GUNPOWDER_TRAIL, Items.GUNPOWDER);
    }
}
