package com.chailotl.fbombs.entity.util;

import com.chailotl.fbombs.entity.DynamiteEntity;
import net.minecraft.world.World;

@FunctionalInterface
public interface DynamiteEntityProviderPos {
    DynamiteEntity spawn(World world, double x, double y, double z);
}