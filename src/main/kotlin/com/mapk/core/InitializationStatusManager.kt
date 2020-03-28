package com.mapk.core

internal interface InitializationStatusManager {
    val isFullInitialized: Boolean
    val count: Int
    fun isInitialized(index: Int): Boolean
    fun put(index: Int)
}

internal class InitializationStatusManagerImpl(
    private val initializationStatus: Array<Boolean>
) : InitializationStatusManager {
    private val size = initializationStatus.size
    override val isFullInitialized: Boolean get() = count == size
    override var count: Int = if (initializationStatus[0]) 1 else 0

    override fun isInitialized(index: Int): Boolean = initializationStatus[index]

    override fun put(index: Int) {
        initializationStatus[index] = true
        count++
    }
}
