package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsEntityTypes;
import com.chailotl.fbombs.init.FBombsItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class StickyDynamiteEntity extends DynamiteEntity {
    private boolean stuck = false;
    private LivingEntity stuckEntity = null;
    private Vec3d stuckOffset = Vec3d.ZERO;

    public StickyDynamiteEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public StickyDynamiteEntity(World world, LivingEntity owner) {
        super(FBombsEntityTypes.STICKY_DYNAMITE, owner, world);
    }

    public StickyDynamiteEntity(World world, double x, double y, double z) {
        super(FBombsEntityTypes.STICKY_DYNAMITE, x, y, z, world);
    }

    @Override
    protected double getVerticalBounce() {
        return 0;
    }

    @Override
    protected double getHorizontalBounce() {
        return 0;
    }

    @Override
    protected Item getDefaultItem() {
        return FBombsItems.STICKY_DYNAMITE;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.stuckEntity != null) {
            if (this.stuckEntity.isAlive()) {
                Vec3d pos = this.stuckEntity.getPos().add(this.stuckOffset);
                this.setPos(pos.x, pos.y, pos.z);
            } else {
                Vec3d velocity = this.stuckEntity.getVelocity();
                this.stuckEntity = null;
                this.setVelocity(velocity);
                this.setNoGravity(false);
            }
        }
    }

    @Override
    public void setVelocity(Vec3d velocity) {
        if (!this.stuck && this.stuckEntity == null) {
            super.setVelocity(velocity);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (this.stuckEntity == null) {
            this.setVelocity(Vec3d.ZERO);
            this.setNoGravity(true);
            this.stuck = true;
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (this.stuckEntity == null && entityHitResult.getEntity() instanceof LivingEntity entity) {
            this.setVelocity(Vec3d.ZERO);
            this.setNoGravity(true);
            this.stuckEntity = entity;
            this.stuckOffset = this.getPos().subtract(entity.getPos()).multiply(0.5, 1, 0.5);
        }
    }
}
