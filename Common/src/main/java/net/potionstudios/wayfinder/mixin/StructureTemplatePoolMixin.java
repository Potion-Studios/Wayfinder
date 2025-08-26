package net.potionstudios.wayfinder.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.level.levelgen.structure.village.VillageShrines;
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

        String pool = original.getFirst().toString();

        if (!wayfinder$hasShrine && pool.contains("houses")) {
            Wayfinder.LOGGER.info("[Wayfinder] Injecting shrine into pool");

            int start = pool.indexOf('/') + 1;
            int end = pool.indexOf("/houses");
            String result = pool.substring(start, end);
            Wayfinder.LOGGER.info("[Wayfinder] Detected village type: {}", result);

            StructurePoolElement shrine = VillageShrines.getVillageShrine(result).getShrineElement();
            if (shrine == null) return;

            cir.setReturnValue(List.of(shrine));
            wayfinder$hasShrine = true;
        }
    }

}
