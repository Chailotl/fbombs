package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.InstantTntBlock;
import com.chailotl.fbombs.block.SplitTntBlock;
import com.chailotl.fbombs.block.TestBlock;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class FBombsBlocks {
    public static final TestBlock TEST = register("test_block", new TestBlock(AbstractBlock.Settings.create()), true);
    public static final InstantTntBlock INSTANT_TNT = register("instant_tnt", new InstantTntBlock(tntSettings()), true);
    public static final SplitTntBlock SPLIT_TNT = register("split_tnt", new SplitTntBlock(tntSettings()), true);

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

    private static AbstractBlock.Settings tntSettings() {
        return AbstractBlock.Settings.create()
            .mapColor(MapColor.BRIGHT_RED)
            .breakInstantly()
            .sounds(BlockSoundGroup.GRASS)
            .burnable()
            .solidBlock(Blocks::never);
    }

    public static void initialize() {
        // static initialisation
    }
}
