import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

group = "dev.avadhut.wist"

kotlin {
    jvm()
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
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }

    kotlin {
        jvmToolchain(21)
    }
}
