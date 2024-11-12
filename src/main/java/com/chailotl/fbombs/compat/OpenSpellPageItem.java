package com.chailotl.fbombs.compat;

import io.github.reoseah.magisterium.item.SpellPageItem;
import net.minecraft.util.Identifier;

public class OpenSpellPageItem extends SpellPageItem implements ExcludeTranslation {
    public OpenSpellPageItem(Settings settings, Identifier spell) {
        super(settings, spell);
    }
}