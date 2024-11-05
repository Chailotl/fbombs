package com.chailotl.fbombs.entity.util;

import com.chailotl.fbombs.entity.DynamiteEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

@FunctionalInterface
public interface DynamiteEntityProviderOwner {
    DynamiteEntity spawn(World world, LivingEntity owner);
}