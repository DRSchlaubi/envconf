package dev.schlaubi.envconf

import kotlin.properties.ReadOnlyProperty

/**
 * Helper class that allows you to specify a [prefix] for your whole config.
 *
 * Is intended to be used via composition or via inheritance
 */
@Suppress("MemberVisibilityCanBePrivate")
public open class Config constructor(private val prefix: String) {

    /**
     * Shortcut to make API usable like this
     *
     * ```kotlin
     * class Config : EConfig("prefix") {
     *
     *    val PORT by environment
     *
     * }
     * ```
     */
    protected val environment: EnvironmentVariable<String>
        get() = getEnv()

    /**
     * Calls [getEnv] with [prefix] applied to it.
     * @see getEnv
     */
    protected fun getEnv(default: String? = null): EnvironmentVariable<String> =
        getEnv(prefix, default)

    /**
     * Calls [getEnv] with [prefix] applied to it.
     * @see getEnv
     */
    protected fun <T> getEnv(
        default: T? = null,
        transform: (String) -> T?
    ): EnvironmentVariable<T> =
        getEnv(prefix, default, transform)
}
