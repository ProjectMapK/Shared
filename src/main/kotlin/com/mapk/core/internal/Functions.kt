package com.mapk.core.internal

import com.mapk.annotations.KConstructor
import com.mapk.annotations.KParameterAlias
import com.mapk.annotations.KUseDefaultArgument
import com.mapk.core.getAnnotatedFunctions
import com.mapk.core.getAnnotatedFunctionsFromCompanionObject
import java.lang.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmName

/**
 * パラメータからエイリアスもしくはプロパティ名を取得する関数
 */
internal fun KParameter.getAliasOrName(): String? = findAnnotation<KParameterAlias>()?.value ?: name

/**
 * デフォルト引数を用いるかチェックする関数
 */
internal fun KParameter.isUseDefaultArgument(): Boolean {
    return annotations.any { it is KUseDefaultArgument }.apply {
        if (this && !isOptional) throw IllegalArgumentException(
            "Find ${KUseDefaultArgument::class.jvmName}, but it's not has default argument."
        )
    }
}

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> KClass<T>.getKConstructor(): Pair<Any?, KFunction<T>> {
    val constructors = ArrayList<Pair<Any?, KFunction<T>>>()

    this.getAnnotatedFunctionsFromCompanionObject<KConstructor>()?.let { (instance, functions) ->
        functions.forEach {
            constructors.add(instance to it as KFunction<T>)
        }
    }

    this.constructors.getAnnotatedFunctions<KConstructor, T>().forEach {
        constructors.add(null to it)
    }

    if (constructors.size == 1) return constructors.single()

    if (constructors.isEmpty()) return null to this.primaryConstructor!!

    throw IllegalArgumentException("${this.jvmName} has multiple ${KConstructor::class.jvmName}.")
}
