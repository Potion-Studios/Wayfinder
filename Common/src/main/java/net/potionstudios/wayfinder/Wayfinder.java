package net.potionstudios.wayfinder;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.slf4j.Logger;

import java.util.function.BiConsumer;

public class Wayfinder {

    /** The mod id for wayfinder. */
    public static final String MOD_ID = "wayfinder";

    /** The logger for wayfinder. */
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Initializes the mod.
     */
    public static void init() {

    }

    /**
     * Registers Entity Attributes
     */
    public static void registerEntityAttributes(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier> consumer) {
        consumer.accept(null, WayfinderEntity.createAttributes().build());
    }

    /**
     * Creates a new ResourceLocation with the wayfinder namespace.
     *
     * @param path The path of the resource location.
     * @return The new ResourceLocation.
     */
    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
