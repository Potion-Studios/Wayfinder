package net.potionstudios.wayfinder.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.potionstudios.wayfinder.PlatformHandler;

class WayfinderDeathsCommand {

    static LiteralArgumentBuilder<CommandSourceStack> register() {
        LiteralArgumentBuilder<CommandSourceStack> deathsCommand = LiteralArgumentBuilder.literal("deaths");

        deathsCommand.then(LiteralArgumentBuilder.<CommandSourceStack>literal("list")
                .executes(WayfinderDeathsCommand::listDeaths));

        deathsCommand.executes(WayfinderDeathsCommand::showPlayerDeaths)
                .then(RequiredArgumentBuilder.<CommandSourceStack, EntitySelector>argument("player", EntityArgument.player())
                .executes(WayfinderDeathsCommand::showSelectedPlayerDeaths));

        deathsCommand.then(LiteralArgumentBuilder.<CommandSourceStack>literal("reset")
                .then(RequiredArgumentBuilder.<CommandSourceStack, EntitySelector>argument("player", EntityArgument.player())
                        .executes(WayfinderDeathsCommand::resetPlayerDeaths)));

        return deathsCommand;
    }

    private static int listDeaths(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSuccess(() -> Component.translatable("wayfinder.commands.deaths.list.start"), false);
        context.getSource().getServer().getPlayerList().getPlayers().forEach(player ->
                context.getSource().sendSuccess(() -> Component.translatable("wayfinder.commands.deaths.list.player", player.getDisplayName(), PlatformHandler.PLATFORM_HANDLER.getWayfinderDeaths(player)), false));
        return 1;
    }

    private static int showSelectedPlayerDeaths(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        context.getSource().sendSuccess(() ->
                        Component.translatable("wayfinder.commands.deaths.show.other", player.getDisplayName(), PlatformHandler.PLATFORM_HANDLER.getWayfinderDeaths(player)),
                false
        );
        return 1;
    }

    private static int showPlayerDeaths(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        context.getSource().sendSuccess(() ->
                        Component.translatable("wayfinder.commands.deaths.show.self", PlatformHandler.PLATFORM_HANDLER.getWayfinderDeaths(player)),
                false
        );
        return 1;
    }

    private static int resetPlayerDeaths(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        // Example logic for resetting a player's deaths
        PlatformHandler.PLATFORM_HANDLER.resetWayfinderDeaths(player);
        context.getSource().sendSuccess(() -> Component.translatable("wayfinder.commands.deaths.reset", player.getDisplayName()), true);
        return 1;
    }
}
