import com.hypherionmc.modpublisher.properties.ModLoader

plugins {
    id("com.gradleup.shadow")
}

architectury {
    platformSetupLoomIde()
    forge()
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
    getByName("developmentForge").extendsFrom(configurations["common"])
    "shadowBundle" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

loom {
    accessWidenerPath.set(project(":Common").loom.accessWidenerPath)

    forge {
        convertAccessWideners.set(true)
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
    }
}

dependencies {
    forge("net.minecraftforge:forge:$minecraftVersion-${project.properties["forge_version"]}")

    "common"(project(":Common")) { isTransitive = false }
    "shadowBundle"(project(":Common", "transformProductionForge"))

    localRuntime("me.djtheredstoner:DevAuth-forge-latest:${project.properties["devauth_version"]}")

    api("com.geckolib:geckolib-forge-$minecraftVersion:${project.properties["geckolib_version"]}")

    compileOnly("net.luckperms:api:5.4")
}

tasks {
    processResources {
        inputs.property("version", project.version)

        filesMatching("META-INF/mods.toml") {
            expand(mapOf("version" to project.version))
        }
    }

    shadowJar {
        exclude("architectury.common.json", ".cache/**")
        configurations = listOf(project.configurations.getByName("shadowBundle"))
        archiveClassifier.set("dev-shadow")
    }
}

publisher {
    setLoaders(ModLoader.FORGE)
    curseDepends.required.set(mutableListOf("geckolib"))
    modrinthDepends.required.set(mutableListOf("geckolib"))
    curseDepends.optional.set(mutableListOf("oh-the-biomes-weve-gone"))
    modrinthDepends.optional.set(mutableListOf("oh-the-biomes-weve-gone"))
}
