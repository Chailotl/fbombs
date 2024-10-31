package com.chailotl.fbombs.item;

import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class AdaptiveTntItem extends BlockItem {
    public AdaptiveTntItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        NbtComponent nbtComponent = stack.getComponents().get(DataComponentTypes.BLOCK_ENTITY_DATA);
        NbtCompound nbt = nbtComponent != null ? nbtComponent.getNbt() : new NbtCompound();

        tooltip.add(Text.literal("Power: " + getIntOrDefault(nbt, "power", 0)).formatted(Formatting.GRAY));
        float fuse = getIntOrDefault(nbt, "fuse", 0) / 20f;
        tooltip.add(Text.literal("Fuse: " + fuse + " second" + (fuse == 1 ? "" : "s")).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Entity Damage: " + getYesNo(nbt, "damage", true)).formatted(Formatting.GRAY));
        tooltip.add(Text.literal("Block Damage: " + getYesNo(nbt, "block_damage", true)).formatted(Formatting.GRAY));

        if (getBooleanOrDefault(nbt, "bouncy", false)) {
            tooltip.add(Text.literal("+ Bouncy").formatted(Formatting.GRAY));
        }
        if (getBooleanOrDefault(nbt, "sticky", false)) {
            tooltip.add(Text.literal("+ Sticky").formatted(Formatting.GRAY));
        }
        if (getBooleanOrDefault(nbt, "underwater", false)) {
            tooltip.add(Text.literal("+ Underwater").formatted(Formatting.GRAY));
        }
        if (getBooleanOrDefault(nbt, "sponge", false)) {
            tooltip.add(Text.literal("+ Sponge").formatted(Formatting.GRAY));
        }
        if (getBooleanOrDefault(nbt, "fire_charged", false)) {
            tooltip.add(Text.literal("+ Fire Charged").formatted(Formatting.GRAY));
        }
        if (getBooleanOrDefault(nbt, "wind_charged", false)) {
            tooltip.add(Text.literal("+ Wind Charged").formatted(Formatting.GRAY));
        }
        if (getBooleanOrDefault(nbt, "levitating", false)) {
            tooltip.add(Text.literal("+ Levitating").formatted(Formatting.GRAY));
        }
        if (getBooleanOrDefault(nbt, "firework", false)) {
            tooltip.add(Text.literal("+ Firework").formatted(Formatting.GRAY));
        }
    }

    private int getIntOrDefault(NbtCompound nbt, String key, int defaultValue) {
        return nbt.contains(key) ? nbt.getInt(key) : defaultValue;
    }

    private boolean getBooleanOrDefault(NbtCompound nbt, String key, boolean defaultValue) {
        return nbt.contains(key) ? nbt.getBoolean(key) : defaultValue;
    }

    private String getYesNo(NbtCompound nbt, String key, boolean defaultValue) {
        return getBooleanOrDefault(nbt, key, defaultValue) ? "Yes" : "No";
    }
}