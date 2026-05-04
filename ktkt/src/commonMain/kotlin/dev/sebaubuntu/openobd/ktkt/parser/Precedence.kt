/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt.parser

/**
 * Operators precedence.
 */
enum class Precedence {
    /**
     * Base level.
     */
    NONE,

    /**
     * =, +=, -=, etc.
     */
    ASSIGNMENT,

    /**
     * ||.
     */
    OR,

    /**
     * &&.
     */
    AND,

    /**
     * ==, !=, ===, !==.
     */
    EQUALITY,

    /**
     * <, >, <=, >=, is, !is
     */
    COMPARISON,

    /**
     * +, -.
     */
    TERM,

    /**
     * *, /, %.
     */
    FACTOR,

    /**
     * !true, -5
     */
    UNARY,

    /**
     * (), property access.
     */
    CALL,

    /**
     * ++, --.
     */
    POSTFIX,

    /**
     * Literals, variables.
     */
    PRIMARY,
}
