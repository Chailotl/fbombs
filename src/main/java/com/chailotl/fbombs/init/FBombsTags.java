package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import net.minecraft.block.Block;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class FBombsTags {
    public static class Items {
        public static final TagKey<Item> SPLITS_TNT = TagKey.of(RegistryKeys.ITEM, FBombs.getId("splits_tnt"));
        public static final TagKey<Item> IGNITES_TNT = TagKey.of(RegistryKeys.ITEM, FBombs.getId("ignites_tnt"));
        public static final TagKey<Item> FIRE_CORAL = TagKey.of(RegistryKeys.ITEM, FBombs.getId("fire_coral"));
    }

    public static class Blocks {
        public static final TagKey<Block> TNT_VARIANTS = TagKey.of(RegistryKeys.BLOCK, FBombs.getId("tnt_variants"));
        public static final TagKey<Block> VOLUMETRIC_EXPLOSION_IMMUNE = TagKey.of(RegistryKeys.BLOCK, FBombs.getId("volumetric_explosion_immune"));
        public static final TagKey<Block> TRANSMITS_REDSTONE_POWER = TagKey.of(RegistryKeys.BLOCK, FBombs.getId("transmits_redstone_power"));
    }

    public static class Fluids {
        public static final TagKey<Fluid> JUICE_THAT_MAKES_YOU_EXPLODE = TagKey.of(RegistryKeys.FLUID, FBombs.getId("juice_that_makes_you_explode"));
    }
}
