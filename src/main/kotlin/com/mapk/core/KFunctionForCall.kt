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

        // 初期化処理の共通化のため先に初期化
        val tempArray = Array<Any?>(parameters.size) { null }
        val maskList = generateSequence(1) { it.shl(1) }.take(parameters.size).toList()

        generator = if (instance != null) {
            tempArray[0] = instance

            // 引数の1番目は初期化済みということでinitializationStatusは1スタート
            BucketGenerator(tempArray, parameters.first { it.kind == KParameter.Kind.INSTANCE } to instance, 1, maskList)
        } else {
            BucketGenerator(tempArray, null, 0, maskList)
        }
    }

    fun getArgumentBucket(): ArgumentBucket = generator.generate()

    fun call(argumentBucket: ArgumentBucket): T =
        if (argumentBucket.isInitialized) function.call(*argumentBucket.bucket)
        else function.callBy(argumentBucket.bucketMap)
}
