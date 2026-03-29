package net.potionstudios.wayfinder.client.renderer.entity;

import net.minecraft.resources.Identifier;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jspecify.annotations.NonNull;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.base.GeoRenderState;

class WayfinderModel<T extends WayfinderEntity> extends GeoModel<T> {

    @Override
    public @NonNull Identifier getModelResource(@NonNull GeoRenderState geoRenderState) {
        if (geoRenderState.getGeckolibData(WayfinderRenderer.PANIC) && geoRenderState.getGeckolibData(WayfinderRenderer.SHIELD) > 0)
            return Wayfinder.id("wayfinder_shield");
        return Wayfinder.id("wayfinder");
    }

    @Override
    public @NonNull Identifier getTextureResource(@NonNull GeoRenderState geoRenderState) {
        String variant = geoRenderState.getGeckolibData(WayfinderRenderer.VARIANT);
        int shield = geoRenderState.getGeckolibData(WayfinderRenderer.SHIELD);
        boolean panic = geoRenderState.getGeckolibData(WayfinderRenderer.PANIC);
        if (panic && shield > 0)
            if (shield == 2)
                return Wayfinder.id("textures/entity/wayfinder/" + variant + "/wayfinder_shield.png");
            else return Wayfinder.id("textures/entity/wayfinder/" + variant + "/wayfinder_shield_broken.png");
        return Wayfinder.id("textures/entity/wayfinder/" + variant + "/wayfinder.png");
    }

    @Override
    public Identifier getAnimationResource(T animatable) {
        return Wayfinder.id("wayfinder");
    }
}
