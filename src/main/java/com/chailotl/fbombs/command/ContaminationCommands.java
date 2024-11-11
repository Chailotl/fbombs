package com.chailotl.fbombs.command;

import com.chailotl.fbombs.FBombs;
import com.chailotl.fbombs.contamination.ContaminationHandler;
import com.chailotl.fbombs.data.RadiationCategory;
import com.chailotl.fbombs.data.RadiationData;
import com.chailotl.fbombs.util.cast.Contaminatable;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ContaminationCommands {
    private static final SimpleCommandExceptionType POS_NOT_FOUND = new SimpleCommandExceptionType(Text.literal("No contamination on specified BlockPos found"));
    private static final SimpleCommandExceptionType NO_SPOTS_FOUND = new SimpleCommandExceptionType(Text.literal("No contaminated spots found"));
    private static final SimpleCommandExceptionType NO_ENTITIES_FOUND = new SimpleCommandExceptionType(Text.literal("No contaminated entities found"));



    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(literal("contamination")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(literal("get")
                        .executes(ContaminationCommands::printSpotsCps)
                        .then(argument("targets", EntityArgumentType.entities())
                                .executes(ContaminationCommands::printEntityCps)))
                .then(literal("set")
                        .then(argument("center", BlockPosArgumentType.blockPos())
                                .then(argument("cps", FloatArgumentType.floatArg(0))
                                        .then(argument("radius", FloatArgumentType.floatArg(0))
                                                .executes(ContaminationCommands::addNewCPSSpot)))
                                .then(argument("category", RadiationCategory.ArgumentType.contaminationCategory())
                                        .then(argument("radius", FloatArgumentType.floatArg(0))
                                                .executes(ContaminationCommands::addNewCPSSpotByCategory)))))
                .then(literal("clear")
                        .executes(ContaminationCommands::clearSpots)
                        .then(argument("center", BlockPosArgumentType.blockPos())
                                .executes(ContaminationCommands::clearSpot))
                        .then(argument("targets", EntityArgumentType.entities())
                                .executes(ContaminationCommands::clearContaminatedEntities))
                )
        );
    }

    private static int printEntityCps(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        for (Entity entity : EntityArgumentType.getEntities(context, "targets")) {
            if (entity.getWorld().isClient()) continue;
            if (!(entity instanceof Contaminatable contaminatable)) continue;
            if (contaminatable.fbombs$getCps() <= 0) continue;
            context.getSource().sendFeedback(() -> Text.literal("%s - %s CPS".formatted(entity.getName().getString(), contaminatable.fbombs$getCps())), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int printSpotsCps(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<String> spots = new ArrayList<>();
        for (RadiationData entry : FBombs.getCachedPersistentState(context.getSource().getWorld()).getRadiationSources()) {
            spots.add("[Pos: %s | CPS: %s | Radius: %s] ".formatted(entry.pos().toShortString(), entry.cps(), entry.radius()));
        }
        if (spots.isEmpty()) {
            throw NO_SPOTS_FOUND.create();
        }
        context.getSource().sendFeedback(() -> Text.literal("Contaminated Spots:"), false);
        for (String spot : spots) {
            context.getSource().sendFeedback(() -> Text.literal(spot), false);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int addNewCPSSpot(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getWorld();
        BlockPos center = BlockPosArgumentType.getValidBlockPos(context, "center");
        float cps = FloatArgumentType.getFloat(context, "cps");
        float radius = FloatArgumentType.getFloat(context, "radius");

        RadiationData data = new RadiationData(center, cps, radius);
        ContaminationHandler.addContaminationSpot(world, data);
        return Command.SINGLE_SUCCESS;
    }

    private static int addNewCPSSpotByCategory(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerWorld world = context.getSource().getWorld();
        BlockPos center = BlockPosArgumentType.getValidBlockPos(context, "center");
        float cps = RadiationCategory.ArgumentType.getRadiationCategory(context, "category").getMaxCps();
        float radius = FloatArgumentType.getFloat(context, "radius");

        RadiationData data = new RadiationData(center, cps, radius);
        ContaminationHandler.addContaminationSpot(world, data);
        return Command.SINGLE_SUCCESS;
    }

    private static int clearSpot(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        BlockPos pos = BlockPosArgumentType.getValidBlockPos(context, "center");
        List<RadiationData> clearedSpots = ContaminationHandler.clearContaminationSpot(context.getSource().getWorld(), pos);
        if (clearedSpots.isEmpty()) {
            throw POS_NOT_FOUND.create();
        }
        for (RadiationData entry : clearedSpots) {
            context.getSource().sendFeedback(() -> Text.literal("Deleted Spot at: " + entry.pos()), true);
        }
        return Command.SINGLE_SUCCESS;
    }

    private static int clearSpots(CommandContext<ServerCommandSource> context) {
        ContaminationHandler.clearAllContaminationSpots(context.getSource().getWorld());
        context.getSource().sendFeedback(() -> Text.literal("All Contaminated Spots have been removed"), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int clearContaminatedEntities(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        List<Entity> clearedEntities = new ArrayList<>();
        for (Entity entry : EntityArgumentType.getEntities(context, "targets")) {
            if (!(entry instanceof Contaminatable contaminatable)) continue;
            if (contaminatable.fbombs$getCps() > 0) {
                clearedEntities.add(entry);
                contaminatable.fbombs$setCps(0);
            }
        }
        if (clearedEntities.isEmpty()) throw NO_ENTITIES_FOUND.create();
        context.getSource().sendFeedback(() -> Text.literal("Cleared contamination for:"), true);
        for (Entity entry : clearedEntities) {
            context.getSource().sendFeedback(() -> Text.literal("%s at %s".formatted(entry.getName().getString(), entry.getBlockPos().toShortString())), true);
        }
        return Command.SINGLE_SUCCESS;
    }
}
