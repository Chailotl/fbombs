package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.block.SplitTntBlock;
import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SplitTntEntity extends AbstractTntEntity {
    private BlockState state;

    public SplitTntEntity(EntityType<SplitTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public SplitTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(FBombsEntityTypes.SPLIT_TNT, world, x, y, z, igniter, state);
        this.state = state;
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.SPLIT_TNT;
    }

    @Override
    protected float getPower() {
        if (SplitTntBlock.containsSplitStates(state)) {
            return SplitTntBlock.getExistingSplits(state).size();
        }
        return super.getPower();
    }
}