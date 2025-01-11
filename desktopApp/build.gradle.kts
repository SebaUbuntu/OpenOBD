/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinJvm)
}

compose.desktop {
    application {
        mainClass = "dev.sebaubuntu.openobd.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Deb, TargetFormat.Dmg, TargetFormat.Msi)
            packageName = "dev.sebaubuntu.openobd"
            packageVersion = "1.0.0"
        }
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(libs.compose.components.resources)
    implementation(libs.koin.core)
    implementation(libs.kotlinx.coroutines.swing)

    implementation(projects.app)
}
