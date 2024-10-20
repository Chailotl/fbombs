package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsEntityTypes;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class ShapedChargeEntity extends AbstractTntEntity {
    private Direction facing = Direction.NORTH;

    public ShapedChargeEntity(EntityType<? extends ShapedChargeEntity> entityType, World world) {
        super(entityType, world);
    }

    public ShapedChargeEntity(World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(FBombsEntityTypes.SHAPED_CHARGE, world, x, y, z, igniter, state);
    }

    public ShapedChargeEntity(EntityType<? extends ShapedChargeEntity> entityType, World world, double x, double y, double z, @Nullable LivingEntity igniter, BlockState state) {
        super(entityType, world, x, y, z, igniter, state);
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    @Override
    protected Block getDefaultBlock() {
        return FBombsBlocks.SHAPED_CHARGE;
    }

    @Override
    protected void explode() {
        if (getPower() < 0) {
            this.discard();
            return;
        }
        Vec3d pos = new Vec3d(this.getX(), this.getBodyY(0.0625), this.getZ());
        this.getWorld()
            .createExplosion(
                this,
                Explosion.createDamageSource(this.getWorld(), this),
                getExplosionBehavior(),
                pos.x,
                pos.y,
                pos.z,
                1,
                shouldCreateFire(),
                World.ExplosionSourceType.TNT
            );
        pos = pos.add(Vec3d.of(facing.getVector()).multiply(4));
        this.getWorld()
            .createExplosion(
                this,
                Explosion.createDamageSource(this.getWorld(), this),
                getExplosionBehavior(),
                pos.x,
                pos.y,
                pos.z,
                getPower(),
                shouldCreateFire(),
                World.ExplosionSourceType.TNT
            );
    }
}