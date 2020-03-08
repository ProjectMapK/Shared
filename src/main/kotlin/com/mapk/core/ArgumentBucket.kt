package com.mapk.core

import kotlin.reflect.KParameter

internal class BucketGenerator(
    private val bucket: Array<Any?>,
    private val bucketMap: MutableMap<KParameter, Any?>,
    private val initializationStatus: Int,
    private val initializeMask: List<Int>
) {
    private val completionValue: Int = initializeMask.reduce { l, r -> l or r }

    fun generate(): ArgumentBucket = ArgumentBucket(
        bucket.copyOf(),
        bucketMap.toMutableMap(),
        initializationStatus,
        initializeMask,
        completionValue
    )
}

class ArgumentBucket internal constructor(
    internal val bucket: Array<Any?>,
    internal val bucketMap: MutableMap<KParameter, Any?>,
    private var initializationStatus: Int,
    private val initializeMask: List<Int>,
    private val completionValue: Int
) {
    val isInitialized: Boolean get() = initializationStatus == completionValue

    fun setArgument(kParameter: KParameter, argument: Any?) {
        val index = kParameter.index
        val temp = initializationStatus or initializeMask[index]

        // 先に入ったものを優先するため、初期化済みなら何もしない
        if (initializationStatus == temp) return

        bucketMap[kParameter] = argument
        bucket[index] = argument
        initializationStatus = temp
    }
}
