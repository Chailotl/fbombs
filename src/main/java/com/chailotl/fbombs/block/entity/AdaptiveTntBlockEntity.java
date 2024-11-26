package com.chailotl.fbombs.block.entity;

import com.chailotl.fbombs.init.FBombsBlockEntities;
import com.chailotl.fbombs.init.FBombsBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class AdaptiveTntBlockEntity extends BlockEntity {
    public int power = 0;
    public int fuse = 0;
    public boolean damage = true;
    public boolean blockDamage = true;
    public boolean underwater = false;
    public boolean sponge = false;
    public boolean fireCharged = false;
    public boolean windCharged = false;
    public boolean levitating = false;
    public boolean firework = false;
    public boolean bouncy = false;
    public boolean sticky = false;

    public AdaptiveTntBlockEntity(BlockPos pos, BlockState state) {
        super(FBombsBlockEntities.ADAPTIVE_TNT, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        nbt.putInt("power", power);
        nbt.putInt("fuse", fuse);
        nbt.putBoolean("damage", damage);
        nbt.putBoolean("block_damage", blockDamage);
        nbt.putBoolean("underwater", underwater);
        nbt.putBoolean("sponge", sponge);
        nbt.putBoolean("fire_charged", fireCharged);
        nbt.putBoolean("wind_charged", windCharged);
        nbt.putBoolean("levitating", levitating);
        nbt.putBoolean("firework", firework);
        nbt.putBoolean("bouncy", bouncy);
        nbt.putBoolean("sticky", sticky);

        super.writeNbt(nbt, wrapper);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        super.readNbt(nbt, wrapper);

        power = nbt.getInt("power");
        fuse = nbt.getInt("fuse");
        damage = nbt.getBoolean("damage");
        blockDamage = nbt.getBoolean("block_damage");
        underwater = nbt.getBoolean("underwater");
        sponge = nbt.getBoolean("sponge");
        fireCharged = nbt.getBoolean("fire_charged");
        windCharged = nbt.getBoolean("wind_charged");
        levitating = nbt.getBoolean("levitating");
        firework = nbt.getBoolean("firework");
        bouncy = nbt.getBoolean("bouncy");
        sticky = nbt.getBoolean("sticky");
    }

    public ItemStack getStack() {
        ItemStack stack = FBombsBlocks.ADAPTIVE_TNT.asItem().getDefaultStack();
        this.setStackNbt(stack, this.getWorld().getRegistryManager());
        return stack;
    }
}