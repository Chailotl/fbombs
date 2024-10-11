package com.chailotl.fbombs.block.entity;

import com.chailotl.fbombs.init.FBombsBlockEntities;
import com.chailotl.fbombs.util.NbtKeys;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("unused")
public class TestBlockEntity extends BlockEntity {
    private String testValue = "";

    public TestBlockEntity(BlockPos pos, BlockState state) {
        super(FBombsBlockEntities.TEST_BLOCK_ENTITY, pos, state);
    }

    public String getTestValue() {
        return testValue;
    }

    public void setTestValue(String testValue) {
        this.testValue = testValue;
    }


    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putString(NbtKeys.TEST_VALUE, "test");
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.testValue = nbt.getString(NbtKeys.TEST_VALUE);
    }
}
