package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.GenericTntBlock;
import com.chailotl.fbombs.init.FBombsBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.CopyComponentsLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BlockLootTableProvider extends FabricBlockLootTableProvider {
    public BlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        List<Block> unsupported = List.of(FBombsBlocks.ADAPTIVE_TNT);

        FBombs.streamEntries(Registries.BLOCK, block -> block instanceof GenericTntBlock).forEach(block -> {
            if (unsupported.contains(block)) { return; }
            addDrop(block);
        });

        addDrop(FBombsBlocks.DETONATOR);
        addDrop(FBombsBlocks.GUNPOWDER_TRAIL, Items.GUNPOWDER);
        addDrop(FBombsBlocks.EXPOSED_CORRUGATED_IRON);
        addDrop(FBombsBlocks.EXPOSED_IRON_PLATE);
        addDrop(FBombsBlocks.EXPOSED_CHAINLINK);

        addDrop(FBombsBlocks.ADAPTIVE_TNT, LootTable.builder()
            .pool(
                addSurvivesExplosionCondition(
                    FBombsBlocks.ADAPTIVE_TNT,
                    LootPool.builder()
                        .rolls(ConstantLootNumberProvider.create(1f))
                        .with(
                            ItemEntry.builder(FBombsBlocks.ADAPTIVE_TNT)
                                .apply(
                                    CopyComponentsLootFunction.builder(CopyComponentsLootFunction.Source.BLOCK_ENTITY)
                                        .include(DataComponentTypes.BLOCK_ENTITY_DATA)
                                )
                        )
                )
            )
        );
    }
}
