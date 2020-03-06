package com.mapk.core

class ArgumentBucket internal constructor(
    internal val bucket: Array<Any?>,
    private var initializationStatus: Int,
    private val initializeMask: List<Int>,
    // clone時の再計算を避けるため1回で済むようにデフォルト値化
    private val completionValue: Int = initializeMask.reduce { l, r -> l or r }
) : Cloneable {
    val isInitialized: Boolean get() = initializationStatus == completionValue
    val notInitializedParameterIndexes: List<Int> get() = initializeMask.indices.filter {
        initializationStatus and initializeMask[it] == 0
    }

    fun setArgument(argument: Any?, index: Int) {
        val temp = initializationStatus or initializeMask[index]

        // 先に入ったものを優先するため、初期化済みなら何もしない
        if (initializationStatus == temp) return

        bucket[index] = argument
        initializationStatus = temp
    }

    public override fun clone(): ArgumentBucket {
        return ArgumentBucket(
            bucket.copyOf(),
            initializationStatus,
            initializeMask,
            completionValue
        )
    }
}
