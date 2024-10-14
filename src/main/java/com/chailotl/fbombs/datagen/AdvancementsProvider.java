package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.advancement.SplitTntBlockCriterion;
import com.chailotl.fbombs.advancement.UsedDynamiteStickCriterion;
import com.chailotl.fbombs.advancement.WaterloggedTntBlockCriterion;
import com.chailotl.fbombs.init.FBombsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
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

        AdvancementEntry dynamiteStickAdvancement = Advancement.Builder.create()
                .parent(splitTntAdvancement)
                .display(FBombsItems.DYNAMITE_STICK,
                        Text.translatable("advancement.fbombs.explodification"),
                        Text.translatable("advancement.fbombs.explodification.desc"),
                        null,
                        AdvancementFrame.TASK, true, true, true)
                .criterion("explodification", UsedDynamiteStickCriterion.Conditions.any())
                .rewards(AdvancementRewards.Builder.experience(50))
                .build(consumer, "%s/dynamite_stick".formatted(FBombs.MOD_ID));

        AdvancementEntry waterloggedTntBlockAdvancement = Advancement.Builder.create()
                .parent(splitTntAdvancement)
                .display(Items.WATER_BUCKET,
                        Text.translatable("advancement.fbombs.waterlogged_tnt_block"),
                        Text.translatable("advancement.fbombs.waterlogged_tnt_block.desc"),
                        null,
                        AdvancementFrame.TASK, true, true, true)
                .criterion("waterlogged_tnt_block", WaterloggedTntBlockCriterion.Conditions.any())
                .rewards(AdvancementRewards.Builder.experience(50))
                .build(consumer, "%s/waterlogged_tnt_block".formatted(FBombs.MOD_ID));
    }
}
