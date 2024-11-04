package com.chailotl.fbombs.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class BottleCapItem extends Item {
    private static final int TOTAL = 9;

    private final int serialNumber;

    public BottleCapItem(int serialNumber, Settings settings) {
        super(settings);
        this.serialNumber = serialNumber;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type)
    {
        tooltip.add(Text.literal("⭐ Quest Item").formatted(Formatting.GRAY));
        tooltip.add(Text.literal(serialNumber + "/" + TOTAL).formatted(Formatting.DARK_GRAY));
    }
}