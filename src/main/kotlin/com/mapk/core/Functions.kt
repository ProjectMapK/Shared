package com.mapk.core

import com.mapk.annotations.KParameterAlias
import com.mapk.annotations.KUseDefaultArgument
import java.lang.IllegalArgumentException
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

/**
 * パラメータからエイリアスもしくはプロパティ名を取得する関数
 */
fun KParameter.getAliasOrName(): String? = findAnnotation<KParameterAlias>()?.value ?: name

/**
 * パラメータがignoreされているかをチェックする関数
 */
fun KParameter.isUseDefaultArgument(): Boolean {
    if (annotations.any { it is KUseDefaultArgument }) {
        if (!isOptional) {
            throw IllegalArgumentException("Find KUseDefaultArgument, but it's not has default argument.")
        }
        return true
    }
    return false
}
