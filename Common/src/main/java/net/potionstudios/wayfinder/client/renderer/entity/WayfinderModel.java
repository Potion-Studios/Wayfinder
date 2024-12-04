package net.potionstudios.wayfinder.client.renderer.entity;

import net.minecraft.resources.ResourceLocation;
import net.potionstudios.wayfinder.Wayfinder;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

class WayfinderModel <T extends GeoAnimatable> extends GeoModel<T> {
    @Override
    public ResourceLocation getModelResource(T animatable) {
        return Wayfinder.id("geo/wayfinder.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return Wayfinder.id("textures/entity/wayfinder/wayfinder.png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return Wayfinder.id("animations/wayfinder.animation.json");
    }
}
