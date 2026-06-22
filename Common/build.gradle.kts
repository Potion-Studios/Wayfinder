architectury {
    common("fabric", "neoforge", "forge")
    platformSetupLoomIde()
}

val minecraftVersion = providers.gradleProperty("minecraft_version").get()

loom.accessWidenerPath.set(file("src/main/resources/wayfinder.accessWidener"))

sourceSets.main.get().resources.srcDir("src/main/generated/resources")

dependencies {
    implementation("net.fabricmc:fabric-loader:${providers.gradleProperty("fabric_loader_version").get()}")

    compileOnly("com.geckolib:geckolib-common-$minecraftVersion:${providers.gradleProperty("geckolib_version").get()}")
}
