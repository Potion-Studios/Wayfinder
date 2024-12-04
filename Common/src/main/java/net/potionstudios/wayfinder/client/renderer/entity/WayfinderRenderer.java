package net.potionstudios.wayfinder.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WayfinderRenderer<T extends WayfinderEntity> extends GeoEntityRenderer<T> {
    public WayfinderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WayfinderModel<>());
    }

    @Override
    protected float getDeathMaxRotation(T animatable) {
        return 0;
    }
}
