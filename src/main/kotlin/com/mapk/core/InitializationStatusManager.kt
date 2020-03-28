package com.mapk.core

internal interface InitializationStatusManager {
    val isFullInitialized: Boolean
    val count: Int
    fun isInitialized(index: Int): Boolean
    fun put(index: Int)
}

internal class BitFlagInitializationStatusManager(
    private var initializationStatus: Long,
    private val initializeMask: List<Long>,
    private val completionValue: Long
) : InitializationStatusManager {
    override val isFullInitialized: Boolean get() = initializationStatus == completionValue
    // インスタンス有りなら1、そうでなければ0スタート
    override var count = completionValue.toInt()

    override fun isInitialized(index: Int) = initializationStatus and initializeMask[index] != 0L

    override fun put(index: Int) {
        initializationStatus = initializationStatus or initializeMask[index]
    }
}
