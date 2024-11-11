package com.chailotl.fbombs.init;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.data.RadiationCategory;
import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;

public class FBombsArgumentTypes {
    static {
        register("radiation_category", RadiationCategory.ArgumentType.class, ConstantArgumentSerializer.of(RadiationCategory.ArgumentType::contaminationCategory));
    }

    @SuppressWarnings("SameParameterValue")
    private static <A extends ArgumentType<?>, T extends ArgumentSerializer.ArgumentTypeProperties<A>> void register(
            String id, Class<? extends A> clazz, ArgumentSerializer<A, T> serializer) {
        ArgumentTypeRegistry.registerArgumentType(FBombs.getId(id), clazz, serializer);
    }

    public static void initialize() {
        // static initialisation
    }
}
