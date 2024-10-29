package com.chailotl.fbombs;

import com.chailotl.fbombs.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class FBombsDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        var pack = fabricDataGenerator.createPack();
        pack.addProvider(ModelProvider::new);
        pack.addProvider(BlockLootTableProvider::new);
        pack.addProvider(TranslationProvider::new);
        pack.addProvider(AdvancementsProvider::new);
        pack.addProvider(RecipeProvider::new);
        TagProvider.registerAll(pack);
    }
}
