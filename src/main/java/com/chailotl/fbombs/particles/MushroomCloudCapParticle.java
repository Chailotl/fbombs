package com.chailotl.fbombs.particles;

import com.chailotl.fbombs.FBombs;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class MushroomCloudCapParticle extends SpriteBillboardParticle {
    protected final SpriteProvider provider;
    private final float yaw;
    private final float size;

    public MushroomCloudCapParticle(ClientWorld clientWorld, double x, double y, double z, double yaw, double size, double zVel, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z);
        setSprite(spriteProvider);
        this.provider = spriteProvider;
        this.scale = 8;
        this.maxAge = this.random.nextInt(90) + 270;
        float f = this.random.nextFloat() * 0.3f;
        this.setColor(f, f, f);

        this.gravityStrength = -0.0025f;
        this.velocityX = 0;
        this.velocityY = 0;
        this.velocityZ = 0;

        this.yaw = (float) yaw;
        this.size = (float) size;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;

        float pitch = ++this.age * 3;// / size;
        if (pitch > this.maxAge) { this.markDead(); }
        float yaw = this.yaw + (float) Math.PI * 1.5f; // Stupid magic number #2
        Vec3d vel = Vec3d.fromPolar(pitch, 0).rotateZ(yaw).multiply(0.5 * size);
        this.velocityX = vel.x;
        this.velocityZ = vel.y;
        this.velocityY = vel.z;
        this.move(this.velocityX, this.velocityY, this.velocityZ);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double x, double y, double z, double xVel, double yVel, double zVel) {
            return new MushroomCloudCapParticle(clientWorld, x, y, z, xVel, yVel, zVel, spriteProvider);
        }
    }
}
