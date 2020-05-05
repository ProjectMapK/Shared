package com.mapk.core

import kotlin.reflect.KClass
import kotlin.reflect.KParameter

interface ValueParameter<T : Any> {
    val annotations: List<Annotation>
    val isOptional: Boolean
    val name: String
    val requiredClazz: KClass<T>
}

// TODO: 仮置き、ArgumentBinder.Valueでimplementした方が良いと感じるが一旦実装を分けている
internal class ValueParameterImpl<T : Any>(
    override val annotations: List<Annotation>,
    override val isOptional: Boolean,
    override val name: String,
    override val requiredClazz: KClass<T>
) : ValueParameter<T> {
    companion object {
        fun newInstance(
            parameter: KParameter,
            parameterNameConverter: (String) -> String
        ): ValueParameter<*> = ValueParameterImpl(
            parameter.annotations,
            parameter.isOptional,
            parameterNameConverter(parameter.getAliasOrName()!!),
            parameter.type.classifier as KClass<*>
        )
    }
}
