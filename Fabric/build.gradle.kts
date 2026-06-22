import com.hypherionmc.modpublisher.properties.ModLoader

plugins {
    id("com.gradleup.shadow")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

val minecraftVersion = providers.gradleProperty("minecraft_version").get()

configurations {
    create("common")
    "common" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    create("shadowBundle")
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    getByName("developmentFabric").extendsFrom(configurations["common"])
    "shadowBundle" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

loom.accessWidenerPath.set(project(":Common").loom.accessWidenerPath)

dependencies {
    implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("fabric_loader_version").get()}")
    api("net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}+$minecraftVersion")

    "common"(project(":Common")) { isTransitive = false }
    "shadowBundle"(project(":Common", "transformProductionFabric"))

    localRuntime("me.djtheredstoner:DevAuth-fabric:${providers.gradleProperty("devauth_version").get()}")

    api("com.geckolib:geckolib-fabric-$minecraftVersion:${providers.gradleProperty("geckolib_version").get()}")
    api("me.lucko:fabric-permissions-api:0.7.0")
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to project.version))
        }
    }

    shadowJar {
        exclude("architectury.common.json", ".cache/**")
        configurations = listOf(project.configurations.getByName("shadowBundle"))
        archiveClassifier.set(null)
    }
}

publisher {
    setLoaders(ModLoader.FABRIC, ModLoader.QUILT)
    curseDepends.required.set(mutableListOf("fabric-api", "geckolib"))
    modrinthDepends.required.set(mutableListOf("fabric-api", "geckolib"))
    curseDepends.optional.set(mutableListOf("oh-the-biomes-weve-gone"))
    modrinthDepends.optional.set(mutableListOf("oh-the-biomes-weve-gone"))
}
