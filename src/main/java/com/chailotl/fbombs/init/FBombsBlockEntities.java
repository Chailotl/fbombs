package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.entity.TestBlockEntity;
import com.chailotl.fbombs.entity.AcmeBedBlockEntity;
import com.chailotl.fbombs.util.HandledInventory;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class FBombsBlockEntities {
    public static final BlockEntityType<TestBlockEntity> TEST_BLOCK_ENTITY = register("test_block_entity", TestBlockEntity::new, FBombsBlocks.TEST);
    public static final BlockEntityType<AcmeBedBlockEntity> ACME_BED = register("acme_bed", AcmeBedBlockEntity::new,
        FBombsBlocks.WHITE_ACME_BED,
        FBombsBlocks.ORANGE_ACME_BED,
        FBombsBlocks.MAGENTA_ACME_BED,
        FBombsBlocks.LIGHT_BLUE_ACME_BED,
        FBombsBlocks.YELLOW_ACME_BED,
        FBombsBlocks.LIME_ACME_BED,
        FBombsBlocks.PINK_ACME_BED,
        FBombsBlocks.GRAY_ACME_BED,
        FBombsBlocks.LIGHT_GRAY_ACME_BED,
        FBombsBlocks.CYAN_ACME_BED,
        FBombsBlocks.PURPLE_ACME_BED,
        FBombsBlocks.BLUE_ACME_BED,
        FBombsBlocks.BROWN_ACME_BED,
        FBombsBlocks.GREEN_ACME_BED,
        FBombsBlocks.RED_ACME_BED,
        FBombsBlocks.BLACK_ACME_BED
    );

    private static <T extends BlockEntity> BlockEntityType<T> register(
            String name, BlockEntityType.BlockEntityFactory<? extends T> entityFactory, Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, FBombs.getId(name),
                BlockEntityType.Builder.<T>create(entityFactory, blocks).build(null));
    }

    @SuppressWarnings({"SameParameterValue", "unused"})
    private static <T extends BlockEntity> BlockEntityType<T> registerWithStorage(
            String name, BlockEntityType.BlockEntityFactory<? extends T> entityFactory, Block... blocks) {
        BlockEntityType<T> blockEntityType = register(name, entityFactory, blocks);
        ItemStorage.SIDED.registerForBlockEntity((blockEntity, direction) -> {
            if (blockEntity instanceof HandledInventory inventory) return inventory.getAsStorage(direction);
            FBombs.LOGGER.error("%s BlockEntity was missing HandledInventory Interface at registration".formatted(blockEntity));
            return null;
        }, blockEntityType);
        return blockEntityType;
    }

    public static void initialize() {
        // static initialisation
    }
}
