package com.mapk.core.internal

import kotlin.reflect.KFunction
import kotlin.reflect.jvm.javaConstructor
import kotlin.reflect.jvm.javaMethod

internal class FullInitializedFunctionWrapper<T>(function: KFunction<T>, instance: Any?, paramSize: Int) {
    private val lambda: (Array<Any?>) -> T

    init {
        val constructor = function.javaConstructor

        lambda = when {
            constructor != null -> {
                { constructor.newInstance(*it) }
            }
            instance != null -> {
                val method = function.javaMethod!!

                @Suppress("UNCHECKED_CAST") { method.invoke(instance, *(it.copyOfRange(1, paramSize))) as T }
            }
            else -> {
                { function.call(*it) }
            }
        }
    }

    fun call(args: Array<Any?>): T = lambda(args)
}
