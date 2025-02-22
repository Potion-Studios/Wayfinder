package net.potionstudios.wayfinder.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.potionstudios.wayfinder.Wayfinder;

public final class WayfinderBiomeTags {

	/** Biomes that should be excluded from the Wayfinder.*/
	public static final TagKey<Biome> WAYFINDER_EXCLUDED = create("excluded");

	private static TagKey<Biome> create(String id) {
		return TagKey.create(Registries.BIOME, Wayfinder.id(id));
	}
}
