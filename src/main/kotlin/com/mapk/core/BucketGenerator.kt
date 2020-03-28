package com.mapk.core

import com.mapk.annotations.KParameterRequireNonNull
import kotlin.reflect.KParameter

internal class BucketGenerator(parameters: List<KParameter>, instancePair: Pair<KParameter, Any>?) {
    private val initializationStatus: Array<Boolean>
    private val isRequireNonNull: List<Boolean>
    private val keyArray: Array<KParameter?>
    private val valueArray: Array<Any?>

    init {
        val capacity = parameters.size
        isRequireNonNull = parameters.map { param ->
            param.annotations.stream().anyMatch { it is KParameterRequireNonNull }
        }
        initializationStatus = Array(capacity) { false }

        keyArray = arrayOfNulls(capacity)
        valueArray = arrayOfNulls(capacity)

        if (instancePair != null) {
            keyArray[0] = instancePair.first
            valueArray[0] = instancePair.second
            initializationStatus[0] = true
        } else {
            initializationStatus[0] = false
        }
    }

    fun generate(): ArgumentBucket {
        return ArgumentBucket(
            keyArray.clone(),
            valueArray.clone(),
            isRequireNonNull,
            InitializationStatusManager(initializationStatus.clone())
        )
    }
}
