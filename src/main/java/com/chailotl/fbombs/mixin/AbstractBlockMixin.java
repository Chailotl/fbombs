package com.chailotl.fbombs.mixin;

import com.chailotl.fbombs.api.VolumetricExplosion;
import com.chailotl.fbombs.block.GenericTntBlock;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin implements VolumetricExplosion {
    @Override
    public void fbombs$onExploded(ServerWorld world, BlockPos pos, BlockPos originPos, BlockState state, int explosionStrength) {
        if (state.isAir()) return;
        LootContextParameterSet.Builder loot = new LootContextParameterSet.Builder(world)
                .add(LootContextParameters.ORIGIN, Vec3d.ofCenter(pos))
                .add(LootContextParameters.TOOL, ItemStack.EMPTY)
                .add(LootContextParameters.EXPLOSION_RADIUS, (float) explosionStrength);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            loot.add(LootContextParameters.BLOCK_ENTITY, blockEntity);
        }
        DefaultedList<ItemStack> droppedStacks = DefaultedList.ofSize(state.getDroppedStacks(loot).size());
        droppedStacks.addAll(state.getDroppedStacks(loot));
        ItemScatterer.spawn(world, pos, droppedStacks);
        state.onStacksDropped(world, pos, ItemStack.EMPTY, state.getBlock() instanceof ExperienceDroppingBlock);

        // LoggerUtil.devLogger(state.toString());
        if (state.getBlock() instanceof TntBlock) {
            TntEntity tntEntity = new TntEntity(world, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, null);
            int i = tntEntity.getFuse();
            tntEntity.setFuse(world.random.nextInt(i / 4) + i / 8);
            world.spawnEntity(tntEntity);
            world.removeBlock(pos, false);
        }
        if (state.getBlock() instanceof GenericTntBlock tntBlock) {
            tntBlock.primeTnt(world, pos);
            world.removeBlock(pos, false);
        }
        world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
    }
}
