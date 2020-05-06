package com.mapk.core.internal

internal class InitializationStatusManager(private val initializationStatus: Array<Boolean>) {
    private val size = initializationStatus.size
    val isFullInitialized: Boolean get() = count == size
    var count: Int = if (initializationStatus[0]) 1 else 0

    fun isInitialized(index: Int): Boolean = initializationStatus[index]

    fun put(index: Int) {
        initializationStatus[index] = true
        count++
    }
}
