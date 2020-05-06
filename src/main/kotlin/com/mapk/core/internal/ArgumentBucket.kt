package com.mapk.core.internal

import com.mapk.core.ArgumentAdaptor
import java.util.Objects
import kotlin.reflect.KParameter

// TODO: 初期化の効率化方法の検討他
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

    private val initializationStatusManager = InitializationStatusManager(initializationStatus)
    val isInitialized: Boolean
    override val size: Int

    init {
        argumentBinders.forEach {
            val result = it.bindArgument(adaptor, valueArray)
            if (result) initializationStatusManager.put(it.index)
        }

        isInitialized = initializationStatusManager.isFullInitialized
        size = initializationStatusManager.count
    }

    override fun containsKey(key: KParameter): Boolean = initializationStatusManager.isInitialized(key.index)

    override fun containsValue(value: Any?): Boolean = valueArray.any { Objects.equals(value, it) }

    override fun get(key: KParameter): Any? = valueArray[key.index]

    override fun isEmpty(): Boolean = initializationStatusManager.count == 0

    override val entries: Set<Map.Entry<KParameter, Any?>>
        get() = keyList
            .filter { initializationStatusManager.isInitialized(it.index) }
            .map { Entry(it, valueArray[it.index]) }
            .toSet()
    override val keys: Set<KParameter>
        get() = keyList.filter { initializationStatusManager.isInitialized(it.index) }.toSet()
    override val values: Collection<Any?>
        get() = valueArray.filterIndexed { i, _ -> initializationStatusManager.isInitialized(i) }
}
