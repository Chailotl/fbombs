package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.entity.BouncyDynamiteEntity;
import com.chailotl.fbombs.entity.DynamiteBundleEntity;
import com.chailotl.fbombs.entity.DynamiteEntity;
import com.chailotl.fbombs.entity.StickyDynamiteEntity;
import com.chailotl.fbombs.item.DynamiteItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class FBombsItems {
    private static final List<Item> VALUES = new ArrayList<>();

    public static final DynamiteItem DYNAMITE = register("dynamite", new DynamiteItem(DynamiteEntity::new, new Item.Settings()), FBombsItemGroups.ITEMS);
    public static final DynamiteItem BOUNCY_DYNAMITE = register("bouncy_dynamite", new DynamiteItem(BouncyDynamiteEntity::new, new Item.Settings()), FBombsItemGroups.ITEMS);
    public static final DynamiteItem STICKY_DYNAMITE = register("sticky_dynamite", new DynamiteItem(StickyDynamiteEntity::new, new Item.Settings()), FBombsItemGroups.ITEMS);
    public static final DynamiteItem DYNAMITE_BUNDLE = register("dynamite_bundle", new DynamiteItem(DynamiteBundleEntity::new, new Item.Settings()), FBombsItemGroups.ITEMS);
    public static final BlockItem GUNPOWDER_TRAIL = register("gunpowder_trail", new BlockItem(FBombsBlocks.GUNPOWDER_TRAIL, new Item.Settings()));

    @SuppressWarnings("SameParameterValue")
    private static <T extends Item> T register(String name, T item, FBombsItemGroups.ItemGroupEntry... itemGroups) {
        VALUES.add(item);
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
    }

    public static Stream<Item> stream() {
        return VALUES.stream();
    }
}
