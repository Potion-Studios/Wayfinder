package net.potionstudios.wayfinder.world.level.levelgen.structure.village;

import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.level.levelgen.structure.processor.WayfinderStructureProcessorLists;

public enum VillageShrines {

    PLAINS("plains", WayfinderStructureProcessorLists.PLAINS_SHRINE),
    DESERT("desert", WayfinderStructureProcessorLists.DESERT_SHRINE),
    SAVANNA("savanna"),
    SNOWY("snowy", WayfinderStructureProcessorLists.SNOWY_SHRINE),
    TAIGA("taiga", WayfinderStructureProcessorLists.TAIGA_SHRINE),
    RED_ROCK("red_rock"),
    SKYRIS("skyris"),
    SALEM("salem"),
    SWAMP("swamp");

    private final String id;
    private final ResourceKey<StructureProcessorList> processorList;
    private StructurePoolElement shrineElement;

    VillageShrines(String id, ResourceKey<StructureProcessorList> processorList) {
        this.id = id;
        this.processorList = processorList;
    }

    VillageShrines(String id) {
        this(id, ProcessorLists.EMPTY);
    }

    public static void setUp(RegistryAccess registryAccess) {
        for (VillageShrines shrine : values())
            shrine.shrineElement = StructurePoolElement.single(Wayfinder.id("wayfinder_" + shrine.id + "_shrine").toString(), registryAccess.registryOrThrow(Registries.PROCESSOR_LIST).getHolderOrThrow(shrine.processorList))
                    .apply(StructureTemplatePool.Projection.RIGID);
    }

    public StructurePoolElement getShrineElement() {
        return shrineElement;
    }

    public static VillageShrines getVillageShrine(String id) {
        for (VillageShrines shrine : values()) {
            if (shrine.id.contains(id)) {
                return shrine;
            }
        }
        return PLAINS; // Default to PLAINS if not found
    }
}
