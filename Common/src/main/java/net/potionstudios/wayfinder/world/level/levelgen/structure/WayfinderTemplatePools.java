package net.potionstudios.wayfinder.world.level.levelgen.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.level.levelgen.structure.processor.WayfinderStructureProcessorLists;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class WayfinderTemplatePools {

    public static final Map<ResourceKey<StructureTemplatePool>, TemplatePoolFactory> TEMPLATE_POOL_FACTORIES = new Reference2ObjectOpenHashMap<>();

    private static final ResourceKey<StructureTemplatePool> PLAINS_SHRINE = register("village/plains/wayfinder_shrine", templatePoolFactoryContext ->
            createTemplatePool(
                    getPool(templatePoolFactoryContext, ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.withDefaultNamespace("village/plains/houses"))),
                    ImmutableList.of(Pair.of(StructurePoolElement.single("wayfinder:wayfinder_grassy_shrine", getProcessor(templatePoolFactoryContext, WayfinderStructureProcessorLists.GRASSY_SHRINE)), 1)), StructureTemplatePool.Projection.RIGID)
    );

    private static final ResourceKey<StructureTemplatePool> SAVANNA_SHRINE = register("village/savanna/wayfinder_shrine", templatePoolFactoryContext ->
            createTemplatePool(
                    getPool(templatePoolFactoryContext, ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.withDefaultNamespace("village/savanna/houses"))),
                    ImmutableList.of(Pair.of(StructurePoolElement.single("wayfinder:wayfinder_grassy_shrine", getProcessor(templatePoolFactoryContext, WayfinderStructureProcessorLists.GRASSY_SHRINE)), 1)), StructureTemplatePool.Projection.RIGID)
    );

    private static final ResourceKey<StructureTemplatePool> TAIGA_SHRINE = register("village/taiga/wayfinder_shrine", templatePoolFactoryContext ->
            createTemplatePool(
                    getPool(templatePoolFactoryContext, ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.withDefaultNamespace("village/taiga/houses"))),
                    ImmutableList.of(Pair.of(StructurePoolElement.single("wayfinder:wayfinder_taiga_shrine", getProcessor(templatePoolFactoryContext, WayfinderStructureProcessorLists.TAIGA_SHRINE)), 1)), StructureTemplatePool.Projection.RIGID)
    );

    private static final ResourceKey<StructureTemplatePool> DESERT_SHRINE = register("village/desert/wayfinder_shrine", templatePoolFactoryContext ->
            createTemplatePool(
                    getPool(templatePoolFactoryContext, ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.withDefaultNamespace("village/desert/houses"))),
                    ImmutableList.of(Pair.of(StructurePoolElement.single("wayfinder:wayfinder_desert_shrine", getProcessor(templatePoolFactoryContext, WayfinderStructureProcessorLists.DESERT_SHRINE)), 1)), StructureTemplatePool.Projection.RIGID)
    );

    private static final ResourceKey<StructureTemplatePool> SNOWY_SHRINE = register("village/snowy/wayfinder_shrine", templatePoolFactoryContext ->
            createTemplatePool(
                    getPool(templatePoolFactoryContext, ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.withDefaultNamespace("village/snowy/houses"))),
                    ImmutableList.of(Pair.of(StructurePoolElement.single("wayfinder:wayfinder_snowy_shrine", getProcessor(templatePoolFactoryContext, WayfinderStructureProcessorLists.SNOWY_SHRINE)), 1)), StructureTemplatePool.Projection.RIGID)
    );

    //Oh The Biomes We've Gone
    private static final ResourceKey<StructureTemplatePool> RED_ROCK_SHRINE = register("village/red_rock/wayfinder_shrine", templatePoolFactoryContext ->
            createTemplatePool(
                    getPool(templatePoolFactoryContext, ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath("biomeswevegone", "village/red_rock/houses"))),
                    ImmutableList.of(Pair.of(StructurePoolElement.single("wayfinder:wayfinder_red_rock_shrine", getProcessor(templatePoolFactoryContext, ProcessorLists.EMPTY)), 1)), StructureTemplatePool.Projection.RIGID)
    );

    private static final ResourceKey<StructureTemplatePool> SALEM_SHRINE = register("village/salem/wayfinder_shrine", templatePoolFactoryContext ->
            createTemplatePool(
                    getPool(templatePoolFactoryContext, ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath("biomeswevegone", "village/salem/houses"))),
                    ImmutableList.of(Pair.of(StructurePoolElement.single("wayfinder:wayfinder_salem_shrine", getProcessor(templatePoolFactoryContext, ProcessorLists.EMPTY)), 1)), StructureTemplatePool.Projection.RIGID)
    );

    private static final ResourceKey<StructureTemplatePool> SKYRIS_SHRINE = register("village/slyris/wayfinder_shrine", templatePoolFactoryContext ->
            createTemplatePool(
                    getPool(templatePoolFactoryContext, ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath("biomeswevegone", "village/skyris/houses"))),
                    ImmutableList.of(Pair.of(StructurePoolElement.single("wayfinder:wayfinder_skyris_shrine", getProcessor(templatePoolFactoryContext, ProcessorLists.EMPTY)), 1)), StructureTemplatePool.Projection.RIGID)
    );

    private static final ResourceKey<StructureTemplatePool> SWAMPY_SHRINE = register("village/swampy/wayfinder_shrine", templatePoolFactoryContext ->
            createTemplatePool(
                    getPool(templatePoolFactoryContext, ResourceKey.create(Registries.TEMPLATE_POOL, ResourceLocation.fromNamespaceAndPath("biomeswevegone", "village/swampy/houses"))),
                    ImmutableList.of(Pair.of(StructurePoolElement.single("wayfinder:wayfinder_swampy_shrine", getProcessor(templatePoolFactoryContext, ProcessorLists.EMPTY)), 1)), StructureTemplatePool.Projection.RIGID)
    );

    private static StructureTemplatePool createTemplatePool(Holder<StructureTemplatePool> fallback, List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> rawTemplateFactories, StructureTemplatePool.Projection projection) {
        return new StructureTemplatePool(fallback, rawTemplateFactories, projection);
    }

    private static ResourceKey<StructureTemplatePool> register(String id, TemplatePoolFactory factory) {
        ResourceKey<StructureTemplatePool> templatePoolResourceKey = Wayfinder.key(Registries.TEMPLATE_POOL, id);
        TEMPLATE_POOL_FACTORIES.put(templatePoolResourceKey, factory);
        return templatePoolResourceKey;
    }

    private static Holder.Reference<StructureTemplatePool> getPool(BootstrapContext<StructureTemplatePool> context, ResourceKey<StructureTemplatePool> poolResourceKey) {
        return context.lookup(Registries.TEMPLATE_POOL).getOrThrow(poolResourceKey);
    }

    private static Holder.Reference<StructureProcessorList> getProcessor(BootstrapContext<StructureTemplatePool> context, ResourceKey<StructureProcessorList> processorList) {
        return context.lookup(Registries.PROCESSOR_LIST).getOrThrow(processorList);
    }

    public static void templatePools() {
        Wayfinder.LOGGER.info("Registering Wayfinder Template Pools");
    }

    @FunctionalInterface
    public interface TemplatePoolFactory {
        StructureTemplatePool generate(BootstrapContext<StructureTemplatePool> templatePoolFactoryContext);
    }
}
