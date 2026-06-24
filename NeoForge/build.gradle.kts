import com.hypherionmc.modpublisher.properties.ModLoader

plugins {
    id("com.gradleup.shadow")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

val minecraftVersion = providers.gradleProperty("minecraft_version").get()

configurations {
    val common = register("common")
    register("shadowCommon")
    compileClasspath.get().extendsFrom(common.get())
    runtimeClasspath.get().extendsFrom(common.get())
    named("developmentNeoForge") { extendsFrom(common.get()) }
}

loom {
    accessWidenerPath.set(project(":Common").loom.accessWidenerPath)

    runs.create("datagen") {
        clientData()
        programArguments.addAll(
            "--all", "--mod", "wayfinder",
            "--output", project(":Common").file("src/main/generated/resources").absolutePath,
            "--existing", project(":Common").file("src/main/resources").absolutePath
        )
    }

    neoForge.convertAccessWideners(tasks.shadowJar, "wayfinder.accessWidener")
}

dependencies {
    neoForge("net.neoforged:neoforge:${providers.gradleProperty("neoforge_version").get()}")

    "common"(project(":Common")) { isTransitive = false }
    "shadowCommon"(project(":Common", "transformProductionNeoForge"))

    localRuntime("me.djtheredstoner:DevAuth-neoforge:${providers.gradleProperty("devauth_version").get()}")

    api("com.geckolib:geckolib-neoforge-$minecraftVersion:${providers.gradleProperty("geckolib_version").get()}")

//    api("net.potionstudios:Oh-The-Biomes-Weve-Gone-NeoForge:${project.properties["bwg_version"]}")

    compileOnly("net.luckperms:api:5.5")
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(mapOf("version" to project.version))
        }
    }

    jar.get().archiveClassifier.set("raw")

    shadowJar {
        dependsOn(jar)
        from(zipTree(jar.get().archiveFile))
        exclude("architectury.common.json", "net/potionstudios/wayfinder/neoforge/datagen/**", ".cache/**")
        configurations = listOf(project.configurations.getByName("shadowCommon"))
        archiveClassifier.set(null)
    }
}

publisher {
    setLoaders(ModLoader.NEOFORGE)
    curseDepends.required.set(mutableListOf("geckolib"))
    modrinthDepends.required.set(mutableListOf("geckolib"))
    curseDepends.optional.set(mutableListOf("oh-the-biomes-weve-gone"))
    modrinthDepends.optional.set(mutableListOf("oh-the-biomes-weve-gone"))
}
