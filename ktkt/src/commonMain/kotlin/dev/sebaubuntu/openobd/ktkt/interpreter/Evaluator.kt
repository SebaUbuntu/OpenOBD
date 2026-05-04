/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt.interpreter

import dev.sebaubuntu.openobd.core.ext.compareTo
import dev.sebaubuntu.openobd.core.ext.dec
import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.inc
import dev.sebaubuntu.openobd.core.ext.minus
import dev.sebaubuntu.openobd.core.ext.plus
import dev.sebaubuntu.openobd.core.ext.rem
import dev.sebaubuntu.openobd.core.ext.times
import dev.sebaubuntu.openobd.core.ext.unaryMinus
import dev.sebaubuntu.openobd.core.ext.unaryPlus
import dev.sebaubuntu.openobd.ktkt.KtKtException
import dev.sebaubuntu.openobd.ktkt.KtKtException.Companion.runtimeCompareTo
import dev.sebaubuntu.openobd.ktkt.KtKtException.Companion.runtimeError
import dev.sebaubuntu.openobd.ktkt.KtKtException.Companion.runtimeRequire
import dev.sebaubuntu.openobd.ktkt.KtKtException.Companion.runtimeRequireIsT
import dev.sebaubuntu.openobd.ktkt.KtKtException.Companion.runtimeRunCatching
import dev.sebaubuntu.openobd.ktkt.parser.Node

/**
 * Evaluator.
 */
class Evaluator {
    /**
     * Exception used to short-circuit and return a value before the end of the block.
     */
    class ReturnException(
        val value: Any?,
        codePosition: Int,
    ) : KtKtException(codePosition = codePosition, "Unexpected return expression")

    /**
     * Types registry.
     */
    val typesRegistry = TypesRegistry()

    /**
     * Global scope.
     */
    private val globalEnvironment = Environment(
        parent = null,
        typesRegistry = typesRegistry,
    )

    fun evaluate(node: Node) = globalEnvironment.evaluate(node)

    fun Environment.evaluate(node: Node): Any? = when (node) {
        is Node.Statement -> when (node) {
            is Node.Statement.VariableDeclaration -> {
                node.initializer?.let { initializer ->
                    val value: Any? = evaluate(initializer)

                    node.typeDeclaration?.let { typeDeclaration ->
                        checkRuntimeType(value, typeDeclaration, node.codePosition)
                    }

                    defineVariable(
                        name = node.name,
                        isMutable = node.isMutable,
                        codePosition = node.codePosition,
                        value = value,
                    )
                } ?: defineVariable(
                    name = node.name,
                    isMutable = node.isMutable,
                    codePosition = node.codePosition,
                )

                Undefined
            }

            is Node.Statement.FunctionDeclaration -> {
                val function = RuntimeFunction(
                    declaration = node,
                    parentEnvironment = this,
                )

                defineFunction(node.name, function)

                Undefined
            }

            is Node.Statement.Expression -> evaluate(node.expression)

            is Node.Statement.While -> {
                // Keep evaluating the body as long as the condition is true
                while (true) {
                    val conditionValue = evaluate(node.condition)
                    runtimeRequireIsT<Boolean>(conditionValue, node.condition.codePosition)

                    if (conditionValue) {
                        node.body?.let {
                            evaluate(it)
                        }
                    } else {
                        break
                    }
                }

                Undefined
            }

            is Node.Statement.DoWhile -> {
                // Evaluate the body once, then check the condition
                while (true) {
                    evaluate(node.body)

                    val conditionValue = evaluate(node.condition)
                    runtimeRequireIsT<Boolean>(conditionValue, node.condition.codePosition)

                    if (!conditionValue) {
                        break
                    }
                }

                Undefined
            }
        }

        is Node.Expression -> when (node) {
            is Node.Expression.Literal<*> -> node.value

            is Node.Expression.MemoryReference -> when (node) {
                is Node.Expression.MemoryReference.Variable -> getVariable(
                    node.name,
                    node.codePosition
                )

                is Node.Expression.MemoryReference.Property -> {
                    val receiverValue = evaluate(node.receiver)
                    runtimeRequire(receiverValue != null, node.codePosition) {
                        "Cannot access property '${node.propertyName}' on null."
                    }

                    // In a real implementation, this would look up the property in the type's scope
                    runtimeError(
                        "Property access not yet implemented for runtime objects",
                        node.codePosition
                    )
                }
            }

            is Node.Expression.Invocation -> {
                val arguments = node.arguments.map { (name, expression) ->
                    RuntimeCallable.ValueParameter.Declaration(
                        name = name,
                        value = evaluate(expression),
                    )
                }

                val callable = when (node.callable) {
                    is Node.Expression.MemoryReference.Variable -> runtimeRunCatching(
                        node.codePosition
                    ) {
                        getFunction(node.callable.name, arguments)
                    }

                    is Node.Expression.MemoryReference.Property -> runtimeError(
                        "Lambda call not yet implemented", node.codePosition
                    )

                    else -> null
                } ?: evaluate(node.callable)

                runtimeRequireIsT<RuntimeCallable>(callable, node.codePosition)

                runtimeRequire(
                    arguments.size == callable.arguments.size,
                    codePosition = node.codePosition,
                ) {
                    "Expected ${callable.arguments.size} arguments but got ${arguments.size}."
                }

                runtimeRunCatching(node.codePosition) {
                    callable.call(this@Evaluator, arguments, node.codePosition)
                }
            }

            is Node.Expression.Return -> {
                val value = evaluate(node.expression)

                throw ReturnException(
                    value = value,
                    codePosition = node.codePosition,
                )
            }

            is Node.Expression.Unary -> when (node) {
                is Node.Expression.Unary.Value -> evaluate(node.argument).let { argument ->
                    when (node.type) {
                        Node.Expression.Unary.Value.Type.PLUS -> {
                            runtimeRequireIsT<Number>(argument, node.codePosition)
                            +argument
                        }

                        Node.Expression.Unary.Value.Type.MINUS -> {
                            runtimeRequireIsT<Number>(argument, node.codePosition)
                            -argument
                        }

                        Node.Expression.Unary.Value.Type.NOT -> {
                            runtimeRequireIsT<Boolean>(argument, node.codePosition)
                            !argument
                        }
                    }
                }

                is Node.Expression.Unary.Assignment -> {
                    val valueBefore = evaluate(node.argument)
                    val valueAfter = valueBefore.let { argument ->
                        when (node.type) {
                            Node.Expression.Unary.Assignment.Type.INCREMENT -> {
                                runtimeRequireIsT<Number>(argument, node.codePosition)
                                argument.inc()
                            }

                            Node.Expression.Unary.Assignment.Type.DECREMENT -> {
                                runtimeRequireIsT<Number>(argument, node.codePosition)
                                argument.dec()
                            }
                        }
                    }

                    when (node.argument) {
                        is Node.Expression.MemoryReference.Variable -> assignVariable(
                            name = node.argument.name,
                            value = valueAfter,
                            codePosition = node.codePosition
                        )

                        is Node.Expression.MemoryReference.Property -> {
                            val receiverValue = evaluate(node.argument.receiver)

                            runtimeRequire(receiverValue != null, node.codePosition) {
                                "Cannot access property '${node.argument.propertyName}' on null."
                            }

                            runtimeError(
                                "Property assignment not yet implemented for runtime objects",
                                node.codePosition
                            )
                        }
                    }

                    when (node.prefix) {
                        true -> valueAfter
                        false -> valueBefore
                    }
                }
            }

            is Node.Expression.Binary -> when (node) {
                is Node.Expression.Binary.Value -> {
                    val left = evaluate(node.left)
                    val right = evaluate(node.right)

                    when (node.type) {
                        Node.Expression.Binary.Value.Type.ADD -> when (left) {
                            is Number -> {
                                runtimeRequireIsT<Number>(right, node.codePosition)
                                left + right
                            }

                            is String -> left + right

                            else -> runtimeError(
                                "Operand requires two numbers or a string on the left side.",
                                node.codePosition,
                            )
                        }

                        Node.Expression.Binary.Value.Type.SUBTRACT -> when (left) {
                            is Number -> {
                                runtimeRequireIsT<Number>(right, node.codePosition)
                                left - right
                            }

                            else -> runtimeError(
                                "Operand requires two numbers.",
                                node.codePosition,
                            )
                        }

                        Node.Expression.Binary.Value.Type.MULTIPLY -> when (left) {
                            is Number -> {
                                runtimeRequireIsT<Number>(right, node.codePosition)
                                left * right
                            }

                            else -> runtimeError(
                                "Operand requires two numbers.",
                                node.codePosition,
                            )
                        }

                        Node.Expression.Binary.Value.Type.DIVIDE -> when (left) {
                            is Number -> {
                                runtimeRequireIsT<Number>(right, node.codePosition)

                                runtimeRequire(right != 0, node.codePosition) {
                                    "Division by zero."
                                }

                                left / right
                            }

                            else -> runtimeError(
                                "Operand requires two numbers.",
                                node.codePosition,
                            )
                        }

                        Node.Expression.Binary.Value.Type.REMAINDER -> when (left) {
                            is Number -> {
                                runtimeRequireIsT<Number>(right, node.codePosition)

                                runtimeRequire(right != 0, node.codePosition) {
                                    "Division by zero."
                                }

                                left % right
                            }

                            else -> runtimeError(
                                "Operand requires two numbers.",
                                node.codePosition,
                            )
                        }

                        Node.Expression.Binary.Value.Type.LESS_THAN,
                        Node.Expression.Binary.Value.Type.GREATER_THAN,
                        Node.Expression.Binary.Value.Type.LESS_THAN_OR_EQUAL,
                        Node.Expression.Binary.Value.Type.GREATER_THAN_OR_EQUAL -> when (left) {
                            is Number -> {
                                runtimeRequireIsT<Number>(right, node.codePosition)
                                left.compareTo(right)
                            }

                            is Comparable<*> -> left.runtimeCompareTo(right, node.codePosition)

                            else -> runtimeError(
                                "Comparing values that cannot be compared.",
                                node.codePosition,
                            )
                        }.let {
                            when (node.type) {
                                Node.Expression.Binary.Value.Type.LESS_THAN -> it < 0
                                Node.Expression.Binary.Value.Type.GREATER_THAN -> it > 0
                                Node.Expression.Binary.Value.Type.LESS_THAN_OR_EQUAL -> it <= 0
                                Node.Expression.Binary.Value.Type.GREATER_THAN_OR_EQUAL -> it >= 0
                            }
                        }

                        Node.Expression.Binary.Value.Type.EQUAL -> left == right
                        Node.Expression.Binary.Value.Type.NOT_EQUAL -> left != right
                        Node.Expression.Binary.Value.Type.IDENTITY_EQUAL -> left === right
                        Node.Expression.Binary.Value.Type.NOT_IDENTITY_EQUAL -> left !== right

                        Node.Expression.Binary.Value.Type.AND -> when (left) {
                            is Boolean -> {
                                runtimeRequireIsT<Boolean>(right, node.codePosition)
                                left && right
                            }

                            else -> runtimeError(
                                "Operand requires two booleans.",
                                node.codePosition,
                            )
                        }

                        Node.Expression.Binary.Value.Type.OR -> when (left) {
                            is Boolean -> {
                                runtimeRequireIsT<Boolean>(right, node.codePosition)
                                left || right
                            }

                            else -> runtimeError(
                                "Operand requires two booleans.",
                                node.codePosition,
                            )
                        }
                    }
                }

                is Node.Expression.Binary.Assignment -> {
                    val value = evaluate(node.right)

                    when (node.left) {
                        is Node.Expression.MemoryReference.Variable -> assignVariable(
                            node.left.name, value, node.codePosition
                        )

                        is Node.Expression.MemoryReference.Property -> {
                            val receiverValue = evaluate(node.left.receiver)
                            runtimeRequire(receiverValue != null, node.codePosition) {
                                "Cannot set property '${node.left.propertyName}' on null."
                            }

                            // Logic to perform the actual assignment to the object's property
                            runtimeError(
                                "Property assignment not yet implemented for runtime objects",
                                node.codePosition
                            )
                        }
                    }

                    Undefined
                }
            }

            is Node.Expression.Block -> {
                // Create a new inner environment
                val environment = Environment(parent = this)

                var lastValue: Any? = Undefined
                // Execute all statements in the new scope
                for (statement in node.statements) {
                    lastValue = environment.evaluate(statement)
                }

                // In Kotlin, blocks evaluate to the value of their last statement
                when (lastValue) {
                    is Undefined -> Unit
                    else -> lastValue
                }
            }

            is Node.Expression.If -> {
                val conditionValue = evaluate(node.condition)
                runtimeRequireIsT<Boolean>(conditionValue, node.condition.codePosition)

                when {
                    conditionValue -> evaluate(node.thenBranch)
                    node.elseBranch != null -> evaluate(node.elseBranch)
                    else -> Undefined
                }
            }
        }

        is Node.TypeDeclaration -> runtimeError(
            "Evaluation for TypeDeclaration should not be called directly. It is handled by type checking.",
            node.codePosition,
        )
    }

    /**
     * Ensures that the evaluated runtime value matches the explicitly declared AST type.
     */
    private fun checkRuntimeType(
        value: Any?,
        declaredType: Node.TypeDeclaration,
        position: Int,
    ) {
        when (declaredType) {
            is Node.TypeDeclaration.Literal -> {
                val typeName = declaredType.name

                val isValid = when (declaredType.isNullable && value == null) {
                    true -> true

                    false -> typesRegistry.getType(typeName)?.isInstance(
                        value
                    ) ?: runtimeError(
                        "Unknown or unsupported runtime type '$typeName'.",
                        position,
                    )
                }

                if (!isValid) {
                    val actualTypeName = value?.let { it::class.qualifiedName }

                    runtimeError(
                        "Type mismatch: Expected $typeName, but got $actualTypeName.",
                        position,
                    )
                }
            }

            is Node.TypeDeclaration.Lambda -> {
                // Validating exact function signatures at runtime is tricky.
                // For a simple implementation, you might just check if the value is a
                // callable/lambda.
                // We'll leave it as a pass-through for now until you implement Lambda evaluation!
            }
        }
    }
}
