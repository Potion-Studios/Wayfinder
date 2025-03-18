package net.potionstudios.wayfinder.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

import java.util.UUID;

class WayfinderRemoveCommand {
	static LiteralArgumentBuilder<CommandSourceStack> register() {
		LiteralArgumentBuilder<CommandSourceStack> removeCommand = LiteralArgumentBuilder.literal("remove");
		removeCommand.requires(commandSourceStack -> commandSourceStack.hasPermission(2));

		removeCommand.executes(WayfinderRemoveCommand::removeWayfinder)
				.then(RequiredArgumentBuilder.<CommandSourceStack, EntitySelector>argument("player", EntityArgument.player())
						.executes(WayfinderRemoveCommand::removeSelectedPlayerWayfinder)
							.then(RequiredArgumentBuilder.<CommandSourceStack, Boolean>argument("kill", BoolArgumentType.bool())
								.executes(WayfinderRemoveCommand::removeSelectedPlayerWayfinderKill)));

		return removeCommand;
	}

	private static int removeWayfinder(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		return removeWayfinder(context.getSource().getPlayerOrException(), context);
	}

	private static int removeSelectedPlayerWayfinder(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		return removeWayfinder(EntityArgument.getPlayer(context, "player"), context);
	}

	private static int removeSelectedPlayerWayfinderKill(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = EntityArgument.getPlayer(context, "player");
		if (BoolArgumentType.getBool(context, "kill")) {
			if (PlatformHandler.PLATFORM_HANDLER.hasWayfinder(player)) {
				UUID wayfinderId = PlatformHandler.PLATFORM_HANDLER.getWayfinder(player);
				PlatformHandler.PLATFORM_HANDLER.setWayfinder(player, Util.NIL_UUID);
				Entity entity = player.serverLevel().getEntity(wayfinderId);
				if (entity instanceof WayfinderEntity wayfinder) {
					wayfinder.discard();
					context.getSource().sendSuccess(() -> Component.translatable("wayfinder.commands.remove.kill.success", player.getDisplayName()).withStyle(ChatFormatting.GREEN), false);
					return 1;
				}
				context.getSource().sendSuccess(() -> Component.translatable("wayfinder.commands.remove.success", player.getDisplayName()).withStyle(ChatFormatting.GREEN), false);
				return 1;
			}
		} else return removeWayfinder(player, context);
		context.getSource().sendFailure(Component.translatable("wayfinder.commands.remove.fail", player.getDisplayName()).withStyle(ChatFormatting.RED));
		return 0;
	}

	private static int removeWayfinder(ServerPlayer player, CommandContext<CommandSourceStack> context) {
		if (PlatformHandler.PLATFORM_HANDLER.hasWayfinder(player)) {
			UUID wayfinderId = PlatformHandler.PLATFORM_HANDLER.getWayfinder(player);
			PlatformHandler.PLATFORM_HANDLER.setWayfinder(player, Util.NIL_UUID);
			Entity entity = player.serverLevel().getEntity(wayfinderId);
			if (entity instanceof WayfinderEntity wayfinder)
				wayfinder.setOwnerUUID(null);
			context.getSource().sendSuccess(() -> Component.translatable("wayfinder.commands.remove.success", player.getDisplayName()).withStyle(ChatFormatting.GREEN), false);
			return 1;
		}
		context.getSource().sendFailure(Component.translatable("wayfinder.commands.remove.fail", player.getDisplayName()).withStyle(ChatFormatting.RED));
		return 0;
	}
}
