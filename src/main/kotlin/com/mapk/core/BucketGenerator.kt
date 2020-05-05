package com.mapk.core

import kotlin.reflect.KParameter

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
        // TODO: ネスト関連への対応
        binders = filteredParameters.map { ArgumentBinder.Value(parameterNameConverter(it.getAliasOrName()!!), it.index) }
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
