package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FuselessTntEntity extends AbstractTntEntity {
    public FuselessTntEntity(EntityType<FuselessTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public FuselessTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(FBombsEntityTypes.FUSELESS_TNT, world, x, y, z, igniter, state);
    }

    @Override
    protected int getDefaultFuse() {
        return 0;
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.FUSELESS_TNT;
    }
}