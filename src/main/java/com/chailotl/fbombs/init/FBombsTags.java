package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class FBombsTags {
    public static final TagKey<Item> SPLITS_TNT = TagKey.of(RegistryKeys.ITEM, FBombs.getId("splits_tnt"));
    public static final TagKey<Block> TNT_VARIANTS = TagKey.of(RegistryKeys.BLOCK, FBombs.getId("tnt_variants"));
}
