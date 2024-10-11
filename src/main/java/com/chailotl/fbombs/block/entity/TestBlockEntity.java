package com.chailotl.fbombs.block.entity;

import com.chailotl.fbombs.init.FBombsBlockEntities;
import com.chailotl.fbombs.util.HandledInventory;
import com.chailotl.fbombs.util.NbtKeys;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("unused")
public class TestBlockEntity extends BlockEntity implements HandledInventory {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(16);

    public TestBlockEntity(BlockPos pos, BlockState state) {
        super(FBombsBlockEntities.TEST_BLOCK_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.readNbt(nbt.getCompound(NbtKeys.INVENTORY), getItems(), registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        NbtCompound inventoryNbt = new NbtCompound();
        Inventories.writeNbt(inventoryNbt, this.getItems(), registryLookup);
        nbt.put(NbtKeys.INVENTORY, inventoryNbt);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return this.inventory;
    }
}
