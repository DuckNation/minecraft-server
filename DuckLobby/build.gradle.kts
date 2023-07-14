plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "io.github.haappi"
version = "unspecified"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("dev.hollowcube:minestom-ce:d47db72421")
    implementation("com.google.guava:guava:32.1.1-jre")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "io.github.haappi.Main"
        attributes["Multi-Release"] = "true"
    }
}
