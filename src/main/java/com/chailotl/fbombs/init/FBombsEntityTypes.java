package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.entity.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FBombsEntityTypes {
    private static final List<EntityType<?>> VALUES = new ArrayList<>();
    private static final List<EntityType<? extends AbstractTntEntity>> TNT_ENTITY_TYPES = new ArrayList<>();

    public static final EntityType<InstantTntEntity> INSTANT_TNT = registerTnt("instant_tnt", InstantTntEntity::new);
    public static final EntityType<ShortFuseTntEntity> SHORT_FUSE_TNT = registerTnt("short_fuse_tnt", ShortFuseTntEntity::new);
    public static final EntityType<LongFuseTntEntity> LONG_FUSE_TNT = registerTnt("long_fuse_tnt", LongFuseTntEntity::new);
    public static final EntityType<HighPowerTntEntity> HIGH_POWER_TNT = registerTnt("high_power_tnt", HighPowerTntEntity::new);
    public static final EntityType<LowPowerTntEntity> LOW_POWER_TNT = registerTnt("low_power_tnt", LowPowerTntEntity::new);
    public static final EntityType<FireTntEntity> FIRE_TNT = registerTnt("fire_tnt", FireTntEntity::new);
    public static final EntityType<SplitTntEntity> SPLIT_TNT = registerTnt("split_tnt", SplitTntEntity::new);

    public static final EntityType<DynamiteStickEntity> DYNAMITE_STICK = register(
        "dynamite_stick",
        EntityType.Builder.<DynamiteStickEntity>create(DynamiteStickEntity::new, SpawnGroup.MISC)
            .makeFireImmune()
            .dimensions(0.25F, 0.25F)
            .maxTrackingRange(4)
            .trackingTickInterval(10)
    );

    private static <T extends Entity> EntityType<T> register(String name, EntityType.Builder<T> type) {
        EntityType<T> entityType = type.build(name);
        VALUES.add(entityType);
        return Registry.register(Registries.ENTITY_TYPE, FBombs.getId(name), entityType);
    }

    private static <T extends AbstractTntEntity> EntityType<T> registerTnt(String name, EntityType.EntityFactory<T> factory) {
        EntityType<T> entityType = EntityType.Builder.create(factory, SpawnGroup.MISC)
            .makeFireImmune()
            .dimensions(0.98F, 0.98F)
            .eyeHeight(0.15F)
            .maxTrackingRange(10)
            .trackingTickInterval(10)
            .build(name);
        VALUES.add(entityType);
        TNT_ENTITY_TYPES.add(entityType);
        return Registry.register(Registries.ENTITY_TYPE, FBombs.getId(name), entityType);
    }

    public static void initialize() {
        // static initialisation
    }

    public static Stream<EntityType<?>> stream() {
        return VALUES.stream();
    }

    public static Stream<EntityType<? extends AbstractTntEntity>> streamTntEntityTypes() {
        return TNT_ENTITY_TYPES.stream();
    }
}