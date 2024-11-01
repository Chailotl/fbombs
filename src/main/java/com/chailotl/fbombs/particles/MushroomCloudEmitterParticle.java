package com.chailotl.fbombs.particles;

import com.chailotl.fbombs.init.FBombsParticleTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.NoRenderParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class MushroomCloudEmitterParticle extends NoRenderParticle {
    private float height = 0;
    private float speed = 0.2f;

    protected MushroomCloudEmitterParticle(ClientWorld clientWorld, double x, double y, double z) {
        super(clientWorld, x, y, z, 0, 0, 0);
        this.maxAge = 20 * 40;
    }

    @Override
    public void tick() {
        double ratio = (double) this.age / this.maxAge;
        double radius = 4 + 14 * ratio;
        double y = this.y + 10f + 100f * (1 - Math.pow(1 - ratio, 3));

        for (int i = 0; i < 4; i++) {
            double x = this.x + (this.random.nextDouble() - this.random.nextDouble()) * radius;
            double z = this.z + (this.random.nextDouble() - this.random.nextDouble()) * radius;
            this.world.addParticle(FBombsParticleTypes.MUSHROOM_CLOUD_STEM, x, this.y, z, y + 5, 0.05 + 0.35 * this.random.nextFloat(), 0);
        }

        int count = 6;

        if (this.age > this.maxAge - 60) {
            count = (this.maxAge - this.age) / 10;
        }

        for (int i = 0; i < count; i++) {
            double angle = this.random.nextDouble() * 2 * Math.PI;
            double x = this.x + radius * 1.5 * Math.cos(angle);
            double z = this.z - radius * 1.5 * Math.sin(angle); // Stupid magic number #1
            this.world.addParticle(FBombsParticleTypes.MUSHROOM_CLOUD_CAP, x, y, z, angle, 0.5 + 1.5 * ratio, 0);
        }

        if (++this.age == this.maxAge) {
            this.markDead();
        }
    }

    private Vec3d randomPointInCircle(float radius) {
        double r = radius * Math.sqrt(this.random.nextDouble());
        double theta = this.random.nextDouble() * 2 * Math.PI;
        return new Vec3d(r * Math.cos(theta), 0, r * Math.sin(theta));
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