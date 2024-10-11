package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LongFuseTntEntity extends AbstractTntEntity {
    public LongFuseTntEntity(EntityType<LongFuseTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public LongFuseTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
        super(FBombsEntityTypes.LONG_FUSE_TNT, world, x, y, z, igniter);
    }

    @Override
    protected int getDefaultFuse() {
        return 160;
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.LONG_FUSE_TNT;
    }
}