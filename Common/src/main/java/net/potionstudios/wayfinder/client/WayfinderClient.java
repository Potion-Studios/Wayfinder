package net.potionstudios.wayfinder.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.potionstudios.wayfinder.world.entity.WayfinderEntities;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderRenderer;

import java.util.function.BiConsumer;

public class WayfinderClient {

    /**
     * Registers the entity renderers.
     * @see EntityRenderers
     * @see WayfinderEntities
     */
    public static void registerEntityRenderers(BiConsumer<EntityType<? extends Entity>, EntityRendererProvider> consumer) {
        consumer.accept(WayfinderEntities.WAYFINDER.get(), WayfinderRenderer::new);
    }
}
