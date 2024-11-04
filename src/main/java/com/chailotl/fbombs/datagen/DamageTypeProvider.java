package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.init.FBombsDamageTypes;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.concurrent.CompletableFuture;

public class DamageTypeProvider extends FabricDynamicRegistryProvider {
    public DamageTypeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {
        FBombsDamageTypes.bootstrap(new Registerable<>() {
            @Override
            public RegistryEntry.Reference<DamageType> register(RegistryKey<DamageType> key, DamageType value, Lifecycle lifecycle) {
                return (RegistryEntry.Reference<DamageType>) entries.add(key, value);
            }

            @Override
            public <S> RegistryEntryLookup<S> getRegistryLookup(RegistryKey<? extends Registry<? extends S>> registryRef) {
                return registries.getWrapperOrThrow(registryRef);
            }
        });
    }

    @Override
    public String getName() {
        return "Damage Types";
    }
}
