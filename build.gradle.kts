plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.3.7"
    id("xyz.jpenilla.run-paper") version "1.0.6"
}

group = "io.github.haappi"
version = "0.1"

var mainClassName = "io.github.haappi.ducksmp.DuckSMP"
val shade = configurations.create("shade")

var version = "1.19.2"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/public/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/")
}

dependencies {
//    implementation("junit:junit:4.13.2")
    paperDevBundle("$version-R0.1-SNAPSHOT")
    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT")
    implementation("org.mongodb:mongodb-driver-sync:4.7.1")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("com.jeff_media:CustomBlockData:2.1.0")
    implementation("com.jeff_media:MorePersistentDataTypes:2.3.1")
//    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
//    testImplementation("com.github.seeseemelk:MockBukkit-v1.18:2.22.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}


tasks {
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    runServer {
        minecraftVersion(version)
    }



    shadowJar {
        // helper function to relocate a package into our package
        fun reloc(pkg: String) = relocate(pkg, "io.github.haappi.duckSMP.dependency.$pkg")

        reloc("com.jeff_media.morepersistentdatatypes")
        reloc("com.jeff_media.customblockdata")
    }

    /*
    reobfJar {
      // This is an example of how you might change the output location for reobfJar. It"s recommended not to do this
      // for a variety of reasons, however it"s asked frequently enough that an example of how to do it is included here.
      outputJar.set(layout.buildDirectory.file("libs/PaperweightTestPlugin-${project.version}.jar"))
    }
     */
}
