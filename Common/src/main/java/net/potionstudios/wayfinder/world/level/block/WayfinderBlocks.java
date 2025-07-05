package net.potionstudios.wayfinder.world.level.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.PushReaction;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.item.WayfinderItems;

import java.util.function.Supplier;

public class WayfinderBlocks {

    public static final Supplier<WayfinderHeartBlock> WAYFINER_HEART = registerBlockItem("wayfinder_heart", () -> new WayfinderHeartBlock(Block.Properties.ofFullCopy(Blocks.STONE).noLootTable().pushReaction(PushReaction.BLOCK)));

    private static <B extends Block> Supplier<B> registerBlockItem(String key, Supplier<B> blockSupplier) {
        Supplier<B> block = PlatformHandler.PLATFORM_HANDLER.register(BuiltInRegistries.BLOCK, key, blockSupplier);
        WayfinderItems.register(key, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    public static void blocks() {
        Wayfinder.LOGGER.info("Registering Wayfinder blocks");
    }
}
