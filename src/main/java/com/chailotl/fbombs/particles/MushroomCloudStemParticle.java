package com.chailotl.fbombs.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

@Environment(EnvType.CLIENT)
public class MushroomCloudStemParticle extends SpriteBillboardParticle {
    protected final SpriteProvider provider;
    private final double maxY;

    public MushroomCloudStemParticle(ClientWorld clientWorld, double x, double y, double z, double xVel, double yVel, double zVel, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z);
        setSprite(spriteProvider);
        this.provider = spriteProvider;
        this.scale = 8;
        this.maxAge = this.random.nextInt(50) + 100;
        float f = this.random.nextFloat() * 0.3f;
        this.setColor(f, f, f);

        this.gravityStrength = -0.0025f;
        this.velocityX = 0;
        this.velocityY = yVel;
        this.velocityZ = 0;
        this.maxY = xVel;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        if (this.age++ < this.maxAge && this.y < this.maxY) {
            //this.velocityY = this.velocityY - (double)this.gravityStrength;
            this.velocityY *= 1.01;
            this.move(this.velocityX, this.velocityY, this.velocityZ);
        } else {
            this.markDead();
        }
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
            return new MushroomCloudStemParticle(clientWorld, x, y, z, xVel, yVel, zVel, spriteProvider);
        }
    }
}
