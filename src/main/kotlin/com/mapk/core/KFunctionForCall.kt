package com.mapk.core

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.isAccessible

class KFunctionForCall<T>(private val function: KFunction<T>, instance: Any? = null) {
    val parameters: List<KParameter> = function.parameters
    private val originalArgumentBucket: ArgumentBucket

    init {
        if (parameters.isEmpty() || (instance != null && parameters.size == 1))
            throw IllegalArgumentException("This function is not require arguments.")

        // この関数には確実にアクセスするためアクセシビリティ書き換え
        function.isAccessible = true

        // 初期化処理の共通化のため先に初期化
        val tempMap = HashMap<KParameter, Any?>(parameters.size, 1.0f)
        val tempArray = Array<Any?>(parameters.size) { null }
        val maskList = generateSequence(1) { it.shl(1) }.take(parameters.size).toList()

        originalArgumentBucket = if (instance != null) {
            // インスタンス有りでは先にインスタンスを初期化する
            parameters.find { it.kind == KParameter.Kind.INSTANCE }?.run { tempMap[this] = instance }
            tempArray[0] = instance

            // 引数の1番目は初期化済みということでinitializationStatusは1スタート
            ArgumentBucket(tempArray, tempMap, 1, maskList)
        } else {
            ArgumentBucket(tempArray, tempMap, 0, maskList)
        }
    }

    fun getArgumentBucket(): ArgumentBucket = originalArgumentBucket.clone()

    fun call(argumentBucket: ArgumentBucket): T =
        if (argumentBucket.isInitialized) function.call(*argumentBucket.bucket)
        else function.callBy(argumentBucket.bucketMap)
}
