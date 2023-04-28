plugins {
    kotlin("jvm") version "1.8.0"
    application /*追記*/
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "click.emesan.bot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.8")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("${group}.${rootProject.name}.MainKt")
}

kotlin {
    jvmToolchain(11)
}