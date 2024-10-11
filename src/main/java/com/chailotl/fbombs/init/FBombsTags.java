package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class FBombsTags {
    // public static final TagKey<Item> TEST_ITEM_TAG = TagKey.of(RegistryKeys.ITEM, FBombs.getId("test_item_tag"));
    public static final TagKey<Block> TNT_VARIANTS = TagKey.of(RegistryKeys.BLOCK, FBombs.getId("tnt_variants"));
}
