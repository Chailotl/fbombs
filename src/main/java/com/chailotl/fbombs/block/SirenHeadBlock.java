package com.chailotl.fbombs.block;

import com.chailotl.fbombs.block.entity.AbstractSirenBlock;
import com.chailotl.fbombs.block.entity.SirenBlockEntity;
import com.chailotl.fbombs.init.FBombsBlockEntities;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SirenHeadBlock extends AbstractSirenBlock implements BlockEntityProvider {
    public SirenHeadBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SirenBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return FBombsBlockEntities.SIREN == type ? (world1, pos, state1, blockEntity) -> SirenBlockEntity.tick(world1, pos, state1, (SirenBlockEntity) blockEntity) : null;
    }
}
