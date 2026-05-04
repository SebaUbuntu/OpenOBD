/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt.interpreter

/**
 * Runtime callable.
 */
interface RuntimeCallable {
    /**
     * Callable's value parameter.
     */
    sealed interface ValueParameter {
        /**
         * Callable value parameter definition.
         */
        data class Definition(
            override val name: String?,
            val type: Type,
            val hasDefaultValue: Boolean,
        ) : ValueParameter {
            data class Type(
                val name: String,
                val isNullable: Boolean,
            ) {
                override fun toString() = buildString {
                    append(name)

                    if (isNullable) {
                        append("?")
                    }
                }
            }

            override fun toString() = buildString {
                name?.let {
                    append(it)
                    append(": ")
                }

                append(type)
            }
        }

        /**
         * Invocation value parameter declaration.
         */
        data class Declaration(
            override val name: String?,
            val value: Any?,
        ) : ValueParameter

        /**
         * The name of the argument. If null, positional order should be used.
         */
        val name: String?
    }

    /**
     * The list of expected arguments.
     */
    val arguments: List<ValueParameter.Definition>

    /**
     * Call the callable and return a value.
     */
    fun call(
        evaluator: Evaluator,
        valueParameters: List<ValueParameter.Declaration>,
        codePosition: Int,
    ): Any?

    companion object {
        fun List<ValueParameter>.validateOrder() {
            var namedParameterFound = false
            forEach { argument ->
                if (argument.name != null) {
                    namedParameterFound = true
                } else if (namedParameterFound) {
                    error("Found positional argument after named parameter.")
                }
            }
        }

        fun List<ValueParameter.Definition>.prepareForCall(
            arguments: List<ValueParameter.Declaration>,
        ): List<ValueParameter.Declaration>? {
            if (arguments.size > this.size) {
                return null
            }

            val finalArguments = Array<ValueParameter.Declaration?>(size) { null }

            val (positionalArguments, namedArguments) = arguments.partition {
                it.name == null
            }

            fun registerArgument(value: ValueParameter.Declaration, index: Int) {
                require(finalArguments[index] == null) {
                    "Duplicate argument."
                }
                finalArguments[index] = value
            }

            for ((index, argumentDefinition) in withIndex()) {
                argumentDefinition.name?.also { argumentName ->
                    namedArguments.firstOrNull {
                        it.name == argumentName
                    }?.let { value ->
                        registerArgument(value, index)
                        continue
                    }
                } ?: positionalArguments.getOrNull(index)?.let { positionalValue ->
                    registerArgument(positionalValue, index)
                    continue
                }

                if (argumentDefinition.hasDefaultValue) {
                    registerArgument(
                        value = ValueParameter.Declaration(
                            name = argumentDefinition.name,
                            value = Undefined,
                        ),
                        index = index,
                    )
                    continue
                }

                return null
            }

            return finalArguments.filterNotNull().takeIf {
                this.size == it.size
            }
        }

        fun List<ValueParameter.Definition>.isCompatible(
            arguments: List<ValueParameter.Declaration>,
        ) = prepareForCall(arguments) != null
    }
}
