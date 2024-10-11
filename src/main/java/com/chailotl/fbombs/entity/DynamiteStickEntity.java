package com.chailotl.fbombs.entity;

import com.chailotl.fbombs.init.FBombsEntityTypes;
import com.chailotl.fbombs.init.FBombsItems;
import com.chailotl.fbombs.util.LoggerUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DynamiteStickEntity extends ThrownItemEntity {
    private int tick = 60;
    private int bounces = -1;

    public DynamiteStickEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public DynamiteStickEntity(World world, LivingEntity user) {
        this(FBombsEntityTypes.DYNAMITE_STICK, world);
        this.setPos(user.getEyePos().getX(), user.getEyePos().getY(), user.getEyePos().getZ());
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public void decrementTick() {
        setTick(getTick() - 1);
    }

    public int getBounces() {
        return bounces;
    }

    public void setMaxBounces(int bounces) {
        this.bounces = bounces;
    }

    public void decrementBounces() {
        this.bounces--;
    }

    @Override
    protected Item getDefaultItem() {
        return FBombsItems.DYNAMITE_STICK;
    }

    @Override
    public void tick() {
        super.tick();
        if (!(getWorld() instanceof ServerWorld serverWorld)) return;

        if (this.getTick() <= 0 || this.bounces == 0 || this.isOnFire()) {
            // TODO: [ShiroJR] ExplosionHelper.explodeSpherical(serverWorld, this.getBlockPos(), 8);
            this.remove(RemovalReason.DISCARDED);
            return;
        }

        if (getTick() % 5 == 0) {
            Vec3d particleVelocity = this.getVelocity().multiply(0.3);
            serverWorld.spawnParticles(ParticleTypes.FLAME, this.getX(), this.getY(), this.getZ(),
                    1, particleVelocity.x, particleVelocity.y, particleVelocity.z, 0);
        }

        this.decrementTick();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);

        Vec3d velocity = this.getVelocity();
        Vec3d wallVector = Vec3d.of(blockHitResult.getSide().getVector());
        double bounceDampenerVertical = 0.4;
        double bounceDampenerHorizontal = 0.7;

        if (Direction.Type.VERTICAL.stream().anyMatch(direction -> direction.equals(blockHitResult.getSide()))) {
            velocity = new Vec3d(velocity.x * bounceDampenerHorizontal, (-velocity.y) * bounceDampenerVertical,
                    velocity.z * bounceDampenerHorizontal);
        } else {
            velocity = velocity.subtract(wallVector.multiply(velocity.dotProduct(wallVector) * 2));
            velocity = velocity.multiply(bounceDampenerHorizontal);
        }
        LoggerUtil.devLogger("Bounce! " + velocity.toString());
        this.setVelocity(velocity);
        this.velocityModified = true;

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.playSound(null, blockHitResult.getBlockPos(),
                    SoundEvents.BLOCK_BAMBOO_STEP, SoundCategory.NEUTRAL, 1.5f, 1f);

            if (getBounces() > 0) decrementBounces();
        }
    }
}
