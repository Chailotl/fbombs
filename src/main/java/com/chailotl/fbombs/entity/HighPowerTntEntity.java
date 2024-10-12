package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HighPowerTntEntity extends AbstractTntEntity {
    public HighPowerTntEntity(EntityType<HighPowerTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public HighPowerTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
        super(FBombsEntityTypes.HIGH_POWER_TNT, world, x, y, z, igniter);
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.HIGH_POWER_TNT;
    }

    @Override
    protected float getPower() {
        return 8f;
    }
}