/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models

/**
 * Generic errors definitions for operations result.
 */
enum class Error {
    /**
     * This feature isn't implemented.
     */
    NOT_IMPLEMENTED,

    /**
     * I/O error, can also be network.
     */
    IO,

    /**
     * Authentication error.
     */
    AUTHENTICATION_REQUIRED,

    /**
     * Invalid credentials.
     */
    INVALID_CREDENTIALS,

    /**
     * The item was not found.
     */
    NOT_FOUND,

    /**
     * Value returned on write requests: The value already exists.
     */
    ALREADY_EXISTS,

    /**
     * Response deserialization error.
     */
    DESERIALIZATION,

    /**
     * The request was cancelled.
     */
    CANCELLED,

    /**
     * The server returned an invalid response.
     */
    INVALID_RESPONSE,
}
