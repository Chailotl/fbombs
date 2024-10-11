package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.*;
import com.chailotl.fbombs.entity.InstantTntEntity;
import com.chailotl.fbombs.entity.LongFuseTntEntity;
import com.chailotl.fbombs.entity.ShortFuseTntEntity;
import com.chailotl.fbombs.util.TntEntityProvider;
import com.chailotl.fbombs.util.TntEntityType;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FBombsBlocks {
    private static List<Block> VALUES = new ArrayList<>();
    private static List<GenericTntBlock> TNT_BLOCKS = new ArrayList<>();

    public static final TestBlock TEST = register("test_block", new TestBlock(AbstractBlock.Settings.create()), true);
    public static final GenericTntBlock INSTANT_TNT = registerTnt("instant_tnt", InstantTntEntity::new);
    public static final SplitTntBlock SPLIT_TNT = register("split_tnt", new SplitTntBlock(
        AbstractBlock.Settings.create()
            .mapColor(MapColor.BRIGHT_RED)
            .breakInstantly()
            .sounds(BlockSoundGroup.GRASS)
            .burnable()
            .solidBlock(Blocks::never)
    ), true);
    public static final GenericTntBlock SHORT_FUSE_TNT = registerTnt("short_fuse_tnt", ShortFuseTntEntity::new);
    public static final GenericTntBlock LONG_FUSE_TNT = registerTnt("long_fuse_tnt", LongFuseTntEntity::new);

    @SuppressWarnings("SameParameterValue")
    private static <T extends Block> T register(String name, T block, boolean hasDefaultItem) {
        VALUES.add(block);
        Registry.register(Registries.BLOCK, FBombs.getId(name), block);
        if (hasDefaultItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, FBombs.getId(name), blockItem);
            FBombsItemGroups.BLOCKS.addItems(blockItem);
        }
        return block;
    }

    private static GenericTntBlock registerTnt(String name, TntEntityProvider tntEntityProvider) {
        GenericTntBlock block = new GenericTntBlock(
            TntEntityType.register(name, tntEntityProvider),
            AbstractBlock.Settings.create()
                .mapColor(MapColor.BRIGHT_RED)
                .breakInstantly()
                .sounds(BlockSoundGroup.GRASS)
                .burnable()
                .solidBlock(Blocks::never)
        );
        TNT_BLOCKS.add(block);
        return register(name, block, true);
    }

    public static void initialize() {
        // static initialisation
    }

    public static Stream<Block> stream() {
        return VALUES.stream();
    }

    public static Stream<GenericTntBlock> streamTntBlocks() {
        return TNT_BLOCKS.stream();
    }
}
