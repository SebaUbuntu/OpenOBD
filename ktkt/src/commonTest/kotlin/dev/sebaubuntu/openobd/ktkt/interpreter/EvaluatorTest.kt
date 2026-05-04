/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt.interpreter

import dev.sebaubuntu.openobd.ktkt.KtKtTest.Companion.evaluateScript
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class EvaluatorTest {
    @Test
    fun testVariableAssignment() = """
        var a = 1
        a = 2
        a
    """.trimIndent().evaluateScript().let {
        assertIs<Int>(it)
        assertEquals(2, it)
    }

    @Test
    fun testUndefinedVariableError() = assertFailsWith<RuntimeException> {
        """
            val a = 1
            val b = a + d
        """.trimIndent().evaluateScript()
    }.let {
        val expectedMessage = """
            Error at line 2, column 13: Undefined variable: d
            val b = a + d
                        ^
        """.trimIndent()

        assertEquals(expectedMessage, it.message)
    }

    @Test
    fun testReadOnlyVariableAssignment() = assertFailsWith<RuntimeException> {
        """
            val a = 1
            a = 2
        """.trimIndent().evaluateScript()
    }.let {
        val expectedMessage = """
            Error at line 2, column 1: Cannot reassign non-mutable variable
            a = 2
            ^
        """.trimIndent()

        assertEquals(expectedMessage, it.message)
    }

    @Test
    fun testArithmeticExpressions() = """
        val a = 10
        val b = 5
        (a + b) * 2 / 5
    """.trimIndent().evaluateScript().let {
        assertIs<Int>(it)
        assertEquals(6, it)
    }

    @Test
    fun testPemdasExpression() = """
        6 + 4 * (9 / 3) - 7
    """.trimIndent().evaluateScript().let {
        assertIs<Int>(it)
        assertEquals(11, it)
    }

    @Test
    fun testUnaryIncreasePrefix() = """
        var condition = 5
        ++condition
        condition
    """.trimIndent().evaluateScript().let {
        assertIs<Int>(it)
        assertEquals(6, it)
    }

    @Test
    fun testIfElseCondition() = """
        if (2 % 2 == 0) {
            1
        } else {
            2
        }
    """.trimIndent().evaluateScript().let {
        assertIs<Int>(it)
        assertEquals(1, it)
    }

    @Test
    fun testAssignmentWithIfAndNoElseBlock() = assertFailsWith<RuntimeException> {
        """
            val test = if (false) {
                1
            }
            test
        """.trimIndent().evaluateScript()
    }.let {
        val expectedMessage = """
            Error at line 1, column 5: Trying to initialize a variable with an undefined value
            val test = if (false) {
                ^
        """.trimIndent()

        assertEquals(expectedMessage, it.message)
    }

    @Test
    fun testWhileLoop() = """
        var condition = 5
        while (condition > 1) {
            condition--
        }
        condition
    """.trimIndent().evaluateScript().let {
        assertIs<Int>(it)
        assertEquals(1, it)
    }

    @Test
    fun testDoWhileLoop() = """
        var condition = 5
        do {
            condition--
        } while (condition > 1)
        condition
    """.trimIndent().evaluateScript().let {
        assertIs<Int>(it)
        assertEquals(1, it)
    }

    @Test
    fun testFunctionCall() = """
        fun add(a: Int, b: Int): Int {
            return a + b
        }
        add(5, 10)
    """.trimIndent().evaluateScript().let {
        assertEquals(15, it)
    }

    @Test
    fun testNestedFunction() = """
        fun test1(): Int {
            fun test2(): Int {
                return 2
            }
            return test2()
        }
        test1()
    """.trimIndent().evaluateScript().let {
        assertIs<Int>(it)
        assertEquals(2, it)
    }

    @Test
    fun testPropertyAccess() = """
        val test = emptyArray()
        test.size
    """.trimIndent().evaluateScript().let {
        assertIs<Int>(it)
        assertEquals(0, it)
    }
}
