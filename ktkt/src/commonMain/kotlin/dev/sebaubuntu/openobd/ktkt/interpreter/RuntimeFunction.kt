/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt.interpreter

import dev.sebaubuntu.openobd.ktkt.KtKtException.Companion.runtimeError
import dev.sebaubuntu.openobd.ktkt.parser.Node

/**
 * A script-defined function.
 */
class RuntimeFunction(
    private val declaration: Node.Statement.FunctionDeclaration,
    private val parentEnvironment: Environment,
) : RuntimeCallable {
    override val arguments = declaration.parameters.map {
        RuntimeCallable.ValueParameter.Definition(
            name = it.name,
            type = it.type.toType(),
            hasDefaultValue = it.defaultValue != null,
        )
    }

    override fun call(
        evaluator: Evaluator,
        valueParameters: List<RuntimeCallable.ValueParameter.Declaration>,
        codePosition: Int,
    ): Any? {
        // Create a NEW environment for the function's local scope.
        // Crucially, its parent is the CLOSURE, not the caller's environment!
        val localEnvironment = Environment(parent = parentEnvironment)

        // Bind the arguments to the parameter names in the local scope
        for (i in declaration.parameters.indices) {
            declaration.parameters[i].name?.let { argumentName ->
                localEnvironment.defineVariable(
                    name = argumentName,
                    isMutable = false,
                    codePosition = codePosition,
                    value = when (val value = valueParameters[i].value) {
                        is Undefined -> {
                            declaration.parameters[i].defaultValue?.let { defaultValue ->
                                with(evaluator) {
                                    localEnvironment.evaluate(defaultValue)
                                }
                            } ?: runtimeError(
                                "Missing argument: $argumentName",
                                codePosition = codePosition,
                            )
                        }

                        else -> value
                    },
                )
            }
        }

        val body = declaration.body ?: runtimeError(
            message = "Trying to call a function without a body.",
            codePosition = codePosition,
        )

        // Execute the function body using the new local environment
        return try {
            with(evaluator) {
                localEnvironment.evaluate(body)
            }
        } catch (r: Evaluator.ReturnException) {
            // We use a custom exception to "jump" out of deep blocks when `return` is called
            r.value
        }

        // TODO: Type validation
    }

    companion object {
        private fun Node.TypeDeclaration.toType(): RuntimeCallable.ValueParameter.Definition.Type {
            return RuntimeCallable.ValueParameter.Definition.Type(
                name = when (this) {
                    is Node.TypeDeclaration.Literal -> buildString {
                        append(name)

                        if (typeParameters.isNotEmpty()) {
                            append("<")
                            typeParameters.forEach { typeParameter ->
                                append(typeParameter.first)

                                typeParameter.second?.let {
                                    append(" : ")
                                    append(it.toType().toString())
                                }
                            }
                            append(">")
                        }
                    }

                    is Node.TypeDeclaration.Lambda -> buildString {
                        receiverType?.let {
                            append(it.toType().toString())
                            append(".")
                        }

                        append("(")
                        arguments.joinTo(this) { (name, type) ->
                            name?.let {
                                append(it)
                                append(": ")
                            }

                            append(type.toType().toString())
                        }
                        append(")")

                        append(" -> ")

                        append(returnType.toType().toString())
                    }
                },
                isNullable = isNullable,
            )
        }
    }
}
