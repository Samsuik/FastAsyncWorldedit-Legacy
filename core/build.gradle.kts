repositories {
    flatDir {
        dirs("lib")
    }
}

dependencies {
    compileOnly("net.fabiozumbi12:redprotect:1.9.6")
    // compile 'com.plotsquared:PlotSquared-Bukkit:3.823'
    // compile 'org.javassist:javassist:3.22.0-CR1'
    compileOnly(files("../lib/PlotSquared-Bukkit-3.823.jar"))
    compileOnly("org.primesoft:BlocksHub:2.0")

    testCompileOnly("junit:junit:4.13.1")

    implementation("org.yaml:snakeyaml:1.33")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("com.github.luben:zstd-jni:1.1.1")
    implementation("co.aikar:fastutil-lite:1.0")
    implementation(group = "com.sk89q.worldedit", name = "worldedit-core", version = "6.1.4-SNAPSHOT") {
        exclude(module = "bukkit-classloader-check")
    }
}

tasks.processResources {
    from("src/main/resources") {
        val props = mapOf("version" to project.parent!!.version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("fawe.properties") {
            expand(props)
        }
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
