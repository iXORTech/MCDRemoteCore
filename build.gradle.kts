import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.Properties

val krontab_version: String by project
val hoplite_version: String by project
val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

val versionPropertiesFile = "${projectDir}/project.properties"

fun String.runCommand(currentWorkingDir: File = file("./")): String {
    val byteOut = ByteArrayOutputStream()
    project.exec {
        workingDir = currentWorkingDir
        commandLine = this@runCommand.split("\\s".toRegex())
        standardOutput = byteOut
    }
    return String(byteOut.toByteArray()).trim()
}

fun getRevision(): String {
    return "git rev-parse --short=7 HEAD".runCommand()
}

fun getProperties(file: String, key: String): String {
    val fileInputStream = FileInputStream(file)
    val props = Properties()
    props.load(fileInputStream)
    return props.getProperty(key)
}

fun getVersion(): String {
    return getProperties(versionPropertiesFile, "version")
}

fun getStage(): String {
    return getProperties(versionPropertiesFile, "stage")
}

plugins {
    application
    id("com.github.johnrengelman.shadow") version("7.1.2")
    kotlin("jvm") version "1.6.10"
}

group = "tech.ixor"
version = getVersion() + "-" + getStage() + "+" + getRevision()

tasks {
    val projectProps by registering(WriteProperties::class) {
        outputFile = file("${projectDir}/src/main/resources/version.properties")
        encoding = "UTF-8"
        property("version", getVersion())
        property("stage", getStage())
        property("revision", getRevision())
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        exclude("conf/config.yaml")
        from(projectProps)
    }
}

application {
    mainClass.set("tech.ixor.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    // Dependencies
    // Scheduled Jobs
    implementation("dev.inmo:krontab:$krontab_version")
    // Config Loader
    implementation("com.sksamuel.hoplite:hoplite-core:$hoplite_version")
    implementation("com.sksamuel.hoplite:hoplite-yaml:$hoplite_version")
    // Ktor
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-html-builder:$ktor_version")
    // Logback
    implementation("ch.qos.logback:logback-classic:$logback_version")

    // Test Dependencies
    // Ktor
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    // Kotlin
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}