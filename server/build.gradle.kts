import java.net.URI

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinSerialization)
    application
}

group = "dev.avadhut.wist"
version = "1.0.0"

application {
    mainClass.set("dev.avadhut.wist.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")

    kotlin {
        jvmToolchain(21)
    }
}

dependencies {
    // Project modules
    implementation(projects.core)
    
    // Ktor Server
    implementation(libs.ktor.serverCore)
    implementation(libs.ktor.serverNetty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)

    // Security
    implementation(libs.bcrypt)
    
    // Ktor Client (for Firecrawl API calls)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)

    // Logging
    implementation(libs.logback)

    // Database
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.kotlin.datetime)
    implementation(libs.postgresql)
    implementation(libs.hikaricp)
    
    // Testing
    testImplementation(libs.ktor.serverTestHost)
    testImplementation(libs.kotlin.testJunit)
    testImplementation(libs.h2)
}

tasks.register("downloadClearUrlsRules") {
    val outputFile = file("src/main/resources/clearurls-rules.json")
    outputs.file(outputFile)
    notCompatibleWithConfigurationCache("Downloads file from network")
    doLast {
        URI("https://rules2.clearurls.xyz/data.minify.json")
            .toURL().openStream().use { input ->
                outputFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
    }
}
tasks.named("processResources") { dependsOn("downloadClearUrlsRules") }