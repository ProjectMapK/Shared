package com.mapk.core

import kotlin.reflect.KParameter

class ArgumentBucket internal constructor(
    internal val bucket: Array<Any?>,
    internal val bucketMap: MutableMap<KParameter, Any?>,
    private var initializationStatus: Int,
    private val initializeMask: List<Int>,
    // clone時の再計算を避けるため1回で済むようにデフォルト値化
    private val completionValue: Int = initializeMask.reduce { l, r -> l or r }
) : Cloneable {
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

    public override fun clone(): ArgumentBucket {
        return ArgumentBucket(
            bucket.copyOf(),
            bucketMap.toMutableMap(),
            initializationStatus,
            initializeMask,
            completionValue
        )
    }
}
