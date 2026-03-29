package net.potionstudios.wayfinder;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.potionstudios.wayfinder.advancements.critereon.WayfinderCriteriaTriggers;
import net.potionstudios.wayfinder.config.Config;
import net.potionstudios.wayfinder.config.ConfigUtils;
import net.potionstudios.wayfinder.sounds.WayfinderSounds;
import net.potionstudios.wayfinder.tags.WayfinderEntityTypeTags;
import net.potionstudios.wayfinder.world.entity.WayfinderEntityType;
import net.potionstudios.wayfinder.world.entity.ai.memory.WayfinderMemoryModuleType;
import net.potionstudios.wayfinder.world.entity.ai.sensing.WayfinderSensorType;
import net.potionstudios.wayfinder.world.entity.block.WayfinderBlockEntityType;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import net.potionstudios.wayfinder.world.item.WayfinderItems;
import net.potionstudios.wayfinder.world.level.block.WayfinderBlocks;
import net.potionstudios.wayfinder.world.level.levelgen.structure.village.PlaceInVillage;
import org.slf4j.Logger;

import java.util.function.BiConsumer;

public class Wayfinder {

    /** The mod id for wayfinder. */
    public static final String MOD_ID = "wayfinder";

    /** The logger for wayfinder. */
    public static final Logger LOGGER = LogUtils.getLogger();

    /** The config for wayfinder. */
    public static Config CONFIG = ConfigUtils.loadConfig(Config.class);

    /**
     * Initializes the mod.
     */
    public static void init() {
        WayfinderItems.items();
        WayfinderBlocks.blocks();
        WayfinderBlockEntityType.blockEntities();
        WayfinderSounds.sounds();
        WayfinderMemoryModuleType.memoryModuleTypes();
        WayfinderSensorType.sensorType();
        WayfinderEntityType.entities();
        WayfinderCriteriaTriggers.criteriaTriggers();
    }

    /**
     * Runs when the Server starts
     * @param server the Server
     */
    public static void onServerStart(MinecraftServer server) {
        PlaceInVillage.addStructuresToVillages(server);
    }

    /**
     * Runs when an entity is loaded.
     * This is used to add an attack goal to monsters that scare wayfinders.
     * @param entity the entity that is loaded
     */
    public static void onEntityLoad(Entity entity) {
        if (entity instanceof Monster monster && monster.getType().is(WayfinderEntityTypeTags.SCARES_WAYFINDER))
            monster.goalSelector.addGoal(3, new NearestAttackableTargetGoal<>(monster, WayfinderEntity.class, true));
    }

    /**
     * Registers Entity Attributes
     */
    public static void registerEntityAttributes(BiConsumer<EntityType<? extends LivingEntity>, AttributeSupplier> consumer) {
        consumer.accept(WayfinderEntityType.WAYFINDER.get(), WayfinderEntity.createAttributes().build());
    }

    /**
     * Creates a new Identifier with the wayfinder namespace.
     *
     * @param path The path of the resource location.
     * @return The new Identifier.
     */
    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    /**
     * Creates a new resource key for Oh The Biomes We've Gone.
     * @param registryKey the registry key for the resource
     * @param name the name of the resource
     * @return the new resource key with the Biomes We've Gone location
     */
    public static <T> ResourceKey<T> key(ResourceKey<? extends Registry<T>> registryKey, String name) {
        return ResourceKey.create(registryKey, id(name));
    }
}
