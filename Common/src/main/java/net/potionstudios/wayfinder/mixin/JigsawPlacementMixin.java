package net.potionstudios.wayfinder.mixin;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.potionstudios.wayfinder.Wayfinder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(JigsawPlacement.Placer.class)
public abstract class JigsawPlacementMixin {

    @Shadow @Final private Registry<StructureTemplatePool> pools;

    @Unique
    private boolean wayfinder$hasShrine;

    @ModifyArg(method = "tryPlacingChildren", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;getHolder(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;"))
    private ResourceKey<StructureTemplatePool> forceShrinePool(ResourceKey<StructureTemplatePool> resourceKey) {
        if (!wayfinder$hasShrine) {
            String poolPath = resourceKey.location().getPath();
            if (poolPath.endsWith("/houses")) {
                ResourceLocation wayfinderPoolName = Wayfinder.id(poolPath.replace("/houses", "/wayfinder_shrine"));
                ResourceKey<StructureTemplatePool> wayfinderPoolKey = ResourceKey.create(Registries.TEMPLATE_POOL, wayfinderPoolName);
                if (pools.getHolder(wayfinderPoolKey).isPresent()) {
                    wayfinder$hasShrine = true;
                    return wayfinderPoolKey;
                }
            }
        }
        return resourceKey;
    }
}
