package net.potionstudios.wayfinder.world.level.levelgen.structure.village;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
//import net.potionstudios.biomeswevegone.BiomesWeveGone;
//import net.potionstudios.biomeswevegone.world.level.levelgen.structure.BWGVillageTemplatePools;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.data.worldgen.WayfinderProcessorLists;

import java.util.ArrayList;
import java.util.List;

public class PlaceInVillage {

    /**
     * Adds structures to villages.
     * @param server The server to add the structures to.
     */
    public static void addStructuresToVillages(MinecraftServer server) {
        RegistryAccess.Frozen registryAccess = server.registryAccess();
        addBuildingToPool(registryAccess, Identifier.withDefaultNamespace("village/plains/houses"), WayfinderStructureProcessorLists.PLAINS_SHRINE, Wayfinder.id("wayfinder_plains_shrine"), StructureTemplatePool.Projection.RIGID, 2);
        addBuildingToPool(registryAccess, Identifier.withDefaultNamespace("village/snowy/houses"), WayfinderStructureProcessorLists.SNOWY_SHRINE, Wayfinder.id("wayfinder_snowy_shrine"), StructureTemplatePool.Projection.RIGID, 2);
        addBuildingToPool(registryAccess, Identifier.withDefaultNamespace("village/taiga/houses"), WayfinderStructureProcessorLists.TAIGA_SHRINE, Wayfinder.id("wayfinder_taiga_shrine"), StructureTemplatePool.Projection.RIGID, 2);
        addBuildingToPool(registryAccess, Identifier.withDefaultNamespace("village/desert/houses"), WayfinderStructureProcessorLists.DESERT_SHRINE, Wayfinder.id("wayfinder_desert_shrine"), StructureTemplatePool.Projection.RIGID, 2);
        if (PlatformHandler.PLATFORM_HANDLER.isModLoaded("biomeswevegone")) { //TODO: Once BWG Updates to 26.1+ remove the identifier hard coding move back to vars
            addBuildingToPool(registryAccess, Identifier.fromNamespaceAndPath("biomeswevegone", "village/red_rock/houses"), ProcessorLists.EMPTY, Wayfinder.id("wayfinder_red_rock_shrine"), StructureTemplatePool.Projection.RIGID, 2);
            addBuildingToPool(registryAccess, Identifier.fromNamespaceAndPath("biomeswevegone", "village/salem/houses"), ProcessorLists.EMPTY, Wayfinder.id("wayfinder_salem_shrine"), StructureTemplatePool.Projection.RIGID, 2);
            addBuildingToPool(registryAccess, Identifier.fromNamespaceAndPath("biomeswevegone", "village/swamp/houses"), ProcessorLists.EMPTY, Wayfinder.id("wayfinder_swamp_shrine"), StructureTemplatePool.Projection.RIGID, 2);
            addBuildingToPool(registryAccess, Identifier.fromNamespaceAndPath("biomeswevegone", "village/skyris/houses"), ProcessorLists.EMPTY, Wayfinder.id("wayfinder_skyris_shrine"), StructureTemplatePool.Projection.RIGID, 2);
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
    private static void addBuildingToPool(RegistryAccess.Frozen serverRegistry, Identifier poolRL, ResourceKey<StructureProcessorList> processorList, Identifier nbtPieceRL, StructureTemplatePool.Projection projection, int weight) {
        Registry<StructureTemplatePool> templatePoolRegistry = serverRegistry.lookup(Registries.TEMPLATE_POOL).orElseThrow();
        Registry<StructureProcessorList> processorListRegistry = serverRegistry.lookup(Registries.PROCESSOR_LIST).orElseThrow();
        StructureTemplatePool pool = templatePoolRegistry.getValue(poolRL);
        Holder<StructureProcessorList> processorList1 = processorListRegistry.getOrThrow(processorList);
        if (pool == null) return;

        SinglePoolElement piece = SinglePoolElement.single(nbtPieceRL.toString(), processorList1).apply(projection);

        for (int i = 0; i < weight; i++)
            pool.templates.add(piece);

        List<Pair<StructurePoolElement, Integer>> listOfPieceEntries = new ArrayList<>(pool.rawTemplates);
        listOfPieceEntries.add(new Pair<>(piece, weight));
        pool.rawTemplates = listOfPieceEntries;
    }
}
