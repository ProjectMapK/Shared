package com.mapk.core.internal

import com.mapk.core.NameJoiner

internal sealed class ParameterNameConverter(protected val converter: ((String) -> String)?) {
    abstract fun convert(name: String): String
    abstract fun nest(infix: String, nameJoiner: NameJoiner): WithPrefix
    abstract fun toSimple(): Simple

    protected fun convertedOrName(name: String): String = converter?.invoke(name) ?: name

    class Simple(converter: ((String) -> String)?) : ParameterNameConverter(converter) {
        override fun convert(name: String) = convertedOrName(name)
        override fun nest(infix: String, nameJoiner: NameJoiner) = WithPrefix(infix, nameJoiner, converter)
        override fun toSimple(): Simple = this
    }

    class WithPrefix(
        prefix: String,
        private val nameJoiner: NameJoiner,
        converter: ((String) -> String)?
    ) : ParameterNameConverter(converter) {
        private val prefix = convertedOrName(prefix)

        // 結合を伴う変換では、「双方変換 -> 結合」の順で処理を行う
        override fun convert(name: String) = convertedOrName(name).let { nameJoiner.join(prefix, it) }
        override fun nest(infix: String, nameJoiner: NameJoiner) = WithPrefix(convert(infix), nameJoiner, converter)
        override fun toSimple(): Simple = Simple(converter)
    }
}
