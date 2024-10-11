package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.*;
import com.chailotl.fbombs.entity.InstantTntEntity;
import com.chailotl.fbombs.entity.ShortFuseTntEntity;
import com.chailotl.fbombs.util.TntEntityProvider;
import com.chailotl.fbombs.util.TntEntityType;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

public class FBombsBlocks {
    public static final TestBlock TEST = register("test_block", new TestBlock(AbstractBlock.Settings.create()), true);
    public static final GenericTntBlock INSTANT_TNT = registerTnt("instant_tnt", InstantTntEntity::new);
    public static final SplitTntBlock SPLIT_TNT = register("split_tnt", new SplitTntBlock(tntSettings()), true);
    public static final GenericTntBlock SHORT_FUSE_TNT = registerTnt("short_fuse_tnt", ShortFuseTntEntity::new);

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

    private static GenericTntBlock registerTnt(String name, TntEntityProvider tntEntityProvider) {
        return register(name, new GenericTntBlock(TntEntityType.register(name, tntEntityProvider), tntSettings()), true);
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
