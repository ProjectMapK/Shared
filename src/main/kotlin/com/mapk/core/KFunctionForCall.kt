package com.mapk.core

import com.mapk.annotations.KParameterFlatten
import com.mapk.core.internal.ArgumentBinder
import com.mapk.core.internal.BucketGenerator
import com.mapk.core.internal.ParameterNameConverter
import com.mapk.core.internal.getAliasOrName
import com.mapk.core.internal.getKConstructor
import com.mapk.core.internal.isUseDefaultArgument
import org.jetbrains.annotations.TestOnly
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaConstructor

class KFunctionForCall<T> internal constructor(
    @TestOnly
    internal val function: KFunction<T>,
    parameterNameConverter: ParameterNameConverter,
    instance: Any? = null
) {
    constructor(function: KFunction<T>, parameterNameConverter: ((String) -> String)?, instance: Any? = null) : this(
        function,
        ParameterNameConverter.Simple(parameterNameConverter),
        instance
    )

    @TestOnly
    internal val fullInitializedFunctionLambda: (Array<Any?>) -> T = function.javaConstructor?.let {
        { values -> it.newInstance(*values) }
    } ?: { function.call(*it) }

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

        val tempBinders = ArrayList<ArgumentBinder>()
        val tempList = ArrayList<ValueParameter<*>>()
        val tempMap = HashMap<String, ValueParameter<*>>()

        parameters.forEach { param ->
            if (param.kind == KParameter.Kind.VALUE && !param.isUseDefaultArgument()) {
                val binder = param.toArgumentBinder(parameterNameConverter)
                tempBinders.add(binder)

                when (binder) {
                    is ArgumentBinder.Value<*> -> addArgs(binder, tempList, tempMap)
                    is ArgumentBinder.Function -> binder.requiredParameters.forEach {
                        addArgs(it, tempList, tempMap)
                    }
                }
            }
        }

        bucketGenerator = BucketGenerator(parameters, tempBinders, instance)
        requiredParameters = tempList
        requiredParametersMap = tempMap
    }

    private fun addArgs(
        parameter: ValueParameter<*>,
        tempList: ArrayList<ValueParameter<*>>,
        tempMap: MutableMap<String, ValueParameter<*>>
    ) {
        if (tempMap.containsKey(parameter.name))
            throw IllegalArgumentException("The argument name ${parameter.name} is duplicated.")
        tempMap[parameter.name] = parameter
        tempList.add(parameter)
    }

    fun getArgumentAdaptor(): ArgumentAdaptor = ArgumentAdaptor(requiredParametersMap)

    fun call(adaptor: ArgumentAdaptor): T {
        val bucket = bucketGenerator.generate(adaptor)
        return if (bucket.isInitialized) fullInitializedFunctionLambda(bucket.valueArray) else function.callBy(bucket)
    }
}

fun <T : Any> KClass<T>.toKConstructor(parameterNameConverter: ((String) -> String)?): KFunctionForCall<T> =
    this.getKConstructor().let { (instance, function) ->
        KFunctionForCall(function, ParameterNameConverter.Simple(parameterNameConverter), instance)
    }

private fun KParameter.toArgumentBinder(parameterNameConverter: ParameterNameConverter): ArgumentBinder {
    val name = getAliasOrName()!!

    return findAnnotation<KParameterFlatten>()?.let { annotation ->
        // 名前の変換処理
        val converter: ParameterNameConverter = if (annotation.fieldNameToPrefix) {
            // 結合が必要な場合は結合機能のインスタンスを持ってきて対応する
            parameterNameConverter.nest(name, annotation.nameJoiner.objectInstance!!)
        } else {
            // プレフィックスを要求しない場合は全てsimpleでマップするように修正
            parameterNameConverter.toSimple()
        }

        getKClass().getKConstructor().let { (instance, function) ->
            ArgumentBinder.Function(KFunctionForCall(function, converter, instance), index, annotations)
        }
    } ?: ArgumentBinder.Value(
        index,
        annotations,
        isOptional,
        parameterNameConverter.convert(name),
        getKClass()
    )
}
