package com.chailotl.fbombs.entity.util;

import com.chailotl.fbombs.entity.AbstractTntEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface TntEntityProvider {
    AbstractTntEntity spawn(World world, double x, double y, double z, @Nullable LivingEntity igniter, @Nullable BlockState state);
}