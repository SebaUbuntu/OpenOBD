/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt.interpreter

import dev.sebaubuntu.openobd.ktkt.KtKtException.Companion.runtimeRequire
import dev.sebaubuntu.openobd.ktkt.interpreter.KotlinCallable.Type.Companion.builtinTypeOf
import dev.sebaubuntu.openobd.ktkt.interpreter.KotlinCallable.Type.Companion.isInstance
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.safeCast
import kotlin.reflect.typeOf

/**
 * Kotlin builtin callable wrapper.
 *
 * @param R Return type
 */
sealed class KotlinCallable<R> : RuntimeCallable {
    /**
     * Runtime type.
     */
    data class Type<T>(
        val clazz: KClass<T & Any>,
        val isNullable: Boolean,
    ) {
        val name by lazy { clazz.getTypeName(isNullable) }

        @Suppress("UNCHECKED_CAST")
        fun cast(value: Any?): T = value?.let(clazz::cast) ?: when (isNullable) {
            true -> null as T
            false -> error("Cast failed.")
        }

        /**
         * @see KClass.safeCast
         */
        fun safeCast(value: Any?): T? = clazz.safeCast(value)

        companion object {
            @OptIn(ExperimentalContracts::class)
            fun <T> Type<T>.isInstance(value: Any?): Boolean {
                contract {
                    returns(true) implies (value is T)
                }

                return value?.let { value ->
                    clazz.isInstance(value)
                } ?: isNullable
            }

            inline fun <reified T> builtinTypeOf() = typeOf<T>().let { type ->
                @Suppress("UNCHECKED_CAST")
                Type<T>(
                    clazz = type.classifier as KClass<T & Any>,
                    isNullable = type.isMarkedNullable,
                )
            }
        }
    }

    abstract class Zero<R>(
        override val returnType: Type<R>,
    ) : KotlinCallable<R>() {
        override val argumentTypes = listOf<Pair<String?, Type<*>>>()

        abstract fun block(): R

        override fun invoke(arguments: List<Any?>): R = block()
    }

    abstract class One<T1, R>(
        private val parameter1Type: Type<T1>,
        override val returnType: Type<R>,
    ) : KotlinCallable<R>() {
        override val argumentTypes = listOf(
            null to parameter1Type,
        )

        abstract fun block(p1: T1): R

        @Suppress("UNCHECKED_CAST")
        override fun invoke(arguments: List<Any?>): R = block(
            parameter1Type.cast(arguments[0]),
        )
    }

    abstract class Two<T1, T2, R>(
        private val parameter1Type: Type<T1>,
        private val parameter2Type: Type<T2>,
        override val returnType: Type<R>,
    ) : KotlinCallable<R>() {
        override val argumentTypes = listOf(
            null to parameter1Type,
            null to parameter2Type,
        )

        abstract fun block(p1: T1, p2: T2): R

        @Suppress("UNCHECKED_CAST")
        override fun invoke(arguments: List<Any?>): R = block(
            parameter1Type.cast(arguments[0]),
            parameter2Type.cast(arguments[1]),
        )
    }

    /**
     * Arguments types.
     */
    abstract val argumentTypes: List<Pair<String?, Type<*>>>

    /**
     * Return type.
     */
    abstract val returnType: Type<R>

    override val arguments by lazy {
        argumentTypes.map { (name, type) ->
            RuntimeCallable.ValueParameter.Definition(
                name = name,
                type = type.toArgumentDefinitionType(),
                hasDefaultValue = false,
            )
        }
    }

    abstract fun invoke(arguments: List<Any?>): R

    override fun call(
        evaluator: Evaluator,
        valueParameters: List<RuntimeCallable.ValueParameter.Declaration>,
        codePosition: Int,
    ): Any? {
        runtimeRequire(valueParameters.size == this.arguments.size, codePosition) {
            "Provided ${valueParameters.size} arguments, expected ${this.arguments.size}"
        }

        argumentTypes.forEachIndexed { index, (name, type) ->
            runtimeRequire(type.isInstance(valueParameters[index]), codePosition) {
                "Expected argument ${name ?: index} of type ${type.name}, got ${
                    typeNameOf(valueParameters[index])
                }"
            }
        }

        // We can assume the return type is correct
        return invoke(valueParameters.map { it.value })
    }

    companion object {
        inline fun <reified R> toKotlinCallable(
            crossinline block: () -> R,
        ) = object : Zero<R>(
            returnType = builtinTypeOf<R>(),
        ) {
            override fun block(): R = block()
        }

        inline fun <reified T1, reified R> toKotlinCallable(
            crossinline block: (T1) -> R,
        ) = object : One<T1, R>(
            parameter1Type = builtinTypeOf<T1>(),
            returnType = builtinTypeOf<R>(),
        ) {
            override fun block(p1: T1): R = block(p1)
        }

        inline fun <reified T1, reified T2, reified R> toKotlinCallable(
            crossinline block: (T1, T2) -> R,
        ) = object : Two<T1, T2, R>(
            parameter1Type = builtinTypeOf<T1>(),
            parameter2Type = builtinTypeOf<T2>(),
            returnType = builtinTypeOf<R>(),
        ) {
            override fun block(p1: T1, p2: T2): R = block(p1, p2)
        }

        private fun <T> Type<T>.toArgumentDefinitionType() =
            RuntimeCallable.ValueParameter.Definition.Type(
                name = clazz.typeName,
                isNullable = isNullable,
            )

        private val KClass<*>?.typeName: String
            get() = this?.let { it.qualifiedName ?: "<unknown>" } ?: "kotlin.Nothing"

        private fun KClass<*>?.getTypeName(isNullable: Boolean): String = typeName.let {
            when (isNullable) {
                true -> "${it}?"
                false -> it
            }
        }

        private fun typeNameOf(value: Any?): String = value?.let {
            it::class.typeName
        } ?: Nothing::class.getTypeName(true)
    }
}
