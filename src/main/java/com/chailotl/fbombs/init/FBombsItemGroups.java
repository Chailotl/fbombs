package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("Convert2MethodRef")
// method references instead of lambdas cause issues when lazy initialisation
public class FBombsItemGroups {
    public static final ItemGroupEntry ITEMS = new ItemGroupEntry("items", () -> FBombsItems.DYNAMITE_STICK);
    public static final ItemGroupEntry BLOCKS = new ItemGroupEntry("blocks", () -> FBombsBlocks.INSTANT_TNT.asItem());

    public static void initialize() {
        ItemGroupEntry.ALL_GROUPS.forEach(entry -> entry.register());
    }

    public record ItemGroupEntry(String name, Supplier<ItemConvertible> icon, List<ItemConvertible> items) {
        public static List<ItemGroupEntry> ALL_GROUPS = new ArrayList<>();

        public ItemGroupEntry(String name, Supplier<ItemConvertible> icon) {
            this(name, icon, new ArrayList<>());
            ALL_GROUPS.add(this);
        }

        public void addItems(ItemConvertible... items) {
            this.items.addAll(List.of(items));
        }

        @NotNull
        public ItemGroup get() {
            return Optional.ofNullable(Registries.ITEM_GROUP.get(getRegistryKey())).orElseThrow();
        }

        public String getTranslationKey() {
            return "itemgroup.%s.%s".formatted(FBombs.MOD_ID, this.name());
        }

        @SuppressWarnings("UnusedReturnValue")
        public RegistryKey<ItemGroup> register() {
            Text displayName = Text.translatable(getTranslationKey());
            ItemGroup itemGroup = FabricItemGroup.builder()
                    .icon(() -> new ItemStack(this.icon.get()))
                    .displayName(displayName)
                    .entries((displayContext, entries) -> entries.addAll(
                            items.stream().map(item -> new ItemStack(item)).toList())
                    )
                    .build();
            Registry.register(Registries.ITEM_GROUP, getRegistryKey(), itemGroup);
            return getRegistryKey();
        }

        public RegistryKey<ItemGroup> getRegistryKey() {
            return RegistryKey.of(RegistryKeys.ITEM_GROUP, FBombs.getId(this.name));
        }
    }
}
