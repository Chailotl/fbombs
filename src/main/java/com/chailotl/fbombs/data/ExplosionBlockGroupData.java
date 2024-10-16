package com.chailotl.fbombs.data;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class ExplosionBlockGroupData {
    private final HashMap<BlockPos, Boolean> processedBlocks = new HashMap<>();
    private boolean isCompletelyProcessed = true;

    public ExplosionBlockGroupData() {
    }

    public ExplosionBlockGroupData(List<BlockPos> unprocessedBlockPosList) {
        for (BlockPos pos : unprocessedBlockPosList) {
            this.processedBlocks.put(pos, false);
        }
    }

    public void addBlockPos(BlockPos pos, boolean processed) {
        this.processedBlocks.put(pos, true);
        if (isCompletelyProcessed && processed) return;
        this.updateBlockGroupProcessedState();
    }

    public void addBlockPosMap(HashMap<BlockPos, Boolean> processedBlockPosMap) {
        boolean inputAlreadyProcessed = true;
        for (var entry : processedBlockPosMap.entrySet()) {
            if (!entry.getValue()) {
                inputAlreadyProcessed = false;
                break;
            }
        }
        this.processedBlocks.putAll(processedBlockPosMap);
        if (isCompletelyProcessed() && inputAlreadyProcessed) return;
        this.updateBlockGroupProcessedState();
    }

    public boolean isCompletelyProcessed() {
        return isCompletelyProcessed;
    }

    private void updateBlockGroupProcessedState() {
        for (var entry : processedBlocks.entrySet()) {
            if (!entry.getValue()) {
                this.isCompletelyProcessed = false;
                return;
            }
        }
        this.isCompletelyProcessed = true;
    }
}
