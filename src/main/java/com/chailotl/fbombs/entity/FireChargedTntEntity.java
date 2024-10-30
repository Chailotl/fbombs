package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FireChargedTntEntity extends AbstractTntEntity {
    public FireChargedTntEntity(EntityType<FireChargedTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public FireChargedTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(FBombsEntityTypes.FIRE_CHARGED_TNT, world, x, y, z, igniter, state);
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.FIRE_CHARGED_TNT;
    }

    @Override
    protected boolean shouldCreateFire() {
        return true;
    }
}