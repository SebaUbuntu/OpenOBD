/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt.parser

/**
 * Nodes.
 */
sealed interface Node {
    sealed interface Statement : Node {
        /**
         * Variable declaration.
         */
        data class VariableDeclaration(
            val name: String,
            val isMutable: Boolean,
            val typeDeclaration: TypeDeclaration?,
            val initializer: Node.Expression?,
            override val codePosition: Int,
        ) : Statement

        /**
         * Function declaration.
         */
        data class FunctionDeclaration(
            val name: String,
            val visibility: Visibility,
            val typeParameters: List<Pair<String, TypeDeclaration?>>,
            val receiverType: TypeDeclaration?,
            val parameters: List<ValueParameter>,
            val returnType: TypeDeclaration?,
            val body: Node.Expression?,
            override val codePosition: Int,
        ) : Statement {
            enum class Visibility {
                PRIVATE,
                PROTECTED,
                INTERNAL,
                PUBLIC,
            }

            data class ValueParameter(
                val name: String?,
                val type: TypeDeclaration,
                val defaultValue: Node.Expression?,
            )
        }

        /**
         * An expression.
         */
        data class Expression(
            val expression: Node.Expression,
            override val codePosition: Int,
        ) : Statement

        /**
         * A while loop.
         */
        data class While(
            val condition: Node.Expression,
            val body: Node.Expression?,
            override val codePosition: Int,
        ) : Statement

        /**
         * A do-while loop.
         */
        data class DoWhile(
            val body: Node.Expression,
            val condition: Node.Expression,
            override val codePosition: Int,
        ) : Statement
    }

    sealed interface Expression : Node {
        /**
         * Literal data.
         */
        sealed interface Literal<T> : Expression {
            /**
             * [kotlin.Boolean] literal.
             */
            data class Boolean(
                override val value: kotlin.Boolean,
                override val codePosition: kotlin.Int,
            ) : Literal<kotlin.Boolean>

            /**
             * [kotlin.Char] literal.
             */
            data class Char(
                override val value: kotlin.Char,
                override val codePosition: kotlin.Int,
            ) : Literal<kotlin.Char>

            /**
             * [kotlin.Double] literal.
             */
            data class Double(
                override val value: kotlin.Double,
                override val codePosition: kotlin.Int,
            ) : Literal<kotlin.Double>

            /**
             * [kotlin.Float] literal.
             */
            data class Float(
                override val value: kotlin.Float,
                override val codePosition: kotlin.Int,
            ) : Literal<kotlin.Float>

            /**
             * [kotlin.Int] literal.
             */
            data class Int(
                override val value: kotlin.Int,
                override val codePosition: kotlin.Int,
            ) : Literal<kotlin.Int>

            /**
             * [kotlin.Long] literal.
             */
            data class Long(
                override val value: kotlin.Long,
                override val codePosition: kotlin.Int,
            ) : Literal<kotlin.Long>

            /**
             * [kotlin.UInt] literal.
             */
            data class UInt(
                override val value: kotlin.UInt,
                override val codePosition: kotlin.Int,
            ) : Literal<kotlin.UInt>

            /**
             * [kotlin.ULong] literal.
             */
            data class ULong(
                override val value: kotlin.ULong,
                override val codePosition: kotlin.Int,
            ) : Literal<kotlin.ULong>

            /**
             * null literal.
             */
            data class Null(
                override val codePosition: kotlin.Int,
            ) : Literal<Nothing?> {
                override val value = null
            }

            /**
             * [kotlin.String] literal.
             */
            data class String(
                override val value: kotlin.String,
                override val codePosition: kotlin.Int,
            ) : Literal<kotlin.String>

            /**
             * The value.
             */
            val value: T
        }

        /**
         * Reference to memory.
         */
        sealed interface MemoryReference : Expression {
            /**
             * Reference to a variable.
             */
            data class Variable(
                val name: String,
                override val codePosition: Int,
            ) : MemoryReference

            /**
             * Property access.
             */
            data class Property(
                val receiver: Expression,
                val propertyName: String,
                override val codePosition: Int,
            ) : MemoryReference
        }

        /**
         * Callable invocation.
         */
        data class Invocation(
            val callable: Expression,
            val arguments: List<Pair<String?, Expression>>,
            override val codePosition: Int,
        ) : Expression

        /**
         * Return expression.
         */
        data class Return(
            val expression: Expression,
            override val codePosition: Int,
        ) : Expression

        /**
         * Unary operators.
         */
        sealed interface Unary : Expression {
            /**
             * Unary operators that operates on a value and returns a value.
             */
            data class Value(
                override val argument: Expression,
                val type: Type,
                override val codePosition: Int,
            ) : Unary {
                enum class Type {
                    PLUS,
                    MINUS,
                    NOT,
                }
            }

            /**
             * Unary operators that operates on a variable and updates its value.
             */
            data class Assignment(
                override val argument: MemoryReference,
                val type: Type,
                val prefix: Boolean,
                override val codePosition: Int,
            ) : Unary {
                enum class Type {
                    INCREMENT,
                    DECREMENT,
                }
            }

            /**
             * The argument of the operator.
             */
            val argument: Expression
        }

        sealed interface Binary : Expression {
            /**
             * An operator that returns a value.
             */
            data class Value(
                override val left: Expression,
                override val right: Expression,
                val type: Type,
                override val codePosition: Int,
            ) : Binary {
                enum class Type {
                    // Math
                    ADD,
                    SUBTRACT,
                    MULTIPLY,
                    DIVIDE,
                    REMAINDER,

                    // Comparison
                    LESS_THAN,
                    GREATER_THAN,
                    LESS_THAN_OR_EQUAL,
                    GREATER_THAN_OR_EQUAL,

                    // Equality
                    EQUAL,
                    NOT_EQUAL,
                    IDENTITY_EQUAL,
                    NOT_IDENTITY_EQUAL,

                    // Logical
                    AND,
                    OR,
                }
            }

            /**
             * An assignment of a value to a variable.
             */
            data class Assignment(
                override val left: MemoryReference,
                override val right: Expression,
                override val codePosition: Int,
            ) : Binary

            /**
             * The left value.
             */
            val left: Expression

            /**
             * The right value.
             */
            val right: Expression
        }

        data class Block(
            val statements: List<Statement>,
            override val codePosition: Int,
        ) : Expression

        data class If(
            val condition: Expression,
            val thenBranch: Expression,
            val elseBranch: Expression?,
            override val codePosition: Int,
        ) : Expression
    }

    sealed interface TypeDeclaration : Node {
        data class Literal(
            val name: String,
            val typeParameters: List<Pair<String, TypeDeclaration?>>,
            override val isNullable: Boolean,
            override val codePosition: Int,
        ) : TypeDeclaration

        data class Lambda(
            val receiverType: TypeDeclaration?,
            val arguments: List<Pair<String?, TypeDeclaration>>,
            val returnType: TypeDeclaration,
            override val isNullable: Boolean,
            override val codePosition: Int,
        ) : TypeDeclaration

        val isNullable: Boolean
    }

    /**
     * The starting code position of this node.
     */
    val codePosition: Int
}
