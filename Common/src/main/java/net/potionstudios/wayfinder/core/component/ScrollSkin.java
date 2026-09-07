package net.potionstudios.wayfinder.core.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

public record ScrollSkin(Identifier skinId) {
	public static final Codec<ScrollSkin> CODEC = Identifier.CODEC
			.xmap(ScrollSkin::new, ScrollSkin::skinId);
	public static final StreamCodec<ByteBuf, ScrollSkin> STREAM_CODEC =
			Identifier.STREAM_CODEC.map(ScrollSkin::new, ScrollSkin::skinId);
}
