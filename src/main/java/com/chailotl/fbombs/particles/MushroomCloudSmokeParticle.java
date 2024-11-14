package com.chailotl.fbombs.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class MushroomCloudSmokeParticle extends SpriteBillboardParticle {
    public static final ParticleTextureSheet PARTICLE_SHEET_OPAQUE_NO_FOG = new ParticleTextureSheet() {
        @Override
        public BufferBuilder begin(Tessellator tessellator, TextureManager textureManager) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getParticleProgram);
            RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
            RenderSystem.setShaderFogEnd(1024);
            return tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
        }

        public String toString() {
            return "PARTICLE_SHEET_OPAQUE_NO_FOG";
        }
    };

    private final SpriteProvider provider;
    public MushroomCloudEmitterParticle owner;
    public boolean donut = false;
    public double speed = 0;
    private float pitch = 0;
    public float yaw = 0;
    private final float shade;

    public MushroomCloudSmokeParticle(ClientWorld clientWorld, double x, double y, double z, double xVel, double yVel, double zVel, SpriteProvider spriteProvider) {
        super(clientWorld, x, y, z);
        provider = spriteProvider;
        setSprite(spriteProvider);
        collidesWithWorld = false;
        scale = 4;
        maxAge = random.nextBetween(0, 200);
        shade = random.nextFloat() * 0.15f;
        alpha = 0;

        velocityX = xVel;
        velocityY = yVel;
        velocityZ = zVel;
        speed = yVel;
    }

    @Override
    public void tick() {
        prevPosX = x;
        prevPosY = y;
        prevPosZ = z;

        if (owner == null) {
            markDead();
            return;
        }

        if (!owner.isAlive()) {
            if (age++ >= maxAge) {
                markDead();
            } else {
                speed /= 1.01;
            }
        }

        if (!donut && y > owner.getHeight()) {
            donut = true;
            owner.spawnDonutParticles(this);
        }

        if (donut) {
            if (speed > 1 - 0.5 * owner.getDelta()) {
                speed /= 1.01;
            }

            Vec3d vel = Vec3d.fromPolar(pitch + 270, yaw - 90).multiply(speed);
            pitch += (float) ((7 - 5 * Math.pow(owner.getDelta(), 0.5)) * speed);
            if (pitch > 360) { markDead(); }
            velocityX = vel.x;
            velocityY = vel.y;
            velocityZ = vel.z;
        } else {
            speed *= 1 + (0.01 * owner.getReverseDelta());
            velocityX = Math.min(0.0, (owner.getX() - x) / 240f);
            velocityY = speed;
            velocityZ = Math.min(0.0, (owner.getZ() - z) / 240f);
        }

        move(velocityX, velocityY, velocityZ);
        updateColor();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void updateColor() {
        double distance = owner.getHeatCenter().distanceTo(new Vec3d(x, y, z));
        alpha = 1;
        float[] rgb = MushroomCloudEmitterParticle.convertTemperatureToRGB(owner.getHeat() - distance / 180f);
        float shade = (float) Math.max(0, this.shade - owner.getHeat() / 8f);
        //float shade = this.shade * (float) Math.clamp(1 - owner.getHeat() / 2, 0, 1);
        setColor(
            Math.max(0.15f, rgb[0]) - shade,
            Math.max(0.15f, rgb[1]) - shade,
            Math.max(0.15f, rgb[2]) - shade
        );
    }

    @Override
    protected int getBrightness(float tint) {
        return 15728880;
    }

    @Override
    public ParticleTextureSheet getType() {
        return PARTICLE_SHEET_OPAQUE_NO_FOG;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleFactory<SimpleParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double x, double y, double z, double xVel, double yVel, double zVel) {
            return new MushroomCloudSmokeParticle(clientWorld, x, y, z, xVel, yVel, zVel, spriteProvider);
        }
    }
}
