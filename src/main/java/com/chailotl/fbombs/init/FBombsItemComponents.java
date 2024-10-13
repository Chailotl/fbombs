package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

@SuppressWarnings({"unused", "SameParameterValue"})
public class FBombsItemComponents {
    public static final ComponentType<Integer> TEST_COUNTER = register("test_counter", PrimitiveCodec.INT);


    private static <T> ComponentType<T> register(String name, Codec<T> codec) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, FBombs.getId(name),
                ComponentType.<T>builder().codec(codec).build());
    }

    public static void initialize() {
        // static initialisation
    }
}
