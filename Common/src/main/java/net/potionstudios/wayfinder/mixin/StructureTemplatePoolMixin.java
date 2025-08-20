package net.potionstudios.wayfinder.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.potionstudios.wayfinder.Wayfinder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(StructureTemplatePool.class)
public abstract class StructureTemplatePoolMixin {

    @Unique
    private boolean wayfinder$hasShrine;

    /**
     * Inject after vanilla returns the shuffled list from getShuffledTemplates.
     * We prepend a SinglePoolElement shrine only once per village.
     */
    @Inject(method = "getShuffledTemplates", at = @At("RETURN"), cancellable = true)
    private void wayfinder$injectShrine(RandomSource random, CallbackInfoReturnable<List<StructurePoolElement>> cir) {
        List<StructurePoolElement> original = cir.getReturnValue();
        if (original.isEmpty()) return;

        if (!wayfinder$hasShrine && original.getFirst().toString().contains("houses")) {
            Wayfinder.LOGGER.info("[Wayfinder] Injecting shrine into pool");

            List<StructurePoolElement> modified = new ArrayList<>();

            StructurePoolElement shrine = StructurePoolElement.single(
                    Wayfinder.id("wayfinder_grassy_shrine").toString()
            ).apply(StructureTemplatePool.Projection.RIGID);
            modified.addFirst(shrine);

            modified.addAll(original);

            cir.setReturnValue(modified);
            wayfinder$hasShrine = true;
        }
    }

}
