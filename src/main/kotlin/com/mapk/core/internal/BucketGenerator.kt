package com.mapk.core.internal

import com.mapk.annotations.KParameterFlatten
import com.mapk.core.ArgumentAdaptor
import com.mapk.core.ValueParameter
import com.mapk.core.toKConstructor
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

internal class BucketGenerator(
    private val parameters: List<KParameter>,
    filteredParameters: List<KParameter>, // フィルタリングは外でもやっているため、ここでは引数として受け取る
    instance: Any?,
    parameterNameConverter: ParameterNameConverter
) {
    private val binders: List<ArgumentBinder> = filteredParameters.map { it.toArgumentBinder(parameterNameConverter) }
    private val originalValueArray: Array<Any?> = arrayOfNulls(parameters.size)
    private val originalInitializationStatus: Array<Boolean> = Array(parameters.size) { false }
    val valueParameters: List<ValueParameter<*>>

    init {
        if (instance != null) {
            originalValueArray[0] = instance
            originalInitializationStatus[0] = true
        }

        // TODO: 仮置き、これを生成するのはKFunctionForCallの方が良さげ
        valueParameters = binders.fold(ArrayList()) { acc, elm ->
            when (elm) {
                is ArgumentBinder.Value<*> -> acc.add(elm)
                is ArgumentBinder.Function -> acc.addAll(elm.requiredParameters)
            }
            acc
        }
    }

    fun generate(adaptor: ArgumentAdaptor): ArgumentBucket =
        ArgumentBucket(parameters, originalValueArray.clone(), originalInitializationStatus.clone(), binders, adaptor)
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

        ArgumentBinder.Function((type.classifier as KClass<*>).toKConstructor(converter), index, annotations)
    } ?: ArgumentBinder.Value(
        index,
        annotations,
        isOptional,
        parameterNameConverter.convert(name),
        type.classifier as KClass<*>
    )
}
