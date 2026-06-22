architectury {
    common("forge", "fabric", "neoforge")
    platformSetupLoomIde()
}

val minecraftVersion = providers.gradleProperty("minecraft_version").get()

loom.accessWidenerPath.set(file("src/main/resources/wayfinder.accesswidener"))

sourceSets.main.get().resources.srcDir("src/main/generated/resources")

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${providers.gradleProperty("fabric_loader_version").get()}")

    modImplementation("software.bernie.geckolib:geckolib-fabric-$minecraftVersion:${providers.gradleProperty("geckolib_version").get()}")
    modCompileOnly("net.potionstudios:Oh-The-Biomes-Weve-Gone-Common:${providers.gradleProperty("bwg_version").get()}")
}
