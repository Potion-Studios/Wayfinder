package net.potionstudios.wayfinder.client.renderer.entity;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WayfinderRenderer<T extends WayfinderEntity> extends GeoEntityRenderer<T> {
    public WayfinderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WayfinderModel<>());
    }

    @Override
    protected float getDeathMaxRotation(T animatable) {
        return 0;
    }

    @Override
    public @Nullable RenderType getRenderType(T animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
	    if (animatable.isPanic() && animatable.hasShield())
            return RenderType.ENTITY_TRANSLUCENT.apply(texture, false);
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    }
}
