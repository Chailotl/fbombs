package com.chailotl.fbombs.compat;

import com.chailotl.fbombs.init.FBombsTags;
import martian.minefactorial.content.registry.MFStrawActions;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class MineFactorial {
    public static void initialize() {
        Consumer<PlayerEntity> playerEntityConsumer = player -> {
            if (!player.getWorld().isClient) {
                player.getWorld().createExplosion(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    2,
                    false,
                    World.ExplosionSourceType.MOB
                );
            }
        };
        try {
            MFStrawActions.class.getMethod("add", TagKey.class, Consumer.class).invoke(null, FBombsTags.Fluids.JUICE_THAT_MAKES_YOU_EXPLODE, playerEntityConsumer);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}