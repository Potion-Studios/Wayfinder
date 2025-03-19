package net.potionstudios.wayfinder.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

class WayfinderLocateCommand {
    static LiteralArgumentBuilder<CommandSourceStack> register() {
        LiteralArgumentBuilder<CommandSourceStack> command = LiteralArgumentBuilder.literal("locate");
        command.requires(commandSourceStack -> commandSourceStack.hasPermission(2));

        command.executes(context -> {
            ServerPlayer player = context.getSource().getPlayerOrException();
            if (PlatformHandler.PLATFORM_HANDLER.hasWayfinder(player))
                if (player.serverLevel().getEntity(PlatformHandler.PLATFORM_HANDLER.getWayfinder(player)) instanceof WayfinderEntity wayfinder) {
                    context.getSource().sendSuccess(() -> Component.translatable("wayfinder.commands.locate.self.success", wayfinder.getX(), wayfinder.getY(), wayfinder.getZ()).withStyle(ChatFormatting.GREEN), false);
                    return 1;
            }
            context.getSource().sendFailure(Component.translatable("wayfinder.commands.locate.self.nowayfinder").withStyle(ChatFormatting.RED));
            return 0;
        });
        command
                .then(RequiredArgumentBuilder.<CommandSourceStack, EntitySelector>argument("player", EntityArgument.player())
                .executes(WayfinderLocateCommand::locateWayfinder));

        return command;
    }

    private static int locateWayfinder(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "player");
        if (PlatformHandler.PLATFORM_HANDLER.hasWayfinder(player))
            if (player.serverLevel().getEntity(PlatformHandler.PLATFORM_HANDLER.getWayfinder(player)) instanceof WayfinderEntity wayfinder) {
                context.getSource().sendSuccess(() -> Component.translatable("wayfinder.commands.locate.other.success", player.getDisplayName(), wayfinder.getX(), wayfinder.getY(), wayfinder.getZ()).withStyle(ChatFormatting.GREEN), false);
                return 1;
            }
        context.getSource().sendFailure(Component.translatable("wayfinder.commands.locate.other.nowayfinder", player.getDisplayName()).withStyle(ChatFormatting.RED));
        return 0;
    }
}
