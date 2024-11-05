package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.entity.*;
import com.chailotl.fbombs.entity.util.DynamiteEntityProviderOwner;
import com.chailotl.fbombs.entity.util.DynamiteEntityProviderPos;
import com.chailotl.fbombs.item.*;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

@SuppressWarnings("unused")
public class FBombsItems {
    public static final DynamiteItem DYNAMITE = registerDynamite("dynamite", DynamiteEntity::new, DynamiteEntity::new);
    public static final DynamiteItem BOUNCY_DYNAMITE = registerDynamite("bouncy_dynamite", BouncyDynamiteEntity::new, BouncyDynamiteEntity::new);
    public static final DynamiteItem STICKY_DYNAMITE = registerDynamite("sticky_dynamite", StickyDynamiteEntity::new, StickyDynamiteEntity::new);
    public static final DynamiteItem DYNAMITE_BUNDLE = registerDynamite("dynamite_bundle", DynamiteBundleEntity::new, DynamiteBundleEntity::new);
    public static final BlockItem GUNPOWDER_TRAIL = register("gunpowder_trail", new BlockItem(FBombsBlocks.GUNPOWDER_TRAIL, new Item.Settings()));

    public static final HazmatArmor HAZMAT_HELMET = register("hazmat_helmet", new HazmatArmor(ArmorItem.Type.HELMET, new Item.Settings()));
    public static final HazmatArmor HAZMAT_CHESTPLATE = register("hazmat_chestplate", new HazmatArmor(ArmorItem.Type.CHESTPLATE, new Item.Settings()));
    public static final HazmatArmor HAZMAT_LEGGINGS = register("hazmat_leggings", new HazmatArmor(ArmorItem.Type.LEGGINGS, new Item.Settings()));
    public static final HazmatArmor HAZMAT_BOOTS = register("hazmat_boots", new HazmatArmor(ArmorItem.Type.BOOTS, new Item.Settings()));

    public static final Item NUCLEAR_LAUNCH_KEY = register("nuclear_launch_key", new NuclearLaunchKeyItem(new Item.Settings().maxCount(1).rarity(Rarity.EPIC)));
    public static final BottleCapItem COLA_BOTTLE_CAP = registerBottleCap("cola_bottle_cap", 1);
    public static final BottleCapItem ROOT_BEER_BOTTLE_CAP = registerBottleCap("root_beer_bottle_cap", 2);
    public static final BottleCapItem CREAM_SODA_BOTTLE_CAP = registerBottleCap("cream_soda_bottle_cap", 3);
    public static final BottleCapItem GINGER_ALE_BOTTLE_CAP = registerBottleCap("ginger_ale_bottle_cap", 4);
    public static final BottleCapItem LEMON_LIME_SODA_BOTTLE_CAP = registerBottleCap("lemon_lime_soda_bottle_cap", 5);
    public static final BottleCapItem BLUEBERRY_SODA_BOTTLE_CAP = registerBottleCap("blueberry_soda_bottle_cap", 6);
    public static final BottleCapItem CHERRY_SODA_BOTTLE_CAP = registerBottleCap("cherry_soda_bottle_cap", 7);
    public static final BottleCapItem ORANGE_SODA_BOTTLE_CAP = registerBottleCap("orange_soda_bottle_cap", 8);
    public static final BottleCapItem SARSPARILLA_BOTTLE_CAP = registerBottleCap("sarsparilla_bottle_cap", 9);

    private static <T extends Item> T register(String name, T item, FBombsItemGroups.ItemGroupEntry... itemGroups) {
        Registry.register(Registries.ITEM, FBombs.getId(name), item);
        if (itemGroups != null) {
            for (var entry : itemGroups) {
                entry.addItems(item);
            }
        }
        return item;
    }

    private static DynamiteItem registerDynamite(String name, DynamiteEntityProviderOwner dynamiteEntityProviderOwner, DynamiteEntityProviderPos dynamiteEntityProviderPos) {
        DynamiteItem item = register(name, new DynamiteItem(dynamiteEntityProviderOwner, dynamiteEntityProviderPos, new Item.Settings()), FBombsItemGroups.GROUP);
        DispenserBlock.registerProjectileBehavior(item);
        return item;
    }

    private static BottleCapItem registerBottleCap(String name, int serialNumber) {
        return register(name, new BottleCapItem(serialNumber, new Item.Settings().maxCount(1).rarity(Rarity.RARE)));
    }

    public static void initialize() {
        // static initialisation
    }
}
