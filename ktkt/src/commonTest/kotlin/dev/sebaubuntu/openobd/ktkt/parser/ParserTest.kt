/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt.parser

import dev.sebaubuntu.openobd.ktkt.KtKtTest.Companion.parseScript
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParserTest {
    @Test
    fun parseDoubleParsing() = "22.05".parseScript().assertContainsSingleExpression(
        Node.Expression.Literal.Double(
            value = 22.05,
            codePosition = 0,
        )
    )

    @Test
    fun parseFloatParsing() = "22.05f".parseScript().assertContainsSingleExpression(
        Node.Expression.Literal.Float(
            value = 22.05f,
            codePosition = 0,
        )
    )

    @Test
    fun parseIntDecParsing() = "2205".parseScript().assertContainsSingleExpression(
        Node.Expression.Literal.Int(
            value = 2205,
            codePosition = 0,
        )
    )

    @Test
    fun parseIntBinParsing() = "0b1010".parseScript().assertContainsSingleExpression(
        Node.Expression.Literal.Int(
            value = 0b1010,
            codePosition = 0,
        )
    )

    @Test
    fun parseIntHexParsing() = "0x2205".parseScript().assertContainsSingleExpression(
        Node.Expression.Literal.Int(
            value = 0x2205,
            codePosition = 0,
        )
    )

    @Test
    fun parseLongParsing() = "2205L".parseScript().assertContainsSingleExpression(
        Node.Expression.Literal.Long(
            value = 2205L,
            codePosition = 0,
        )
    )

    @Test
    fun parseUnsignedIntParsing() = "2205u".parseScript().assertContainsSingleExpression(
        Node.Expression.Literal.UInt(
            value = 2205u,
            codePosition = 0,
        )
    )

    @Test
    fun parseUnsignedLongParsing() = "2205uL".parseScript().assertContainsSingleExpression(
        Node.Expression.Literal.ULong(
            value = 2205uL,
            codePosition = 0,
        )
    )

    @Test
    fun parseStringParsing() = "2205uL".parseScript().assertContainsSingleExpression(
        Node.Expression.Literal.ULong(
            value = 2205uL,
            codePosition = 0,
        )
    )

    @Test
    fun testMultiLineAssignment() = """
        val a =
            1
    """.trimIndent().parseScript().assertContainsSingleStatement(
        Node.Statement.VariableDeclaration(
            name = "a",
            isMutable = false,
            typeDeclaration = null,
            initializer = Node.Expression.Literal.Int(
                value = 1,
                codePosition = 12,
            ),
            codePosition = 4,
        )
    )

    @Test
    fun testMalformedAssignment() = assertFailsWith<RuntimeException> {
        """
            val a =
            val b = 1
        """.trimIndent().parseScript()
    }.let {
        val expectedMessage = """
            Error at line 2, column 1: Expected expression, found VAL
            val b = 1
            ^
        """.trimIndent()

        assertEquals(expectedMessage, it.message)
    }

    companion object {
        private fun List<Node.Statement>.assertContainsSingleStatement(
            statement: Node.Statement,
        ) = assertContentEquals(
            expected = listOf(statement),
            actual = this,
        )

        private fun List<Node.Statement>.assertContainsSingleExpression(
            expression: Node.Expression,
        ) = assertContainsSingleStatement(
            Node.Statement.Expression(
                expression = expression,
                codePosition = 0,
            )
        )
    }
}
