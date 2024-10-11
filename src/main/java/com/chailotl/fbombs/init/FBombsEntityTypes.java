package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.entity.DynamiteStickEntity;
import com.chailotl.fbombs.entity.InstantTntEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FBombsEntityTypes {
    public static final EntityType<InstantTntEntity> INSTANT_TNT = register(
        "instant_tnt",
        EntityType.Builder.<InstantTntEntity>create(InstantTntEntity::new, SpawnGroup.MISC)
            .makeFireImmune()
            .dimensions(0.98F, 0.98F)
            .eyeHeight(0.15F)
            .maxTrackingRange(10)
            .trackingTickInterval(10)
    );

    public static final EntityType<DynamiteStickEntity> DYNAMITE_STICK = register(
            "dynamite_stick",
            EntityType.Builder.<DynamiteStickEntity>create(DynamiteStickEntity::new, SpawnGroup.MISC)
                    .makeFireImmune()
                    .dimensions(0.25F, 0.25F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(10)
    );

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> type) {
        return Registry.register(Registries.ENTITY_TYPE, FBombs.getId(name), type.build(name));
    }

    public static void initialize() {
        // static initialisation
    }
}