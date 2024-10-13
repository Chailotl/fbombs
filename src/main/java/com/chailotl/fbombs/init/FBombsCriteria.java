package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.advancement.UsedDynamiteStickCriterion;
import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@SuppressWarnings("SameParameterValue")
public class FBombsCriteria {
    public static final UsedDynamiteStickCriterion USED_DYNAMITE_STICK = register("used_dynamite_stick", new UsedDynamiteStickCriterion());


    private static <T extends Criterion<?>> T register(String name, T criterion) {
        return Registry.register(Registries.CRITERION, FBombs.getId(name), criterion);
    }

    public static void initialize() {
        // static initialisation
    }
}
