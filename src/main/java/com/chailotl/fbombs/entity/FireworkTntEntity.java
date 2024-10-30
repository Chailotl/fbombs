package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FireworkTntEntity extends AbstractTntEntity {
    public FireworkTntEntity(EntityType<FireworkTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public FireworkTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(FBombsEntityTypes.FIREWORK_TNT, world, x, y, z, igniter, state);
        this.setVelocity(this.random.nextTriangular(0.0, 0.002297), 0.05, this.random.nextTriangular(0.0, 0.002297));
        this.setNoGravity(true);
    }

    @Override
    protected int getDefaultFuse() {
        return 40 + this.random.nextInt(6) + this.random.nextInt(7);
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.FIREWORK_TNT;
    }

    @Override
    public void tick() {
        this.setVelocity(this.getVelocity().multiply(1.15, 1.0, 1.15).add(0.0, 0.04, 0.0));
        super.tick();
    }
}