package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsEntityTypes;
import com.chailotl.fbombs.init.FBombsItems;
import com.chailotl.fbombs.init.FBombsParticleTypes;
import com.chailotl.fbombs.init.FBombsSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

import java.util.Optional;

public class BouncyDynamiteEntity extends DynamiteEntity {
    public BouncyDynamiteEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public BouncyDynamiteEntity(World world, LivingEntity owner) {
        super(FBombsEntityTypes.BOUNCY_DYNAMITE, owner, world);
    }

    public BouncyDynamiteEntity(World world, double x, double y, double z) {
        super(FBombsEntityTypes.BOUNCY_DYNAMITE, x, y, z, world);
    }

    @Override
    protected double getVerticalBounce() {
        return 0.9;
    }

    @Override
    protected double getHorizontalBounce() {
        return 0.9;
    }

    @Override
    protected Item getDefaultItem() {
        return FBombsItems.BOUNCY_DYNAMITE;
    }
}
