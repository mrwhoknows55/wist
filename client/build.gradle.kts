@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinSerialization)
}

group = "dev.avadhut.wist"

kotlin {
    jvm {
        mainRun {
            mainClass.set("dev.avadhut.wist.client.MainKt")
        }
    }
    androidLibrary {
        namespace = "dev.avadhut.wist.client"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class) wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            // Project modules
            implementation(projects.core)

            // Ktor Client
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.multiplatform.settings.no.arg)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.cio)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.datetime)
            implementation(libs.ktor.client.mock)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
        }
    }
    kotlin {
        jvmToolchain(21)
    }
}
