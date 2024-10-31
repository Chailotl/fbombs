package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.block.AdaptiveTntBlock;
import com.chailotl.fbombs.entity.*;
import com.chailotl.fbombs.item.AdaptiveTntItem;
import com.chailotl.fbombs.item.DynamiteItem;
import com.chailotl.fbombs.item.HazmatArmor;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

@SuppressWarnings("unused")
public class FBombsItems {
    public static final DynamiteItem DYNAMITE = register("dynamite", new DynamiteItem(DynamiteEntity::new, new Item.Settings()), FBombsItemGroups.ITEMS);
    public static final DynamiteItem BOUNCY_DYNAMITE = register("bouncy_dynamite", new DynamiteItem(BouncyDynamiteEntity::new, new Item.Settings()), FBombsItemGroups.ITEMS);
    public static final DynamiteItem STICKY_DYNAMITE = register("sticky_dynamite", new DynamiteItem(StickyDynamiteEntity::new, new Item.Settings()), FBombsItemGroups.ITEMS);
    public static final DynamiteItem DYNAMITE_BUNDLE = register("dynamite_bundle", new DynamiteItem(DynamiteBundleEntity::new, new Item.Settings()), FBombsItemGroups.ITEMS);
    public static final BlockItem GUNPOWDER_TRAIL = register("gunpowder_trail", new BlockItem(FBombsBlocks.GUNPOWDER_TRAIL, new Item.Settings()));
    public static final AdaptiveTntItem ADAPTIVE_TNT = register("adaptive_tnt", new AdaptiveTntItem(FBombsBlocks.ADAPTIVE_TNT, new Item.Settings()), FBombsItemGroups.BLOCKS);

    public static final HazmatArmor HAZMAT_HELMET = register("hazmat_helmet", new HazmatArmor(ArmorItem.Type.HELMET, new Item.Settings()));
    public static final HazmatArmor HAZMAT_CHESTPLATE = register("hazmat_chestplate", new HazmatArmor(ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    public static final HazmatArmor HAZMAT_LEGGINGS = register("hazmat_leggings", new HazmatArmor(ArmorItem.Type.LEGGINGS, new Item.Settings()));
    public static final HazmatArmor HAZMAT_BOOTS = register("hazmat_boots", new HazmatArmor(ArmorItem.Type.BOOTS, new Item.Settings()));


    private static <T extends Item> T register(String name, T item, FBombsItemGroups.ItemGroupEntry... itemGroups) {
        Registry.register(Registries.ITEM, FBombs.getId(name), item);
        if (itemGroups != null) {
            for (var entry : itemGroups) {
                entry.addItems(item);
            }
        }
        return item;
    }

    public static void initialize() {
        // static initialisation
        DispenserBlock.registerBehavior(ADAPTIVE_TNT, new ItemDispenserBehavior() {
            @Override
            protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
                World world = pointer.world();
                BlockPos blockPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
                AdaptiveTntEntity tntEntity = new AdaptiveTntEntity(world, (double) blockPos.getX() + 0.5, blockPos.getY(), (double) blockPos.getZ() + 0.5, null, ADAPTIVE_TNT.getBlock().getDefaultState());
                AdaptiveTntBlock.configureAdaptiveTnt(tntEntity, stack);

                world.spawnEntity(tntEntity);
                if (tntEntity.getFuse() >= 10) {
                    world.playSound(null, tntEntity.getX(), tntEntity.getY(), tntEntity.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
                world.emitGameEvent(null, GameEvent.ENTITY_PLACE, blockPos);
                stack.decrement(1);
                return stack;
            }
        });
    }
}
