package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.item.TestItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class FBombsItems {
    public static final TestItem TEST_ITEM = register("test_item", new TestItem(new Item.Settings()), null);

    @SuppressWarnings("SameParameterValue")
    private static <T extends Item> T register(String name, T item, @Nullable List<FBombsItemGroups.ItemGroupEntry> itemGroups) {
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
}
