package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.SplitTntBlock;
import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;

public class ModelProvider extends FabricModelProvider {

    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // TODO: [Chai] unsure if we should assume all TNT blocks will have the same textured model
        //FBombsBlocks.streamTntBlocks().forEach(block -> blockStateModelGenerator.registerSingleton(block, TexturedModel.CUBE_BOTTOM_TOP));
        blockStateModelGenerator.registerSingleton(FBombsBlocks.INSTANT_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.SHORT_FUSE_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.LONG_FUSE_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.HIGH_POWER_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.LOW_POWER_TNT, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.FIRE_TNT, TexturedModel.CUBE_BOTTOM_TOP);

        //TODO: [ShiroJR] we could assume that all will use it by default and select the ones, which don't while streaming it
        /*FBombsBlocks.streamTntBlocks().forEach(block -> {
            var unsupported = List.of(FBombsBlocks.FIRE_TNT, FBombsBlocks.INSTANT_TNT);
            if (unsupported.contains(block)) return;
            blockStateModelGenerator.registerSingleton(block, TexturedModel.CUBE_BOTTOM_TOP);
        });*/

        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(FBombsBlocks.SPLIT_TNT)
                .coordinate(createSplitTntBlockState()));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(FBombsItems.DYNAMITE_STICK, Models.GENERATED);
    }

    private BlockStateVariantMap createSplitTntBlockState() {
        return BlockStateVariantMap.create(SplitTntBlock.Split.NE.getProperty(), SplitTntBlock.Split.SE.getProperty(),
                SplitTntBlock.Split.SW.getProperty(), SplitTntBlock.Split.NW.getProperty()
        ).register((ne, se, sw, nw) -> {
                    StringBuilder sb = new StringBuilder("block/split_tnt");
                    if (ne) sb.append("_ne");
                    if (se) sb.append("_se");
                    if (sw) sb.append("_sw");
                    if (nw) sb.append("_nw");
                    return BlockStateVariant.create().put(VariantSettings.MODEL, FBombs.getId(sb.toString()));
                }
        );
    }
}
