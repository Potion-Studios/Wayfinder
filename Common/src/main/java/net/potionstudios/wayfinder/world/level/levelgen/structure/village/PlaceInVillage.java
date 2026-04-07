package net.potionstudios.wayfinder.world.level.levelgen.structure.village;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.potionstudios.biomeswevegone.BiomesWeveGone;
import net.potionstudios.biomeswevegone.world.level.levelgen.structure.BWGVillageTemplatePools;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.level.levelgen.structure.processor.WayfinderStructureProcessorLists;

import java.util.ArrayList;
import java.util.List;

public class PlaceInVillage {

    /**
     * Adds structures to villages.
     * @param server The server to add the structures to.
     */
    public static void addStructuresToVillages(MinecraftServer server) {
        RegistryAccess.Frozen registryAccess = server.registryAccess();
        addBuildingToPool(registryAccess, ResourceLocation.withDefaultNamespace("village/plains/houses"), WayfinderStructureProcessorLists.PLAINS_SHRINE, Wayfinder.id("wayfinder_plains_shrine"), StructureTemplatePool.Projection.RIGID, 2);
        addBuildingToPool(registryAccess, ResourceLocation.withDefaultNamespace("village/snowy/houses"), WayfinderStructureProcessorLists.SNOWY_SHRINE, Wayfinder.id("wayfinder_snowy_shrine"), StructureTemplatePool.Projection.RIGID, 2);
        addBuildingToPool(registryAccess, ResourceLocation.withDefaultNamespace("village/taiga/houses"), WayfinderStructureProcessorLists.TAIGA_SHRINE, Wayfinder.id("wayfinder_taiga_shrine"), StructureTemplatePool.Projection.RIGID, 2);
        addBuildingToPool(registryAccess, ResourceLocation.withDefaultNamespace("village/desert/houses"), WayfinderStructureProcessorLists.DESERT_SHRINE, Wayfinder.id("wayfinder_desert_shrine"), StructureTemplatePool.Projection.RIGID, 2);
        if (PlatformHandler.PLATFORM_HANDLER.isModLoaded(BiomesWeveGone.MOD_ID)) {
            addBuildingToPool(registryAccess, BWGVillageTemplatePools.RED_ROCK_HOUSES.location(), ProcessorLists.EMPTY, Wayfinder.id("wayfinder_red_rock_shrine"), StructureTemplatePool.Projection.RIGID, 2);
            addBuildingToPool(registryAccess, BWGVillageTemplatePools.SALEM_HOUSES.location(), ProcessorLists.EMPTY, Wayfinder.id("wayfinder_salem_shrine"), StructureTemplatePool.Projection.RIGID, 2);
            addBuildingToPool(registryAccess, BWGVillageTemplatePools.SALEM_HOUSES.location(), ProcessorLists.EMPTY, Wayfinder.id("wayfinder_salem_shrine_2"), StructureTemplatePool.Projection.RIGID, 2);
            addBuildingToPool(registryAccess, BWGVillageTemplatePools.SKYRIS_HOUSES.location(), ProcessorLists.EMPTY, Wayfinder.id("wayfinder_skyris_shrine"), StructureTemplatePool.Projection.RIGID, 2);
        }
    }

    /**
     * Adds a building to a village structure pool.
     *
     * @param serverRegistry The server registry.
     * @param poolRL         The pool to add the building to.
     * @param processorList  The processor list to use.
     * @param nbtPieceRL     The nbt piece to add.
     * @param projection     The projection to use.
     * @param weight         The weight of the building.
     */
    private static void addBuildingToPool(RegistryAccess.Frozen serverRegistry, ResourceLocation poolRL, ResourceKey<StructureProcessorList> processorList, ResourceLocation nbtPieceRL, StructureTemplatePool.Projection projection, int weight) {
        Registry<StructureTemplatePool> templatePoolRegistry = serverRegistry.registry(Registries.TEMPLATE_POOL).orElseThrow();
        Registry<StructureProcessorList> processorListRegistry = serverRegistry.registry(Registries.PROCESSOR_LIST).orElseThrow();
        StructureTemplatePool pool = templatePoolRegistry.get(poolRL);
        Holder<StructureProcessorList> processorList1 = processorListRegistry.getHolderOrThrow(processorList);
        if (pool == null) return;

        SinglePoolElement piece = SinglePoolElement.single(nbtPieceRL.toString(), processorList1).apply(projection);

        for (int i = 0; i < weight; i++)
            pool.templates.add(piece);

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(pool.rawTemplates);
        listOfPieceEntries.add(new Pair<>(piece, weight));
        pool.rawTemplates = listOfPieceEntries;
    }
}
