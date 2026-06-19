architectury {
    common("forge", "fabric", "neoforge")
    platformSetupLoomIde()
}

val minecraftVersion = project.properties["minecraft_version"] as String

loom.accessWidenerPath.set(file("src/main/resources/wayfinder.accesswidener"))

sourceSets.main.get().resources.srcDir("src/main/generated/resources")

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${project.properties["fabric_loader_version"]}")

    modImplementation("software.bernie.geckolib:geckolib-fabric-$minecraftVersion:${project.properties["geckolib_version"]}")
    modCompileOnly("net.potionstudios:Oh-The-Biomes-Weve-Gone-Common:${project.properties["bwg_version"]}")
}
