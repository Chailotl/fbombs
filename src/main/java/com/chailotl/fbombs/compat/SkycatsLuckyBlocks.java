package com.chailotl.fbombs.compat;

import com.chailotl.fbombs.block.GenericTntBlock;
import com.chailotl.fbombs.init.FBombsBlocks;
import com.skycatdev.skycatsluckyblocks.LuckyEffectPools;
import com.skycatdev.skycatsluckyblocks.impl.SimpleLuckyEffect;
import net.minecraft.registry.Registries;

import java.util.List;

public class SkycatsLuckyBlocks {
    public static void initialize() {
        List<GenericTntBlock> tntBlocks = List.of(
            FBombsBlocks.FRAGMENTATION_TNT,
            FBombsBlocks.CLUSTER_TNT,
            FBombsBlocks.WIND_CHARGED_TNT,
            FBombsBlocks.FIREWORK_TNT,
            FBombsBlocks.TNT_SLAB
        );

        tntBlocks.forEach(block -> new SimpleLuckyEffect.Builder(Registries.BLOCK.getId(block), (world, pos, state, player) -> {
            world.setBlockState(pos, block.getDefaultState());
            block.primeTnt(world, pos);
            world.removeBlock(pos, false);
            return true;
        }).addPool(LuckyEffectPools.DEFAULT, 1).build());
    }
}