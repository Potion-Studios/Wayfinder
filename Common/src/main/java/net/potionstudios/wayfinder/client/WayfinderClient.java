package net.potionstudios.wayfinder.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.potionstudios.wayfinder.world.entity.WayfinderEntityType;
import net.potionstudios.wayfinder.client.renderer.entity.WayfinderRenderer;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

import java.util.function.BiConsumer;

public class WayfinderClient {

    /**
     * Registers the entity renderers.
     * @see EntityRenderers
     * @see WayfinderEntityType
     */
    public static void registerEntityRenderers(BiConsumer<EntityType<WayfinderEntity>, EntityRendererProvider<WayfinderEntity>> consumer) {
        consumer.accept(WayfinderEntityType.WAYFINDER.get(), WayfinderRenderer::new);
    }
}
