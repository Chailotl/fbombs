package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.*;
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

public class DynamiteEntity extends ThrownItemEntity {
    private int fuse = 40;

    public DynamiteEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public DynamiteEntity(EntityType<? extends ThrownItemEntity> entityType, LivingEntity owner, World world) {
        super(entityType, owner, world);
    }

    public DynamiteEntity(EntityType<? extends ThrownItemEntity> entityType, double x, double y, double z, World world) {
        super(entityType, x, y, z, world);
    }

    public DynamiteEntity(World world, LivingEntity owner) {
        this(FBombsEntityTypes.DYNAMITE, owner, world);
    }

    public DynamiteEntity(World world, double x, double y, double z) {
        this(FBombsEntityTypes.DYNAMITE, x, y, z, world);
    }

    public int getFuse() {
        return fuse;
    }

    public void setFuse(int fuse) {
        this.fuse = fuse;
    }

    protected double getVerticalBounce() {
        return 0.3;
    }

    protected double getHorizontalBounce() {
        return 0.5;
    }

    @Override
    protected Item getDefaultItem() {
        return FBombsItems.DYNAMITE;
    }

    @Override
    protected double getGravity() {
        return 0.05;
    }

    @Override
    public void tick() {
        super.tick();

        if (getWorld().isClient()) {
            if (age % 2 == 1) {
                Vec3d particleVelocity = this.getVelocity().multiply(0);
                getWorld().addParticle(FBombsParticleTypes.FAST_FLAME,
                    this.getX(), this.getY(), this.getZ(),
                    particleVelocity.x, particleVelocity.y, particleVelocity.z);
            }
        } else {
            if (this.getFuse() <= 0 || this.isOnFire()) {
                if (explode((ServerWorld) getWorld(), this.getBlockPos())) return;
            }

            this.setFuse(this.getFuse() - 1);
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        setPosition(blockHitResult.getPos());
        Vec3d velocity = this.getVelocity();
        boolean grounded = false;

        if (Direction.Type.VERTICAL.stream().anyMatch(direction -> direction.equals(blockHitResult.getSide()))) {
            if (velocity.y <= 0 && velocity.y >= -0.2) {
                velocity = Vec3d.ZERO;
                grounded = true;
            } else {
                velocity = new Vec3d(
                    velocity.x * getHorizontalBounce(),
                    (-velocity.y) * getVerticalBounce(),
                    velocity.z * getHorizontalBounce()
                );
            }
        } else {
            Vec3d wallVector = Vec3d.of(blockHitResult.getSide().getVector());
            velocity = velocity.subtract(wallVector.multiply(velocity.dotProduct(wallVector) * 2));
            velocity = velocity.multiply(getHorizontalBounce());
        }

        if (!this.isOnGround()) {
            this.getWorld().playSound(null, blockHitResult.getBlockPos(),
                SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.NEUTRAL, 1f, 1f);
        }

        this.setVelocity(velocity);
        this.velocityModified = true;
        this.setOnGround(grounded);
    }

    private static final ExplosionBehavior EXPLOSION_BEHAVIOR = new ExplosionBehavior() {
        @Override
        public Optional<Float> getBlastResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState) {
            if (blockState.isAir() && fluidState.isEmpty()) {
                return Optional.empty();
            } else {
                float blastResistance = blockState.getBlock().getBlastResistance();
                return Optional.of(blastResistance <= 0.1f ? blastResistance : 3600000f);
            }
        }

        @Override
        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
            return false;
        }
    };

    private boolean explode(ServerWorld serverWorld, BlockPos pos) {
        //ExplosionHandler.explodeSpherical(serverWorld, pos, 2, 8);
        serverWorld.createExplosion(
            this,
            Explosion.createDamageSource(serverWorld, this),
            EXPLOSION_BEHAVIOR,
            this.getX(),
            this.getY() + 0.05,
            this.getZ(),
            2,
            false,
            World.ExplosionSourceType.TNT,
            ParticleTypes.EXPLOSION,
            ParticleTypes.EXPLOSION_EMITTER,
            Registries.SOUND_EVENT.getEntry(FBombsSoundEvents.DYNAMITE_EXPLOSION)
        );
        this.remove(RemovalReason.DISCARDED);
        return true;
    }
}
