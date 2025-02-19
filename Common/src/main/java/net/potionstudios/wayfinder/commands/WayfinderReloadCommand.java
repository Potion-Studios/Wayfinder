package net.potionstudios.wayfinder.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.config.Config;
import net.potionstudios.wayfinder.config.ConfigLoader;

import java.util.function.Consumer;

public class WayfinderReloadCommand {

	public static void register(Consumer<LiteralArgumentBuilder<CommandSourceStack>> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> root = LiteralArgumentBuilder.literal("wayfinder");

		LiteralArgumentBuilder<CommandSourceStack> command = LiteralArgumentBuilder.literal("reload");
		command.requires(commandSourceStack -> commandSourceStack.hasPermission(2));
		command.executes(context -> {
			Wayfinder.CONFIG = ConfigLoader.loadConfig(Config.class);
			context.getSource().sendSuccess(() -> Component.translatable("wayfinder.commands.reload.success").withStyle(ChatFormatting.GREEN), true);
			return 1;
		});

		dispatcher.accept(root.then(command));
	}
}
