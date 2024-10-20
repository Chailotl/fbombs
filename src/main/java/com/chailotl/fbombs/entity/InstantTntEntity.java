package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InstantTntEntity extends AbstractTntEntity {
    public InstantTntEntity(EntityType<InstantTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public InstantTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(FBombsEntityTypes.INSTANT_TNT, world, x, y, z, igniter, state);
    }

    @Override
    protected int getDefaultFuse() {
        return 0;
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.INSTANT_TNT;
    }
}