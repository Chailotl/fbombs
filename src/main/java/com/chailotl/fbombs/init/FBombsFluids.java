package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.fluid.JuiceThatMakesYouExplodeFluid;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FBombsFluids {
    public static final JuiceThatMakesYouExplodeFluid JUICE_THAT_MAKES_YOU_EXPLODE = register("juice_that_makes_you_explode", new JuiceThatMakesYouExplodeFluid.Still());
    public static final JuiceThatMakesYouExplodeFluid FLOWING_JUICE_THAT_MAKES_YOU_EXPLODE = register("flowing_juice_that_makes_you_explode", new JuiceThatMakesYouExplodeFluid.Flowing());

    private static <T extends FlowableFluid> T register(String name, T fluid) {
        return Registry.register(Registries.FLUID, FBombs.getId(name), fluid);
    }

    public static void initialize() {
        // static initialisation
    }
}