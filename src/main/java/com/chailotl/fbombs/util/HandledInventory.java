package com.chailotl.fbombs.util;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public interface HandledInventory extends SidedInventory {
    DefaultedList<ItemStack> getItems();

    default int size() {
        return getItems().size();
    }

    default int nonEmptyStackSize() {
        return (int) getItems().stream().filter(stack -> !stack.isEmpty()).count();
    }

    @Override
    default int[] getAvailableSlots(Direction side) {
        List<Integer> availableSlots = new ArrayList<>();
        for (int i = 0; i < getItems().size(); i++) {
            availableSlots.add(i);
        }
        return availableSlots.stream().mapToInt(Integer::intValue).toArray();
    }

    @Override
    default boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return true;
    }

    default boolean insert(ItemStack stack, @Nullable Direction direction) {
        if (!canInsert(getItems().size(), stack, direction)) return false;
        if (!tryAddingStack(stack.copy())) return false;
        markDirty();
        return true;
    }

    @Override
    default boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return !isEmpty();
    }

    /**
     * Extracts latest inserted item from HandledInventory.
     *
     * @param direction Interaction side for potential side filtering
     * @return Removed Stack from Inventory. Is empty if stack couldn't be extracted.
     */
    default Optional<ItemStack> extract(@Nullable Direction direction) {
        return tryRemovingStack();
    }

    @Override
    default boolean isEmpty() {
        return size() <= 0 || getItems().stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    default ItemStack getStack(int slot) {
        return getItems().get(slot);
    }

    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        markDirty();
    }

    default boolean tryAddingStack(ItemStack stack) {
        boolean stackWasAdded = false;
        for (int i = 0; i < size(); i++) {
            if (!getItems().get(i).equals(ItemStack.EMPTY) || !canInsert(i, stack, null)) continue;
            setStack(i, stack);
            stackWasAdded = true;
            markDirty();
            break;
        }
        return stackWasAdded;
    }

    default Optional<ItemStack> tryRemovingStack() {
        ItemStack outputStack = ItemStack.EMPTY;
        for (int i = getItems().size() - 1; i >= 0; i--) {
            ItemStack stackInList = getItems().get(i);
            if (stackInList.isEmpty() || !canExtract(i, stackInList, null)) continue;
            outputStack = getItems().get(i).copy();
            setStack(i, ItemStack.EMPTY);
            markDirty();
            break;
        }
        return outputStack.isEmpty() ? Optional.empty() : Optional.of(outputStack);
    }

    default ItemStack removeStack(ItemStack itemStack) {
        ItemStack stack = itemStack.copy();
        for (int i = getItems().size() - 1; i >= 0; i--) {
            if (getItems().get(i).isOf(stack.getItem())) {
                if (getItems().get(i).getCount() >= stack.getCount()) {
                    if (getItems().get(i).getCount() == stack.getCount()) {
                        getItems().set(i, ItemStack.EMPTY);
                    } else {
                        getItems().get(i).decrement(stack.getCount());
                    }
                    return ItemStack.EMPTY;
                } else {
                    stack.decrement(getItems().get(i).getCount());
                    getItems().set(i, ItemStack.EMPTY);
                }
            }
        }
        markDirty();
        return stack;
    }

    @Override
    default ItemStack removeStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }

    @Override
    default ItemStack removeStack(int slot, int amount) {
        ItemStack stack = Inventories.splitStack(getItems(), slot, amount);
        if (!stack.isEmpty()) markDirty();
        return stack;
    }

    default boolean contains(Item item) {
        for (ItemStack stack : this.getItems()) {
            if (stack.isOf(item)) return true;
        }
        return false;
    }

    default boolean contains(ItemStack stack) {
        for (ItemStack entry : this.getItems()) {
            if (entry.equals(stack)) return true;
        }
        return false;
    }

    @Override
    default boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    default void clear() {
        getItems().clear();
        markDirty();
    }

    default void moveInventory(HandledInventory external) {
        for (int i = 0; i < this.getItems().size(); i++) {
            external.setStack(i, this.getItems().get(i));
        }
        this.clear();
    }

    void markDirty();

    default InventoryStorage getAsStorage(@Nullable Direction direction) {
        return InventoryStorage.of(this, direction);
    }

    default ItemVariant getTopItem(@Nullable Direction direction) {
        return ItemVariant.of(getItems().getLast());
    }
}
