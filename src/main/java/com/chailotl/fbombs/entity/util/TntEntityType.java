package com.chailotl.fbombs.entity.util;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public record TntEntityType(String name, TntEntityProvider tntEntityProvider) {
    private static final Map<String, TntEntityType> VALUES = new Object2ObjectArrayMap<>();
    public static final Codec<TntEntityType> CODEC = Codec.stringResolver(TntEntityType::name, VALUES::get);
    public static TntEntityType register(String name, TntEntityProvider tntEntityProvider) {
        TntEntityType tntEntityType = new TntEntityType(name, tntEntityProvider);
        VALUES.put(name, tntEntityType);
        return tntEntityType;
    }
}