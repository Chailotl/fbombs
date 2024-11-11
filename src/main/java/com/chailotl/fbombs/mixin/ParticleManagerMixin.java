package com.chailotl.fbombs.mixin;

import com.chailotl.fbombs.particles.MushroomCloudSmokeParticle;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Final
    @Shadow
    @Mutable
    private static List<ParticleTextureSheet> PARTICLE_TEXTURE_SHEETS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void injectNoFogSheet(CallbackInfo ci) {
        PARTICLE_TEXTURE_SHEETS = ImmutableList.<ParticleTextureSheet>builder()
            .addAll(PARTICLE_TEXTURE_SHEETS)
            .add(MushroomCloudSmokeParticle.PARTICLE_SHEET_OPAQUE_NO_FOG)
            .build();
    }
}