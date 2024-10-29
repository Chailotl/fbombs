package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FBombsParticleTypes {
    public static final SimpleParticleType FAST_FLAME = register("fast_flame", false);
    public static final SimpleParticleType FAST_SMALL_FLAME = register("fast_small_flame", false);

    private static SimpleParticleType register(String name, boolean alwaysShow) {
        return Registry.register(Registries.PARTICLE_TYPE, FBombs.getId(name), FabricParticleTypes.simple(alwaysShow));
    }

    public static void initialize() {
        // static initialisation
    }
}