package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import net.minecraft.entity.damage.DamageEffects;
import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class FBombsDamageTypes {
    public static final RegistryKey<DamageType> NUCLEAR_EXPLOSION = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, FBombs.getId("nuclear_explosion"));

    public static void bootstrap(Registerable<DamageType> damageTypeRegisterable) {
        damageTypeRegisterable.register(NUCLEAR_EXPLOSION, new DamageType("nuclear_explosion", DamageScaling.ALWAYS, 0.1f, DamageEffects.BURNING));
    }
}