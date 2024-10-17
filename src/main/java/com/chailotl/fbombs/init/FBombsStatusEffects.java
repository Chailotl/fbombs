package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.effect.RadiationPoisoningStatusEffect;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Colors;

@SuppressWarnings("SameParameterValue")
public class FBombsStatusEffects {
    public static final RegistryEntry<StatusEffect> RADIATION_POISONING = register("radiation_poisoning",
            new RadiationPoisoningStatusEffect(StatusEffectCategory.HARMFUL, Colors.GREEN));


    private static <T extends StatusEffect> RegistryEntry<StatusEffect> register(String name, T effect) {
        return Registry.registerReference(Registries.STATUS_EFFECT, FBombs.getId(name), effect);
    }

    public static void initialize() {
        // static initialisation
    }
}
