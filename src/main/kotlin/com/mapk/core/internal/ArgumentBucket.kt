package com.mapk.core.internal

import com.mapk.core.ArgumentAdaptor
import java.util.Objects
import kotlin.reflect.KParameter

internal class ArgumentBucket(
    private val keyList: List<KParameter>,
    val valueArray: Array<Any?>,
    initializationStatus: Array<Boolean>,
    argumentBinders: List<ArgumentBinder>,
    adaptor: ArgumentAdaptor
) : Map<KParameter, Any?> {
    class Entry internal constructor(
        override val key: KParameter,
        override var value: Any?
    ) : Map.Entry<KParameter, Any?>

    private val initializationStatuses: List<Boolean>
    val isInitialized: Boolean
    override val size: Int

    init {
        var count: Int = if (initializationStatus[0]) 1 else 0

        argumentBinders.forEach {
            val result = it.bindArgument(adaptor, valueArray)
            if (result) {
                count++
                initializationStatus[it.index] = true
            }
        }

        initializationStatuses = initializationStatus.asList()
        isInitialized = count == initializationStatus.size
        size = count
    }

    override fun containsKey(key: KParameter): Boolean = initializationStatuses[key.index]

    override fun containsValue(value: Any?): Boolean = valueArray.any { Objects.equals(value, it) }

    override fun get(key: KParameter): Any? = valueArray[key.index]

    override fun isEmpty(): Boolean = size == 0

    // NOTE: 本来であれば生成時に確定する内容だが、これらのメソッドは関数呼び出し時にアクセスされることが無いため、カスタムゲッターとしている
    override val entries: Set<Map.Entry<KParameter, Any?>>
        get() = keyList
            .filter { initializationStatuses[it.index] }
            .map { Entry(it, valueArray[it.index]) }
            .toSet()
    override val keys: Set<KParameter>
        get() = keyList.filter { initializationStatuses[it.index] }.toSet()
    override val values: Collection<Any?>
        get() = valueArray.filterIndexed { i, _ -> initializationStatuses[i] }
}
