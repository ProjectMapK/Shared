package com.mapk.core

import com.mapk.annotations.KConstructor
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.functions
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmName

inline fun <reified A : Annotation> KClass<*>.getAnnotatedFunctionsFromCompanionObject(): Pair<Any, List<KFunction<*>>>? {
    return this.companionObject?.let { companionObject ->
        val temp = companionObject.functions.filter { functions -> functions.annotations.any { it is A } }

        if (temp.isEmpty()) {
            // 空ならその後の処理をしてもしょうがないのでnullに合わせる
            null
        } else {
            companionObject.objectInstance!! to temp
        }
    }
}

inline fun <reified A : Annotation, T> Collection<KFunction<T>>.getAnnotatedFunctions(): List<KFunction<T>> {
    return filter { function -> function.annotations.any { it is A } }
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> KClass<T>.getKConstructor(): Pair<Any?, KFunction<T>> {
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

fun KParameter.getKClass(): KClass<*> = type.classifier as KClass<*>
