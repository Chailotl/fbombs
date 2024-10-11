package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.TestBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FBombsBlocks {
    public static final TestBlock TEST_BLOCK = register("test_block", new TestBlock(AbstractBlock.Settings.create()), true);

    @SuppressWarnings("SameParameterValue")
    private static <T extends Block> T register(String name, T block, boolean hasDefaultItem) {
        Registry.register(Registries.BLOCK, FBombs.getId(name), block);
        if (hasDefaultItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, FBombs.getId(name), blockItem);
            FBombsItemGroups.BLOCKS.addItems(blockItem);
        }
        return block;
    }

    public static void initialize() {
        // static initialisation
    }
}
