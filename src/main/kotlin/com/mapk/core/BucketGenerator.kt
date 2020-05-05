package com.mapk.core

import com.mapk.annotations.KParameterFlatten
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

internal class BucketGenerator(
    private val parameters: List<KParameter>,
    filteredParameters: List<KParameter>, // フィルタリングは外でもやっているため、ここでは引数として受け取る
    instance: Any?,
    parameterNameConverter: (String) -> String
) {
    private val binders: List<ArgumentBinder>
    private val originalValueArray: Array<Any?>
    private val originalInitializationStatus: Array<Boolean>

    init {
        binders = filteredParameters.map {
            val name = it.getAliasOrName()!!

            it.findAnnotation<KParameterFlatten>()?.let { annotation ->
                // 名前の変換処理、結合が必要な場合はインスタンスを持ってきて対応する
                val converter: (String) -> String = if (annotation.fieldNameToPrefix) {
                    val joiner = annotation.namingConvention.objectInstance!!
                    { suffix -> parameterNameConverter(joiner.join(name, suffix)) }
                } else {
                    parameterNameConverter
                }

                ArgumentBinder.Function(
                    (it.type.classifier as KClass<*>).toKConstructor(converter),
                    it.index
                )
            } ?: ArgumentBinder.Value(parameterNameConverter(name), it.index)
        }
        // 配列系ではフィルタリング前のサイズが必要
        originalValueArray = arrayOfNulls(parameters.size)
        originalInitializationStatus = Array(parameters.size) { false }
        if (instance != null) {
            originalValueArray[0] = instance
            originalInitializationStatus[0] = true
        }
    }

    fun generate(adaptor: ArgumentAdaptor): ArgumentBucket = ArgumentBucket(
        parameters, originalValueArray.clone(), originalInitializationStatus.clone(), binders, adaptor
    )
}
