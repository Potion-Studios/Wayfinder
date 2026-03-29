package net.potionstudios.wayfinder.mixin;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
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

    @ModifyArg(method = "tryPlacingChildren(Lnet/minecraft/world/level/levelgen/structure/PoolElementStructurePiece;Lorg/apache/commons/lang3/mutable/MutableObject;IZLnet/minecraft/world/level/LevelHeightAccessor;Lnet/minecraft/world/level/levelgen/RandomState;Lnet/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasLookup;Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/Registry;get(Lnet/minecraft/resources/ResourceKey;)Ljava/util/Optional;"))
    private ResourceKey<StructureTemplatePool> forceShrinePool(ResourceKey<StructureTemplatePool> resourceKey) {
        if (!wayfinder$hasShrine) {
            String poolPath = resourceKey.identifier().getPath();
            if (poolPath.endsWith("/houses")) {
                Identifier wayfinderPoolName = Wayfinder.id(poolPath.replace("/houses", "/wayfinder_shrine"));
                ResourceKey<StructureTemplatePool> wayfinderPoolKey = ResourceKey.create(Registries.TEMPLATE_POOL, wayfinderPoolName);
                if (pools.get(wayfinderPoolKey).isPresent()) {
                    wayfinder$hasShrine = true;
                    return wayfinderPoolKey;
                }
            }
        }
        return resourceKey;
    }
}
