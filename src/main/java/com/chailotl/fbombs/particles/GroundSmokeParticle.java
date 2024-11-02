package com.chailotl.fbombs.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.Vec3d;

import java.util.List;

@Environment(EnvType.CLIENT)
public class GroundSmokeParticle extends SpriteBillboardParticle {
    protected final SpriteProvider provider;

    public GroundSmokeParticle(ClientWorld clientWorld, double x, double y, double z, double xVel, double yVel, double zVel, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z);
        provider = spriteProvider;
        setSprite(spriteProvider);
        collidesWithWorld = false;
        scale = 2;
        maxAge = random.nextBetween(300, 600);
        float f = 0.15f + random.nextFloat() * 0.15f;
        setColor(f, f, f);

        velocityX = xVel;
        velocityY = yVel;
        velocityZ = zVel;
    }

    @Override
    public void tick() {
        setSpriteForAge(provider);
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;
        if (age++ < maxAge) {
            velocityX *= 0.95;
            velocityY *= 0.95;
            velocityZ *= 0.95;
            move(velocityX, velocityY, velocityZ);
        } else {
            markDead();
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
            return new GroundSmokeParticle(clientWorld, x, y, z, xVel, yVel, zVel, spriteProvider);
        }
    }
}
