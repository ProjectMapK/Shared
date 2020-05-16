package com.mapk.core

import java.lang.IllegalArgumentException

class ArgumentAdaptor(private val requiredParameters: Map<String, ValueParameter<*>>) {
    private val argumentMap: MutableMap<String, Any?> = HashMap(requiredParameters.size, 2.0f)

    fun isFullInitialized(): Boolean = requiredParameters.size == argumentMap.size

    fun isInitialized(key: String): Boolean {
        if (!requiredParameters.containsKey(key)) throw IllegalArgumentException("$key is not contains in parameters.")
        return argumentMap.containsKey(key)
    }

    fun putIfAbsent(key: String, value: Any?) {
        if (!isInitialized(key) && (value != null || requiredParameters.getValue(key).isNullable)) {
            argumentMap[key] = value
        }
    }

    fun forcePut(key: String, value: Any?) {
        if (value != null || requiredParameters.getValue(key).isNullable) {
            argumentMap[key] = value
        }
    }

    // 事前に存在チェックはやるものと仮定してここでは読み出しだけ実装
    internal fun readout(key: String): Any? = argumentMap[key]
}
