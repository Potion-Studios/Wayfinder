package net.potionstudios.wayfinder.client.renderer.entity;

import net.minecraft.resources.ResourceLocation;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib.model.GeoModel;

class WayfinderModel<T extends WayfinderEntity> extends GeoModel<T> {
    @Override
    public ResourceLocation getModelResource(@NotNull T animatable) {
        if (animatable.isScared() && animatable.hasShield())
            return Wayfinder.id("geo/wayfinder_shield.geo.json");
        return Wayfinder.id("geo/wayfinder.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(@NotNull T animatable) {
        if (animatable.isScared() && animatable.hasShield())
            if (animatable.shield() == WayfinderEntity.SHIELD.FULL)
                return Wayfinder.id("textures/entity/wayfinder/wayfinder_shield.png");
            else return Wayfinder.id("textures/entity/wayfinder/wayfinder_shield_broken.png");
        return Wayfinder.id("textures/entity/wayfinder/wayfinder.png");
    }

    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return Wayfinder.id("animations/wayfinder.animation.json");
    }
}
