/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.network.core

/**
 * A protocol transceiver.
 */
interface Transceiver<F : Frame> : Receiver<F>, Transmitter<F>
