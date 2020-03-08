package com.mapk.core

import kotlin.reflect.KParameter

internal class BucketGenerator(capacity: Int, instancePair: Pair<KParameter, Any?>?) {
    private val initializationStatus: Int = if (instancePair == null) 0 else 1
    private val initializeMask: List<Int> = generateSequence(1) { it.shl(1) }.take(capacity).toList()
    private val completionValue: Int = initializeMask.reduce { l, r -> l or r }

    private val keyArray: Array<KParameter?> = Array(capacity) { null }
    private val valueArray: Array<Any?> = Array(capacity) { null }

    init {
        if (instancePair != null) {
            keyArray[0] = instancePair.first
            valueArray[0] = instancePair.second
        }
    }

    fun generate(): ArgumentBucket = ArgumentBucket(
        keyArray.copyOf(),
        valueArray.copyOf(),
        initializationStatus,
        initializeMask,
        completionValue
    )
}

class ArgumentBucket internal constructor(
    private val keyArray: Array<KParameter?>,
    internal val valueArray: Array<Any?>,
    private var initializationStatus: Int,
    private val initializeMask: List<Int>,
    private val completionValue: Int
) : MutableMap<KParameter, Any?> {
    private var count: Int = 0

    val isInitialized: Boolean get() = initializationStatus == completionValue

    class MutableEntry internal constructor(
        override val key: KParameter,
        override var value: Any?
    ) : MutableMap.MutableEntry<KParameter, Any?> {
        override fun setValue(newValue: Any?): Any? {
            throw UnsupportedOperationException()
        }
    }

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

    override val entries: MutableSet<MutableMap.MutableEntry<KParameter, Any?>>
        get() = keyArray.mapNotNull { it?.let { MutableEntry(it, valueArray[it.index]) } }.toMutableSet()
    override val keys: MutableSet<KParameter>
        get() = keyArray.filterNotNull().toMutableSet()
    override val values: MutableCollection<Any?>
        get() = throw UnsupportedOperationException()

    override fun clear() {
        throw UnsupportedOperationException()
    }

    override fun put(key: KParameter, value: Any?) {
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

    override fun putAll(from: Map<out KParameter, Any?>) {
        throw UnsupportedOperationException()
    }

    override fun remove(key: KParameter): Any? {
        throw UnsupportedOperationException()
    }
}
