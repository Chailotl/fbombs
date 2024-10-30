package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.*;
import com.chailotl.fbombs.entity.*;
import com.chailotl.fbombs.entity.util.TntEntityProvider;
import com.chailotl.fbombs.entity.util.TntEntityType;
import net.minecraft.block.*;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.block.enums.BedPart;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class FBombsBlocks {
    public static final TestBlock TEST = register("test_block", new TestBlock(AbstractBlock.Settings.create()), true);

    public static final GenericTntBlock INSTANT_TNT = registerTnt("instant_tnt", InstantTntEntity::new);
    public static final GenericTntBlock SHORT_FUSE_TNT = registerTnt("short_fuse_tnt", ShortFuseTntEntity::new);
    public static final GenericTntBlock LONG_FUSE_TNT = registerTnt("long_fuse_tnt", LongFuseTntEntity::new);
    public static final GenericTntBlock LOW_POWER_TNT = registerTnt("low_power_tnt", LowPowerTntEntity::new);
    public static final GenericTntBlock HIGH_POWER_TNT = registerTnt("high_power_tnt", HighPowerTntEntity::new);
    public static final GenericTntBlock FRAGMENTATION_TNT = registerTnt("fragmentation_tnt", FragmentationTntEntity::new);
    public static final GenericTntBlock FIRE_CHARGED_TNT = registerTnt("fire_charged_tnt", FireChargedTntEntity::new);
    public static final GenericTntBlock WIND_CHARGED_TNT = registerTnt("wind_charged_tnt", WindChargedTntEntity::new);
    public static final GenericTntBlock UNDERWATER_TNT = registerTnt("underwater_tnt", UnderwaterTntEntity::new);
    public static final GenericTntBlock SPONGE_BOMB = registerTnt("sponge_bomb", SpongeBombEntity::new);
    public static final GenericTntBlock LEVITATING_TNT = registerTnt("levitating_tnt", LevitatingTntEntity::new);
    public static final ShapedChargeBlock SHAPED_CHARGE = register("shaped_charge", new ShapedChargeBlock(
        new TntEntityType("shaped_charge", ShapedChargeEntity::new),
        AbstractBlock.Settings.create()
            .mapColor(MapColor.BRIGHT_RED)
            .breakInstantly()
            .sounds(BlockSoundGroup.GRASS)
            .burnable()
            .solidBlock(Blocks::never)
    ), true);
    public static final ShapedChargeBlock MINING_CHARGE = register("mining_charge", new ShapedChargeBlock(
        new TntEntityType("mining_charge", MiningChargeEntity::new),
        AbstractBlock.Settings.create()
            .mapColor(MapColor.BRIGHT_RED)
            .breakInstantly()
            .sounds(BlockSoundGroup.GRASS)
            .burnable()
            .solidBlock(Blocks::never)
    ), true);

    public static final SplitTntBlock SPLIT_TNT = register("split_tnt", new SplitTntBlock(
        new TntEntityType("split_tnt", SplitTntEntity::new),
        AbstractBlock.Settings.create()
            .mapColor(MapColor.BRIGHT_RED)
            .breakInstantly()
            .sounds(BlockSoundGroup.GRASS)
            .burnable()
            .solidBlock(Blocks::never)
    ), false);
    public static final TntSlabBlock TNT_SLAB = register("tnt_slab", new TntSlabBlock(
        new TntEntityType("tnt_slab", TntSlabEntity::new),
        AbstractBlock.Settings.create()
            .mapColor(MapColor.BRIGHT_RED)
            .breakInstantly()
            .sounds(BlockSoundGroup.GRASS)
            .burnable()
            .solidBlock(Blocks::never)
    ), true);

    public static final GunpowderTrailBlock GUNPOWDER_TRAIL = register("gunpowder_trail", new GunpowderTrailBlock(
        AbstractBlock.Settings.create()
            .noCollision()
            .breakInstantly()
            .pistonBehavior(PistonBehavior.DESTROY)
    ), false);

    public static final AcmeBedBlock WHITE_ACME_BED = registerAcmeBed("white_acme_bed", DyeColor.WHITE);
    public static final AcmeBedBlock LIGHT_GRAY_ACME_BED = registerAcmeBed("light_gray_acme_bed", DyeColor.LIGHT_GRAY);
    public static final AcmeBedBlock GRAY_ACME_BED = registerAcmeBed("gray_acme_bed", DyeColor.GRAY);
    public static final AcmeBedBlock BLACK_ACME_BED = registerAcmeBed("black_acme_bed", DyeColor.BLACK);
    public static final AcmeBedBlock BROWN_ACME_BED = registerAcmeBed("brown_acme_bed", DyeColor.BROWN);
    public static final AcmeBedBlock RED_ACME_BED = registerAcmeBed("red_acme_bed", DyeColor.RED);
    public static final AcmeBedBlock ORANGE_ACME_BED = registerAcmeBed("orange_acme_bed", DyeColor.ORANGE);
    public static final AcmeBedBlock YELLOW_ACME_BED = registerAcmeBed("yellow_acme_bed", DyeColor.YELLOW);
    public static final AcmeBedBlock LIME_ACME_BED = registerAcmeBed("lime_acme_bed", DyeColor.LIME);
    public static final AcmeBedBlock GREEN_ACME_BED = registerAcmeBed("green_acme_bed", DyeColor.GREEN);
    public static final AcmeBedBlock CYAN_ACME_BED = registerAcmeBed("cyan_acme_bed", DyeColor.CYAN);
    public static final AcmeBedBlock BLUE_ACME_BED = registerAcmeBed("blue_acme_bed", DyeColor.BLUE);
    public static final AcmeBedBlock LIGHT_BLUE_ACME_BED = registerAcmeBed("light_blue_acme_bed", DyeColor.LIGHT_BLUE);
    public static final AcmeBedBlock PURPLE_ACME_BED = registerAcmeBed("purple_acme_bed", DyeColor.PURPLE);
    public static final AcmeBedBlock MAGENTA_ACME_BED = registerAcmeBed("magenta_acme_bed", DyeColor.MAGENTA);
    public static final AcmeBedBlock PINK_ACME_BED = registerAcmeBed("pink_acme_bed", DyeColor.PINK);

    public static final MultiShotDispenserBlock MULTI_SHOT_DISPENSER = register("multi_shot_dispenser", new MultiShotDispenserBlock(
        AbstractBlock.Settings.create()
            .mapColor(MapColor.STONE_GRAY)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresTool()
            .strength(3.5F)
    ), true);

    @SuppressWarnings("SameParameterValue")
    private static <T extends Block> T register(String name, T block, boolean hasDefaultItem) {
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
        register(name, block, true);
        DispenserBlock.registerBehavior(block, new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                World world = pointer.world();
                BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
                AbstractTntEntity tntEntity = tntEntityProvider.spawn(world, (double) blockPos.getX() + 0.5, blockPos.getY(), (double) blockPos.getZ() + 0.5, null, block.getDefaultState());

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

    private static AcmeBedBlock registerAcmeBed(String name, DyeColor color) {
        AcmeBedBlock block = new AcmeBedBlock(
            color,
            AbstractBlock.Settings.create()
                .mapColor(state -> state.get(BedBlock.PART) == BedPart.FOOT ? color.getMapColor() : MapColor.WHITE_GRAY)
                .sounds(BlockSoundGroup.WOOD)
                .strength(0.2F)
                .nonOpaque()
                .burnable()
                .pistonBehavior(PistonBehavior.DESTROY)
        );
        return register(name, block, true);
    }

    public static void initialize() {
        // static initialisation
    }
}
