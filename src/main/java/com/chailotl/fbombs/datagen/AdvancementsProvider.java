package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.advancement.SplitTntBlockCriterion;
import com.chailotl.fbombs.advancement.UsedDynamiteCriterion;
import com.chailotl.fbombs.advancement.WaterloggedTntBlockCriterion;
import com.chailotl.fbombs.init.FBombsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancement.*;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class AdvancementsProvider extends FabricAdvancementProvider {
    public AdvancementsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        AdvancementEntry splitTntAdvancement = Advancement.Builder.create()
            .display(Blocks.TNT.asItem(),
                Text.translatable("advancement.fbombs.split_tnt_block"),
                Text.translatable("advancement.fbombs.split_tnt_block.desc"),
                Identifier.ofVanilla("textures/gui/advancements/backgrounds/stone.png"),
                AdvancementFrame.TASK, true, false, false)
            .criterion("split_tnt_block", SplitTntBlockCriterion.Conditions.any()).sendsTelemetryEvent()
            .build(consumer, "%s/split_tnt_block".formatted(FBombs.MOD_ID));

        AdvancementEntry dynamiteAdvancement = Advancement.Builder.create()
            .parent(splitTntAdvancement)
            .display(FBombsItems.DYNAMITE,
                Text.translatable("advancement.fbombs.explodification"),
                Text.translatable("advancement.fbombs.explodification.desc"),
                null,
                AdvancementFrame.TASK, true, false, true)
            .criterion("explodification", UsedDynamiteCriterion.Conditions.any())
            .build(consumer, "%s/dynamite".formatted(FBombs.MOD_ID));

        AdvancementEntry waterloggedTntBlockAdvancement = Advancement.Builder.create()
            .parent(splitTntAdvancement)
            .display(Items.WATER_BUCKET,
                Text.translatable("advancement.fbombs.waterlogged_tnt_block"),
                Text.translatable("advancement.fbombs.waterlogged_tnt_block.desc"),
                null,
                AdvancementFrame.TASK, true, false, true)
            .criterion("waterlogged_tnt_block", WaterloggedTntBlockCriterion.Conditions.any())
            .build(consumer, "%s/waterlogged_tnt_block".formatted(FBombs.MOD_ID));

        AdvancementEntry bottleCapQuest = Advancement.Builder.create()
            .parent(splitTntAdvancement)
            .display(FBombsItems.NUCLEAR_LAUNCH_KEY,
                Text.translatable("advancement.fbombs.bottle_cap_quest"),
                Text.translatable("advancement.fbombs.bottle_cap_quest.desc"),
                null,
                AdvancementFrame.GOAL, true, true, true)
            .criteriaMerger(AdvancementRequirements.CriterionMerger.AND)
            .criterion("1", InventoryChangedCriterion.Conditions.items(FBombsItems.COLA_BOTTLE_CAP))
            .criterion("2", InventoryChangedCriterion.Conditions.items(FBombsItems.ROOT_BEER_BOTTLE_CAP))
            .criterion("3", InventoryChangedCriterion.Conditions.items(FBombsItems.CREAM_SODA_BOTTLE_CAP))
            .criterion("4", InventoryChangedCriterion.Conditions.items(FBombsItems.GINGER_ALE_BOTTLE_CAP))
            .criterion("5", InventoryChangedCriterion.Conditions.items(FBombsItems.LEMON_LIME_SODA_BOTTLE_CAP))
            .criterion("6", InventoryChangedCriterion.Conditions.items(FBombsItems.BLUEBERRY_SODA_BOTTLE_CAP))
            .criterion("7", InventoryChangedCriterion.Conditions.items(FBombsItems.CHERRY_SODA_BOTTLE_CAP))
            .criterion("8", InventoryChangedCriterion.Conditions.items(FBombsItems.ORANGE_SODA_BOTTLE_CAP))
            .criterion("9", InventoryChangedCriterion.Conditions.items(FBombsItems.SARSPARILLA_BOTTLE_CAP))
            .rewards(AdvancementRewards.Builder.recipe(Registries.ITEM.getId(FBombsItems.NUCLEAR_LAUNCH_KEY)))
            .build(consumer, "%s/bottle_cap_quest".formatted(FBombs.MOD_ID));
    }
}
