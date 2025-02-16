package net.potionstudios.wayfinder.forge.networking;

import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import net.potionstudios.wayfinder.Wayfinder;

public class ForgeNetworking {

	private static final String PROTOCOL_VERSION = "1";

	public static final SimpleChannel INSTANCE = ChannelBuilder.named(Wayfinder.id("main")).networkProtocolVersion(1).simpleChannel();

}
