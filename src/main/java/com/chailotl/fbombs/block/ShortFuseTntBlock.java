package com.chailotl.fbombs.block;

import com.chailotl.fbombs.entity.AbstractTntEntity;
import com.chailotl.fbombs.entity.ShortFuseTntEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShortFuseTntBlock extends AbstractTntBlock {
    public static final MapCodec<ShortFuseTntBlock> CODEC = createCodec(ShortFuseTntBlock::new);

    public MapCodec<ShortFuseTntBlock> getCodec() {
        return CODEC;
    }

    public ShortFuseTntBlock(Settings settings) {
        super(settings);
    }

    @Override
    public AbstractTntEntity createTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter) {
        return new ShortFuseTntEntity(world, x, y, z, igniter);
    }
}