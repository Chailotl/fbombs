package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.GenericTntBlock;
import com.chailotl.fbombs.block.SplitTntBlock;
import com.chailotl.fbombs.block.TestBlock;
import com.chailotl.fbombs.block.TntSlabBlock;
import com.chailotl.fbombs.entity.*;
import com.chailotl.fbombs.entity.util.TntEntityProvider;
import com.chailotl.fbombs.entity.util.TntEntityType;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FBombsBlocks {
    private static final List<Block> VALUES = new ArrayList<>();
    private static final List<GenericTntBlock> TNT_BLOCKS = new ArrayList<>();

    public static final TestBlock TEST = register("test_block", new TestBlock(AbstractBlock.Settings.create()), true);
    public static final GenericTntBlock INSTANT_TNT = registerTnt("instant_tnt", InstantTntEntity::new);
    public static final SplitTntBlock SPLIT_TNT = register("split_tnt", new SplitTntBlock(
                    new TntEntityType("split_tnt", SplitTntEntity::new), AbstractBlock.Settings.create()
                    .mapColor(MapColor.BRIGHT_RED)
                    .breakInstantly()
                    .sounds(BlockSoundGroup.GRASS)
                    .burnable()
                    .solidBlock(Blocks::never)),
            false);
    public static final TntSlabBlock TNT_SLAB = register("tnt_slab", new TntSlabBlock(
                    new TntEntityType("tnt_slab", TntSlabEntity::new), AbstractBlock.Settings.create()
                    .mapColor(MapColor.BRIGHT_RED)
                    .breakInstantly()
                    .sounds(BlockSoundGroup.GRASS)
                    .burnable()
                    .solidBlock(Blocks::never)),
            true);
    public static final GenericTntBlock SHORT_FUSE_TNT = registerTnt("short_fuse_tnt", ShortFuseTntEntity::new);
    public static final GenericTntBlock LONG_FUSE_TNT = registerTnt("long_fuse_tnt", LongFuseTntEntity::new);
    public static final GenericTntBlock HIGH_POWER_TNT = registerTnt("high_power_tnt", HighPowerTntEntity::new);
    public static final GenericTntBlock LOW_POWER_TNT = registerTnt("low_power_tnt", LowPowerTntEntity::new);
    public static final GenericTntBlock FIRE_TNT = registerTnt("fire_tnt", FireTntEntity::new);
    public static final GenericTntBlock CONCUSSIVE_TNT = registerTnt("concussive_tnt", ConcussiveTntEntity::new);
    public static final GenericTntBlock WIND_CHARGED_TNT = registerTnt("wind_charged_tnt", WindChargedTntEntity::new);
    public static final GenericTntBlock UNDERWATER_TNT = registerTnt("underwater_tnt", UnderwaterTntEntity::new);
    public static final GenericTntBlock SPONGE_BOMB = registerTnt("sponge_bomb", SpongeBombEntity::new);
    public static final GenericTntBlock MINING_CHARGE = registerTnt("mining_charge", MiningChargeEntity::new);

    @SuppressWarnings("SameParameterValue")
    private static <T extends Block> T register(String name, T block, boolean hasDefaultItem) {
        VALUES.add(block);
        Registry.register(Registries.BLOCK, FBombs.getId(name), block);
        if (hasDefaultItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Settings());
            Registry.register(Registries.ITEM, FBombs.getId(name), blockItem);
            FBombsItemGroups.BLOCKS.addItems(blockItem);
        }
        return block;
    }

    private static GenericTntBlock registerTnt(String name, TntEntityProvider tntEntityProvider) {
        GenericTntBlock block = new GenericTntBlock(
                TntEntityType.register(name, tntEntityProvider),
                AbstractBlock.Settings.create()
                        .mapColor(MapColor.BRIGHT_RED)
                        .breakInstantly()
                        .sounds(BlockSoundGroup.GRASS)
                        .burnable()
                        .solidBlock(Blocks::never)
        );
        TNT_BLOCKS.add(block);
        register(name, block, true);
        DispenserBlock.registerBehavior(block, new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                World world = pointer.world();
                BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
                BlockState state = world.getBlockState(blockPos);
                AbstractTntEntity tntEntity;
                tntEntity = tntEntityProvider.spawn(world, (double) blockPos.getX() + 0.5, blockPos.getY(), (double) blockPos.getZ() + 0.5, null, state);

                world.spawnEntity(tntEntity);
                if (tntEntity.getFuse() >= 10) {
                    world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                world.emitGameEvent(null, GameEvent.ENTITY_PLACE, blockPos);
                stack.decrement(1);
                return stack;
            }
        });
        return block;
    }

    public static void initialize() {
        // static initialisation
    }

    public static Stream<Block> stream() {
        return VALUES.stream();
    }

    public static Stream<GenericTntBlock> streamTntBlocks() {
        return TNT_BLOCKS.stream();
    }
}
