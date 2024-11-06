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
import java.util.regex.Pattern;

public class TranslationProvider extends FabricLanguageProvider {
    private static List<String> CAPITALIZED_WORDS = List.of("TNT", "ACME");

    public TranslationProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(FBombsTags.Items.SPLITS_TNT, "Splits TNT");
        translationBuilder.add(FBombsTags.Items.IGNITES_TNT, "Ignites TNT");
        translationBuilder.add(FBombsTags.Blocks.TNT_VARIANTS, "TNT Variants");
        translationBuilder.add(FBombsTags.Blocks.VOLUMETRIC_EXPLOSION_IMMUNE, "Volumetric Explosion Immune");
        translationBuilder.add(FBombsTags.Blocks.TRANSMITS_REDSTONE_POWER, "Transmits Redstone Power for Sirens");

        FBombsItemGroups.ItemGroupEntry.ALL_GROUPS.forEach(itemGroupEntry -> translationBuilder.add(itemGroupEntry.getTranslationKey(), "FBombs"));

        FBombs.streamEntries(Registries.ENTITY_TYPE).forEach(entityType -> translationBuilder.add(entityType, cleanString(entityType)));
        FBombs.streamEntries(Registries.STATUS_EFFECT).forEach(statusEffect -> translationBuilder.add(statusEffect, cleanString(statusEffect)));
        FBombs.streamEntries(Registries.ITEM, item -> !(item instanceof BlockItem)).forEach(item -> translationBuilder.add(item, cleanString(item)));
        FBombs.streamEntries(Registries.BLOCK).forEach(block -> translationBuilder.add(block, cleanString(block)));

        translationBuilder.add("sound.fbombs.radioactive_noise", "Radioactive noise");
        translationBuilder.add("sound.fbombs.dynamite_explosion", "Dynamite explosion");
        translationBuilder.add("sound.fbombs.nuclear_explosion", "Nuclear explosion");
        translationBuilder.add("sound.fbombs.nuclear_siren", "Nuclear siren");


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
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            char capitalized = Character.toUpperCase(word.charAt(0));
            stringBuilder.append(capitalized).append(word.substring(1));
            if (i < words.length - 1) {
                stringBuilder.append(" ");
            }
        }

        String string = stringBuilder.toString();

        for (String word : CAPITALIZED_WORDS) {
            string = Pattern.compile("\\b" + word + "\\b", Pattern.CASE_INSENSITIVE)
                .matcher(string)
                .replaceAll(word.toUpperCase());
        }

        return string;
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
