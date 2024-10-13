package com.chailotl.fbombs.init;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

@SuppressWarnings("SameParameterValue")
public class FBombsGamerules {
    public static GameRules.Key<GameRules.BooleanRule> ALLOW_VOLUMETRIC_EXPLOSION_DAMAGE =
            register("allowVolumetricExplosionDamage", GameRules.Category.MISC,
                    GameRuleFactory.createBooleanRule(true));


    private static <T extends GameRules.Rule<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> test) {
        return GameRuleRegistry.register(name, category, test);
    }

    public static void initialize() {
        // static initialisation
    }
}
