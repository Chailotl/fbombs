package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public class AdaptiveTntEntity extends AbstractTntEntity {
    public int power = 0;
    public boolean fireCharged = false;
    public boolean firework = false;
    public ExplosionBehavior explosionBehavior = null;

    public AdaptiveTntEntity(EntityType<AdaptiveTntEntity> entityType, World world) {
        super(entityType, world);
    }

    public AdaptiveTntEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(FBombsEntityTypes.ADAPTIVE_TNT, world, x, y, z, igniter, state);
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.ADAPTIVE_TNT;
    }

    @Override
    protected float getPower() {
        return power;
    }

    @Override
    protected boolean shouldCreateFire() {
        return fireCharged;
    }

    @Override
    protected ExplosionBehavior getExplosionBehavior() {
        return explosionBehavior;
    }

    @Override
    public void tick() {
        if (this.firework) {
            this.setVelocity(this.getVelocity().multiply(1.15, 1.0, 1.15).add(0.0, 0.04, 0.0));
        }
        super.tick();
    }

    public void enableLevitating() {
        this.setVelocity(0, 0, 0);
        this.setNoGravity(true);
    }

    public void enableFirework() {
        this.firework = true;
        this.setVelocity(this.random.nextTriangular(0.0, 0.002297), 0.05, this.random.nextTriangular(0.0, 0.002297));
        this.setNoGravity(true);
    }
}