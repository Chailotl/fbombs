package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.AcmeBedBlock;
import com.chailotl.fbombs.block.SplitTntBlock;
import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;

import java.util.List;

public class ModelProvider extends FabricModelProvider {

    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        blockStateModelGenerator.registerSingleton(FBombsBlocks.INSTANT_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.SHORT_FUSE_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.LONG_FUSE_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.HIGH_POWER_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.LOW_POWER_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.FIRE_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.CONCUSSIVE_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.WIND_CHARGED_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.UNDERWATER_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.SPONGE_BOMB, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.SHAPED_CHARGE, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.MINING_CHARGE, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.LEVITATING_TNT, TexturedModel.CUBE_BOTTOM_TOP);

        blockStateModelGenerator.registerBuiltin(FBombs.getId("block/acme_bed"), Blocks.OAK_PLANKS)
            .includeWithoutItem(
                FBombs.streamEntries(Registries.BLOCK, block -> block instanceof AcmeBedBlock).toArray(Block[]::new)
            );
        blockStateModelGenerator.registerBed(FBombsBlocks.WHITE_ACME_BED, Blocks.WHITE_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.ORANGE_ACME_BED, Blocks.ORANGE_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.MAGENTA_ACME_BED, Blocks.MAGENTA_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.LIGHT_BLUE_ACME_BED, Blocks.LIGHT_BLUE_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.YELLOW_ACME_BED, Blocks.YELLOW_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.LIME_ACME_BED, Blocks.LIME_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.PINK_ACME_BED, Blocks.PINK_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.GRAY_ACME_BED, Blocks.GRAY_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.LIGHT_GRAY_ACME_BED, Blocks.LIGHT_GRAY_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.CYAN_ACME_BED, Blocks.CYAN_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.PURPLE_ACME_BED, Blocks.PURPLE_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.BLUE_ACME_BED, Blocks.BLUE_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.BROWN_ACME_BED, Blocks.BROWN_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.GREEN_ACME_BED, Blocks.GREEN_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.RED_ACME_BED, Blocks.RED_WOOL);
        blockStateModelGenerator.registerBed(FBombsBlocks.BLACK_ACME_BED, Blocks.BLACK_WOOL);

        //TODO: [ShiroJR] we could assume that all will use it by default and select the ones, which don't while streaming it
        /*FBombsBlocks.streamTntBlocks().forEach(block -> {
            var unsupported = List.of(FBombsBlocks.FIRE_TNT, FBombsBlocks.INSTANT_TNT);
            if (unsupported.contains(block)) return;
            blockStateModelGenerator.registerSingleton(block, TexturedModel.CUBE_BOTTOM_TOP);
        });*/

        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(FBombsBlocks.SPLIT_TNT)
            .coordinate(createSplitTntBlockState()));
        blockStateModelGenerator.blockStateCollector.accept(
            BlockStateModelGenerator.createSlabBlockState(FBombsBlocks.TNT_SLAB,
                FBombs.getId("block/tnt_slab_bottom"),
                FBombs.getId("block/tnt_slab_top"),
                FBombs.getId("block/tnt_slab_double")
            )
        );

        //TODO: [ShiroJR] apply proper block item model
        blockStateModelGenerator.registerParentedItemModel(FBombsBlocks.TNT_SLAB.asItem(), FBombs.getId("block/tnt_slab_bottom"));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        List<Item> unsupported = List.of();

        FBombs.streamEntries(Registries.ITEM).forEach(item -> {
            if (item instanceof BlockItem || unsupported.contains(item)) { return; }
            itemModelGenerator.register(item, Models.GENERATED);
        });
    }

    private BlockStateVariantMap createSplitTntBlockState() {
        return BlockStateVariantMap.create(
            SplitTntBlock.Split.NE.getProperty(),
            SplitTntBlock.Split.SE.getProperty(),
            SplitTntBlock.Split.SW.getProperty(),
            SplitTntBlock.Split.NW.getProperty()
        ).register((ne, se, sw, nw) -> {
            StringBuilder sb = new StringBuilder("block/split_tnt");
            if (ne) sb.append("_ne");
            if (se) sb.append("_se");
            if (sw) sb.append("_sw");
            if (nw) sb.append("_nw");
            return BlockStateVariant.create().put(VariantSettings.MODEL, FBombs.getId(sb.toString()));
        });
    }
}
