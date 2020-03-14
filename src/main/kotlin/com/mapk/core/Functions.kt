package com.mapk.core

import com.mapk.annotations.KParameterAlias
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

/**
 * パラメータからエイリアスもしくはプロパティ名を取得する関数
 */
fun KParameter.getAliasOrName(): String? = findAnnotation<KParameterAlias>()?.value ?: name
