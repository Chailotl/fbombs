package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsEntityTypes;
import com.chailotl.fbombs.init.FBombsItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class DynamiteBundleEntity extends DynamiteEntity {
    public DynamiteBundleEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
        setFuse(60);
    }

    public DynamiteBundleEntity(World world, LivingEntity owner) {
        super(FBombsEntityTypes.DYNAMITE_BUNDLE, owner, world);
        setFuse(60);
    }

    public DynamiteBundleEntity(World world, double x, double y, double z) {
        super(FBombsEntityTypes.DYNAMITE_BUNDLE, x, y, z, world);
        setFuse(60);
    }

    @Override
    protected Item getDefaultItem() {
        return FBombsItems.DYNAMITE_BUNDLE;
    }

    @Override
    protected int getPower() {
        return 4;
    }
}
