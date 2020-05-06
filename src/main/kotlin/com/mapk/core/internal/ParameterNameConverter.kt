package com.mapk.core.internal

import com.mapk.core.NameJoiner

internal sealed class ParameterNameConverter {
    protected abstract val converter: (String) -> String
    abstract fun convert(name: String): String
    abstract fun nest(infix: String, nameJoiner: NameJoiner): WithPrefix

    class Simple(override val converter: (String) -> String) : ParameterNameConverter() {
        override fun convert(name: String) = converter(name)
        override fun nest(infix: String, nameJoiner: NameJoiner) =
            WithPrefix(infix, nameJoiner, converter)
    }

    class WithPrefix(
        prefix: String,
        private val nameJoiner: NameJoiner,
        override val converter: (String) -> String
    ) : ParameterNameConverter() {
        private val prefix = converter(prefix)

        // 結合を伴う変換では、「双方変換 -> 結合」の順で処理を行う
        override fun convert(name: String) = converter(name).let { nameJoiner.join(prefix, it) }
        override fun nest(infix: String, nameJoiner: NameJoiner) =
            WithPrefix(
                convert(infix),
                nameJoiner,
                converter
            )
    }
}
