import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.5.5"
    id("xyz.jpenilla.run-paper") version "2.1.0"
}

val minecraftVersion = "1.20"
val javaVersion = JavaVersion.VERSION_17
group = "io.github.haappi"
version = "0.1"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
}

dependencies {
    paperweight.paperDevBundle("$minecraftVersion-R0.1-SNAPSHOT")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("cloud.commandframework:cloud-core:1.7.1")
    implementation("javax.websocket:javax.websocket-api:1.1")
    implementation("org.glassfish.tyrus.bundles:tyrus-standalone-client:1.9")
    implementation("cloud.commandframework:cloud-paper:1.7.1")
    implementation("cloud.commandframework:cloud-annotations:1.7.1")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.7.1")
    annotationProcessor("cloud.commandframework:cloud-annotations:1.7.1")
//    implementation("com.destroystokyo.paper:paper-api:$minecraftVersion-R0.1-SNAPSHOT")
//    compileOnly("com.destroystokyo.paper:paper:$minecraftVersion-R0.1-SNAPSHOT")
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}




tasks.withType<JavaCompile> {
    if (JavaVersion.current() < javaVersion) {
        options.compilerArgs.add(javaVersion.majorVersion.toInt().toString())
    }
}

tasks.named<ShadowJar>("shadowJar") {
    // Configure the Shadow Plugin
    fun reloc(pkg: String) = relocate(pkg, "io.github.haappi.duckpaper.dependency.$pkg")

    reloc("cloud.commandframework")
    reloc("javax.websocket")
}

tasks {
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    build {
        dependsOn(shadowJar)
    }

}


