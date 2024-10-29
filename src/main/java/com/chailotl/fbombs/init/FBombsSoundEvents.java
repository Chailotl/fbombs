package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

@SuppressWarnings({"SameParameterValue", "unused"})
public class FBombsSoundEvents {
    public static final SoundEvent RADIOACTIVE_NOISE = register("radioactive_noise");
    public static final SoundEvent DYNAMITE_EXPLOSION = register("dynamite_explosion");

    private static SoundEvent register(String name) {
        Identifier identifier = FBombs.getId(name);
        SoundEvent soundEvent = SoundEvent.of(identifier);
        return Registry.register(Registries.SOUND_EVENT, identifier, soundEvent);
    }

    public static void initialize() {
        // static initialisation
    }
}
