package com.chailotl.fbombs.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class NuclearLaunchKeyItem extends Item {
    public NuclearLaunchKeyItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type)
    {
        tooltip.add(Text.literal("\"I am become death, the destroyer of worlds.\"").formatted(Formatting.GRAY));
        tooltip.add(Text.literal("—J. Robert Oppenheimer").formatted(Formatting.GRAY));
    }
}