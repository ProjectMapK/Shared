package com.mapk.annotations

import com.mapk.core.NameJoiner
import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class KParameterFlatten(
    val fieldNameToPrefix: Boolean = true,
    val namingConvention: KClass<out NameJoiner> = NameJoiner.Camel::class
)
