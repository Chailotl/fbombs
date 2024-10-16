package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ConcussiveTntEntity extends AbstractTntEntity {
    public ConcussiveTntEntity(EntityType<ConcussiveTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public ConcussiveTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(FBombsEntityTypes.SHORT_FUSE_TNT, world, x, y, z, igniter, state);
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.SHORT_FUSE_TNT;
    }

    @Override
    protected boolean shouldBreakBlocks() {
        return false;
    }
}