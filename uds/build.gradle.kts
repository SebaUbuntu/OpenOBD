/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

plugins {
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    androidLibrary {
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        namespace = "dev.sebaubuntu.openobd.uds"
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "udsKit"
        }
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)

            implementation(projects.core)
            implementation(projects.elm327)
            implementation(projects.logging)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}
