import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization)
    `maven-publish`
}

group = property("maven_group") as String
version = property("mod_version") as String

val minecraftVersion = providers.gradleProperty("minecraft_version").get()
val loaderVersion = providers.gradleProperty("loader_version").get()

base {
    archivesName.set(property("archives_base_name") as String)
}

repositories {
    mavenCentral()
    maven("https://maven.fabricmc.net/")
}

dependencies {
    minecraft(
        "com.mojang:minecraft:${property("minecraft_version")}"
    )

    implementation(
        "net.fabricmc:fabric-loader:${property("loader_version")}"
    )

    implementation(
        "net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}"
    )

    implementation(
        "net.fabricmc:fabric-language-kotlin:1.13.13+kotlin.2.4.10"
    )
}

loom {
    runs {
        named("client") {
            client()
            runDir("run")
        }

        named("server") {
            server()
            runDir("run-server")
        }
    }
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", minecraftVersion)
    inputs.property("loader_version", loaderVersion)

    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "version" to project.version,
                "minecraft_version" to minecraftVersion,
                "loader_version" to loaderVersion
            )
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(25)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.fromTarget("25"))
    }

    jvmToolchain(25)
}

java {
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = property("archives_base_name") as String
            from(components["java"])
        }
    }
}