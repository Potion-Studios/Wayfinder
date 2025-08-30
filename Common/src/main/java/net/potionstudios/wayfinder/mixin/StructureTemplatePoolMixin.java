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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Mixin(StructureTemplatePool.class)
public abstract class StructureTemplatePoolMixin {

    // Track "already injected" once per jigsaw generation run using the RandomSource identity.
    // Weak keys so entries are GC'd after generation completes.
    @Unique
    private static final Map<RandomSource, Boolean> WAYFINDER$INJECTED_ONCE =
            Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Inject after vanilla returns the shuffled list from getShuffledTemplates.
     * On the first time we hit a houses pool during a jigsaw run, force the shrine as the only choice
     * to guarantee exactly one shrine per village.
     */
    @Inject(method = "getShuffledTemplates", at = @At("RETURN"), cancellable = true)
    private void wayfinder$injectShrine(RandomSource random, CallbackInfoReturnable<List<StructurePoolElement>> cir) {
        List<StructurePoolElement> original = cir.getReturnValue();
        if (original == null || original.isEmpty()) return;

        // Only once per jigsaw placement run
        if (Boolean.TRUE.equals(WAYFINDER$INJECTED_ONCE.get(random))) return;

        // Try to detect a houses pool by inspecting the elements.
        // Fallback approach because StructureTemplatePool doesn't expose its registry name in this environment.
        String villageType = null;
        for (StructurePoolElement element : original) {
            // Many SinglePoolElement toStrings include the template path like "minecraft:village/plains/houses/..."
            String s = String.valueOf(element);
            int villageIdx = s.indexOf("village/");
            if (villageIdx >= 0) {
                int typeStart = villageIdx + "village/".length();
                int housesIdx = s.indexOf("/houses", typeStart);
                if (housesIdx > typeStart) {
                    villageType = s.substring(typeStart, housesIdx);
                    break;
                }
            }
        }

        if (villageType == null) return;

        var shrineHolder = VillageShrines.getVillageShrine(villageType);
        if (shrineHolder == null) {
            Wayfinder.LOGGER.warn("[Wayfinder] No shrine holder for village type '{}'", villageType);
            return;
        }

        StructurePoolElement shrine = shrineHolder.getShrineElement();
        if (shrine == null) {
            Wayfinder.LOGGER.warn("[Wayfinder] No shrine element found for village type '{}'", villageType);
            return;
        }

        Wayfinder.LOGGER.info("[Wayfinder] Injecting shrine for village type '{}' (once per jigsaw run)", villageType);

        // Force shrine pick now to guarantee at least one spawn
        cir.setReturnValue(List.of(shrine));
        WAYFINDER$INJECTED_ONCE.put(random, true);
    }
}