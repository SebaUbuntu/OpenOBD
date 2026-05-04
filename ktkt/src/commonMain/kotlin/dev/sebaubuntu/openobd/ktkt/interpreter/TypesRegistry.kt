/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt.interpreter

import dev.sebaubuntu.openobd.ktkt.interpreter.KotlinCallable.Companion.toKotlinCallable
import dev.sebaubuntu.openobd.ktkt.interpreter.RuntimeCallable.Companion.isCompatible
import dev.sebaubuntu.openobd.ktkt.interpreter.RuntimeCallable.Companion.validateOrder
import dev.sebaubuntu.openobd.ktkt.interpreter.TypesRegistry.Companion.registerType
import kotlin.reflect.KClass

/**
 * Holder class for all known types.
 *
 * The following packages of the standard library are implicitly imported:
 *
 * - `kotlin`
 * - `kotlin.annotation`
 * - `kotlin.collections`
 * - `kotlin.comparisons`
 * - `kotlin.io`
 * - `kotlin.math`
 * - `kotlin.ranges`
 * - `kotlin.sequences`
 * - `kotlin.text`
 */
class TypesRegistry {
    /**
     * Types.
     */
    private val nameToType = mutableMapOf<String, KClass<*>>()

    /**
     * Functions.
     */
    private val nameToFunction = mutableMapOf<String, MutableList<RuntimeCallable>>()

    /**
     * Implicitly imported types.
     */
    private val implicitTypeImports = mutableMapOf<String, String>()

    /**
     * Implicitly imported types.
     */
    private val implicitFunctionImports = mutableMapOf<String, MutableSet<String>>()

    init {
        // Types
        builtinTypes.forEach { (kClass, implicitlyImported) ->
            registerType(
                kClass = kClass,
                implicitlyImported = implicitlyImported,
            )
        }

        // Functions
        builtinFunctions.forEach { (qualifierName, runtimeCallable) ->
            registerFunction(
                runtimeCallable = runtimeCallable,
                qualifierName = qualifierName,
                implicitlyImported = true,
            )
        }
    }

    fun registerType(
        kClass: KClass<*>,
        qualifierName: String? = null,
        implicitlyImported: Boolean = false,
    ) {
        val qualifierName = qualifierName ?: kClass.qualifiedName ?: error(
            "Unknown qualifier name, specify it manually"
        )

        when {
            qualifierName in nameToType -> require(nameToType[qualifierName] == kClass) {
                "Trying to register ${kClass.qualifiedName} with an already used name"
            }

            else -> {
                nameToType[qualifierName] = kClass

                if (implicitlyImported) {
                    val simpleName = qualifierName.substringAfterLast('.')

                    when {
                        simpleName in implicitTypeImports -> require(
                            implicitTypeImports[simpleName] == qualifierName
                        ) {
                            "Trying to register $simpleName with an already used name"
                        }

                        else -> implicitTypeImports[simpleName] = qualifierName
                    }
                }
            }
        }
    }

    fun registerFunction(
        runtimeCallable: RuntimeCallable,
        qualifierName: String,
        implicitlyImported: Boolean = false,
    ) {
        nameToFunction.getOrPut(
            key = qualifierName,
            defaultValue = ::mutableListOf,
        ).add(runtimeCallable)

        if (implicitlyImported) {
            val simpleName = qualifierName.substringAfterLast('.')

            implicitFunctionImports.getOrPut(
                key = simpleName,
                defaultValue = ::mutableSetOf,
            ).add(qualifierName)
        }
    }

    fun getType(qualifiedName: String) = when (qualifiedName) {
        in nameToType -> nameToType[qualifiedName]
        in implicitTypeImports -> nameToType[implicitTypeImports[qualifiedName]]
        else -> null
    }

    fun getFunction(
        name: String,
        valueParameters: List<RuntimeCallable.ValueParameter.Declaration>,
    ) = when (name) {
        in nameToFunction -> nameToFunction[name]

        in implicitFunctionImports -> implicitFunctionImports[
            name
        ]?.let { implicitFunctionImports ->
            implicitFunctionImports.flatMap {
                nameToFunction.getValue(it)
            }
        }

        else -> null
    }?.let { callables ->
        valueParameters.validateOrder()

        val matchingOverloads = callables.filter {
            it.arguments.isCompatible(valueParameters)
        }

        when (matchingOverloads.size) {
            0 -> error("Incompatible value parameters for function $name")
            1 -> matchingOverloads.first()
            else -> error("Ambiguous overload resolving for function $name")
        }
    }

    companion object {
        /**
         * Builtin types to whether it's implicitly imported.
         */
        @OptIn(ExperimentalUnsignedTypes::class)
        private val builtinTypes = listOf(
            // `kotlin`
            Annotation::class to true,
            Any::class to true,
            ArithmeticException::class to true,
            Array::class to true,
            AssertionError::class to true,
            AutoCloseable::class to true,
            Boolean::class to true,
            BooleanArray::class to true,
            Byte::class to true,
            ByteArray::class to true,
            Char::class to true,
            CharArray::class to true,
            CharSequence::class to true,
            ClassCastException::class to true,
            Comparable::class to true,
            Comparator::class to true,
            ConcurrentModificationException::class to true,
            DeepRecursiveFunction::class to true,
            DeepRecursiveScope::class to true,
            DeprecationLevel::class to true,
            Double::class to true,
            DoubleArray::class to true,
            Enum::class to true,
            Error::class to true,
            Exception::class to true,
            Float::class to true,
            FloatArray::class to true,
            Function::class to true,
            IllegalArgumentException::class to true,
            IllegalStateException::class to true,
            IndexOutOfBoundsException::class to true,
            Int::class to true,
            IntArray::class to true,
            KotlinVersion::class to true,
            Lazy::class to true,
            LazyThreadSafetyMode::class to true,
            Long::class to true,
            LongArray::class to true,
            NoSuchElementException::class to true,
            NotImplementedError::class to true,
            NullPointerException::class to true,
            Nothing::class to true,
            Number::class to true,
            NumberFormatException::class to true,
            Pair::class to true,
            Result::class to true,
            RuntimeException::class to true,
            Short::class to true,
            ShortArray::class to true,
            String::class to true,
            Throwable::class to true,
            Triple::class to true,
            UByte::class to true,
            UByteArray::class to true,
            UInt::class to true,
            UIntArray::class to true,
            ULong::class to true,
            ULongArray::class to true,
            UShort::class to true,
            UShortArray::class to true,
            Unit::class to true,
            UnsupportedOperationException::class to true,

            // `kotlin.collections`
            AbstractCollection::class to true,
            AbstractIterator::class to true,
            AbstractList::class to true,
            AbstractMap::class to true,
            AbstractMutableCollection::class to true,
            AbstractMutableList::class to true,
            AbstractMutableMap::class to true,
            AbstractMutableSet::class to true,
            AbstractSet::class to true,
            ArrayDeque::class to true,
            ArrayList::class to true,
            BooleanIterator::class to true,
            ByteIterator::class to true,
            CharIterator::class to true,
            Collection::class to true,
            DoubleIterator::class to true,
            FloatIterator::class to true,
            Grouping::class to true,
            HashMap::class to true,
            HashSet::class to true,
            IndexedValue::class to true,
            IntIterator::class to true,
            Iterable::class to true,
            Iterator::class to true,
            LinkedHashMap::class to true,
            LinkedHashSet::class to true,
            List::class to true,
            ListIterator::class to true,
            LongIterator::class to true,
            Map::class to true,
            MutableCollection::class to true,
            MutableIterable::class to true,
            MutableIterator::class to true,
            MutableList::class to true,
            MutableListIterator::class to true,
            MutableMap::class to true,
            MutableSet::class to true,
            RandomAccess::class to true,
            Set::class to true,
            ShortIterator::class to true,

            // `kotlin.ranges`
            CharProgression::class to true,
            CharRange::class to true,
            ClosedFloatingPointRange::class to true,
            ClosedRange::class to true,
            IntProgression::class to true,
            IntRange::class to true,
            LongProgression::class to true,
            LongRange::class to true,
            OpenEndRange::class to true,
            UIntProgression::class to true,
            UIntRange::class to true,
            ULongProgression::class to true,
            ULongRange::class to true,

            // `kotlin.sequences`
            Sequence::class to true,
            SequenceScope::class to true,

            // `kotlin.text`
            Appendable::class to true,
            CharacterCodingException::class to true,
            CharCategory::class to true,
            HexFormat::class to true,
            MatchGroup::class to true,
            MatchGroupCollection::class to true,
            MatchNamedGroupCollection::class to true,
            MatchResult::class to true,
            Regex::class to true,
            RegexOption::class to true,
            StringBuilder::class to true,
            Typography::class to true,
        )

        private val builtinFunctions = listOf<Pair<String, RuntimeCallable>>(
            // `kotlin`
            "kotlin.addSuppressed" to toKotlinCallable(Throwable::addSuppressed),
            "kotlin.also" to toKotlinCallable(::also),
            "kotlin.apply" to toKotlinCallable(::apply),
            "kotlin.arrayOf" to toKotlinCallable<Array<Any?>, Any?>(::arrayOf),
            "kotlin.arrayOfNulls" to toKotlinCallable<Int, Array<Any?>>(::arrayOfNulls),
            "kotlin.emptyArray" to toKotlinCallable<Array<Any?>>(::emptyArray),
            "kotlin.error" to toKotlinCallable<Any, Nothing?>(::error),
            "kotlin.hashCode" to toKotlinCallable(Any?::hashCode),
            "kotlin.lazy" to toKotlinCallable<() -> Any?, Lazy<Any?>>(::lazy),
            "kotlin.lazy" to toKotlinCallable<LazyThreadSafetyMode, () -> Any?, Lazy<Any?>>(::lazy),
            "kotlin.lazyOf" to toKotlinCallable<Any?, Lazy<Any?>>(::lazyOf),
            "kotlin.let" to toKotlinCallable<_, (Any?) -> Any?, _>(Any?::let),
            "kotlin.repeat" to toKotlinCallable(::repeat),
            "kotlin.require" to toKotlinCallable<Boolean, Unit>(::require),
            "kotlin.require" to toKotlinCallable<Boolean, () -> Any, Unit>(::require),
            "kotlin.requireNotNull" to toKotlinCallable<Any?, Any>(::requireNotNull),
            "kotlin.requireNotNull" to toKotlinCallable<Any?, () -> Any, Any>(::requireNotNull),
            "kotlin.run" to toKotlinCallable<() -> Any?, Any?>(::run),
            "kotlin.run" to toKotlinCallable<_, Any?.() -> Any?, _>(Any?::run),
            "kotlin.runCatching" to toKotlinCallable<() -> Any?, _>(::runCatching),
            "kotlin.runCatching" to toKotlinCallable<_, Any?.() -> Any?, _>(Any?::runCatching),
            "kotlin.takeIf" to toKotlinCallable(Any?::takeIf),
            "kotlin.takeUnless" to toKotlinCallable(Any?::takeUnless),
            "kotlin.to" to toKotlinCallable<_, _, Pair<Any?, Any?>>(Any?::to),
            "kotlin.TODO" to toKotlinCallable<Nothing?>(::TODO),
            "kotlin.TODO" to toKotlinCallable<String, Nothing?>(::TODO),
            "kotlin.toList" to toKotlinCallable(Pair<Any?, Any?>::toList),
            "kotlin.toList" to toKotlinCallable(Triple<Any?, Any?, Any?>::toList),
            "kotlin.toString" to toKotlinCallable(Any?::toString),
            "kotlin.use" to toKotlinCallable<_, (Any?) -> Any?, _>(AutoCloseable?::use),
            "kotlin.with" to toKotlinCallable<_, Any?.() -> Any?, _>(::with),

            // `kotlin.io`
            "kotlin.io.print" to toKotlinCallable<Any?, Unit>(::print),
            "kotlin.io.println" to toKotlinCallable<Unit>(::println),
            "kotlin.io.println" to toKotlinCallable<Any?, Unit>(::println),
            "kotlin.io.readln" to toKotlinCallable(::readln),
            "kotlin.io.readlnOrNull" to toKotlinCallable(::readlnOrNull),
        )

        /**
         * @see TypesRegistry.registerType
         */
        inline fun <reified T> TypesRegistry.registerType(
            qualifierName: String? = null,
            implicitlyImported: Boolean = false,
        ) = registerType(T::class, qualifierName, implicitlyImported)
    }
}
