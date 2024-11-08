package com.chailotl.fbombs.command;

import com.chailotl.fbombs.explosion.ExplosionHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class VolumetricExplosionCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(literal("explosion")
                .then(literal("volumetric")
                        .then(argument("origin", BlockPosArgumentType.blockPos())
                                .then(argument("radius", IntegerArgumentType.integer())
                                        .then(argument("strength", IntegerArgumentType.integer())
                                                .then(argument("falloff", FloatArgumentType.floatArg())
                                                        .then(argument("scorchedThreshold", IntegerArgumentType.integer())
                                                                .executes(VolumetricExplosionCommands::executeSpherical)))))
                        )
                ));
    }

    private static int executeSpherical(CommandContext<ServerCommandSource> context) {
        ServerWorld world = context.getSource().getWorld();
        BlockPos pos = BlockPosArgumentType.getBlockPos(context, "origin");
        int radius = IntegerArgumentType.getInteger(context, "radius");
        int strength = IntegerArgumentType.getInteger(context, "strength");
        float falloff = FloatArgumentType.getFloat(context, "falloff");
        int scorchedThreshold = IntegerArgumentType.getInteger(context, "scorchedThreshold");

        ExplosionHandler.explodeSpherical(world, pos, radius, strength, falloff, scorchedThreshold);

        return Command.SINGLE_SUCCESS;
    }
}


// collect(world, origin, /*radius*/ 15, ExplosionShape.SPHERE, null,
//                        isImmune, /*strength*/ 20, 0.1f, 8)