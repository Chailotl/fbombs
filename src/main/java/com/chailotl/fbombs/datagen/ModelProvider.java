package com.chailotl.fbombs.datagen;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.AcmeBedBlock;
import com.chailotl.fbombs.block.DetonatorBlock;
import com.chailotl.fbombs.block.GenericTntBlock;
import com.chailotl.fbombs.block.SplitTntBlock;
import com.chailotl.fbombs.init.FBombsBlocks;
import com.chailotl.fbombs.init.FBombsItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.*;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.state.property.Properties;

import java.util.List;

public class ModelProvider extends FabricModelProvider {

    public ModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        List<Block> unsupported = List.of(
                FBombsBlocks.TNT_SLAB,
                FBombsBlocks.SPLIT_TNT,
                FBombsBlocks.SHAPED_CHARGE,
                FBombsBlocks.MINING_CHARGE,
                FBombsBlocks.FIREWORK_TNT,
                FBombsBlocks.DETONATOR
        );

        FBombs.streamEntries(Registries.BLOCK).forEach(block -> {
            if (!(block instanceof GenericTntBlock) || unsupported.contains(block)) {
                return;
            }
            blockStateModelGenerator.registerSingleton(block, TexturedModel.CUBE_BOTTOM_TOP);
        });

        blockStateModelGenerator.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(FBombsBlocks.SIREN_BASE, FBombs.getId("block/siren_base")));
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(FBombsBlocks.SIREN_POLE).coordinate(createPowerableSirenPole("siren_pole")));
        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(FBombsBlocks.SIREN_HEAD).coordinate(createPowerableSirenPole("siren_pole")));
        blockStateModelGenerator.excludeFromSimpleItemModelGeneration(FBombsBlocks.SIREN_HEAD);

        blockStateModelGenerator.blockStateCollector.accept(VariantsBlockStateSupplier.create(FBombsBlocks.DETONATOR)
                .coordinate(createPressableDetonator())
                .coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates()));
        blockStateModelGenerator.registerParentedItemModel(FBombsBlocks.DETONATOR, FBombs.getId("block/detonator"));

        blockStateModelGenerator.registerSingleton(FBombsBlocks.SHAPED_CHARGE, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.MINING_CHARGE, TexturedModel.CUBE_BOTTOM_TOP);
        registerFireworkTnt(blockStateModelGenerator);

        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier.create(FBombsBlocks.SPLIT_TNT).coordinate(createSplitTntBlockState())
        );

        blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createSlabBlockState(FBombsBlocks.TNT_SLAB,
                        FBombs.getId("block/tnt_slab_bottom"),
                        FBombs.getId("block/tnt_slab_top"),
                        FBombs.getId("block/tnt_slab_double")
                )
        );
        blockStateModelGenerator.registerParentedItemModel(FBombsBlocks.TNT_SLAB, FBombs.getId("block/tnt_slab_bottom"));

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

        blockStateModelGenerator.registerAxisRotated(FBombsBlocks.EXPOSED_CORRUGATED_IRON, TexturedModel.CUBE_ALL);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.EXPOSED_IRON_PLATE, TexturedModel.CUBE_ALL);
        blockStateModelGenerator.registerSingleton(FBombsBlocks.EXPOSED_CHAINLINK, TexturedModel.CUBE_ALL);
    }

    @SuppressWarnings("ConstantValue")
    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        List<Item> unsupported = List.of();

        FBombs.streamEntries(Registries.ITEM).forEach(item -> {
            if (item instanceof BlockItem || item instanceof ArmorItem || unsupported.contains(item)) {
                return;
            }
            itemModelGenerator.register(item, Models.GENERATED);
        });
        itemModelGenerator.registerArmor(FBombsItems.HAZMAT_HELMET);
        itemModelGenerator.registerArmor(FBombsItems.HAZMAT_CHESTPLATE);
        itemModelGenerator.registerArmor(FBombsItems.HAZMAT_LEGGINGS);
        itemModelGenerator.registerArmor(FBombsItems.HAZMAT_BOOTS);
    }

    private BlockStateVariantMap createPowerableSirenPole(String name) {
        return BlockStateVariantMap.create(Properties.POWERED).register(isPowered -> {
            String path = "block/" + name;
            if (isPowered) path = path.concat("_active");
            return BlockStateVariant.create().put(VariantSettings.MODEL, FBombs.getId(path));
        });
    }

    private BlockStateVariantMap createPressableDetonator() {
        return BlockStateVariantMap.create(DetonatorBlock.IS_PRESSED).register(isPressed -> {
            String path = "block/detonator";
            if (isPressed) path = path.concat("_pressed");
            return BlockStateVariant.create().put(VariantSettings.MODEL, FBombs.getId(path));
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

    private void registerFireworkTnt(BlockStateModelGenerator blockStateModelGenerator) {
        TextureMap textureMap = new TextureMap()
                .put(TextureKey.PARTICLE, TextureMap.getSubId(FBombsBlocks.FIREWORK_TNT, "_front"))
                .put(TextureKey.DOWN, TextureMap.getSubId(FBombsBlocks.FIREWORK_TNT, "_bottom"))
                .put(TextureKey.UP, TextureMap.getSubId(FBombsBlocks.FIREWORK_TNT, "_top"))
                .put(TextureKey.NORTH, TextureMap.getSubId(FBombsBlocks.FIREWORK_TNT, "_front"))
                .put(TextureKey.SOUTH, TextureMap.getSubId(FBombsBlocks.FIREWORK_TNT, "_front"))
                .put(TextureKey.EAST, TextureMap.getSubId(FBombsBlocks.FIREWORK_TNT, "_side"))
                .put(TextureKey.WEST, TextureMap.getSubId(FBombsBlocks.FIREWORK_TNT, "_side"));
        blockStateModelGenerator.blockStateCollector.accept(
                BlockStateModelGenerator.createSingletonBlockState(FBombsBlocks.FIREWORK_TNT, Models.CUBE.upload(FBombsBlocks.FIREWORK_TNT, textureMap, blockStateModelGenerator.modelCollector))
        );
    }
}
