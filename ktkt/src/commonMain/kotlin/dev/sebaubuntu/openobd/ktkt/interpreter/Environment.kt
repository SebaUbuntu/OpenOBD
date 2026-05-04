/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt.interpreter

import dev.sebaubuntu.openobd.ktkt.KtKtException.Companion.runtimeError
import dev.sebaubuntu.openobd.ktkt.KtKtException.Companion.runtimeRequire

/**
 * Local environment.
 *
 * @param parent The parent [Environment]
 * @param typesRegistry The Kotlin interop [TypesRegistry]
 */
class Environment(
    val parent: Environment?,
    val typesRegistry: TypesRegistry? = null,
) {
    class ValueHolder(
        private var value: Any? = Undefined,
        private val isMutable: Boolean,
    ) {
        fun getValue(codePosition: Int): Any? = value.let {
            when (it is Undefined) {
                true -> runtimeError("Trying to access an uninitialized variable", codePosition)
                false -> it
            }
        }

        fun setValue(value: Any?, codePosition: Int) {
            runtimeRequire(value !is Undefined, codePosition) {
                "Trying to assign undefined to this variable"
            }

            runtimeRequire(isMutable || this.value is Undefined, codePosition) {
                "Cannot reassign non-mutable variable"
            }

            this.value = value
        }
    }

    /**
     * Variable declarations.
     */
    private val values = mutableMapOf<String, ValueHolder>()

    /**
     * Function declarations.
     */
    private val functions = mutableMapOf<String, RuntimeCallable>()

    /**
     * Map of import name to qualified name.
     */
    private val imports = mutableMapOf<String, String>()

    /**
     * Set of qualified names used to quickly check whether a name is already fully qualified.
     */
    private val knownQualifiedNames = mutableSetOf<String>()

    /**
     * Declares a new variable in the current scope.
     * Used by: `val x: Int = 5`
     */
    fun defineVariable(
        name: String,
        isMutable: Boolean,
        codePosition: Int,
        value: Any?,
    ) {
        runtimeRequire(name !in values, codePosition) {
            "Variable $name already defined"
        }

        runtimeRequire(value !is Undefined, codePosition) {
            "Trying to initialize a variable with an undefined value"
        }

        values[name] = ValueHolder(value, isMutable)
    }

    /**
     * Declare a new uninitialized variable in the current scope.
     * Used by: `val x: Int`
     */
    fun defineVariable(
        name: String,
        isMutable: Boolean,
        codePosition: Int,
    ) {
        runtimeRequire(name !in values, codePosition) {
            "Variable $name already defined"
        }

        values[name] = ValueHolder(Undefined, isMutable)
    }

    /**
     * Retrieves a variable from the current scope, or searches upwards.
     * Used by: `print(x)`
     */
    fun getVariable(name: String, codePosition: Int): Any? {
        values[name]?.let {
            return it.getValue(codePosition)
        }

        // If not found here, check the parent scope
        parent?.let {
            return it.getVariable(name, codePosition)
        }

        runtimeError("Undefined variable: $name", codePosition)
    }

    /**
     * Reassigns an existing variable, searching upwards if necessary.
     * Used by: `x = 10`
     */
    fun assignVariable(name: String, value: Any?, codePosition: Int) {
        values[name]?.let {
            it.setValue(value, codePosition)
            return
        }

        parent?.let {
            it.assignVariable(name, value, codePosition)
            return
        }

        runtimeError("Undefined variable: $name", codePosition)
    }

    /**
     * Declares a new function in the current scope.
     */
    fun defineFunction(name: String, function: RuntimeCallable) {
        functions[name] = function
    }

    fun getFunction(
        name: String,
        valueParameters: List<RuntimeCallable.ValueParameter.Declaration>,
    ): RuntimeCallable? {
        functions[name]?.let {
            return it
        }

        parent?.let {
            return it.getFunction(name, valueParameters)
        }

        typesRegistry?.let {
            return it.getFunction(name, valueParameters)
        }

        return null
    }

    /**
     * Define an import statement.
     *
     * @param qualifiedName The full qualified name of the type
     * @param name The short name, with the last package segment being the default
     */
    fun defineImport(
        qualifiedName: String,
        name: String = qualifiedName.substringAfterLast('.'),
        codePosition: Int,
    ) {
        if (name in imports) {
            runtimeError("Import name conflicts with ${imports[name]}", codePosition)
        }

        imports[name] = qualifiedName
        knownQualifiedNames.add(qualifiedName)
    }

    /**
     * Get the qualified name given the short name.
     */
    fun resolveType(name: String, codePosition: Int): String = when {
        name in knownQualifiedNames -> name
        else -> imports[name]
            ?: parent?.resolveType(name, codePosition)
            ?: runtimeError("Cannot resolve type", codePosition)
    }
}
