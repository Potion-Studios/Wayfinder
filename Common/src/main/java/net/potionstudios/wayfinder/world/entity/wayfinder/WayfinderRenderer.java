package net.potionstudios.wayfinder.world.entity.wayfinder;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WayfinderRenderer<T extends WayfinderEntity> extends GeoEntityRenderer<T> {
    public WayfinderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WayfinderModel<>());
    }
}
