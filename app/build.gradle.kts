/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin application project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.0.2/userguide/building_java_projects.html
 */
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
val ktor_version = "1.5.0"

// repositories, dependencies, etc...
val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.5.0"

    // Apply java plugin
    id("java")

    kotlin("plugin.serialization") version "1.5.10"

    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()

    // Use 3rd party dependencies
    google()
}

// Compile for java 1.8
compileKotlin.kotlinOptions {
  jvmTarget = "1.8"
}
compileTestKotlin.kotlinOptions {
  jvmTarget = "1.8"
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.10")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.1")
    implementation("org.reflections:reflections:0.9.12")

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // This dependency is used by the application.
    implementation("com.google.guava:guava:30.0-jre")
    implementation("com.google.code.gson:gson:2.8.5")

    // These are 3rd party dependencies
    implementation("org.litote.kmongo:kmongo:4.2.7")
    //implementation("org.slf4j:slf4j-log4j12:1.7.29")

    // Use Ktor.io
    implementation("io.ktor:ktor-server-core:1.5.4")
    implementation("io.ktor:ktor-server-netty:1.5.4")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("io.ktor:ktor-locations:$ktor_version")
    implementation("io.ktor:ktor-metrics:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")

    // Use for ktor tests
    testImplementation("io.ktor:ktor-server-test-host:1.5.4")
    
    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

application {
    // Define the main class for the application.
    mainClass.set("kt.json.server.AppKt")
}
