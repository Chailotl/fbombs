package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LevitatingTntEntity extends AbstractTntEntity {
    public LevitatingTntEntity(EntityType<LevitatingTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public LevitatingTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(FBombsEntityTypes.LEVITATING_TNT, world, x, y, z, igniter, state);
        this.setVelocity(0, 0, 0);
        this.setNoGravity(true);
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.LEVITATING_TNT;
    }
}