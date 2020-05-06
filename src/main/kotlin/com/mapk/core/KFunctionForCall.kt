package com.mapk.core

import com.mapk.annotations.KConstructor
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.functions
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.isAccessible
import org.jetbrains.annotations.TestOnly

class KFunctionForCall<T>(
    @TestOnly
    internal val function: KFunction<T>,
    parameterNameConverter: (String) -> String,
    instance: Any? = null
) {
    @TestOnly
    internal val parameters: List<KParameter> = function.parameters

    // 上は外部への公開用、下はArgumentAdaptor生成用
    val requiredParameters: List<ValueParameter<*>>
    private val requiredParametersMap: Map<String, ValueParameter<*>>

    private val bucketGenerator: BucketGenerator

    init {
        if (parameters.isEmpty() || (instance != null && parameters.size == 1))
            throw IllegalArgumentException("This function is not require arguments.")

        // この関数には確実にアクセスするためアクセシビリティ書き換え
        function.isAccessible = true

        val filteredParameters = parameters.filter { it.kind == KParameter.Kind.VALUE && !it.isUseDefaultArgument() }
        bucketGenerator = BucketGenerator(parameters, filteredParameters, instance, parameterNameConverter)

        requiredParameters = bucketGenerator.valueParameters
        requiredParametersMap = requiredParameters.associateBy { it.name }
    }

    fun getArgumentAdaptor(): ArgumentAdaptor = ArgumentAdaptor(requiredParametersMap)

    fun call(adaptor: ArgumentAdaptor): T {
        val bucket = bucketGenerator.generate(adaptor)
        return if (bucket.isInitialized) function.call(*bucket.valueArray) else function.callBy(bucket)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> KClass<T>.toKConstructor(parameterNameConverter: (String) -> String): KFunctionForCall<T> {
    val factoryConstructor: List<KFunctionForCall<T>> =
        this.companionObjectInstance?.let { companionObject ->
            companionObject::class.functions
                .filter { it.annotations.any { annotation -> annotation is KConstructor } }
                .map { KFunctionForCall(it, parameterNameConverter, companionObject) as KFunctionForCall<T> }
        } ?: emptyList()

    val constructors: List<KFunctionForCall<T>> = factoryConstructor + this.constructors
        .filter { it.annotations.any { annotation -> annotation is KConstructor } }
        .map { KFunctionForCall(it, parameterNameConverter) }

    if (constructors.size == 1) return constructors.single()

    if (constructors.isEmpty()) return KFunctionForCall(this.primaryConstructor!!, parameterNameConverter)

    throw IllegalArgumentException("Find multiple target.")
}
