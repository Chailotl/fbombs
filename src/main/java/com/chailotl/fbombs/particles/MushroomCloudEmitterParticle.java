package com.chailotl.fbombs.particles;

import com.chailotl.fbombs.init.FBombsParticleTypes;
import com.chailotl.fbombs.init.FBombsSoundEvents;
import com.chailotl.fbombs.mixin.access.BillboardParticleAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.RaycastContext;

@Environment(EnvType.CLIENT)
public class MushroomCloudEmitterParticle extends NoRenderParticle {
    private final ParticleManager particleManager;
    private double delta = 0;
    private double height = 0;
    private double heat = 0;
    private Vec3d heatCenter;

    protected MushroomCloudEmitterParticle(ClientWorld clientWorld, double x, double y, double z) {
        super(clientWorld, x, y, z, 0, 0, 0);
        MinecraftClient client = MinecraftClient.getInstance();
        particleManager = client.particleManager;
        maxAge = 20 * 40;
        PositionedSoundInstance positionedSoundInstance = new PositionedSoundInstance(FBombsSoundEvents.NUCLEAR_EXPLOSION, SoundCategory.BLOCKS, 20, 1, Random.create(random.nextLong()), x, y, z);
        double d = client.gameRenderer.getCamera().getPos().squaredDistanceTo(x, y, z);
        double e = Math.sqrt(d) / 120;
        client.getSoundManager().play(positionedSoundInstance, (int)(e * 20));
    }

    @Override
    public void tick() {
        delta = (double) age / maxAge;
        double radius = 12 + 12 * delta;
        height = y + 10 + 100 * (1 - Math.pow(getReverseDelta(), 4));
        heat = 1.25 - 1 * delta; //Math.sqrt(1 - Math.pow(owner.getDelta() - 1, 2));
        heatCenter = new Vec3d(x, height, z);

        for (int i = 0; i < 6; i++) {
            double r = radius * random.nextDouble();
            double theta = random.nextDouble() * 2 * Math.PI;

            double x = this.x + r * Math.cos(theta);
            double z = this.z + r * Math.sin(theta);
            double yVel = 0.5 + 0.20 * this.random.nextFloat();
            if (age < 30) {
                yVel *= 1 + (30 - age) / 15f;
            }
            spawnParticle(x, this.y - 10, z, 0, yVel, 0, theta);
        }

        if (age < 30) {
            double r = age * 6;
            for (int i = 0; i < 48; i++) {
                double r2 = r + random.nextDouble() * 6 - 3;
                double theta = random.nextDouble() * 2 * Math.PI;
                Vec3d pos = new Vec3d(this.x + r2 * Math.cos(theta), this.y, this.z + r2 * Math.sin(theta));

                BlockHitResult blockHitResult = world.raycast(new RaycastContext(pos, pos.add(0, -30, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, ShapeContext.absent()));
                if (blockHitResult.getType() == HitResult.Type.BLOCK) {
                    pos = blockHitResult.getPos();
                    world.addParticle(FBombsParticleTypes.GROUND_SMOKE, pos.x, pos.y, pos.z, 0, 0.15, 0);
                }
            }
        }

        if (age < 40) {
            for (int i = 0; i < 12; i++) {
                double d = x + (random.nextDouble() - random.nextDouble()) * 32;
                double e = y + (random.nextDouble() - random.nextDouble()) * 32 + 16;
                double f = z + (random.nextDouble() - random.nextDouble()) * 32;
                BillboardParticleAccessor particle = (BillboardParticleAccessor) particleManager.addParticle(ParticleTypes.EXPLOSION, d, e, f, 0, 0, 0);
                if (particle != null) {
                    particle.setScale(12);
                }
            }
        }

        if (++age == maxAge) {
            markDead();
        }
    }

    public double getX() {
        return x;
    }

    public double getZ() {
        return z;
    }

    public double getDelta() {
        return delta;
    }

    public double getReverseDelta() {
        return 1 - delta;
    }

    public double getHeight() {
        return height;
    }

    public double getHeat() {
        return heat;
    }

    public Vec3d getHeatCenter() {
        return heatCenter;
    }

    private MushroomCloudSmokeParticle spawnParticle(double x, double y, double z, double velocityX, double velocityY, double velocityZ, double radians) {
        MushroomCloudSmokeParticle particle = (MushroomCloudSmokeParticle) particleManager.addParticle(FBombsParticleTypes.MUSHROOM_CLOUD_SMOKE, x, y, z, velocityX, velocityY, velocityZ);

        if (particle != null) {
            particle.owner = this;
            particle.yaw = (float) Math.toDegrees(radians);
        }

        return particle;
    }

    public void spawnDonutParticles(MushroomCloudSmokeParticle particle) {
        if (!isAlive()) { return; }

        double radius = 6 + 6 * delta;
        int count = age > 120 ? 3 : 6;

        for (int i = 0; i < count; i++) {
            double r = radius * random.nextDouble();
            double theta = random.nextDouble() * 2 * Math.PI;
            Vec3d point = new Vec3d(r * Math.cos(theta), 0, r * Math.sin(theta));

            double x = this.x + point.x;
            double z = this.z + point.z;
            var particle2 = spawnParticle(x, height, z, 0, 0, 0, theta);
            particle2.donut = true;
            particle2.speed = particle.speed;
        }
    }

    public static float[] convertTemperatureToRGB(double delta) {
        delta = delta * 2;
        double red = Math.clamp(delta, 0f, 1f);
        double green = Math.clamp(delta - 0.5f, 0f, 1f);
        double blue = Math.clamp(delta - 1f, 0f, 1f);

        return new float[] { (float) red, (float) green, (float) blue };
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        public Factory(SpriteProvider spriteProvider) {

        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double x, double y, double z, double xVel, double yVel, double zVel) {
            return new MushroomCloudEmitterParticle(clientWorld, x, y, z);
        }
    }
}