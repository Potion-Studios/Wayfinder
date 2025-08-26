import com.hypherionmc.modpublisher.properties.ModLoader

plugins {
    id("com.gradleup.shadow")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

val minecraftVersion = project.properties["minecraft_version"] as String

configurations {
    create("common")
    "common" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
    create("shadowBundle")
    compileClasspath.get().extendsFrom(configurations["common"])
    runtimeClasspath.get().extendsFrom(configurations["common"])
    getByName("developmentNeoForge").extendsFrom(configurations["common"])
    "shadowBundle" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

loom {
    accessWidenerPath.set(project(":Common").loom.accessWidenerPath)

    // NeoForge Datagen Gradle config.  Remove if not using NeoForge datagen
    runs.create("datagen") {
        data()
        programArgs("--all", "--mod", "wayfinder")
        programArgs("--output", project(":Common").file("src/main/generated/resources").absolutePath)
        programArgs("--existing", project(":Common").file("src/main/resources").absolutePath)
    }
}

dependencies {
    neoForge("net.neoforged:neoforge:${project.properties["neoforge_version"]}")

    "common"(project(":Common", "namedElements")) { isTransitive = false }
    "shadowBundle"(project(":Common", "transformProductionNeoForge"))

    modLocalRuntime("me.djtheredstoner:DevAuth-neoforge:${project.properties["devauth_version"]}")

    modApi("software.bernie.geckolib:geckolib-neoforge-$minecraftVersion:${project.properties["geckolib_version"]}")

    modApi("net.potionstudios:Oh-The-Biomes-Weve-Gone-NeoForge:${project.properties["bwg_version"]}")

    modLocalRuntime("com.github.Jab125.architectury-data-generation-fix:architectury-data-generation-fix-neoforge:21.0.3")

    compileOnly("net.luckperms:api:5.4")
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/neoforge.mods.toml") {
            expand(mapOf("version" to project.version))
        }
    }

    shadowJar {
        exclude("architectury.common.json", "net/potionstudios/wayfinder/neoforge/datagen/**", ".cache/**")
        configurations = listOf(project.configurations.getByName("shadowBundle"))
        archiveClassifier.set("dev-shadow")
    }

    remapJar {
        inputFile.set(shadowJar.get().archiveFile)
        dependsOn(shadowJar)
        atAccessWideners.add("wayfinder.accesswidener")
    }
}

publisher {
    setLoaders(ModLoader.NEOFORGE)
    curseDepends.required.set(mutableListOf("geckolib"))
    modrinthDepends.required.set(mutableListOf("geckolib"))
}
