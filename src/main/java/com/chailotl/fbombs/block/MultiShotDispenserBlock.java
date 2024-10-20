package com.chailotl.fbombs.block;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.entity.MultiShotDispenserBlockEntity;
import com.chailotl.fbombs.init.FBombsBlockEntities;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.DispenserBehavior;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.event.GameEvent;
import org.slf4j.Logger;

public class MultiShotDispenserBlock extends DispenserBlock {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<MultiShotDispenserBlock> CODEC = createCodec(MultiShotDispenserBlock::new);

    @Override
    public MapCodec<? extends MultiShotDispenserBlock> getCodec() {
        return CODEC;
    }

    public MultiShotDispenserBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MultiShotDispenserBlockEntity(pos, state);
    }

    @Override
    protected void dispense(ServerWorld world, BlockState state, BlockPos pos) {
        DispenserBlockEntity dispenserBlockEntity = world.getBlockEntity(pos, FBombsBlockEntities.MULTI_SHOT_DISPENSER).orElse(null);
        if (dispenserBlockEntity == null) {
            LOGGER.warn("Ignoring dispensing attempt for Dispenser without matching block entity at {}", pos);
        } else {
            BlockPointer blockPointer = new BlockPointer(world, pos, state, dispenserBlockEntity);
            int i = dispenserBlockEntity.chooseNonEmptySlot(world.random);
            if (i < 0) {
                world.syncWorldEvent(WorldEvents.DISPENSER_FAILS, pos, 0);
                world.emitGameEvent(GameEvent.BLOCK_ACTIVATE, pos, GameEvent.Emitter.of(dispenserBlockEntity.getCachedState()));
            } else {
                ItemStack itemStack = dispenserBlockEntity.getStack(i);
                DispenserBehavior dispenserBehavior = this.getBehaviorForItem(world, itemStack);
                if (dispenserBehavior != DispenserBehavior.NOOP) {
                    dispenserBlockEntity.setStack(i, dispenserBehavior.dispense(blockPointer, itemStack));
                }
            }
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        int power = world.getReceivedStrongRedstonePower(pos);
        FBombs.LOGGER.info(String.valueOf(power));
        for (int i = 0; i < power; ++i) {
            this.dispense(world, state, pos);
        }
    }
}