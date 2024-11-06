package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DetonatorEntity extends AbstractTntEntity{
    public DetonatorEntity(EntityType<? extends AbstractTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public DetonatorEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(FBombsEntityTypes.DETONATOR, world, x, y, z, igniter, state);
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.DETONATOR;
    }

    @Override
    protected int getDefaultFuse() {
        return 20;
    }

    @Override
    protected float getPower() {
        return 2;
    }
}
