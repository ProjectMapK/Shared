package com.mapk.core

import kotlin.reflect.KClass
import kotlin.reflect.KParameter

interface ValueParameter<T : Any> {
    val requiredClazz: KClass<T>
    val name: String
    val annotations: List<Annotation>
    val isOptional: Boolean
}

// TODO: 仮置き、ArgumentBinder.Valueでimplementした方が良いと感じるが一旦実装を分けている
internal class ValueParameterImpl<T : Any>(
    override val requiredClazz: KClass<T>,
    override val name: String,
    override val annotations: List<Annotation>,
    override val isOptional: Boolean
) : ValueParameter<T> {
    companion object {
        fun newInstance(
            parameter: KParameter,
            parameterNameConverter: (String) -> String
        ): ValueParameter<*> = ValueParameterImpl(
            parameter.type.classifier as KClass<*>,
            parameterNameConverter(parameter.getAliasOrName()!!),
            parameter.annotations,
            parameter.isOptional
        )
    }
}
