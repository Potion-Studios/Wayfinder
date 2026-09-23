import com.hypherionmc.modpublisher.properties.ModLoader

plugins {
    id("com.gradleup.shadow")
}

architectury {
    platformSetupLoomIde()
    forge()
}

val minecraftVersion = providers.gradleProperty("minecraft_version").get()

configurations {
    val common = register("common")
    register("shadowCommon")
    compileClasspath.get().extendsFrom(common.get())
    runtimeClasspath.get().extendsFrom(common.get())
    named("developmentForge") { extendsFrom(common.get()) }
}

loom {
    accessWidenerPath.set(project(":Common").loom.accessWidenerPath)
    forge.convertAccessWideners(tasks.shadowJar, "wayfinder.accessWidener")
}

dependencies {
    forge("net.minecraftforge:forge:$minecraftVersion-${providers.gradleProperty("forge_version").get()}")

    "common"(project(":Common")) { isTransitive = false }
    "shadowCommon"(project(":Common", "transformProductionForge"))

    localRuntime("me.djtheredstoner:DevAuth-forge-latest:${providers.gradleProperty("devauth_version").get()}")

    api("com.geckolib:geckolib-forge-$minecraftVersion:${providers.gradleProperty("geckolib_version").get()}")

    compileOnly("net.luckperms:api:5.5")
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand(mapOf("version" to project.version))
        }
    }

    jar.get().archiveClassifier.set("raw")

    shadowJar {
        dependsOn(jar)
        from(zipTree(jar.get().archiveFile))
        exclude("architectury.common.json", ".cache/**")
        configurations = listOf(project.configurations.getByName("shadowCommon"))
        archiveClassifier.set(null)
    }
}

publisher {
    setLoaders(ModLoader.FORGE)
    curseDepends.required.set(mutableListOf("geckolib"))
    modrinthDepends.required.set(mutableListOf("geckolib"))
    curseDepends.optional.set(mutableListOf("oh-the-biomes-weve-gone"))
    modrinthDepends.optional.set(mutableListOf("oh-the-biomes-weve-gone"))
}
