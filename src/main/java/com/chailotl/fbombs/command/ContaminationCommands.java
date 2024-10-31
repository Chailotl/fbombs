package com.chailotl.fbombs.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ContaminationCommands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(literal("contamination")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(literal("set")
                        .then(argument("pos1", BlockPosArgumentType.blockPos())
                                .then(argument("pos2", BlockPosArgumentType.blockPos())
                                        .then(argument("value", FloatArgumentType.floatArg(0))
                                                .executes(ContaminationCommands::setRegion)))))
                .then(literal("clear")
                        .executes(ContaminationCommands::clearAll)));
    }

    private static int clearAll(CommandContext<ServerCommandSource> context) {
        //TODO: [ShiroJR] set contamination level in PersistentState
        return Command.SINGLE_SUCCESS;
    }

    private static int setRegion(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getWorld();
        var box = BlockPos.iterate(BlockPosArgumentType.getValidBlockPos(context, "pos1"), BlockPosArgumentType.getValidBlockPos(context, "pos2"));
        for (BlockPos pos : box) {
            setCps(pos, FloatArgumentType.getFloat(context, "value"));
        }
        return Command.SINGLE_SUCCESS;
    }

    private static void setCps(BlockPos pos, float value) {
        //TODO: [ShiroJR] set contamination level in PersistentState
    }
}
