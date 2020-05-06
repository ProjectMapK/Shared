package com.mapk.core

import com.mapk.annotations.KParameterRequireNonNull
import kotlin.reflect.KClass

internal sealed class ArgumentBinder(val annotations: List<Annotation>) {
    abstract val index: Int
    val isNullable: Boolean = annotations.none { it is KParameterRequireNonNull }

    /**
     * 初期化されており、かつ読み出し条件に合致すれば読み出してその値をバインド、されていなければ何もしない
     * @return バインドしたかどうか
     */
    abstract fun bindArgument(adaptor: ArgumentAdaptor, valueArray: Array<Any?>): Boolean

    class Value<T : Any>(
        override val index: Int,
        annotations: List<Annotation>,
        override val isOptional: Boolean,
        override val name: String,
        override val requiredClazz: KClass<T>
    ) : ArgumentBinder(annotations), ValueParameter<T> {
        override fun bindArgument(adaptor: ArgumentAdaptor, valueArray: Array<Any?>): Boolean {
            return if (adaptor.isInitialized(name)) {
                valueArray[index] = adaptor.readout(name)
                true
            } else {
                false
            }
        }
    }

    class Function(
        private val function: KFunctionForCall<*>,
        override val index: Int,
        annotations: List<Annotation>
    ) : ArgumentBinder(annotations) {
        val requiredParameters: List<ValueParameter<*>> = function.requiredParameters

        override fun bindArgument(adaptor: ArgumentAdaptor, valueArray: Array<Any?>): Boolean {
            val temp = function.call(adaptor)

            if (!isNullable && temp == null) return false

            valueArray[index] = temp
            return true
        }
    }
}
