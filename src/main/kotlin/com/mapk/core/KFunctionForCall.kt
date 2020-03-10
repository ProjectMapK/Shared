package com.mapk.core

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.isAccessible

class KFunctionForCall<T>(private val function: KFunction<T>, instance: Any? = null) {
    val parameters: List<KParameter> = function.parameters
    private val generator: BucketGenerator

    init {
        if (parameters.isEmpty() || (instance != null && parameters.size == 1))
            throw IllegalArgumentException("This function is not require arguments.")

        // この関数には確実にアクセスするためアクセシビリティ書き換え
        function.isAccessible = true

        generator = if (instance != null) {
            BucketGenerator(parameters.size, parameters.first { it.kind == KParameter.Kind.INSTANCE } to instance)
        } else {
            BucketGenerator(parameters.size, null)
        }
    }

    fun getArgumentBucket(): ArgumentBucket = generator.generate()

    fun call(argumentBucket: ArgumentBucket): T =
        if (argumentBucket.isInitialized) function.call(*argumentBucket.valueArray)
        else function.callBy(argumentBucket)
}
