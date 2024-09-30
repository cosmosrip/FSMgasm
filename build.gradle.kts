plugins {
    kotlin("jvm") version "2.0.0"

    `maven-publish`
}

version = "2.0.0"
group = "net.minikloon.fsmgasm"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.25")
    implementation("com.github.Minikloon:Kloggs:-SNAPSHOT")
}

publishing {
    publications {
        register<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}