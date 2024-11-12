package com.chailotl.fbombs.compat;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.init.FBombsItemGroups;
import io.github.reoseah.magisterium.data.effect.SpellEffect;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class Magisterium {
    public static final Item CRIMSON_EXPLOSION = new OpenSpellPageItem(new Item.Settings().maxCount(16), FBombs.getId("crimson_explosion"));

    public static void initialize() {
        Registry.register(Registries.ITEM, FBombs.getId("crimson_explosion_page"), CRIMSON_EXPLOSION);
        ItemGroupEvents.modifyEntriesEvent(FBombsItemGroups.GROUP.getRegistryKey()).register(content -> content.add(CRIMSON_EXPLOSION));
        Registry.register(SpellEffect.REGISTRY, FBombs.getId("crimson_explosion"), CrimsonExplosionEffect.CODEC);
    }
}