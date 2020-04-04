package com.mapk.core

import com.mapk.annotations.KParameterRequireNonNull
import kotlin.reflect.KParameter

internal class BucketGenerator(private val parameters: List<KParameter>, instance: Any?) {
    private val initializationStatus: Array<Boolean>
    private val isRequireNonNull: List<Boolean>
    private val valueArray: Array<Any?>

    init {
        val capacity = parameters.size
        isRequireNonNull = parameters.map { param ->
            param.annotations.stream().anyMatch { it is KParameterRequireNonNull }
        }
        initializationStatus = Array(capacity) { false }

        valueArray = arrayOfNulls(capacity)

        if (instance != null) {
            valueArray[0] = instance
            initializationStatus[0] = true
        } else {
            initializationStatus[0] = false
        }
    }

    fun generate(): ArgumentBucket {
        return ArgumentBucket(
            parameters,
            valueArray.clone(),
            isRequireNonNull,
            InitializationStatusManager(initializationStatus.clone())
        )
    }
}
