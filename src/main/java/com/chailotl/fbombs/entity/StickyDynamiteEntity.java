package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsEntityTypes;
import com.chailotl.fbombs.init.FBombsItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class StickyDynamiteEntity extends DynamiteEntity {
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
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.setNoGravity(true);
    }
}
