package net.potionstudios.wayfinder.references;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.potionstudios.wayfinder.Wayfinder;

public class WayfinderBlockIds {
    public static final ResourceKey<Block> WAYFINDER_HEART = create("wayfinder_heart");

    private static ResourceKey<Block> create(String name) {
        return Wayfinder.key(Registries.BLOCK, name);
    }
}
