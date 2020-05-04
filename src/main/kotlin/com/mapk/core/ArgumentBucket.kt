package com.mapk.core

import java.util.Objects
import kotlin.reflect.KParameter

class ArgumentBucket internal constructor(
    private val keyList: List<KParameter>,
    internal val valueArray: Array<Any?>,
    private val isRequireNonNull: List<Boolean>,
    initializationStatus: Array<Boolean>
) : Map<KParameter, Any?> {
    private val initializationStatusManager = InitializationStatusManager(initializationStatus)
    val isInitialized: Boolean get() = initializationStatusManager.isFullInitialized

    class Entry internal constructor(
        override val key: KParameter,
        override var value: Any?
    ) : Map.Entry<KParameter, Any?>

    override val size: Int get() = initializationStatusManager.count

    override fun containsKey(key: KParameter): Boolean {
        return initializationStatusManager.isInitialized(key.index)
    }

    override fun containsValue(value: Any?): Boolean = valueArray.any { Objects.equals(value, it) }

    override fun get(key: KParameter): Any? = valueArray[key.index]
    fun getByIndex(key: Int): Any? =
        if (initializationStatusManager.isInitialized(key)) valueArray[key]
        else throw IllegalStateException("This argument is not initialized.")

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

    fun putIfAbsent(key: KParameter, value: Any?) {
        val index = key.index

        // null入力禁止かつnullなら無視する
        if (isRequireNonNull[index] && value == null) return

        // 先に入ったものを優先するため、初期化済みなら何もしない
        if (initializationStatusManager.isInitialized(index)) return

        initializationStatusManager.put(index)
        valueArray[index] = value

        return
    }
}
