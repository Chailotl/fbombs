package com.chailotl.fbombs;

import com.chailotl.fbombs.datagen.*;
import com.chailotl.fbombs.init.FBombsDamageTypes;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;

public class FBombsDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(ModelProvider::new);
        pack.addProvider(BlockLootTableProvider::new);
        pack.addProvider(TranslationProvider::new);
        pack.addProvider(AdvancementsProvider::new);
        pack.addProvider(RecipeProvider::new);
        pack.addProvider(DamageTypeProvider::new);
        TagProvider.registerAll(pack);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        DataGeneratorEntrypoint.super.buildRegistry(registryBuilder);
        registryBuilder.addRegistry(RegistryKeys.DAMAGE_TYPE, FBombsDamageTypes::bootstrap);
    }
}
