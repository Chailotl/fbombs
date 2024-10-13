package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.item.DynamiteStickItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class FBombsItems {
    private static final List<Item> VALUES = new ArrayList<>();

    public static final DynamiteStickItem DYNAMITE_STICK = register("dynamite_stick", new DynamiteStickItem(new Item.Settings()), FBombsItemGroups.ITEMS);

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
