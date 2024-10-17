package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.init.FBombsItemGroups;
import com.chailotl.fbombs.init.FBombsTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
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
        translationBuilder.add(FBombsTags.Items.SPLITS_TNT, "Splits TNT");
        translationBuilder.add(FBombsTags.Items.IGNITES_TNT, "Ignites TNT");
        translationBuilder.add(FBombsTags.Blocks.TNT_VARIANTS, "TNT Variants");
        translationBuilder.add(FBombsTags.Blocks.VOLUMETRIC_EXPLOSION_IMMUNE, "Volumetric Explosion Immune");

        FBombsItemGroups.ItemGroupEntry.ALL_GROUPS.forEach(itemGroupEntry ->
                translationBuilder.add(itemGroupEntry.getTranslationKey(), "FBombs " + cleanString(itemGroupEntry.get())));

        FBombs.streamEntries(Registries.ENTITY_TYPE).forEach(entityType -> translationBuilder.add(entityType, cleanString(entityType)));
        FBombs.streamEntries(Registries.STATUS_EFFECT).forEach(statusEffect -> translationBuilder.add(statusEffect, cleanString(statusEffect)));
        FBombs.streamEntries(Registries.ITEM, item -> !(item instanceof BlockItem)).forEach(item -> translationBuilder.add(item, cleanString(item)));
        FBombs.streamEntries(Registries.BLOCK).forEach(block -> translationBuilder.add(block, cleanString(block)));


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

    @NotNull
    public static String cleanString(ItemGroup itemGroup) {
        return cleanString(Registries.ITEM_GROUP.getId(itemGroup));
    }

    @NotNull
    public static String cleanString(Block block) {
        return cleanString(Registries.BLOCK.getId(block));
    }

    @NotNull
    public static String cleanString(Item item) {
        return cleanString(Registries.ITEM.getId(item));
    }

    @NotNull
    public static String cleanString(EntityType<?> entityType) {
        return cleanString(Registries.ENTITY_TYPE.getId(entityType));
    }

    @NotNull
    public static String cleanString(StatusEffect statusEffect) {
        return cleanString(Registries.STATUS_EFFECT.getId(statusEffect));
    }
}
