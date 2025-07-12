package net.potionstudios.wayfinder.world.level.levelgen.structure.village;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.level.levelgen.structure.processor.WayfinderStructureProcessorLists;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to add structures to villages.
 * We do this by adding the structures to the existing village structure pool.
 * @author Joseph T. McQuigg
 */
public class PlaceInVillage {

    /**
     * Adds a building to a village structure pool.
     *
     * @param server         The Server.
     * @param poolRL         The pool to add the building to.
     * @param processorList  The processor list to use.
     * @param nbtPieceRL     The nbt piece to add.
     * @param projection     The projection to use.
     * @param weight         The weight of the building.
     */
    private static void addBuildingToPool(@NotNull MinecraftServer server, ResourceLocation poolRL, ResourceKey<StructureProcessorList> processorList, ResourceLocation nbtPieceRL, StructureTemplatePool.Projection projection, int weight) {
        RegistryAccess.Frozen serverRegistry = server.registryAccess();
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

    /**
     * Adds structures to villages.
     * @param server The server to add the structures to.
     */
    public static void addStructuresToVillages(@NotNull MinecraftServer server) {
        addBuildingToPool(server, getMcRL("plains/houses"), WayfinderStructureProcessorLists.GRASSY_SHRINE, Wayfinder.id("wayfinder_grassy_shrine"), StructureTemplatePool.Projection.RIGID, 2);
        addBuildingToPool(server, getMcRL("savanna/houses"), WayfinderStructureProcessorLists.GRASSY_SHRINE, Wayfinder.id("wayfinder_grassy_shrine"), StructureTemplatePool.Projection.RIGID, 2);
        addBuildingToPool(server, getMcRL("taiga/houses"), WayfinderStructureProcessorLists.TAIGA_SHRINE, Wayfinder.id("wayfinder_taiga_shrine"), StructureTemplatePool.Projection.RIGID, 2);
        addBuildingToPool(server, getMcRL("desert/houses"), WayfinderStructureProcessorLists.DESERT_SHRINE, Wayfinder.id("wayfinder_desert_shrine"), StructureTemplatePool.Projection.RIGID, 2);
        addBuildingToPool(server, getMcRL("snowy/houses"), WayfinderStructureProcessorLists.SNOWY_SHRINE, Wayfinder.id("wayfinder_snowy_shrine"), StructureTemplatePool.Projection.RIGID, 2);
    }

    private static ResourceLocation getMcRL(String poolName) {
        return ResourceLocation.withDefaultNamespace("village/" + poolName);
    }
}
