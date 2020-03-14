package com.mapk.core

import com.mapk.annotations.KConstructor
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.functions
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible

class KFunctionForCall<T>(internal val function: KFunction<T>, instance: Any? = null) {
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

@Suppress("UNCHECKED_CAST")
fun <T : Any> KClass<T>.toKConstructor(): KFunctionForCall<T> {
    val factoryConstructor: List<KFunctionForCall<T>> =
        this.companionObjectInstance?.let { companionObject ->
            companionObject::class.functions
                .filter { it.annotations.any { annotation -> annotation is KConstructor } }
                .map { KFunctionForCall(
                    it,
                    companionObject
                ) as KFunctionForCall<T> }
        } ?: emptyList()

    val constructors: List<KFunctionForCall<T>> = factoryConstructor + this.constructors
        .filter { it.annotations.any { annotation -> annotation is KConstructor } }
        .map { KFunctionForCall(it) }

    if (constructors.size == 1) return constructors.single()

    if (constructors.isEmpty()) return KFunctionForCall(this.primaryConstructor!!)

    throw IllegalArgumentException("Find multiple target.")
}
