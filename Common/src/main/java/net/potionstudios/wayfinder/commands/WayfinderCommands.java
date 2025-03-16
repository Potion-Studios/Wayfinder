package net.potionstudios.wayfinder.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.potionstudios.wayfinder.Wayfinder;

import java.util.function.Consumer;

public class WayfinderCommands {
    public static void register(Consumer<LiteralArgumentBuilder<CommandSourceStack>> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> base = LiteralArgumentBuilder.literal(Wayfinder.MOD_ID);
        base.then(WayfinderDeathsCommand.register());
        base.then(WayfinderReloadCommand.register());
        base.then(WayfinderRemoveCommand.register());
        dispatcher.accept(base);
    }
}
