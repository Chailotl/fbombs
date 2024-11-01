package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FBombsParticleTypes {
    public static final SimpleParticleType MUSHROOM_CLOUD_EMITTER = register("mushroom_cloud_emitter", true);
    public static final SimpleParticleType MUSHROOM_CLOUD_STEM = register("mushroom_cloud_stem", true);
    public static final SimpleParticleType MUSHROOM_CLOUD_CAP = register("mushroom_cloud_cap", true);

    private static SimpleParticleType register(String name, boolean alwaysShow) {
        return Registry.register(Registries.PARTICLE_TYPE, FBombs.getId(name), FabricParticleTypes.simple(alwaysShow));
    }

    public static void initialize() {
        // static initialisation
    }
}