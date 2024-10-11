package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsItemGroups;
import com.chailotl.fbombs.init.FBombsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class TranslationProvider extends FabricLanguageProvider {
    public TranslationProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(FBombsItemGroups.BLOCKS.getTranslationKey(), "FBombs " + cleanString(Registries.ITEM_GROUP.getId(FBombsItemGroups.BLOCKS.get())));
        translationBuilder.add(FBombsItemGroups.ITEMS.getTranslationKey(), "FBombs " + cleanString(Registries.ITEM_GROUP.getId(FBombsItemGroups.ITEMS.get())));

        translationBuilder.add(FBombsBlocks.TEST_BLOCK, cleanString(Registries.BLOCK.getId(FBombsBlocks.TEST_BLOCK)));

        translationBuilder.add(FBombsItems.TEST_ITEM, cleanString(Registries.ITEM.getId(FBombsItems.TEST_ITEM)));

        // Load an existing language file.
        try {
            Path existingFilePath = dataOutput.getModContainer().findPath("assets/%s/lang/en_us.existing.json".formatted(FBombs.MOD_ID)).orElseThrow();
            translationBuilder.add(existingFilePath);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add existing language file!", e);
        }
    }

    @NotNull
    public static String cleanString(@Nullable Identifier identifier) {
        if (identifier == null) {
            throw new NullPointerException("missing Identifier for translation datagen");
        }
        String[] words = List.of(identifier.getPath().split("/")).getLast().split("_");
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            char capitalized = Character.toUpperCase(word.charAt(0));
            output.append(capitalized).append(word.substring(1));
            if (i < words.length - 1) {
                output.append(" ");
            }
        }
        return output.toString();
    }
}
