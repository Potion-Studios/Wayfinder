package net.potionstudios.wayfinder.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jspecify.annotations.NonNull;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import org.jspecify.annotations.Nullable;

public class WayfinderRenderer<R extends EntityRenderState & GeoRenderState> extends GeoEntityRenderer<WayfinderEntity, @NonNull R> {
    protected static final DataTicket<String> VARIANT = DataTicket.create("variant", String.class);
    protected static final DataTicket<Integer> SHIELD = DataTicket.create("shield", Integer.class);
    protected static final DataTicket<Boolean> PANIC = DataTicket.create("panic", Boolean.class);

    public WayfinderRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WayfinderModel<>());
    }

    @Override
    protected float getDeathMaxRotation(@NonNull GeoRenderState renderState) {
        return 0;
    }

    @Override
    public @Nullable RenderType getRenderType(@NonNull R renderState, @NonNull Identifier texture) {
        if (renderState.getGeckolibData(PANIC) && renderState.getGeckolibData(SHIELD) == 0)
            return RenderTypes.entityTranslucent(texture, false);
        return super.getRenderType(renderState, texture);
    }

    @Override
    public void addRenderData(WayfinderEntity animatable, @Nullable Void relatedObject, @NonNull R renderState, float partialTick) {
        renderState.addGeckolibData(VARIANT, animatable.getVariant().getSerializedName());
        renderState.addGeckolibData(SHIELD, animatable.shield().ordinal());
        renderState.addGeckolibData(PANIC, animatable.isPanic());
    }
}
