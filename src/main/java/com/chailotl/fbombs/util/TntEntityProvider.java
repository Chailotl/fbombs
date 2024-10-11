package com.chailotl.fbombs.util;

import com.chailotl.fbombs.entity.AbstractTntEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface TntEntityProvider {
    AbstractTntEntity spawn(World world, double x, double y, double z, @Nullable LivingEntity igniter);
}