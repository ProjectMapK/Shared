package com.mapk.core

import java.util.Objects
import kotlin.reflect.KParameter

class ArgumentBucket internal constructor(
    private val keyArray: Array<KParameter?>,
    internal val valueArray: Array<Any?>,
    private val isRequireNonNull: List<Boolean>,
    private val initializationStatusManager: InitializationStatusManager
) : Map<KParameter, Any?> {
    // インスタンス有りなら1、そうでなければ0スタート
    private var count: Int = initializationStatusManager.count

    val isInitialized: Boolean get() = initializationStatusManager.isFullInitialized

    class Entry internal constructor(
        override val key: KParameter,
        override var value: Any?
    ) : Map.Entry<KParameter, Any?>

    override val size: Int get() = count

    override fun containsKey(key: KParameter): Boolean {
        // NOTE: もしかしたらステータスを見た方が速いかも
        return keyArray[key.index] != null
    }

    override fun containsValue(value: Any?): Boolean = valueArray.any { Objects.equals(value, it) }

    override fun get(key: KParameter): Any? = valueArray[key.index]
    fun getByIndex(key: Int): Any? =
        if (initializationStatusManager.isInitialized(key)) valueArray[key]
        else throw IllegalStateException("This argument is not initialized.")

    override fun isEmpty(): Boolean = count == 0

    override val entries: Set<Map.Entry<KParameter, Any?>>
        get() = keyArray.mapNotNull { it?.let { Entry(it, valueArray[it.index]) } }.toSet()
    override val keys: MutableSet<KParameter>
        get() = keyArray.filterNotNull().toMutableSet()
    override val values: MutableCollection<Any?>
        get() = valueArray.filterIndexed { i, _ -> initializationStatusManager.isInitialized(i) }.toMutableList()

    fun putIfAbsent(key: KParameter, value: Any?) {
        val index = key.index

        // null入力禁止かつnullなら無視する
        if (isRequireNonNull[index] && value == null) return

        // 先に入ったものを優先するため、初期化済みなら何もしない
        if (initializationStatusManager.isInitialized(index)) return

        count += 1
        initializationStatusManager.put(index)
        keyArray[index] = key
        valueArray[index] = value

        return
    }
}
