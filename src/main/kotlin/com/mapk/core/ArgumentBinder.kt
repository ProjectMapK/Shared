package com.mapk.core

internal sealed class ArgumentBinder {
    abstract val index: Int

    /**
     * 初期化されており、かつ読み出し条件に合致すれば読み出してその値をバインド、されていなければ何もしない
     * @return バインドしたかどうか
     */
    abstract fun bindArgument(adaptor: ArgumentAdaptor, valueArray: Array<Any?>): Boolean

    class Value(private val key: String, override val index: Int) : ArgumentBinder() {
        override fun bindArgument(adaptor: ArgumentAdaptor, valueArray: Array<Any?>): Boolean {
            return if (adaptor.isInitialized(key)) {
                valueArray[index] = adaptor.readout(key)
                true
            } else {
                false
            }
        }
    }

    class Function(
        private val function: KFunctionForCall<*>,
        override val index: Int
    ) : ArgumentBinder() {
        override fun bindArgument(adaptor: ArgumentAdaptor, valueArray: Array<Any?>): Boolean {
            val temp = function.call(adaptor)
            valueArray[index] = temp
            return true
        }
    }
}
