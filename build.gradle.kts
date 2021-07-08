import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    kotlin("jvm") version "1.5.20"
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

application.mainClass.set("de.skyslycer.starboard.StarboardBotKt")

group = "de.skyslycer"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("net.dv8tion:JDA:4.3.0_293") {
        exclude("opus-java")
    }

    implementation("org.slf4j", "slf4j-api", "1.7.30")
    implementation("ch.qos.logback", "logback-classic", "1.3.0-alpha5")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.12.3")

    implementation("com.vdurmont:emoji-java:5.1.1")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "16"
}

tasks {
    named<ShadowJar>("shadowJar") {
        val classifier : String? = null
        archiveClassifier.set(classifier)
    }

    build {
        dependsOn("shadowJar")
    }
}