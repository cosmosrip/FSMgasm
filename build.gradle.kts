plugins {
    kotlin("jvm") version "2.0.0"

    `maven-publish`
}

version = "1.0.8"
group = "net.minikloon.fsmgasm"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation(group = "org.slf4j", name = "slf4j-api", version = "1.7.25")
    implementation("com.github.Minikloon:Kloggs:-SNAPSHOT")
}
