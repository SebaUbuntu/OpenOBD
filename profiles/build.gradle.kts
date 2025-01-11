/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

plugins {
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidLibrary {
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        namespace = "dev.sebaubuntu.openobd.profiles"

        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "profilesKit"
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.components.resources)
            implementation(libs.compose.runtime)
            implementation(libs.kotlin.stdlib)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)

            implementation(projects.core)
            implementation(projects.obd2)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
