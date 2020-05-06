package com.mapk.core.internal

import com.mapk.annotations.KParameterAlias
import com.mapk.annotations.KUseDefaultArgument
import java.lang.IllegalArgumentException
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmName

/**
 * パラメータからエイリアスもしくはプロパティ名を取得する関数
 */
internal fun KParameter.getAliasOrName(): String? = findAnnotation<KParameterAlias>()?.value ?: name

/**
 * デフォルト引数を用いるかチェックする関数
 */
internal fun KParameter.isUseDefaultArgument(): Boolean {
    if (annotations.any { it is KUseDefaultArgument }) {
        if (!isOptional) throw IllegalArgumentException(
            "Find ${KUseDefaultArgument::class.jvmName}, but it's not has default argument."
        )
        return true
    }
    return false
}
