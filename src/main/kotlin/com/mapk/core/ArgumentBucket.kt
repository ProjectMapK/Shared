package com.mapk.core

import kotlin.reflect.KParameter

class ArgumentBucket internal constructor(
    private val keyArray: Array<KParameter?>,
    internal val valueArray: Array<Any?>,
    private var initializationStatus: Int,
    private val initializeMask: List<Int>,
    private val completionValue: Int
) : Map<KParameter, Any?> {
    // インスタンス有りなら1、そうでなければ0スタート
    private var count: Int = initializationStatus

    val isInitialized: Boolean get() = initializationStatus == completionValue

    class MutableEntry internal constructor(
        override val key: KParameter,
        override var value: Any?
    ) : Map.Entry<KParameter, Any?>

    override val size: Int get() = count

    override fun containsKey(key: KParameter): Boolean {
        // NOTE: もしかしたらステータスを見た方が速いかも
        return keyArray[key.index] != null
    }

    override fun containsValue(value: Any?): Boolean {
        throw UnsupportedOperationException()
    }

    override fun get(key: KParameter): Any? = valueArray[key.index]
    fun getByIndex(key: Int): Any? =
        if (initializationStatus and initializeMask[key] != 0) valueArray[key]
        else throw IllegalStateException("This argument is not initialized.")

    override fun isEmpty(): Boolean = count == 0

    override val entries: Set<Map.Entry<KParameter, Any?>>
        get() = keyArray.mapNotNull { it?.let { MutableEntry(it, valueArray[it.index]) } }.toSet()
    override val keys: MutableSet<KParameter>
        get() = keyArray.filterNotNull().toMutableSet()
    override val values: MutableCollection<Any?>
        get() = throw UnsupportedOperationException()

    fun putIfAbsent(key: KParameter, value: Any?) {
        val index = key.index
        val temp = initializationStatus or initializeMask[index]

        // 先に入ったものを優先するため、初期化済みなら何もしない
        if (initializationStatus == temp) return

        count += 1
        initializationStatus = temp
        keyArray[index] = key
        valueArray[index] = value

        return
    }
}
