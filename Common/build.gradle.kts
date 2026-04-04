architectury {
    common("fabric", "neoforge")
    platformSetupLoomIde()
}

val minecraftVersion = project.properties["minecraft_version"] as String

loom.accessWidenerPath.set(file("src/main/resources/wayfinder.accesswidener"))

sourceSets.main.get().resources.srcDir("src/main/generated/resources")

dependencies {
    implementation("net.fabricmc:fabric-loader:${project.properties["fabric_loader_version"]}")

    compileOnly("com.geckolib:geckolib-common-26.1:${project.properties["geckolib_version"]}")
}
