package com.chailotl.fbombs.block;

import com.chailotl.fbombs.entity.AbstractTntEntity;
import com.chailotl.fbombs.entity.InstantTntEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class InstantTntBlock extends AbstractTntBlock {
    public static final MapCodec<InstantTntBlock> CODEC = createCodec(InstantTntBlock::new);

    public MapCodec<InstantTntBlock> getCodec() {
        return CODEC;
    }

    public InstantTntBlock(Settings settings) {
        super(settings);
    }

    @Override
    public AbstractTntEntity createTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
        return new InstantTntEntity(world, x, y, z, igniter);
    }
}