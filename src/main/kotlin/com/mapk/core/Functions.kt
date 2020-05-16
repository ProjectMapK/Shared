package com.mapk.core

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.functions

inline fun <reified A : Annotation> KClass<*>.getAnnotatedFunctionFromCompanionObject(): List<Pair<KFunction<*>, Any>>? {
    return this.companionObject?.let { companionObject ->
        val temp = companionObject.functions.filter { functions -> functions.annotations.any { it is A } }

        if (temp.isEmpty()) {
            // 空ならその後の処理をしてもしょうがないのでnullに合わせる
            null
        } else {
            val instance = companionObject.objectInstance!!
            temp.map { it to instance }
        }
    }
}
